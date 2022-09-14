package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.dto.Result.Success

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf<ReminderDTO>()) : ReminderDataSource {

//    TODO: Create a fake data source to act as a double to the real data source
    private var shouldReturnError : Boolean = false

    fun setReturnError(value : Boolean){
        shouldReturnError = value
    }
    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        if(shouldReturnError){
            return Result.Error("Test Exception")
        }
        return Result.Success(reminders.toList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if(shouldReturnError){
            return  Result.Error("Test Exception")
        }
        reminders.find { it.id == id }?.let {
            return Result.Success(it)
        }
        return Result.Error(
            "Reminder not found"
        )
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }


}