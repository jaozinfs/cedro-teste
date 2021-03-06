package findsolucoes.com.prova_cedro.models

import android.app.Application
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.RecyclerView
import findsolucoes.com.prova_cedro.R

import findsolucoes.com.prova_cedro.entites.WebsiteCredentialsEntity
import findsolucoes.com.prova_cedro.models.tracker.WebsiteCredentialsDetails
import findsolucoes.com.prova_cedro.repositories.logo.DownloadImage





class WebsiteCredentialsListAdapter(val application: Application) : RecyclerView.Adapter<WebsiteCredentialsListAdapter.ViewHolder>() {

    private val list: ArrayList<WebsiteCredentialsEntity> = ArrayList()
    private var mInflater: LayoutInflater = LayoutInflater.from(application)
    private val imageDownload : DownloadImage  = DownloadImage(application)
    lateinit var selectionTracker : SelectionTracker<Long>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.listmodel_activity_main_nav_header, parent, false)
        return ViewHolder(view, imageDownload)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("as", "bind ${list[position].url}")
        holder.bindModel(list[position], position)
        holder.imageV.tag = list[position]
    }




    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateList(updatelist : ArrayList<WebsiteCredentialsEntity>){
        this.list.clear()
        if(updatelist != null && !updatelist.isEmpty()){

            this.list.addAll(updatelist)
            this.notifyDataSetChanged()
        }else{

            this.list.addAll(ArrayList<WebsiteCredentialsEntity>())
            this.notifyDataSetChanged()
        }

    }

    fun getList() : ArrayList<WebsiteCredentialsEntity> = list

    fun removeALl(deleteItems: MutableList<WebsiteCredentialsEntity>) {

        list.removeAll(deleteItems)
        notifyDataSetChanged()

    }


    inner class ViewHolder(itemView: View, val downloadImage: DownloadImage) : RecyclerView.ViewHolder(itemView) {

        //
        val imageV: ImageView = itemView.findViewById(findsolucoes.com.prova_cedro.R.id.listmodel_activity_main_nav_header_imgv)
        val url: TextView = itemView.findViewById(findsolucoes.com.prova_cedro.R.id.listmodel_activity_main_nav_header_url)
        val bg: LinearLayout = itemView.findViewById(R.id.listmodel_activity_main_nav_header_bg)

        //
        private val progress : ProgressBar = itemView.findViewById(findsolucoes.com.prova_cedro.R.id.listmodel_activity_main_nav_header_progress)
        val websiteCredentialsDetails : WebsiteCredentialsDetails = WebsiteCredentialsDetails()

        //
        fun bindModel(websiteCredentialsEntity: WebsiteCredentialsEntity, position: Int) {
            url.text = websiteCredentialsEntity.url
            downloadImage.requestDownloadImage(websiteCredentialsEntity.url, imageV, progress)
            websiteCredentialsDetails.websiteCredentialsEntity = websiteCredentialsEntity
            websiteCredentialsDetails.adapterPosition = position

            if(selectionTracker.isSelected( websiteCredentialsDetails.selectionKey )){
                setSelectionUI()
                itemView.isActivated = true
            }else{
                setDeselectUI()
                itemView.isActivated = false
            }

        }

        //selectiontracker on UI
        fun setSelectionUI(){
            bg.setBackgroundResource(R.drawable.cardview_bg_selected)

        }

        //selectiontracker off UI
        fun setDeselectUI(){
            bg.setBackgroundResource(R.drawable.cardview_bg_desselected)
        }
    }


}