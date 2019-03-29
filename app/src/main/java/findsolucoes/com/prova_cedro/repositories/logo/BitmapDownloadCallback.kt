package findsolucoes.com.prova_cedro.repositories.logo

import android.graphics.Bitmap

/**
 * Callback to download bitmap asynk
 */
interface BitmapDownloadCallback{
    fun onSucess(bitmap : Bitmap)
    fun onError(error : Throwable)
}