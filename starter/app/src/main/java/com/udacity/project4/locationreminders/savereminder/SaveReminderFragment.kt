package com.udacity.project4.locationreminders.savereminder

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.utils.sendNotification
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding
    var geofencingClient: GeofencingClient? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)
        setDisplayHomeAsUpEnabled(true)
        binding.viewModel = _viewModel
        binding.reminderDescription
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }
        geofencingClient = LocationServices.getGeofencingClient(requireContext())

        binding.saveReminder.setOnClickListener {
            _viewModel.reminderTitle.value = binding.reminderTitle.text.toString()
            _viewModel.reminderDescription.value = binding.reminderDescription.text.toString()
            val title1 = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription.value
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude: Double? = _viewModel.latitude.value
            val longitude: Double? = _viewModel.longitude.value
//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
//             2) save the reminder to the local db
            val geofenceList = mutableListOf<Geofence>()

            geofenceList.add(Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId(location)

                // Set the circular region of this geofence.
                .setCircularRegion(
                    latitude!!,
                    longitude!!,
                    20.00f
                )

                // Set the expiration duration of the geofence. This geofence gets automatically
                // removed after this period of time.
                .setExpirationDuration(172800000)

                // Set the transition types of interest. Alerts are only generated for these
                // transition. We track entry and exit transitions in this sample.
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

                // Create the geofence.
                .build())
            val geofencePendingIntent: PendingIntent by lazy {
                val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java)
                // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
                // addGeofences() and removeGeofences().
                intent.action = ACTION_GEOFENCE_EVENT
                PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            val geofence = Geofence.Builder()
                .setRequestId(location)
                .setCircularRegion(latitude!!,
                    longitude!!,
                    20.00f)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build()

            val geofencingRequest = GeofencingRequest.Builder()
                .setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                .addGeofence(geofence)
                .build()
            geofencingClient?.addGeofences(geofencingRequest, geofencePendingIntent)
                ?.run {
                    addOnSuccessListener {
                        Log.d("geo: ", "Success")
                    }
                    addOnFailureListener {
                        Log.e("geo", it.message!!)
                        Log.e("geo", "Error saving geofence")
                    }
                }
            val localDb = LocalDB
            val dao = localDb.createRemindersDao(requireContext())
            val dispatcher = Dispatchers.IO
            val reminder = ReminderDTO(
                title1,
                description,
                location,
                latitude,
                longitude)
            val remindersLocalRepository = RemindersLocalRepository(dao,dispatcher)
            CoroutineScope(Dispatchers.IO).launch {remindersLocalRepository.saveReminder(reminder)}

            val reminderNotification = ReminderDataItem(
                title1,
                description,
                location,
                latitude,
                longitude
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }

    private fun getGeofencingRequest(geofenceList: List<Geofence>): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofenceList)
        }.build()
    }


    companion object {
        internal const val ACTION_GEOFENCE_EVENT = ""
    }

    }

