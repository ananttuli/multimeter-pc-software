package prototype.Models;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import prototype.utils.ParentRelayPair;
import prototype.utils.RelayCoord;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static prototype.utils.RelayCoordManager.newHighLowCollisionDetection;

/**
 * Class manages plotting of connected, disconnected and mask testing modes
 * Contains various helper functions related to charting
 */
public class LinC {
    // References the connected Line Chart
    public static LineChart<String, Number> connectedLineChart;

    // References the mask file readings
    public static List<MaskReading> maskFileReadings;
    public static void setConnectedLineChart(LineChart<String, Number> lc) {
        connectedLineChart = lc;
    }

    public static int resumeCounter = 0;
    public static boolean isGraphPaused = false;
    public static boolean isInitialised = false;
    public static StringBuilder unitBeforePausing = new StringBuilder();
    public static XYChart.Series<String, Number> cachedSeries;
    public static XYChart.Series<String, Number> cachedSeriesBackup = new XYChart.Series<>();
    public static XYChart.Series<String, Number> pausedSeries = new XYChart.Series<>();
    public static ScrollPane scrollPane = new ScrollPane();
    public static Label graphHoverLabelConnected;
    public static Label timeRangeConnected;
    public static Label timeRangeDisconnected;
    public static Label timeRangeMasked;
    public static XYChart.Series<Number, Number> maskSeriesHigh = new XYChart.Series<>();
    public static XYChart.Series<Number, Number> maskSeriesLow = new XYChart.Series<>();
    public static RadioButton lowMaskRadioButton;
    public static RadioButton highMaskRadioButton;

    public static List<ParentRelayPair> highRelayPairs;
    public static List<ParentRelayPair> lowRelayPairs;
    public static List<ParentRelayPair> dataRelayPairs;

    public static String currentMaskUnit;
    public static double maskYError;
    public static double maskXError;
    public static XYChart.Series<Number, Number> savedSeriesForMask;
    public static Label logicModeLegendLabel;
    /**
     * Comparator created to compare two XYChart Data Points according to their X Axis values
     */
    public static Comparator<XYChart.Data<Number, Number>> xSort = new Comparator<XYChart.Data<Number, Number>>() {
        @Override
        public int compare(XYChart.Data<Number, Number> o1, XYChart.Data<Number, Number> o2) {
            if(o1.getXValue().floatValue() < o2.getXValue().floatValue()){
                return -5;
            }else if(o1.getXValue().floatValue() > o2.getXValue().floatValue()){
                return 5;
            }else{
                return 0;
            }
        }
    };
    public LinC() {

    }

    /**
     * TODO: CHECK IMPORTANT, UNIT A OR MA . KEEP CONSISTENT/CONVERT TO AMPS
     *
     * @param min
     * @param max
     * @param measurementType
     * @return
     */

    //Assigns mask a measurement type to check if mask is valid for data
    public static void calculateMaskError(double min, double max, double tickUnit, String measurementType) {

        //maskYError = tickUnit-tickUnit;
        maskYError = tickUnit;
        maskXError = 0.1;
        switch (measurementType) {
            case "VDC":
            case "VAC":
                currentMaskUnit = new String("V");
                break;

            case "R":
                currentMaskUnit = new String("Ohm");
                break;
            case "AAC":
            case "ADC":
                currentMaskUnit = new String("mA");
                break;
            default:
                break;
            }


    }

