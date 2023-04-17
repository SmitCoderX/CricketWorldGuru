package com.wedoapps.cricketLiveLine.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.wedoapps.cricketLiveLine.R
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class ReusedMethod {
    var alertDialog: AlertDialog? = null

    companion object {
        fun getApplicationName(context: Context): String {
            val applicationInfo = context.applicationInfo
            val stringId = applicationInfo.labelRes
            return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
                stringId
            )
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        fun showSnackBar(context: Activity?, message: String?, length: Int) {
            if (context != null) {
                val contextView = context.findViewById<View>(android.R.id.content)
                val snackbar = Snackbar.make(contextView, message!!, Snackbar.LENGTH_SHORT)
                val snackBarView = snackbar.view
                snackBarView.background = context.getDrawable(R.drawable.ic_launcher_background)
                val tv = snackBarView.findViewById<TextView>(R.id.snackbar_text)
                tv.textSize = 12f
                tv.setTextColor(ContextCompat.getColor(context, R.color.white))
                tv.gravity = Gravity.CENTER
                val params = snackBarView.layoutParams as MarginLayoutParams
                params.setMargins(2, params.topMargin, 2, params.bottomMargin + 0)
                snackBarView.layoutParams = params
                snackbar.show()
            }
        }

        fun hideKeyboard(activity: Activity) {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val f = activity.currentFocus
            if (null != f && null != f.windowToken && EditText::class.java.isAssignableFrom(f.javaClass)) imm.hideSoftInputFromWindow(
                f.windowToken,
                0
            ) else activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        }

        fun isNetworkConnected(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }

        fun getMapsAPIKeyFromManifest(context: Context): String? {
            var apiKey: String? = null
            try {
                val ai = context.packageManager
                    .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                val bundle = ai.metaData
                apiKey = bundle.getString("com.google.android.geo.API_KEY")
                //            getPresenter().setGoogleAPIKey(apiKey);
                return apiKey
            } catch (e: PackageManager.NameNotFoundException) {
                ShowLogToast.showLogError(
                        "GETKEYERROR",
                        "Failed to load meta-data, NameNotFound: " + e.message
                )
            } catch (e: NullPointerException) {
                ShowLogToast.showLogError(
                        "GETKEYERROR",
                        "Failed to load meta-data, NullPointer: " + e.message
                )
            }

            return null
        }


        fun updateStatusBarColor(
            context: Activity,
            color: Int,
            position: Int,
        ) { // Color must be in hexadecimal fromat
            val window = context.window
            val view = window.decorView
            if (position == 0 || position == 2) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    view.systemUiVisibility =
                        view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window.clearFlags(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                }
                view.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
            //        window.setStatusBarColor(context.getResources().getColor(color, context.getTheme()));
            window.statusBarColor = ContextCompat.getColor(context, color)
        }

        @SuppressLint("TimberArgCount")
        @JvmStatic
        fun gotoAdsContact(context: Context) {
            val url = "https://api.whatsapp.com/send?phone=" + Constants.adsMobileNumber
            val sendIntent: Intent
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ADS_URL))
                context.startActivity(sendIntent)
            } else {
                sendIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                if (sendIntent.resolveActivity(Objects.requireNonNull(context).packageManager) != null) {
                    context.startActivity(sendIntent)
                } else {
                    Toast.makeText(
                        context.applicationContext,
                        context.resources.getString(R.string.install_whats_app_msg),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }


        @JvmStatic
        fun getImageBitmap(url: String?): Bitmap? {
            var bm: Bitmap? = null
            try {
                val aURL = URL(url)
                val conn = aURL.openConnection()
                conn.connect()
                val `is` = conn.getInputStream()
                val bis = BufferedInputStream(`is`)
                bm = BitmapFactory.decodeStream(bis)
                bis.close()
                `is`.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bm
        }

        @JvmStatic
        fun resizeBitmap(bitmap: Bitmap, bannerWidth: Int, bannerHeight: Int): Bitmap {
            var source = bitmap
            if (source.height == bannerHeight && source.width == bannerWidth) return source
            val maxLength = Math.min(bannerWidth, bannerHeight)
            return try {
                source = source.copy(source.config, true)
                if (source.height <= source.width) {
                    if (source.height <= maxLength) { // if image already smaller than the required height
                        return source
                    }
                    val aspectRatio = source.width
                        .toDouble() / source.height.toDouble()
                    val targetWidth = (maxLength * aspectRatio).toInt()
                    Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
                } else {
                    if (source.width <= maxLength) { // if image already smaller than the required height
                        return source
                    }
                    val aspectRatio = source.height.toDouble() / source.width
                        .toDouble()
                    val targetHeight = (maxLength * aspectRatio).toInt()
                    Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                source
            }
        }

        fun setStatusBarGradiant(activity: Activity) {
            val window: Window = activity.window
            val background =ContextCompat.getDrawable(activity, R.drawable.gradient_bg)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = ContextCompat.getColor(activity,android.R.color.transparent)
            window.navigationBarColor = ContextCompat.getColor(activity,android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }

        fun getBitmapFromURL(src: String?): Bitmap? {
            return try {
                Log.e("src", src!!)
                val url = URL(src)
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
                connection.setDoInput(true)
                connection.connect()
                val input: InputStream = connection.getInputStream()
                val myBitmap = BitmapFactory.decodeStream(input)
                Log.e("Bitmap", "returned")
                myBitmap
            } catch (e: IOException) {
                e.printStackTrace()
                Log.e("Exception", e.message!!)
                null
            }
        }
    }
}