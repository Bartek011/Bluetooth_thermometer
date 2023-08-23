<img src="https://i.imgur.com/mJalnr4.png" alt="real" width="9000" height="300" style="display: block; margin: 0 auto;\">

# Introduction
This simple project was made to summary the class "Industrial communication interfaces" (pol. "Przemysłowe interfejsy komunikacyjne") at Warsaw University of Technology. The main aim was to create wireless connection between MCU and phone with Android operating system. 

The transmitted frames contain temperature data from digital temperature sensor **DHT22** connected to **Raspberry Pi Pico W** and are received by smartphone via Bluetooth.

# MCU code
As you can see it's very simple 🙂
```C
#include <SerialBT.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>

/*Define pin of DHT22 */
#define DHTPIN 15
#define DHTTYPE    DHT22
DHT_Unified dht(DHTPIN, DHTTYPE);
void setup() {
  /*Initialize serial port and sensor*/
  SerialBT.begin();
  dht.begin();
}
void loop() {
while (SerialBT) {
      sensors_event_t event;
      /*Get temeperature data*/
      dht.temperature().getEvent(&event);
      /*Send data via bluetooth*/
      SerialBT.printf("%2.1f\n", event.temperature);
      /*wait 3sec*/
      delay(3000);
  }
}
```

# Android App
Graphical interface was created as a modified version of [Ssaurel's tutorial](https://www.ssaurel.com/blog/learn-to-create-a-thermometer-application-for-android/).

<p align="center">
<img src="https://i.imgur.com/3UVghEu.png" alt="real" style="display: block; margin: 0 auto;\">
</p>


Communication is being handle by this code:

```Java
//Initialize bluetooh adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //Set default Bluetooth adress
        String deviceAddress = "43:43:A2:12:1F:AC"; // adres 
        mDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);
        //Create thread to receive data 
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Establish connection
                    mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    mSocket.connect();
                    mInputStream = mSocket.getInputStream();
                    //Data buffer
                    byte[] buffer = new byte[1024];
                    int bytes;
                    while (true) {
                        bytes = mInputStream.read(buffer);
                        String message = new String(buffer, 0, bytes);
                        mHandler.obtainMessage(MESSAGE_READ, message).sendToTarget();}
                //Lost connection
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Nic nie odebrano!");
                    Toast.makeText(MainActivity.this, "Utracono polaczenie z Pico W",Toast.LENGTH_LONG).show;
                }
            }
        });
        mThread.start();
```
# Real view

<p align="center">
<img src="https://i.imgur.com/9d3Riee.png" alt="real" style="display: block; margin: 0 auto;\">
</p>

Full assigment you can read in polish [here](https://drive.google.com/file/d/1o4tR8p_UO62BoCpG_BAhXDsTPEUxqWYp/view?usp=drive_link).



