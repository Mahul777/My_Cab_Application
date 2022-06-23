package com.chitkarauniversity.mycabapplication;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chitkarauniversity.mycabapplication.databinding.ActivityDriverHomeBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DriverHome extends FragmentActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap mMap;
    private ActivityDriverHomeBinding binding;
    LatLng ltlg;
    LocationManager lm;
    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDriverHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bt = findViewById(R.id.dh_btn_refresh);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ActivityCompat.checkSelfPermission(DriverHome.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverHome.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, DriverHome.this);
                gotoLocation();
            }
        });
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        ActivityCompat.requestPermissions(DriverHome.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);

    }

    void gotoLocation() {
        if (ltlg != null) {
            mMap.clear();
            CircleOptions co = new CircleOptions();
            co.center(ltlg);
            co.fillColor(Color.rgb(0, 0, 255));
            co.radius(50);
            mMap.addCircle(co);
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(ltlg, 15);
            mMap.animateCamera(cu);
            SharedPreferences prefs = getSharedPreferences("mytaxi", MODE_PRIVATE);
            String taxino = prefs.getString("taxino", "NA");
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, String> user = new HashMap<>();
            user.put("latitude", "" + ltlg.latitude);
            user.put("longitude", "" + ltlg.longitude);
            user.put("datetime", new Date().toString());
            user.put("pno", prefs.getString("pno", "NA"));

            // Add a new document with a generated ID
            db.collection("taxies").document(taxino)
                    .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(DriverHome.this, "Saving data properly", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location!=null) {
                ltlg = new LatLng(location.getLatitude(), location.getLongitude());
                Toast.makeText(DriverHome.this, "Wait for GPS", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Toast.makeText(DriverHome.this, "Entered in location changed", Toast.LENGTH_SHORT).show();
        ltlg=new LatLng(location.getLatitude(),location.getLongitude());
        gotoLocation();
    }
}