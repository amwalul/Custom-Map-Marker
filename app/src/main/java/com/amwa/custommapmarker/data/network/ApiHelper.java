package com.amwa.custommapmarker.data.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.amwa.custommapmarker.BuildConfig;
import com.amwa.custommapmarker.data.model.Vehicle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHelper {
    private final String endpoint = "https://carbon.api.katsana.com/vehicles/?id=105";
    private final String token = BuildConfig.PERSONAL_ACCESS_TOKEN;
    private ApiInterface apiInterface;

    public ApiHelper(ApiInterface apiInterface) {
        this.apiInterface = apiInterface;
    }

    public Vehicle getData() {
        Vehicle vehicle = null;
        {
            try {
                URL url = new URL(endpoint);

                HttpURLConnection client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("GET");
                client.setRequestProperty("Accept", "application/vnd.KATSANA.v1+json");
                client.setRequestProperty("Authorization", token);
                int responseCode = client.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        response.append(line);
                    }
                    bufferedReader.close();

                    JSONObject responseObject = new JSONObject(response.toString())
                            .getJSONArray("devices")
                            .getJSONObject(0);

                    InputStream in = new URL(responseObject.getString("marker")).openStream();
                    Bitmap image = BitmapFactory.decodeStream(in);
                    in.close();

                    vehicle = new Vehicle(responseObject, image);
                } else {
                    apiInterface.showNetworkErrorMessage(client.getResponseMessage());
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
                apiInterface.showNetworkErrorMessage(e.getLocalizedMessage());
            }
        }
        return vehicle;
    }

}
