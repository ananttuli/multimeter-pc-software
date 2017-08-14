package prototype.utils;

/**
 * Created by anant on 09-May-17.
 */
public class ParentCoord {
    Number parentX;
    Number parentY;

    public ParentCoord(){

    }
    public ParentCoord(Number px, Number py){
        parentX = px;
        parentY = py;
    }
    public Number getXCoordinate(){
        return parentX;
    }
    public Number getYCoordinate(){
        return parentY;
    }
    public void setCoordinates(Number px, Number py){
        parentX = px;
        parentY = py;
    }
    public void setXCoordinate(Number px){
        parentX = px;
    }
    public void setYCoordinate(Number py){
        parentY = py;
    }
}
