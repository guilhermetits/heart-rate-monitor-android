# heart-rate-monitor-android
An Android Bluetooth Heart Rate Monitor


# Setup the Bluetooth device
Use any device that implements the `Bluetooth Heart Rate Profile` containing the `Heart Rate Service` and the `Device Information Service`

## Using the Device available in [heart-rate-device](heart-rate-device/index.js)
This device requires a Nordic Development hardware, the one used for this project is the [nRF52 DK](https://www.nordicsemi.com/Products/Development-hardware/nrf52-dk)

This device uses the [nordicSemiconductor/pc-ble-driver-js](https://github.com/nordicSemiconductor/pc-ble-driver-js) and it's example of a heart rate monitor 

### Running the device (Mac OS)
It requires Node js please see the complete instructions in the *pc-ble-driver-js* repository

* Get the device port (I recommend using the [nRF Connect for Desktop](https://www.nordicsemi.com/Products/Development-tools/nrf-connect-for-desktop))
* replace the device port (_/dev/tty.usbmodemxxxx_) in the command below 

run the following command:

```
cd heart-rate-device
npm install
node . /dev/tty.usbmodemxxxx v5
```