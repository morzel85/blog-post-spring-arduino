package springarduino;

import gnu.io.NRSerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.DataOutputStream;

@Component
public class ArduinoConnection {
    private static final int MESSAGE_SEPARATOR = 255;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${arduinoPortName}")
    private String portName;

    @Value("${arduinoBaudRate}")
    private int baudRate;

    private NRSerialPort serial;


    @PostConstruct
    public void Connect() {
        log.info("ArduinoConnection PostConstruct callback: connecting to Arduino...");

        serial = new NRSerialPort(portName, baudRate);
        serial.connect();

        if (serial.isConnected()) {
            log.info("Arduino connection opened!");
        }
    }

    @PreDestroy
    public void Disconnect() {
        log.info("ArduinoConnection PreDestroy callback: disconnecting from Arduino...");

        if (serial != null && serial.isConnected()) {
            serial.disconnect();

            if (!serial.isConnected()) {
                log.info("Arduino connection closed!");
            }
        }
    }

    public boolean ControlTurret(int pan, int tilt, boolean fire){
        try {
            // Actual values sent to Arduino will be in proper unsigned byte range (0..255)
            byte[] message = new byte[]{(byte) pan, (byte) tilt, (byte) (fire ? 1 : 0), (byte) MESSAGE_SEPARATOR};

            DataOutputStream stream = new DataOutputStream(serial.getOutputStream());
            stream.write(message);

            log.info("Turret control message sent (pan={}, tilt={}, fire={})!", pan, tilt, fire);
            return  true;
        } catch (Exception ex) {
            log.error("Error while sending control message: ", ex);
            return false;
        }
    }
}
