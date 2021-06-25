package com.example.flixster;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.CreditsAdapter;
import com.example.flixster.models.Movie;
import com.example.flixster.models.MovieTrailerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class MovieDetailsActivity extends AppCompatActivity {
    public static final String KEY_ITEM_TEXT = "item_text";
    Movie movie;

    TextView tvTitleDetails;
    TextView tvOverviewDetails;
    RatingBar rbVoteAverage;
    TextView tvCast;
    TextView tvDirector;
    TextView tvDirectorName;
    RecyclerView rvCredits;
    ImageView ivBackdrop;
    ImageView ivPlay;
    Button btnSimilar;

    List<String> cast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // unwrap the movie passed in via intent
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for %s", movie.getTitle()));

        tvTitleDetails = findViewById(R.id.tvTitleDetails);
        tvOverviewDetails = findViewById(R.id.tvOverviewDetails);
        rbVoteAverage = findViewById(R.id.rbVoteAverage);
        rvCredits = findViewById(R.id.rvCredits);
        tvDirector = findViewById(R.id.tvDirector);
        tvDirectorName = findViewById(R.id.tvDirectorName);
        tvCast = findViewById(R.id.tvCast);
        ivBackdrop = findViewById(R.id.ivBackdrop);
        ivPlay = findViewById(R.id.ivPlay);
        btnSimilar = findViewById(R.id.btnSimilar);

        cast = new ArrayList<>();
        // set the title and overview
        tvTitleDetails.setText(movie.getTitle());
        tvOverviewDetails.setText(movie.getOverview());
        tvOverviewDetails.setMovementMethod(new ScrollingMovementMethod());

        String CREDITS_URL = String.format("https://api.themoviedb.org/3/movie/%s/credits?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed", movie.getId());
        String VIDEO_URL = String.format("https://api.themoviedb.org/3/movie/%s/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed",movie.getId());
        String TAG = "MovieDetailsActivity";

        CreditsAdapter creditsAdapter = new CreditsAdapter(this, cast);
        rvCredits.setAdapter(creditsAdapter);
        rvCredits.setLayoutManager(new LinearLayoutManager(this));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(CREDITS_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray result = jsonObject.getJSONArray("cast");
                    JSONArray result2 = jsonObject.getJSONArray("crew");
                    Log.i(TAG, "Cast: " + result.toString());
                    for(int j = 0; j < result.length(); j++){
                        String temp = result.getJSONObject(j).getString("name");
                        Log.i(TAG, "name: " + temp);
                        cast.add(temp);
                    }
                    creditsAdapter.notifyDataSetChanged();


                    for(int k = 0; k < result2.length(); k++){
                        String temp = result2.getJSONObject(k).getString("job");
                        if(temp.equals("Director")){
                            String name = result2.getJSONObject(k).getString("name");
                            tvDirectorName.setText(name);
                            Log.i(TAG, "director: " + name);
                            break;
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });

        client.get(VIDEO_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Headers headers, JSON json) {
                JSONObject jsonObject = json.jsonObject;
                try{
                    JSONArray result = jsonObject.getJSONArray("results");
                    for(int j = 0; j < result.length(); j++){
                        String temp = result.getJSONObject(j).getString("type");
                        if(temp.equals("Official Trailer") || temp.equals("Trailer")){
                            String name = result.getJSONObject(j).getString("key");
                            movie.setVideoId(name);
                            break;
                        }
                    }

                }catch (JSONException e){
                    Log.e(TAG, "Hit json exception", e);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Headers headers, String s, Throwable throwable) {

            }
        });
        // set vote average
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage / 2.0f);

        Glide.with(ivBackdrop)
                .load(movie.getBackdropPath())
                .placeholder( R.drawable.flicks_backdrop_placeholder)
                .transform(new RoundedCorners(30))
                .into(ivBackdrop);

        Glide.with(ivPlay)
                .load(R.drawable.play_btn)
                .into(ivPlay);

        btnSimilar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create intent for new activity
                Intent intent = new Intent(MovieDetailsActivity.this, SimilarActivity.class);

                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));

                // show the activity
                MovieDetailsActivity.this.startActivity(intent);
            }
        });

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create intent for new activity
                Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);

                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                intent.putExtra("Type", true);

                // show the activity
                MovieDetailsActivity.this.startActivity(intent);
            }
        });
    }
}