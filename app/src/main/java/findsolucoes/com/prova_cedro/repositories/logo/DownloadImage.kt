package findsolucoes.com.prova_cedro.repositories.logo

import android.app.Application
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import findsolucoes.com.prova_cedro.data.RetrofitAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class DownloadImage(application: Application){

    private var retrofitAPI: RetrofitAPI

    init {

        retrofitAPI = RetrofitAPI(application)
    }

    /**
     * Download bitmap from recyclerview async and set in container
     *
     */
    fun requestDownloadImage(url: String, imageView: ImageView, progressBar: ProgressBar){
        retrofitAPI.searchLogoFromUrl(url, null).timeout(60, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(

                {

                    imageView.setImageBitmap(BitmapFactory.decodeStream(it.byteStream()))
                    progressBar.visibility = View.GONE

                },

                {
                })

    }
    fun requestDownloadImage(url: String, imageView: ImageView){
        retrofitAPI.searchLogoFromUrl(url, null).timeout(60, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(

                {

                    imageView.setImageBitmap(BitmapFactory.decodeStream(it.byteStream()))


                },

                {
                })

    }

    /**
     * Download bitmap async when search url in editext
     * callback @sucess return bitmap
     */
    fun searchFavIconImageFromUrl(url: String, downloadCallback: BitmapDownloadCallback){
        retrofitAPI.searchLogoFromUrl(url, null).timeout(60, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .unsubscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(

                {
                    downloadCallback.onSucess(BitmapFactory.decodeStream(it.byteStream()))
                },

                {
                   downloadCallback.onError(it)
                })

    }

}