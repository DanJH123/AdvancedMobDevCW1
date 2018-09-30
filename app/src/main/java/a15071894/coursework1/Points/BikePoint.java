package a15071894.coursework1.Points;

import android.content.res.Resources;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;

import a15081794.coursework1.R;

public class BikePoint extends APoint {

    public BikePoint(){
    }

    @Override
    public String getJSONArrayId() {
        return "places";
    }

    @Override
    public String getJSONIdTag() { return "id"; }

    @Override
    public float getColourCode() {
        return BitmapDescriptorFactory.HUE_GREEN;
    }

    @Override
    public URL getNearbyPointURL(Resources resources, String radius, LatLng currentLocationCoordinates) throws MalformedURLException {
        return new URL(
                resources.getString(R.string.tfl_nearby_bikepoint_url) +
                "lat=" + currentLocationCoordinates.latitude +
                "&lon=" + currentLocationCoordinates.longitude +
                "&radius=" + radius +
                        resources.getString(R.string.tfl_app_credentials));
    }

    @Override //No URL for this as all info is in nearbyPointURL request
    public URL getDeparturesURL(Resources resources) throws MalformedURLException {
        return null;
    }
}