    /**
     * Sets axis bounds according to reading type, reading value
     * Set of overloaded functions for different utilities
     * @param myChart
     * @param min
     * @param max
     * @param tickUnit
     */
    public static void setAxisBounds(LineChart<String, Number> myChart, double min, double max, double tickUnit) {
        NumberAxis axis;
            axis=(NumberAxis) myChart.getYAxis();
        axis.setAutoRanging(false);
        axis.setLowerBound(min);axis.setTickUnit(tickUnit);
        axis.setUpperBound(max);
    }
    //FINAL AXIS BOUNDS SETTER FOR MASKING
    public static void setAxisBounds(LineChart lc, String measurementType, double valueToBeAdded, String mode) {
        if(mode.equals("mask")) {
            double min = 0, max = 100, tickUnit = 1;
            String graphLabel;
            double absValueToBeAdded = Math.abs(valueToBeAdded);
            switch (measurementType) {
                case "VAC":
                case "VDC":
                    if (absValueToBeAdded <= 1) {
                        min = -5;
                        max = 5;
                        tickUnit = 0.1;

                    }
                    if (absValueToBeAdded > 1 && absValueToBeAdded <= 5) {
                        min = -12;
                        max = 12;
                        tickUnit = 0.25;
                    }
                    if (absValueToBeAdded > 5 && absValueToBeAdded <= 12) {
                        min = -20;
                        max = 20;
                        tickUnit = 0.5;
                    }
                    if (absValueToBeAdded > 12) {
                        min = -20;
                        max = 20;
                        tickUnit = 1;
                    }
                    graphLabel = new String("Volts");

                    break;

                case "AAC":
                case "ADC":
                    if (absValueToBeAdded <= 10) {
                        min = -15;
                        max = 15;
                        tickUnit = 0.5;
                    }
                    if (absValueToBeAdded > 10 && absValueToBeAdded <= 200) {
                        min = -250;
                        max = 250;
                        tickUnit = 5;
                    }
                    if (absValueToBeAdded > 200) {
                        min = -250;
                        max = 250;
                        tickUnit = 10;
                    }
                    graphLabel = new String("Current (mA)");
                    break;

                case "R":
                    if (absValueToBeAdded <= 1000) {
                        min = 0;
                        max = 1200;
                        tickUnit = 50;
                    }
                    if (absValueToBeAdded > 1000) {
                        min = 0;
                        max = 1010000;
                        tickUnit = 50000;
                    }
                    graphLabel = new String("R (Ohm)");
                    break;

                case "C":
                    min = -2; max = 2; tickUnit = 1;
                    graphLabel = new String("Continuity");
                    break;
                case "V":
                    min = -2; max = 2; tickUnit= 0.5;
                    graphLabel = new String("Logic");
                    break;
                default:
                    min = -1200;
                    max = 1200;
                    tickUnit = 200;
                    graphLabel = new String("");
            }

            lc.getYAxis().setLabel(graphLabel);
            NumberAxis axis;
            axis = (NumberAxis) lc.getYAxis();
            axis.setAutoRanging(false);
            axis.setLowerBound(min);
            axis.setTickUnit(tickUnit);
            axis.setUpperBound(max);

            calculateMaskError(min, max, tickUnit, measurementType);
        }
    }

    public static void setAxisBounds(LineChart lc, String measurementType, double valueToBeAdded) {
        double min = 0,max = 100,tickUnit = 1;
        String graphLabel;
        double absValueToBeAdded = Math.abs(valueToBeAdded);
        switch (measurementType) {
            case "VAC":
            case "VDC":
                if(absValueToBeAdded <= 1){
                    min = -1; max = 1; tickUnit = 0.1;
                }
                if(absValueToBeAdded > 1 && absValueToBeAdded <= 5){
                    min = -5; max = 5; tickUnit = 0.25;
                }
                if(absValueToBeAdded > 5 && absValueToBeAdded <= 12){
                    min = -12; max = 12; tickUnit = 0.5;
                }
                if(absValueToBeAdded > 12){
                    min = -20; max = 20; tickUnit = 1;
                }
                graphLabel = new String("Volts");

                break;

            case "AAC":
            case "ADC":
                if(absValueToBeAdded <= 10){
                    min = -10; max = 10; tickUnit = 0.5;
                }
                if(absValueToBeAdded > 10 && absValueToBeAdded <= 200){
                    min = -200; max = 200; tickUnit = 5;
                }
                if(absValueToBeAdded > 200){
                    min = -300; max = 300; tickUnit = 10;
                }
                graphLabel = new String("Current (mA)");
                break;

            case "R":
                if(absValueToBeAdded <= 1000){
                    min = 0; max = 1000; tickUnit = 50;
                }
                if(absValueToBeAdded > 1000){
                    min = 0; max = 1000000; tickUnit = 50000;
                }
                graphLabel = new String("R (Ohm)");
                break;
            case "V":
                min = 0; max = 1; tickUnit = 0.5;
                graphLabel = new String("V");
                break;
            case "C":
                min = 0; max = 1; tickUnit = 1;
                graphLabel = new String("Continuity");
                break;
            default:
                min = -1000;max = 1000; tickUnit = 200;
                graphLabel = new String("");
        }

        lc.getYAxis().setLabel(graphLabel);
        NumberAxis axis;
        axis=(NumberAxis) lc.getYAxis();
        axis.setAutoRanging(false);
        axis.setLowerBound(min);axis.setTickUnit(tickUnit);
        axis.setUpperBound(max);

        calculateMaskError(min, max, tickUnit, measurementType);
    }

