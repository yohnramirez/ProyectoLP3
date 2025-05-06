package com.example.communitysecureapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.communitysecureapp.viewmodel.RegisterViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = hiltViewModel()) {

    val state by viewModel.state.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var fullName by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var country by rememberSaveable { mutableStateOf("") }
    var city by rememberSaveable { mutableStateOf("") }
    var birthday by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var typeDocument by rememberSaveable { mutableStateOf("") }
    var numberDocument by rememberSaveable { mutableStateOf("") }

    Surface {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
        ) {
            NameRegisterField(
                value = fullName,
                onChange = {
                    fullName = it
                },
                errorMessage = null,
                modifier = Modifier.fillMaxWidth()
            )

            EmailRegisterField(
                value = email,
                onChange = {
                    email = it
                },
                errorMessage = null,
                modifier = Modifier.fillMaxWidth()
            )

            PasswordRegisterField(
                value = password,
                onChange = {
                    password = it
                },
                submit = {
                },
                errorMessage = null,
                modifier = Modifier.fillMaxWidth()
            )

            CountryRegisterField(
                value = country,
                onChange = {
                    country = it
                },
                errorMessage = null,
                modifier = Modifier.fillMaxWidth()
            )

            CityRegisterField(
                value = city,
                onChange = {
                    city = it
                },
                errorMessage = null,
                modifier = Modifier.fillMaxWidth()
            )

            AddressRegisterField(
                value = address,
                onChange = {
                    address = it
                },
                errorMessage = null,
                modifier = Modifier.fillMaxWidth()
            )

            BirthdayRegisterField(
                birthday = birthday,
                onChange = {
                    birthday = it
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }
        }
    }
}

@Composable
fun NameRegisterField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String?,
    label: String = "Nombre",
    placeholder: String = "Nombre Completo"
) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        modifier = modifier,
        onValueChange = onChange,
        isError = errorMessage != null,
        supportingText = { if (errorMessage != null) Text(errorMessage) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        label = { Text(label) },
        placeholder = { Text(placeholder) }
    )
}

@Composable
fun EmailRegisterField(
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
fun PasswordRegisterField(
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

@Composable
fun AddressRegisterField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String?,
    label: String = "Dirección",
    placeholder: String = "Ingrese su dirección"
) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        modifier = modifier,
        onValueChange = onChange,
        isError = errorMessage != null,
        supportingText = { if (errorMessage != null) Text(errorMessage) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        label = { Text(label) },
        placeholder = { Text(placeholder) }
    )
}

@Composable
fun CityRegisterField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String?,
    label: String = "Ciudad",
    placeholder: String = "Ingrese su ciudad"
) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        modifier = modifier,
        onValueChange = onChange,
        isError = errorMessage != null,
        supportingText = { if (errorMessage != null) Text(errorMessage) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        label = { Text(label) },
        placeholder = { Text(placeholder) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryRegisterField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String?,
    label: String = "País",
    placeholder: String = "Ingrese su país"
) {

    val focusManager = LocalFocusManager.current
    val countries = listOf("Argentina", "Brasil", "Colombia", "Estados Unidos", "México")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            value = value,
            onValueChange = {},
            label = { Text(label) },
            placeholder = { Text(placeholder) },
            isError = errorMessage != null,
            supportingText = { if (errorMessage != null) Text(errorMessage) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth(),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countries.forEach { country ->
                DropdownMenuItem(
                    text = { Text(country) },
                    onClick = {
                        onChange(country)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BirthdayRegisterField(
    birthday: String,
    onChange: (String) -> Unit,
    label: String = "Fecha de nacimiento",
    placeholder: String = "DD/MM/YYYY"
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialDisplayedMonthMillis = System.currentTimeMillis(),
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis < System.currentTimeMillis()
            }
        }
    )
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    LaunchedEffect(selectedDate) {
        if (selectedDate.isNotEmpty()) {
            onChange(selectedDate)
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = birthday,
            onValueChange = { },
            placeholder = { Text(placeholder) },
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Seleccionar fecha",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        if (showDatePicker) {
            Popup(
                onDismissRequest = { showDatePicker = false },
                alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }
        }
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(Date(millis))
}