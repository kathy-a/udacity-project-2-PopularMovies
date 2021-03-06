package com.udacity.popularmovies

import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.WorkerThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udacity.popularmovies.database.MovieEntity
import com.udacity.popularmovies.model.Result
import com.udacity.popularmovies.network.AssertConnectivity
import com.udacity.popularmovies.network.MovieService.buildPosterPathUrl
import com.udacity.popularmovies.network.MovieService.retrofitInstance
import com.udacity.popularmovies.network.TheMovieDBService
import com.udacity.popularmovies.ui.MoviesViewAdapter
import com.udacity.popularmovies.utilies.App
import com.udacity.popularmovies.viewmodel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class MainActivity : AppCompatActivity() {
    var mSortOrder = "popularity.desc"
    lateinit var mRecyclerView: RecyclerView
    private var mAdapter: MoviesViewAdapter? = null
    private var movieData = ArrayList<MovieEntity>()
    private var mViewModel: MainViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.recycler_MainActivity)

        mViewModel = ViewModelProviders.of(this)
                .get(MainViewModel::class.java)

        // ADD LISTENER TO LIVE DATA
        mViewModel?.initialMovieData?.observe(this, Observer {
            initRecyclerView(it as ArrayList<Result>)
        })

        AssertConnectivity(this@MainActivity)
        if (AssertConnectivity.isOnline())
            CoroutineScope(Dispatchers.IO).launch { callWebService() }
        else {
            AssertConnectivity.errorConnectMessage(App.getAppResources().getString(R.string.error_connection_themoviedb))

        }

    }


    @WorkerThread
    suspend fun callWebService() {
        val service = retrofitInstance!!.create(TheMovieDBService::class.java)
        val movieList: ArrayList<Result>? = service.getData(API_KEY, mSortOrder)?.results

        // SAVE MOVIE LIST IN INITIAL MOVIE DATA IF THERE'S RESPONSE FOR THE LIST
        if (movieList != null) {
            //ADD THE FULL MOVIE ULR POSTER PATH
            for (i in 0 until movieList.size){
                var posterPath: String = movieList[i].posterPath
                var currentPoster: String = buildPosterPathUrl(posterPath).toString()
                movieList[i].posterPath = currentPoster
            }
            mViewModel?.initialMovieData?.postValue(movieList)
        }
    }


    private fun initViewModel() {
        // LISTENER TO LOCAL DATA
        var movieObserver: Observer<List<MovieEntity?>?> = Observer {
            if (mAdapter == null) {
                mAdapter = MoviesViewAdapter(this@MainActivity, it as ArrayList<MovieEntity>?, true)
                mRecyclerView?.adapter = mAdapter
            } else {
                mAdapter!!.notifyDataSetChanged()
            }
        }

        mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mViewModel!!.localMovieData.observe(this, movieObserver)
    }

    // Display Menu layout created
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_sort_order, menu)
        return true
    }

    // If movie sort order preference is modified, send the request to themoviedb.org and display response
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_most_popular) {
            mSortOrder = "popularity.desc"
        } else if (item.itemId == R.id.item_top_rated) {
            mSortOrder = "vote_count.desc"
        }

        if (item.itemId == R.id.item_favorites) {
            initLocalRecyclerView()
            initViewModel()
        }else{
            if (AssertConnectivity.isOnline())
                CoroutineScope(Dispatchers.IO).launch { callWebService() }
            else
                AssertConnectivity.errorConnectMessage(App.getAppResources().getString(R.string.error_connection_themoviedb))
        }

        return true
    }

    // Display movie poster path via recyclerview
    private fun initRecyclerView(movieList: ArrayList<Result>) {
        mAdapter = MoviesViewAdapter(this, movieList)
        if (mAdapter != null) mRecyclerView?.setAdapter(mAdapter)
    }

    private fun initLocalRecyclerView() {
        mAdapter = null
        mRecyclerView.layoutManager = GridLayoutManager(this, 2)
    }



    companion object {
        private val API_KEY = App.getAppResources().getString(R.string.movie_db_api_key)
        private const val TAG = "MainActivity"
    }
}