    public static void setAxisBounds(LineChart<String, Number> lc, String measurementType) {
        double min,max,tickUnit;
        String graphLabel;

        switch (measurementType) {
            case "VAC":
                min = -1; max = 1; tickUnit = 0.1;
                graphLabel = new String("Volts");
                logicModeLegendLabel.setVisible(false);
                break;
            case "VDC":  // yAxis.setUpperBound(25);
                min = -1; max = 1; tickUnit = 0.1;
                graphLabel = new String("Volts");
                logicModeLegendLabel.setVisible(false);
                break;
            case "AAC":
                min = -10; max = 10; tickUnit = 0.5;
                graphLabel = new String("Current (mA)");
                logicModeLegendLabel.setVisible(false);
                break;
            case "ADC":
                min = -10; max = 10; tickUnit = 0.5;
                graphLabel = new String("Current (mA)");
                logicModeLegendLabel.setVisible(false);
                break;
            case "R":
                min = 0; max = 1000; tickUnit = 200;
                graphLabel = new String("R (Ohm)");
                logicModeLegendLabel.setVisible(false);
                break;
            case "V":
                min = 0;max = 1; tickUnit = 0.5;
                graphLabel = new String("Logic (V)");
                logicModeLegendLabel.setText("Logic Mode | 0 => Low | 0.5 => Floating | 1 => High");
                logicModeLegendLabel.setVisible(true);
                break;
            case "C":
                min = 0; max = 1; tickUnit = 1;
                graphLabel = new String("Continuity");
                break;
            default:
                min = -1000;max = 1000; tickUnit = 200;
                graphLabel = new String("");
                logicModeLegendLabel.setVisible(false);
        }


        lc.getYAxis().setLabel(graphLabel);


        NumberAxis axis;
        axis=(NumberAxis) lc.getYAxis();
        axis.setAutoRanging(false);
        axis.setLowerBound(min);axis.setTickUnit(tickUnit);
        axis.setUpperBound(max);
    }

    /**
     * Checks if on click, there is an acceptable amount of distance from the click (For delete functionality)
     * @param x2
     * @param x1
     * @return
     */
    public static boolean equalizeYError(double x2, double x1){
        if(Math.abs(x2-x1) <= maskYError){
            return true;
        }
        return false;
    }
    /**
     * Checks if on click, there is an acceptable amount of distance from the click (For delete functionality)
     * @param x2
     * @param x1
     * @return
     */
    public static boolean equalizeXError(double x2, double x1){
        if(Math.abs(x2-x1) <= maskXError){
            return true;
        }
        return false;
    }
    //MASK TESTING GRAPH PREP\\

