package com.delegate42.android.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ici on 19.1.2016.
 */
public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "2de7609d6cdb48dcb39251f917a38d17";

    private static final String FETCH_RECENTS_METHOD = "flickr.photos.getRecent";
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final Uri ENDPOINTT = Uri.parse("https://api.flickr.com/services/rest/")
                .buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .appendQueryParameter("format", "json")
                .appendQueryParameter("nojsoncallback", "1")
                .appendQueryParameter("extras", "url_s")
                .build();


    private List<GalleryItem> downloadGalleryItems(String url) {
        List<GalleryItem> items = new ArrayList<>();
        try {

            String jsonString = getUrlString(url);
            JSONObject jsonBody = new JSONObject(jsonString);
            items.addAll(parseItems(jsonBody));
            Log.i(TAG,"Received JSON: "+jsonString);
        } catch (IOException e) {
            Log.e(TAG,"Failed to fetch items: ",e);
        } catch (JSONException e) {
            Log.e(TAG,"Failed to parse JSON: ",e);
        }
        return items;
    }

    private String buildUrl(String method,Integer pageNumber, String query) {
        Uri.Builder uriBuilder= ENDPOINTT.buildUpon()
                .appendQueryParameter("method", method)
                .appendQueryParameter("page",pageNumber.toString());

        if(method.equals(SEARCH_METHOD)) {
            uriBuilder.appendQueryParameter("text",query);
        }

        return uriBuilder.toString();
    }

    public List<GalleryItem> fetchRecentPhotos(Integer pageNumber) {
        String url = buildUrl(FETCH_RECENTS_METHOD,pageNumber,null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(Integer pageNumber, String query) {
        String url = buildUrl(SEARCH_METHOD,pageNumber,query);
        return downloadGalleryItems(url);
    }

    private List<GalleryItem> parseItems(JSONObject jsonBody) throws IOException, JSONException {
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        String jsonString = photoJsonArray.toString();
        Gson gson = new Gson();
        return gson.fromJson(jsonString, new TypeToken<List<GalleryItem>>() {}.getType());
    }

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage()+": with "+urlSpec);
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();

        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
}
