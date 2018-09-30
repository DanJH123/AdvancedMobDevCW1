package a15071894.coursework1.JsonHandling;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import a15071894.coursework1.Points.APoint;
import a15071894.coursework1.Points.APointInfo;
import a15071894.coursework1.Points.BikePointInfo;

/*
* This class gets the bike point information from the JSON request, implementing methods from the
* superclass.
* */
public class JsonBikePointLocation extends AJsonLocation {

    @Override
    protected APoint getPointFromJson(JSONObject jPoint, APoint point) throws JSONException {
        point.setId(jPoint.getString(point.getJSONIdTag()));
        point.setCommonName(jPoint.getString("commonName"));
        point.setCoordinates(new LatLng(
                jPoint.getDouble("lat"), jPoint.getDouble("lon")));
        JSONArray apJsonArray = jPoint.getJSONArray("additionalProperties");
        for (int i = 0; i < apJsonArray.length(); i++){
            String apKey = (apJsonArray.getJSONObject(i) != null) ?
                    apJsonArray.getJSONObject(i).getString("key") : "";
            String apValue =(apJsonArray.getJSONObject(i) != null) ?
                    apJsonArray.getJSONObject(i).getString("value") : "";
            if(!apKey.isEmpty() && !apValue.isEmpty() && apKey.equals("NbBikes")){
                ArrayList<APointInfo> bikePointInfo = new ArrayList<>();
                bikePointInfo.add(new BikePointInfo(apValue,jPoint.getString("commonName")));
                point.setPointInfo(bikePointInfo);
            }
        }
        return point;
    }

    @Override
    protected ArrayList getPointInformation(APoint point, URL... params) throws JSONException {
        return null; //all information is retrieved in location request. Info request not necessary
    }
}
