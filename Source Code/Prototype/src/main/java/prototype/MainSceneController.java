package prototype;

import com.sun.org.apache.xpath.internal.operations.Mult;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import prototype.Models.*;
import prototype.utils.ReadingsLoader;
import prototype.utils.RelayCoordManager;
import prototype.utils.SerialComm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static prototype.Models.LinC.*;
import static prototype.utils.RelayCoordManager.*;

/**
 * This class is the Controller class in the MVC design pattern
 * It maps the UI elements to their interaction functions
 */
public class MainSceneController implements Initializable{
    @FXML MenuButton modeMenuButton;
    @FXML TabPane tabPane;
    @FXML Tab connectedTab;
    @FXML Tab disconnectedTab;
    @FXML MenuItem connectedMenuItem;
    @FXML MenuItem disconnectedMenuItem;
    @FXML Label graphValueLabel;
    ////////////////////////////////////////////////DISCONNECTED STATES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    @FXML LineChart<String,Number> disconnectedLineChart;
    @FXML Button dummySaveButton;
    @FXML NumberAxis yAxisDisconnectedLineChart;
    @FXML Label graphHoverLabelDisconnected;
    @FXML Label timeRangeDisconnected;
    //////////////////////////////////////////////MASK TESTING\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    @FXML LineChart<Number,Number> maskLineChart;
    @FXML Label graphHoverLabelMasked;
    @FXML Label timeRangeMasked;
    ToggleGroup maskRadioGroup;
    @FXML RadioButton highMaskRadioButton;
    @FXML RadioButton lowMaskRadioButton;
    @FXML Button loadMaskFileButton;
    @FXML TableView maskFailureTable;
    public static ObservableList<MaskFailure> maskFailuresList =  FXCollections.observableArrayList();
    ///////////////////////////////////////////////////CONNECTED STATES\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    @FXML Label topScreenLabel;
    @FXML Label bottomScreenLabel;
    @FXML Label serialTestLabel;
    @FXML Button startLiveButton;
    String measurementFromRemote = null;
    @FXML Label timeRangeConnected;
    @FXML Button pauseButton;
    @FXML Button resumeButton;

    @FXML Button discardButton;
    @FXML LineChart<String,Number> connectedLineChart;
    @FXML NumberAxis yAxisConnectedLineChart;
    @FXML Label multimeterScreenLabel;
    @FXML MenuButton measureTypeMenuButton;
    @FXML Label remoteScreenUnitLabel;
    @FXML public ScrollPane connectedScrollPane;
    @FXML MenuButton measureSampleRate;
    Tooltip modeToolTip;
    @FXML Label graphHoverLabelConnected;
    @FXML Label logicModeLegendLabel;
    @FXML Label overLimitLabelLive;

    //////////////////////////////////////////////////////METHODS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /**
     * Method initializes the requisite classes and states
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //SerialComm.parseReadingFromSerial("DATA:5,273,1");
        modeToolTip = new Tooltip();
        modeToolTip.setText("Please choose mode");
        modeToolTip.setStyle("-fx-font: normal bold 20 Langdon; "
                + "-fx-base: #AE3522; "
                + "-fx-text-fill: lightgray;");
        modeMenuButton.setTooltip(modeToolTip);
        SerialComm.getInstance();
        LinC.setConnectedLineChart(connectedLineChart);
        Multimeter.getInstance().initMultimeter();
        disconnectedTab.disableProperty().setValue(true);
        connectedTab.disableProperty().setValue(true);
        tabPane.setVisible(false);
        connectedScrollPane.setOnDragDetected(event -> connectedScrollPane.pannableProperty().setValue(true));
//        discardButton.disableProperty().setValue(true);
        resumeButton.setDisable(true);
        LinC.scrollPane = connectedScrollPane;
        SerialComm.hasValidSerialInput = false;
        LinC.graphHoverLabelConnected = graphHoverLabelConnected;
        LinC.timeRangeConnected = timeRangeConnected;
        LinC.timeRangeDisconnected = timeRangeDisconnected;
        LinC.timeRangeMasked = timeRangeMasked;
        Multimeter.topScreenLabel = topScreenLabel;
        Multimeter.bottomScreenLabel = bottomScreenLabel;
        maskRadioGroup = new ToggleGroup();
        highMaskRadioButton.setToggleGroup(maskRadioGroup);
        lowMaskRadioButton.setToggleGroup(maskRadioGroup);


