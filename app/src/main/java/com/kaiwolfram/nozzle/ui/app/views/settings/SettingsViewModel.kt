package com.kaiwolfram.nozzle.ui.app.views.settings

import android.content.Context
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.isValidUsername
import com.kaiwolfram.nozzle.data.preferences.IPersonalProfileStorage
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "SettingsViewModel"

data class SettingsViewModelState(
    val usernameInput: String = "",
    val bioInput: String = "",
    val pictureUrlInput: String = "",
    val hasChanges: Boolean = false,
    val isInvalidUsername: Boolean = false,
    val isInvalidPictureUrl: Boolean = false,
)

class SettingsViewModel(
    private val personalProfileStorage: IPersonalProfileStorage,
    private val profileDao: ProfileDao,
    context: Context,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(SettingsViewModelState())
    private var username: String = ""
    private var bio: String = ""
    private var pictureUrl: String = ""

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize SettingsViewModel")
        useCachedValues()
    }

    val onUpdateProfileAndShowToast: (String) -> Unit =
        { toast ->
            uiState.value.let {
                val isValidUsername = isValidUsername(it.usernameInput)
                val isValidUrl = it.pictureUrlInput.isEmpty()
                        || URLUtil.isValidUrl(it.pictureUrlInput)
                if (isValidUsername && isValidUrl) {
                    Log.i(TAG, "Updating profile")
                    viewModelScope.launch(context = Dispatchers.IO) {
                        profileDao.updateMetaData(
                            pubkey = personalProfileStorage.getPubkey(),
                            name = it.usernameInput,
                            bio = it.bioInput,
                            pictureUrl = it.pictureUrlInput
                        )
                    }
                    personalProfileStorage.setName(it.usernameInput)
                    personalProfileStorage.setBio(it.bioInput)
                    personalProfileStorage.setPictureUrl(it.pictureUrlInput)
                    useCachedValues()
                    Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
                } else {
                    Log.i(TAG, "New values are invalid")
                    viewModelState.update { state ->
                        state.copy(
                            isInvalidUsername = !isValidUsername,
                            isInvalidPictureUrl = !isValidUrl
                        )
                    }
                }
            }
        }

    val onChangeName: (String) -> Unit = { input ->
        if (input != uiState.value.usernameInput) {
            viewModelState.update {
                it.copy(usernameInput = input)
            }
            setHasChanges()
        }
    }

    val onChangeBio: (String) -> Unit = { input ->
        if (input != uiState.value.bioInput) {
            viewModelState.update {
                it.copy(bioInput = input)
            }
            setHasChanges()
        }
    }

    val onChangePictureUrl: (String) -> Unit = { input ->
        if (input != uiState.value.pictureUrlInput) {
            viewModelState.update {
                it.copy(pictureUrlInput = input)
            }
            setHasChanges()
        }
    }

    val onResetUiState: () -> Unit = {
        useCachedValues()
    }

    private fun setHasChanges() {
        uiState.value.let {
            val hasChanges = it.usernameInput != username
                    || it.bioInput != bio
                    || it.pictureUrlInput != pictureUrl
            if (hasChanges != it.hasChanges) {
                viewModelState.update { state ->
                    state.copy(hasChanges = hasChanges)
                }
            }
        }
    }

    private fun useCachedValues() {
        username = personalProfileStorage.getName()
        bio = personalProfileStorage.getBio()
        pictureUrl = personalProfileStorage.getPictureUrl()
        viewModelState.update {
            it.copy(
                usernameInput = username,
                bioInput = bio,
                pictureUrlInput = pictureUrl,
                hasChanges = false,
                isInvalidUsername = false,
                isInvalidPictureUrl = false
            )
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        fun provideFactory(
            personalProfileStorage: IPersonalProfileStorage,
            profileDao: ProfileDao,
            context: Context,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(
                    personalProfileStorage = personalProfileStorage,
                    profileDao = profileDao,
                    context = context
                ) as T
            }
        }
    }
}
