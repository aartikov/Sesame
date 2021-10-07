package me.aartikov.sesamecomposesample.profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import me.aartikov.sesame.loading.simple.Loading
import me.aartikov.sesamecomposesample.R
import me.aartikov.sesamecomposesample.theme.AppTheme
import me.aartikov.sesamecomposesample.profile.domain.Profile

@Composable
fun ProfileUi(
    component: ProfileComponent,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        when (val currentState = component.profileState) {
            is Loading.State.Data -> {
                ProfileContent(
                    profile = currentState.data,
                    isRefreshing = currentState.refreshing,
                    onRefresh = { component.onPullToRefresh() }
                )
            }

            is Loading.State.Error -> {
                val message = currentState.throwable.message
                ProfilePlaceholder(
                    component = component,
                    errorMessage = message ?: stringResource(R.string.common_some_error_description)
                )
            }

            is Loading.State.Loading -> {
                ProfileProgress()
            }

            is Loading.State.Empty -> {
                ProfileProgress()
            }
        }
    }
}

@Composable
fun ProfileContent(
    profile: Profile,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
        onRefresh = onRefresh,
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(48.dp)
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = profile.name,
                style = MaterialTheme.typography.h5,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp, end = 18.dp, top = 32.dp)
            )
            Image(
                painter = rememberImagePainter(profile.avatarUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colors.surface)
            )
        }
    }
}

@Composable
fun ProfileProgress() {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun ProfilePlaceholder(
    component: ProfileComponent,
    errorMessage: String
) {
    Box {
        Column(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .align(Alignment.Center)
                .fillMaxWidth()
        ) {
            Text(
                text = errorMessage,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.body2
            )
            TextButton(
                onClick = component::onRetryClicked
            ) {
                Text(
                    text = stringResource(R.string.common_retry).uppercase()
                )
            }
        }
    }
}

@Preview
@Composable
fun ProfileUiPreview() {
    AppTheme {
        ProfileUi(FakeProfileComponent())
    }
}


class FakeProfileComponent : ProfileComponent {

    override val profileState: Loading.State<Profile> = Loading.State.Loading

    override fun onPullToRefresh() {}

    override fun onRetryClicked() {}
}
