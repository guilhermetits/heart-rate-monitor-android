'use strict';

const _ = require('underscore');

const { AdapterFactory, ServiceFactory } = require('pc-ble-driver-js');
const path = require('path');

const adapterFactory = AdapterFactory.getInstance(undefined, { enablePolling: false });
const serviceFactory = new ServiceFactory();

const BLE_UUID_HEART_RATE_SERVICE = '180d'; /** < Heart Rate service UUID. */
const BLE_UUID_DEVICE_INFORMATION_SERVICE = '180a'; /** < Device Information service UUID. */
const BLE_UUID_HEART_RATE_MEASUREMENT_CHAR = '2a37'; /** < Heart Rate Measurement characteristic UUID. */
const BLE_UUID_MANUFACTURER_NAME_CHAR = '2a29'; /** < Manufacturer Name characteristic UUID. */
const BLE_UUID_MODEL_NUMBER_CHAR = '2a24'; /** < Model Number characteristic UUID. */
const BLE_UUID_CCCD = '2902'; /** < Client characteristic descriptor UUI    D. */

/* State */
let heartRateService;
let deviceInformationService;
let heartRateMeasurementCharacteristic;
let cccdDescriptor;
let heartRateInterval;

/**
 * When notifications are disabled on the hrm characteristic's CCCD, stop generating and sending heart rates.
 * @returns {undefined}
 */
function disableNotificationsOnHRM() {
    if (heartRateInterval !== null) {
        console.log('Disabling notifications on heart rate measurement characteristic...');

        clearInterval(heartRateInterval);
        heartRateInterval = null;
    }
}

/**
 * Function for initializing the Advertising functionality and starting advertising.
 *
 * @param {Adapter} adapter Adapter being used.
 * @returns {Promise} Resolves if advertising is started successfully.
 *                    If an error occurs, rejects with the corresponding error.
 */
function advertisingStart(adapter) {
    return new Promise((resolve, reject) => {
        console.log('Starting advertising...');

        const options = {
            interval: 40,
            timeout: 180,
            connectable: true,
            scannable: false,
        };

        adapter.startAdvertising(options, err => {
            if (err) {
                reject(new Error(`Error starting advertising: ${err}.`));
                return;
            }

            resolve();
        });
    });
}

/**
 * Function for sending the heart rate measurement over Bluetooth Low Energy.
 *
 * @param {Adapter} adapter Adapter being used.
 * @param {array} encodedHeartRate Data to be sent over Bluetooth Low Energy.
 * @returns {Promise} Resolves if the data is successfully sent.
 *                    If an error occurs, rejects with the corresponding error.
 */
function heartRateMeasurementSend(adapter, encodedHeartRate) {
    return new Promise((resolve, reject) => {
        console.log('Sending heart rate measurement over Bluetooth Low Energy...');

        adapter.writeCharacteristicValue(heartRateMeasurementCharacteristic.instanceId, encodedHeartRate, false,
            err => {
                if (err) {
                    reject(Error(`Error writing heartRateMeasurementCharacteristic: ${err}.`));
                }
            }, () => {
                resolve();
            });
    });
}

/**
 * Function for generating and sending heart rate measurements when notifications are enabled on hrm characteristic.
 *
 * Called whenever a descriptor's value is changed (CCCD of hrm characteristic in our example).
 *
 * @param {Adapter} adapter Adapter being used.
 * @param {any} attribute Object from descriptorValueChanged event emitter.
 * @returns {undefined}
 */
