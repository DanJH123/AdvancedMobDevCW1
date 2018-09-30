package a15071894.coursework1.JsonHandling;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;

import a15071894.coursework1.Points.APoint;
import a15071894.coursework1.Points.BikePoint;
import a15071894.coursework1.Points.BusPoint;
import a15071894.coursework1.Points.TubePoint;

/*
* Called a factory as I was supposed to be basing my structure around the Factory pattern. Unsure of
* my success in doing this but the class simplifies the JSON handling for the LocationController.
* */
public class JsonHandlingFactory {

    public ArrayList<APoint> getNearbyPointList(APoint point, URL[] params) {
        AJsonLocation pointHandler = null;
        if(point instanceof BusPoint || point instanceof TubePoint){
            pointHandler = new JsonStopPointLocation();
        }
        else if( point instanceof BikePoint){
            pointHandler = new JsonBikePointLocation();
        }
        return pointHandler != null ? pointHandler.getNearbyPointsFromJson(point, params) : null;
    }

    public ArrayList getStopInfoList(APoint point, URL[] params) throws JSONException {
        AJsonLocation pointHandler = null;
        if(point instanceof BusPoint || point instanceof TubePoint){
            pointHandler = new JsonStopPointLocation();
        }
        else if( point instanceof BikePoint){
            pointHandler = new JsonBikePointLocation();
        }
        return pointHandler != null ? pointHandler.getPointInformation(point, params) : null;
    }
}
