package com.udacity.popularmovies;

import android.content.Context;
import android.graphics.Movie;
import android.util.Log;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.udacity.popularmovies.database.AppDatabase;
import com.udacity.popularmovies.database.MovieDao;
import com.udacity.popularmovies.database.MovieEntity;
import com.udacity.popularmovies.utilies.SampleData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {
    private static final String TAG = "Junit";
    private AppDatabase db;
    private MovieDao dao;

    @Before
    public void createDb() {
        Context context = InstrumentationRegistry.getTargetContext();

        // Create db in memory
        db = Room.inMemoryDatabaseBuilder(context,
                AppDatabase.class).build();

        dao = db.movieDao();
        Log.d(TAG, "Create database");
    }

    @After
    public void closeDb() {
        db.close();
        Log.d(TAG, "Close database");
    }

    @Test
    public void createAndRetrieveMovie() {
        dao.insertAll(SampleData.getSampleMovieData());
        int count = dao.getCount();
        Log.d(TAG, "createAndRetrieveNotes: " + SampleData.getSampleMovieData());
        Log.d(TAG, "createAndRetrieveNotes: count=" + count);
        Log.d(TAG, "createAndRetrieveNotes: count=" + SampleData.getSampleMovieData().size());

        assertEquals(SampleData.getSampleMovieData().size(), count);
    }

    @Test
    public void compareStrings() {
        dao.insertAll(SampleData.getSampleMovieData());
        int original = SampleData.getSampleMovieData().get(0).getId();
        MovieEntity fromDb = dao.getMovieById(0);
        assertEquals(original, fromDb.getId());

    }


}
