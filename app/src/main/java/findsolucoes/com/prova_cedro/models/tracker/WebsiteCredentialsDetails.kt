package findsolucoes.com.prova_cedro.models.tracker

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import findsolucoes.com.prova_cedro.entites.WebsiteCredentialsEntity

class WebsiteCredentialsDetails(var websiteCredentialsEntity: WebsiteCredentialsEntity? = null,
                                var adapterPosition : Int = -1) : ItemDetailsLookup.ItemDetails<Long>(){


    override fun getSelectionKey(): Long?
            = websiteCredentialsEntity!!.id.toLong()


    override fun getPosition(): Int
            = adapterPosition

    override fun inSelectionHotspot(e: MotionEvent): Boolean
            = true

}