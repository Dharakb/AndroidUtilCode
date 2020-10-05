package com.androidutilcode

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.androidutilcode.api.ResponseUtils
import com.androidutilcode.database.SampleDatabase
import com.androidutilcode.databinding.ActivityMainBinding
import com.androidutilcode.recyclerViewAdapter.ThemesNameListAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : BaseActivity(), CoroutineScope {

    private lateinit var binding: ActivityMainBinding
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.IO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.adapter = ThemesNameListAdapter()
        title = getString(R.string.app_name)
        setSupportActionBar(toolbar)

        showProgressDialog()
        apiRepository.getMasterData().observe(this@MainActivity, Observer {
            hideProgressDialog()
            if (ResponseUtils.checkIfValidResponse(
                    this,
                    it,
                    true
                ) == ResponseUtils.Companion.Status.SUCCESS
            ) {
                binding.adapter?.submitList(it.data?.data?.themes)
                binding.adapter?.notifyDataSetChanged()

                launch {
                    withContext(Dispatchers.IO) {
                        it.data?.data?.themes?.let { it1 ->
                            SampleDatabase.getDatabase(this@MainActivity).themesDao().insert(it1)
                        }
                    }
                }
            }
        })
    }

    override fun getTAGName() = this::class.java.simpleName

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
