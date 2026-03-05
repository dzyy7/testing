package id.co.psplauncher.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.co.psplauncher.data.network.RemoteDataSource
import id.co.psplauncher.data.network.auth.AuthApi
import id.co.psplauncher.data.network.balance.BalanceApi
import id.co.psplauncher.data.network.cart.ShoppingCartApi
import id.co.psplauncher.data.network.chip.ChipApi
import id.co.psplauncher.data.network.dashboard.DashboardApi
import id.co.psplauncher.data.network.notification.NotificationApi
import id.co.psplauncher.data.network.transaction.TransactionApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAuthApi(remoteDataSource: RemoteDataSource): AuthApi {
        return remoteDataSource.buildApi(AuthApi::class.java)
    }

    @Provides
    fun provideChipApi(remoteDataSource: RemoteDataSource): ChipApi {
        return remoteDataSource.buildApi(ChipApi::class.java)
    }

    @Provides
    fun provideDashboardApi(remoteDataSource: RemoteDataSource): DashboardApi {
        return remoteDataSource.buildApi(DashboardApi::class.java)
    }

    @Provides
    fun provideBalanceApi(remoteDataSource: RemoteDataSource): BalanceApi {
        return remoteDataSource.buildApi(BalanceApi::class.java)
    }

    @Provides
    fun provideShoppingCartApi(remoteDataSource: RemoteDataSource): ShoppingCartApi {
        return remoteDataSource.buildApi(ShoppingCartApi::class.java)
    }

    @Provides
    fun provideTransactionApi(remoteDataSource: RemoteDataSource): TransactionApi {
        return remoteDataSource.buildApi(TransactionApi::class.java)
    }

    @Provides
    fun provideNotificationApi(remoteDataSource: RemoteDataSource): NotificationApi {
        return remoteDataSource.buildApi(NotificationApi::class.java)
    }
}