package com.nlrd.alerttransport;

import android.content.Context;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class MapActivity extends Fragment implements OnMapReadyCallback ,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener
{
    private GoogleMap mMap;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Marker marker;
    private LatLng position;
    MarkerOptions markerOptions;
    Float zoom = new Float(17);

    public static LatLng newLocation = new LatLng(0, 0);
    public static int rayon = 1000;
    public static String infoDestination = "Destination";
    public static LatLng myLocation = new LatLng(0,0);


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.activity_map, container, false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment supportMapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        return rootView;
    }

    @Override
    public void setMenuVisibility(boolean menuVisible)
    {
        super.setMenuVisibility(menuVisible);

        if (menuVisible)
        {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            if(newLocation.latitude!=0){
                mMap.clear();
                builder.include(newLocation);
                builder.include(myLocation);
                LatLngBounds bounds = builder.build();
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.20);
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                marker = mMap.addMarker(new MarkerOptions().position(newLocation).title(infoDestination));
                marker.showInfoWindow();
                CircleOptions circleOptions = new CircleOptions()
                        .center(newLocation).radius(rayon).strokeWidth(5).strokeColor(Color.GREEN).fillColor(Color.argb(30,76,212,157));
                Circle circle = mMap.addCircle(circleOptions);
                mMap.animateCamera(cu);
            }else{
                mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

               // marker = mMap.addMarker(new MarkerOptions().position(newLocation));
            }

        }else{
            newLocation = new LatLng(0, 0);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        //add this here:
        buildGoogleApiClient();

        mGoogleApiClient.connect();
    }


    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            myLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            createLocationRequest();
            startLocationUpdates();

        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                position = latLng;
                mMap.clear();
                mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
                marker = mMap.addMarker(new MarkerOptions().position(latLng));
                marker.showInfoWindow();
                new ReverseGeocoding(getContext()).execute(latLng);
            }
        });
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result;

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates state = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        Toast.makeText(getContext(),"Location changed",Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(), 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.

                        break;
                }
            }
        });
        
    }
    private class  ReverseGeocoding extends AsyncTask <LatLng, Void, String>{

        Context mContext;

        public ReverseGeocoding (Context context){
            super();
            mContext = context;
        }
        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder((mContext));
            double lat = params[0].latitude;
            double longitude = params[0].longitude;
            List<Address> adresses = null;
            String adressText = "";

            try{
                adresses = geocoder.getFromLocation(lat,longitude,1);
            } catch (IOException e){
                e.printStackTrace();
            }

            if(adresses != null && adresses.size() > 0 ){
                Address address = adresses.get(0);
                adressText = String.format("%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());
            }
            return adressText;
        }
        @Override
        protected void onPostExecute(String addressText) {
            mMap.clear();
            mMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            marker = mMap.addMarker(new MarkerOptions().position(position).title(addressText));
            marker.showInfoWindow();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this.getContext(),"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = new LatLng(location.getLatitude(),location.getLongitude());
        //Toast.makeText(getContext(), DateFormat.getTimeInstance().format(new Date()).toString(),Toast.LENGTH_SHORT).show();
       Location destination = new Location("destination");
        destination.setLatitude(newLocation.latitude);
        destination.setLongitude(newLocation.longitude);
        float distance = location.distanceTo(destination);
        if(distance <= rayon){
            Toast.makeText(getContext(),"Arrivé",Toast.LENGTH_SHORT).show();
            //AlarmClock(s);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        stopLocationUpdates();
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
}
