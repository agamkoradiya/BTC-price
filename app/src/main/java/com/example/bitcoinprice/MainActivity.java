package com.example.bitcoinprice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androdocs.httprequest.HttpRequest;
import com.appolica.flubber.Flubber;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    LinearLayout row1,row2;
    Double btc_price;
    Double usd_price;
    Double final_btc_price;
    TextView usd_textView , inr_textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        row1 = findViewById(R.id.row1);
        row2 = findViewById(R.id.row2);

        usd_textView = findViewById(R.id.usd_textView);
        inr_textView = findViewById(R.id.inr_textView);

        Intent i = getIntent();
        String btc_price = i.getStringExtra("btc_price");
        String final_btc_price = i.getStringExtra("final_btc_price");

        Flubber.with()
                .animation(Flubber.AnimationPreset.SQUEEZE_RIGHT) // Slide up animation
                .repeatCount(0)                              // Repeat once
                .duration(1500)                              // Last for 1000 milliseconds(1 second)
                .createFor(row1)                             // Apply it to the view
                .start();
        usd_textView.setText(btc_price);

        Flubber.with()
                .animation(Flubber.AnimationPreset.SQUEEZE_LEFT) // Slide up animation
                .repeatCount(0)                              // Repeat once
                .duration(1500)                              // Last for 1000 milliseconds(1 second)
                .createFor(row2)                             // Apply it to the view
                .start();
        inr_textView.setText(final_btc_price);

    }

    public void btn(View view) {

        ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() == true) {

            new ApiProcess().execute();
            new ApiProcess1().execute();

        }else {
            Toast.makeText(getApplicationContext(), "Check Your Internet Connection!!!", Toast.LENGTH_SHORT).show();
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
                        Log.d("fun"," -------------------->     " + i);
                        btc_price = zero.getDouble("price_usd");
                        Log.d("fun"," -------------------->     " + btc_price);
                    }
                }

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Error:" + e.toString(), Toast.LENGTH_SHORT).show();
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

                Log.d("fun","---------------->       " + usd_price);

                final_btc_price = btc_price * usd_price;
                Log.d("fun","~~~~~~~~~~~~~~~~~~~~~~~~>     " + final_btc_price);


                // ANIMATION WITH ANSWER :

                Flubber.with()
                        .animation(Flubber.AnimationPreset.SQUEEZE_RIGHT) // Slide up animation
                        .repeatCount(0)                              // Repeat once
                        .duration(1000)                              // Last for 1000 milliseconds(1 second)
                        .createFor(row1)                             // Apply it to the view
                        .start();
                usd_textView.setText(String.valueOf(btc_price));

                Flubber.with()
                        .animation(Flubber.AnimationPreset.SQUEEZE_LEFT) // Slide up animation
                        .repeatCount(0)                              // Repeat once
                        .duration(1000)                              // Last for 1000 milliseconds(1 second)
                        .createFor(row2)                             // Apply it to the view
                        .start();
                inr_textView.setText(String.valueOf(final_btc_price));

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Something went wrong !!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
