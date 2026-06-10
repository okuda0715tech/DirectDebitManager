package com.kurodai0715.directdebitmanager.di

import com.kurodai0715.directdebitmanager.data.PaymentDefaultRepository
import com.kurodai0715.directdebitmanager.data.PaymentV2Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindPaymentRepository(
        impl: PaymentDefaultRepository
    ): PaymentV2Repository
}
