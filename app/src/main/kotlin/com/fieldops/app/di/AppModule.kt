package com.fieldops.app.di

import android.content.Context
import com.fieldops.app.data.local.FieldOpsDatabase
import com.fieldops.app.data.local.dao.AssetDao
import com.fieldops.app.data.local.dao.IncidentDao
import com.fieldops.app.data.remote.api.FieldOpsApiService
import com.fieldops.app.data.remote.api.RetrofitInstance
import com.fieldops.app.data.repository.AssetRepository
import com.fieldops.app.data.repository.IncidentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FieldOpsDatabase =
        FieldOpsDatabase.getDatabase(context)

    @Provides
    fun provideIncidentDao(db: FieldOpsDatabase): IncidentDao = db.incidentDao()

    @Provides
    fun provideAssetDao(db: FieldOpsDatabase): AssetDao = db.assetDao()

    @Provides
    @Singleton
    fun provideApiService(): FieldOpsApiService = RetrofitInstance.apiService

    @Provides
    @Singleton
    fun provideIncidentRepository(
        incidentDao: IncidentDao,
        apiService: FieldOpsApiService
    ): IncidentRepository = IncidentRepository(incidentDao, apiService)

    @Provides
    @Singleton
    fun provideAssetRepository(
        assetDao: AssetDao,
        apiService: FieldOpsApiService
    ): AssetRepository = AssetRepository(assetDao, apiService)
}
