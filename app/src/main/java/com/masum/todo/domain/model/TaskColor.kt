package com.masum.todo.domain.model

import androidx.compose.ui.graphics.Color

enum class TaskColor(val color: Color, val displayName: String) {
    DEFAULT(Color(0xFFFFFBFE), "Default"),
    TRANSPARENT(Color.Transparent, "Transparent"),
    RED(Color(0xFFF28B82), "Red"),
    ORANGE(Color(0xFFFBBC04), "Orange"), 
    YELLOW(Color(0xFFFFF475), "Yellow"),
    GREEN(Color(0xFFCCFF90), "Green"),
    TEAL(Color(0xFFA7FFEB), "Teal"),
    BLUE(Color(0xFF80DEEA), "Blue"),
    INDIGO(Color(0xFFAECBFA), "Indigo"),
    PURPLE(Color(0xFFD7AEFB), "Purple"),
    PINK(Color(0xFFFDCFE8), "Pink"),
    BROWN(Color(0xFFE6C9A8), "Brown"),
    GRAY(Color(0xFFE8EAED), "Gray");
    
    companion object {
        fun fromOrdinal(ordinal: Int): TaskColor {
            return values().getOrElse(ordinal) { DEFAULT }
        }
    }
}
