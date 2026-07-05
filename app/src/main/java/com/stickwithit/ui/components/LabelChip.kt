package com.stickwithit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stickwithit.ui.theme.NavyBlue

/**
 * Small pill used to tag a task (e.g. "Design", "Dev", "Docs").
 * Reusable anywhere a category/label needs to be shown.
 */
@Composable
fun LabelChip(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    contentColor: Color = NavyBlue
) {
    Text(
        text = text,
        color = contentColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .background(color, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}
