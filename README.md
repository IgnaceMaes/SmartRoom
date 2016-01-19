# SmartRoom
An Android app to control/monitor my room using an Arduino with a web API as backend.

## Features
* View room's temperature (in °C)
* View room's illuminance (in lux)
* View if door is open or closed
* View room's outlet states (on/off)
* Turn on/off outlet
* Autoswitch mode: turns on an outlet when the door opens, turns it off when the door closes

## Arduino scheme
![scheme](http://i.imgur.com/oxzaATl.png)
*Note: the ethernet and 433MHz transmitter modules are the ones provided by Fritzing, the real ones I used differ a bit (but you get the idea).*

## Android app

![my room](http://i.imgur.com/llCQIl1.png)
The Android app has a card for each value of the room. Tap the card to change its value (e.g. tap on powerswitch 0 to turn it on/off). The app also displays a message to tell if the call was succesful or has failed.

## API calls
#### GET /
Returns a JSON with all the up-to-date info about the room.

###### Request
/

###### Response
Parameter | Type | Description
--- | --- | ---
temperature | double | The temperature of the room in °C.
light | int | The illuminance of the room in lux.
doorClosed | boolean | Tells you if the door is open or closed.
autoswitch | boolean | True if the Autoswitch mode is on, false if not.
powerswitchStates | boolean[] | Array of all the states of the outlets (true = on, false = off).

#### GET /power
Turn a specific outlet in the room on or off.

###### Request
Parameter | Type | Description
--- | --- | ---
id | int | The index of the specific outlet.
a | String | The action to complete [on/off].

###### Response
Parameter | Type | Description
--- | --- | ---
m | String | Message saying the action has been completed.

#### GET /mode
Lets you turn the Autoswitch mode on or off.

###### Request
Parameter | Type | Description
--- | --- | ---
a | String | The new state for the Arduino's Autoswitch mode [on/off].

###### Response
Parameter | Type | Description
--- | --- | ---
m | String | Message saying the action has been completed.


## About
This project was started for my class Internet of Things at school.
If you want to read about the progress you can check out the [blog](http://smartroom.ignacemaes.com/) behind it (Dutch).


If you wish to contact me for anything you can get in touch at:
- Twitter: https://twitter.com/Ignace_Maes
- Personal Website: http://ignacemaes.com
