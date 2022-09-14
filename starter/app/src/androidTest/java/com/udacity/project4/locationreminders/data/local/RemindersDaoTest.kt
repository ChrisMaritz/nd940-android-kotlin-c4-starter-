package com.udacity.project4.locationreminders.data.local

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

//    TODO: Add testing implementation to the RemindersDao.kt

    @get:Rule
    val instantExecutor = InstantTaskExecutorRule()

    private lateinit var dtaBase : RemindersDatabase

    @Before
    fun getDtaBase(){
        dtaBase = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun reset(){
        dtaBase.close()
    }

    @Test
    fun create_get_checkCorrect(){
        runBlocking {
            val reminder = ReminderDTO("title", "description","title", 0.0, 0.0)
            dtaBase.reminderDao().saveReminder(reminder)

            val getReminder = dtaBase.reminderDao().getReminderById(reminder.id)

            assertThat(getReminder, `is`(notNullValue()))
            assertThat(getReminder?.title, `is`(reminder.title))
            assertThat(getReminder?.description, `is`(reminder.description))
            assertThat(getReminder?.location, `is`(reminder.location))
            assertThat(getReminder?.longitude, `is`(reminder.longitude))
            assertThat(getReminder?.latitude, `is`(reminder.latitude))
            assertThat(getReminder?.id, `is`(reminder.id))
        }
    }
}