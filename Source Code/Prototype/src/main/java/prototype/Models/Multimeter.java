package prototype.Models;

import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static prototype.Models.LinC.cachedSeries;
import static prototype.Models.LinC.setAxisBounds;

/**
 * Class manages the state, data and functions related to the Multimeter
 * Author : Anant Tuli
 */
public class Multimeter {

    private static Multimeter multimeter = null;
//    private String currentMeasurementType;
    private SimpleStringProperty currentMeasurementType = new SimpleStringProperty();
    public static Label topScreenLabel;
    public static Label bottomScreenLabel;
    public static Reading serialTestReading;
    public static String DC_UNICODE = "\u2393";
    public static volatile double numberOfSamples = 1;
    public static volatile double numberOfDuration = 1;
    Reading currentReading;
    public ObservableList<Reading> currentList;
    public List<Reading> pausedList;
    public List<Reading> loadedReadings;
    public List<Reading> loadedMaskReadings;
    public List<Reading> fullReadingsList;
    public List<Reading> saveList;
    public List<Reading> getFullReadingsList(){
        return this.fullReadingsList;
    }


    protected Multimeter(){

    }
    public static Multimeter getInstance(){
        if(multimeter == null)
        multimeter = new Multimeter();
            return multimeter;
    }

    public Reading getCurrentReading(){
        return this.currentReading;
    }




    public String getCurrentMeasurementType() {
        return currentMeasurementType.get();
    }

    public SimpleStringProperty currentMeasurementTypeProperty() {
        return currentMeasurementType;
    }

    public void setCurrentMeasurementType(String currentMeasurement) {
        this.currentMeasurementType.set(currentMeasurement);
    }

    /**
     * Initialize multimeter
     */
    public void initMultimeter(){
        currentMeasurementType.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // initialise line chart
                Platform.runLater(()->{
                    Multimeter.getInstance().setCurrentMeasurementType(newValue);
                    LinC.initLineChart(LinC.connectedLineChart);
                    cachedSeries.setName(Multimeter.getInstance().getCurrentMeasurementType());

                    Multimeter.getInstance().currentList.clear();
                    LinC.cachedSeries.getData().clear();
                    LinC.connectedLineChart.getData().clear();
                });

                LinC.resumeCounter = 0;
            }
        });
        fullReadingsList = new ArrayList<>();
        saveList = new ArrayList<Reading>();
        loadedReadings = new ArrayList<>();
//        currentCachedList = new ArrayList<>();
//        toBeSaved = new ArrayList<>();
//        wasDiscarded = false;
        pausedList = new ArrayList<Reading>();
        currentList = FXCollections.observableArrayList();
        currentList.addListener(new ListChangeListener<Reading>() {
            @Override
            public void onChanged(Change<? extends Reading> c) {
                while(c.next()) {
                    if (c.wasAdded()) {
                        //if(currentList.size() > 20) Multimeter.getInstance().setCurrentMeasurementType("AAC");
                        System.out.println("LIST CHANGED");
                        Platform.runLater(()->{

                            if((!LinC.isGraphPaused) && LinC.isInitialised) {
                                LinC.scrollPane.setHvalue(LinC.scrollPane.getHmax());
                                LocalDateTime currentTime = LocalDateTime.now();
                                LinC.cachedSeries.getData().add(new XYChart.Data<String, Number>(currentTime.format(DateTimeFormatter.ISO_LOCAL_TIME),
                                        Double.parseDouble(c.getAddedSubList().get(0).getValue()) ));

                                double maxValueForScale = 0;
                                maxValueForScale = Double.parseDouble(Multimeter.getInstance().currentList.get(0).getValue());
                                for(Reading r: Multimeter.getInstance().currentList){
                                    maxValueForScale = (Math.abs(maxValueForScale) < Math.abs(Double.parseDouble(r.getValue())))?Double.parseDouble(r.getValue()):maxValueForScale ;
                                }
                                setAxisBounds(LinC.connectedLineChart,Multimeter.getInstance().getCurrentMeasurementType(),maxValueForScale);
                                LinC.timeRangeConnected.setText(Multimeter.getInstance().currentList.get(0).getTimeStamp() + " / " +
                                         currentTime.toString());

                                System.out.println((LocalDateTime.now().toString() + " ,,,, " +
                                        Double.parseDouble(c.getAddedSubList().get(0).getValue())));

                            }
                        });

                    }
                }
            }
        });

    }



    public boolean isOpticalLinkConnected(){return false;} //If Optical link is not connected, connect mode cannot be enabled in software


}
