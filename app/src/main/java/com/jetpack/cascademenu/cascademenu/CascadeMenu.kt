package com.jetpack.cascademenu.cascademenu

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowLeft
import androidx.compose.material.icons.rounded.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

val MAX_WIDTH = 192.dp

@ExperimentalAnimationApi
fun <T> AnimatedContentScope<T>.animateToPrevious(): ContentTransform {
    return slideIntoContainer(AnimatedContentScope.SlideDirection.Right) with
            slideOutOfContainer(AnimatedContentScope.SlideDirection.Right)
}

@ExperimentalAnimationApi
fun <T> AnimatedContentScope<T>.animateToNext(): ContentTransform {
    return slideIntoContainer(AnimatedContentScope.SlideDirection.Left) with
            slideOutOfContainer(AnimatedContentScope.SlideDirection.Left)
}

fun <T : Any> isNavigatingBack(
    currentMenu: CascadeMenuItem<T>,
    nextMenu: CascadeMenuItem<T>
): Boolean {
    return currentMenu.hasParent() && nextMenu == currentMenu.parent!!
}

@ExperimentalAnimationApi
@Composable
fun <T : Any> CascadeMenu(
    modifier: Modifier = Modifier,
    isOpen: Boolean,
    menu: CascadeMenuItem<T>,
    colors: CascadeMenuColors = cascadeMenuColors(),
    offset: DpOffset = DpOffset.Zero,
    onItemSelected: (T) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = isOpen,
        onDismissRequest = onDismiss,
        offset = offset,
        modifier = modifier
            .width(MAX_WIDTH)
            .background(colors.backgroundColor)
    ) {
        val state by remember { mutableStateOf(CascadeMenuState(menu)) }
        AnimatedContent(
            targetState = state.currentMenuItem,
            transitionSpec = {
                if (isNavigatingBack(initialState, targetState)) {
                    animateToPrevious()
                } else {
                    animateToNext()
                }
            }
        ) { targetState ->
            CascadeMenuContent(
                state = state,
                targetMenu = targetState,
                onItemSelected = onItemSelected,
                colors = colors
            )
        }
    }
}

@Composable
fun <T: Any>CascadeMenuContent(
    state: CascadeMenuState<T>,
    targetMenu: CascadeMenuItem<T>,
    onItemSelected: (T) -> Unit,
    colors: CascadeMenuColors
) {
    Column(
        modifier = Modifier.width(MAX_WIDTH)
    ) {
        if (targetMenu.hasParent()) {
            CascadeHeaderItem(
                title = targetMenu.title,
                contentColor = colors.contentColor
            ) {
                state.currentMenuItem = targetMenu.parent!!
            }
        }
        if (targetMenu.hasChildren()) {
            for (item in targetMenu.children!!) {
                if (item.hasChildren()) {
                    CascadeParentItem(
                        item.id,
                        item.title,
                        item.icon,
                        colors.contentColor
                    ) { id ->
                        val child = targetMenu.getChild(id)
                        if (child != null) {
                            state.currentMenuItem = child
                        } else {
                            throw IllegalStateException("Invalid item id : $id")
                        }
                    }
                } else {
                    CascadeChildItem(
                        id = item.id,
                        title = item.title,
                        icon = item.icon,
                        contentColor = colors.contentColor,
                        onClick = onItemSelected
                    )
                }
            }
        }
    }
}

@Composable
fun <T> CascadeChildItem(
    id: T,
    title: String,
    icon: ImageVector?,
    contentColor: Color,
    onClick: (T) -> Unit
) {
    CascadeMenuItem(onClick = { onClick(id) }) {
        if (icon != null) {
            CascadeMenuItemIcon(icon = icon, tint = contentColor)
            Space()
        }

        CascadeMenuItemText(
            modifier = Modifier.weight(1f),
            text = title,
            color = contentColor
        )
    }
}

@Composable
fun CascadeMenuItemText(
    modifier: Modifier,
    text: String,
    color: Color,
    isHeaderText: Boolean = false
) {
    val style = if (isHeaderText) {
        MaterialTheme.typography.subtitle2
    } else {
        MaterialTheme.typography.subtitle1
    }

    Text(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun Space() {
    Spacer(modifier = Modifier.width(12.dp))
}

@Composable
fun CascadeMenuItemIcon(
    icon: ImageVector,
    tint: Color
) {
    Icon(
      imageVector = icon,
      modifier = Modifier.size(24.dp),
      contentDescription = "Icon",
      tint = tint
    )
}

@Composable
fun CascadeMenuItem(
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    DropdownMenuItem(
        onClick = onClick,
        content = content,
        interactionSource = remember { MutableInteractionSource() }
    )
}

@Composable
fun <T> CascadeParentItem(
    id: T,
    title: String,
    icon: ImageVector?,
    contentColor: Color,
    onClick: (T) -> Unit
) {
    CascadeMenuItem(onClick = { onClick(id) }) {
        if (icon != null) {
            CascadeMenuItemIcon(icon = icon, tint = contentColor)
            Space()
        }
        CascadeMenuItemText(
            modifier = Modifier.weight(1f),
            text = title,
            color = contentColor
        )
        Space()
        CascadeMenuItemIcon(
            icon = Icons.Rounded.ArrowRight,
            tint = contentColor
        )
    }
}

@Composable
fun CascadeHeaderItem(
    title: String,
    contentColor: Color,
    onClick: () -> Unit
) {
    CascadeMenuItem(onClick = { onClick() }) {
        CascadeMenuItemIcon(
            icon = Icons.Rounded.ArrowLeft,
            tint = contentColor.copy(alpha = ContentAlpha.medium)
        )
        Spacer(modifier = Modifier.width(4.dp))
        CascadeMenuItemText(
            modifier = Modifier.weight(1f),
            text = title,
            color = contentColor.copy(ContentAlpha.medium),
            isHeaderText = true
        )
    }
}

























