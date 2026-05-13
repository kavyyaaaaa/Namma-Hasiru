package com.nammahasiru.app.ui.screens

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
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nammahasiru.app.R
import com.nammahasiru.app.viewmodel.AuthViewModel
import com.nammahasiru.app.viewmodel.isValidEmail

@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val state by viewModel.loginState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    var email         by remember { mutableStateOf("") }
    var password      by remember { mutableStateOf("") }
    var showPassword  by remember { mutableStateOf(false) }
    var rememberMe    by remember { mutableStateOf(false) }

    // Field-level errors shown after first attempt
    var emailTouched    by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    val emailError    = emailTouched && !isValidEmail(email)
    val passwordError = passwordTouched && password.isBlank()

    // Navigate on success
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            viewModel.clearLoginError()
            onLoginSuccess()
        }
    }

    // Entry animation
    var visible by remember { mutableStateOf(false) }
    val cardAlpha by animateFloatAsState(if (visible) 1f else 0f, tween(700, 200), label = "cardAlpha")
    val cardSlide by animateFloatAsState(if (visible) 0f else 60f, tween(700, 200, FastOutSlowInEasing), label = "cardSlide")
    val logoScale by animateFloatAsState(if (visible) 1f else 0.5f, tween(700), label = "logoScale")

    // Floating logo
    val infiniteAnim = rememberInfiniteTransition(label = "float")
    val floatY by infiniteAnim.animateFloat(
        initialValue  = -5f, targetValue = 5f,
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
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── Logo ──────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .offset(y = floatY.dp)
                    .scale(logoScale)
                    .shadow(8.dp, RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(50))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_plant_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Namma Hasiru",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                "Welcome back 🌿",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(Modifier.height(32.dp))

            // ── Card ──────────────────────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(cardAlpha)
                    .offset(y = cardSlide.dp),
                shape  = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    Text(
                        "Sign In",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )

                    // ── Email ──────────────────────────────────────────────────
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; emailTouched = true; viewModel.clearLoginError() },
                        label = { Text("Email Address") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, null, tint = Color(0xFF2E7D32))
                        },
                        isError = emailError,
                        supportingText = if (emailError) {{ Text("Enter a valid email address") }} else null,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Color(0xFF2E7D32),
                            focusedLabelColor    = Color(0xFF2E7D32),
                            cursorColor          = Color(0xFF2E7D32)
                        )
                    )

                    // ── Password ───────────────────────────────────────────────
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; passwordTouched = true; viewModel.clearLoginError() },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, null, tint = Color(0xFF2E7D32))
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = Color(0xFF757575)
                                )
                            }
                        },
                        isError = passwordError,
                        supportingText = if (passwordError) {{ Text("Password cannot be empty") }} else null,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                emailTouched = true
                                passwordTouched = true
                                viewModel.login(email, password, rememberMe)
                            }
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Color(0xFF2E7D32),
                            focusedLabelColor    = Color(0xFF2E7D32),
                            cursorColor          = Color(0xFF2E7D32)
                        )
                    )

                    // ── Remember me + Forgot password ─────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF2E7D32)
                                )
                            )
                            Text(
                                "Remember me",
                                fontSize = 13.sp,
                                color = Color(0xFF424242)
                            )
                        }
                        Text(
                            text = "Forgot Password?",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2E7D32),
                            modifier = Modifier.clickable { /* future: show reset dialog */ }
                        )
                    }

                    // ── Error banner ──────────────────────────────────────────
                    state.errorMessage?.let { msg ->
                        Surface(
                            color = Color(0xFFFFEBEE),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFC62828),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(msg, fontSize = 13.sp, color = Color(0xFFC62828))
                            }
                        }
                    }

                    // ── Sign In button ────────────────────────────────────────
                    Button(
                        onClick = {
                            emailTouched = true
                            passwordTouched = true
                            focusManager.clearFocus()
                            viewModel.login(email, password, rememberMe)
                        },
                        enabled  = !state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(27.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2E7D32)
                        )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color    = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Icon(Icons.Default.Login, null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Sign In",
                                fontSize   = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color.White
                            )
                        }
                    }

                    // ── Divider ───────────────────────────────────────────────
                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    // ── Register link ─────────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Don't have an account? ",
                            fontSize = 14.sp,
                            color = Color(0xFF616161)
                        )
                        Text(
                            "Register",
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFF2E7D32),
                            modifier   = Modifier.clickable { onNavigateToRegister() }
                        )
                    }
                }
            }
        }
    }
}
