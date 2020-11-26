package hu.bme.aut.android.gifthing.database.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.snakydesign.livedataextensions.emptyLiveData
import com.snakydesign.livedataextensions.liveDataOf
import com.snakydesign.livedataextensions.map
import com.snakydesign.livedataextensions.switchMap
import hu.bme.aut.android.gifthing.authentication.dto.LoginData
import hu.bme.aut.android.gifthing.database.models.entities.*
import hu.bme.aut.android.gifthing.database.repositories.UserRepository
import java.lang.Exception
import java.security.spec.ECField

class UserViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val mRepository: UserRepository = UserRepository(application)
    private var mCurrentUser: LiveData<UserWithTeams>? = null

    fun create(user: User) {
        mRepository.create(user)
    }

    fun getUserWithOwnedGifts(id: Long): LiveData<UserWithOwnedGifts> {
        return mRepository.getUserWithOwnedGifts(id)
    }

    fun getUserWithReservedGifts(id: Long): LiveData<UserWithReservedGifts> {
        return mRepository.getUserWithReservedGifts(id)
    }

    fun getById(id: Long): LiveData<User> {
        return mRepository.getById(id)
    }

    fun getByServerId(id: Long): LiveData<User> {
        return mRepository.getByServerId(id)
    }

    fun getAll(): LiveData<List<User>> {
        return mRepository.getAllUsers()
    }

    fun login(username: String, password: String): LiveData<LoginData> {
            return mRepository.login(username, password).switchMap {
                if (it.id > 0)
                    mRepository.getByServerId(it.id).map { user ->
                        LoginData(
                            it.accessToken,
                            user.userClientId,
                            it.username,
                            it.email,
                            it.roles
                        )
                    }
                else
                    liveDataOf(LoginData(id = it.id))
            }
    }

    fun getAllUsername(): LiveData<List<String>> {
        return mRepository.getUsername()
    }

    fun getUserWithTeams(userId: Long): LiveData<UserWithTeams> {
        return mRepository.getUserWithTeams(userId)
    }

    fun getUserWithOwnedTeams(userId: Long): LiveData<UserWithOwnedTeams> {
        return mRepository.getUserWithOwnedTeams(userId)
    }
}