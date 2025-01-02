package com.example.newsly

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.carousel.CarouselSnapHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class MainActivity : AppCompatActivity() {
    private lateinit var adapter: NewsAdapter
    private val articles = mutableListOf<Article>()
    private var pageNum: Int = 1
    private var totalResult: Int = -1
    private val pageSize = 20  // Number of articles per page (API default)
    private var scrollCount = 0 // To track scrolls

    lateinit var recyclerView : RecyclerView
    lateinit var progressBar : ProgressBar

    //add
    private var mInterstitialAd: InterstitialAd? = null
    private final val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //show add
        initializeAdMob()

        adapter = NewsAdapter(this@MainActivity, articles)
        recyclerView = findViewById<RecyclerView>(R.id.newsList)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = CarouselLayoutManager()
        CarouselSnapHelper().attachToRecyclerView(recyclerView)

         progressBar = findViewById<ProgressBar>(R.id.progress)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    scrollCount++
                    if (scrollCount % 5 == 0) { // Show ad after every 5 scrolls
                        showAd()
                    }
                }
            }
        })

        // Start fetching all news
        fetchAllNews()
    }
    private fun initializeAdMob() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
                mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdClicked() {
                        // Called when a click is recorded for an ad.
                        Log.d(TAG, "Ad was clicked.")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        // Called when ad is dismissed.
                        Log.d(TAG, "Ad dismissed fullscreen content.")
                        mInterstitialAd = null
                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        // Called when ad fails to show.
                        Log.e(TAG, "Ad failed to show fullscreen content.")
                        mInterstitialAd = null
                    }

                    override fun onAdImpression() {
                        // Called when an impression is recorded for an ad.
                        Log.d(TAG, "Ad recorded an impression.")
                    }

                    override fun onAdShowedFullScreenContent() {
                        // Called when ad is shown.
                        Log.d(TAG, "Ad showed fullscreen content.")
                    }
                }
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(this@MainActivity)
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, "Failed to load ad: ${adError.message}")
                mInterstitialAd = null
            }
        })

    }
    private fun fetchAllNews() {
        // Fetch the first page to get totalResults
        fetchPage(pageNum) { success ->
            if (success && totalResult > pageSize) {
                // Continue fetching remaining pages
                val totalPages = (totalResult + pageSize - 1) / pageSize
                for (page in 2..totalPages) {
                    fetchPage(page)
                }
            }
        }
    }

    private fun fetchPage(page: Int, onFirstPageLoaded: ((Boolean) -> Unit)? = null) {
        val news = NewsInterface.NewsService.newsInstance.getHeadlines("us", page)

        news.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val new = response.body()
                if (new != null) {
                    if (page == 1) {
                        totalResult = new.totalResults  // Set total results from the first page
                        onFirstPageLoaded?.invoke(true)
                    }
                    articles.addAll(new.articles)
                    adapter.notifyDataSetChanged()
                    if (articles.size != 0) {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                    }
                } else if (page == 1) {
                    onFirstPageLoaded?.invoke(false)
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("ashuBitWise", "Error in fetching news", t)
                if (page == 1) {
                    onFirstPageLoaded?.invoke(false)
                }
            }
        })
    }
    private fun showAd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
            initializeAdMob() // Reload the ad
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // Clean up AdMob resources
        mInterstitialAd = null
    }
}
