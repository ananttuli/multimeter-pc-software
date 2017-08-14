package prototype.Models;

import javafx.beans.property.SimpleStringProperty;

/**
 * Class for modelling a Mask Failure
 * @author Anant Tuli
 */
public class MaskFailure {

    private final SimpleStringProperty failNo;
    private final SimpleStringProperty failStart;
    private final SimpleStringProperty failEnd;

    public MaskFailure(SimpleStringProperty failNo, SimpleStringProperty failStart, SimpleStringProperty failEnd) {
        this.failNo = failNo;
        this.failStart = failStart;
        this.failEnd = failEnd;
    }

    public MaskFailure(String failNo, String failStart, String failEnd) {
        this.failNo = new SimpleStringProperty();
        this.failNo.setValue(failNo);

        this.failStart = new SimpleStringProperty();
        this.failStart.setValue(failStart);

        this.failEnd = new SimpleStringProperty();
        this.failEnd.setValue(failEnd);
    }


    public String getFailNo() {
        return failNo.get();
    }

    public SimpleStringProperty failNoProperty() {
        return failNo;
    }

    public void setFailNo(String failNo) {
        this.failNo.set(failNo);
    }

    public String getFailStart() {
        return failStart.get();
    }

    public SimpleStringProperty failStartProperty() {
        return failStart;
    }

    public void setFailStart(String failStart) {
        this.failStart.set(failStart);
    }

    public String getFailEnd() {
        return failEnd.get();
    }

    public SimpleStringProperty failEndProperty() {
        return failEnd;
    }

    public void setFailEnd(String failEnd) {
        this.failEnd.set(failEnd);
    }
}
