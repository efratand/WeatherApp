package com.myapps.and.weatherapp;

import android.app.Service;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity implements LocationListener {

    String openWeatherKey = "86fd79f91e7435f10ed56118d31f5159";
    EditText cityET;
    Button goBtn;
    TextView descTV;
    ImageView weatherIV;

    Location currentLocation;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cityET = (EditText) findViewById(R.id.cityET);

        weatherIV = (ImageView) findViewById(R.id.imageView);
        //goBtn = (Button) findViewById(R.id.goBtn);
        //goBtn.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        //String weatherStr = "http://api.openweathermap.org/data/2.5/weather?q="+ cityET.getText().toString()+"&appid="+openWeatherKey;
        //        Log.d("on click","***");
                listenOnLocation();
        //
        //           }
        //     });

        descTV = (TextView) findViewById(R.id.descTV);
    }

    @Override
    public void onLocationChanged(Location location) {
        double currentLat = location.getLatitude();
        double currentLong = location.getLongitude();
        String weatherStr = "http://api.openweathermap.org/data/2.5/weather?lat=" + currentLat + "&lon=" + currentLong + "&appid=" + openWeatherKey;
        DownloadWeather d = new DownloadWeather();
        Log.i("api string=",weatherStr);
        d.execute(weatherStr);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class DownloadWeather extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            BufferedReader input = null;
            HttpURLConnection connection = null;
            StringBuilder response = new StringBuilder();

            URL url = null;
            try {
                url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();
                input = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                String line = "";
                while ((line = input.readLine()) != null) {
                    response.append(line + "\n");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (input != null) {
                    connection.disconnect();
                }
            }

            Log.i("json result-",response.toString());
            return response.toString();
        }


        @Override
        protected void onPostExecute(String s) {
            Gson gson = new Gson();
            JsonResponse jsonResponse = gson.fromJson(s, JsonResponse.class);
            Weather cityWeather = jsonResponse.weather.get(0);

            cityET.setText(jsonResponse.name);
            cityWeather.icon = "http://openweathermap.org/img/w/" + cityWeather.icon + ".png";
            Picasso.with(MainActivity.this).load(cityWeather.icon).into(weatherIV);
            descTV.setText(cityWeather.description);

        }
    }

    private void listenOnLocation() {

        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        // Check for permissions
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            // permission granted. register for location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, MainActivity.this);
        } else {
            // request user for permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 12);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 12) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, MainActivity.this);
            } else
            {
                Toast.makeText(MainActivity.this, "App requires location services", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(MainActivity.this);
    }
}

