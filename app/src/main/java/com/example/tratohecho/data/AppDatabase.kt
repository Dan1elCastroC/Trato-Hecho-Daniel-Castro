package com.example.tratohecho.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Cliente::class, Cotizacion::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun clienteDao(): ClienteDao
    abstract fun cotizacionDao(): CotizacionDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        /** Añade líneas múltiples conservando los registros anteriores. */
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cotizaciones ADD COLUMN lineasJson TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "trato_hecho_mobile.db"
                ).addMigrations(MIGRATION_1_2).build().also { instance = it }
            }
        }
    }
}
