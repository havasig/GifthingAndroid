package hu.bme.aut.android.gifthing

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import hu.bme.aut.android.gifthing.models.User

object AppPreferences {
    private var sharedPreferences: SharedPreferences? = null

    fun setup(context: Context) {
        sharedPreferences = context.getSharedPreferences("gifthing.sharedprefs", MODE_PRIVATE)
    }

    var token: String?
        get() = Key.TOKEN.getString()
        set(value) = Key.TOKEN.setString(value)

    var username: String?
        get() = Key.USERNAME.getString()
        set(value) = Key.USERNAME.setString(value)

    var email: String?
        get() = Key.EMAIL.getString()
        set(value) = Key.EMAIL.setString(value)

    var currentId: Long?
        get() = Key.CURRENT_ID.getLong()
        set(value) = Key.CURRENT_ID.setLong(value)

    var roles: Set<String>?
        get() = Key.ROLES.getStringSet()
        set(value) = Key.ROLES.setStringSet(value)

    private enum class Key {
        TOKEN, CURRENT_ID, ROLES, USERNAME, EMAIL;

        fun getBoolean(): Boolean? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getBoolean(name, false) else null
        fun getFloat(): Float? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getFloat(name, 0f) else null
        fun getInt(): Int? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getInt(name, 0) else null
        fun getLong(): Long? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getLong(name, 0) else null
        fun getString(): String? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getString(name, "") else null
        fun getStringSet(): Set<String>? = if (sharedPreferences!!.contains(name)) sharedPreferences!!.getStringSet(name, HashSet<String>()) else null

        fun setBoolean(value: Boolean?) = value?.let { sharedPreferences!!.edit { putBoolean(name, value) } } ?: remove()
        fun setFloat(value: Float?) = value?.let { sharedPreferences!!.edit { putFloat(name, value) } } ?: remove()
        fun setInt(value: Int?) = value?.let { sharedPreferences!!.edit { putInt(name, value) } } ?: remove()
        fun setLong(value: Long?) = value?.let { sharedPreferences!!.edit { putLong(name, value) } } ?: remove()
        fun setString(value: String?) = value?.let { sharedPreferences!!.edit { putString(name, value) } } ?: remove()
        fun setStringSet(value: Set<String>?) = value?.let { sharedPreferences!!.edit { putStringSet(name, value) } } ?: remove()

        fun remove() = sharedPreferences!!.edit { remove(name) }
    }
}