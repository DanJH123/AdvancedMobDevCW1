package a15071894.coursework1.JsonHandling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import a15071894.coursework1.Points.APoint;
import a15071894.coursework1.Points.BikePoint;
import a15071894.coursework1.Points.BusPoint;
import a15071894.coursework1.Points.TubePoint;

/*
* Abstract class that contains common methods for each point type and abstract methods for the
* different points. The methods in this class and its subclasses make GET requests to the TFL API
* and then handle the JSON files returned by them.
* */
public abstract class AJsonLocation {
    protected abstract APoint getPointFromJson(JSONObject jStopPoint, APoint point) throws JSONException;
    protected abstract ArrayList getPointInformation(APoint point, URL...params) throws JSONException;

    private APoint getPointType(APoint type) {
        APoint point = null;

        if (type instanceof TubePoint) {
            point = new TubePoint();
        } else if (type instanceof BusPoint) {
            point = new BusPoint();
        } else if (type instanceof BikePoint) {
            point = new BikePoint();
        }
        return point;
    }

    //Uses URL to make a get request and converts the result into an arraylist of APoints
    public ArrayList<APoint> getNearbyPointsFromJson(APoint point, URL... params){
        HttpURLConnection connection = null;
        ArrayList<APoint> results = new ArrayList<>();
        try {
            connection = (HttpURLConnection) params[0].openConnection();
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) { builder.append(line); }
            results = getLocationPoints(results, builder, point);
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (connection == null) connection.disconnect();
        }
        return results;
    }

    // Converts the JSON string into an arraylist of APoints, using a method that needs implementing
    // by subclasses
    private ArrayList<APoint> getLocationPoints(ArrayList<APoint> results, StringBuilder builder,
                                                APoint point) throws JSONException {
        JSONObject jsonObject = new JSONObject(builder.toString());
        JSONArray jPoints = jsonObject.getJSONArray(point.getJSONArrayId());
        for (int i = 0; i < jPoints.length(); i++) {
            results.add(getPointFromJson(jPoints.getJSONObject(i), getPointType(point)));
        }
        return results;
    }
}