    /**
     * Sets up the various event listeners on the chart in Mask Testing mode
     * @param lineChart
     * @param cursorCoords
     */
    public static void maskingEventListener(LineChart<Number, Number> lineChart, Label cursorCoords){
        maskSeriesHigh = new XYChart.Series<>();
        maskSeriesLow = new XYChart.Series<>();
        maskSeriesHigh.setName("High mask");
        maskSeriesLow.setName("Low mask");
        lineChart.getData().add(maskSeriesHigh);
        lineChart.getData().add(maskSeriesLow);



        final Node chartBackground = lineChart.lookup(".chart-plot-background ");
        for (Node n: chartBackground.getParent().getChildrenUnmodifiable()) {
            if (n != chartBackground && n != lineChart.getXAxis() && n != lineChart.getYAxis()) {
                n.setMouseTransparent(true);
            }
        }


        chartBackground.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setVisible(true);
            }
        });

        chartBackground.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setText("(X,Y): "+
                        String.format(
                                "(%.2f, %.2f)",
                                lineChart.getXAxis().getValueForDisplay(mouseEvent.getX()),
                                lineChart.getYAxis().getValueForDisplay(mouseEvent.getY())
                        )
                );
            }
        });



        chartBackground.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setVisible(false);
            }
        });

        //----
        lineChart.getXAxis().setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setVisible(true);
            }
        });

        lineChart.getXAxis().setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setText("X :"+
                        String.format(
                                "x = %.2f",
                                lineChart.getXAxis().getValueForDisplay(mouseEvent.getX())
                        )
                );
            }
        });

        lineChart.getXAxis().setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setVisible(false);
            }
        });

        lineChart.getYAxis().setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setVisible(true);
            }
        });

        lineChart.getYAxis().setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setText(
                        String.format(
                                "y = %.2f",
                                lineChart.getYAxis().getValueForDisplay(mouseEvent.getY())
                        )
                );
            }
        });

        lineChart.getYAxis().setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setVisible(false);
            }
        });
        //----------------------START MASK LISTENERS--------------\\


        chartBackground.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                Number y = lineChart.getYAxis().getValueForDisplay(event.getY());
                Number x = lineChart.getXAxis().getValueForDisplay(event.getX());
                DecimalFormat df = new DecimalFormat("#.0");
                df.setRoundingMode(RoundingMode.CEILING);

                final Number xValue = Double.parseDouble(df.format(x));
                final Number yValue = Double.parseDouble(df.format(y));
                //for deletion
                if(event.isShiftDown()){
                    if(lowMaskRadioButton.isSelected()) {
                        for (XYChart.Data<Number, Number> xydata : maskSeriesLow.getData()) {
                            if (equalizeXError(Double.parseDouble(df.format(xydata.getXValue().doubleValue())),xValue.doubleValue() ) &&
                                    equalizeYError(Double.parseDouble(df.format(xydata.getYValue().doubleValue())) , yValue.doubleValue() )) {
                                maskSeriesLow.getData().remove(xydata);
                                maskSeriesLow.getData().sort(xSort);
                                //newHighLowCollisionDetection("removedlow");
                                //delete from relay pairs
                            }
                        }
                    }else if(highMaskRadioButton.isSelected()) {
                        for (XYChart.Data<Number, Number> xydata : maskSeriesHigh.getData()) {
                            if (equalizeXError(Double.parseDouble(df.format(xydata.getXValue().doubleValue())) , xValue.doubleValue() ) &&
                                    equalizeYError(Double.parseDouble(df.format(xydata.getYValue().doubleValue())) , yValue.doubleValue() )) {
                                maskSeriesHigh.getData().remove(xydata);
                                maskSeriesHigh.getData().sort(xSort);
                                //newHighLowCollisionDetection("removedhigh");
                                //delete from relay pairs
                            }
                        }
                    }

                    return;
                }
                //add to high Series

                if(highMaskRadioButton.isSelected()){
                    Platform.runLater(()->{
                        maskSeriesHigh.getData().add(new XYChart.Data<>(xValue, yValue ));
                        maskSeriesHigh.getData().sort(xSort);
                        newHighLowCollisionDetection("high");
                        //RelayCoordManager.getCoordsFromSeries(maskSeriesHigh, "high");

                        for(ParentRelayPair p:LinC.highRelayPairs){
                           // System.out.println("Parent1 X : "+ p.getParentCoord1().getXCoordinate() + " | Parent1 Y: "+ p.getParentCoord1().getYCoordinate());
                            //System.out.println("Parent2 X : "+ p.getParentCoord2().getXCoordinate() + " | Parent2 Y: "+ p.getParentCoord2().getYCoordinate());
                            for(RelayCoord r: p.getRelayCoordsListFromPair()){
                                //System.out.println("Relay coordinate: "+ r.getXCoord()+","+r.getYCoord());
                            }
                        }
                    });
                }
                //add to low series
                else if(lowMaskRadioButton.isSelected()){
                    Platform.runLater(()->{
                        maskSeriesLow.getData().add(new XYChart.Data<>(xValue, yValue ));
                        maskSeriesLow.getData().sort(xSort);
                        newHighLowCollisionDetection("low");
                        //RelayCoordManager.getCoordsFromSeries(maskSeriesLow, "low");
//                        for(ParentRelayPair p:LinC.lowRelayPairs){
//                            //System.out.println("Parent1 X : "+ p.getParentCoord1().getXCoordinate() + " | Parent1 Y: "+ p.getParentCoord1().getYCoordinate());
//                           // System.out.println("Parent2 X : "+ p.getParentCoord2().getXCoordinate() + " | Parent2 Y: "+ p.getParentCoord2().getYCoordinate());
//                            for(RelayCoord r: p.getRelayCoordsListFromPair()){
//                                //System.out.println("Relay coordinate: "+ r.getXCoord()+","+r.getYCoord());
//                            }
//                        }
                    });
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Choose either High or Low Mask", ButtonType.OK);
                    alert.show();
                }

                System.out.println("X : " + xValue + "Y: " + yValue);

            }
        });

    }

    public static double xSceneShift(Node node) {
        return node.getParent() == null ? 0 : node.getBoundsInParent().getMinX() + xSceneShift(node.getParent());
    }

    //ACTIVATE HOVER COORDINATES
    public static Label activateHoverCoordinates(LineChart<String,Number> lineChart, Label cursorCoords){


        final Node chartBackground = lineChart.lookup(".chart-plot-background");
        final double shiftX = xSceneShift(chartBackground);
        for (Node n: chartBackground.getParent().getChildrenUnmodifiable()) {
            if (n != chartBackground && n != lineChart.getXAxis() && n != lineChart.getYAxis()) {
                n.setMouseTransparent(true);
            }
        }
        chartBackground.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setVisible(true);
            }
        });

        chartBackground.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {

                //----

                double x = mouseEvent.getSceneX() - shiftX;

                String xValue = new String(lineChart.getXAxis().getValueForDisplay(x ));
                //-------

                cursorCoords.setText("X:"+xValue + " | Y:"+
                        String.format(
     //                           "(%.2f, %.2f)",
                                ("%.2f"),
                               // lineChart.getXAxis().getValueForDisplay(mouseEvent.getX()),
                                lineChart.getYAxis().getValueForDisplay(mouseEvent.getY())
                        )
                );
            }
        });



        chartBackground.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setVisible(false);
            }
        });

        //----
        lineChart.getXAxis().setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setVisible(true);
            }
        });

        lineChart.getYAxis().setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setVisible(true);
            }
        });

        lineChart.getYAxis().setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setText(
                        String.format(
                                "y = %.2f",
                                lineChart.getYAxis().getValueForDisplay(mouseEvent.getY())
                        )
                );
            }
        });

        lineChart.getYAxis().setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                cursorCoords.setVisible(false);
            }
        });
        return cursorCoords;
    }
