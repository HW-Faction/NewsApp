package com.news.app.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.news.app.R
import com.news.app.adapters.NewsRecyclerViewAdapter
import com.news.app.ui.MainActivity
import com.news.app.ui.MainViewModel
import com.news.app.util.Constants
import com.news.app.util.Resource
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NewsFragment : Fragment(R.layout.fragment_news) {

    lateinit var viewModel: MainViewModel
    lateinit var savedNewsRecyclerViewAdapter: NewsRecyclerViewAdapter
    lateinit var searchNewsRecyclerViewAdapter: NewsRecyclerViewAdapter
    private val isSearching = MutableLiveData<Boolean>(false)
    private val TAG = "_SAVED_NEWS_FRAG_"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        setupRecyclerView()
        hideProgressBar()

        var job: Job? = null
        etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchNews(editable.toString())
                        showNews(rvSearchNews)
                    } else {
                        showNews(rvSavedNews)
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        searchNewsRecyclerViewAdapter.differ.submitList(newsResponse.articles.toList())
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        savedNewsRecyclerViewAdapter.differ.submitList(newsResponse.articles.toList())
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(activity, "An error occured: $message", Toast.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })

    }

    private fun setupRecyclerView() {
        savedNewsRecyclerViewAdapter = NewsRecyclerViewAdapter(context!!)
        searchNewsRecyclerViewAdapter = NewsRecyclerViewAdapter(context!!)
        rvSavedNews.apply {
            adapter = savedNewsRecyclerViewAdapter
            layoutManager = LinearLayoutManager(activity)
        }
        rvSearchNews.apply {
            adapter = searchNewsRecyclerViewAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun showNews(recyclerView: RecyclerView) {
        if(recyclerView == rvSavedNews) {
            rvSavedNews.visibility = View.VISIBLE
            rvSearchNews.visibility = View.GONE
        } else {
            rvSavedNews.visibility = View.GONE
            rvSearchNews.visibility = View.VISIBLE
        }
    }

    var isLoading = false
}