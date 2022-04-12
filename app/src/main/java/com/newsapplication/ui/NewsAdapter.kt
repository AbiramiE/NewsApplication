package com.newsapplication.ui

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.newsapplication.R
import com.newsapplication.databinding.NewsListItemLayoutBinding
import com.newsapplication.model.NewsArticle
import com.newsapplication.others.Constants
import com.newsapplication.viewmodel.NewsViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter constructor(private val context: Context,
                              private val newsList: List<NewsArticle>,
                              private val viewModel : NewsViewModel) :
    RecyclerView.Adapter<NewsAdapter.ItemViewHolder>() {

    val newsItemList = this.newsList

    inner class ItemViewHolder
        (val binding: NewsListItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(newsItem: NewsArticle) {
            binding.newsItem = newsItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = NewsListItemLayoutBinding.inflate(layoutInflater, parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val newsItem = newsItemList[position]
        holder.bind(newsItem)

        val sdf: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
        val date: Date = sdf.parse(newsItem.publishedAt!!)!!
        val publishedAt = SimpleDateFormat("dd-MM-yyyy HH:mm aa", Locale.ENGLISH).format(date)

        holder.binding.tvDatePublished.text = publishedAt

        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 3f
        circularProgressDrawable.centerRadius = 25f
        circularProgressDrawable.start()

        Glide.with(context)
            .load(newsItem.urlToImage)
            .placeholder(circularProgressDrawable)
            .error(R.drawable.no_image)
            .into(holder.binding.ivNews)

        holder.binding.itemLayout.setOnClickListener {
            val intent = Intent(context, NewsDetailActivity::class.java)
            intent.putExtra(Constants.NEWS_DATA, newsItem)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}