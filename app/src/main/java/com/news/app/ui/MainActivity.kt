package com.news.app.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.news.app.R
import com.news.app.db.ArticleDatabase
import com.news.app.repository.NewsRepository

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelProviderFactory = MainViewModelProviderFactory(application, newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory).get(MainViewModel::class.java)
    }
}
