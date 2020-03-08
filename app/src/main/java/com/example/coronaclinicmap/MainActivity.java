package com.example.coronaclinicmap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.lang.UScript;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mgoogleMap;
    ArrayList<Clinic> clinics;
    Context context = this;
    final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clinics = (ArrayList<Clinic>)getIntent().getSerializableExtra("clinic");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mgoogleMap = googleMap;

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.d(TAG, "Load");
                LatLng latLng = new LatLng(addrToPoint(context, "서울시청").getLatitude(), addrToPoint(context, "서울시청").getLongitude());
                mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mgoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            }
        });

        for(int i = 0 ; i < clinics.size(); i++) {
            Log.d(TAG,"create" + String.valueOf(i));
            Location location = addrToPoint(context, clinics.get(i).getAddress());
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            mgoogleMap.addMarker(markerOptions);
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String marker_ID = marker.getId();
                final String marker_ID_number = marker_ID.substring(1);
                Log.d("Marker ID Number = ", String.valueOf(marker_ID_number));
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("병원정보");
                builder.setMessage(
                        "이름 : " + clinics.get(Integer.parseInt(marker_ID_number)).getName() +
                        "\n주소 : " + clinics.get(Integer.parseInt(marker_ID_number)).getAddress() +
                        "\n병원전화번호 : " + clinics.get(Integer.parseInt(marker_ID_number)).getPhoneNumber() +
                        "\n검체채취가능여부 : " + clinics.get(Integer.parseInt(marker_ID_number)).getSample()
                        );
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton("전화걸기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel" + clinics.get(Integer.parseInt(marker_ID_number)).getPhoneNumber())));
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });
    }

    public static Location addrToPoint(Context context, String addr) {
        Location location = new Location("");
        Geocoder geocoder = new Geocoder(context);
        List<Address> addresses = null;

        try {
            addresses = geocoder.getFromLocationName(addr,3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses != null) {
            for(int i = 0 ; i < addresses.size() ; i++) {
                Address lating = addresses.get(i);
                location.setLatitude(lating.getLatitude());
                location.setLongitude(lating.getLongitude());
            }
        }
        return location;
    }
}
