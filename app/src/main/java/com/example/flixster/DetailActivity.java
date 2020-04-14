package com.example.flixster;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.databinding.ActivityDetailBinding;
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

    // Store the binding
    private ActivityDetailBinding binding;

    TextView tvTitle;
    TextView tvOverview;
    TextView tvOverviewStatic;
    TextView tvReleaseDate;
    TextView tvRuntime;
    TextView tvRating;
    RatingBar ratingBar;
    YouTubePlayerView youTubePlayerView;
    YouTubePlayer ytPlayer;
    ImageView ivBackdrop;
    ChipGroup chipGenres;

    boolean videoLoaded = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the content view (replacing `setContentView`)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        final Context context = this;

        tvTitle = binding.tvTitle;
        tvOverview = binding.tvOverview;
        tvOverviewStatic = binding.tvOverviewStatic;
        tvReleaseDate = binding.tvReleaseDate;
        tvRuntime = binding.tvRuntime;
        tvRating = binding.tvRating;
        ratingBar = binding.ratingBar;
        youTubePlayerView = binding.player;
        ivBackdrop = binding.ivBackdrop;
        chipGenres = binding.chipGenres;

        final Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating((float) movie.getVoteAverage());

        String tempRating = String.format("%.1f/10", movie.getVoteAverage());
        SpannableStringBuilder spannable = new SpannableStringBuilder("User rating:  " + tempRating);
        spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent)), spannable.length()-tempRating.length(), spannable.length(), 0);
        tvRating.setText(spannable);
        tvReleaseDate.setText(String.format("Release date:  %s", movie.getReleaseDate()));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(MOVIE_DETAILS, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONObject movieDetails = json.jsonObject;
                    int runtime = movieDetails.getInt("runtime");
                    if (runtime < 60) {
                        tvRuntime.setText(String.format("Runtime:  %dm", runtime));
                    } else {
                        tvRuntime.setText(String.format("Runtime:  %dh %dm", runtime / 60, runtime % 60));
                    }

                    JSONArray genres = movieDetails.getJSONArray("genres");

                    for (int i = 0; i < genres.length(); i++){
                        Chip chip = new Chip(context);
                        chip.setText(genres.getJSONObject(i).getString("name"));
                        chip.setClickable(false);
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

                ytPlayer = youTubePlayer;
                if (rating > MovieAdapter.RATING) {
                    ytPlayer.loadVideo(youTubeKey);
                } else {
                    ytPlayer.cueVideo(youTubeKey);
                }
                videoLoaded = true;

                ytPlayer.setOnFullscreenListener(new YouTubePlayer.OnFullscreenListener() {
                    @Override
                    public void onFullscreen(boolean b) {
                        Log.d(TAG, "onFullscreenMinimize");
                        if (!b) {
                            setVisible();
                        }
                    }
                });
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "onInitializationFailure");
            }
        });
    }

    @Override //reconfigure display properties on screen rotation
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        //Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            setVisible();
            if (videoLoaded) {
                ytPlayer.setFullscreen(false);
            } else {
                ivBackdrop.setVisibility(View.VISIBLE);
            }
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE && videoLoaded)
        {
            setGone();
            ytPlayer.setFullscreen(true);
        }
    }

    private void setGone() {
        tvTitle.setVisibility(View.GONE);
        tvOverview.setVisibility(View.GONE);
        tvOverviewStatic.setVisibility(View.GONE);
        tvReleaseDate.setVisibility(View.GONE);
        tvRuntime.setVisibility(View.GONE);
        ratingBar.setVisibility(View.GONE);
        chipGenres.setVisibility(View.GONE);
    }

    private void setVisible() {
        tvTitle.setVisibility(View.VISIBLE);
        tvOverview.setVisibility(View.VISIBLE);
        tvOverviewStatic.setVisibility(View.VISIBLE);
        tvReleaseDate.setVisibility(View.VISIBLE);
        tvRuntime.setVisibility(View.VISIBLE);
        ratingBar.setVisibility(View.VISIBLE);
        chipGenres.setVisibility(View.VISIBLE);
    }
}
