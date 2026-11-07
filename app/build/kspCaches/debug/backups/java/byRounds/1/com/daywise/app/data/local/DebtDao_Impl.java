package com.daywise.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.daywise.app.data.model.DebtEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
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
public final class DebtDao_Impl implements DebtDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DebtEntity> __insertionAdapterOfDebtEntity;

  private final EntityDeletionOrUpdateAdapter<DebtEntity> __deletionAdapterOfDebtEntity;

  private final EntityDeletionOrUpdateAdapter<DebtEntity> __updateAdapterOfDebtEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteDebtById;

  private final SharedSQLiteStatement __preparedStmtOfUpdateSettledStatus;

  public DebtDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDebtEntity = new EntityInsertionAdapter<DebtEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `debts` (`id`,`person_name`,`amount_paisa`,`type`,`date_millis`,`due_date_millis`,`note`,`is_settled`,`settled_at`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPersonName());
        statement.bindLong(3, entity.getAmountPaisa());
        statement.bindString(4, entity.getType());
        if (entity.getDateMillis() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getDateMillis());
        }
        if (entity.getDueDateMillis() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getDueDateMillis());
        }
        statement.bindString(7, entity.getNote());
        final int _tmp = entity.isSettled() ? 1 : 0;
        statement.bindLong(8, _tmp);
        if (entity.getSettledAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getSettledAt());
        }
        statement.bindLong(10, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfDebtEntity = new EntityDeletionOrUpdateAdapter<DebtEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `debts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfDebtEntity = new EntityDeletionOrUpdateAdapter<DebtEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `debts` SET `id` = ?,`person_name` = ?,`amount_paisa` = ?,`type` = ?,`date_millis` = ?,`due_date_millis` = ?,`note` = ?,`is_settled` = ?,`settled_at` = ?,`created_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DebtEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getPersonName());
        statement.bindLong(3, entity.getAmountPaisa());
        statement.bindString(4, entity.getType());
        if (entity.getDateMillis() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getDateMillis());
        }
        if (entity.getDueDateMillis() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getDueDateMillis());
        }
        statement.bindString(7, entity.getNote());
        final int _tmp = entity.isSettled() ? 1 : 0;
        statement.bindLong(8, _tmp);
        if (entity.getSettledAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getSettledAt());
        }
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteDebtById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM debts WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateSettledStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE debts SET is_settled = ?, settled_at = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertDebt(final DebtEntity debt, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDebtEntity.insertAndReturnId(debt);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertDebts(final List<DebtEntity> debts,
      final Continuation<? super List<Long>> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        __db.beginTransaction();
        try {
          final List<Long> _result = __insertionAdapterOfDebtEntity.insertAndReturnIdsList(debts);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDebt(final DebtEntity debt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDebtEntity.handle(debt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDebt(final DebtEntity debt, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDebtEntity.handle(debt);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteDebtById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteDebtById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeleteDebtById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateSettledStatus(final long id, final boolean isSettled, final Long settledAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateSettledStatus.acquire();
        int _argIndex = 1;
        final int _tmp = isSettled ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        if (settledAt == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, settledAt);
        }
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfUpdateSettledStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getDebtById(final long id, final Continuation<? super DebtEntity> $completion) {
    final String _sql = "SELECT * FROM debts WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DebtEntity>() {
      @Override
      @Nullable
      public DebtEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "person_name");
          final int _cursorIndexOfAmountPaisa = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_paisa");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "date_millis");
          final int _cursorIndexOfDueDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date_millis");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_settled");
          final int _cursorIndexOfSettledAt = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final DebtEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPersonName;
            _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            final long _tmpAmountPaisa;
            _tmpAmountPaisa = _cursor.getLong(_cursorIndexOfAmountPaisa);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final Long _tmpDateMillis;
            if (_cursor.isNull(_cursorIndexOfDateMillis)) {
              _tmpDateMillis = null;
            } else {
              _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            }
            final Long _tmpDueDateMillis;
            if (_cursor.isNull(_cursorIndexOfDueDateMillis)) {
              _tmpDueDateMillis = null;
            } else {
              _tmpDueDateMillis = _cursor.getLong(_cursorIndexOfDueDateMillis);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsSettled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp != 0;
            final Long _tmpSettledAt;
            if (_cursor.isNull(_cursorIndexOfSettledAt)) {
              _tmpSettledAt = null;
            } else {
              _tmpSettledAt = _cursor.getLong(_cursorIndexOfSettledAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new DebtEntity(_tmpId,_tmpPersonName,_tmpAmountPaisa,_tmpType,_tmpDateMillis,_tmpDueDateMillis,_tmpNote,_tmpIsSettled,_tmpSettledAt,_tmpCreatedAt);
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
  public Flow<List<DebtEntity>> getAllDebtsFlow() {
    final String _sql = "SELECT * FROM debts ORDER BY is_settled ASC, created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"debts"}, new Callable<List<DebtEntity>>() {
      @Override
      @NonNull
      public List<DebtEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "person_name");
          final int _cursorIndexOfAmountPaisa = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_paisa");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "date_millis");
          final int _cursorIndexOfDueDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date_millis");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_settled");
          final int _cursorIndexOfSettledAt = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<DebtEntity> _result = new ArrayList<DebtEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DebtEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPersonName;
            _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            final long _tmpAmountPaisa;
            _tmpAmountPaisa = _cursor.getLong(_cursorIndexOfAmountPaisa);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final Long _tmpDateMillis;
            if (_cursor.isNull(_cursorIndexOfDateMillis)) {
              _tmpDateMillis = null;
            } else {
              _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            }
            final Long _tmpDueDateMillis;
            if (_cursor.isNull(_cursorIndexOfDueDateMillis)) {
              _tmpDueDateMillis = null;
            } else {
              _tmpDueDateMillis = _cursor.getLong(_cursorIndexOfDueDateMillis);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsSettled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp != 0;
            final Long _tmpSettledAt;
            if (_cursor.isNull(_cursorIndexOfSettledAt)) {
              _tmpSettledAt = null;
            } else {
              _tmpSettledAt = _cursor.getLong(_cursorIndexOfSettledAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new DebtEntity(_tmpId,_tmpPersonName,_tmpAmountPaisa,_tmpType,_tmpDateMillis,_tmpDueDateMillis,_tmpNote,_tmpIsSettled,_tmpSettledAt,_tmpCreatedAt);
            _result.add(_item);
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
  public Object getAllDebts(final Continuation<? super List<DebtEntity>> $completion) {
    final String _sql = "SELECT * FROM debts ORDER BY is_settled ASC, created_at DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DebtEntity>>() {
      @Override
      @NonNull
      public List<DebtEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPersonName = CursorUtil.getColumnIndexOrThrow(_cursor, "person_name");
          final int _cursorIndexOfAmountPaisa = CursorUtil.getColumnIndexOrThrow(_cursor, "amount_paisa");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "date_millis");
          final int _cursorIndexOfDueDateMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "due_date_millis");
          final int _cursorIndexOfNote = CursorUtil.getColumnIndexOrThrow(_cursor, "note");
          final int _cursorIndexOfIsSettled = CursorUtil.getColumnIndexOrThrow(_cursor, "is_settled");
          final int _cursorIndexOfSettledAt = CursorUtil.getColumnIndexOrThrow(_cursor, "settled_at");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<DebtEntity> _result = new ArrayList<DebtEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DebtEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpPersonName;
            _tmpPersonName = _cursor.getString(_cursorIndexOfPersonName);
            final long _tmpAmountPaisa;
            _tmpAmountPaisa = _cursor.getLong(_cursorIndexOfAmountPaisa);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final Long _tmpDateMillis;
            if (_cursor.isNull(_cursorIndexOfDateMillis)) {
              _tmpDateMillis = null;
            } else {
              _tmpDateMillis = _cursor.getLong(_cursorIndexOfDateMillis);
            }
            final Long _tmpDueDateMillis;
            if (_cursor.isNull(_cursorIndexOfDueDateMillis)) {
              _tmpDueDateMillis = null;
            } else {
              _tmpDueDateMillis = _cursor.getLong(_cursorIndexOfDueDateMillis);
            }
            final String _tmpNote;
            _tmpNote = _cursor.getString(_cursorIndexOfNote);
            final boolean _tmpIsSettled;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSettled);
            _tmpIsSettled = _tmp != 0;
            final Long _tmpSettledAt;
            if (_cursor.isNull(_cursorIndexOfSettledAt)) {
              _tmpSettledAt = null;
            } else {
              _tmpSettledAt = _cursor.getLong(_cursorIndexOfSettledAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new DebtEntity(_tmpId,_tmpPersonName,_tmpAmountPaisa,_tmpType,_tmpDateMillis,_tmpDueDateMillis,_tmpNote,_tmpIsSettled,_tmpSettledAt,_tmpCreatedAt);
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

  @Override
  public Flow<Long> getActiveLentTotalFlow() {
    final String _sql = "SELECT COALESCE(SUM(amount_paisa), 0) FROM debts WHERE type = 'LENT' AND is_settled = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"debts"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
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
  public Flow<Long> getActiveBorrowedTotalFlow() {
    final String _sql = "SELECT COALESCE(SUM(amount_paisa), 0) FROM debts WHERE type = 'BORROWED' AND is_settled = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"debts"}, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
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
  public Flow<Integer> getActiveDebtCountFlow() {
    final String _sql = "SELECT COUNT(*) FROM debts WHERE is_settled = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"debts"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object getTotalDebtCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM debts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
