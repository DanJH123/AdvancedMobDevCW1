package a15071894.coursework1.Points;

import android.content.res.Resources;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import java.net.MalformedURLException;
import java.net.URL;

import a15081794.coursework1.R;

public class BusPoint extends APoint {

    @Override
    public String getJSONArrayId() {
        return "stopPoints";
    }

    @Override
    public String getJSONIdTag() {
        return "id";
    }

    @Override
    public float getColourCode() {
        return BitmapDescriptorFactory.HUE_BLUE;
    }


    @Override
    public URL getNearbyPointURL(Resources resources, String radius, LatLng currentLocationCoordinates)
                                                                    throws MalformedURLException {
        return new URL(
                resources.getString(R.string.tfl_nearby_stoppoint_url) +
                resources.getString(R.string.tfl_bus_id)+
                "&lat=" + currentLocationCoordinates.latitude +
                "&lon=" + currentLocationCoordinates.longitude +
                "&radius=" + radius +
                        resources.getString(R.string.tfl_app_credentials));
    }

    @Override
    public URL getDeparturesURL(Resources resources) throws MalformedURLException {
        return new URL(
                resources.getString(R.string.tfl_departure_info_url)+
                ""+super.getId()+
                        resources.getString(R.string.tfl_departure_info_end));
    }

}
