package a15071894.coursework1.Points;

import android.content.res.Resources;

import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

// This abstract class represents each of the different types of points. It contains methods for the
// subclasses to implement at the bottom of the class. These APoints are added to an array list after
// JSON handling for display on the Map.
public abstract class APoint {

    private String id, commonName;
    private ArrayList<APointInfo> pointInfo;
    private LatLng coordinates;

    public String getId() {
        return this.id;
    }
    public String getCommonName() {
        return this.commonName;
    }
    public LatLng getCoordinates() {
        return this.coordinates;
    }
    public ArrayList<APointInfo> getPointInfo() {
        return this.pointInfo;
    }

    public void setId(String id) { this.id = id; }
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }
    public void setPointInfo(ArrayList<APointInfo> pointInfo) {
        this.pointInfo = pointInfo;
    }

    public abstract URL getNearbyPointURL(Resources resources, String radius,
                                          LatLng currentLocationCoordinates) throws MalformedURLException;
    public abstract URL getDeparturesURL(Resources resources) throws MalformedURLException;
//Each point can have a different jsonObjectID and JsonArrayId in the GET request. Having these methods
// here allow them to be retrieved when passing an APoint object through in json handling.
    public abstract String getJSONArrayId();
    public abstract String getJSONIdTag();
// For displaying pins in different colours.
    public abstract float getColourCode();
}
