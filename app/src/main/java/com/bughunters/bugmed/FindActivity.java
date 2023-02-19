package com.bughunters.bugmed;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bughunters.JsonParser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class FindActivity extends AppCompatActivity implements OnMapReadyCallback{
    //initialize variable
    Spinner sp_type;
    Button bt_find;
    SupportMapFragment supportMapFragment;
   static GoogleMap map;

    FusedLocationProviderClient fusedLocationProviderClient;

    double currentLat = 0, currentLong = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

           //assign variable
        sp_type = findViewById(R.id.sp_type);
        bt_find = findViewById(R.id.bt_find);



            //initialize array of place type
        String[] placeTypeList = {"hospital", "pharmacy"};
        //initialize array of place name
        String[] placeNameList = {"Hospital", "Pharmacy"};
           //set adapter on spinner
        sp_type.setAdapter(new ArrayAdapter<>(FindActivity.this,
                android.R.layout.simple_spinner_dropdown_item, placeNameList));


        //initialize fused location client
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);



        bt_find.setOnClickListener(v -> {
            //get selected position of spinner
            int i = sp_type.getSelectedItemPosition();
            // initialize url
            String url =  "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +//url
                    "?location=" + currentLat + "," + currentLong + //location lati nad longi
                    "&radius=5000" + //nearby radius
                    "&type=" + placeTypeList[i] +  //place type
                    "&sensor=true" + //sensor
                    "&key=" + getResources().getString(R.string.google_map_key); //google maps key

            //executing place task method to download json
            new PlaceTask().execute(url);


        });

    }

    private void util (){
        //check permission
        if (ActivityCompat.checkSelfPermission(FindActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //when permision granted call method
            //getCurrentLocation();
            return;
        }
        //
        Task<Location> ta = fusedLocationProviderClient.getLastLocation();
        ta.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    currentLat=location.getLatitude();
                    currentLong=location.getLongitude();

                    supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(FindActivity.this);
                }
            }
        });
//        else {
//            //when permission denied
//            //request permision
//            ActivityCompat.requestPermissions(FindActivity.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
//
//        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        LatLng ll =new LatLng(currentLat,currentLong);
        MarkerOptions mar = new MarkerOptions().position(ll);
        map.animateCamera(CameraUpdateFactory.newLatLng(ll));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(ll,4));
        map.addMarker(mar);
        map=googleMap;
        ParserTask pt = new ParserTask(map);
        pt.execute();
    }

    private void getCurrentLocation() {

        //initialize task location
//        Task<Location> task = fusedLocationProviderClient.getLastLocation();
//        task.addOnSuccessListener(location -> {
//
//            //when success
//            if(location!=null) {
//
//                //when location is not equal to null
//                //get current latitude
//                currentLat = location.getLatitude();
//                //get current longitude
//                currentLong = location.getLongitude();
//
//                //sync map
////                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
////
////                    @Override
////                    public void onMapReady(GoogleMap googleMap) {
////                        //when map is ready
////                        map = googleMap;
////                        //zoom current location
////                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
////                                new LatLng(currentLat, currentLong), 10
////                        ));
////                    }
////                });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //when permission granted
                // call method
                getCurrentLocation();
            }
        }

    }


    private  class PlaceTask extends AsyncTask<String,Integer,String> {
        @Override
        protected String doInBackground(String... strings) {
            String data =null;
            try {
                // initialize data
                data = downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //execute parser task
//            new ParserTask().execute(s);
        }
    }

    private String downloadUrl (String string) throws IOException {
        //intitialize url
        URL url = new URL(string);
        //initialize connection
        HttpURLConnection connection =(HttpURLConnection)url.openConnection();
        //connect connection
        connection.connect();
        //initialize input streams
        InputStream stream= connection.getInputStream();
        //initialize buffer reader
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        //initialize string builder
        StringBuilder builder = new StringBuilder();
        //initialize string variable
        String line;
        //Use While loop
        while ((line =reader.readLine())!=null){

            //append line
            builder.append(line);

        }
        //get appended data
        String data = builder.toString();
//close reader
        reader.close();
        //return data
        return data;


    }


    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>>{
        GoogleMap map ;
        public ParserTask (GoogleMap map ){
            this.map=map;
        }

        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //create json parsher class
            JsonParser jsonParser = new JsonParser();
            //initiliaze hash maplist
            List<HashMap<String,String>> mapList = null;
            JSONObject object;
            try {
                //initialize json object
                object =new JSONObject(strings[0]);
                //parse json object maplist
                mapList =jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //return maplist
            return mapList;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {
            super.onPostExecute(hashMaps);
            //clear map

                map.clear();
            

            //use for loop
            for(int i =0;i<hashMaps.size();i++){
                HashMap<String,String> hashMapList = hashMaps.get(i);
                //get latitude
                double lat = Double.parseDouble(Objects.requireNonNull(hashMapList.get("lat")));
                //get longitude
                double lng = Double.parseDouble(Objects.requireNonNull(hashMapList.get("lng")));
                //get name
                String name = hashMapList.get("name");
                //concat latitude and longitude
                LatLng latlng = new LatLng(lat,lng);
                //initialize marker options
                MarkerOptions options= new MarkerOptions();
                //set position
                options.position(latlng);
                //set title
                options.title(name);
                //add marker on map
                map.addMarker(options);
            }
        }
    }
}