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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
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
@Composable
fun MyApp() {

    Surface(color = MaterialTheme.colors.background) {
        Timer()
    }
}

@Composable
fun Timer() {
    val totalTimerTime = 180
    var countdownSeconds by remember { mutableStateOf(totalTimerTime) }
    var timerState by remember { mutableStateOf(0) } // 0 not started, 1 running, 2 is ended

    when (timerState) {
        0 -> {
            ConstraintLayout(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                val (progress, progressTxt, btnStart) = createRefs()

                // not yet start show start button

                Progress(
                    Modifier.constrainAs(progress) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    countdownSeconds = countdownSeconds, totalSeconds = totalTimerTime
                )

                Button(
                    modifier = Modifier.constrainAs(btnStart) {
                        top.linkTo(progress.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    onClick = {
                        countdownSeconds = totalTimerTime
                        timerState = 1
                    }
                ) {
                    Text(text = "Start")
                }
            }
        }
        1 -> {
            // running show stop button

            val minutes = countdownSeconds / 60
            val seconds = countdownSeconds % 60
            Column(modifier = Modifier.fillMaxWidth()) {
                Progress(countdownSeconds = countdownSeconds, totalSeconds = totalTimerTime)
                TimerUi(minutes, seconds)
            }

            RunTimer(
                time = countdownSeconds,
                onTimerChange = {
                    countdownSeconds = it
                    if (it == 0) {
                        // ended
                        timerState = 2
                    }
                }
            )
        }
        2 -> {
            // timer stopped show restart button
            Row(Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        timerState = 0
                    }
                ) {
                    Text(text = "Reset")
                }
            }
        }
    }
}

@Composable
fun Progress(modifier: Modifier = Modifier, countdownSeconds: Int, totalSeconds: Int) {
    val progress = countdownSeconds.toFloat() / totalSeconds

    CircularProgressIndicator(progress, modifier = modifier)
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
fun TimerUi(minutes: Int, seconds: Int) {
    Row(Modifier.fillMaxWidth()) {
        Text(text = "$minutes")
        Text(text = ":")
        Text(text = "$seconds")
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
