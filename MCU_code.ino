/*Import niezbednych bibliotek */
#include <SerialBT.h>
#include <Adafruit_Sensor.h>
#include <DHT.h>
#include <DHT_U.h>

/*Defininicja uzytego DHT22 */
#define DHTPIN 15
#define DHTTYPE    DHT22
DHT_Unified dht(DHTPIN, DHTTYPE);
void setup() {
  /*Uruchumienie serial portu i czujnika */
  SerialBT.begin();
  dht.begin();
}
void loop() {
while (SerialBT) {
      sensors_event_t event;
      /*Zmierz temperature*/
      dht.temperature().getEvent(&event);
      /*Wyslij dane przez Bluetooth*/
      SerialBT.printf("%2.1f\n", event.temperature);
      /*Odczekaj 3sec*/
      delay(3000);
  }
}