//--------------------INIT LINE CHART WITHOUT HOVER LABEL | NOW ADDING HOVER LABEL | FINAL FUNCTION
public static void initLineChart(LineChart<String, Number> lc) {
        lc.setCacheShape(true);
    String measurementType = new String(Multimeter.getInstance().getCurrentMeasurementType());
        LinC.cachedSeries=new XYChart.Series<>();
        LinC.isInitialised = true;
        LinC.cachedSeries.getData().clear();
    LinC.cachedSeries.getData().addListener(new ListChangeListener<XYChart.Data<String, Number>>() {

        @Override
        public void onChanged(Change<? extends XYChart.Data<String, Number>> c) {
            while (c.next()) {
                if (c.wasAdded()) {

                    System.out.println("SERIES CHANGED");
                    LinC.scrollPane.setHvalue(LinC.scrollPane.getHmax());
                    connectedLineChart.setPrefWidth(lc.getPrefWidth() + 15);
                    LinC.scrollPane.setHvalue(LinC.scrollPane.getHmax());
LinC.scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);



                }
            }
        }

    });
    if(!(measurementType.equals("C")||measurementType.equals("V"))) {
        activateHoverCoordinates(LinC.connectedLineChart, LinC.graphHoverLabelConnected);
    }
    Platform.runLater(()->{
        connectedLineChart.getData().add(LinC.cachedSeries);
        connectedLineChart.getXAxis().autoRangingProperty().setValue(true);
        connectedLineChart.setAnimated(false);
        connectedLineChart.setLegendSide(Side.BOTTOM);
        connectedLineChart.setCursor(Cursor.CROSSHAIR);
        connectedLineChart.setPrefWidth(300);
        connectedLineChart.setTitle(measurementType + " v/s Time");
//        lc.getXAxis().setAutoRanging(false);
        connectedLineChart.getXAxis().tickLabelRotationProperty().setValue(65);
        connectedLineChart.getXAxis().setTickLabelFill(Paint.valueOf("white"));
        connectedLineChart.getXAxis().setTickLabelFont(Font.font("System", 12));
        connectedLineChart.getYAxis().setTickLabelFill(Paint.valueOf("white"));

    });

    setAxisBounds(connectedLineChart, measurementType);


}
    //--------------INIT LINE CHART WITH HOVER LABEL
    public static void initLineChart(LineChart<String, Number> lc, Label graphHoverLabel){
        String measurementType = new String(Multimeter.getInstance().getCurrentMeasurementType());

        cachedSeries.getData().addListener(new ListChangeListener<XYChart.Data<String, Number>>() {
            @Override
            public void onChanged(Change<? extends XYChart.Data<String, Number>> c) {
                while(c.next()) {
                    if (c.wasAdded()) {

                            System.out.println("SERIES CHANGED");
                            lc.setPrefWidth(lc.getPrefWidth() + 40);
                            LinC.scrollPane.setHvalue(LinC.scrollPane.getHmax());

                    }
                }
            }
        });


        lc.getData().clear();
        lc.getXAxis().autoRangingProperty().setValue(true);
        lc.setAnimated(false);
        lc.setLegendSide(Side.BOTTOM);
        lc.setCursor(Cursor.CROSSHAIR);
        lc.setPrefWidth(300);
        lc.setTitle(measurementType +" v/s Time");
        lc.getXAxis().tickLabelRotationProperty().setValue(65);
        lc.getXAxis().setTickLabelFill(Paint.valueOf("white"));
        lc.getXAxis().setTickLabelFont(Font.font("System",12));
        lc.getYAxis().setTickLabelFill(Paint.valueOf("white"));
        //lc.getYAxis().setSide(Side.RIGHT);

        setAxisBounds(lc, measurementType);

    graphHoverLabel = activateHoverCoordinates(lc, graphHoverLabel);


    }

    public static boolean checkOLDataLinC(Reading readFromSerial){
        if(readFromSerial.getReadingType().equals("VAC") || readFromSerial.getReadingType().equals("VDC")){
            if(Double.parseDouble(readFromSerial.getValue()) > 11.5 ||Double.parseDouble(readFromSerial.getValue()) < -11.5){
                //OL
               return true;

            }
        }
        if(readFromSerial.getReadingType().equals("AAC") || readFromSerial.getReadingType().equals("ADC") ){
            if(Double.parseDouble(readFromSerial.getValue()) > 195 || Double.parseDouble(readFromSerial.getValue()) < -195 ){
                //OL
                return true;
            }
        }
        return false;
    }
