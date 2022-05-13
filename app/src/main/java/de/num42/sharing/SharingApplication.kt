package de.num42.sharing

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SharingApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
}
