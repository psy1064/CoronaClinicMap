package com.example.coronaclinicmap;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Camera;
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
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mgoogleMap;
    private ClusterManager<MyItem> clusterManager;
    ArrayList<Clinic> clinics;
    Context context = this;
    final String TAG = "LogMainActivity";
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
        clusterManager = new ClusterManager<>(this,mgoogleMap);

     
                Log.d(TAG, "Load");
                LatLng latLng = new LatLng(addrToPoint(context, "서울시청").getLatitude(), addrToPoint(context, "서울시청").getLongitude());
                mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mgoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));


        mgoogleMap.setOnCameraIdleListener(clusterManager);
        mgoogleMap.setOnMarkerClickListener(clusterManager);

        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem myItem) {
                // mgoogleMap.moveCamera(CameraUpdateFactory.newLatLng(myItem.getPosition()));
                mgoogleMap.animateCamera(CameraUpdateFactory.zoomIn());
                return false;
            }
        });

        for(int i = 0 ; i < clinics.size(); i++) {
            Log.d(TAG, "marker " + String.valueOf(i));
            Log.d(TAG, "clinics name = " + clinics.get(i).getName());
            Location location = addrToPoint(context, clinics.get(i).getAddress());
            MyItem clinicItem = new MyItem(location.getLatitude(), location.getLongitude(),
                    clinics.get(i).getName());
            clusterManager.addItem(clinicItem);
        }
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String marker_number = null;
                for(int i = 0 ; i < clinics.size() ; i++ ) {
                    if(clinics.get(i).findIndex(marker.getTitle()) != null) {
                        marker_number = clinics.get(i).findIndex(marker.getTitle());
                        Log.d(TAG, "marker_number " + marker_number);
                    }
                }
                final int marker_ID_number = Integer.parseInt(marker_number);
                Log.d(TAG, "marker number = " + String.valueOf(marker_ID_number));
                Log.d(TAG, "marker clinic name = " + clinics.get(marker_ID_number).getName());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("병원정보");
                builder.setMessage(
                        "이름 : " + clinics.get(marker_ID_number-1).getName() +
                                "\n주소 : " + clinics.get(marker_ID_number-1).getAddress() +
                                "\n병원전화번호 : " + clinics.get(marker_ID_number-1).getPhoneNumber() +
                                "\n검체채취가능여부 : " + clinics.get(marker_ID_number-1).getSample()
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
            }
        });// 마커 클릭 시 Alert Dialog가 나오도록 설정
    } // 구글맵 사용

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
    } // 주소명으로 위도 경도를 구하는 메소드
}
