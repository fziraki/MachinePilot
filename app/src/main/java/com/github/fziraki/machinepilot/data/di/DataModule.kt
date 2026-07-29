package com.github.fziraki.machinepilot.data.di

import com.github.fziraki.machinepilot.data.repository.MachineRepositoryImpl
import com.github.fziraki.machinepilot.domain.repository.MachineRepository
import com.github.fziraki.machinepilot.machinesdk.sdk.FakeMachineClient
import com.github.fziraki.machinepilot.machinesdk.sdk.MachineClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindMachineRepository(impl: MachineRepositoryImpl): MachineRepository

    companion object {
        @Provides
        @Singleton
        fun provideMachineClient(): MachineClient = FakeMachineClient()
    }
}
