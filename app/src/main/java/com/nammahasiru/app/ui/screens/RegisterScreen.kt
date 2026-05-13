package com.nammahasiru.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nammahasiru.app.R
import com.nammahasiru.app.viewmodel.AuthViewModel
import com.nammahasiru.app.viewmodel.PasswordStrength
import com.nammahasiru.app.viewmodel.isStrongPassword
import com.nammahasiru.app.viewmodel.isValidEmail

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToSignIn: () -> Unit
) {
    val state by viewModel.registerState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var firstName           by remember { mutableStateOf("") }
    var lastName            by remember { mutableStateOf("") }
    var email               by remember { mutableStateOf("") }
    var password            by remember { mutableStateOf("") }
    var confirmPassword     by remember { mutableStateOf("") }
    var showPassword        by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    // Touched trackers
    var fNameTouched   by remember { mutableStateOf(false) }
    var lNameTouched   by remember { mutableStateOf(false) }
    var emailTouched   by remember { mutableStateOf(false) }
    var pwdTouched     by remember { mutableStateOf(false) }
    var cPwdTouched    by remember { mutableStateOf(false) }

    val pwdStrength = remember(password) { PasswordStrength.evaluate(password) }

    val fNameError  = fNameTouched && firstName.isBlank()
    val lNameError  = lNameTouched && lastName.isBlank()
    val emailError  = emailTouched && !isValidEmail(email)
    val pwdError    = pwdTouched && !isStrongPassword(password) && password.isNotEmpty()
    val cPwdError   = cPwdTouched && confirmPassword != password

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.clearRegisterError()
            onRegisterSuccess()
        }
    }

    // Entry animation
    var visible by remember { mutableStateOf(false) }
    val cardAlpha by animateFloatAsState(if (visible) 1f else 0f, tween(700, 200), label = "cardAlpha")
    val cardSlide by animateFloatAsState(if (visible) 0f else 80f, tween(700, 200, FastOutSlowInEasing), label = "cardSlide")
    val logoScale by animateFloatAsState(if (visible) 1f else 0.5f, tween(600), label = "logoScale")

    val infiniteAnim = rememberInfiniteTransition(label = "float")
    val floatY by infiniteAnim.animateFloat(
        initialValue = -5f, targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "floatY"
    )

    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1B5E20), Color(0xFF2E7D32), Color(0xFF388E3C))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Logo ──────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .offset(y = floatY.dp)
                    .scale(logoScale)
                    .shadow(8.dp, RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(50))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_plant_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(16.dp))
            Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text("Join the green movement 🌳", fontSize = 15.sp, color = Color.White.copy(alpha = 0.75f))
            Spacer(Modifier.height(24.dp))

            // ── Card ──────────────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(cardAlpha)
                    .offset(y = cardSlide.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Register", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))

                    // ── Name row ──────────────────────────────────────────────
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it; fNameTouched = true; viewModel.clearRegisterError() },
                            label = { Text("First Name") },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFF2E7D32)) },
                            isError = fNameError,
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Right) }),
                            colors = authFieldColors()
                        )
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it; lNameTouched = true; viewModel.clearRegisterError() },
                            label = { Text("Last Name") },
                            isError = lNameError,
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            colors = authFieldColors()
                        )
                    }
                    if (fNameError || lNameError) {
                        Text("First and last name are required", fontSize = 12.sp, color = Color(0xFFC62828))
                    }

                    // ── Email ─────────────────────────────────────────────────
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; emailTouched = true; viewModel.clearRegisterError() },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF2E7D32)) },
                        isError = emailError,
                        supportingText = if (emailError) {{ Text("Enter a valid email address") }} else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        colors = authFieldColors()
                    )

                    // ── Password ──────────────────────────────────────────────
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; pwdTouched = true; viewModel.clearRegisterError() },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF2E7D32)) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFF757575)
                                )
                            }
                        },
                        isError = pwdError,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        colors = authFieldColors()
                    )

                    // ── Password strength bar ─────────────────────────────────
                    if (password.isNotEmpty()) {
                        PasswordStrengthIndicator(pwdStrength)
                    }

                    // ── Password rules ────────────────────────────────────────
                    if (pwdTouched && password.isNotEmpty()) {
                        PasswordRules(pwdStrength)
                    }

                    // ── Confirm Password ──────────────────────────────────────
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; cPwdTouched = true; viewModel.clearRegisterError() },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF2E7D32)) },
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFF757575)
                                )
                            }
                        },
                        isError = cPwdError,
                        supportingText = if (cPwdError) {{ Text("Passwords do not match") }} else null,
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                markAllTouched { fNameTouched = true; lNameTouched = true; emailTouched = true; pwdTouched = true; cPwdTouched = true }
                                viewModel.register(firstName, lastName, email, password, confirmPassword)
                            }
                        ),
                        colors = authFieldColors()
                    )

                    // ── Error banner ──────────────────────────────────────────
                    state.errorMessage?.let { msg ->
                        Surface(color = Color(0xFFFFEBEE), shape = RoundedCornerShape(10.dp)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Warning, null, tint = Color(0xFFC62828), modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(msg, fontSize = 13.sp, color = Color(0xFFC62828))
                            }
                        }
                    }

                    // ── Register button ───────────────────────────────────────
                    Button(
                        onClick = {
                            fNameTouched = true; lNameTouched = true
                            emailTouched = true; pwdTouched = true; cPwdTouched = true
                            focusManager.clearFocus()
                            viewModel.register(firstName, lastName, email, password, confirmPassword)
                        },
                        enabled  = !state.isLoading,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.5.dp)
                        } else {
                            Icon(Icons.Default.PersonAdd, null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Create Account", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Already have an account? ", fontSize = 14.sp, color = Color(0xFF616161))
                        Text(
                            "Sign In",
                            fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32),
                            modifier = Modifier.clickable { onNavigateToSignIn() }
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Reusable helpers ──────────────────────────────────────────────────────────

@Composable
private fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Color(0xFF2E7D32),
    focusedLabelColor  = Color(0xFF2E7D32),
    cursorColor        = Color(0xFF2E7D32)
)

