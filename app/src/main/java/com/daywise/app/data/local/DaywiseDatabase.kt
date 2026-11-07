package com.daywise.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.daywise.app.data.model.DebtEntity
import com.daywise.app.data.model.ExpenseEntity
import com.daywise.app.data.model.MonthlyBudgetEntity

@Database(
    entities = [
        ExpenseEntity::class,
        MonthlyBudgetEntity::class,
        DebtEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class DaywiseDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao
    abstract fun debtDao(): DebtDao

    companion object {
        private const val DATABASE_NAME = "daywise_expenses.db"

        @Volatile
        private var INSTANCE: DaywiseDatabase? = null

        /**
         * Migration from Schema 1 to 2:
         * Adds 'debts' table without deleting or modifying any existing expense/budget data.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `debts` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `person_name` TEXT NOT NULL,
                        `amount_paisa` INTEGER NOT NULL,
                        `type` TEXT NOT NULL,
                        `date_millis` INTEGER,
                        `due_date_millis` INTEGER,
                        `note` TEXT NOT NULL,
                        `is_settled` INTEGER NOT NULL,
                        `settled_at` INTEGER,
                        `created_at` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_debts_person_name` ON `debts` (`person_name`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_debts_type` ON `debts` (`type`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_debts_is_settled` ON `debts` (`is_settled`)")
            }
        }

        fun getInstance(context: Context): DaywiseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DaywiseDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
