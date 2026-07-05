package com.stickwithit.ui.components.board

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stickwithit.ui.theme.AzureBlue
import com.stickwithit.ui.theme.Cream
import com.stickwithit.ui.theme.NavyBlue
import com.stickwithit.ui.theme.White

private val ColumnWidth = 300.dp

/**
 * Horizontally-scrollable board: a row of columns, each with its own task list.
 * The whole board also scrolls vertically so a tall column stays reachable.
 */
@Composable
fun BoardView(
    columns: List<BoardColumnUi>,
    modifier: Modifier = Modifier,
    onAddTask: (BoardColumnUi) -> Unit = {},
    onTaskClick: (BoardColumnUi, BoardTaskUi) -> Unit = { _, _ -> }
) {
    Row(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .horizontalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        columns.forEach { column ->
            BoardColumnView(
                column = column,
                onAddTask = { onAddTask(column) },
                onTaskClick = { task -> onTaskClick(column, task) }
            )
        }
    }
}

/**
 * A single board column: header (dot, name, count) and its stacked task cards
 * plus an "Add task" affordance.
 */
@Composable
fun BoardColumnView(
    column: BoardColumnUi,
    modifier: Modifier = Modifier,
    onAddTask: () -> Unit = {},
    onTaskClick: (BoardTaskUi) -> Unit = {}
) {
    Column(
        modifier = modifier
            .width(ColumnWidth)
            .clip(RoundedCornerShape(16.dp))
            .background(White)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(column.accent, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = column.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = NavyBlue,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "${column.tasks.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = AzureBlue,
                modifier = Modifier
                    .background(Cream, CircleShape)
                    .padding(horizontal = 9.dp, vertical = 3.dp)
            )
        }

        column.tasks.forEach { task ->
            TaskCard(
                task = task,
                accent = column.accent,
                onClick = { onTaskClick(task) }
            )
        }

        AddTaskButton(onClick = onAddTask)
    }
}

@Composable
private fun AddTaskButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            tint = AzureBlue,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Add task",
            color = AzureBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
