package kz.divtech.odyssey.rotation.domain.repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import kz.divtech.odyssey.rotation.data.local.Dao
import kz.divtech.odyssey.rotation.data.remote.result.Result
import kz.divtech.odyssey.rotation.data.remote.retrofit.RetrofitClient
import kz.divtech.odyssey.rotation.domain.model.trips.Trip
import kz.divtech.odyssey.rotation.domain.model.trips.refund.applications.RefundAppItem
import kz.divtech.odyssey.rotation.domain.model.trips.refund.create.RefundApplication
import okhttp3.ResponseBody

class RefundRepository(val dao: Dao) {

    suspend fun getRefundListByTripId(applicationId: Int): Result<ArrayList<RefundAppItem>> {
        return RetrofitClient.getApiService().getRefundApplications(applicationId)
    }

    suspend fun sendApplicationToRefund(applicationId: Int, segmentId: IntArray, reason: String)
            : Result<Map<String, Int>> {
        val application = RefundApplication(applicationId, segmentId.toList(), reason)
        return RetrofitClient.getApiService().sendApplicationToRefund(application)
    }

    suspend fun cancelRefund(refundId: Int): Result<ResponseBody>{
        return RetrofitClient.getApiService().cancelRefund(refundId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun getTripById(tripId: Int): Flow<Trip> {
        return dao.observeTripById(tripId)
    }

}