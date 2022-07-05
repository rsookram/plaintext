package io.github.rsookram.txt.reader

import android.content.Context
import androidx.room.*
import io.github.rsookram.txt.Book

@Entity(
    tableName = "progress"
)
data class Progress(
    @PrimaryKey @ColumnInfo(name = "key") val key: String,
    @ColumnInfo(name = "value") val value: Int
)

@Database(entities = [Progress::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun progressDao(): ProgressDao
}

@Dao
abstract class ProgressDao {

    suspend fun get(book: Book): Int? {
        val key = key(book) ?: return null
        return getByKey(key)?.value
    }

    @Query("SELECT * FROM progress WHERE `key` = :key")
    protected abstract suspend fun getByKey(key: String): Progress?

    suspend fun set(book: Book, progress: Int) {
        val key = key(book) ?: return
        insert(Progress(key, progress))
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insert(progress: Progress)

    private fun key(book: Book): String? = book.uri.path

    companion object {

        private lateinit var db: AppDatabase

        fun getInstance(context: Context) {
            db = Room.databaseBuilder(context, AppDatabase::class.java, "txt.db").build()

            db.progressDao()
        }
    }
}
