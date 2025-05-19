package com.example.communitysecureapp.screen

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.communitysecureapp.model.login.LoginRequest
import com.example.communitysecureapp.utils.loader.Loader
import com.example.communitysecureapp.utils.navigation.Home
import com.example.communitysecureapp.utils.navigation.Login
import com.example.communitysecureapp.utils.navigation.Register
import com.example.communitysecureapp.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val loaderState by viewModel.loaderState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun validateForm(): Boolean {
        val isEmailValid = when {
            email.isEmpty() -> {
                emailError = "Email es requerido"
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailError = "Formato de email invalido"
                false
            }

            else -> {
                emailError = null
                true
            }
        }

        val isPasswordValid = when {
            password.isEmpty() -> {
                passwordError = "Contraseña es requerida"
                false
            }

            else -> {
                passwordError = null
                true
            }
        }

        return isEmailValid && isPasswordValid
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { paddingValues ->

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 30.dp)
        ) {

            Text(
                text = "MapGuard",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .align(Alignment.CenterHorizontally)
            )

            EmailField(
                value = email,
                onChange = {
                    email = it
                    emailError = null
                },
                errorMessage = emailError,
                modifier = Modifier.fillMaxWidth()
            )

            PasswordField(
                value = password,
                onChange = {
                    password = it
                    passwordError = null
                },
                submit = {
                    if (validateForm()) {
                        viewModel.login(
                            LoginRequest(
                                email = email,
                                password = password
                            )
                        )
                    }
                },
                errorMessage = passwordError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate(Register) }) {
                Text("¿No tienes una cuenta? Crea una aquí")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (validateForm()) {
                        viewModel.login(
                            LoginRequest(
                                email = email,
                                password = password
                            )
                        )
                    }
                },
                enabled = !loaderState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (loaderState.isLoading) {
                    Loader()
                } else {
                    Text("Iniciar sesión")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LaunchedEffect(loaderState.result) {
                loaderState.result?.let { result ->
                    if (result.success) {

                        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

                        prefs.edit().apply {
                            putString("auth_token", result.token)
                            putLong(
                                "token_expiry",
                                result.expiry ?: (System.currentTimeMillis() / 1000 + 3600)
                            )
                            putString("user_name", result.userName)
                            apply()
                        }

                        Toast.makeText(
                            context,
                            "¡Bienvenido, ${result.userName}!",
                            Toast.LENGTH_LONG
                        ).show()

                        navController.navigate(Home) {
                            popUpTo(Login) { inclusive = true }
                        }

                        viewModel.clearLoginViewModel()

                    } else {
                        errorMessage = "Credenciales incorrectas"
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = errorMessage!!,
                                withDismissAction = true
                            )
                        }
                        viewModel.clearLoginViewModel()
                    }
                }
            }
        }
    }
}

@Composable
fun EmailField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String?,
    label: String = "Email",
    placeholder: String = "Ingrese su email"
) {

    val focusManager = LocalFocusManager.current
    val emailIcon = @Composable {
        Icon(
            Icons.Default.Person,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }

    OutlinedTextField(
        value = value,
        modifier = modifier,
        leadingIcon = emailIcon,
        onValueChange = onChange,
        isError = errorMessage != null,
        supportingText = { if (errorMessage != null) Text(errorMessage) },
        colors = OutlinedTextFieldDefaults.colors(
            errorBorderColor = MaterialTheme.colorScheme.error
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        label = { Text(label) },
        placeholder = { Text(placeholder) }
    )
}

@Composable
fun PasswordField(
    value: String,
    onChange: (String) -> Unit,
    submit: () -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String?,
    label: String = "Password",
    placeholder: String = "Ingrese su contraseña"
) {

    var isPasswordVisible by remember { mutableStateOf(false) }

    val passwordIcon = @Composable {
        Icon(
            Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }

    val viewPasswordIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
                if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }

    OutlinedTextField(
        value = value,
        modifier = modifier,
        leadingIcon = passwordIcon,
        trailingIcon = viewPasswordIcon,
        onValueChange = onChange,
        isError = errorMessage != null,
        supportingText = { if (errorMessage != null) Text(errorMessage) },
        colors = OutlinedTextFieldDefaults.colors(
            errorBorderColor = MaterialTheme.colorScheme.error
        ),
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = { submit() }
        ),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}