private inline fun markAllTouched(block: () -> Unit) = block()

@Composable
private fun PasswordStrengthIndicator(strength: PasswordStrength.Result) {
    val targetColor = when (strength.level) {
        PasswordStrength.Level.EMPTY,
        PasswordStrength.Level.WEAK      -> Color(0xFFC62828)
        PasswordStrength.Level.FAIR      -> Color(0xFFF57F17)
        PasswordStrength.Level.STRONG    -> Color(0xFF2E7D32)
        PasswordStrength.Level.VERY_STRONG -> Color(0xFF1B5E20)
    }
    val animatedProgress by animateFloatAsState(
        targetValue = strength.progress,
        animationSpec = tween(400),
        label = "strengthProgress"
    )
    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(400),
        label = "strengthColor"
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = animatedColor,
            trackColor = Color(0xFFE0E0E0)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Password Strength", fontSize = 11.sp, color = Color(0xFF757575))
            Text(strength.label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = animatedColor)
        }
    }
}

@Composable
private fun PasswordRules(strength: PasswordStrength.Result) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        PasswordRule("At least 8 characters",         strength.hasMinLength)
        PasswordRule("Uppercase letter (A–Z)",         strength.hasUppercase)
        PasswordRule("Lowercase letter (a–z)",         strength.hasLowercase)
        PasswordRule("Number (0–9)",                   strength.hasDigit)
        PasswordRule("Special character (!@#$…)",    strength.hasSpecial)
    }
}

@Composable
private fun PasswordRule(label: String, met: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (met) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (met) Color(0xFF2E7D32) else Color(0xFF9E9E9E),
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (met) Color(0xFF2E7D32) else Color(0xFF9E9E9E)
        )
    }
}
