package com.chitkarauniversity.mycabapplication;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chitkarauniversity.mycabapplication.databinding.ActivityClientHomeBinding;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.maps.android.SphericalUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class ClientHome extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ActivityClientHomeBinding binding;
    LatLng ltlg;
    LocationManager lm;
    Button btn_find_cab;
    Button btn_referesh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityClientHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_find_cab = findViewById(R.id.btn_find_cab);
        btn_referesh = findViewById(R.id.ch_btn_refresh);
        btn_referesh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(ClientHome.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ClientHome.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, ClientHome.this);
                gotoLocation();

            }
        });
        btn_find_cab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                CircleOptions co = new CircleOptions();
                co.center(ltlg);
                co.fillColor(Color.rgb(0, 0, 255));
                co.radius(50);
                mMap.addCircle(co);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("taxies")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, Object> map = document.getData();
                                        LatLng taxilocation = new LatLng(Double.parseDouble(map.get("latitude").toString()), Double.parseDouble(map.get("longitude").toString()));
                                        double distance = SphericalUtil.computeDistanceBetween(ltlg, taxilocation);
                                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                            @Override
                                            public boolean onMarkerClick(@NonNull Marker marker) {

                                                Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                                                phoneIntent.setData(Uri.parse("tel:"+marker.getTitle()));
                                                startActivity(phoneIntent);
                                                return false;
                                            }
                                        });
                                        if(distance<1000)
                                        {
                                            mMap.addMarker(new MarkerOptions().position(taxilocation).title(map.get("pno").toString())
                                                    .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_baseline_directions_car_24)));

                                        }

                                    }
                                } else {
                                    Toast.makeText(ClientHome.this, "wait", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        ActivityCompat.requestPermissions(ClientHome.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE,Manifest.permission.READ_PHONE_STATE}, 0);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

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
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location!=null) {
                ltlg = new LatLng(location.getLatitude(), location.getLongitude());
                Toast.makeText(ClientHome.this, "Wait for GPS", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        ltlg=new LatLng(location.getLatitude(),location.getLongitude());
        gotoLocation();
    }
}