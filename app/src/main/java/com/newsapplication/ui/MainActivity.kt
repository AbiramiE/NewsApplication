package com.newsapplication.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.newsapplication.R
import com.newsapplication.databinding.ActivityMainBinding
import com.newsapplication.model.NewsArticle
import com.newsapplication.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val newsViewModel: NewsViewModel by viewModels()
    lateinit var newsAdapter : NewsAdapter
    lateinit var binding: ActivityMainBinding
    lateinit var toolbar : Toolbar
    var newsList = mutableListOf<NewsArticle>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar.toolbar
        setUpToolBar()

        binding.rvNewsList.apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                RecyclerView.VERTICAL, false
            )
            setHasFixedSize(true)
            setItemViewCacheSize(20)
            newsAdapter = NewsAdapter(this@MainActivity, newsList,newsViewModel)
            adapter = newsAdapter
        }

        newsViewModel.newsList.observe(this, {
            binding.swipeRefreshLayout.isRefreshing = false
            newsList.clear()
            newsList.addAll(it.newsArticle!!)
            newsAdapter.notifyDataSetChanged()
        })

        newsViewModel.errorMessage.observe(this, {
            binding.swipeRefreshLayout.isRefreshing = false
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        newsViewModel.loading.observe(this, Observer {
            binding.swipeRefreshLayout.isRefreshing = false
            if (it) {
                binding.progressCircular.visibility = View.VISIBLE
            } else {
                binding.progressCircular.visibility = View.GONE
            }
        })

        if (checkForInternet(this)) {
            newsViewModel.getAllNewsList()
        } else {
            binding.swipeRefreshLayout.isRefreshing = false
            Toast.makeText(this, "No Internet Connection..", Toast.LENGTH_SHORT).show()
        }
        binding.swipeRefreshLayout.setOnRefreshListener(refreshListener);

    }

    private val refreshListener = SwipeRefreshLayout.OnRefreshListener {
        binding.swipeRefreshLayout.isRefreshing = true
        if (checkForInternet(this)) {
            newsViewModel.getAllNewsList()
        } else {
            Toast.makeText(this, "No Internet Connection..", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.toolbar.toolbar as Toolbar)
        val actionBar = supportActionBar
        actionBar?.let { toolbar ->
            toolbar.setDisplayHomeAsUpEnabled(false)
            toolbar.setDisplayShowHomeEnabled(false)
            toolbar.title = null
            toolbar.setIcon(null)
            val mScreenTitleTV = findViewById<TextView>(R.id.tv_toolbar_title)
            mScreenTitleTV.text = "TOP NEWS"
        }
    }

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}