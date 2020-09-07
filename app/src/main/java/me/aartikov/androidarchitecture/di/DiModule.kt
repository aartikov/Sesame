package me.aartikov.androidarchitecture.di

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import me.aartikov.androidarchitecture.base.StoreLoading
import me.aartikov.androidarchitecture.profile.data.ProfileGateway
import me.aartikov.androidarchitecture.profile.data.ProfileStorage
import me.aartikov.androidarchitecture.profile.domain.Profile
import me.aartikov.lib.loading.simple.Loading

@Module
@InstallIn(ApplicationComponent::class)
object DiModule {

    @Provides
    fun provideProfileStore(profileGateway: ProfileGateway, profileStorage: ProfileStorage): Store<Unit, Profile> {
        return StoreBuilder
            .from<Unit, Profile, Profile>(
                fetcher = Fetcher.of { profileGateway.loadProfile() },
                sourceOfTruth = SourceOfTruth.of(
                    reader = { profileStorage.getProfile() },
                    writer = { _, profile -> profileStorage.saveProfile(profile) },
                    delete = { profileStorage.removeProfile() },
                    deleteAll = { profileStorage.removeProfile() }
                )
            )
            .build()
    }

    @Provides
    fun provideProfileLoading(profileStore: Store<Unit, Profile>): Loading<Profile> {
        return StoreLoading(profileStore)
    }
}