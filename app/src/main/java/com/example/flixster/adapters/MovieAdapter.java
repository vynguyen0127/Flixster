package com.example.flixster.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.MovieDetailsActivity;
import com.example.flixster.R;
import com.example.flixster.models.Movie;
import com.example.flixster.models.MovieTrailerActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import okhttp3.Headers;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    Context context;
    List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies){
        this.context = context;
        this.movies = movies;
    }


    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MovieAdapter", "onCreateViewHolder");
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    // Involves populating the data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("MovieAdapter", "onBindViewHolder" + position);
        // Get the movie at the passed in position
        Movie movie = movies.get(position);

        // Bind movie data into the VH
        holder.bind(movie);


    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTitle;
        TextView tvOverview;
        ImageView ivPoster;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivPoster = itemView.findViewById(R.id.ivPoster);

            itemView.setOnClickListener(this);

        }


        public void bind(Movie movie) {

            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());

            String imageURL;

            // if phone is in landscape mode
            // then imageURL = backdrop img
            // else imageURL = poster img
            int imgID;

            if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                imageURL = movie.getBackdropPath();
                imgID = R.drawable.flicks_backdrop_placeholder;
            }
            else{
                imageURL = movie.getPosterPath();
                imgID = R.drawable.flicks_movie_placeholder;
            }

            Glide.with(context)
                    .load(imageURL)
                    .placeholder(imgID)
                    .transform(new RoundedCorners(30))
                    .into(ivPoster);

            String VIDEO_URL = String.format("https://api.themoviedb.org/3/movie/%s/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed",movie.getId());
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(VIDEO_URL, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Headers headers, JSON json) {
                    JSONObject jsonObject = json.jsonObject;
                    try{
                        JSONArray result = jsonObject.getJSONArray("results");
                        for(int j = 0; j < result.length(); j++){
                            String type = result.getJSONObject(j).getString("type");
                            String site = result.getJSONObject(j).getString("site");
                            if((type.equals("Official Trailer" ) || type.equals("Trailer") ) && site.equals("YouTube")){
                                String name = result.getJSONObject(j).getString("key");
                                movie.setVideoId(name);
                                break;
                            }
                        }

                    }catch (JSONException e){
                        Log.e("MovieAdapter", "Hit json exception", e);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Headers headers, String s, Throwable throwable) {
                    Log.d("MovieAdapter", "onFailure");
                }
            });

            ivPoster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

                        // create intent for new activity
                        Intent intent = new Intent(context, MovieTrailerActivity.class);

                        // serialize the movie using parceler, use its short name as a key
                        intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));

                        // show the activity
                        context.startActivity(intent);
                    }
                }
            });

        }

        @Override
        public void onClick(View v) {
            // gets item position
            int position = getAdapterPosition();

            // make sure the position is valid
            if(position != RecyclerView.NO_POSITION){
                // get the movie at the position
                Movie movie = movies.get(position);

                // create intent for new activity
                Intent intent = new Intent(context, MovieDetailsActivity.class);

                // serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));

                // show the activity
                context.startActivity(intent);
            }
        }
    }

}
