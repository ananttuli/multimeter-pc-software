package prototype.utils;

/**
 * Created by anant on 09-May-17.
 */
public class RelayCoord{
    Number x;
    Number y;

    ParentCoord pc1;
    ParentCoord pc2;

    public RelayCoord(){
        pc1 = new ParentCoord();
        pc2 = new ParentCoord();

    }
    public RelayCoord(Number rx, Number ry){
        x = rx;
        y = ry;
    }
    public Number getXCoord(){
        return x;
    }
    public Number getYCoord(){
        return y;
    }

    public void setXCoord(Number xc){
        x = xc;
    }
    public void setYCoord(Number yc){
        y = yc;
    }

    public ParentCoord getParentCoord1() {
        return pc1;
    }
    public ParentCoord getParentCoord2(){
        return pc2;
    }
    public void setParentCoord1(Number pxc, Number pyc){
        pc1 = new ParentCoord();
        pc1.setCoordinates(pxc, pyc);
    }
    public void setParentCoord2(Number pxc, Number pyc){
        pc2 = new ParentCoord();
        pc2.setCoordinates(pxc, pyc);
    }
}