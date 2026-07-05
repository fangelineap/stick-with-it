package com.stickwithit.ui.components.board

import androidx.compose.ui.graphics.Color

/**
 * Lightweight UI models for rendering the board.
 *
 * These are presentation-only placeholders. Once the columns/rows data layer
 * exists, map the domain models (WorkspaceResult, and future ColumnResult /
 * RowResult) into these before handing them to [BoardView].
 */
data class BoardTaskUi(
    val label: String,
    val labelColor: Color,
    val title: String
)

data class BoardColumnUi(
    val name: String,
    val accent: Color,
    val tasks: List<BoardTaskUi>
)