function onDescValueChanged(adapter, attribute) {
    const descriptorHandle = adapter._getCCCDOfCharacteristic(heartRateMeasurementCharacteristic.instanceId).handle;

    if (descriptorHandle === cccdDescriptor.handle) {
        const descriptorValue = attribute.value[Object.keys(attribute.value)[0]];

        if (_.isEmpty(descriptorValue)) {
            return;
        }

        const isNotificationEnabled = () => descriptorValue[0] === 1;

        const isIndicationEnabled = () => {
            if (descriptorValue.length <= 1) {
                return false;
            }

            return descriptorValue[1] === 1;
        };

        if (isIndicationEnabled()) {
            console.log('Warning: indications not supported on heart rate measurement characteristic.');
        }

        if (isNotificationEnabled()) {
            let heartRate = 65;

            if (heartRateInterval === null) {
                console.log('Enabling notifications on heart rate measurement characteristic...');


                heartRateInterval = setInterval(() => {
                    /**
                     * Function for simulating a heart rate sensor reading.
                     *
                     * Note: Modifies the heart rate state.
                     *
                     * @returns {undefined}
                     */
                    const heartRateGenerate = () => {
                        heartRate += 3;
                        if (heartRate >= 190) {
                            heartRate = 65;
                        }
                    };

                    /**
                     * Function for encoding a heart rate Measurement.
                     *
                     * @returns {[flag, heartRate]} Array of encoded data.
                     */
                    const heartRateMeasurementEncode = () => [0, heartRate];

                    heartRateGenerate();
                    const encodedHeartRate = heartRateMeasurementEncode();
                    heartRateMeasurementSend(adapter, encodedHeartRate).then(() => {
                        console.log('Heart rate measurement successfully sent over Bluetooth Low Energy.');
                    }).catch(err => {
                        console.log(err);
                        process.exit(1);
                    });
                }, 1000);
            }
        } else {
            disableNotificationsOnHRM();
        }
    }
}

/**
 * Handling events emitted by adapter.
 *
 * @param {Adapter} adapter Adapter in use.
 * @returns {undefined}
 */
function addAdapterListener(adapter) {
    adapter.on('dataLengthUpdateRequest', function (device, event) {
        console.log("try to update data length")
        let dataLength = Math.max(event.max_rx_octets, event.max_tx_octets)
        adapter.dataLengthUpdate(device.instanceId, {
            max_rx_octets: dataLength,
            max_tx_octets: dataLength
        }, function () {
            console.log(`data length update callback: ${JSON.stringify(arguments)}`);
        });
    });

    adapter.on('phyUpdateRequest', function (device, event) {
        console.log("try to update phy" + JSON.stringify(event))
        const phyParams = {
            tx_phys: 1,
            rx_phys: 1,
        };
        adapter.phyUpdate(device.instanceId, phyParams, function () {
            console.log(`phy update callback: ${JSON.stringify(arguments)}`);
        });
    });

    adapter.on('attMtuRequest', function (device, requestAttMtu) {
        console.log("try to update mtu" + JSON.stringify(requestAttMtu))
        adapter.attMtuReply(device.instanceId, requestAttMtu, function () {
            console.log(`mtu update callback: ${JSON.stringify(arguments)}`);
        })
    });

    const secParamsPeripheral = {
        bond: true,
        mitm: false,
        lesc: true,
        keypress: false,
        io_caps: adapter.driver.BLE_GAP_IO_CAPS_DISPLAY_ONLY,
        oob: false,
        min_key_size: 7,
        max_key_size: 16,
        kdist_own: {
            enc: false,   /** Long Term Key and Master Identification. */
            id: false,    /** Identity Resolving Key and Identity Address Information. */
            sign: false,  /** Connection Signature Resolving Key. */
            link: false,  /** Derive the Link Key from the LTK. */
        },
        kdist_peer: {
            enc: false,   /** Long Term Key and Master Identification. */
            id: false,    /** Identity Resolving Key and Identity Address Information. */
            sign: false,  /** Connection Signature Resolving Key. */
            link: false,  /** Derive the Link Key from the LTK. */
        },
    };

    adapter.on('secParamsRequest', function (device, peer_params) {
        secKeyset.keys_own.pk = { pk: adapter.computePublicKey() };
        adapter.replySecParams(device.instanceId, 0, secParamsPeripheral, secKeyset, function () {
            console.log(`secParamsRequest callback: ${JSON.stringify(arguments)}`);
        })
    });


    /**
     * Handling error and log message events from the adapter.
     */
    adapter.on('logMessage', (severity, message) => { console.log(`${message}.`); });
    adapter.on('error', error => { console.log(`error: ${JSON.stringify(error, null, 1)}.`); });
    /**
    * Handling the Application's BLE Stack events.
    */
    adapter.on('deviceConnected', device => { console.log(`Device ${device.address}/${device.addressType} connected.`); });

    adapter.on('deviceDisconnected', device => {
        console.log(`Device ${device.address}/${device.addressType} disconnected.`);

        disableNotificationsOnHRM();
        advertisingStart(adapter);
    });

    adapter.on('deviceDiscovered', device => {
        console.log(`Discovered device ${device.address}/${device.addressType}.`);
    });

    adapter.on('descriptorValueChanged', attribute => {
        onDescValueChanged(adapter, attribute);
    });

    adapter.on('advertiseTimedOut', () => {
        console.log('advertiseTimedOut: Advertising timed-out. Exiting.');
        process.exit(1);
    });
}

