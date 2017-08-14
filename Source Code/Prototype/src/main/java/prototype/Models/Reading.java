package prototype.Models;

import javafx.beans.property.SimpleStringProperty;
import sun.java2d.pipe.SpanShapeRenderer;

import java.time.LocalDateTime;

/**
 * Class stores the fundamental design of a reading. Basic building block for all data interaction within the software
 * Author : Anant Tuli
 */
public class Reading {
    public SimpleStringProperty value;
    public SimpleStringProperty readingType; // VAC for volts AC, VDC,IAC,IDC,R
    public SimpleStringProperty timeStamp;


    public Reading(double reading, String readingType, String timestamp){
        this.value = new SimpleStringProperty(reading+"");


        this.readingType = new SimpleStringProperty(readingType);
        this.timeStamp = new SimpleStringProperty(timestamp);


    }



    public String getValue() {
        return value.get();
    }

    public SimpleStringProperty valueProperty() {
        return value;
    }

    public void setValue(String value) {
        this.value.set(value);
    }

    public String getReadingType() {
        return readingType.get();
    }

    public SimpleStringProperty readingTypeProperty() {
        return readingType;
    }

    public void setReadingType(String readingType) {
        this.readingType.set(readingType);
    }

    public String getTimeStamp() {
        return timeStamp.get();
    }

    public SimpleStringProperty timeStampProperty() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp.set(timeStamp);
    }
}
