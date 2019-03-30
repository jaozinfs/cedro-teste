package findsolucoes.com.prova_cedro.models.tracker

import androidx.recyclerview.selection.ItemKeyProvider
import findsolucoes.com.prova_cedro.entites.WebsiteCredentialsEntity

class WebsiteCredentialsKeyprovider(var list : ArrayList<WebsiteCredentialsEntity>) : ItemKeyProvider<Long> ( ItemKeyProvider.SCOPE_MAPPED ) {


    override fun getKey(position: Int): Long? = list[position].id.toLong()


    override fun getPosition(key: Long): Int
     = list.indexOf(
        list.filter { wbc-> wbc.id.toLong() == key }.single()
        )
}