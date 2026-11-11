package com.daywise.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daywise.app.data.model.DebtEntity
import com.daywise.app.data.model.DebtType
import com.daywise.app.domain.util.CurrencyFormatter
import com.daywise.app.domain.util.DateUtils

@Composable
fun DebtItemCard(
    debt: DebtEntity,
    onToggleSettled: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLent = debt.type == DebtType.LENT.name
    val badgeColor = if (isLent) MaterialTheme.colorScheme.primary else Color(0xFFF97316)
    val badgeContainerColor = badgeColor.copy(alpha = 0.15f)
    val badgeText = if (isLent) "Lent (To Receive)" else "Borrowed (To Pay)"

    DaywiseCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        tintColor = if (debt.isSettled) Color(0xFF040E1B).copy(alpha = 0.82f) else Color(0xFF061424).copy(alpha = 0.65f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Settle Checkbox Button
                IconButton(
                    onClick = onToggleSettled,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (debt.isSettled) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                        contentDescription = "Toggle Settled",
                        tint = if (debt.isSettled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Person Name & Badge
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = debt.personName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                textDecoration = if (debt.isSettled) TextDecoration.LineThrough else null
                            ),
                            color = if (debt.isSettled) Color(0xFF9EBCD4) else Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeContainerColor
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Amount
                Text(
                    text = CurrencyFormatter.formatPaisa(debt.amountPaisa),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textDecoration = if (debt.isSettled) TextDecoration.LineThrough else null
                    ),
                    color = if (debt.isSettled) MaterialTheme.colorScheme.onSurfaceVariant else badgeColor
                )
            }

            // Note (if present)
            if (debt.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = debt.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF9EBCD4)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Date (Optional) & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Optional Date display
                val dateLabel = if (debt.dateMillis != null) {
                    "📅 " + DateUtils.formatDisplayDate(debt.dateMillis)
                } else {
                    "📅 No date set"
                }

                Text(
                    text = dateLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF9EBCD4).copy(alpha = 0.85f),
                    modifier = Modifier.weight(1f)
                )

                // Edit Button
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        tint = Color(0xFF9EBCD4),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Delete Button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFFF87171),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
