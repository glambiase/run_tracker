package com.glambiase.core.presentation.designsystem.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.glambiase.core.presentation.designsystem.RunTrackerTheme

@Composable
fun ClickableTextWrapper(
    text: AnnotatedString,
    annotationsClickHandler: Map<String, () -> Unit>,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    BasicText(
        text = text,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { tappedLocationCoordinates ->
                    textLayoutResult?.let { layoutResult ->
                        val tappedCharIndex = layoutResult.getOffsetForPosition(tappedLocationCoordinates)
                        // Check if there is an annotation whose span includes the index of the character that was tapped
                        text.getStringAnnotations(start = tappedCharIndex, end = tappedCharIndex)
                            .firstOrNull()
                            ?.let { annotation ->
                                annotationsClickHandler[annotation.item]?.invoke()
                            }
                    }
                }
            },
        style = style,
        onTextLayout = { textLayoutResult = it }
    )
}

@Preview(showBackground = true)
@Composable
private fun ClickableTextWrapperPreview() {
    RunTrackerTheme {
        val annotatedString = buildAnnotatedString {
            append("Agree to our ")
            pushStringAnnotation(tag = "link", annotation = "terms")
            withStyle(SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold)) {
                append("Terms")
            }
            pop()
            append(" and ")
            pushStringAnnotation(tag = "link", annotation = "privacy")
            withStyle(SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold)) {
                append("Privacy Policy")
            }
            pop()
        }

        ClickableTextWrapper(
            text = annotatedString,
            annotationsClickHandler = mapOf(
                "terms" to {},
                "privacy" to {}
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}