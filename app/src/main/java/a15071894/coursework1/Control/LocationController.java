package a15071894.coursework1.Control;

import android.content.Context;
import android.content.res.Resources;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import a15071894.coursework1.JsonHandling.JsonHandlingFactory;
import a15071894.coursework1.Points.APoint;

/*
* As a sort of bridge between the Map and the JSON handling. This class is a more readable version
* of all the JSON handling that goes on. It calls the JSON handler, passing variables through params
* and returning lists of information. It also simplifies the getting of URL strings.
* */
public class LocationController {

    private MainMenuView mainMenuView;
    private Resources resources;
    private Context context;
    private JsonHandlingFactory jsonFactory = new JsonHandlingFactory();

    public LocationController(Context context, MainMenuView mainMenuView) {
        this.mainMenuView = mainMenuView;
        this.context = context;
        this.resources = context.getResources();
    }

    public URL makePointRequest(APoint point, LatLng currentCoordinates) throws MalformedURLException {
        return point.getNearbyPointURL(resources, mainMenuView.getDistance(), currentCoordinates);
    }

    public URL makeDepartureInfoRequest(APoint point) throws MalformedURLException {
        return point.getDeparturesURL(resources);
    }

    public ArrayList<APoint> getPointList(APoint point, URL... url){
        return jsonFactory.getNearbyPointList(point, url);
    }

    public ArrayList getDepartureInfoList(APoint point, URL... url) throws JSONException {
        return jsonFactory.getStopInfoList(point, url);
    }

}