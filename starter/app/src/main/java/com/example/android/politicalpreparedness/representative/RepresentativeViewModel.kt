package com.example.android.politicalpreparedness.representative

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.CivicRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch
import java.lang.Exception

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

    private val _addressLine1 = MutableLiveData<String?>()
    val addressLine1: LiveData<String?>
        get() = _addressLine1

    private val _addressLine2 = MutableLiveData<String?>()
    val addressLine2: LiveData<String?>
        get() = _addressLine2

    private val _city = MutableLiveData<String?>()
    val city: LiveData<String?>
        get() = _city

    private val _state = MutableLiveData<String?>()
    val state: LiveData<String?>
        get() = _state

    private val _zip = MutableLiveData<String?>()
    val zip:LiveData<String?>
        get() = _zip

    //function to fetch representatives from API from a provided address
    fun getRepresentativesByAddress(){
        viewModelScope.launch {
            try {
                val address = getAddressFromFields()
                val representativeResponse = repository.getRepresentativeInfo(address)
                _representatives.value = representativeResponse?.offices?.flatMap {
                    office -> office.getRepresentatives(representativeResponse?.officials)
                }

            }catch (e:Exception){
                e.message?.let { Log.e("RepresentativeViewModel",it)}
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

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields
    fun getAddressFromFields(): String{
        val address =  Address(addressLine1.value?:"",
                                addressLine2.value,
                                city.value?:"",
                                state.value?:"",
                                zip.value?:"")
        _address.value = address
        return address.toFormattedString()
    }




}
