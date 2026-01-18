package com.darc.app.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isComplete) {
        if (state.isComplete) {
            onComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0D0D),
                        Color(0xFF1A1A2E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { (state.currentStep + 1) / 3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color(0xFFBB86FC),
                trackColor = Color(0xFF2D2D2D)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Content based on step
            when (state.currentStep) {
                0 -> NameInputStep(
                    name = state.name,
                    onNameChange = viewModel::updateName,
                    onNext = viewModel::nextStep
                )
                1 -> FocusSelectionStep(
                    selectedFocus = state.focus,
                    onFocusSelect = viewModel::updateFocus,
                    onNext = viewModel::nextStep,
                    onBack = viewModel::previousStep
                )
                2 -> ConfirmationStep(
                    name = state.name,
                    focus = state.focus,
                    isLoading = state.isLoading,
                    onConfirm = viewModel::completeOnboarding,
                    onBack = viewModel::previousStep
                )
            }
        }
    }
}

@Composable
private fun NameInputStep(
    name: String,
    onNameChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "IDENTIFY YOURSELF",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your name, Hunter.",
            fontSize = 16.sp,
            color = Color(0xFF888888)
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Your Name", color = Color(0xFF555555))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color(0xFFBB86FC),
                unfocusedBorderColor = Color(0xFF333333),
                cursorColor = Color(0xFFBB86FC)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onNext,
            enabled = name.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFBB86FC),
                disabledContainerColor = Color(0xFF333333)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "CONTINUE",
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
private fun FocusSelectionStep(
    selectedFocus: String,
    onFocusSelect: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val focusOptions = listOf(
        FocusOption("gym", "GYM", "Physical training and strength"),
        FocusOption("study", "STUDY", "Learning and knowledge"),
        FocusOption("skills", "SKILLS", "Practice and mastery"),
        FocusOption("general", "GENERAL", "Daily habits and discipline")
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CHOOSE YOUR PATH",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select your primary focus.",
            fontSize = 16.sp,
            color = Color(0xFF888888)
        )

        Spacer(modifier = Modifier.height(32.dp))

        focusOptions.forEach { option ->
            FocusCard(
                option = option,
                isSelected = selectedFocus == option.id,
                onClick = { onFocusSelect(option.id) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("BACK", letterSpacing = 2.sp)
            }

            Button(
                onClick = onNext,
                enabled = selectedFocus.isNotBlank(),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBB86FC),
                    disabledContainerColor = Color(0xFF333333)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "CONTINUE",
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }
        }
    }
}

@Composable
private fun FocusCard(
    option: FocusOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) Color(0xFF2D1B4E) else Color(0xFF1A1A1A)
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color(0xFFBB86FC) else Color(0xFF333333),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = option.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFFBB86FC) else Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = option.description,
                fontSize = 14.sp,
                color = Color(0xFF888888)
            )
        }
    }
}

@Composable
private fun ConfirmationStep(
    name: String,
    focus: String,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val focusLabel = when (focus) {
        "gym" -> "GYM"
        "study" -> "STUDY"
        "skills" -> "SKILLS"
        else -> "GENERAL"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CONFIRM YOUR IDENTITY",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Player card preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2D1B4E),
                            Color(0xFF1A1A2E)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color(0xFFBB86FC),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "RANK E",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFBB86FC),
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = name.uppercase(),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "\"Novice\"",
                    fontSize = 16.sp,
                    color = Color(0xFF888888),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatPreview("LVL", "1")
                    StatPreview("EXP", "0")
                    StatPreview("FOCUS", focusLabel)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Your starter routine will be created based on your focus.",
            fontSize = 14.sp,
            color = Color(0xFF888888),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                enabled = !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF333333)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("BACK", letterSpacing = 2.sp)
            }

            Button(
                onClick = onConfirm,
                enabled = !isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBB86FC)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "BEGIN",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPreview(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF666666),
            letterSpacing = 1.sp
        )
    }
}

private data class FocusOption(
    val id: String,
    val title: String,
    val description: String
)
