#include<WiFi.h>
#include<Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

#define WIFI_SSID "Whiskey"
#define WIFI_PASSWORD "globalwarming"
#define API_KEY "AIzaSyAOogzTzoKOWMklXWGJEbPlEl22aaQD1c0"
#define DATABASE_URL "https://nodemcu-eaa69-default-rtdb.firebaseio.com/"

#define LED1_PIN 18
#define PWMChannel 0

const int freq = 5000;
const int resolution = 8;

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;

unsigned long sendDataPrevMillis = 0;
bool signupOK = false;
int ldrData = 0;
float voltage = 0.0;
int pwmValue = 0;
bool ledStatus = false;

void setup() {
  // put your setup code here, to run once:
  pinMode(LED1_PIN, OUTPUT);
  
  Serial.begin(115200);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while(WiFi.status () != WL_CONNECTED){
    Serial.print("."); delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  if(Firebase.signUp(&config, &auth, "", "")){
    Serial.println("signUP OK");
    signupOK = true;
  }else{
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }

  config.token_status_callback = tokenStatusCallback;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

}

void loop() {
  // put your main code here, to run repeatedly:
  if(Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 5000 || sendDataPrevMillis == 0)){
    sendDataPrevMillis = millis();
    //led control value
  if(Firebase.RTDB.getInt(&fbdo, "/LED/digital")){
    if(fbdo.dataType() == "boolean"){
      ledStatus = fbdo.boolData();
      Serial.println("Successful READ from " + fbdo.dataPath() + ": " + ledStatus + " (" + fbdo.dataType() + ")");
      digitalWrite(LED1_PIN, ledStatus);
    }
  }else{
    Serial.println("FAILED: " + fbdo.errorReason());
  }

  }

  
  

}
