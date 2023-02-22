package kz.divtech.odyssey.rotation.domain.repository

import android.widget.Toast
import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import kz.divtech.odyssey.rotation.app.App
import kz.divtech.odyssey.rotation.data.local.Dao
import kz.divtech.odyssey.rotation.data.remote.result.asSuccess
import kz.divtech.odyssey.rotation.data.remote.result.isSuccess
import kz.divtech.odyssey.rotation.data.remote.retrofit.RetrofitClient
import kz.divtech.odyssey.rotation.domain.model.help.press_service.full_article.FullArticle

class ArticleRepository(private val dao: Dao) {

    fun getArticleById(id: Int): Flow<FullArticle> {
        return dao.observeArticleById(id)
    }

    @WorkerThread
    @Suppress("RedundantSuspendModifier")
    suspend fun insertFullArticle(fullArticle: FullArticle){
        dao.insertFullArticle(fullArticle)
    }

    @WorkerThread
    @Suppress("RedundantSuspendModifier")
    suspend fun deleteFullArticles(){
        dao.deleteFullArticles()
    }

    suspend fun getArticleByIdFromServer(articleId: Int){
        val response = RetrofitClient.getApiService().getSpecificArticleById(articleId)
        if(response.isSuccess()){
            insertFullArticle(response.asSuccess().value)
        }else{
            Toast.makeText(App.appContext, "$response", Toast.LENGTH_LONG).show()
        }
    }


    suspend fun markArticleAsRead(id: Int){
        RetrofitClient.getApiService().markAsReadArticleById(id)
    }

}