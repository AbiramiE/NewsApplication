package com.newsapplication.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.newsapplication.R
import com.newsapplication.databinding.ActivityNewsDetailBinding
import com.newsapplication.model.NewsArticle
import com.newsapplication.others.Constants
import com.newsapplication.viewmodel.NewsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class NewsDetailActivity : AppCompatActivity() {

    lateinit var binding: ActivityNewsDetailBinding
    private val newsViewModel: NewsViewModel by viewModels()

    lateinit var newsDetails: NewsArticle
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.toolbar.toolbar
        setUpToolBar()

        newsDetails = intent.getSerializableExtra(Constants.NEWS_DATA) as NewsArticle

        newsViewModel.getFavorite(
            newsDetails.source?.name.toString()
        )

        val circularProgressDrawable = CircularProgressDrawable(this)
        circularProgressDrawable.strokeWidth = 3f
        circularProgressDrawable.centerRadius = 25f
        circularProgressDrawable.start()

        Glide.with(this)
            .load(newsDetails.urlToImage)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.no_image)
            .into(binding.ivNews)

        val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        val date: Date = sdf.parse(newsDetails.publishedAt!!)!!
        val publishedAt = SimpleDateFormat("dd-MM-yyyy HH:mm aa", Locale.ENGLISH).format(date)

        binding.tvDatePublished.text = publishedAt
        binding.tvDescription.text = newsDetails.content

        binding.ivShare.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, newsDetails.url)
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Share To"))
        }

        binding.ivFavorite.setOnClickListener {
            newsViewModel.setFavorite(
                newsDetails.source?.name.toString(),
                !newsViewModel.getFavorite(newsDetails.source?.name.toString())
            )
        }

        newsViewModel.favoriteNewsList.observe(this, {
            if (it)
                binding.ivFavorite.setImageResource(R.drawable.ic_marked_favorite)
            else
                binding.ivFavorite.setImageResource(R.drawable.ic_favorite)
        })
    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.toolbar.toolbar as Toolbar)
        val actionBar = supportActionBar
        actionBar?.let { toolbar ->
            toolbar.setDisplayHomeAsUpEnabled(true)
            toolbar.setDisplayShowHomeEnabled(true)
            toolbar.title = null
            toolbar.setIcon(null)
            val mScreenTitleTV = findViewById<TextView>(R.id.tv_toolbar_title)
            mScreenTitleTV.text = "NEWS DETAILS"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }
}