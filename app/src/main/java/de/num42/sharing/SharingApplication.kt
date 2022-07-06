package de.num42.sharing

import android.app.Application
import de.num42.sharing.apollo.ApolloInstance
import de.num42.sharing.database.Repository
import de.num42.sharing.database.SharingDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

val apolloInstance = ApolloInstance()

class SharingApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { SharingDB.getDatabase(this, applicationScope) }
    val repository by lazy { Repository(database.itemDAO()) }

}
