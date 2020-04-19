package com.example.bitcoinprice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;

import org.json.JSONObject;

public class Splash extends AppCompatActivity {


    Double btc_price;
    Double usd_price;
    Double final_btc_price;

    private Handler handler;
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() == true) {

            new ApiProcess().execute();
            new ApiProcess1().execute();

        }else{
            Toast.makeText(getApplicationContext(), "Check Your Internet Connection!!!", Toast.LENGTH_SHORT).show();
            runnable = new Runnable() {
                @Override
                public void run() {
                   Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                }
            };
            handler = new Handler();
            handler.postDelayed(runnable,2000);
        }
    }

    class ApiProcess extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            // Generate your API key using https://happi.dev/
            // Paste in the next line ---> E.X. :- https://api.happi.dev/v1/exchange?apikey= *Paste your API key
            String response = HttpRequest.excuteGet("https://api.happi.dev/v1/exchange?apikey=");
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObj = new JSONObject(s);
                int length = jsonObj.getInt("length");

                for (int i = 0 ; i<length ; i++){

                    JSONObject zero = jsonObj.getJSONArray("result").getJSONObject(i);
                    String name = zero.getString("code");

                    if (name.equals("BTC")){
                        Log.d("background_process"," ---------------------->     " + i);
                        btc_price = zero.getDouble("price_usd");
                        Log.d("background_process"," ---------------------->     " + btc_price);
                    }
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class ApiProcess1 extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            // Generate your API key using https://happi.dev/
            // Paste in the next line ---> E.X. :- https://api.happi.dev/v1/exchange?apikey= *Paste your API key
            String response = HttpRequest.excuteGet("https://api.happi.dev/v1/exchange/usd/inr?apikey=");
            return response;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObj = new JSONObject(s);

                JSONObject results = jsonObj.getJSONObject("result").getJSONObject("result");
                usd_price = results.getDouble("value");
                final_btc_price = btc_price * usd_price;

                Log.d("background_process","~~~~~~~~~~~~~~~~~~~~~~~~~ >       " + final_btc_price);

                Intent i = new Intent(Splash.this, MainActivity.class);
                i.putExtra("btc_price", String.valueOf(btc_price));
                i.putExtra("final_btc_price", String.valueOf(final_btc_price));
                startActivity(i);

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable!=null)
            handler.removeCallbacks(runnable);
    }
}
