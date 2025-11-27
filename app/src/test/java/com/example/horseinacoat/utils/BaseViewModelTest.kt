package com.example.horseinacoat.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseViewModelTest {

    protected val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val viewModelTestRule = TestRuleProvider.createViewModelTestRule(testDispatcher)

    protected val testDispatchers: TestDispatchers = TestDispatchers(testDispatcher)
}