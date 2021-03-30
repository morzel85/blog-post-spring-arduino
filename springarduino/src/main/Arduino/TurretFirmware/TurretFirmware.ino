#include <Servo.h>

// SpringArduino - sample code from morzel.net blog post 

#include <Servo.h>  

const byte fireLedPin = 8;
const byte setupReadyLedPin = 9;
const byte triggerPin = 10;
const byte panServoPin = 11;
const byte tiltServoPin = 12;

const byte separator = 255;
const byte triggerPullDelayInMs = 250;

Servo panServo; 
Servo tiltServo; 

void setup() {  
    pinMode(fireLedPin, OUTPUT);
    pinMode(triggerPin, OUTPUT);
    pinMode(setupReadyLedPin, OUTPUT);
         
    panServo.attach(panServoPin);   
    tiltServo.attach(tiltServoPin);   
    
    Serial.begin(9600); // Open connection with PC
    
    digitalWrite(setupReadyLedPin, HIGH);
}

void loop() {      
    if (Serial.available() > 3) {            
        byte panAngle = Serial.read();
        byte tiltAngle = Serial.read();
        byte fire = Serial.read();
        byte lastByte = Serial.read();
         
        // Packet validation 
        if (panAngle != separator && tiltAngle != separator && fire != separator && lastByte == separator) {         
            
            // Moving servos
            panServo.write(panAngle);
            tiltServo.write(tiltAngle);
            
            if (fire) { 
               // Shooting 
               digitalWrite(fireLedPin, HIGH); 
               digitalWrite(triggerPin, HIGH); 
               delay(triggerPullDelayInMs);  
               digitalWrite(triggerPin, LOW); 
               digitalWrite(fireLedPin, LOW); 
            }
        }
    }       
}
