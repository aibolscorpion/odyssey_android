package kz.divtech.odyssey.rotation.ui.profile.profile

import androidx.lifecycle.*
import kz.divtech.odyssey.rotation.data.remote.RetrofitClient
import kz.divtech.odyssey.rotation.domain.model.login.login.Employee
import kz.divtech.odyssey.rotation.domain.repository.ApplicationsRepository
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response

class ProfileViewModel(val repository: ApplicationsRepository): ViewModel() {
    private val _isSuccessfullyLoggedOut = MutableLiveData<Boolean>()
    val isSuccessfullyLoggedOut = _isSuccessfullyLoggedOut

    fun logout(){
        RetrofitClient.getApiService().logout().enqueue(object: retrofit2.Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                _isSuccessfullyLoggedOut.postValue(response.isSuccessful)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                _isSuccessfullyLoggedOut.postValue(false)
            }
        })

    }

    class ProfileViewModelFactory(val repository: ApplicationsRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(ProfileViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val employeeLiveData: LiveData<Employee> = repository.employeeInfo.asLiveData()

}