/**
 * Opens adapter for use with the default options.
 *
 * @param {Adapter} adapter Adapter to be opened.
 * @returns {Promise} Resolves if the adapter is opened successfully.
 *                    If an error occurs, rejects with the corresponding error.
 */
function openAdapter(adapter) {
    return new Promise((resolve, reject) => {
        const baudRate = 1000000;
        console.log(`Opening adapter with ID: ${adapter.instanceId} and baud rate: ${baudRate}...`);

        adapter.open({ baudRate, logLevel: 'error' }, err => {
            if (err) {
                reject(Error(`Error opening adapter: ${err}.`));
                return;
            }

            resolve();
        });
    });
}

/**
 * Function for setting the advertisement data.
 *
 * Sets the full device name and its available BLE services in the advertisement data.
 *
 * @param {Adapter} adapter Adapter being used.
 * @returns {Promise} Resolves if advertisement data is set successfully.
 *                    If an error occurs, rejects with the corresponding error.
 */
function advertisementDataSet(adapter) {
    return new Promise((resolve, reject) => {
        console.log('Setting advertisement data...');

        const advertisingData = {
            completeLocalName: 'Heart Rate Monitor',
            flags: ['leGeneralDiscMode', 'brEdrNotSupported'],
            txPowerLevel: -10,
        };

        const scanResponseData = {
            completeListOf16BitServiceUuids: [BLE_UUID_HEART_RATE_SERVICE],
        };

        adapter.setAdvertisingData(advertisingData, scanResponseData, err => {
            if (err) {
                reject(new Error(`Error initializing the advertising functionality: ${err}.`));
                return;
            }

            resolve();
        });
    });
}

/**
 * Function for adding the Heart Rate Measurement characteristic and CCCD descriptor to global state `heartRateService`.
 * @returns {undefined}
 */
function heartRateCharacteristicsInit() {
    heartRateMeasurementCharacteristic = serviceFactory.createCharacteristic(
        heartRateService,
        BLE_UUID_HEART_RATE_MEASUREMENT_CHAR,
        [0, 0],
        {
            broadcast: false,
            read: false,
            write: false,
            writeWoResp: false,
            reliableWrite: false,
            notify: true,
            indicate: false,
        },
        {
            maxLength: 2,
            readPerm: ['open'],
            writePerm: ['open'],
        });

    cccdDescriptor = serviceFactory.createDescriptor(
        heartRateMeasurementCharacteristic,
        BLE_UUID_CCCD,
        [0, 0],
        {
            maxLength: 2,
            readPerm: ['open'],
            writePerm: ['open'],
            variableLength: false,
        });
}


