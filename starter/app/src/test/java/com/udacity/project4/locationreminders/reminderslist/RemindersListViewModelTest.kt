package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.getOrAwaitValue
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.bouncycastle.asn1.x500.style.RFC4519Style.title
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin



@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    //TODO: provide testing to the RemindersListViewModel and its live data objects
    private lateinit var viewModel: RemindersListViewModel
    private lateinit var dataSrc : FakeDataSource

    @get: Rule
    var coroutineRule = MainCoroutineRule()

    @get: Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    @Before
    fun viewModel() {
        dataSrc = FakeDataSource(buildReminderData().toMutableList())
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSrc)
    }

    @After
    fun finished() {
        stopKoin()
    }

    private fun buildReminderData(): List<ReminderDTO> {
        return listOf(
            ReminderDTO(
                "title",
                "Description", "Location", 18.743646142139145,
                -33.955688815535005
            ),
            ReminderDTO(
                "title",
                "Description", "Location", 18.743646142139145,
                -33.955688815535005

            ),
            ReminderDTO(
                "title",
                "Description", "Location", 18.743646142139145,
                -33.955688815535005
            ),
            ReminderDTO(
                "title",
                "Description", "Location", 18.743646142139145,
                -33.955688815535005
            )
        )
    }

    @Test
    fun load_getReminders(){

        val reminders = buildReminderData()

        val viewModel1 = viewModel.loadReminders()

        val value1 = viewModel.remindersList.getOrAwaitValue()
        assertThat(value1[0].title, `is`("title"))

    }
}