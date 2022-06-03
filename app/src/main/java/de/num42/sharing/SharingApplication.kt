package de.num42.sharing

import android.app.Application
import de.num42.sharing.apollo.ApolloInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

val apolloInstance = ApolloInstance()

class SharingApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

}
