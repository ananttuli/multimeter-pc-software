package prototype.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anant on 10-May-17.
 */
public class ParentRelayPair {

    ParentCoord p1;
    ParentCoord p2;
    List<RelayCoord> relayCoords;

    public ParentRelayPair(){
        p1 = new ParentCoord();
        p2 = new ParentCoord();
        relayCoords = new ArrayList<>();
    }

    public ParentRelayPair(ParentCoord pc1, ParentCoord pc2, List<RelayCoord> rc){
        p1 = new ParentCoord();
        p2 = new ParentCoord();
        relayCoords = new ArrayList<>();
        p1 = pc1;
        p2 = pc2;
        for(RelayCoord r:rc){
            relayCoords.add(r);
        }
    }

    public ParentCoord getParentCoord1(){
        return p1;
    }
    public ParentCoord getParentCoord2(){
        return p2;
    }
    public List<RelayCoord> getRelayCoordsListFromPair(){
        return relayCoords;
    }

}
