package com.example.android.politicalpreparedness.representative

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.election.ApiStatus
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.CivicRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import java.lang.Exception

enum class RepresentativeApiStatus{
    LOADING,
    DONE,
    ERROR
}

class RepresentativeViewModel(private val app: Application): AndroidViewModel(app) {

    val database = ElectionDatabase.getInstance(app)
    val repository = CivicRepository(database)

    //live data for representatives and address
    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives: LiveData<List<Representative>>
            get() = _representatives

    private val _address = MutableLiveData<Address>()
    val address: LiveData<Address>
        get() = _address

    val addressLine1 = MutableLiveData<String>()

    val addressLine2 = MutableLiveData<String>()

    val city = MutableLiveData<String>()

    val state = MutableLiveData<String>()

    val zip = MutableLiveData<String>()

    private val _showError = MutableLiveData<String>()
    val showError: LiveData<String>
    get() = _showError


    private val _representativeApiStatus = MutableLiveData<RepresentativeApiStatus>()
    val representativeApiStatus: LiveData<RepresentativeApiStatus>
    get() = _representativeApiStatus

    //function to fetch representatives from API from a provided address
    fun getRepresentativesByAddress(){
        viewModelScope.launch {
            _representativeApiStatus.value = RepresentativeApiStatus.LOADING
            try {
                val address = getAddressFromFields()
                val representativeResponse = repository.getRepresentativeInfo(address)
                _representatives.value = representativeResponse?.offices?.flatMap {
                    office -> office.getRepresentatives(representativeResponse?.officials)
                }
                _representativeApiStatus.value = RepresentativeApiStatus.DONE
            }catch (e:Exception){
                _representativeApiStatus.value = RepresentativeApiStatus.ERROR
                _representatives.value = emptyList()
                e.message?.let {
                    Log.e("RepresentativeViewModel",it)
                    showErrorRetrievingAddress(it)
                }
            }
        }
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    fun getAddressFromGeoLocation(address:Address){
        _address.value = address
        populateAddressFields()
    }

    // Create function to get address from individual fields
    fun getAddressFromFields(): String{
        val address =  Address(addressLine1.value.toString(),
                                addressLine2.value.toString(),
                                city.value.toString(),
                                state.value.toString(),
                                zip.value.toString())
        _address.value = address
        return address.toFormattedString()
    }

    fun showErrorRetrievingAddress(error: String){
        _showError.value = error
        _representatives.value = emptyList()
        _representativeApiStatus.value = RepresentativeApiStatus.ERROR
    }

    private fun populateAddressFields(){
        addressLine1.value = _address.value?.line1
        addressLine2.value = _address.value?.line2
        city.value = _address.value?.city
        state.value = _address.value?.state
        zip.value = _address.value?.zip
    }
}
