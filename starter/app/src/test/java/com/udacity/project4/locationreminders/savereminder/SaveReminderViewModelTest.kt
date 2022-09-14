package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    private lateinit var dataSrc: FakeDataSource


    //TODO: provide testing to the SaveReminderView and its live data objects

    @get:Rule
    var executorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun testSetup(){
        dataSrc = FakeDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(), dataSrc)
    }

    @Test
    fun create_save_getReminder(){

        runBlocking {
            val reminderHere = ReminderDTO(
                "title",
                "description",
                "location",
                0.0,
                0.0,
                "id")

            saveReminderViewModel.saveReminder(
                ReminderDataItem(
                    reminderHere.title,
                    reminderHere.description,
                    reminderHere.location,
                    reminderHere.latitude,
                    reminderHere.longitude,
                    reminderHere.id
                )
            )

            val getReminder = saveReminderViewModel.dataSource.getReminder("id")

            getReminder as Result.Success
            assertThat(getReminder.data.title, `is`("title"))
            assertThat(getReminder.data.description, `is`("description"))
            assertThat(getReminder.data.location, `is`("location"))
            assertThat(getReminder.data.latitude, `is`(0.0))
            assertThat(getReminder.data.longitude, `is`(0.0))
            assertThat(getReminder.data.id, `is`("id"))
        }
    }


}