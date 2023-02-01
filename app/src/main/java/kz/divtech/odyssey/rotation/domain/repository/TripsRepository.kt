package kz.divtech.odyssey.rotation.domain.repository

import androidx.annotation.WorkerThread
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kz.divtech.odyssey.rotation.data.local.Dao
import kz.divtech.odyssey.rotation.data.remote.retrofit.RetrofitClient
import kz.divtech.odyssey.rotation.domain.model.trips.Trip
import kz.divtech.odyssey.rotation.ui.trips.active_archive_trips.TripRemoteMediator
import timber.log.Timber


class TripsRepository(private val dao : Dao) {

    val nearestActiveTrip: Flow<Trip> = dao.getNearestActiveTrip()
    fun getTripById(id: Int): Flow<Trip> = dao.getTripById(id)

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertTrips(trips: List<Trip>) {
        dao.insertTrips(trips)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteTrips(){
        dao.deleteTrips()
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun refreshTrips(data: List<Trip>){
        dao.refreshTrips(data)
    }

    suspend fun getTripsFromFirstPage(){
        try {
            val response = RetrofitClient.getApiService().getTrips(1, orderDir = "desc")
            if(response.isSuccessful){
                val trips = response.body()?.data?.data!!
                insertTrips(trips)
            }
        }catch (e: Exception){
            Timber.i("exception - $e")
        }

    }

    @OptIn(ExperimentalPagingApi::class)
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getActivePagingTrip(): Flow<PagingData<Trip>>{

       return Pager(
           config = PagingConfig(pageSize = 20, enablePlaceholders = false),
           remoteMediator = TripRemoteMediator(dao, "asc"),
           pagingSourceFactory = {dao.getActiveTrips()}
       ).flow
    }

    @OptIn(ExperimentalPagingApi::class)
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getArchivePagingTrip(): Flow<PagingData<Trip>>{

        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = TripRemoteMediator(dao, "desc"),
            pagingSourceFactory = {dao.getArchiveTrips()}
        ).flow
    }

}