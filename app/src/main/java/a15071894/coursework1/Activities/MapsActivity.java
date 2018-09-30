package a15071894.coursework1.Activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import a15071894.coursework1.Control.LocationController;
import a15081794.coursework1.R;
import a15071894.coursework1.Control.Constants;
import a15071894.coursework1.Control.MainMenuView;
import a15071894.coursework1.Points.APoint;
import a15071894.coursework1.Points.BikePoint;
import a15071894.coursework1.Points.BusPoint;
import a15071894.coursework1.Points.TubePoint;

/*
* This activity displays the map. It receives information from other activities and converts into
* pinpoints. It uses the LocationController class for most of the handling.
* */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng LONDON_COORDINATES = new LatLng(51.5060891,-0.1314385);
    private FloatingActionButton mainMenuFab;
    private String location;
    private String phone;
    private String distance;
    private boolean tubeModeToggle;
    private boolean busModeToggle;
    private boolean bikeModeToggle;
    private Map<Marker,APoint> pointsMap = new HashMap<>();
    private MainMenuView view;
    private APoint currentPoint;

    private LocationController locationController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

    // Get the parameters to use for providing the different functionality of the app.
        this.location = getIntent().getStringExtra(Constants.LOCATION);
        this.phone = getIntent().getStringExtra(Constants.LOCATION);
        this.distance = getIntent().getStringExtra(Constants.DISTANCE);
        this.tubeModeToggle = getIntent().getBooleanExtra(Constants.TUBE, false);
        this.busModeToggle = getIntent().getBooleanExtra(Constants.BUS, false);
        this.bikeModeToggle = getIntent().getBooleanExtra(Constants.BIKE, false);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    //floating action button that takes user back to main menu
        mainMenuFab = (FloatingActionButton) findViewById(R.id.settings_fab);
        mainMenuFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        if(this.location != null) { //if it is null, the View Map button was pressed in the menu
        //Initialise the View with the information retrieved from Intent
            this.view = new MainMenuView(
                    this.location, this.distance, this.tubeModeToggle, this.busModeToggle, this.bikeModeToggle);

            locationController = new LocationController(getApplicationContext(), this.view);
        //Place the simulated location pin down
            LatLng locationCoords = getCurrentLocationCoordinates(this.view.getLocation());
            addSimulatedLocationPin(locationCoords);
            try {
                if (tubeModeToggle) {
                    currentPoint = new TubePoint();
                    addNearbyLocationPins(currentPoint, locationCoords);
                }
                if (busModeToggle) {
                    currentPoint = new BusPoint();
                    addNearbyLocationPins(currentPoint, locationCoords);
                }
                if (bikeModeToggle) {
                    currentPoint = new BikePoint();
                    addNearbyLocationPins(currentPoint, locationCoords);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        // when a marker is clicked it should display certain information
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    if (pointsMap.get(marker) != null) {
                        APoint point = pointsMap.get(marker);
                        currentPoint = point;
                        if (point instanceof BikePoint) {
                            //opens dialog fragment containing relevant information about the bike point
                            // doesn't need to be Async task as info was added on retrieval of point
                            DialogFragment pointInfoFragment =
                                    PointInfoFragment.newInstance(point.getPointInfo());
                            pointInfoFragment.show(getFragmentManager(), "dialog");
                        } else {
                            try {
                                //performs Async task to get information and fragment for other points
                                getDepartureInfo(point);
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //ending here most likely means the clicked point is the current location
                        marker.showInfoWindow();
                    }
                    return true;
                }
            });
        }
        else{
            //If View Map was clicked in main menu or there was some other problem, just display London
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LONDON_COORDINATES, 10));
        }
    }

    //Takes care of adding and styling the simulated location pin, as well as moving the camera
    private void addSimulatedLocationPin(LatLng coordinates){
        mMap.clear(); //clear any previous pins dropped
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(coordinates)
                .title( (getIntent().getStringExtra(Constants.TITLESTRING).isEmpty()) ?
                        "Your Location: "+ location : getIntent().getStringExtra(Constants.TITLESTRING))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        marker.showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
    }

    // Calls the Async reader task to GET information on location of nearby transport points
    private void addNearbyLocationPins(APoint point, LatLng coordinates) throws MalformedURLException {
        nearbyPointReaderTask nearbyReaderTask = new nearbyPointReaderTask();
        nearbyReaderTask.execute(locationController.makePointRequest(point, coordinates),
                                    locationController.makeDepartureInfoRequest(point));
    }

    // Calls the Async reader task to GET information on the clicked transport point
    private void getDepartureInfo(APoint point) throws MalformedURLException {
        departureReaderTask departureReaderTask = new departureReaderTask();
        departureReaderTask.execute(locationController.makeDepartureInfoRequest(point));
    }

    // Uses Google maps to turn location string into coordinates.
    public LatLng getCurrentLocationCoordinates(String location){
        LatLng coordinates = null;
        try{
            Geocoder geocoder = new Geocoder(getApplicationContext());
            Address address = geocoder.getFromLocationName(location+" ,London, England", 1).get(0);
            coordinates = new LatLng(address.getLatitude(), address.getLongitude());
        }catch (IOException e){
            e.printStackTrace();
        }
        return coordinates;
    }

    private class nearbyPointReaderTask extends AsyncTask<URL, APoint, ArrayList<APoint>> {
        private APoint point = currentPoint;
        @Override
        protected ArrayList<APoint> doInBackground(URL... params) {
            return locationController.getPointList(point, params[0],params[1]);
        }

        @Override
        protected void onPostExecute(ArrayList<APoint> points) {
            for(APoint result: points){
                BitmapDescriptor iconColour = BitmapDescriptorFactory.defaultMarker(result.getColourCode());
                MarkerOptions markerOptions = new MarkerOptions()   .position(result.getCoordinates())
                                                                    .title(result.getCommonName())
                                                                    .icon(iconColour);
                Marker marker = mMap.addMarker(markerOptions);
                pointsMap.put(marker, result); // for getting the point at each marker when clicked
            }
        }
    }

    private class departureReaderTask extends AsyncTask<Object, Object, ArrayList> {
        private APoint point = currentPoint;
        @Override
        protected ArrayList doInBackground(Object... params) {
            ArrayList pointInfo = new ArrayList<>();
            try {
                pointInfo = locationController.getDepartureInfoList(point, locationController.makeDepartureInfoRequest(point));
            } catch (JSONException | MalformedURLException e) {
                e.printStackTrace();
            }
            return pointInfo;
        }

        @Override
        protected void onPostExecute(ArrayList departureInfoList) {
            DialogFragment departuresFragment =
                    PointInfoFragment.newInstance(departureInfoList);
            departuresFragment.show(getFragmentManager(), "dialog");
        }
    }
}