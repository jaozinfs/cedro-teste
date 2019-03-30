package findsolucoes.com.prova_cedro.models.tracker

import androidx.recyclerview.selection.SelectionTracker

class WebsiteCredentialsPredicate : SelectionTracker.SelectionPredicate<Long>(){

    override fun canSelectMultiple(): Boolean
            = true

    override fun canSetStateForKey(key: Long, nextState: Boolean): Boolean
            = true

    override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean
            = true

}