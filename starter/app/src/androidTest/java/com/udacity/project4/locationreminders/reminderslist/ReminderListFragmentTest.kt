package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.RemindersActivity
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.local.LocalDB
import com.udacity.project4.locationreminders.data.local.RemindersDao
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    private lateinit var contextApp: Application
    private lateinit var repository: ReminderDataSource

    @Before
    fun setUpHere(){
        stopKoin()
        contextApp = getApplicationContext()
        val module = module {
            viewModel {
                RemindersListViewModel(
                    contextApp,
                    get() as ReminderDataSource
                )
            }
            single { LocalDB.createRemindersDao(contextApp) }
            single { RemindersLocalRepository(get() as RemindersDao) }
        }
        startKoin {
            androidContext(getApplicationContext())
            modules(listOf(module))
        }
        repository = GlobalContext.get().koin.get()
        runBlocking {
            repository.deleteAllReminders()
        }
    }
//    TODO: test the navigation of the fragments.
    @Test
    fun testHere(){
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)

    }
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.
}