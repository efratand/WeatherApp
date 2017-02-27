package com.myapps.and.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String openWeatherKey="86fd79f91e7435f10ed56118d31f5159";
    EditText cityET;
    Button goBtn;
    TextView descTV;
    ImageView weatherIV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        cityET =(EditText) findViewById(R.id.cityET);

        weatherIV = (ImageView) findViewById(R.id.imageView);
         goBtn = (Button) findViewById(R.id.goBtn);
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String weatherStr = "http://api.openweathermap.org/data/2.5/weather?q="+ cityET.getText().toString()+"&appid="+openWeatherKey;
                //String weatherStr = "http://api.openweathermap.org/data/2.5/weather?q=london&appid=86fd79f91e7435f10ed56118d31f5159";
                DownloadWeather d= new DownloadWeather();
                Log.d("clicked",weatherStr);
                d.execute(weatherStr);
            }
        });

        descTV = (TextView) findViewById(R.id.descTV);
    }

    public class DownloadWeather extends AsyncTask<String,Void,String>
    {

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
            String line="";
            while (( line=input.readLine()) != null)
            {
                response.append(line+"\n");
            }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (input != null)
                {
                    connection.disconnect();
                }
            }


            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            Gson gson = new Gson();
            JsonResponse jsonResponse=gson.fromJson(s,JsonResponse.class);
            Weather cityWeather = jsonResponse.weather.get(0);

            cityWeather.icon = "http://openweathermap.org/img/w/"+cityWeather.icon+".png";
            Picasso.with(MainActivity.this).load(cityWeather.icon).into(weatherIV);
            descTV.setText(cityWeather.description);

        }
    }

}

