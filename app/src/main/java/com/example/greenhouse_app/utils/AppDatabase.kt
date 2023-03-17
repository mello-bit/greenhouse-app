package com.example.greenhouse_app.utils

import android.content.Context
import androidx.room.*

@Entity(tableName = "sensor_snapshots")
data class SensorData(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "snapshot_date", typeAffinity = ColumnInfo.TEXT)
    val createdAt: String,

    @ColumnInfo(name = "greenhouse_temperature", typeAffinity = ColumnInfo.REAL)
    val greenhouse_temperature: Float,

    @ColumnInfo(name = "greenhouse_humidity", typeAffinity = ColumnInfo.REAL)
    val greenhouse_humidity: Float,

    @ColumnInfo(name = "furrow1_humidity", typeAffinity = ColumnInfo.REAL)
    val furrow1_humidity: Float,

    @ColumnInfo(name = "furrow2_humidity", typeAffinity = ColumnInfo.REAL)
    val furrow2_humidity: Float,

    @ColumnInfo(name = "furrow3_humidity", typeAffinity = ColumnInfo.REAL)
    val furrow3_humidity: Float,

    @ColumnInfo(name = "furrow4_humidity", typeAffinity = ColumnInfo.REAL)
    val furrow4_humidity: Float,

    @ColumnInfo(name = "furrow5_humidity", typeAffinity = ColumnInfo.REAL)
    val furrow5_humidity: Float,

    @ColumnInfo(name = "furrow6_humidity", typeAffinity = ColumnInfo.REAL)
    val furrow6_humidity: Float,
)

@Dao
interface SensorDao {
    @Insert
    fun insertData(sensorData: SensorData)

    @Query("SELECT COUNT(*) FROM sensor_snapshots")
    fun getCount(): Int

    @Query("SELECT * FROM sensor_snapshots WHERE date(snapshot_date) = date(:date)")
    fun getSensorDataForDate(date: String): List<SensorData>

    @Query("SELECT * FROM sensor_snapshots WHERE datetime(snapshot_date) BETWEEN datetime(:startDate) AND datetime(:endDate)")
    fun getSensorDataBetweenDates(startDate: String, endDate: String): List<SensorData>

    @Query("SELECT * FROM sensor_snapshots ORDER BY id DESC LIMIT 1")
    fun getLatestSensorData(): SensorData?

    @Query("DELETE FROM sensor_snapshots WHERE id = :id")
    fun deleteDataById(id: Long)

    @Query("DELETE FROM sensor_snapshots WHERE datetime(snapshot_date) <= datetime(:cutoffDate)")
    fun deleteOlderThan(cutoffDate: String)
}

@Database(entities = [SensorData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun SensorDao(): SensorDao
}

object AppDatabaseHelper {
    private val databases = mutableMapOf<String, AppDatabase>()

    fun getDatabase(context: Context, userId: String): AppDatabase {
        if (databases.containsKey(userId)) {
            return databases[userId]!!
        }
        val databaseName = "SensorData_$userId.db"
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            databaseName
        ).build()
        databases[userId] = db
        return db
    }
}