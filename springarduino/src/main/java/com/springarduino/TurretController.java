package com.springarduino;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TurretController {
    private final ArduinoConnection arduino;

    @Autowired
    public TurretController(ArduinoConnection arduino) {
        this.arduino = arduino;
    }

    @RequestMapping(value = "turret/execute", method = RequestMethod.POST, consumes = "application/json")
    public TurretResponse executeTurretAction(@RequestBody TurretRequest request) {
        if (request.getPan() < 0 || request.getPan() > 180) {
            throw new IllegalArgumentException("Pan out of 0..180 range (" + request.getPan() + ")");
        }

        if (request.getTilt() < 0 || request.getTilt() > 180) {
            throw new IllegalArgumentException("Tilt out of 0..180 range (" + request.getTilt() + ")");
        }

        boolean sent = arduino.controlTurret(request.getPan(), request.getTilt(), request.isFire());
        if (!sent) {
            throw new RuntimeException("Command not sent :(");
        }

        return new TurretResponse(request.getId(), "Command sent :)");
    }
}
