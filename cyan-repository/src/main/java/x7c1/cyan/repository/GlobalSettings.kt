package x7c1.cyan.repository

import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.sharedpreferences.Pref
import org.androidannotations.annotations.sharedpreferences.SharedPref

@SharedPref(value = SharedPref.Scope.UNIQUE)
interface GlobalPrefs {
    fun currentJobcanCode(): String?
}

@EBean
class GlobalSettings {

    @Pref
    lateinit var internalPref: GlobalPrefs_

    fun getCurrentCode(): String? {
        return internalPref.currentJobcanCode().get()
    }

    fun updateCurrentCode(code: String) {
        internalPref.currentJobcanCode().put(code)
    }

}
