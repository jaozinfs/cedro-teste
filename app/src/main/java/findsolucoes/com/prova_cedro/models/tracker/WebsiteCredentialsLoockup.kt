package findsolucoes.com.prova_cedro.models.tracker

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import findsolucoes.com.prova_cedro.models.WebsiteCredentialsListAdapter

class WebsiteCredentialsLoockup(val rvWebsitecredentials : RecyclerView) : ItemDetailsLookup<Long>(){

    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view: View? = rvWebsitecredentials.findChildViewUnder( event.x, event.y )

        if(view != null){

            val holder = rvWebsitecredentials.getChildViewHolder(view)

            return (holder as WebsiteCredentialsListAdapter.ViewHolder).websiteCredentialsDetails

        }
        return null
    }
}