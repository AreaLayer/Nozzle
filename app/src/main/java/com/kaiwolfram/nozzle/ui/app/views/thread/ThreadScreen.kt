package com.kaiwolfram.nozzle.ui.app.views.thread

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.model.ThreadPosition
import com.kaiwolfram.nozzle.ui.components.PostCard
import com.kaiwolfram.nozzle.ui.components.PostNotFound
import com.kaiwolfram.nozzle.ui.components.TopBar
import com.kaiwolfram.nozzle.ui.theme.LightYellow
import com.kaiwolfram.nozzle.ui.theme.spacing


@Composable
fun ThreadScreen(
    uiState: ThreadViewModelState,
    onPrepareReply: (PostWithMeta) -> Unit,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onRefreshThreadView: () -> Unit,
    onOpenThread: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onGoBack: () -> Unit,
    onNavigateToReply: () -> Unit,
) {
    Column {
        TopBar(text = stringResource(id = R.string.thread), onGoBack = onGoBack)
        Column {
            uiState.current?.let {
                ThreadedPosts(
                    previous = uiState.previous,
                    current = it,
                    replies = uiState.replies,
                    currentThreadPosition = uiState.currentThreadPosition,
                    isRefreshing = uiState.isRefreshing,
                    onPrepareReply = onPrepareReply,
                    onRefresh = onRefreshThreadView,
                    onLike = onLike,
                    onRepost = onRepost,
                    onOpenThread = onOpenThread,
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToReply = onNavigateToReply,
                )
            }
        }
    }
}

@Composable
private fun ThreadedPosts(
    previous: List<PostWithMeta>,
    current: PostWithMeta,
    replies: List<PostWithMeta>,
    currentThreadPosition: ThreadPosition,
    isRefreshing: Boolean,
    onPrepareReply: (PostWithMeta) -> Unit,
    onRefresh: () -> Unit,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onOpenThread: (String) -> Unit,
    onNavigateToReply: () -> Unit,
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
    ) {
        // TODO: Do not reset after swipe refresh, like, or repost
        val listState = LazyListState(firstVisibleItemIndex = previous.size)
        LazyColumn(modifier = Modifier.fillMaxSize(), state = listState) {
            itemsIndexed(previous) { index, post ->
                var threadPosition = ThreadPosition.MIDDLE
                if (index == 0) {
                    if (post.replyToId != null) {
                        PostNotFound()
                    } else {
                        threadPosition = ThreadPosition.START
                    }
                }
                PostCard(
                    post = post,
                    onLike = onLike,
                    onRepost = onRepost,
                    onPrepareReply = onPrepareReply,
                    threadPosition = threadPosition,
                    onOpenProfile = onNavigateToProfile,
                    onNavigateToThread = onOpenThread,
                    onNavigateToReply = onNavigateToReply
                )
            }
            item {
                PostCard(
                    post = current,
                    onLike = onLike,
                    onRepost = onRepost,
                    onPrepareReply = onPrepareReply,
                    modifier = Modifier.background(color = LightYellow),
                    threadPosition = currentThreadPosition,
                    onOpenProfile = onNavigateToProfile,
                    onNavigateToReply = onNavigateToReply
                )
                Divider()
                Spacer(modifier = Modifier.height(spacing.tiny))
                Divider()
            }
            items(replies) { post ->
                PostCard(
                    post = post,
                    onLike = onLike,
                    onRepost = onRepost,
                    onPrepareReply = onPrepareReply,
                    onOpenProfile = onNavigateToProfile,
                    onNavigateToThread = onOpenThread,
                    onNavigateToReply = onNavigateToReply
                )
            }
        }
    }
}
