package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction

@Entity(tableName = "households")
data class Household(
    @PrimaryKey val householdId: String,
    val familyType: String,
    val totalFamilyMembers: Int,
    val monthlyIncome: Double
)

@Entity(
    tableName = "children",
    foreignKeys = [
        ForeignKey(
            entity = Household::class,
            parentColumns = ["householdId"],
            childColumns = ["householdId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["csamId"], unique = true),
        Index(value = ["householdId"])
    ]
)
data class Child(
    @PrimaryKey val studyId: String,
    val csamId: String,
    val householdId: String,
    val childName: String,
    val sex: String,
    val awcName: String,
    val sectorBlockDistrict: String
)

@Entity(
    tableName = "baseline_assessments",
    foreignKeys = [
        ForeignKey(
            entity = Child::class,
            parentColumns = ["studyId"],
            childColumns = ["studyId"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class BaselineAssessment(
    @PrimaryKey val studyId: String,
    val weightEnrolment: Double,
    val weight12Weeks: Double,
    val heightEnrolment: Double,
    val height12Weeks: Double,
    val programmeOutcome: String,
    val thrReceivedTreatment: String,
    val thrConsumedDays7d: Int,
    val dietaryScore24h: Int,
    val extraDataJson: String
)

@Entity(
    tableName = "longitudinal_followups",
    foreignKeys = [
        ForeignKey(
            entity = Child::class,
            parentColumns = ["studyId"],
            childColumns = ["studyId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["studyId", "followupRound"], unique = true)]
)
data class LongitudinalFollowup(
    @PrimaryKey(autoGenerate = true) val followupId: Int = 0,
    val studyId: String,
    val followupRound: String, // 'M9', 'M12', or 'M15'
    val currentStatus: String, // 'Alive' or 'Dead'
    val weight: Double,
    val height: Double,
    val whz: Double,
    val thrReceived: String,
    val thrConsumedDays7d: Int,
    val dietaryScore24h: Int,
    val extraDataJson: String
)

@Dao
interface SamDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHousehold(household: Household)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChild(child: Child)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBaseline(baseline: BaselineAssessment)

    @Transaction
    suspend fun logBaselineTransaction(household: Household, child: Child, baseline: BaselineAssessment) {
        insertHousehold(household)
        insertChild(child)
        insertBaseline(baseline)
    }

    @Query("SELECT * FROM children WHERE studyId = :query OR csamId = :query OR childName LIKE '%' || :query || '%' LIMIT 1")
    suspend fun getChildByStudyOrCsamId(query: String): Child?

    @Query("SELECT * FROM children ORDER BY childName ASC")
    fun getAllChildren(): kotlinx.coroutines.flow.Flow<List<Child>>

    @Query("SELECT * FROM households")
    suspend fun getAllHouseholds(): List<Household>

    @Query("SELECT * FROM children")
    suspend fun getAllChildrenList(): List<Child>

    @Query("SELECT * FROM baseline_assessments")
    suspend fun getAllBaselines(): List<BaselineAssessment>

    @Query("SELECT * FROM longitudinal_followups")
    suspend fun getAllFollowups(): List<LongitudinalFollowup>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertFollowup(followup: LongitudinalFollowup)
}

@Database(
    entities = [Household::class, Child::class, BaselineAssessment::class, LongitudinalFollowup::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun samDao(): SamDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                // Initialize database and enforce PRAGMA foreign_keys = ON via fallbackToDestructiveMigration if needed 
                // Note: Room enables foreign keys by default on standard configurations, 
                // but setting a specific callback can verify or explicitly execute.
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sam_tracking_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
