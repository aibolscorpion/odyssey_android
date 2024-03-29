package kz.divtech.odyssey.rotation.ui.login.find_by_phone_number

import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kz.divtech.odyssey.rotation.data.remote.result.*
import kz.divtech.odyssey.rotation.domain.model.login.search_employee.EmployeeResult
import kz.divtech.odyssey.rotation.data.repository.FindEmployeeRepository
import kz.divtech.odyssey.rotation.data.repository.OrgInfoRepository
import kz.divtech.odyssey.rotation.common.utils.Event

class FindEmployeeViewModel(private val findEmployeeRepository: FindEmployeeRepository,
                            private val orgInfoRepository: OrgInfoRepository
) : ViewModel() {
    private val _employeeResult = MutableLiveData<Event<Result<EmployeeResult>>>()
    val employeeResult : LiveData<Event<Result<EmployeeResult>>> = _employeeResult

    val pBarVisibility = ObservableInt(View.GONE)

    fun getEmployeeInfoByPhoneNumber(phoneNumber: String){
        viewModelScope.launch {
            pBarVisibility.set(View.VISIBLE)
            val result = findEmployeeRepository.findByPhoneNumber(phoneNumber)
            _employeeResult.value = Event(result)
            pBarVisibility.set(View.GONE)
        }
    }

    fun getOrgInfoFromServer() =
        viewModelScope.launch {
            pBarVisibility.set(View.VISIBLE)
            orgInfoRepository.getOrgInfoFromServer()
            pBarVisibility.set(View.GONE)
        }


    class FindEmployeeViewModelFactory(private val findEmployeeRepository: FindEmployeeRepository,
                                       private val orgInfoRepository: OrgInfoRepository
    ): ViewModelProvider.Factory{
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(FindEmployeeViewModel::class.java)){
                return FindEmployeeViewModel(findEmployeeRepository, orgInfoRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }



}