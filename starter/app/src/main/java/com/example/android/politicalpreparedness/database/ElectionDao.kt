package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(election: Election)

    //TODO: Add select all election query
    @Query("SELECT * FROM election_table")
    fun getAllElections():LiveData<List<Election>?>

    //select single election query
    @Query("SELECT * FROM election_table WHERE id = :electionId")
    suspend fun getSelectedElection(electionId: Int?): Election?

    //delete query
    @Delete
    suspend fun deleteSelectedElection(election: Election)

    //TODO: Add clear query

}