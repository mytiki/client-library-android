package com.mytiki.publish.client.optIn.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Button(title: String, outline: Boolean = false, onClick: () -> Unit) {
  val cardColor = if (outline) Color.Transparent else MaterialTheme.colorScheme.primary
  Card(
      onClick = onClick,
      shape = RoundedCornerShape(50),
      border = BorderStroke(3.8.dp, MaterialTheme.colorScheme.primary),
      modifier = Modifier.height(61.26.dp).fillMaxWidth(),
      colors = CardColors(cardColor, cardColor, cardColor, cardColor)) {
        Column(
            modifier = Modifier.height(61.26.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
              Text(
                  text = title,
                  style = MaterialTheme.typography.displayMedium,
                  color = if (outline) MaterialTheme.colorScheme.primary else Color(0xFF15925E),
                  textAlign = TextAlign.Center,
              )
            }
      }
}
