package findsolucoes.com.prova_cedro.models

import android.app.Application
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import findsolucoes.com.prova_cedro.R

import findsolucoes.com.prova_cedro.entites.WebsiteCredentialsEntity
import findsolucoes.com.prova_cedro.repositories.logo.DownloadImage





class WebsiteCredentialsListAdapter(val application: Application) : RecyclerView.Adapter<WebsiteCredentialsListAdapter.ViewHolder>() {

    private val list: ArrayList<WebsiteCredentialsEntity> = ArrayList()
    private var mInflater: LayoutInflater = LayoutInflater.from(application)
    val imageDownload : DownloadImage  = DownloadImage(application)
    var ratingShow: Int = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.listmodel_activity_main_nav_header, parent, false)
        return ViewHolder(view, imageDownload)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindModel(list[position])
        holder.imageV.tag = list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun updateList(updatelist : ArrayList<WebsiteCredentialsEntity>){
        list.clear()
        list.addAll(updatelist)
        notifyDataSetChanged()
    }



    class ViewHolder(itemView: View, val downloadImage: DownloadImage) : RecyclerView.ViewHolder(itemView) {

        val imageV: ImageView = itemView.findViewById(findsolucoes.com.prova_cedro.R.id.listmodel_activity_main_nav_header_imgv)
        val url: TextView = itemView.findViewById(findsolucoes.com.prova_cedro.R.id.listmodel_activity_main_nav_header_url)
        val progress : ProgressBar = itemView.findViewById(findsolucoes.com.prova_cedro.R.id.listmodel_activity_main_nav_header_progress)

        fun bindModel(websiteCredentialsEntity: WebsiteCredentialsEntity) {
            url.text = websiteCredentialsEntity.url
            downloadImage.requestDownloadImage(websiteCredentialsEntity.url, imageV, progress)

        }
    }
}