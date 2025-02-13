package com.kaiwolfram.nozzle.ui.app.views.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.kaiwolfram.nozzle.R
import com.kaiwolfram.nozzle.model.PostIds
import com.kaiwolfram.nozzle.model.PostWithMeta
import com.kaiwolfram.nozzle.ui.components.*
import com.kaiwolfram.nozzle.ui.theme.LightGray21
import com.kaiwolfram.nozzle.ui.theme.sizing
import com.kaiwolfram.nozzle.ui.theme.spacing


@Composable
fun ProfileScreen(
    uiState: ProfileViewModelState,
    onPrepareReply: (PostWithMeta) -> Unit,
    onLike: (String) -> Unit,
    onRepost: (String) -> Unit,
    onFollow: (String) -> Unit,
    onUnfollow: (String) -> Unit,
    onRefreshProfileView: () -> Unit,
    onCopyNpubAndShowToast: (String) -> Unit,
    onNavigateToThread: (PostIds) -> Unit,
    onNavigateToReply: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
) {
    Column {
        ProfileData(
            pubkey = uiState.pubkey,
            npub = uiState.npub,
            name = uiState.name,
            about = uiState.about,
            pictureUrl = uiState.picture,
            isOneself = uiState.isOneself,
            isFollowed = uiState.isFollowed,
            onFollow = onFollow,
            onUnfollow = onUnfollow,
            onCopyNpubAndShowToast = onCopyNpubAndShowToast,
            onNavToEditProfile = onNavigateToEditProfile,
        )
        Spacer(Modifier.height(spacing.medium))
        FollowerNumbers(
            numOfFollowing = uiState.numOfFollowing,
            numOfFollowers = uiState.numOfFollowers,
        )
        Spacer(Modifier.height(spacing.xl))
        Divider()
        PostCardList(
            posts = uiState.posts,
            isRefreshing = uiState.isRefreshing,
            onRefresh = onRefreshProfileView,
            onLike = onLike,
            onRepost = onRepost,
            onPrepareReply = onPrepareReply,
            onLoadMore = {},
            onNavigateToThread = onNavigateToThread,
            onNavigateToReply = onNavigateToReply
        )
    }
    if (uiState.posts.isEmpty()) {
        NoPostsHint()
    }
}

@Composable
private fun ProfileData(
    pubkey: String,
    npub: String,
    name: String,
    about: String,
    pictureUrl: String,
    isOneself: Boolean,
    isFollowed: Boolean,
    onFollow: (String) -> Unit,
    onUnfollow: (String) -> Unit,
    onCopyNpubAndShowToast: (String) -> Unit,
    onNavToEditProfile: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = spacing.screenEdge),
        verticalArrangement = Arrangement.Center
    ) {
        ProfilePictureAndActions(
            pictureUrl = pictureUrl,
            pubkey = pubkey,
            isOneself = isOneself,
            isFollowed = isFollowed,
            onFollow = onFollow,
            onUnfollow = onUnfollow,
            onNavToEditProfile = onNavToEditProfile,
        )
        NameAndNpub(
            name = name,
            npub = npub,
            onCopyNpubAndShowToast = onCopyNpubAndShowToast,
        )
        Spacer(Modifier.height(spacing.medium))
        if (about.isNotBlank()) {
            Text(
                text = about,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfilePictureAndActions(
    pictureUrl: String,
    pubkey: String,
    isOneself: Boolean,
    isFollowed: Boolean,
    onFollow: (String) -> Unit,
    onUnfollow: (String) -> Unit,
    onNavToEditProfile: () -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(spacing.screenEdge)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ProfilePicture(
            modifier = Modifier
                .size(sizing.largeProfilePicture)
                .aspectRatio(1f)
                .clip(CircleShape),
            pictureUrl = pictureUrl,
            pubkey = pubkey,
        )
        FollowOrEditButton(
            isOneself = isOneself,
            isFollowed = isFollowed,
            onFollow = { onFollow(pubkey) },
            onUnfollow = { onUnfollow(pubkey) },
            onNavToEditProfile = onNavToEditProfile,
        )
    }
}

@Composable
private fun FollowOrEditButton(
    isOneself: Boolean,
    isFollowed: Boolean,
    onFollow: () -> Unit,
    onUnfollow: () -> Unit,
    onNavToEditProfile: () -> Unit,
) {
    if (isOneself) {
        EditProfileButton(onNavToEditProfile = onNavToEditProfile)
    } else {
        FollowButton(
            isFollowed = isFollowed,
            onFollow = { onFollow() },
            onUnfollow = { onUnfollow() }
        )
    }
}

@Composable
private fun FollowerNumbers(
    numOfFollowing: Int,
    numOfFollowers: Int,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.xl),
    ) {
        Row {
            Text(
                text = numOfFollowing.toString(),
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(spacing.medium))
            Text(text = stringResource(id = R.string.following))
        }
        Spacer(Modifier.width(spacing.large))
        Row {
            Text(
                text = numOfFollowers.toString(),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(spacing.medium))
            Text(text = stringResource(id = R.string.followers))
        }
    }
}

@Composable
private fun NameAndNpub(
    name: String,
    npub: String,
    onCopyNpubAndShowToast: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(Modifier.padding(end = spacing.medium)) {
            Text(
                text = name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.h6,
            )
            CopyableNpub(
                npub = npub,
                onCopyNpubAndShowToast = onCopyNpubAndShowToast
            )
        }
    }
}

@Composable
private fun CopyableNpub(
    npub: String,
    onCopyNpubAndShowToast: (String) -> Unit,
) {
    val toast = stringResource(id = R.string.pubkey_copied)
    Row(
        Modifier.clickable { onCopyNpubAndShowToast(toast) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        CopyIcon(
            modifier = Modifier.size(sizing.smallIcon),
            description = stringResource(id = R.string.copy_pubkey),
            tint = LightGray21
        )
        Text(
            text = npub,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = LightGray21,
            style = MaterialTheme.typography.body2,
        )
    }
}
