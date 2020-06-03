package ca.mohawk.fallis;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
//SchoolMappingFragment is misnamed. It should be called SchoolInformationFragment. This fragment displays information about the school and a Google Map.
public class SchoolMappingFragment extends Fragment implements OnMapReadyCallback {

	//Tracks if we have location permission.
    private boolean locationPermission = false;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9001;
    private GoogleMap map;
    private View myView;
    private FusedLocationProviderClient fusedLocationProviderClient;


    public SchoolMappingFragment() {
        // Required empty public constructor
    }
	
	//On fragment creation call the getLocationPermission method and displays the XML for School Information Page.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myView = inflater.inflate(R.layout.fragment_school_mapping, container, false);
		
		//Get the users location.
        getLocationPermission();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        return myView;
    }

	//Override the onViewCreated metho and create a map fragment and display it on the screen.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

	//Override the onMapReady method and display a markers for the schools location and call the setMyLocationEnabled method if we have location permission.
    @Override
    public void onMapReady(GoogleMap googleMap) {
		
        map = googleMap;
		
		//Set a markers for Mohawk College and set the map type.
        LatLng mohawkCollegeMarker = new LatLng(43.238713, -79.888082);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.addMarker(new MarkerOptions()
                .position(mohawkCollegeMarker));
		
		//Change the map to display around Mohawk College and zoom the camera in so we can see the roads of Hamilton.
        map.moveCamera(CameraUpdateFactory.newLatLng(mohawkCollegeMarker));
        CameraPosition zoom = CameraPosition.builder()
                .target(mohawkCollegeMarker)
                .zoom(11)
                .bearing(0)
                .tilt(0)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(zoom), 2000, null);
		
		//If we have location permissions display the current users location.
        if(locationPermission)
        {
            map.setMyLocationEnabled(true);
        }
    }

	//Method asks the user if they want to allow the application to use location permissions.
    private void getLocationPermission() {
        
		//Request location permission, so that we can get the location of the device.
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermission = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}
