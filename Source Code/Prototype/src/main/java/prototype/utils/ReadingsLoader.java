package prototype.utils;

import prototype.Models.MaskReading;
import prototype.Models.Reading;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anant on 13-Mar-17.
 */
public class ReadingsLoader {

    List<Reading> readingsList = new ArrayList<>();
    List<MaskReading> maskFileList = new ArrayList<>();
    String fileName;
    public ReadingsLoader(String filename) throws IOException {
        this.fileName = filename;
        BufferedReader br = new BufferedReader(new FileReader(filename));

        String line;
        while ((line = br.readLine()) != null) {
            String[] fields = line.split(",", -1);
            if(fields[1].equals("OL")){
                if(fields[0].equals("VDC") || fields[0].equals("VAC") ) {
                    fields[1] = new String("12");
                }
                if(fields[1].equals("ADC") || fields[1].equals("AAC")){
                    fields[1] =new String("195");
                }
            }
            Reading reading = new Reading(Double.parseDouble(fields[1]), fields[0], fields[2]);
            readingsList.add(reading);
        }

    }


    public ReadingsLoader(String filename, String mode) throws IOException {
        this.fileName = filename;
        BufferedReader br = new BufferedReader(new FileReader(filename));

        String line;
        while ((line = br.readLine()) != null) {
            String[] fields = line.split(",", -1);
            MaskReading mr = new MaskReading(Float.parseFloat(fields[2]),Float.parseFloat(fields[1]),fields[3], fields[0]);
            maskFileList.add(mr);
        }

    }
    public List<Reading> getLoadedReadings(){
        return this.readingsList;
    }
    public List<MaskReading> getMaskFile(){return this.maskFileList;}
}
