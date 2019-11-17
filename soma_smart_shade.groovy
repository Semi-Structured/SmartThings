/**
 *  Soma Smart Shade
 *
 *  Copyright 2019 Ben A
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */

metadata {
	definition (name: "Soma Smart Shade", namespace: "semi-structured", author: "Ben A", cstHandler: true) {
		capability "Window Shade"
		capability "Battery"
    capability "Switch Level"
		capability "Refresh"
	}


	simulator {
		// TODO: define status and reply messages here
	}

  tiles(scale: 2) {
    multiAttributeTile(name:"windowShade", type: "generic", width: 6, height: 4) {
      tileAttribute("device.windowShade", key: "PRIMARY_CONTROL") {
        attributeState "open", label: 'Open', action: "close", icon: "https://raw.githubusercontent.com/a4refillpad/media/master/blind-open.png", backgroundColor: "#e86d13", nextState: "closing"
        attributeState "closed", label: 'Closed', action: "open", icon: "https://raw.githubusercontent.com/a4refillpad/media/master/blind-closed.png", backgroundColor: "#00A0DC", nextState: "opening"
        attributeState "partially open", label: 'Partially open', action: "close", icon: "https://raw.githubusercontent.com/a4refillpad/media/master/blind-part-open.png", backgroundColor: "#d45614", nextState: "closing"
        attributeState "opening", label: 'Opening', action: "pause", icon: "st.thermostat.thermostat-up", backgroundColor: "#e86d13", nextState: "partially open"
        attributeState "closing", label: 'Closing', action: "pause", icon: "st.thermostat.thermostat-down", backgroundColor: "#00A0DC", nextState: "partially open"
      }
      tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
        attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
      }
    }
    standardTile("contPause", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "pause", label:"", icon:'st.sonos.stop-btn', action:'pause', backgroundColor:"#cccccc"
    }
    standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
      state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
    }
    valueTile("shadeLevel", "device.level", width: 3, height: 1) {
      state "level", label: 'Blind is ${currentValue}% open', defaultState: true
    }
    controlTile("levelSliderControl", "device.level", "slider", width:3, height: 1, inactiveLabel: false) {
      state "level", action:"switch level.setLevel"
    }
    standardTile("resetClosed", "device.windowShade", inactiveLabel: false, decoration: "flat", width: 3, height: 1) {
  		state "default", action:"close", label: "Close", icon:"https://raw.githubusercontent.com/a4refillpad/media/master/blind-closed.png"
  	}
  	standardTile("resetOpen", "device.windowShade", inactiveLabel: false, decoration: "flat", width: 3, height: 1) {
  		state "default", action:"open", label: "Open", icon:"https://raw.githubusercontent.com/a4refillpad/media/master/blind-open.png"
  	}
   	valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
  		state "battery", label:'${currentValue}% battery', unit:""
  	}

    main "windowShade"
    details(["windowShade", "shadeLevel", "levelSliderControl", "contPause", "battery", "refresh", "resetClosed", "resetOpen"])
  }
}

preferences {
		input "ip_address", "text", title: "SOMA Connect IP", required: true
		input "port", "text", title: "Port (if blank = 3000)", required: false
    input "mac_address", "text", title: "MAC Address", required: true
}


// parse events into attributes
def parse(String description) {
  def msg = parseLanMessage(description)
	log.debug "Parsing '${msg}'"
  def json = msg.json
	if (json.result == "success") {
		log.debug "Success"
		if (json.battery_level){
			sendEvent(name: "battery", value: json.battery_level)
		}
		if (json.position){
			sendEvent(name: "level", value: json.position)
		}
	}

	// TODO: handle 'battery' attribute
	// TODO: handle 'windowShade' attribute
	// TODO: handle 'supportedWindowShadeCommands' attribute

}

// handle commands
def open() {
  log.debug "Executing OPEN"
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
		sendEvent(name: "shade", value: "open")
    log.debug result
	}
}

def close() {
  log.debug "Executing CLOSE"
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
  sendEvent(name: "shade", value: "close")
  log.debug result
  }
}

def pause() {
  log.debug "Executing STOP"
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
  sendEvent(name: "shade", value: "stopped")
  log.debug result
  }
}

def setLevel(data) {
  log.debug "Executing SET LEVEL"
  if (ip_address){
    def port
    if (port){
      port = "${port}"
    } else {
      port = 3000
    }

  data = data.toInteger()

  def result = new physicalgraph.device.HubAction(
    method: "GET",
    path: "/set_shade_position/${mac_address}/${data}",
    headers: [
      HOST: "${ip_address}:${port}"
      ]
  )
  sendHubCommand(result)
  sendEvent(name: "shade", value: "set level ${data}")
  log.debug result
  }
}

def getLevel() {
  log.debug "Executing GET SHADE LEVEL"
  if (ip_address){
    def port
    if (port){
      port = "${port}"
    } else {
      port = 3000
    }

  def result = new physicalgraph.device.HubAction(
    method: "GET",
    path: "/get_shade_state/${mac_address}",
    headers: [
      HOST: "${ip_address}:${port}"
      ]
  )
  sendHubCommand(result)
  sendEvent(name: "shade", value: "get level")
  log.debug result
  }
}

def batteryLevel() {
  log.debug "Executing GET BATTERY LEVEL"
  if (ip_address){
    def port
    if (port){
      port = "${port}"
    } else {
      port = 3000
    }

  def result = new physicalgraph.device.HubAction(
    method: "GET",
    path: "/get_battery_level/${mac_address}",
    headers: [
      HOST: "${ip_address}:${port}"
      ],
  )
  sendHubCommand(result)
  sendEvent(name: "shade", value: "get battery level")
  log.debug result
  }
}

def refresh() {
	getLevel()
	batteryLevel()
}
