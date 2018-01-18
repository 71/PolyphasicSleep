package `is`.gregoirege.polyphasicsleep

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class NapsDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "Naps", null, 1) {
    companion object {
        private var instance: NapsDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): NapsDatabaseOpenHelper {
            if (instance == null) {
                instance = NapsDatabaseOpenHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable("Schedule", true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "minute" to INTEGER,
                "duration" to INTEGER)
        db.createTable("History", true,
                "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                "minute" to INTEGER,
                "duration" to INTEGER)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable("Schedule", true)
        db.dropTable("History", true)
    }
}


val Context.db: NapsDatabaseOpenHelper
    get() = NapsDatabaseOpenHelper.getInstance(applicationContext)

val SQLiteDatabase.schedule: SelectQueryBuilder
    get() = select("Schedule")

val SQLiteDatabase.history: SelectQueryBuilder
    get() = select("History")
