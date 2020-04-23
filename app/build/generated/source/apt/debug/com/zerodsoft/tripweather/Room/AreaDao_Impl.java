package com.zerodsoft.tripweather.Room;

import android.database.Cursor;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;

import com.zerodsoft.tripweather.Room.DAO.AreaDao;
import com.zerodsoft.tripweather.Room.DTO.Area;
import com.zerodsoft.tripweather.WeatherData.Phase1Tuple;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AreaDao_Impl implements AreaDao
{
  private final RoomDatabase __db;

  public AreaDao_Impl(RoomDatabase __db) {
    this.__db = __db;
  }

  @Override
  public List<Phase1Tuple> getPhase1() {
    final String _sql = "SELECT area_id, phase_1 FROM area_table GROUP BY phase_1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfAreaId = CursorUtil.getColumnIndexOrThrow(_cursor, "area_id");
      final int _cursorIndexOfPhase1 = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_1");
      final List<Phase1Tuple> _result = new ArrayList<Phase1Tuple>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Phase1Tuple _item;
        _item = new Phase1Tuple();
        final int _tmpArea_id;
        _tmpArea_id = _cursor.getInt(_cursorIndexOfAreaId);
        _item.setArea_id(_tmpArea_id);
        final String _tmpPhase1;
        _tmpPhase1 = _cursor.getString(_cursorIndexOfPhase1);
        _item.setPhase1(_tmpPhase1);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Area> getPhase2(final String phase1) {
    final String _sql = "SELECT * FROM area_table WHERE phase_1 LIKE ? GROUP BY phase_2";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (phase1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, phase1);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfAreaId = CursorUtil.getColumnIndexOrThrow(_cursor, "area_id");
      final int _cursorIndexOfPhase1 = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_1");
      final int _cursorIndexOfPhase2 = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_2");
      final int _cursorIndexOfPhase3 = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_3");
      final int _cursorIndexOfX = CursorUtil.getColumnIndexOrThrow(_cursor, "x");
      final int _cursorIndexOfY = CursorUtil.getColumnIndexOrThrow(_cursor, "y");
      final List<Area> _result = new ArrayList<Area>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Area _item;
        _item = new Area();
        final int _tmpArea_id;
        _tmpArea_id = _cursor.getInt(_cursorIndexOfAreaId);
        _item.setArea_id(_tmpArea_id);
        final String _tmpPhase1;
        _tmpPhase1 = _cursor.getString(_cursorIndexOfPhase1);
        _item.setPhase1(_tmpPhase1);
        final String _tmpPhase2;
        _tmpPhase2 = _cursor.getString(_cursorIndexOfPhase2);
        _item.setPhase2(_tmpPhase2);
        final String _tmpPhase3;
        _tmpPhase3 = _cursor.getString(_cursorIndexOfPhase3);
        _item.setPhase3(_tmpPhase3);
        final String _tmpX;
        _tmpX = _cursor.getString(_cursorIndexOfX);
        _item.setX(_tmpX);
        final String _tmpY;
        _tmpY = _cursor.getString(_cursorIndexOfY);
        _item.setY(_tmpY);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public List<Area> getPhase3(final String phase1, final String phase2) {
    final String _sql = "SELECT * FROM area_table WHERE phase_1 LIKE ? AND phase_2 LIKE ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (phase1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, phase1);
    }
    _argIndex = 2;
    if (phase2 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, phase2);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfAreaId = CursorUtil.getColumnIndexOrThrow(_cursor, "area_id");
      final int _cursorIndexOfPhase1 = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_1");
      final int _cursorIndexOfPhase2 = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_2");
      final int _cursorIndexOfPhase3 = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_3");
      final int _cursorIndexOfX = CursorUtil.getColumnIndexOrThrow(_cursor, "x");
      final int _cursorIndexOfY = CursorUtil.getColumnIndexOrThrow(_cursor, "y");
      final List<Area> _result = new ArrayList<Area>(_cursor.getCount());
      while(_cursor.moveToNext()) {
        final Area _item;
        _item = new Area();
        final int _tmpArea_id;
        _tmpArea_id = _cursor.getInt(_cursorIndexOfAreaId);
        _item.setArea_id(_tmpArea_id);
        final String _tmpPhase1;
        _tmpPhase1 = _cursor.getString(_cursorIndexOfPhase1);
        _item.setPhase1(_tmpPhase1);
        final String _tmpPhase2;
        _tmpPhase2 = _cursor.getString(_cursorIndexOfPhase2);
        _item.setPhase2(_tmpPhase2);
        final String _tmpPhase3;
        _tmpPhase3 = _cursor.getString(_cursorIndexOfPhase3);
        _item.setPhase3(_tmpPhase3);
        final String _tmpX;
        _tmpX = _cursor.getString(_cursorIndexOfX);
        _item.setX(_tmpX);
        final String _tmpY;
        _tmpY = _cursor.getString(_cursorIndexOfY);
        _item.setY(_tmpY);
        _result.add(_item);
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }

  @Override
  public Area getXy(final String phase1, final String phase2, final String phase3) {
    final String _sql = "SELECT * FROM area_table WHERE phase_1 LIKE ? AND phase_2 LIKE ? AND phase_3 LIKE ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    if (phase1 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, phase1);
    }
    _argIndex = 2;
    if (phase2 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, phase2);
    }
    _argIndex = 3;
    if (phase3 == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, phase3);
    }
    __db.assertNotSuspendingTransaction();
    final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
    try {
      final int _cursorIndexOfAreaId = CursorUtil.getColumnIndexOrThrow(_cursor, "area_id");
      final int _cursorIndexOfPhase1 = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_1");
      final int _cursorIndexOfPhase2 = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_2");
      final int _cursorIndexOfPhase3 = CursorUtil.getColumnIndexOrThrow(_cursor, "phase_3");
      final int _cursorIndexOfX = CursorUtil.getColumnIndexOrThrow(_cursor, "x");
      final int _cursorIndexOfY = CursorUtil.getColumnIndexOrThrow(_cursor, "y");
      final Area _result;
      if(_cursor.moveToFirst()) {
        _result = new Area();
        final int _tmpArea_id;
        _tmpArea_id = _cursor.getInt(_cursorIndexOfAreaId);
        _result.setArea_id(_tmpArea_id);
        final String _tmpPhase1;
        _tmpPhase1 = _cursor.getString(_cursorIndexOfPhase1);
        _result.setPhase1(_tmpPhase1);
        final String _tmpPhase2;
        _tmpPhase2 = _cursor.getString(_cursorIndexOfPhase2);
        _result.setPhase2(_tmpPhase2);
        final String _tmpPhase3;
        _tmpPhase3 = _cursor.getString(_cursorIndexOfPhase3);
        _result.setPhase3(_tmpPhase3);
        final String _tmpX;
        _tmpX = _cursor.getString(_cursorIndexOfX);
        _result.setX(_tmpX);
        final String _tmpY;
        _tmpY = _cursor.getString(_cursorIndexOfY);
        _result.setY(_tmpY);
      } else {
        _result = null;
      }
      return _result;
    } finally {
      _cursor.close();
      _statement.release();
    }
  }
}
