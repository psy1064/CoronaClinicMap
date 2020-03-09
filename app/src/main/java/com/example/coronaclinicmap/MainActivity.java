package com.example.coronaclinicmap;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mgoogleMap;
    private ClusterManager<MyItem> clusterManager;
    ArrayList<Clinic> clinics;
    ArrayList<Location> clinic_address;
    Context context = this;
    final String TAG = "LogMainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clinics = (ArrayList<Clinic>)getIntent().getSerializableExtra("clinic");
        clinic_address = (ArrayList<Location>)getIntent().getSerializableExtra("clinic_addr");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mgoogleMap = googleMap;
        clusterManager = new ClusterManager<>(this,mgoogleMap);

        mgoogleMap.setOnCameraIdleListener(clusterManager);
        mgoogleMap.setOnMarkerClickListener(clusterManager);

        mgoogleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                Log.d(TAG, "Load");
                LatLng latLng = new LatLng(37.5318247,126.8368475);
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15);
                mgoogleMap.moveCamera(cameraUpdate);
            }
        });

        mgoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String marker_number = null;
                for(int i = 0 ; i < clinics.size() ; i++ ) {
                    if(clinics.get(i).findIndex(marker.getTitle()) != null) {
                        marker_number = clinics.get(i).findIndex(marker.getTitle());
                        Log.d(TAG, "marker_number " + marker_number);
                    }
                } // marker title로 clinic을 검색하여 number 반환받아옴
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
        mgoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                VisibleRegion vr = mgoogleMap.getProjection().getVisibleRegion();
                double left = vr.latLngBounds.southwest.longitude;
                double top = vr.latLngBounds.northeast.latitude;
                double right = vr.latLngBounds.northeast.longitude;
                double bottom = vr.latLngBounds.southwest.latitude;
                Log.d(TAG, "left = " + String.valueOf(left) +
                                    "top = " + String.valueOf(top) +
                                    "right = " + String.valueOf(right) +
                                    "bottom = " + String.valueOf(bottom) + "\n");

                clusterManager.clearItems();
                findMarker(left, top, right, bottom);
            }
        });
    } // 구글맵 사용

    public void findMarker(double left, double top, double right, double bottom) {
        int count = 0;
        for(int i = 0; i < clinic_address.size(); i++) {
            if(clinic_address.get(i).getLongitude() >= left && clinic_address.get(i).getLongitude() <= right) {
                if(clinic_address.get(i).getLatitude() >=bottom && clinic_address.get(i).getLatitude() <= top) {
                    Location location = clinic_address.get(i);
                    MyItem clinicItem = new MyItem(location.getLatitude(), location.getLongitude(),
                                clinics.get(i).getName());
                    clusterManager.addItem(clinicItem);
                }
            }
        }
    } // 위도 경도 범위 안에

}
