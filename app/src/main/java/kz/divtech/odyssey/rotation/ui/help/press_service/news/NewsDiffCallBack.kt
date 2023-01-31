package kz.divtech.odyssey.rotation.ui.help.press_service.news

import androidx.recyclerview.widget.DiffUtil
import kz.divtech.odyssey.rotation.domain.model.help.press_service.news.Article

class NewsDiffCallBack : DiffUtil.ItemCallback<Article>() {

    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}