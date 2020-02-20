package com.udacity.popularmovies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private static final String MOVIE_ORIGINAL_TITLE = "movieOriginalTitle" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView movieTitle = findViewById(R.id.text_movie_title);
        ImageView moviePoster = findViewById(R.id.image_movie_poster);
        TextView releaseDate = findViewById(R.id.text_release_date);
        TextView userRating = findViewById(R.id.text_user_rating);
        TextView moviePlotSynopsis = findViewById(R.id.text_movie_plot_synopsis);

        Intent intent = getIntent();

        if((intent != null) && (intent.hasExtra(MOVIE_ORIGINAL_TITLE))){

            movieTitle.setText(intent.getStringExtra("movieOriginalTitle"));

            Picasso.with(this)
                    .load(intent.getStringExtra("moviePoster"))
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(moviePoster);

            releaseDate.setText(intent.getStringExtra("movieReleaseDate"));
            userRating.setText(intent.getStringExtra("movieUserRating"));
            moviePlotSynopsis.setText(intent.getStringExtra("moviePlotSynopsis"));

        }
    }
}
