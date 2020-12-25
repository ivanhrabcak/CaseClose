# CaseClose
Turn off the screen when a flip case is closed on unsupported android devices (without proximity sensor)

# How does it work?
Every time the light sensor registers 0 (from testing), the screen sleep timeout gets changed to 1 second. After sleeping for 1 second screen timeout changes back to its original value.
 
