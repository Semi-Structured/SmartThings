### SmartThings Device Handler for Soma Smart Shades

This device handler allows you to control your Soma Smart Shades (https://www.somasmarthome.com/) through SmartThings. 

The device handler interacts with a Soma Connect device on your LAN to control your Soma Smart Shades. Soma Connect version 2.06 or greater is required, and installation/upgrade instructions can be found here: https://somasmarthome.zendesk.com/hc/en-us/articles/360035521234

To use, add a new device to SmartThings for each Soma Smart Shade that you have connected. You will then need to configure each device with the IP address of your Soma Connect, and the MAC address of the Soma Smart Shade device. This can be done through the SmartThings Groovy IDE or the SmartThings Classic app. 

At this stage, the only way (that Im aware of) to get the MAC addresses of your Soma Smart Shades is through the API on your Soma Connect. While connected to your LAN, browse to http://X.X.X.X:3000/list_devices where X.X.X.X is the IP address of your SOMA connect.

This is the first SmartThings device handler that I have written, so there is likely a lot of room for improvement. 

