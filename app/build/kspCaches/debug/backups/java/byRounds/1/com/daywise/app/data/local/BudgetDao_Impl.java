package com.daywise.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.daywise.app.data.model.MonthlyBudgetEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BudgetDao_Impl implements BudgetDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<MonthlyBudgetEntity> __insertionAdapterOfMonthlyBudgetEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBudget;

  public BudgetDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfMonthlyBudgetEntity = new EntityInsertionAdapter<MonthlyBudgetEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `monthly_budgets` (`year_month`,`budget_paisa`) VALUES (?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final MonthlyBudgetEntity entity) {
        statement.bindString(1, entity.getYearMonth());
        statement.bindLong(2, entity.getBudgetPaisa());
      }
    };
    this.__preparedStmtOfDeleteBudget = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM monthly_budgets WHERE year_month = ?";
        return _query;
      }
    };
  }

  @Override
  public Object setBudget(final MonthlyBudgetEntity budget,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMonthlyBudgetEntity.insert(budget);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertBudgets(final List<MonthlyBudgetEntity> budgets,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfMonthlyBudgetEntity.insert(budgets);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBudget(final String yearMonth, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBudget.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, yearMonth);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteBudget.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<MonthlyBudgetEntity> getBudgetForMonthFlow(final String yearMonth) {
    final String _sql = "SELECT * FROM monthly_budgets WHERE year_month = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, yearMonth);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"monthly_budgets"}, new Callable<MonthlyBudgetEntity>() {
      @Override
      @Nullable
      public MonthlyBudgetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "year_month");
          final int _cursorIndexOfBudgetPaisa = CursorUtil.getColumnIndexOrThrow(_cursor, "budget_paisa");
          final MonthlyBudgetEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpYearMonth;
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth);
            final long _tmpBudgetPaisa;
            _tmpBudgetPaisa = _cursor.getLong(_cursorIndexOfBudgetPaisa);
            _result = new MonthlyBudgetEntity(_tmpYearMonth,_tmpBudgetPaisa);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getBudgetForMonth(final String yearMonth,
      final Continuation<? super MonthlyBudgetEntity> $completion) {
    final String _sql = "SELECT * FROM monthly_budgets WHERE year_month = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, yearMonth);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<MonthlyBudgetEntity>() {
      @Override
      @Nullable
      public MonthlyBudgetEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "year_month");
          final int _cursorIndexOfBudgetPaisa = CursorUtil.getColumnIndexOrThrow(_cursor, "budget_paisa");
          final MonthlyBudgetEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpYearMonth;
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth);
            final long _tmpBudgetPaisa;
            _tmpBudgetPaisa = _cursor.getLong(_cursorIndexOfBudgetPaisa);
            _result = new MonthlyBudgetEntity(_tmpYearMonth,_tmpBudgetPaisa);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllBudgets(final Continuation<? super List<MonthlyBudgetEntity>> $completion) {
    final String _sql = "SELECT * FROM monthly_budgets";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<MonthlyBudgetEntity>>() {
      @Override
      @NonNull
      public List<MonthlyBudgetEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfYearMonth = CursorUtil.getColumnIndexOrThrow(_cursor, "year_month");
          final int _cursorIndexOfBudgetPaisa = CursorUtil.getColumnIndexOrThrow(_cursor, "budget_paisa");
          final List<MonthlyBudgetEntity> _result = new ArrayList<MonthlyBudgetEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final MonthlyBudgetEntity _item;
            final String _tmpYearMonth;
            _tmpYearMonth = _cursor.getString(_cursorIndexOfYearMonth);
            final long _tmpBudgetPaisa;
            _tmpBudgetPaisa = _cursor.getLong(_cursorIndexOfBudgetPaisa);
            _item = new MonthlyBudgetEntity(_tmpYearMonth,_tmpBudgetPaisa);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
