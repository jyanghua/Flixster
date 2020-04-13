package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {

    private static final String YOUTUBE_API_KEY = "AIzaSyD41Ku8VDtmCqIMypnjshW8uzegRoaCfqM";
    private static final String SOURCE = "YouTube";
    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String MOVIE_DETAILS = "https://api.themoviedb.org/3/movie/%d?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "DetailActivity";

    TextView tvTitle;
    TextView tvOverview;
    TextView tvReleaseDate;
    TextView tvRuntime;
    RatingBar ratingBar;
    YouTubePlayerView youTubePlayerView;
    ImageView ivBackdrop;
    ChipGroup chipGenres;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final Context context = this;

        tvTitle = findViewById(R.id.tvTitle);
        tvOverview = findViewById(R.id.tvOverview);
        tvReleaseDate = findViewById(R.id.tvReleaseDate);
        tvRuntime = findViewById(R.id.tvRuntime);
        ratingBar = findViewById(R.id.ratingBar);
        youTubePlayerView = findViewById(R.id.player);
        ivBackdrop = findViewById(R.id.ivBackdrop);
        chipGenres = findViewById(R.id.chipGenres);

        final Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating((float) movie.getVoteAverage());
        tvReleaseDate.setText(movie.getReleaseDate());


        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(MOVIE_DETAILS, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONObject movieDetails = json.jsonObject;
                    int runtime = movieDetails.getInt("runtime");
                    if (runtime < 60) {
                        tvRuntime.setText(String.format("%dm", runtime));
                    } else {
                        tvRuntime.setText(String.format("%dh %dm", runtime / 60, runtime % 60));
                    }

                    JSONArray genres = movieDetails.getJSONArray("genres");

                    for (int i = 0; i < genres.length(); i++){
                        Chip chip = new Chip(context);
                        chip.setText(genres.getJSONObject(i).getString("name"));
                        chipGenres.addView(chip);
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse JSON");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Failed to parse JSON");
            }
        });

        client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if (results.length() > 0 && results.getJSONObject(0).getString("site").equalsIgnoreCase(SOURCE)) {
                        String youTubeKey = results.getJSONObject(0).getString("key");
                        Log.d(TAG, youTubeKey);
                        initializeYouTube(youTubeKey, movie.getVoteAverage());
                    } else {
                        // Handles the poster if there is no YouTube video available.
                        int placeholder = R.drawable.placeholder_land;
                        ivBackdrop.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(movie.getBackdropPath())
                                .placeholder(placeholder)
                                .error(placeholder)
                                .into(ivBackdrop);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse JSON");
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "Failed to parse JSON");
            }
        });


    }

    private void initializeYouTube(final String youTubeKey, final double rating){


        youTubePlayerView.initialize(YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "onInitializationSuccess");

                if (rating > MovieAdapter.RATING) {
                    youTubePlayer.loadVideo(youTubeKey);
                } else {
                    youTubePlayer.cueVideo(youTubeKey);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "onInitializationFailure");
            }
        });
    }
}
