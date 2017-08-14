package prototype.utils;

import com.sun.javafx.geom.Line2D;
import com.sun.javafx.geom.Point2D;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import prototype.Models.LinC;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import static prototype.Models.LinC.*;

/**
 * Class has various functions for handling and managing collisions for mask testing
 */
public class RelayCoordManager {
    //public static Set<RelayCoord> coordSet = new HashSet<>();
    public static List<RelayCoord> coordSet = new ArrayList<>();
    public static List<Point2D> parentHighListFinal = new ArrayList<>();
    public static List<Point2D> parentLowListFinal = new ArrayList<>();
    public static List<Point2D> dataListFinal = new ArrayList<>();
    public static List<Point2D> intersectionPointsListFinalHigh = new ArrayList<>();
    public static List<Point2D> intersectionPointsListFinalLow = new ArrayList<>();
    public RelayCoordManager(){

    }



    public static Number calculateSlope(ParentCoord pc1, ParentCoord pc2){
        double x2,x1,y2,y1;

        x1 = pc1.getXCoordinate().doubleValue();
        x2 = pc2.getXCoordinate().doubleValue();
        y1 = pc1.getYCoordinate().doubleValue();
        y2 = pc2.getYCoordinate().doubleValue();
        Number slope = (y2-y1)/(x2-x1);
        return slope;
    }
    public static double twoDecimal(Number x){
        DecimalFormat df = new DecimalFormat("#.0");
        df.setRoundingMode(RoundingMode.CEILING);

        double xVal = Double.parseDouble(df.format(x));
        //System.out.println("Two Decimal:=> Before: "+ x + " | After: "+ xVal);
        return xVal;
    }



    public static void calculateIntersectionPoint(Point2D highxy1, Point2D highxy2, Point2D dataxy1, Point2D dataxy2){
        //y-y1 = m(x-x1)
        // m = y2-y1/x2-x1

    }