        loadMaskFileButton.setDisable(true);
        LinC.logicModeLegendLabel = logicModeLegendLabel;
        LinC.logicModeLegendLabel.setVisible(false);
        overLimitLabelLive.setVisible(false);
        SerialComm.overLimitLabelConnected = overLimitLabelLive;

    }






    //////////////////////////////////////////////CONNECTED MODE FUNCTIONS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\


    @FXML
    public void writeToSerialB1(){
        SerialComm.getInstance().writeToSerialButtons("CMD:b1");
    }
    @FXML
    public void writeToSerialB2(){
        SerialComm.getInstance().writeToSerialButtons("CMD:b2");
    }
    @FXML
    public void writeToSerialB3(){
        SerialComm.getInstance().writeToSerialButtons("CMD:b3");
    }
    @FXML
    public void writeToSerialB4(){
        SerialComm.getInstance().writeToSerialButtons("CMD:b4");
    }

    public void setUnitFromMeasurementType(){
        String screenUnit;
        switch(Multimeter.getInstance().getCurrentMeasurementType()){
            case "VAC":
                    screenUnit = new String("V");
                    break;
            case "VDC":
                screenUnit = new String("V");
                break;
            case "AAC":
                screenUnit = new String("mA");
                break;
            case "ADC":
                screenUnit = new String("mA");
                break;
            case "R":
                screenUnit = new String("\u2126");
                break;
            default:
                screenUnit= new String("");
        }
        remoteScreenUnitLabel.setText(screenUnit);
    }

    /**
     * Resets serial and restarts it
     */
    @FXML void resetSerial(){
        if(SerialComm.comPort.isOpen()) {
            SerialComm.comPort.closePort();
        }
            startLiveButton.setDisable(false);
            startLiveMode();

    }
    /**
     * Starts Serial data transfer between PC Software and Multimeter via USB
     */
    @FXML void startLiveMode(){

        SerialComm.getInstance().initSerial();

        if(SerialComm.hasValidSerialInput == false){
            connectedLineChart.disableProperty().setValue(true);
            Alert notConnected = new Alert(Alert.AlertType.ERROR, "Serial communication not available", ButtonType.OK);
            notConnected.show();
            onDisconnectedMode();
            return;
        }
        new Thread(()->{
            while(true) {
                try {
                    Thread.sleep(200);
                    Reading readFromSerial = Multimeter.serialTestReading;

                    Platform.runLater(()->{
                        if(readFromSerial == null) System.out.println("\nNULL\n");
                        if(readFromSerial!=null)
                            serialTestLabel.setText("Reading created: Value: " + readFromSerial.getValue() + "\n" + "Type: " + readFromSerial.getReadingType() +
                                    "\n" + readFromSerial.getTimeStamp());
                        multimeterScreenLabel.setText(Multimeter.serialTestReading.getValue().toString());


                        setUnitFromMeasurementType();
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        startLiveButton.setDisable(true);
        //graphItLive();
    }

    /**
     * Method is called when connected mode is chosen from the Mode dropdown
     */
    @FXML
    public void onConnectedMode(){
        tabPane.setVisible(true);


        modeMenuButton.setText("Mode: Connected");


        System.out.println(tabPane.getTabs().get(0).getText());
        tabPane.getSelectionModel().select(0);
        disconnectedTab.disableProperty().setValue(true);
        connectedTab.disableProperty().setValue(false);


    }


    /**
     * Called when plot is paused
     * This method extracts all the paused data as soon as the pause button is clicked, but continues receiving serial
     * data in background
     */
    @FXML
    public void onPlotPaused(){

        LinC.isGraphPaused = true;
        LinC.resumeCounter = Multimeter.getInstance().currentList.size();

        LinC.unitBeforePausing = new StringBuilder(Multimeter.getInstance().getCurrentMeasurementType());

        Multimeter.getInstance().pausedList.clear();
        for(Reading r:Multimeter.getInstance().currentList){
            Multimeter.getInstance().pausedList.add(r);
        }

        Platform.runLater(()->{
            pauseButton.setDisable(true);
            resumeButton.setDisable(false);


        });

    }

    /**
     * Called when the Resume button is clicked
     * Resumes plotting graph after plotting whichever values were received in the background while paused
     */
    @FXML
    public void onResumed(){
        if(LinC.unitBeforePausing.toString().equals(Multimeter.getInstance().getCurrentMeasurementType())){
            for(int i = LinC.resumeCounter; i < Multimeter.getInstance().currentList.size(); i++){
                LocalDateTime localDateTime = LocalDateTime.parse(Multimeter.getInstance().currentList.get(i).getTimeStamp());

                cachedSeries.getData().add(new XYChart.Data<>(  localDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME), Double.parseDouble(Multimeter.getInstance().currentList.get(i).getValue())   ));
            }

        }else{
            LinC.unitBeforePausing = new StringBuilder(Multimeter.getInstance().getCurrentMeasurementType());
            initLineChart(connectedLineChart, graphHoverLabelConnected);

            //Reset graph and start plotting
        }

        //default behavior
        LinC.isGraphPaused = false;
        Platform.runLater(()->{

            pauseButton.setDisable(false);
            resumeButton.setDisable(true);

        });



    }

    /**
     * Method called when Discard button is clicked
     * Removes all currently cached data and clears the graph
     * Throws warning prompt to user before discarding data
     */
    @FXML
    public void onClickedDiscardButton(){


        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure ?");
        confirm.initModality(Modality.APPLICATION_MODAL);
        confirm.setHeaderText("Discard");
        confirm.showAndWait();

        if(confirm.getResult() == ButtonType.OK) {
           LinC.cachedSeries.getData().clear();
           Multimeter.getInstance().currentList.clear();
           connectedLineChart.setPrefWidth(300);
            //Multimeter.getInstance().initMultimeter();
            //graphItLive();

            if(LinC.isGraphPaused){
                onResumed();
            }


        }else if(confirm.getResult() == ButtonType.CANCEL){
            return;
        }
    }

    /**
     * Method is called when Save button is clicked
     * Retrieves all data saved in the list when it was paused
     * Saves it to a .csv file
     * @throws IOException
     */
    @FXML
    public void onSaved() throws IOException {
        if(!LinC.isGraphPaused){
            Alert notPausedAlert = new Alert(Alert.AlertType.ERROR,"Please pause before saving", ButtonType.OK );
            notPausedAlert.initModality(Modality.APPLICATION_MODAL);
            notPausedAlert.show();
        }else {


            FileChooser fileChooser = new FileChooser();

            // Set extension filter
            FileChooser.ExtensionFilter extFilter =
                    new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
            fileChooser.getExtensionFilters().add(extFilter);

            // Show save file dialog
            File saveFile = fileChooser.showSaveDialog(graphHoverLabelConnected.getScene().getWindow());

            if (saveFile == null) {
                System.out.println("File not chosen");
                return;
            }

            FileWriter writer = new FileWriter(saveFile);
            new Thread(() -> {
                try {

                    for (int j = 0; j < Multimeter.getInstance().pausedList.size(); j++) {

                        writer.append(Multimeter.getInstance().pausedList.get(j).getReadingType());
                        writer.append(",");
                        if(LinC.checkOLDataLinC(Multimeter.getInstance().pausedList.get(j))){
                            writer.append("OL");
                        }else{
                            writer.append(Multimeter.getInstance().pausedList.get(j).getValue());
                        }

                        writer.append(",");
                        writer.append(Multimeter.getInstance().pausedList.get(j).getTimeStamp());


                        writer.append("\n");
                    }
                    writer.close();
                    System.out.println("CSV File was generated at " + saveFile.getAbsolutePath());

                    Platform.runLater(() -> {
                        Alert savedAlert = new Alert(Alert.AlertType.INFORMATION, "File saved at " + saveFile.getAbsolutePath());
                        savedAlert.initModality(Modality.NONE);
                        savedAlert.setTitle("Confirmation");
                        savedAlert.setHeaderText("File saved successfully");
                        savedAlert.show();
                    });

                } catch (Exception e) {

                    e.printStackTrace();
                }


            }).start();
        }





    }
    ///////////////////////////////////////////DISCONNECTED MODE FUNCTIONS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /**
     * Invoked when disconnected mode is chosen from the mode dropdown
     */
    @FXML
    public void onDisconnectedMode(){
        tabPane.setVisible(true);

        modeMenuButton.setText("Mode: Disconnected");
        System.out.println(tabPane.getTabs().get(1).getText());
        tabPane.getSelectionModel().select(1);
        connectedTab.disableProperty().setValue(true);
        disconnectedTab.disableProperty().setValue(false);


    }

    /**
     * Loads readings with file chooser and uses the ReadingsLoader class to save loaded readings
     * in the loadedReadings ArrayList in the multimeter instance
     * @throws IOException
     */
    @FXML
    public void loadReadings() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open Readings");

        File rfile;

        rfile = fileChooser.showOpenDialog(graphHoverLabelDisconnected.getScene().getWindow());
        if(rfile == null){
            System.out.println("File not chosen");
            return;
        }
        ReadingsLoader readingsLoader = new ReadingsLoader(rfile.getAbsolutePath());
        Multimeter.getInstance().loadedReadings = readingsLoader.getLoadedReadings();
        Multimeter.getInstance().setCurrentMeasurementType(Multimeter.getInstance().loadedReadings.get(0).getReadingType());


        //LinC.initLineChart(disconnectedLineChart, graphHoverLabelDisconnected);
        LinC.plotLineChartDisconnected(disconnectedLineChart, Multimeter.getInstance().loadedReadings, graphHoverLabelDisconnected);



        new Thread(()->{
            for(Reading r:Multimeter.getInstance().loadedReadings){
                System.out.println("Reading "+ r.getTimeStamp()+" | "+ r.getReadingType()+" | "+ r.getValue());
            }
        }).run();
    }

//////////////////////////MASK TESTING MODE\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

    /**
     * Called when the Test Mask button is clicked
     *
     */
    @FXML
    public void testMask(){


        RelayCoordManager.dataMaskCollisionDetection();

        Number lowY = LinC.maskSeriesLow.getData().get(0).getYValue();
        Number lowX = LinC.maskSeriesLow.getData().get(0).getXValue();

        Number highY = LinC.maskSeriesHigh.getData().get(0).getYValue();
        Number highX = LinC.maskSeriesHigh.getData().get(0).getXValue();

        Point2D lowLinePointL1 = new Point2D(0, lowY.floatValue());
        Point2D lowLinePointL2 = new Point2D(lowX.floatValue(), lowY.floatValue());
        Point2D highLinePointL1 = new Point2D(0, highY.floatValue());
        Point2D highLinePointL2 = new Point2D(highX.floatValue(), highY.floatValue());

        XYChart.Series<Number,Number> highLeftLineSeries = new XYChart.Series<>();
        XYChart.Series<Number,Number> lowLeftLineSeries = new XYChart.Series<>();

        lowLeftLineSeries.getData().add(new XYChart.Data<>(lowLinePointL1.getX(), lowLinePointL1.getY()));
        lowLeftLineSeries.getData().add(new XYChart.Data<>(lowLinePointL2.getX(), lowLinePointL2.getY()));

        highLeftLineSeries.getData().add(new XYChart.Data<>(highLinePointL1.getX(), highLinePointL1.getY()));
        highLeftLineSeries.getData().add(new XYChart.Data<>(highLinePointL2.getX(), highLinePointL2.getY()));

        maskLineChart.getData().addAll(lowLeftLineSeries, highLeftLineSeries);
        System.out.println("FAILURE FAILS");
        Comparator<com.sun.javafx.geom.Point2D> xComparator = (new Comparator<com.sun.javafx.geom.Point2D>() {
            @Override
            public int compare(com.sun.javafx.geom.Point2D o1, com.sun.javafx.geom.Point2D o2) {
                if(o1.x < o2.x) return -5;
                if(o1.x > o2.x) return 5;
                if(o1.x == o2.x) return 0;
                return 5;
            }
        });

        TableColumn failNoCol = new TableColumn("Failure #");
        failNoCol.setCellValueFactory(new PropertyValueFactory<MaskFailure, String>("failNo"));

        TableColumn failStartCol = new TableColumn("Start");
        failStartCol.setCellValueFactory(new PropertyValueFactory<MaskFailure,String>("failStart"));
        TableColumn failEndCol = new TableColumn("End");
        failEndCol.setCellValueFactory(new PropertyValueFactory<MaskFailure, String>("failEnd"));




       // intersectionPointsListFinalHigh.sort(xComparator);

//        for(int i = 1; i < (intersectionPointsListFinalHigh.size()%2 == 0 ? intersectionPointsListFinalHigh.size()-1:intersectionPointsListFinalHigh.size()-2); i++){
//            if(((i+1)%2 == 0) && intersectionPointsListFinalHigh.get(i).equals(intersectionPointsListFinalHigh.get(i+1))){
//               intersectionPointsListFinalHigh.remove(i);
//               intersectionPointsListFinalHigh.remove(i);
//
//            }
//        }
        int c = 0;
       for(int i = 0; i < intersectionPointsListFinalHigh.size()-1; i+=2){
           maskFailuresList.add(new MaskFailure(String.valueOf(++c),String.valueOf(intersectionPointsListFinalHigh.get(i).x),
                                                                String.valueOf(intersectionPointsListFinalHigh.get(i+1).x)));
       }

  //      intersectionPointsListFinalLow.sort(xComparator);
//        for(int i = 1; i < (intersectionPointsListFinalLow.size()%2 ==0? intersectionPointsListFinalLow.size() - 1 : intersectionPointsListFinalLow.size() - 2) ; i++){
//            if(((i+1)%2 == 0) && intersectionPointsListFinalLow.get(i).equals(intersectionPointsListFinalLow.get(i+1))){
//                intersectionPointsListFinalLow.remove(i);
//                intersectionPointsListFinalLow.remove(i);
//
//            }
//        }
        c = 0;
        for(int i = 0; i < intersectionPointsListFinalLow.size()-1; i+=2){
            maskFailuresList.add(new MaskFailure(String.valueOf(++c),String.valueOf(intersectionPointsListFinalLow.get(i).x),
                    String.valueOf(intersectionPointsListFinalLow.get(i+1).x)));
        }

        maskFailureTable.setItems(maskFailuresList);
        maskFailureTable.getColumns().addAll(failNoCol, failStartCol, failEndCol);
        //checkIfDataAboveOrBelow();
        //calculateFailureIntervals();

    }

    /**
     * Called when high mask radio button is chosen
     */
    @FXML
    public void onClickHighMaskRadio(){
        lowMaskRadioButton.disarm();
    }
    /**
     * Called when high mask radio button is chosen
     */
    @FXML
    public void onClickLowMaskRadio(){
        highMaskRadioButton.disarm();
    }
    /**
     * Called when mask file is saved to disk
     */
    @FXML public void saveMaskToDisk() throws IOException {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File saveFile = fileChooser.showSaveDialog(graphHoverLabelConnected.getScene().getWindow());

        if (saveFile == null) {
            System.out.println("File not chosen");
            return;
        }

        FileWriter writer = new FileWriter(saveFile);
        new Thread(() -> {
            try {

                for (int j = 0; j < LinC.maskSeriesHigh.getData().size(); j++) {
                    writer.append("high");
                    writer.append(",");
                    writer.append(LinC.maskSeriesHigh.getData().get(j).getXValue().toString());
                    writer.append(",");
                    writer.append(LinC.maskSeriesHigh.getData().get(j).getYValue().toString());
                    writer.append(",");
                    writer.append(LinC.currentMaskUnit);

                    writer.append("\n");
                }
                for (int j = 0; j < LinC.maskSeriesLow.getData().size(); j++) {
                    writer.append("low");
                    writer.append(",");
                    writer.append(LinC.maskSeriesLow.getData().get(j).getXValue().toString());
                    writer.append(",");
                    writer.append(LinC.maskSeriesLow.getData().get(j).getYValue().toString());
                    writer.append(",");
                    writer.append(LinC.currentMaskUnit);

                    writer.append("\n");
                }
                writer.close();
                System.out.println("CSV File was generated at " + saveFile.getAbsolutePath());
                Platform.runLater(() -> {
                    Alert savedAlert = new Alert(Alert.AlertType.INFORMATION, "File saved at " + saveFile.getAbsolutePath());
                    savedAlert.initModality(Modality.NONE);
                    savedAlert.setTitle("Confirmation");
                    savedAlert.setHeaderText("File saved successfully");
                    savedAlert.show();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /////////////////////////MASK GRAPH DATA LOAD\\\\\\\\\\\\\\\\\\\\\\\\

    /**
     * Called when Load Mask File button is clicked
     * Loads the mask file and superimposes it on the currently loaded data graph
     * Throws errors if mask file is invalid/inconsistent
     * @throws IOException
     */
    @FXML
    public void loadMaskFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open Readings");

        File rfile;

        rfile = fileChooser.showOpenDialog(graphHoverLabelMasked.getScene().getWindow());
        if(rfile == null){
            System.out.println("File not chosen");
            return;
        }

        ReadingsLoader maskReadingsLoader = new ReadingsLoader(rfile.getAbsolutePath(), "mask");
        LinC.maskFileReadings = maskReadingsLoader.getMaskFile();
        List<MaskReading> highMaskList = new ArrayList<>();
        List<MaskReading> lowMaskList = new ArrayList<>();
        Comparator<MaskReading> xSort = new Comparator<MaskReading>() {
            @Override
            public int compare(MaskReading o1, MaskReading o2) {
                if(o1.getxTimeValue().floatValue() < o2.getxTimeValue().floatValue()){
                    return -5;
                }else if(o1.getxTimeValue().floatValue() > o2.getxTimeValue().floatValue()){
                    return 5;
                }else{
                    return 0;
                }
            }
        };

        String maskUnit = maskFileReadings.get(0).getMaskReadingType();
        if(!LinC.currentMaskUnit.equals(maskUnit)){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Mask file is incompatible with loaded data", ButtonType.OK);
            alert.show();
            return;
        }
        for(int i=0;i<maskFileReadings.size(); i++){
            if(maskFileReadings.get(i).getMaskReadingType().equals(maskUnit)) {
                if (maskFileReadings.get(i).getMaskMode().equals("high")) {
                    highMaskList.add(maskFileReadings.get(i));

                } else if (maskFileReadings.get(i).getMaskMode().equals("low")) {
                    lowMaskList.add(maskFileReadings.get(i));

                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR, "Inconsistent Mask File!", ButtonType.OK);
                alert.setHeaderText("Load data again");
                alert.show();
                return;
            }
        }
        highMaskList.sort(xSort);
        lowMaskList.sort(xSort);
//        maskSeriesLow = new XYChart.Series<>();
//        maskSeriesHigh = new XYChart.Series<>();
//        maskLineChart.getData().add(maskSeriesHigh);
//        maskLineChart.getData().add(maskSeriesLow);

        for(int i=0;i<highMaskList.size();i++){
            maskSeriesHigh.getData().add(new XYChart.Data<>(highMaskList.get(i).getxTimeValue().floatValue(), highMaskList.get(i).getyValue().floatValue() ));
            newHighLowCollisionDetection("high");
        }

        for(int i=0;i<lowMaskList.size();i++){
            maskSeriesLow.getData().add(new XYChart.Data<>(lowMaskList.get(i).getxTimeValue().floatValue(), lowMaskList.get(i).getyValue().floatValue() ));
            newHighLowCollisionDetection("low");
        }

    }

    /**
     * Called when Load Data is clicked in Mask Testing Mode
     * Loads the data and plots it on the graph in Mask testing mode
     * @throws IOException
     */
    @FXML
    public void loadMaskReadings() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open Readings");

        File rfile;

        rfile = fileChooser.showOpenDialog(graphHoverLabelDisconnected.getScene().getWindow());
        if(rfile == null){
            System.out.println("File not chosen");
            return;
        }
        ReadingsLoader readingsLoader = new ReadingsLoader(rfile.getAbsolutePath());
        Multimeter.getInstance().loadedMaskReadings = readingsLoader.getLoadedReadings();
        Multimeter.getInstance().setCurrentMeasurementType(Multimeter.getInstance().loadedMaskReadings.get(0).getReadingType());


        //LinC.initLineChart(disconnectedLineChart, graphHoverLabelDisconnected);
        LinC.plotLineChartMasked(maskLineChart, Multimeter.getInstance().loadedMaskReadings, graphHoverLabelMasked,
                                highMaskRadioButton, lowMaskRadioButton);



        new Thread(()->{
            for(Reading r:Multimeter.getInstance().loadedMaskReadings){
                System.out.println("Reading "+ r.getTimeStamp()+" | "+ r.getReadingType()+" | "+ r.getValue());
            }
        }).run();
        loadMaskFileButton.setDisable(false);
    }

    ///////////////////////////////////////////UTILITY FUNCTIONS\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\




    /**
     * Sets new scene in the current window from reference node.
     * @param referenceNode
     * @param nextSceneFileFxml
     */
    public void setSceneFromReference(Node referenceNode, String nextSceneFileFxml){

        Stage currentWindow;

        currentWindow = (Stage)referenceNode.getScene().getWindow();
        try {
            currentWindow.setScene(new Scene(FXMLLoader.load(getClass().getResource(nextSceneFileFxml))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
