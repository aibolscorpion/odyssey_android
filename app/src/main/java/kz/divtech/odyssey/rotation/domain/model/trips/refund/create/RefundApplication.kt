package kz.divtech.odyssey.rotation.domain.model.trips.refund.create

data class RefundApplication(
    val application_id: Int,
    val segments_id: List<Int>,
    val reason: String
)