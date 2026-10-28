package com.example.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun InfoDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        },
        title = {
            Text(
                text = "Ghi Chú Nhanh - 100% Offline",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Dữ liệu của bạn được lưu an toàn tuyệt đối trong bộ nhớ SQLite trên thiết bị, không gửi qua máy chủ trung gian và hoạt động hoàn hảo khi không có mạng Internet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                InfoFeatureItem(
                    icon = Icons.Default.ColorLens,
                    title = "8 Thẻ màu trực quan",
                    desc = "Phân loại ghi chú nhanh theo màu sắc và danh mục tự đặt."
                )

                Spacer(modifier = Modifier.height(10.dp))

                InfoFeatureItem(
                    icon = Icons.Default.Search,
                    title = "Tìm kiếm tức thì",
                    desc = "Hỗ trợ tìm kiếm tiếng Việt không dấu và có dấu siêu tốc."
                )

                Spacer(modifier = Modifier.height(10.dp))

                InfoFeatureItem(
                    icon = Icons.Default.PushPin,
                    title = "Ghim ghi chú quan trọng",
                    desc = "Giữ các ghi chú cần ưu tiên luôn nằm ở đầu danh sách."
                )

                Spacer(modifier = Modifier.height(10.dp))

                InfoFeatureItem(
                    icon = Icons.Default.Lock,
                    title = "Bảo mật & Quyền riêng tư",
                    desc = "Không yêu cầu bất kỳ quyền mạng hay theo dõi nào."
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Đã hiểu")
            }
        }
    )
}

@Composable
private fun InfoFeatureItem(
    icon: ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
