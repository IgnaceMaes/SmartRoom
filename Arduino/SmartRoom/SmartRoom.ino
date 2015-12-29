#include <UIPEthernet.h>
#include <ArduinoJson.h>
#include <RCSwitch.h>

#define PIN_TEMPERATURE 0
#define PIN_PHOTOCELL 1
#define PIN_REED_SWITCH 2
#define PIN_TRANSMITTER 3
#define TRANSMISSION_LENTH 24
#define POWERSWITCH_AMOUNT 2
#define POWERSWITCH_ON 0
#define POWERSWITCH_OFF 1

byte mac[] = { 0x54, 0x34, 0x41, 0x30, 0x30, 0x31 };                                    
IPAddress ip(192, 168, 1, 170);                        
EthernetServer server(80);

RCSwitch mySwitch = RCSwitch();

//ON-OFF codes for each powerswitch
unsigned long powerswitches[POWERSWITCH_AMOUNT][2] = { {15511983, 15511982}, {15511981, 15511980 } };
//states of the power switches (on = true, off = false)
boolean powerswitchStates[POWERSWITCH_AMOUNT] = { false, false };

void setup() {
  Serial.begin(9600);
  pinMode(PIN_REED_SWITCH, INPUT); 
  
  mySwitch.enableTransmit(PIN_TRANSMITTER);
  mySwitch.setProtocol(1);
  mySwitch.setRepeatTransmit(10);

  Ethernet.begin(mac, ip);
  server.begin();
}

void loop() {
  // listen for incoming clients
  EthernetClient client = server.available();

  if (client) 
  {  
    // an http request ends with a blank line
    boolean currentLineIsBlank = true;
    String httpRequest;

    while (client.connected()) 
    {
      if (client.available()) 
      {
        char c = client.read();
        
        if (!containsString(httpRequest, "HTTP/"))
        {
          httpRequest += c;
        }

        //complete request
        if (c == '\n' && currentLineIsBlank) 
        {          
            client.println("HTTP/1.1 200 OK");     
            client.println("Content-Type: application/json");
            client.println("Connection: close");
            client.println();
              
            if (containsString(httpRequest, "GET /power"))
            {                          
              for (int powerswitchIndex = 0; powerswitchIndex < POWERSWITCH_AMOUNT; powerswitchIndex++)
              {
                String powerswitch = "id=";
                String powerswitchCheck = powerswitch + powerswitchIndex;
                if (containsString(httpRequest, powerswitchCheck))
                {                    
                  if (containsString(httpRequest, "action=on"))
                  {
                    transmitToPowerswitch(powerswitchIndex, POWERSWITCH_ON);
                  }
                  else if (containsString(httpRequest, "action=off"))
                  {
                    transmitToPowerswitch(powerswitchIndex, POWERSWITCH_OFF);
                  }
                } 
              }
              
              StaticJsonBuffer<30> jsonBuffer;
  
              JsonObject& root = jsonBuffer.createObject();
              root["message"] = "action completed";
              root.printTo(client);
            }
            else 
            {          
              StaticJsonBuffer<100> jsonBuffer;
  
              JsonObject& root = jsonBuffer.createObject();
              
              root["temperature"] = readTemperatureInC(PIN_TEMPERATURE);
              root["light"] = analogRead(PIN_PHOTOCELL);
              root["doorClosed"] = readReedSwitch(PIN_REED_SWITCH);
              
              JsonArray& data = root.createNestedArray("powerswitchStates");  
              for (int powerswitchIndex = 0; powerswitchIndex < POWERSWITCH_AMOUNT; powerswitchIndex++)
              {
                data.add(powerswitchStates[powerswitchIndex]);
              }
  
              root.printTo(client);
            }     
            httpRequest = "";
            break;
        }

        if (c == '\n') {
          // you're starting a new line
          currentLineIsBlank = true;
        }
        else if (c != '\r') 
        {
          // you've gotten a character on the current line
          currentLineIsBlank = false;
        }
      } 
    }

    // give the web browser time to receive the data
    delay(20);

    // close the connection:
    client.stop();
  }
}

void transmitToPowerswitch(int powerswitchIndex, int action)
{
  unsigned long transmissionCode = powerswitches[powerswitchIndex][action];
  /*Serial.print("transmitting code [");
  Serial.print("index: ");
  Serial.print(powerswitchIndex);
  Serial.print(", action: ");
  Serial.print(action);
  Serial.print("] Code: ");
  Serial.println(transmissionCode);*/
  mySwitch.send(transmissionCode, TRANSMISSION_LENTH);  
  
  boolean on = action == POWERSWITCH_ON;
  powerswitchStates[powerswitchIndex] = on;
}

float readTemperatureInC(int pin)
{
  float voltage = (analogRead(pin) * 0.004882814);
  float temperatureC = (voltage - 0.5) * 100.0;
  
  return temperatureC;
}

boolean readReedSwitch(int pin)
{
  return (digitalRead(pin) == HIGH);
}

boolean containsString(String input, String search) 
{
  return input.indexOf(search) > -1;
}
