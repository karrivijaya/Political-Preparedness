package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi.retrofitService
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CivicRepository (private val database: ElectionDatabase) {

    suspend fun refreshElectionData(): List<Election>? {
        var electionResponse: ElectionResponse? = null
        withContext(Dispatchers.IO) {
            electionResponse = retrofitService.getElections()
        }
        return electionResponse?.elections
    }

    suspend fun getVoterInfo(address: String, electionId: Int?): VoterInfoResponse? {
        var voterInfoResponse: VoterInfoResponse? = null
        withContext(Dispatchers.IO) {
            voterInfoResponse = retrofitService.getVoterInfo(address, electionId)
        }
        return voterInfoResponse
    }

    suspend fun getSelectedElection(electionId: Int?): Election? {
        var selectedElection: Election? = null
        withContext(Dispatchers.IO) {
            selectedElection = database.electionDao.getSelectedElection(electionId)
        }
        return selectedElection
    }

    suspend fun saveElectionToDatabase(election: Election) {
        withContext(Dispatchers.IO) {
            database.electionDao.insert(election)
        }
    }

    suspend fun deleteElectionFromDatabase(election: Election) {
        withContext(Dispatchers.IO) {
            database.electionDao.deleteSelectedElection(election)
        }
    }

    val getAllElections: LiveData<List<Election>?> = database.electionDao.getAllElections()

    suspend fun getRepresentativeInfo(address: String): RepresentativeResponse?{
        var representativeResponse: RepresentativeResponse? = null
        withContext(Dispatchers.IO){
            representativeResponse = retrofitService.getRepresentativesByAddress(address)
        }
        return representativeResponse
    }
}
