package itech.app.mapstest;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class UserLocationActivity extends AppCompatActivity implements GoogleMap.OnMarkerDragListener {

    private GPSTracker gps;
    private AudioManager am;

    //static final LatLng MY_CUR_POS = new LatLng(18.520430 , 73.856744);
    private LatLng MY_CUR_POS;
    private GoogleMap googleMap;
    private String LOCALITY;
    private ProgressDialog progressDialog1;
    private String college_name;
    private double latitude,longitude,radius;
    private Button confirm_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        gps = new GPSTracker(UserLocationActivity.this);

        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

        Log.d("LOC","Lat : " + String.valueOf(latitude) + ", Long : " + String.valueOf(longitude));

        MY_CUR_POS = new LatLng(latitude, longitude);

        try {
            Geocoder gcd = new Geocoder(UserLocationActivity.this, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                LOCALITY = addresses.get(0).getLocality();
                for (int i = 0; i < addresses.size(); i++) {
                    Log.d("ERR", addresses.get(i).getLocality());
                }
            }
        } catch (Exception ex) {
            Log.d("ERR", "There is an error parsing.");
        }
        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MY_CUR_POS, 15));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            googleMap.setOnMarkerDragListener(this);
            Marker TP = googleMap.addMarker(new MarkerOptions().
                    position(MY_CUR_POS).title(college_name));

            LatLng newloc = new LatLng(15.3576355,75.1154962);

            String dist = getDistance(MY_CUR_POS,newloc);
            Log.d("LOC",dist);




            CircleOptions circleOptions = new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(20000)
                    .strokeWidth(1)
                    .strokeColor(Color.parseColor("#8e44ad"))
                    .fillColor(Color.parseColor("#449b59b6"));
                    // In meters
            // Get back the mutable Circle
            Circle circle = googleMap.addCircle(circleOptions);

            if(Double.valueOf(dist) < 20){
                Marker TP1 = googleMap.addMarker(new MarkerOptions().
                        position(newloc).title(college_name).snippet("Far Marker"));
                TP1.showInfoWindow();
                TP1.setDraggable(true);

            }


            Toast.makeText(getApplicationContext(), "You selected : " + college_name, Toast.LENGTH_LONG).show();



        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng dragPosition = marker.getPosition();
        double dragLat = dragPosition.latitude;
        double dragLong = dragPosition.longitude;
        Log.i("info", "on drag end :" + dragLat + " dragLong :" + dragLong);
        Toast.makeText(getApplicationContext(), "Marker Dragged..!", Toast.LENGTH_LONG).show();

        LatLng dragloc = new LatLng(dragLat,dragLong);
        String dist = getDistance(MY_CUR_POS,dragloc);
        if(Double.valueOf(dist) < 20){
            Toast.makeText(getApplicationContext(), "Inside 20km, dist:" + dist, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Outside 20km, dist:"+ dist, Toast.LENGTH_LONG).show();
        }
    }

    public static String getDistance(LatLng ll_source, LatLng ll_destination) {


        int Radius = 6371;// radius of earth in Km

        double lat1 = ll_source.latitude;
        double lat2 = ll_destination.latitude;
        double lon1 = ll_source.longitude;
        double lon2 = ll_destination.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        Integer kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        Integer meterInDec = Integer.valueOf(newFormat.format(meter));
        DecimalFormat df = new DecimalFormat("#.#");
        return df.format(valueResult);
    }

}
