package com.zerodsoft.tripweather.Room;

import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.zerodsoft.tripweather.Room.DAO.AreaDao;

import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AreaDb_Impl extends AreaDb
{
  private volatile AreaDao _areaDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `area_table` (`area_id` INTEGER NOT NULL, `phase_1` TEXT, `phase_2` TEXT, `phase_3` TEXT, `x` TEXT, `y` TEXT, PRIMARY KEY(`area_id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '273f2ab43ed05a3e7ff5c04854da9a0d')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `area_table`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      protected RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsAreaTable = new HashMap<String, TableInfo.Column>(6);
        _columnsAreaTable.put("area_id", new TableInfo.Column("area_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAreaTable.put("phase_1", new TableInfo.Column("phase_1", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAreaTable.put("phase_2", new TableInfo.Column("phase_2", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAreaTable.put("phase_3", new TableInfo.Column("phase_3", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAreaTable.put("x", new TableInfo.Column("x", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAreaTable.put("y", new TableInfo.Column("y", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAreaTable = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAreaTable = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAreaTable = new TableInfo("area_table", _columnsAreaTable, _foreignKeysAreaTable, _indicesAreaTable);
        final TableInfo _existingAreaTable = TableInfo.read(_db, "area_table");
        if (! _infoAreaTable.equals(_existingAreaTable)) {
          return new RoomOpenHelper.ValidationResult(false, "area_table(com.zerodsoft.tripweather.Room.DTO.Area).\n"
                  + " Expected:\n" + _infoAreaTable + "\n"
                  + " Found:\n" + _existingAreaTable);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "273f2ab43ed05a3e7ff5c04854da9a0d", "b3e7831f4d84f51e6e559883bae1e814");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "area_table");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `area_table`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  public AreaDao areaDao() {
    if (_areaDao != null) {
      return _areaDao;
    } else {
      synchronized(this) {
        if(_areaDao == null) {
          _areaDao = new AreaDao_Impl(this);
        }
        return _areaDao;
      }
    }
  }
}
