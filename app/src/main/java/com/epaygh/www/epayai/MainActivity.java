package com.epaygh.www.epayai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.nio.charset.Charset;
import java. util.Arrays;

import android.support.design.widget.Snackbar;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Response";
    private String token;
    Button loadApi, postReq;
    private Spinner spinner1;

    EditText amount, customer_name, customer_email, customer_telephone, mobile_wallet_number, payment_description;

    String customer_name1, customer_email1, customer_telephone1, mobile_wallet_number1, payment_description1, mobile_network1, amount_num1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amount = (EditText)findViewById(R.id.amount);

        customer_name = (EditText)findViewById(R.id.customer_name);
        customer_email = (EditText)findViewById(R.id.customer_email);
        customer_telephone = (EditText)findViewById(R.id.momoinput);
        mobile_wallet_number = (EditText)findViewById(R.id.momoinput);
        payment_description = (EditText)findViewById(R.id.description);
        customer_name = (EditText)findViewById(R.id.customer_name);

        spinner1 = (Spinner) findViewById(R.id.spinner);



        postReq = (Button)findViewById(R.id.payNowButton);

        postReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_num1 = amount.getText().toString();
                mobile_network1 = String.valueOf(spinner1.getSelectedItem());
                customer_name1 = customer_name.getText().toString();
                customer_email1 = customer_email.getText().toString();
                customer_telephone1 = customer_telephone.getText().toString();
                mobile_wallet_number1 = mobile_wallet_number.getText().toString();
                payment_description1 = payment_description.getText().toString();


                try {
                    postRequest();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void postRequest() throws IOException {

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "https://epaygh.com/api/v1/token";

        OkHttpClient client = new OkHttpClient();

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("merchant_key", "YOUR_MERCHANT_KEY");
            postdata.put("app_id", "APP_ID");
            postdata.put("app_secret", "APP_SECRET");
        } catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                String mMessage = response.body().string();
                String mMessage = response.body().string();

                String[] arrSplit = mMessage.split(",");
                for (int i=0; i < arrSplit.length; i++)
                {
//                    System.out.println(arrSplit[i]);
//                    Log.e(TAG, arrSplit[i]);

                    if(i == 2)
                    {
                        String[] token_line = arrSplit[i].split("\"");
                        for (int j=0; j < token_line.length; j++)
                        {
//                            Log.e(TAG, token_line[j]);
                            if (j == 5){
                                token = token_line[j];
                                Log.e(TAG, token);
                            }

                        }
                    }
                }

                makePayment();

//                Log.e(TAG, String.valueOf(response));
            }
        });
    }

    public void makePayment() throws IOException {

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        String url = "https://epaygh.com/api/v1/charge";

        OkHttpClient client = new OkHttpClient();

        Log.d("Token", "Token Again: "+token);

        //Random String to use as reference
        String generatedString = getAlphaNumericString(10);

        JSONObject postdata = new JSONObject();
        try {
            postdata.put("reference", generatedString);
            postdata.put("amount", 1.00);
            postdata.put("payment_method", "momo");
            postdata.put("customer_name", customer_name1);
            postdata.put("customer_email", customer_email1);
            postdata.put("customer_telephone", mobile_wallet_number1);
            postdata.put("mobile_wallet_number", customer_telephone1);
            postdata.put("mobile_wallet_network", "mtn");
            postdata.put("payment_description", payment_description1);
        } catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .addHeader("Authorization","Bearer "+ token)
                .build();

        Log.d("Request Header:", String.valueOf(request));

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage().toString();
                Log.w("failure Response", mMessage);
                //call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                String mMessage = response.body().string();
                String mMessage = response.body().string();

                String success_message = "A payment request has been sent to the mobile wallet.\n" +
                        "Please Approve Payment.";

                Log.e(TAG, mMessage);
//                Snackbar mySnackbar = Snackbar.make(RelativeLayout, success_message, 4000);

                Snackbar.make(findViewById(R.id.myRel), success_message,5000)
                        .show();


//                String[] arrSplit = mMessage.split(",");


//                Log.e(TAG, String.valueOf(response));
            }
        });
    }

    // function to generate a random string of length n
    static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
}