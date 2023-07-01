package com.example.apprealidadeaumentada;


import android.app.Activity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class SketchfabAPI extends Activity {
    private static final String SKETCHFAB_API_URL = "https://api.sketchfab.com/v3";
    private static final String SKETCHFAB_ME_MODELS_ENDPOINT = "/me/models";

    private final String token;

    public SketchfabAPI(String token) {
        this.token = token;
    }

    public String getModels() throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(SKETCHFAB_API_URL + SKETCHFAB_ME_MODELS_ENDPOINT)
                .addHeader("Authorization", "Token " + token)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }

            assert response.body() != null;
            return response.body().string();
        }
    }
}
