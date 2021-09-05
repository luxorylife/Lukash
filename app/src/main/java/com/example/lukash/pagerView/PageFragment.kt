package com.example.lukash.pagerView

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.lukash.MainActivity
import com.example.lukash.R
import com.example.lukash.entityes.ApiResponse
import com.example.lukash.api.GifApi
import com.example.lukash.databinding.PageFragmentBinding
import com.example.lukash.entityes.GifPrototype
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PageFragment : Fragment() {

    lateinit var binding: PageFragmentBinding

    private var section = "latest"

    val connectionURL = "https://developerslife.ru/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (getArguments() != null) {
            section = arguments?.getString(ARG_SEC) ?: "latest"
        }

        Log.d("TEST", "Current section: " + section)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = PageFragmentBinding.inflate(inflater, container, false)

        binding.progressBar.visibility = View.VISIBLE

        //настраиваем API-запрос
        val retrofit = Retrofit.Builder()
            .baseUrl(connectionURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: GifApi = retrofit.create(GifApi::class.java)

        var call: Call<ApiResponse> = service.get(section, curPage, true)

        val callback = object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    if (result?.result!!.count() > 0) {
                        for (obj in result?.result!!) {
                            if (obj != null) {
                                val urlGIF: String? = obj.get("gifURL")!!
                                val descr: String? = obj.get("description")!!
                                gifList.add(GifPrototype(urlGIF!!, descr!!))
                            }
                        }
                        tryToLoad()
                    } else {
                        loadRefresh()
                        //loadError()
                        binding.buttonNext.isClickable = false
                        binding.buttonPrev.isClickable = false
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Log.d("TEST", "Receive data from server problem")
                Log.e("TEST", t.localizedMessage)
                loadRefresh()
            }
        }

        //вызываем API в первый раз (при загрузке)
        call.enqueue(callback)

        //Обработчики (следующая гифка, предыдущая, повторная попытка (при неудачной загрузке))
        binding.buttonNext.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                binding.buttonNext.isClickable = false
                binding.buttonPrev.isClickable = false
                binding.progressBar.visibility = View.VISIBLE

                Log.d("TEST", "click next button")

                curGif++
                if (curGif < gifList.count()) {
                    tryToLoad()
                } else {
                    //изменяем номер страницы, с которой будем загружать гифку
                    call = service.get(section, ++curPage, true)
                    call.clone().enqueue(callback)
                }
            }
        })

        binding.buttonPrev.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                binding.buttonNext.isClickable = false
                binding.buttonPrev.isClickable = false
                binding.progressBar.visibility = View.VISIBLE

                Log.d("TEST", "click prev button")

                curGif--
                tryToLoad()
            }
        })

        binding.buttonRefresh.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                binding.buttonNext.isClickable = false
                binding.buttonPrev.isClickable = false
                binding.buttonRefresh.visibility = View.INVISIBLE
                binding.progressBar.visibility = View.VISIBLE

                Log.d("TEST", "click refresh button")

                binding.textDescr.text = "Томительное ожидание...ъ"
                lifecycleScope.launch {
                    delay(500)
                    call.clone().enqueue(callback)
                }

            }
        })

        return binding.root
    }

    //возобновляем состояние фрагмента
    override fun onResume() {
        super.onResume()
        Log.d("TEST", "SDJIFHGUHWBGUOWRG " + section)

        if (section.equals("latest")) {
            //Log.d("TEST", "latest")
            gifList = MainActivity.gifLatestList
            curPage = MainActivity.curLatestPage
            curGif = MainActivity.curlatestGif
        } else if (section.equals("hot")) {
            //Log.d("TEST", "hot")
            gifList = MainActivity.gifHotList
            curPage = MainActivity.curHotPage
            curGif = MainActivity.curHotGif
        } else {
            //Log.d("TEST", "best")
            gifList = MainActivity.gifBestList
            curPage = MainActivity.curBestPage
            curGif = MainActivity.curBestGif
        }
    }

    //запоминаем состояние фрагмента
    override fun onPause() {
        super.onPause()

        if (section.equals("latest")) {
            MainActivity.gifLatestList = gifList
            MainActivity.curLatestPage = curPage
            MainActivity.curlatestGif = curGif
        } else if (section.equals("hot")) {
            MainActivity.gifHotList = gifList
            MainActivity.curHotPage = curPage
            MainActivity.curHotGif = curGif
        } else {
            MainActivity.gifBestList = gifList
            MainActivity.curBestPage = curPage
            MainActivity.curBestGif = curGif
        }
    }

    //загрузка гифки из сохраненного списка
    private fun tryToLoad() {
        if (gifList.get(curGif).urlGIF != null) {
            Log.d("TEST", gifList.get(curGif).urlGIF!!)
            loadGif(gifList.get(curGif).urlGIF)
        } else {
            Log.d("TEST", "urlGIF нет в JSON?")
            loadError()
        }
        if (gifList.get(curGif).descr != null) {
            Log.d("TEST", gifList.get(curGif).descr!!)
            binding.textDescr.text = gifList.get(curGif).descr!!
        } else {
            Log.d("TEST", "DEscription нету в JSOn?")
            binding.textDescr.text = "Описание не пришло =("
        }
        Log.d("TEST", "page = " + curPage + ", gif №" + curGif)
        binding.buttonNext.isClickable = true
        binding.buttonPrev.isClickable = curGif > 0
    }

    //если не удалось получить ответ от API
    private fun loadRefresh() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.textDescr.text = "Ошибка загрузки - повторите попытку!"
        binding.buttonRefresh.visibility = View.VISIBLE
    }

    //если в ответе не было ссылки на гифку
    private fun loadError() {
        Glide.with(requireContext())
            .load(R.drawable.ic_baseline_error_24)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("TEST", "unsuccess error loaded")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("TEST", "error loaded")
                    binding.progressBar.visibility = View.INVISIBLE
                    return false
                }
            })
            .into(binding.imageGif)
    }

    //загрузка гифки
    private fun loadGif(url: String) {
        Glide.with(requireContext())
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

    companion object {
        const val ARG_SEC = "ARG_SEC"

        var gifList = MainActivity.gifLatestList

        var curPage = MainActivity.curLatestPage
        var curGif = MainActivity.curlatestGif

        fun newInstance(section: String): PageFragment {
            val args = Bundle()
            args.putString(ARG_SEC, section)

            val fragment = PageFragment()
            fragment.arguments = args
            return fragment
        }
    }
}