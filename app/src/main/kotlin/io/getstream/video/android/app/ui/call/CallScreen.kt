package io.getstream.video.android.app.ui.call

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import io.getstream.video.android.compose.ui.components.call.CallContent
import io.getstream.video.android.compose.ui.components.incomingcall.IncomingCallScreen
import io.getstream.video.android.compose.ui.components.outgoingcall.OutgoingCallScreen
import io.getstream.video.android.viewmodel.CallViewModel
import io.getstream.video.android.model.state.StreamCallState as State

@Composable
internal fun CallScreen(
    viewModel: CallViewModel,
    onRejectCall: () -> Unit,
    onAcceptCall: () -> Unit,
    onCancelCall: () -> Unit = {},
    onMicToggleChanged: (Boolean) -> Unit = {},
    onVideoToggleChanged: (Boolean) -> Unit,
) {
    val stateHolder = viewModel.streamCallState.collectAsState(initial = State.Idle)
    val state = stateHolder.value
    if (state is State.Incoming && !state.acceptedByMe) {
        IncomingCallScreen(
            viewModel = viewModel,
            onDeclineCall = onRejectCall,
            onAcceptCall = onAcceptCall,
            onVideoToggleChanged = onVideoToggleChanged
        )
    } else if (state is State.Outgoing && !state.acceptedByCallee) {
        OutgoingCallScreen(
            viewModel = viewModel,
            onCancelCall = { onCancelCall() },
            onMicToggleChanged = onMicToggleChanged,
            onVideoToggleChanged = onVideoToggleChanged
        )
    } else {
        CallContent(
            callViewModel = viewModel,
            onLeaveCall = onCancelCall
        )
    }
}