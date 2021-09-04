package com.example.lukash

import android.graphics.drawable.Drawable
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.lukash.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Thread.sleep
import javax.sql.DataSource

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    var gifList = mutableListOf<Drawable>()

    val connectionURL = "https://developerslife.ru/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE

        val retrofit = Retrofit.Builder()
            .baseUrl(connectionURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: GifApi = retrofit.create(GifApi::class.java)

        val call: Call<ApiResponse> = service.get("latest", 0, true)

        val callback = object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    val urlGIF: String? = result?.result?.get(0)?.get("gifURL")!!
                    Log.d("TEST", urlGIF!!)
                    loadGif(urlGIF)
                    binding.textDescr.text = result?.result?.get(0)?.get("description")!!
                    binding.buttonNext.isClickable = true
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.d("TEST", "Receive data from server problem")
                Log.e("TEST", t.localizedMessage)
            }
        }

        //call.enqueue(callback)

        binding.buttonNext.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                binding.buttonNext.isClickable = false

                Log.d("TEST", "click button")

                call.clone().enqueue(callback)
            }
        })

    }

    fun loadGif(url: String) {
        Glide.with(applicationContext)
            .load(url)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("TEST", "unsuccess loaded")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("TEST", "success loaded")
                    binding.progressBar.visibility = View.INVISIBLE
                    return false
                }
            })
            .into(binding.imageGif)
    }

}