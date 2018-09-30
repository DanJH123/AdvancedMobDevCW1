package a15071894.coursework1.JsonHandling;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import a15071894.coursework1.Points.APoint;
import a15071894.coursework1.Points.APointInfo;
import a15071894.coursework1.Points.StopPointInfo;

/*
* This class gets the bus and tube point information from the JSON request, implementing methods
* from the superclass. This requires another GET request to be sent to TFL to get departure information
* */
public class JsonStopPointLocation extends AJsonLocation {

    private String time;

    @Override
    protected APoint getPointFromJson(JSONObject jPoint, APoint point)
                                        throws JSONException {
        point.setId(jPoint.getString(point.getJSONIdTag()));
        point.setCommonName(jPoint.getString("commonName"));
        point.setCoordinates(new LatLng(
                jPoint.getDouble("lat"), jPoint.getDouble("lon")));
        return point;
    }

    @Override
    protected ArrayList getPointInformation(APoint point, URL...url) throws JSONException {
        return getDeparturesArrayFromJson(url);
    }

    private ArrayList<APointInfo> getDeparturesArrayFromJson(URL... url){
        HttpURLConnection connection = null;
        ArrayList<APointInfo> stopPointsInfo = new ArrayList<>();
        try {
            connection = (HttpURLConnection) url[0].openConnection();
            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) { builder.append(line); }
            JSONArray jsonArray = new JSONArray(builder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                stopPointsInfo.add(getPointInfo(jsonArray.getJSONObject(i)));
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (connection == null) connection.disconnect();
        }
        return stopPointsInfo;
    }
    // Works with the getDeparturesArrayFromJson method to handle each object of the json array for
    // departures
    private StopPointInfo getPointInfo(JSONObject jsonInfoObject) throws JSONException {
        String naptanId = jsonInfoObject.has("naptanId") ?
                jsonInfoObject.getString("naptanId") : "Not found";
        String stationName = jsonInfoObject.has("stationName") ?
                jsonInfoObject.getString("stationName") : "Not found";
        String lineName = jsonInfoObject.has("lineName") ?
                jsonInfoObject.getString("lineName") : "N/A";
        String bearing = jsonInfoObject.has("bearing") ?
                jsonInfoObject.getString("bearing") : "N/A";
        String destinationName = jsonInfoObject.has("destinationName") ?
                jsonInfoObject.getString("destinationName") : "Not found";
        String expectedArrival;
        if (jsonInfoObject.has("expectedArrival")) {
            expectedArrival = jsonInfoObject.getString("expectedArrival");
            expectedArrival = getTime(expectedArrival);
        } else {
            expectedArrival = "Not found";
        }
        String modeName = jsonInfoObject.has("modeName") ?
                jsonInfoObject.getString("modeName") : "Not found";

        return new StopPointInfo(naptanId, stationName, lineName, bearing, destinationName, expectedArrival, modeName);
    }

    //Convert timestamp into a more readable time
    public String getTime(String dateString) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        String time = "";
        try {
            Date date = dateFormat.parse(dateString);
            time = timeFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}
