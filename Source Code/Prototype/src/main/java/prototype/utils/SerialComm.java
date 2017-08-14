package prototype.utils;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import prototype.MainSceneController;
import prototype.Models.LinC;
import prototype.Models.Multimeter;
import prototype.Models.Reading;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class handles majority of the Serial I/O between the Multimeter and PC Software
 * Created by anant on 28-Mar-17.
 */
public class SerialComm {

    private static SerialComm comm = null;
    public static SerialPort comPort;
    public static boolean hasValidSerialInput;
    public static Label overLimitLabelConnected;
    protected SerialComm(){

    }
    public static SerialComm getInstance(){
        if(comm == null){
            comm = new SerialComm();
        }
        return comm;
    }

    /**
     * Function to map Units to serial
     * @param unit
     * @return
     */
    public static String mapUnitFromSerial(String unit){
        switch(unit){
            case "1":
                return "VAC";

            case "2":
                return "VDC";

            case "3":
                return "AAC";

            case "4":
                return "ADC";

            case "5":
                return "R";
            case "6":
                return "C";
            //Logic
            case "7":
                return "V";

            default: break;
        }
        return null;
    }

    /**
     * Checks and handles OVER LIMIT data
     * Shows warning message when OL data is received via SerialPort
     * @param readFromSerial
     */
    public static void checkOLData(Reading readFromSerial){
        if(readFromSerial.getReadingType().equals("VAC") || readFromSerial.getReadingType().equals("VDC")){
            if(Double.parseDouble(readFromSerial.getValue()) > 11.5 ||Double.parseDouble(readFromSerial.getValue()) < -11.5){
                //OL
                Platform.runLater(()-> overLimitLabelConnected.setVisible(true));

            }else{
                Platform.runLater(()-> overLimitLabelConnected.setVisible(false));
            }
        }
        else if(readFromSerial.getReadingType().equals("AAC") || readFromSerial.getReadingType().equals("ADC") ){
            if(Double.parseDouble(readFromSerial.getValue()) > 195 || Double.parseDouble(readFromSerial.getValue()) < -195 ){
                //OL
                Platform.runLater(()-> overLimitLabelConnected.setVisible(true));

            }else{
                Platform.runLater(()-> overLimitLabelConnected.setVisible(false));
            }
        }else{
            Platform.runLater(()-> overLimitLabelConnected.setVisible(false));
        }
    }

    /**
     * Helper function to parse data received from serial input
     * @param line
     */
    public static void parseReadingFromSerial(String line) {

        System.out.println(line);
        final String systemRegex = "^SYSTEM:(.*)";
        final String logRegex = "";
        final String dataRegex = "^DATA:(-)?(\\d+),(\\d+),(\\d+)$";

        final String string = "";

        final Pattern systemPattern = Pattern.compile(systemRegex);
        final Pattern logPattern = Pattern.compile(logRegex);
        final Pattern dataPattern = Pattern.compile(dataRegex);
        //Using line.substring(1) to avoid preceding \n
        final Matcher matcher = dataPattern.matcher(line.substring(1));
        final Matcher systemMatcher = systemPattern.matcher(line.substring(1));
        if (matcher.find()) {
            String[] parts = line.substring(1).substring(5).split(",");

            Reading readFromSerial = new Reading(Double.parseDouble(parts[0] + "." + parts[1]),
                                                    SerialComm.mapUnitFromSerial(parts[2]),
                                                    LocalDateTime.now().toString());

//            System.out.println("Reading created: Value: " + readFromSerial.getValue());
//            System.out.println("                 Type: " + readFromSerial.getReadingType());
//            System.out.println("                 Time: " + readFromSerial.getTimeStamp());
//            System.out.println("----------------------------------------------------------");
            Multimeter.serialTestReading = readFromSerial;
            Multimeter.getInstance().setCurrentMeasurementType(readFromSerial.getReadingType());

            //CHECK FOR OVERLIMIT DATA
            checkOLData(readFromSerial);
            Multimeter.getInstance().currentList.add(readFromSerial);
//            return readFromSerial;

        }else{
            System.out.println("Could not find data regex pattern");

        }

        if(systemMatcher.find()){
            String top = new String(line.substring(8,24));
            String bottom = new String(line.substring(24,40));
            String leftBottom = bottom.replace('\u007f', '←');
            String finalBottom = leftBottom.replace('\u007e', '→');
            //System.out.println("TOP:"+top+"\n" );
            Platform.runLater(()->{
                Multimeter.topScreenLabel.setText(top);
                Multimeter.bottomScreenLabel.setText(finalBottom);
            });

            //System.out.println("BOTTOM:"+finalBottom+"\n" );

        }else{
            System.out.println("System regex not matched");
        }
//        return null;
    }

    /**
     * Method used to send remote commands from PC Software to Firmware/Multimeter
     * @param toWrite
     */
    public void writeToSerialButtons(String toWrite){
        String toWriteFinal = new String(toWrite + "\n");
        byte[] msg = toWriteFinal.getBytes();
        comPort.writeBytes(msg, msg.length);
    }


    /**
     * Helper function to identify and initialize serial port
     */
    public void initSerial(){
        boolean found = false;
//     comPort = SerialPort.getCommPorts()[0];
        SerialPort[] allCommPorts = SerialPort.getCommPorts();
        for(int k = 0; k < allCommPorts.length; k++) {
            //        if(allCommPorts[i].getDescriptivePortName().contains("USB Serial Port (COM")) {
            if(allCommPorts[k].LISTENING_EVENT_DATA_AVAILABLE == 1) {
                comPort = allCommPorts[k];
                System.out.println(comPort.getDescriptivePortName());
                found = true;
                SerialComm.hasValidSerialInput = true;
            }


            //       }
        }
        if(found == false){
            SerialComm.hasValidSerialInput = false;
             System.out.println("Could not detect serial port");
            return;
        }

     System.out.println(comPort.getDescriptivePortName());
        comPort.setComPortParameters(9600, 8,SerialPort.ONE_STOP_BIT,SerialPort.NO_PARITY);


        comPort.openPort();
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
        //-----------WRITING---------\\
        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_WRITTEN; }
            @Override
            public void serialEvent(SerialPortEvent event)
            {
                if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_WRITTEN)
                    System.out.println("All bytes were successfully transmitted!");
            }
        });


        //-----WRITING END\-------------
        InputStream in = comPort.getInputStream();
        new Thread(()->{
                StringBuilder lineBuilder = new StringBuilder();
                char c;
                try {
                    while(comPort.isOpen()) {
                        c = (char)in.read();
                        System.out.print(c);
                        if(c == '\n'){
                            parseReadingFromSerial(lineBuilder.toString());
                            System.out.print(lineBuilder.toString());
                            lineBuilder = new StringBuilder();

                        }
                        if(c == '\r') continue;
                        lineBuilder.append(c);
                    }

                } catch (IOException e) {
                    e.printStackTrace();

                }
     }).start();
    }
}
