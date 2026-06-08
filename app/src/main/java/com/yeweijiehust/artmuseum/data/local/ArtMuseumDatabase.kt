package com.yeweijiehust.artmuseum.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ImageEntity::class], version = 1, exportSchema = false)
abstract class ArtMuseumDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}
