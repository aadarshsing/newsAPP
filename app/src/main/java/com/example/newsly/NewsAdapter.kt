package com.example.newsly

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class NewsAdapter(val context: Context,val articles : List<Article>,)  :
    RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    class ArticleViewHolder( view: View) : RecyclerView.ViewHolder(view){
        val newsImage = view.findViewById<ImageView>(R.id.newsImage)
        val newsTitle = view.findViewById<TextView>(R.id.newsTitle)
        val newsDescription = view.findViewById<TextView>(R.id.newsDescription)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val layoutInflater = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false)

        return ArticleViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int {
        return  articles.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        Glide.with(context).load(article.urlToImage).into(holder.newsImage)
        holder.newsTitle.text = article.title
        holder.newsDescription.text = article.description

        holder.itemView.setOnClickListener{
            val intent = Intent(context,DetailActivity::class.java)
            intent.putExtra("URL",article.url)
            context.startActivity(intent)
        }

    }
}