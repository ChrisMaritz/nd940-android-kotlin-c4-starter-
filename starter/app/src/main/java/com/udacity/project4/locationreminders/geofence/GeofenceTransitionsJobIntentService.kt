package com.udacity.project4.locationreminders.geofence

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.sendNotification
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {
        private const val JOB_ID = 573

        //        TODO: call this to start the JobIntentService to handle the geofencing transition events
        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {

        val event = GeofencingEvent.fromIntent(intent)
        val list = event.triggeringGeofences
        Log.i("OnHandle", "FromIntent")
        //TODO: handle the geofencing transition events and
        // send a notification to the user when he enters the geofence area
        if(event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL){
            Log.i("OnHandle", "Enter")
            val id = when{
                !event.triggeringGeofences.isEmpty() -> {
                    Log.i("OnHandle", "not empty")
                    list[0].requestId
                    }
                else -> {Log.i("Trigger", "error")
                    return
                   }
            }
            Log.i("OnHandle", id.toString())
            sendNotification(id)
        }
        //TODO call @sendNotification
    }

    //TODO: get the request id of the current geofence
    private fun sendNotification(id : String) {
        Log.i("Notify", "Begin" )
        //Get the local repository instance
        val remindersLocalRepository: ReminderDataSource by inject()
//        Interaction to the repository has to be through a coroutine scope
        CoroutineScope(coroutineContext).launch(SupervisorJob()) {
            //get the reminder with the request id
            Log.i("Notify", "Coroutine" )
            val result = remindersLocalRepository.getReminder(id)
            Log.i("Notify", result.toString())
            if (result is Result.Success<ReminderDTO>) {
                Log.i("Notify", "if" )
                val reminderDTO = result.data
                //send a notification to the user with the reminder details
                sendNotification(
                    this@GeofenceTransitionsJobIntentService, ReminderDataItem(
                        reminderDTO.title,
                        reminderDTO.description,
                        reminderDTO.location,
                        reminderDTO.latitude,
                        reminderDTO.longitude,
                        reminderDTO.id
                    )
                )
            }else{
                Log.i("GetReminder", "Error")
            }
        }
    }

}
