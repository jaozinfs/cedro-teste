package findsolucoes.com.prova_cedro.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.InputStream
import java.util.stream.BaseStream

class Utils{

    companion object {

        //Return bitmap from inputStream
       fun createBitmapFromInputStream(inputStream: InputStream) : Bitmap?{
            try{
                return BitmapFactory.decodeStream(inputStream)
            }catch (err : Throwable){
                err.printStackTrace()
            }
            return null
        }
    }

}