//////////////////////////PLOT CHART\\\\\\\\\\\\\\\\\\\\\\\
public static void plotLineChartDisconnected(LineChart<String,Number> lc, List<Reading> readingsList, Label graphHoverLabel){
    lc.getData().clear();
    lc.setCacheShape(true);
    lc.getXAxis().autoRangingProperty().setValue(true);
    lc.setAnimated(false);
    lc.setLegendSide(Side.BOTTOM);
    lc.setCursor(Cursor.CROSSHAIR);
    lc.setPrefWidth(300);
    XYChart.Series<String, Number> savedSeries = new XYChart.Series<>();
    lc.getData().clear();
    savedSeries.setName(readingsList.get(0).getReadingType());
    String measurementType = new String(readingsList.get(0).getReadingType());
    lc.setTitle(measurementType +" v/s Time");

    lc.getXAxis().tickLabelRotationProperty().setValue(65);
    lc.getXAxis().setTickLabelFill(Paint.valueOf("white"));
    lc.getXAxis().setTickLabelFont(Font.font("System",12));
    lc.getYAxis().setTickLabelFill(Paint.valueOf("white"));

    double maxValueForScale = 0;


    for(Reading reading : readingsList){

        maxValueForScale = (maxValueForScale < Math.abs(Double.parseDouble(reading.getValue())))? Double.parseDouble(reading.getValue()):maxValueForScale;
        LocalDateTime localDateTime = LocalDateTime.parse(reading.getTimeStamp());
        XYChart.Data<String,Number> d = new XYChart.Data<String, Number>(localDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME), Double.parseDouble(reading.getValue()));
        savedSeries.getData().add(d);

    }
    setAxisBounds(lc, measurementType, maxValueForScale);






    Platform.runLater(()->{
        lc.getData().add(savedSeries);
        lc.setPrefWidth(savedSeries.getData().size()*30 + lc.getPrefWidth());
        timeRangeDisconnected.setText(readingsList.get(0).getTimeStamp() + " / " + readingsList.get(readingsList.size()-1).getTimeStamp());



    });
    graphHoverLabel = activateHoverCoordinates(lc, graphHoverLabel);


}
// MASK GRAPH
    public static void plotLineChartMasked(LineChart<Number, Number> lc, List<Reading> readingsList, Label graphHoverLabelMasked,
                                           RadioButton highRadioButton, RadioButton lowRadioButton) {

        highRelayPairs = new ArrayList<ParentRelayPair>();
        lowRelayPairs = new ArrayList<ParentRelayPair>();
        dataRelayPairs = new ArrayList<ParentRelayPair>();

        highMaskRadioButton = highRadioButton;
        lowMaskRadioButton = lowRadioButton;
        lc.getData().clear();
        lc.getXAxis().setLabel("Second(s)");
        lc.getXAxis().autoRangingProperty().setValue(true);
        lc.setAnimated(false);
        lc.setLegendSide(Side.BOTTOM);
        lc.setCursor(Cursor.CROSSHAIR);
        lc.setPrefWidth(500);
        savedSeriesForMask = new XYChart.Series<>();
        lc.getData().clear();
        savedSeriesForMask.setName(readingsList.get(0).getReadingType());
        String measurementType = new String(readingsList.get(0).getReadingType());
        currentMaskUnit = new String(measurementType);

        lc.setTitle(measurementType +" v/s Time");
//        lc.getXAxis().setAutoRanging(false);
        lc.getXAxis().tickLabelRotationProperty().setValue(65);
        lc.getXAxis().setTickLabelFill(Paint.valueOf("white"));
        lc.getXAxis().setTickLabelFont(Font.font("System",12));
        lc.getYAxis().setTickLabelFill(Paint.valueOf("white"));

        double maxValueForScale = 0;
        LocalDateTime startLocalDateTime = LocalDateTime.parse(readingsList.get(0).getTimeStamp());
        for(Reading reading : readingsList){
            maxValueForScale = (maxValueForScale < Math.abs(Double.parseDouble(reading.getValue())))? Double.parseDouble(reading.getValue()):maxValueForScale;
            LocalDateTime localDateTime = LocalDateTime.parse(reading.getTimeStamp());
            Number secs = startLocalDateTime.until(localDateTime, ChronoUnit.SECONDS);
            savedSeriesForMask.getData().add(new XYChart.Data<>(secs, Double.parseDouble(reading.getValue())));
        }
        setAxisBounds(lc, measurementType, maxValueForScale,"mask");





        Platform.runLater(()->{
            lc.getData().add(savedSeriesForMask);
            lc.setPrefWidth(savedSeriesForMask.getData().size()*50 + lc.getPrefWidth());
            timeRangeMasked.setText(readingsList.get(0).getTimeStamp() + " / " + readingsList.get(readingsList.size()-1).getTimeStamp());



        });
        maskingEventListener(lc, graphHoverLabelMasked);


    }
public static double getRandomValue(String measurementType){
    double res = 0.0, ub = 0, lb = 0;
    switch(measurementType){
        case "VAC":
            ub = 1.0; lb = -1.0;
            break;
        case "VDC":
            ub = 1.0; lb = -1.0;
            break;
        case "AAC":
            ub = 10.0; lb = -10.0;
            break;
        case "ADC":
            ub = 10.0; lb = -10.0;
            break;
        case "R":
            ub = 1000; lb = 0;
            break;
    }
    res = Math.round(ThreadLocalRandom.current().nextDouble(lb,ub));

    return res;
}

}