    public static void dataMaskCollisionDetection(){
        System.out.println("Reached");
        List<Point2D> parentHighList = new ArrayList<>();
        List<Point2D> parentLowList = new ArrayList<>();
        List<Point2D> dataList = new ArrayList<>();

        if(LinC.maskSeriesLow.getData().size() > 1 && LinC.maskSeriesHigh.getData().size() > 1) {
            for (int i = 0; i < LinC.maskSeriesLow.getData().size(); i++) {
                Point2D lowPoint = new Point2D(LinC.maskSeriesLow.getData().get(i).getXValue().floatValue(), LinC.maskSeriesLow.getData().get(i).getYValue().floatValue());
                parentLowList.add(lowPoint);
            }
            for (int i = 0; i < LinC.maskSeriesHigh.getData().size(); i++) {
                Point2D highPoint = new Point2D(LinC.maskSeriesHigh.getData().get(i).getXValue().floatValue(), LinC.maskSeriesHigh.getData().get(i).getYValue().floatValue());
                parentHighList.add(highPoint);
            }
            for (int i = 0; i < LinC.savedSeriesForMask.getData().size(); i++) {
                Point2D dataPoint = new Point2D(LinC.savedSeriesForMask.getData().get(i).getXValue().floatValue(), LinC.savedSeriesForMask.getData().get(i).getYValue().floatValue());
                dataList.add(dataPoint);
                dataListFinal.add(dataPoint);
            }

            for(int i = 0; i < parentHighList.size()-1; i++) {
                Point2D highxy1 = parentHighList.get(i);
                Point2D highxy2 = parentHighList.get(i + 1);

                for (int j = 0; j < dataList.size() - 1; j++) {
                    Point2D dataxy1 = dataList.get(j);
                    Point2D dataxy2 = dataList.get(j + 1);

                    Line2D highLine = new Line2D(highxy1, highxy2);
                    Line2D dataLine = new Line2D(dataxy1, dataxy2);

                    if(highLine.intersectsLine(dataLine)){

                            intersectionPointsListFinalHigh.add(dataxy1);
                            intersectionPointsListFinalHigh.add(dataxy2);


                        System.out.println("Intersection detected with high :");
                        System.out.println("High Line : "+ "(" +highxy1.x + "," + highxy1.y+")"+ "(" +highxy2.x + "," + highxy2.y+")");
                        System.out.println("Data Line : "+ "(" +dataxy1.x + "," + dataxy1.y+")"+ "(" +dataxy2.x + "," + dataxy2.y+")");
                    }
                }
            }




            for(int i = 0; i < parentLowList.size()-1; i++) {
                Point2D lowxy1 = parentLowList.get(i);
                Point2D lowxy2 = parentLowList.get(i + 1);

                for (int j = 0; j < dataList.size() - 1; j++) {
                    Point2D dataxy1 = dataList.get(j);
                    Point2D dataxy2 = dataList.get(j + 1);

                    Line2D lowLine = new Line2D(lowxy1, lowxy2);
                    Line2D dataLine = new Line2D(dataxy1, dataxy2);

                    if(lowLine.intersectsLine(dataLine)){

                            intersectionPointsListFinalLow.add(dataxy1);
                            intersectionPointsListFinalLow.add(dataxy2);

                        System.out.println("Intersection detected with low :");
                        System.out.println("High Line : "+ "(" +lowxy1.x + "," + lowxy1.y+")"+ "(" +lowxy2.x + "," + lowxy2.y+")");
                        System.out.println("Data Line : "+ "(" +dataxy1.x + "," + dataxy1.y+")"+ "(" +dataxy2.x + "," + dataxy2.y+")");
                    }
                }
            }
        }

    }
    public static void checkIfDataAboveOrBelow(){
        if(LinC.maskSeriesLow.getData().size() >= 1 && LinC.maskSeriesHigh.getData().size() >= 1) {
            //Check if point lies above or below the line
            for (int j = 0; j < parentHighListFinal.size();j++) {
                Point2D highPoint = parentHighListFinal.get(j);
                if(highPoint.x < LinC.savedSeriesForMask.getData().get(0).getXValue().floatValue() ||
                        highPoint.x > LinC.savedSeriesForMask.getData().get(LinC.savedSeriesForMask.getData().size()-1).getXValue().floatValue()) continue;
                Point2D dataLeftPoint = null;
                Point2D dataRightPoint = null;
                for (int i = 0; i < savedSeriesForMask.getData().size() - 1; i++) {
                    float x1 = savedSeriesForMask.getData().get(i).getXValue().floatValue();
                    float x2 = savedSeriesForMask.getData().get(i + 1).getXValue().floatValue();
                    if (highPoint.x >= x1 && highPoint.x <= x2) {
                        dataLeftPoint = new Point2D(x1, savedSeriesForMask.getData().get(i).getYValue().floatValue());
                        dataRightPoint = new Point2D(x2, LinC.savedSeriesForMask.getData().get(i + 1).getYValue().floatValue());
                    }
                }

                float d = (highPoint.x - dataLeftPoint.x) * (dataRightPoint.y - dataLeftPoint.y) - (highPoint.y - dataLeftPoint.y) * (dataRightPoint.x - dataLeftPoint.x);
                //Data point lies above high
                if (d > 0) {
                    if((j-1)%2 == 0){
                        System.out.println("Failed high mask from "+dataLeftPoint.x + " to "+dataRightPoint.x);
                    }else{
                        System.out.println("Failed high mask from "+dataLeftPoint.x + " to "+dataRightPoint.x);
                    }

                    //LinC.maskSeriesHigh.getData().remove(j);

                    //found = true;
//                    Alert alert = new Alert(Alert.AlertType.ERROR, "High mask cannot be lower than Low Mask", ButtonType.OK);
//                    alert.show();

                }
                System.out.println("D : " + d);
            }

            //check data with low-------------
            for (int j = 0; j < parentLowListFinal.size();j++) {
                Point2D lowPoint = parentLowListFinal.get(j);
                if(lowPoint.x < LinC.savedSeriesForMask.getData().get(0).getXValue().floatValue() ||
                        lowPoint.x > LinC.savedSeriesForMask.getData().get(LinC.savedSeriesForMask.getData().size()-1).getXValue().floatValue()) continue;
                Point2D dataLeftPoint = null;
                Point2D dataRightPoint = null;
                for (int i = 0; i < savedSeriesForMask.getData().size() - 1; i++) {
                    float x1 = savedSeriesForMask.getData().get(i).getXValue().floatValue();
                    float x2 = savedSeriesForMask.getData().get(i + 1).getXValue().floatValue();
                    if (lowPoint.x >= x1 && lowPoint.x <= x2) {
                        dataLeftPoint = new Point2D(x1, savedSeriesForMask.getData().get(i).getYValue().floatValue());
                        dataRightPoint = new Point2D(x2, LinC.savedSeriesForMask.getData().get(i + 1).getYValue().floatValue());
                    }
                }

                float d = (lowPoint.x - dataLeftPoint.x) * (dataRightPoint.y - dataLeftPoint.y) - (lowPoint.y - dataLeftPoint.y) * (dataRightPoint.x - dataLeftPoint.x);
                //Data point lies above high
                if (d < 0) {
                    if((j-1)%2 == 0){
                        System.out.println("Failed Low mask from "+dataLeftPoint.x + " to "+dataRightPoint.x);
                    }else{
                        System.out.println("Failed Low mask from "+dataLeftPoint.x + " to "+dataRightPoint.x);
                    }


                }
                System.out.println("D : " + d);
            }



        }
    }
    public static boolean checkIfAboveOrBelow(String seriesType, List<Point2D> parentHighList, List<Point2D> parentLowList){
        boolean found = false;
        if(LinC.maskSeriesLow.getData().size() >= 1 && LinC.maskSeriesHigh.getData().size() >= 1) {
            if(seriesType.equals("high")){
                //Check if point lies above or below the line
                for (int j = 0; j < parentHighList.size();j++) {
                    Point2D highPoint = parentHighList.get(j);
                    if(highPoint.x < LinC.maskSeriesLow.getData().get(0).getXValue().floatValue() ||
                            highPoint.x > LinC.maskSeriesLow.getData().get(LinC.maskSeriesLow.getData().size()-1).getXValue().floatValue()) continue;
                    Point2D lowLeftPoint = null;
                    Point2D lowRightPoint = null;
                    for (int i = 0; i < maskSeriesLow.getData().size() - 1; i++) {
                        float x1 = maskSeriesLow.getData().get(i).getXValue().floatValue();
                        float x2 = maskSeriesLow.getData().get(i + 1).getXValue().floatValue();
                        if (highPoint.x >= x1 && highPoint.x <= x2) {
                            lowLeftPoint = new Point2D(x1, maskSeriesLow.getData().get(i).getYValue().floatValue());
                            lowRightPoint = new Point2D(x2, LinC.maskSeriesLow.getData().get(i + 1).getYValue().floatValue());
                        }
                    }

                    float d = (highPoint.x - lowLeftPoint.x) * (lowRightPoint.y - lowLeftPoint.y) - (highPoint.y - lowLeftPoint.y) * (lowRightPoint.x - lowLeftPoint.x);

                    if (d > 0) {
                        LinC.maskSeriesHigh.getData().remove(j);
                        found = true;
                        Alert alert = new Alert(Alert.AlertType.ERROR, "High mask cannot be lower than Low Mask", ButtonType.OK);
                        alert.show();

                    }
                    System.out.println("D : " + d);
                }
            }else if(seriesType.equals(("low"))){
                for (int j = 0; j < parentLowList.size();j++) {
                    Point2D lowPoint = parentLowList.get(j);
                    if(lowPoint.x < LinC.maskSeriesHigh.getData().get(0).getXValue().floatValue() ||
                            lowPoint.x > LinC.maskSeriesHigh.getData().get(LinC.maskSeriesHigh.getData().size()-1).getXValue().floatValue()) continue;
                    Point2D highLeftPoint = null;
                    Point2D highRightPoint = null;
                    for (int i = 0; i < maskSeriesHigh.getData().size() - 1; i++) {
                        float x1 = maskSeriesHigh.getData().get(i).getXValue().floatValue();
                        float x2 = maskSeriesHigh.getData().get(i + 1).getXValue().floatValue();
                        if (lowPoint.x >= x1 && lowPoint.x <= x2) {
                            highLeftPoint = new Point2D(x1, maskSeriesHigh.getData().get(i).getYValue().floatValue());
                            highRightPoint = new Point2D(x2, LinC.maskSeriesHigh.getData().get(i + 1).getYValue().floatValue());
                        }
                    }

                    float d = (lowPoint.x - highLeftPoint.x) * (highRightPoint.y - highLeftPoint.y) - (lowPoint.y - highLeftPoint.y) * (highRightPoint.x - highLeftPoint.x);

                    if (d < 0) {
                        LinC.maskSeriesLow.getData().remove(j);
                        found = true;
                        Alert alert = new Alert(Alert.AlertType.ERROR, "High mask cannot be lower than Low Mask", ButtonType.OK);
                        alert.show();

                    }
                    System.out.println("D : " + d);
                }
            }




        }
        return found;
    }

