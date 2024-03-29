package kz.divtech.odyssey.rotation.data.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import kz.divtech.odyssey.rotation.data.local.Dao
import kz.divtech.odyssey.rotation.data.remote.result.asFailure
import kz.divtech.odyssey.rotation.data.remote.result.asSuccess
import kz.divtech.odyssey.rotation.data.remote.result.isSuccess
import kz.divtech.odyssey.rotation.data.remote.retrofit.RetrofitClient
import kz.divtech.odyssey.rotation.domain.model.trips.response.trip.Trip
import kz.divtech.odyssey.rotation.domain.model.trips.response.trip.toActiveTripList
import kz.divtech.odyssey.rotation.domain.model.trips.response.trip.toArchiveTripList
import kz.divtech.odyssey.rotation.common.utils.TicketDownloadUtil

@ExperimentalPagingApi
class TripRemoteMediator(val dao: Dao,
                         val isActive: Boolean,
                         val status: Array<String> = arrayOf(),
                         val direction: Array<String> = arrayOf(),
                         val sortBy: String = "date") : RemoteMediator<Int, Trip>() {

    private var pageIndex = 0

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Trip>): MediatorResult {
        pageIndex = getPageIndex(loadType)?:
            return MediatorResult.Success(true)
        val response = if(isActive){
            RetrofitClient.getApiService().getActiveTrips(pageIndex, status.joinToString(","),
                direction.joinToString(","), sortBy)
        }else{
            RetrofitClient.getApiService().getArchiveTrips(pageIndex, status.joinToString(","),
                direction.joinToString(","), sortBy)
        }

        return if(response.isSuccess()){
            val trips = response.asSuccess().value.data
            if(isActive){
                TicketDownloadUtil.downloadAllActiveTickets(trips)
            }

            when(loadType){
                LoadType.REFRESH ->
                    if(isActive) {
                        dao.refreshActiveTrips(trips.toActiveTripList())
                    }else{
                        dao.refreshArchiveTrips(trips.toArchiveTripList())
                    }
                else ->
                    if(isActive) {
                        dao.insertActiveTrips(trips.toActiveTripList())
                    }else{
                        dao.insertArchiveTrips(trips.toArchiveTripList())
                    }
            }
            MediatorResult.Success(trips.size < state.config.pageSize)
        }else{
            MediatorResult.Error(response.asFailure().error!!)
        }
    }

    private fun getPageIndex(loadType: LoadType) : Int?{
        return when(loadType){
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> null
            LoadType.APPEND -> ++pageIndex
        }
    }


    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }
}
