package com.kaiwolfram.nozzle.ui.app.views.post

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kaiwolfram.nozzle.data.nostr.INostrService
import com.kaiwolfram.nozzle.data.preferences.profile.IProfileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "PostViewModel"

data class PostViewModelState(
    val content: String = "",
    val isSendable: Boolean = false,
    val pictureUrl: String = "",
    val pubkey: String = "",
)

class PostViewModel(
    private val nostrService: INostrService,
    private val profileProvider: IProfileProvider,
    context: Context,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(PostViewModelState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        Log.i(TAG, "Initialize ReplyViewModel")
    }

    val onPreparePost: () -> Unit = {
        viewModelScope.launch(context = Dispatchers.IO) {
            Log.i(TAG, "Preparing new post")
            viewModelState.update {
                it.copy(
                    pictureUrl = profileProvider.getPictureUrl(),
                    pubkey = profileProvider.getPubkey(),
                    content = "",
                    isSendable = false,
                )
            }
        }
    }

    val onChangeContent: (String) -> Unit = { input ->
        if (input != uiState.value.content) {
            viewModelState.update {
                it.copy(content = input, isSendable = input.isNotBlank())
            }
        }
    }

    val onSendOrShowErrorToast: (String) -> Unit = { errorToast ->
        uiState.value.let { state ->
            if (!state.isSendable) {
                Toast.makeText(context, errorToast, Toast.LENGTH_SHORT).show()
            } else {
                Log.i(TAG, "Sending post")
                nostrService.send(content = state.content)
                reset()
            }
        }
    }

    private fun reset() {
        viewModelState.update {
            it.copy(
                content = "",
                isSendable = false,
            )
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
        super.onCleared()
    }

    companion object {
        fun provideFactory(
            nostrService: INostrService,
            profileProvider: IProfileProvider,
            context: Context
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PostViewModel(
                    nostrService = nostrService,
                    profileProvider = profileProvider,
                    context = context,
                ) as T
            }
        }
    }
}