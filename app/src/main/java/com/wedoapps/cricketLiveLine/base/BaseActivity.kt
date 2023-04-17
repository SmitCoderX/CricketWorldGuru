package com.wedoapps.cricketLiveLine.base

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.wedoapps.cricketLiveLine.utils.ShowLogToast

abstract class BaseActivity : AppCompatActivity() {

    @LayoutRes
    abstract fun getResource(): Int
    private lateinit var binding: ViewDataBinding
    abstract fun initView()
    abstract fun handleListener()
    abstract fun displayMessage(message: String)
    abstract fun initProgressBar()
    abstract fun showLoadingIndicator(isShow: Boolean)
    var isInternetConnected: Boolean = true

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView(getResource())
    }


    private fun setView(@LayoutRes layoutId: Int) {
        try {
            binding = DataBindingUtil.setContentView(this, layoutId)
            initProgressBar()
            initView()
            handleListener()
        } catch (e: Exception) {
            ShowLogToast.showLog(this.localClassName, "Error" + e.printStackTrace())
//            resToast(e.message!!)
        }
    }


    protected fun <T : ViewDataBinding> getBinding(): T {
        @Suppress("UNCHECKED_CAST")
        return binding as T
    }


}
