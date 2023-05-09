package com.codinginflow.mvvmtodo.di

import android.app.Application
import androidx.room.Room
import com.codinginflow.mvvmtodo.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    // Provides the TaskDatabase instance as a singleton and sets a callback for pre-populating the database
    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: TaskDatabase.Callback
    ) = Room.databaseBuilder(app, TaskDatabase::class.java, "task_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    // Provides the TaskDao instance
    @Provides
    fun provideTaskDao(db: TaskDatabase) = db.taskDao()

    // Provides a coroutine scope as a singleton for use in the application
    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}
// Defines a custom qualifier for the application scope
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
