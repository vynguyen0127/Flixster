package com.example.flixster;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class SimilarActivity extends AppCompatActivity {

    Movie movie;
    List<Movie> similarMovies;
    String TAG = "SimilarActivity";
    String URL;
    String query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar);

        boolean mode = getIntent().getBooleanExtra("Type",true);
        if(mode){
            movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
            URL = String.format("https://api.themoviedb.org/3/movie/%s/similar?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&language=en-US&page=1", movie.getId());
            getSupportActionBar().setTitle("Similar movies");
            getSupportActionBar().setSubtitle("Similar movies to: " + movie.getTitle());
        }
        else{
            query = getIntent().getStringExtra(MainActivity.KEY_ITEM_TEXT);
            URL = String.format("https://api.themoviedb.org/3/search/movie?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&language=en-US&query=%s&page=1&include_adult=false",
                    query);
            getSupportActionBar().setTitle("Search Results");
            getSupportActionBar().setSubtitle("Showing results for: "+ query);
        }


        RecyclerView rvSimilar = findViewById(R.id.rvSimilar);
        similarMovies = new ArrayList<>();

        MovieAdapter movieAdapter = new MovieAdapter(this, similarMovies);

        rvSimilar.setAdapter(movieAdapter);
        rvSimilar.setLayoutManager(new LinearLayoutManager(this));


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    similarMovies.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Movies: " + similarMovies.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception",e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
    }
}