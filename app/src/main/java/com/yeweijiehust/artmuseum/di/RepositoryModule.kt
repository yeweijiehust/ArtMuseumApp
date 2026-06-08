package com.yeweijiehust.artmuseum.di

import com.yeweijiehust.artmuseum.data.repository.AuthRepositoryImpl
import com.yeweijiehust.artmuseum.data.repository.EndpointRepositoryImpl
import com.yeweijiehust.artmuseum.data.repository.GalleryRepositoryImpl
import com.yeweijiehust.artmuseum.data.repository.PreferencesRepositoryImpl
import com.yeweijiehust.artmuseum.domain.repository.AuthRepository
import com.yeweijiehust.artmuseum.domain.repository.EndpointRepository
import com.yeweijiehust.artmuseum.domain.repository.GalleryRepository
import com.yeweijiehust.artmuseum.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun endpoint(impl: EndpointRepositoryImpl): EndpointRepository

    @Binds
    @Singleton
    abstract fun preferences(impl: PreferencesRepositoryImpl): PreferencesRepository

    @Binds
    @Singleton
    abstract fun auth(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun gallery(impl: GalleryRepositoryImpl): GalleryRepository
}
