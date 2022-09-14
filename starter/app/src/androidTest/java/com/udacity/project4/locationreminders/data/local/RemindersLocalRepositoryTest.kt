package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.filters.MediumTest
import com.udacity.project4.MainCoroutineRule
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @Rule
    val instantExecutor = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @Rule
    var mainRule = MainCoroutineRule()

    private lateinit var localRepository: RemindersLocalRepository

    private lateinit var dtaBase: RemindersDatabase

    @Before
    fun localReposAndDtaBase(){

        dtaBase = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localRepository = RemindersLocalRepository(dtaBase.reminderDao(), Dispatchers.Main)
    }

    @After
    fun finish(){
     dtaBase.close()
    }

    @Test
    fun test(){
        mainRule.runBlockingTest {

            val reminder = ReminderDTO("title", "description", "location", 0.0, 0.0)
            localRepository.saveReminder(reminder)

            val result = localRepository.getReminder(reminder.id)

            result as Result.Success
            assertThat(result.data.title, `is`(reminder.title))
            assertThat(result.data.description, `is`(reminder.description))
            assertThat(result.data.location, `is`(reminder.location))
            assertThat(result.data.latitude, `is`(reminder.latitude))
            assertThat(result.data.longitude, `is`(reminder.longitude))
            assertThat(result.data.id, `is`(reminder.id))

        }
    }


}