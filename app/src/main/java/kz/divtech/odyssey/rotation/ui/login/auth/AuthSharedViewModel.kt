package kz.divtech.odyssey.rotation.ui.login.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import kz.divtech.odyssey.rotation.data.remote.RetrofitClient
import kz.divtech.odyssey.rotation.domain.model.login.login.BadRequest
import kz.divtech.odyssey.rotation.domain.model.login.login.Login
import kz.divtech.odyssey.rotation.domain.model.login.login.LoginResponse
import kz.divtech.odyssey.rotation.domain.model.login.login.TooManyRequest
import kz.divtech.odyssey.rotation.domain.model.login.search_by_iin.EmployeeData
import kz.divtech.odyssey.rotation.domain.model.login.sendsms.CodeResponse
import kz.divtech.odyssey.rotation.domain.model.login.sendsms.PhoneNumber
import kz.divtech.odyssey.rotation.utils.Event
import kz.divtech.odyssey.rotation.utils.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.io.IOException


class AuthSharedViewModel : ViewModel() {
    var authLogId: String? = null
    var phoneNumber: String? = null

    private val _message = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>> = _message

    private val _isPhoneNumberFounded = MutableLiveData<Event<Boolean>>()
    val isPhoneNumberFounded: LiveData<Event<Boolean>> = _isPhoneNumberFounded

    private val _isEmployeeNotFounded = MutableLiveData<Event<Boolean>>()
    val isEmployeeNotFounded : LiveData<Event<Boolean>> = _isEmployeeNotFounded

    private val _employeeInfo = MutableLiveData<Event<EmployeeData>>()
    val employeeInfo : LiveData<Event<EmployeeData>> = _employeeInfo

    private val _isSuccessfullyLoggedIn = MutableLiveData<Boolean>()
    val isSuccessfullyLoggedIn : LiveData<Boolean> = _isSuccessfullyLoggedIn

    private val _isErrorHappened = MutableLiveData<Event<Boolean>>()
    val isErrorHappened: LiveData<Event<Boolean>> = _isErrorHappened

    fun sendSmsToPhone(phoneNumber: String?){
        if(phoneNumber == null && this.phoneNumber != null)
            requestSmsCode(this.phoneNumber!!)
        else{
            requestSmsCode(phoneNumber!!)
            this.phoneNumber = phoneNumber
        }
    }

    private fun requestSmsCode(phoneNumber: String){
        RetrofitClient.getApiService().sendSms(PhoneNumber(phoneNumber)).enqueue(object : Callback<CodeResponse>{
            override fun onResponse(call: Call<CodeResponse>, response: Response<CodeResponse>) {
                when(response.code()){
                    200 -> {
                        val codeResponse  = response.body()
                        authLogId = codeResponse?.data?.auth_log_id
                        _isPhoneNumberFounded.postValue(Event(true))
                    }
                    400 -> {
                        lateinit var mError : BadRequest
                        try {
                            mError = GsonBuilder().create().fromJson(response.errorBody()!!.string(),BadRequest::class.java)
                        } catch (_: IOException) {}
                        when(mError.slug){
                            "employee_not_found" -> _isPhoneNumberFounded.postValue(Event(false))
                            "invalid_phone" -> _message.postValue(Event(mError.message!!))
                        }
                    }
                    429 -> {
                        lateinit var mError : TooManyRequest
                        try {
                            mError = GsonBuilder().create().fromJson(response.errorBody()!!.string(),TooManyRequest::class.java)
                        } catch (_: IOException) {}
                        when(mError.type){
                            "too_many_requests" -> _message.postValue(Event(mError.message!!))
                        }
                    }

                }
            }

            override fun onFailure(call: Call<CodeResponse>, t: Throwable) {
                _isErrorHappened.postValue(Event(true))
            }

        })
    }

    fun login(code: String){
        val login = Login(phoneNumber, code, authLogId)
        RetrofitClient.getApiService().login(login).enqueue(object: Callback<LoginResponse>{
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if(response.isSuccessful){
                    val loginResponse = response.body()
                    Timber.d(loginResponse.toString())
                    SessionManager().saveAuthToken(loginResponse?.data?.token!!)
                    _message.postValue(Event(loginResponse.message!!))
                    _isSuccessfullyLoggedIn.postValue(true)
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _message.postValue(Event(t.message.toString()))
            }

        })
    }

    fun getEmployeeInfoByPhoneNumber(phoneNumber: String){
        RetrofitClient.getApiService().getEmployeeByPhone(phoneNumber).enqueue(object: Callback<EmployeeData>{
            override fun onResponse(call: Call<EmployeeData>, response: Response<EmployeeData>) {
                if(response.code() == 200)
                    _employeeInfo.postValue(Event(response.body()!!))
                else if(response.code() == 400)
                    _isEmployeeNotFounded.postValue(Event(true))
            }

            override fun onFailure(call: Call<EmployeeData>, t: Throwable) {
                _isErrorHappened.postValue(Event(true))
            }

        })
    }

}