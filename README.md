# heart-rate-monitor-android
An Android Bluetooth Heart Rate Monitor Simulator
There are 2 apps one working as a Bluetooth Central and the other as an Bluetooth Peripheral

## Testing the connection
* Start the Peripheral App
* Make sure all connection requirements a ready
* Allow New Connections

* Start the Central App
* Make sure all connection requirements a ready
* Connect

#Apps
* [Central](heart-rate-app/central) [![Build Status](https://app.bitrise.io/app/117b0dde3425a228/status.svg?token=8_ZKZKjRpAk9Q1-_7CPNPg&branch=development)](https://app.bitrise.io/app/117b0dde3425a228)
* [Peripheral](heart-rate-app/peripheral) [![Build Status](https://app.bitrise.io/app/848c91c98aae0a07/status.svg?token=GV9nugoeB2jU2ncUpdUhcw&branch=development)](https://app.bitrise.io/app/848c91c98aae0a07)




Alternatively, instead of using the android peripheral, an embedded device is also available at [heart-rate-device](heart-rate-device/index.js)


# Setup the [heart-rate-device](heart-rate-device/index.js) bluetooth device
Use any device that implements the `Bluetooth Heart Rate Profile` containing the `Heart Rate Service` and the `Device Information Service`

## Using the Device available in [heart-rate-device](heart-rate-device/index.js)
This device requires a Nordic Development hardware, the one used for this project is the [nRF52 DK](https://www.nordicsemi.com/Products/Development-hardware/nrf52-dk)

This device uses the [nordicSemiconductor/pc-ble-driver-js](https://github.com/nordicSemiconductor/pc-ble-driver-js) and it's example of a heart rate monitor 

### Running the device (Mac OS)
It requires Node js please see the complete instructions in the *pc-ble-driver-js* repository

* Get the device port running the  commmand (_nrfjprog --com_) (Or using the [nRF Connect for Desktop](https://www.nordicsemi.com/Products/Development-tools/nrf-connect-for-desktop))
* replace the device port (_/dev/tty.usbmodemxxxx_) in the command below 

run the following command:

```
cd heart-rate-device
npm install
node . /dev/tty.usbmodemxxxx v5
```
