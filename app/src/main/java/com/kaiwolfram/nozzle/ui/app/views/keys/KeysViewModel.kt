package com.kaiwolfram.nozzle.ui.app.views.keys

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.preferences.PersonalProfileStorage
import com.kaiwolfram.nozzle.data.preferences.ProfilePreferences
import com.kaiwolfram.nozzle.data.utils.isHex
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

private const val TAG = "KeysViewModel"

data class KeysViewModelState(
    val pubkey: String = "",
    val privkeyInput: String = "",
    val hasChanges: Boolean = false,
    val isInvalid: Boolean = false,
)

class KeysViewModel(
    private val profileStorage: PersonalProfileStorage,
    context: Context,
    clip: ClipboardManager,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(KeysViewModelState())
    private var privkey: String = ""

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize KeysViewModel")
        useCachedValues()
    }

    val onCopyPubkeyAndShowToast: (String) -> Unit = { toast ->
        val pubkey = uiState.value.pubkey
        Log.i(TAG, "Copy pubkey $pubkey and show toast '$toast'")
        clip.setText(AnnotatedString(pubkey))
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }

    val onCopyPrivkeyAndShowToast: (String) -> Unit = { toast ->
        val privkeyInput = uiState.value.privkeyInput
        Log.i(TAG, "Copy privkey $privkeyInput and show toast '$toast'")
        clip.setText(AnnotatedString(privkeyInput))
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }

    val onUpdateKeyPairAndShowToast: (String) -> Unit =
        { toast ->
            val privkeyInput = uiState.value.privkeyInput
            val isValid = privkeyInput.length == 64 && privkeyInput.isHex()
            if (isValid) {
                Log.i(TAG, "Saving new privkey $privkeyInput")
                profileStorage.setPrivkey(privkeyInput)
                profileStorage.resetMetaData()
                useCachedValues()
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "New privkey $privkeyInput is invalid")
                viewModelState.update {
                    it.copy(isInvalid = true)
                }
            }
        }

    val onChangePrivkey: (String) -> Unit = { newValue ->
        if (privkey == newValue) {
            viewModelState.update {
                it.copy(
                    privkeyInput = newValue,
                    isInvalid = false,
                    hasChanges = false,
                )
            }
        } else if (uiState.value.privkeyInput != newValue) {
            viewModelState.update {
                it.copy(
                    privkeyInput = newValue,
                    isInvalid = false,
                    hasChanges = true,
                )
            }
        }
    }

    val onResetUiState: () -> Unit = {
        useCachedValues()
    }

    private fun useCachedValues() {
        viewModelState.update {
            privkey = profileStorage.getPrivkey()
            it.copy(
                privkeyInput = privkey,
                pubkey = profileStorage.getPubkey(),
                hasChanges = false,
                isInvalid = false,
            )
        }
    }

    companion object {
        fun provideFactory(
            profileStorage: PersonalProfileStorage,
            context: Context,
            clip: ClipboardManager,
        ): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return KeysViewModel(
                        profileStorage = profileStorage,
                        context = context,
                        clip = clip
                    ) as T
                }
            }
    }
}
