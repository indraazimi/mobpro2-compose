package com.indraazimi.mobpro2

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.lifecycle.coroutineScope
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseUser
import com.indraazimi.mobpro2.data.DataDB
import com.indraazimi.mobpro2.data.DataDao
import com.indraazimi.mobpro2.navigation.SetupNavGraph
import com.indraazimi.mobpro2.types.TestingPathConfig
import com.indraazimi.mobpro2.ui.screen.AddClassScreen
import com.indraazimi.mobpro2.ui.screen.ModuleListScreen
import com.indraazimi.mobpro2utils.models.Dosen
import com.indraazimi.mobpro2utils.models.Kelas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Testing {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        DataDB.getInstance(
            pathConfig = TestingPathConfig()
        ).clear()
    }

    @Test
    fun addClassScreenTest() {
        val fakeUser = mutableStateOf<FirebaseUser?>(
            null
        )

        val dataDao: DataDao = DataDB.getInstance(
            pathConfig = TestingPathConfig()
        ).dao

        dataDao.addDosen(
            Dosen(
                id = "1",
                nama = "Test",
                kodeDosen = "123"
            )
        )

        composeTestRule.setContent {
            val navController = rememberNavController()
            SetupNavGraph(navController = navController, user = fakeUser, modifier = Modifier)
            AddClassScreen(navController = navController, user = fakeUser, modifier = Modifier)
        }

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.name
            )
        ).performTextInput("Test Class")

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.save
            )
        ).performClick()

        composeTestRule.onNodeWithText(
            "Test Class"
        ).assertExists()
    }

    @Test
    fun actionModeTest() = runTest {
        val fakeUser = mutableStateOf<FirebaseUser?>(null)

        val dataDao: DataDao = DataDB.getInstance(
            pathConfig = TestingPathConfig()
        ).dao

        dataDao.addDosen(
            Dosen(
                id = "1",
                nama = "Test",
                kodeDosen = "123"
            )
        )

        dataDao.addKelas(
            "1",
            Kelas(
                id = "111",
                nama = "Test Class"
            )
        )

        val _kelas: MutableStateFlow<List<Kelas>> = MutableStateFlow(emptyList())
        val kelas: StateFlow<List<Kelas>> = _kelas.asStateFlow()

        composeTestRule.activity.lifecycle.coroutineScope.launch {
            dataDao.getKelasByDosenID("1").collect { result ->
                _kelas.value = result
            }
        }

        composeTestRule.waitUntil {
            kelas.value.size > 0
        }

        val classId = kelas.value.firstOrNull()?.id

        composeTestRule.setContent {
            val navController = rememberNavController()
            SetupNavGraph(navController = navController, user = fakeUser, modifier = Modifier)
            ModuleListScreen(navController = navController, user = fakeUser, modifier = Modifier, classId = classId ?: "")
        }

        composeTestRule.onNodeWithTag(
            "add_module_fab"
        ).performClick()

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.title
            )
        ).performTextInput(
            "Module 1"
        )

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.save
            )
        ).performClick()

        composeTestRule.onNodeWithTag(
            "add_module_fab"
        ).performClick()

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.title
            )
        ).performTextInput(
            "Module 2"
        )

        composeTestRule.onNodeWithText(
            composeTestRule.activity.getString(
                R.string.save
            )
        ).performClick()

        composeTestRule.onNodeWithTag(
            "module_list"
        )
        .onChildAt(0)
        .performTouchInput {
            longClick()
        }

        composeTestRule.onNodeWithContentDescription("Edit").assertExists()
        composeTestRule.onNodeWithContentDescription("Delete").assertExists()

        composeTestRule.onNodeWithTag(
            "module_list"
        )
        .onChildAt(1)
        .performClick()

        composeTestRule.onNodeWithContentDescription("Edit").assertDoesNotExist()
    }
}