    public static void calculateFailureIntervals(List<Point2D> intersectionPointsList,List<Point2D> parentHighList,
                                                 List<Point2D> parentLowList, List<Point2D> dataList){

    }
    public static void newHighLowCollisionDetection(String seriesType){
        //List<ParentRelayPair> parentRelayPairList = new ArrayList<>();
        List<Point2D> parentHighList = new ArrayList<>();
        List<Point2D> parentLowList = new ArrayList<>();
        for(int i = 0; i < LinC.maskSeriesLow.getData().size(); i++){
            Point2D lowPoint = new Point2D(LinC.maskSeriesLow.getData().get(i).getXValue().floatValue(), LinC.maskSeriesLow.getData().get(i).getYValue().floatValue());
            parentLowList.add(lowPoint);
            parentLowListFinal.add(lowPoint);
        }
        for(int i = 0; i < LinC.maskSeriesHigh.getData().size(); i++){
            Point2D highPoint = new Point2D(LinC.maskSeriesHigh.getData().get(i).getXValue().floatValue(), LinC.maskSeriesHigh.getData().get(i).getYValue().floatValue());
            parentHighList.add(highPoint);
            parentHighListFinal.add(highPoint);
        }
        //--------------------------------------------------------------------------------
        boolean invalid = checkIfAboveOrBelow(seriesType, parentHighList, parentLowList);
        if(invalid){

        }
        //-----------------------------------------------------------------------------
        if(LinC.maskSeriesLow.getData().size() > 1 && LinC.maskSeriesHigh.getData().size() > 1){



            //Create lines and compare
            for(int i = 0; i < parentHighList.size()-1; i++){
                Point2D highxy1 = parentHighList.get(i);
                Point2D highxy2 = parentHighList.get(i+1);

                for(int j = 0 ; j < parentLowList.size()-1; j++){
                    Point2D lowxy1 = parentLowList.get(j);
                    Point2D lowxy2 = parentLowList.get(j+1);

                    Line2D highLine = new Line2D(highxy1, highxy2);
                    Line2D lowLine = new Line2D(lowxy1, lowxy2);

                    if(highLine.intersectsLine(lowLine)){
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Low and high masks cannot intersect!", ButtonType.OK);
                        alert.show();
                        if(seriesType.equals("high")){
                            LinC.maskSeriesHigh.getData().remove(i+1);
                        }else{
                            LinC.maskSeriesLow.getData().remove(j+1);

                        }
                        System.out.println("Intersection detected :");
                        System.out.println("High Line : "+ "(" +highxy1.x + "," + highxy1.y+")"+ "(" +highxy2.x + "," + highxy2.y+")");
                        System.out.println("Low Line : "+ "(" +lowxy1.x + "," + lowxy1.y+")"+ "(" +lowxy2.x + "," + lowxy2.y+")");
                    }
                }
            }
        }

    }


}
