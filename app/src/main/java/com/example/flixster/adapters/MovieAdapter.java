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
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.flixster.DetailActivity;
import com.example.flixster.R;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Movie> movies;
    public static final String TAG = "MovieAdapter";
    public static final double RATING = 7.5;
    public static final int POPULAR = 0, LESS_POPULAR = 1;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    // Inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;

        if (viewType == POPULAR && context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            viewHolder = new PopularMovieViewHolder(inflater.inflate(R.layout.item_popular_movie, parent, false));
        } else {
            viewHolder = new MovieViewHolder(inflater.inflate(R.layout.item_movie, parent, false));
        }
        return viewHolder;
    }

    // Populate the data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder " + position);
        // Get the movie at the passed in position
        Movie movie = movies.get(position);
        // Bind the movie data into the View Holder
        if (holder.getItemViewType() == POPULAR && context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            PopularMovieViewHolder vh = (PopularMovieViewHolder) holder;
            configurePopularMovieViewHolder(vh, movie);
        } else {
            MovieViewHolder vh = (MovieViewHolder) holder;
            configureMovieViewHolder(vh, movie);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (movies.get(position).getVoteAverage() >= RATING) {
            return POPULAR;
        } else {
            return LESS_POPULAR;
        }
    }

    // Return the total count of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {

        CardView container;
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            container = itemView.findViewById(R.id.container);
        }
    }

    public class PopularMovieViewHolder extends RecyclerView.ViewHolder {

        CardView container;
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivPoster;

        public PopularMovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitlePopular);
            tvOverview = itemView.findViewById(R.id.tvOverviewPopular);
            ivPoster = itemView.findViewById(R.id.ivPosterPopular);
            container = itemView.findViewById(R.id.container);
        }
    }

    private void configurePopularMovieViewHolder(PopularMovieViewHolder vh, final Movie movie) {
        vh.tvTitle.setText(movie.getTitle());
        vh.tvOverview.setText(movie.getOverview());
        String imageUrl = movie.getBackdropPath();
        int radius = 2;
        int placeholder = R.drawable.placeholder_land;

        Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .transform(new RoundedCorners(radius))
                .into(vh.ivPoster);

        vh.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("movie", Parcels.wrap(movie));
                context.startActivity(i);
            }
        });
    }

    private void configureMovieViewHolder(MovieViewHolder vh, final Movie movie) {
        vh.tvTitle.setText(movie.getTitle());
        vh.tvOverview.setText(movie.getOverview());
        String imageUrl;
        int radius = 2;
        int placeholder;

        // Configuration changes for images
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageUrl = movie.getBackdropPath();
            placeholder = R.drawable.placeholder_land;
        } else {
            imageUrl = movie.getPosterPath();
            placeholder = R.drawable.placeholder_port;
        }

        Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholder)
                .error(placeholder)
                .transform(new RoundedCorners(radius))
                .into(vh.ivPoster);

        vh.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("movie", Parcels.wrap(movie));
                context.startActivity(i);
            }
        });
    }
}
