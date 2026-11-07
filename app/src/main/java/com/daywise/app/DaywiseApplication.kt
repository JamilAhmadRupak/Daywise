package com.daywise.app

import android.app.Application
import com.daywise.app.data.local.DaywiseDatabase
import com.daywise.app.data.repository.ExpenseRepository

class DaywiseApplication : Application() {

    lateinit var database: DaywiseDatabase
        private set

    lateinit var repository: ExpenseRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = DaywiseDatabase.getInstance(this)
        repository = ExpenseRepository(
            expenseDao = database.expenseDao(),
            budgetDao = database.budgetDao(),
            debtDao = database.debtDao()
        )
    }
}
