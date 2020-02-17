package com.geeksville.mesh.ui

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.animation.Crossfade
import androidx.ui.core.Clip
import androidx.ui.core.Text
import androidx.ui.core.TextField
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.input.ImeAction
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.surface.Surface
import androidx.ui.text.TextStyle
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.geeksville.android.Logging
import com.geeksville.mesh.R


object UILog : Logging

/// Given a human name, strip out the first letter of the first three words and return that as the initials for
/// that user.
fun getInitials(name: String): String {
    val words = name.split(Regex("\\s+")).filter { it.isNotEmpty() }.take(3).map { it.first() }
        .joinToString("")

    return words
}

@Composable
fun HomeContent() {
    Column {
        Row {
            Container(LayoutSize(40.dp, 40.dp)) {
                VectorImage(id = if (UIState.isConnected.value) R.drawable.cloud_on else R.drawable.cloud_off)
            }

            Text(if (UIState.isConnected.value) "Connected" else "Not Connected")
        }

        NodeDB.nodes.value.values.forEach {
            NodeInfoCard(it)
        }

        val state = state { "fixme bob" }
        Surface(color = Color.LightGray) {
            Row {
                Clip(shape = RoundedCornerShape(15.dp)) {
                    Padding(padding = 15.dp) {
                        TextField(
                            value = state.value,
                            onValueChange = { state.value = it },
                            textStyle = TextStyle(
                                color = Color.DarkGray
                            ),
                            imeAction = ImeAction.Done,
                            onImeActionPerformed = {
                                UILog.info("did IME action")
                            }
                        )
                    }
                }

                Text(text = getInitials(state.value))
            }
        }


        /* FIXME - doens't work yet - probably because I'm not using release keys
        // If account is null, then show the signin button, otherwise
        val context = ambient(ContextAmbient)
        val account = GoogleSignIn.getLastSignedInAccount(context)
        if (account != null)
            Text("We have an account")
        else {
            Text("No account yet")
            if (context is Activity) {
                Button("Google sign-in", onClick = {
                    val signInIntent: Intent = UIState.googleSignInClient.signInIntent
                    context.startActivityForResult(signInIntent, MainActivity.RC_SIGN_IN)
                })
            }
        } */

        /*
        Button(text = "Start scan",
            onClick = {
                if (bluetoothAdapter != null) {
                    // Note: We don't want this service to die just because our activity goes away (because it is doing a software update)
                    // So we use the application context instead of the activity
                    SoftwareUpdateService.enqueueWork(
                        applicationContext,
                        SoftwareUpdateService.startUpdateIntent
                    )
                }
            })

        Button(text = "send packets",
            onClick = { sendTestPackets() }) */
    }
}

val palette = lightColorPalette() // darkColorPalette()

@Composable
fun MeshApp() {
    val (drawerState, onDrawerStateChange) = state { DrawerState.Closed }

    MaterialTheme(colors = palette) {
        ModalDrawerLayout(
            drawerState = drawerState,
            onStateChange = onDrawerStateChange,
            gesturesEnabled = drawerState == DrawerState.Opened,
            drawerContent = {

                AppDrawer(
                    currentScreen = AppStatus.currentScreen,
                    closeDrawer = { onDrawerStateChange(DrawerState.Closed) }
                )

            }, bodyContent = { AppContent { onDrawerStateChange(DrawerState.Opened) } })
    }
}

@Preview
@Composable
fun previewView() {
    // It seems modaldrawerlayout not yet supported in preview
    MaterialTheme(colors = palette) {
        HomeContent()
    }
}


@Composable
private fun AppContent(openDrawer: () -> Unit) {
    Crossfade(AppStatus.currentScreen) { screen ->
        Surface(color = (MaterialTheme.colors()).background) {

            Column {
                TopAppBar(
                    title = { Text(text = "Meshtastic") },
                    navigationIcon = {
                        VectorImageButton(R.drawable.ic_launcher_new_foreground) {
                            openDrawer()
                        }
                    }
                )

                VerticalScroller(modifier = LayoutFlexible(1f)) {
                    when (screen) {
                        Screen.messages -> MessagesContent()
                        Screen.settings -> BTScanScreen()
                        Screen.users -> HomeContent()
                        Screen.channel -> HomeContent()
                        else -> TODO()
                    }
                }
            }
        }
    }
}