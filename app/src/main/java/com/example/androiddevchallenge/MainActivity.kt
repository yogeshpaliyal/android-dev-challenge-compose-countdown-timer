/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class TimerState {
    INACTIVE, RUNNING, PAUSED
}

class MainActivity : AppCompatActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

// Start building your app here!
@ExperimentalAnimationApi
@Composable
fun MyApp() {

    Surface(color = MaterialTheme.colors.background) {
        Timer()
    }
}

@ExperimentalAnimationApi
@Composable
fun Timer() {
    val totalTimerTime = 60
    var countdownSeconds by remember { mutableStateOf(totalTimerTime) }
    var timerState by remember { mutableStateOf(TimerState.INACTIVE) } // 0 not started, 1 running, 2 is ended

    ConstraintLayout(
        modifier =
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()

    ) {
        val (progress, progressTxt, btnsRow) = createRefs()

        Progress(
            Modifier
                .constrainAs(progress) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(btnsRow.top)
                }
                .height(300.dp)
                .width(300.dp),
            countdownSeconds = countdownSeconds, totalSeconds = totalTimerTime
        )
        TimerUi(
            Modifier.constrainAs(progressTxt) {
                top.linkTo(progress.top)
                bottom.linkTo(progress.bottom)
                start.linkTo(progress.start)
                end.linkTo(progress.end)
            },
            countdownSeconds
        )

        Row(
            Modifier
                .constrainAs(btnsRow) {
                    top.linkTo(progress.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(
                modifier = Modifier
                    .clip(CircleShape),
                onClick = {
                    when (timerState) {
                        TimerState.RUNNING -> {
                            timerState = TimerState.PAUSED
                        }
                        TimerState.PAUSED -> {
                            timerState = TimerState.RUNNING
                        }
                        TimerState.INACTIVE -> {
                            countdownSeconds = totalTimerTime
                            timerState = TimerState.RUNNING
                        }
                    }
                }
            ) {
                Crossfade(targetState = timerState) { timerState ->
                    when (timerState) {
                        TimerState.RUNNING -> {
                            Icon(Icons.Filled.Pause, "Pause")
                        }
                        TimerState.PAUSED -> {
                            Icon(Icons.Filled.PlayArrow, "Resume")
                        }
                        TimerState.INACTIVE -> {
                            Icon(Icons.Filled.PlayArrow, "Play")
                        }
                    }
                }
            }

            AnimatedVisibility(visible = timerState != TimerState.INACTIVE) {

                Button(
                    modifier = Modifier
                        .padding(start = 50.dp)
                        .clip(CircleShape),
                    onClick = {

                        countdownSeconds = totalTimerTime
                        timerState = TimerState.INACTIVE
                    }
                ) {
                    Icon(Icons.Filled.RestartAlt, "Restart")
                }
            }
        }

        if (timerState == TimerState.RUNNING)
            RunTimer(
                time = countdownSeconds,
                onTimerChange = {
                    countdownSeconds = it
                    if (it == 0) {
                        // ended
                        timerState = TimerState.INACTIVE
                    }
                }
            )
    }
}

@Composable
fun Progress(modifier: Modifier = Modifier, countdownSeconds: Int, totalSeconds: Int) {
    val progress = countdownSeconds.toFloat() / totalSeconds
    val animatedProgress = animateFloatAsState(
        targetValue = progress,
        animationSpec = SpringSpec(Spring.DampingRatioNoBouncy, 5f, visibilityThreshold = 1 / 1000f)
    ).value

    CircularProgressIndicator(animatedProgress, modifier = modifier)
}

@Composable
fun RunTimer(time: Int, onTimerChange: (Int) -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    coroutineScope.launch {
        Log.d("TestingLoop", "$time")
        delay(1000)
        var newTime = time
        newTime--
        onTimerChange(newTime)
    }
}

@Composable
fun TimerUi(modifier: Modifier, countdownSeconds: Int) {
    val minutes = countdownSeconds / 60
    val seconds = countdownSeconds % 60
    Row(modifier) {
        Text(text = "$minutes")
        Text(text = ":")
        Text(text = "$seconds")
    }
}

@ExperimentalAnimationApi
@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@ExperimentalAnimationApi
@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
