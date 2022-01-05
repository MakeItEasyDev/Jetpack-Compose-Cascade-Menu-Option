package com.jetpack.cascademenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.twotone.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.jetpack.cascademenu.cascademenu.CascadeMenu
import com.jetpack.cascademenu.cascademenu.CascadeMenuItem
import com.jetpack.cascademenu.cascademenu.cascadeMenu
import com.jetpack.cascademenu.ui.theme.CascadeMenuTheme
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow

class MainActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CascadeMenuTheme {
                Surface(color = MaterialTheme.colors.background) {
                    CascadeMenuDropDown()
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun CascadeMenuDropDown() {
    val snackbarHostState = remember { SnackbarHostState() }
    val channel = remember { Channel<String>(Channel.CONFLATED) }
    val (isOpen, setIsOpen) = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = channel) {
        channel.receiveAsFlow().collect {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = Color.Transparent,
        scaffoldState = rememberScaffoldState(snackbarHostState = snackbarHostState),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cascade Menu",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                actions = {
                    IconButton(onClick = { setIsOpen(true) }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = null
                        )
                    }

                    Column(
                        modifier = Modifier
                            .wrapContentSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.End
                    ) {
                        Spacer(modifier = Modifier.height(48.dp))
                        Box(
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Menu(
                                setIsOpen = setIsOpen,
                                itemSelected = {
                                    channel.trySend(it)
                                    setIsOpen(false)
                                },
                                isOpen = isOpen
                            )
                        }
                    }
                }
            )
        },
        content = {}
    )
}

fun getMenu(): CascadeMenuItem<String> {
    return cascadeMenu {
        item("about", "About") {
            icon(Icons.TwoTone.Language)
        }
        item("copy", "Copy") {
            icon(Icons.TwoTone.FileCopy)
        }
        item("share", "Share") {
            icon(Icons.TwoTone.Share)
            item("to_clipboard", "To Clipboard") {
                item("pdf", "PDF")
                item("epub", "EPUB")
                item("web page", "Web Page")
                item("microsoft word", "Microsoft Word")
            }
            item("as a file", "As a file") {
                item("pdf", "PDF")
                item("epub", "EPUB")
                item("web page", "Web Page")
                item("microsoft word", "Microsoft Word")
            }
        }
        item("remove", "Remove") {
            icon(Icons.TwoTone.DeleteSweep)
            item("Yep", "Yep") {
                icon(Icons.TwoTone.Done)
            }
            item("Go Back", "Go Back") {
                icon(Icons.TwoTone.Close)
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun Menu(
    isOpen: Boolean = false,
    setIsOpen: (Boolean) -> Unit,
    itemSelected: (String) -> Unit
) {
    val menu = getMenu()
    CascadeMenu(
        isOpen = isOpen,
        menu = menu,
        onItemSelected = itemSelected,
        onDismiss = { setIsOpen(false) },
        offset = DpOffset(8.dp, 0.dp)
    )
}



















