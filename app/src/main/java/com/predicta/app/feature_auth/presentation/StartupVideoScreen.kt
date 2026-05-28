package com.predicta.app.feature_auth.presentation

import android.content.Context
import android.content.ContextWrapper
import android.media.MediaPlayer
import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.predicta.app.R

@Composable
fun StartupVideoScreen(
    onVideoFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val view = LocalView.current

    DisposableEffect(view) {
        val window = view.context.findActivity()?.window
        val controller = window?.let { WindowInsetsControllerCompat(it, view) }

        if (window != null && controller != null) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            if (window != null && controller != null) {
                controller.show(WindowInsetsCompat.Type.systemBars())
                WindowCompat.setDecorFitsSystemWindows(window, true)
            }
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        factory = { context ->
            FrameLayout(context).apply {
                setBackgroundColor(android.graphics.Color.BLACK)

                val videoView = AspectFillVideoView(context).apply {
                    setVideoURI(
                        Uri.parse("android.resource://${context.packageName}/${R.raw.starting_video}"),
                    )
                    setOnPreparedListener { player ->
                        player.isLooping = false
                        player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
                        setVideoSize(player.videoWidth, player.videoHeight)
                        start()
                    }
                    setOnCompletionListener { onVideoFinished() }
                    setOnErrorListener { _, _, _ ->
                        onVideoFinished()
                        true
                    }
                }

                addView(
                    videoView,
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER,
                    ),
                )
            }
        },
    )
}

private class AspectFillVideoView(context: Context) : VideoView(context) {
    private var videoWidth: Int = 0
    private var videoHeight: Int = 0

    fun setVideoSize(width: Int, height: Int) {
        videoWidth = width
        videoHeight = height
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val availableWidth = MeasureSpec.getSize(widthMeasureSpec)
        val availableHeight = MeasureSpec.getSize(heightMeasureSpec)

        if (videoWidth <= 0 || videoHeight <= 0 || availableWidth <= 0 || availableHeight <= 0) {
            setMeasuredDimension(availableWidth, availableHeight)
            return
        }

        var measuredHeight = availableHeight
        var measuredWidth = measuredHeight * videoWidth / videoHeight

        if (measuredWidth < availableWidth) {
            measuredWidth = availableWidth
            measuredHeight = measuredWidth * videoHeight / videoWidth
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }
}

private tailrec fun Context.findActivity(): ComponentActivity? {
    return when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
