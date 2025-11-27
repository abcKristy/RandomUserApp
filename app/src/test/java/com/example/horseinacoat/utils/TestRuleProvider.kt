package com.example.horseinacoat.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@OptIn(ExperimentalCoroutinesApi::class)
object TestRuleProvider {

    fun createMainDispatcherRule(
        testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    ): MainDispatcherRule {
        return MainDispatcherRule(testDispatcher)
    }

    fun createViewModelTestRule(
        testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
    ): TestRule {
        return object : TestRule {
            private val instantTaskExecutorRule = androidx.arch.core.executor.testing.InstantTaskExecutorRule()
            private val mainDispatcherRule = MainDispatcherRule(testDispatcher)

            override fun apply(base: Statement, description: Description): Statement {
                return instantTaskExecutorRule.apply(
                    mainDispatcherRule.apply(base, description),
                    description
                )
            }
        }
    }
}