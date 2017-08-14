package prototype.Models;

import javafx.beans.property.SimpleStringProperty;

/**
 * This class is the model for saving/retrieving mask readings
 * @author Anant Tuli
 */
public class MaskReading {

    Number yValue;
    Number xTimeValue;
    String maskReadingType;
    String maskMode;

    public MaskReading(Number yValue, Number xTimeValue, String maskReadingType, String maskMode) {
        this.yValue = yValue;
        this.xTimeValue = xTimeValue;
        this.maskReadingType = maskReadingType;
        this.maskMode = maskMode;
    }

    public String getMaskMode() {
        return maskMode;
    }

    public void setMaskMode(String maskMode) {
        this.maskMode = maskMode;
    }

    public Number getyValue() {
        return yValue;
    }

    public void setyValue(Number yValue) {
        this.yValue = yValue;
    }

    public Number getxTimeValue() {
        return xTimeValue;
    }

    public void setxTimeValue(Number xTimeValue) {
        this.xTimeValue = xTimeValue;
    }

    public String getMaskReadingType() {
        return maskReadingType;
    }

    public void setMaskReadingType(String maskReadingType) {
        this.maskReadingType = maskReadingType;
    }

    public SimpleStringProperty value;
    public SimpleStringProperty readingType; // VAC for volts AC, VDC,IAC,IDC,R
}
