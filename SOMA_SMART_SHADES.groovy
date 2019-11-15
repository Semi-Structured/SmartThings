/*
* Author: MAC
*
* SOMA SMART BLINDS Device Handler
* https://raw.githubusercontent.com/a4refillpad/media/master/blind-part-open.png
*/

metadata {
	definition (name: "SOMA", namespace: "mac", author: "Mark C") {
		capability "Actuator"
			capability "Switch"
			capability "Sensor"
	}

preferences {
	
		input "ip_address", "text", title: "SOMA Connect IP", required: true
		input "port", "text", title: "Port (if blank = 3000)", required: false
        input "mac_address", "text", title: "MAC Address", required: true
	}

// simulator metadata
	simulator {
	}

	// UI tile definitions
	tiles (scale: 2) {
		standardTile("button", "device.switch", width: 6, height: 4, canChangeIcon: true) {
			state "off", label: 'OPEN', action: "switch.on", icon: "https://raw.githubusercontent.com/a4refillpad/media/master/blind-closed.png", backgroundColor: "#ffffff", nextState: "on"
				state "on", label: 'CLOSED', action: "switch.off", icon: "https://raw.githubusercontent.com/a4refillpad/media/master/blind-open.png", backgroundColor: "#79b821", nextState: "off"
		}
		standardTile("offButton", "device.switch", width: 3, height: 3, canChangeIcon: true) {
			state "default", label: 'OPEN', action: "switch.off", icon: "https://raw.githubusercontent.com/a4refillpad/media/master/blind-open.png", backgroundColor: "#ffffff"
		}
		standardTile("onButton", "device.switch", width: 3, height: 3, canChangeIcon: true) {
			state "default", label: 'CLOSE', action: "switch.on", icon: "https://raw.githubusercontent.com/a4refillpad/media/master/blind-closed.png", backgroundColor: "#79b821"
		}
        standardTile("stopButton", "device.switch", width: 6, height: 2, canChangeIcon: true) {
			state "default", label: 'STOP', action: "switch.stop", icon: "https://raw.githubusercontent.com/a4refillpad/media/master/blind-closed.png", backgroundColor: "#79b821"
		}
		main "button"
			details (["button","onButton","offButton","stopButton"])
	}
}

def parse(String description) {
	log.debug(description)
}

def on() {
	
	if (ip_address){
		def port
			if (port){
				port = "${port}"
			} else {
				port = 3000
			}

		def result = new physicalgraph.device.HubAction(
				method: "GET",
				path: "/close_shade/${mac_address}",
				headers: [
				HOST: "${ip_address}:${port}"
				]
				)
			sendHubCommand(result)
			sendEvent(name: "switch", value: "on") 
			log.debug "Executing ON" 
			log.debug result
	}
}

def off() {
	
	if (ip_address){
		def port
			if (port){
				port = "${port}"
			} else {
				port = 3000
			}

		def result = new physicalgraph.device.HubAction(
				method: "GET",
				path: "/open_shade/${mac_address}",
				headers: [
				HOST: "${ip_address}:${port}"
				]
				)

			sendHubCommand(result)
			sendEvent(name: "switch", value: "off")
			log.debug "Executing OFF" 
			log.debug result
	}
}

def stop() {
	
	if (ip_address){
		def port
			if (port){
				port = "${port}"
			} else {
				port = 3000
			}

		def result = new physicalgraph.device.HubAction(
				method: "GET",
				path: "/stop_shade/${mac_address}",
				headers: [
				HOST: "${ip_address}:${port}"
				]
				)

			sendHubCommand(result)
			sendEvent(name: "switch", value: "stop")
			log.debug "Executing STOP" 
			log.debug result
	}
}