/**
 * Function for adding the Manufacturer Name Characteristic to the `deviceInformationService`.
 * @returns {undefined}
 */
function deviceInformationCharacteristicsInit() {
    let manufacturer = Array.from(Buffer.from("Guilherme Titschkoski", 'utf8'));
    let model = Array.from(Buffer.from("heart_rate_monitor:1.0", 'utf8'));

    serviceFactory.createCharacteristic(
        deviceInformationService,
        BLE_UUID_MANUFACTURER_NAME_CHAR,
        manufacturer,
        {
            broadcast: false,
            read: true,
            write: false,
            writeWoResp: false,
            reliableWrite: false,
            notify: false,
            indicate: false,
        },
        {
            maxLength: manufacturer.length,
            readPerm: ['open'],
            writePerm: ['open']
        });

    serviceFactory.createCharacteristic(
        deviceInformationService,
        BLE_UUID_MODEL_NUMBER_CHAR,
        model,
        {
            broadcast: false,
            read: true,
            write: false,
            writeWoResp: false,
            reliableWrite: false,
            notify: false,
            indicate: false,
        },
        {
            maxLength: model.length,
            readPerm: ['open'],
            writePerm: ['open']
        });
}

/**
 * Function for initializing services that will be used by the application.
 *
 * Initialize the Heart Rate service and it's characteristics and add to GATT.
 *
 * @param {Adapter} adapter Adapter being used.
 * @returns {Promise} Resolves if the service is started initialized successfully.
 *                    If an error occurs, rejects with the corresponding error.
 */
function servicesInit(adapter) {
    return new Promise((resolve, reject) => {
        console.log('Initializing the heart rate service and its characteristics/descriptors...');

        heartRateService = serviceFactory.createService(BLE_UUID_HEART_RATE_SERVICE);
        heartRateCharacteristicsInit();
        console.log('Initializing the device information service and its characteristics...');
        deviceInformationService = serviceFactory.createService(BLE_UUID_DEVICE_INFORMATION_SERVICE);
        deviceInformationCharacteristicsInit();

        adapter.setServices([heartRateService, deviceInformationService], err => {
            if (err) {
                reject(Error(`Error initializing services: ${JSON.stringify(err, null, 1)}'.`));
                return;
            }

            resolve();
        });
    });
}

function help() {
    console.log(`Usage: ${path.basename(__filename)} <PORT> <SD_API_VERSION>`);
    console.log();
    console.log('PORT is the UART for the adapter. For example /dev/ttyS0 on Unix based systems or COM1 on Windows based systems.');
    console.log('SD_API_VERSION can be v2 or v5. nRF51 series uses v2.');
    console.log();
    console.log('It is assumed that the nRF device has been programmed with the correct connectivity firmware.');
}

/**
 * Application main entry.
 */
if (process.argv.length !== 4) {
    help();
    process.exit(-1);
} else {
    const [, , port, apiVersion] = process.argv;

    if (port == null) {
        console.error('PORT must be specified');
        process.exit(-1);
    }

    if (apiVersion == null) {
        console.error('SD_API_VERSION must be provided');
        process.exit(-1);
    } else if (!['v2', 'v5'].includes(apiVersion)) {
        console.error(`SD_API_VERSION must be v2 or v5, argument provided is ${apiVersion}`);
        process.exit(-1);
    }

    const adapter = adapterFactory.createAdapter(apiVersion, port, '');
    addAdapterListener(adapter);

    openAdapter(adapter).then(() => {
        console.log('Opened adapter.');
        return servicesInit(adapter);
    }).then(() => {
        console.log('Initialized the heart rate service and its characteristics/descriptors.');
        return advertisementDataSet(adapter);
    }).then(() => {
        console.log('Set advertisement data.');
        return advertisingStart(adapter);
    })
        .then(() => {
            console.log('Started advertising.');
        })
        .catch(error => {
            console.log(error);
            process.exit(-1);
        });
}
