package findsolucoes.com.prova_cedro.models

import android.content.Context
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import findsolucoes.com.prova_cedro.R
import java.lang.Exception


class LogoListAdapter(private val notes: List<String>,
                      private val context: Context
) : RecyclerView.Adapter<LogoListAdapter.ViewHolder>() {


    private lateinit var mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.listmodel_activity_main_nav_header, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindModel()
        holder.imageV.tag = notes[position]
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageV: ImageView = itemView.findViewById(R.id.listmodel_activity_main_nav_header_imgv)
        val url: TextView = itemView.findViewById(R.id.listmodel_activity_main_nav_header_url)


        fun bindModel() {
            url.text = "www.google.com"
        }
    }

}