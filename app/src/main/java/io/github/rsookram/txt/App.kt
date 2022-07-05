package io.github.rsookram.txt

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.rsookram.txt.reader.AppDatabase
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun dao(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "str.db")
            .build()
            .progressDao()

    @BgContext
    @Provides
    fun bgContext(): CoroutineContext = Dispatchers.IO
}

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BgContext
