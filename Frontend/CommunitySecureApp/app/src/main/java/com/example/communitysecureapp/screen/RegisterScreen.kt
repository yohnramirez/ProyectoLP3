package com.example.communitysecureapp.screen

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.communitysecureapp.model.document.TypeDocument
import com.example.communitysecureapp.model.gender.Gender
import com.example.communitysecureapp.model.register.RegisterRequest
import com.example.communitysecureapp.state.RegisterState
import com.example.communitysecureapp.utils.navigation.Home
import com.example.communitysecureapp.viewmodel.RegisterViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RegisterScreen(navController: NavController, viewModel: RegisterViewModel = hiltViewModel()) {

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val genders by viewModel.genders.collectAsState()
    val registerState by viewModel.registerState.collectAsState()
    val typeDocuments by viewModel.typeDocuments.collectAsState()

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

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var countryError by remember { mutableStateOf<String?>(null) }
    var cityError by remember { mutableStateOf<String?>(null) }
    var birthdayError by remember { mutableStateOf<String?>(null) }
    var genderError by remember { mutableStateOf<String?>(null) }
    var typeDocumentError by remember { mutableStateOf<String?>(null) }
    var numberDocumentError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getTypesDocument()
        viewModel.getGenders()
    }

    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is RegisterState.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Registro exitoso")
                    navController.navigate(Home) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                viewModel.resetRegisterState()
            }

            is RegisterState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Error: ${state.message}")
                }
                viewModel.resetRegisterState()
            }

            else -> {}
        }
    }

    fun validateForm(): Boolean {
        val isEmailValid = when {
            email.isEmpty() -> {
                emailError = "Email es requerido"
                false
            }

            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailError = "Formato de email inválido"
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

            password.length < 6 -> {
                passwordError = "La contraseña debe tener al menos 6 caracteres"
                false
            }

            else -> {
                emailError = null
                true
            }
        }

        val isFullNameValid = when {
            fullName.isEmpty() -> {
                fullNameError = "Nombre es requerido"
                false
            }

            fullName.length > 100 -> {
                fullNameError = "Limite de caracteres superado"
                false
            }

            else -> {
                fullNameError = null
                true
            }
        }

        val isAddressValid = when {
            address.isEmpty() -> {
                addressError = "Direccion es requerida"
                false
            }

            address.length > 150 -> {
                addressError = "Limite de caracteres superado"
                false
            }

            else -> {
                addressError = null
                true
            }
        }

        val isCountryValid = when {
            country.isEmpty() -> {
                countryError = "Pais requerido"
                false
            }

            else -> {
                countryError = null
                true
            }
        }

        val isCityValid = when {
            city.isEmpty() -> {
                cityError = "Ciudad requerida"
                false
            }

            city.length > 100 -> {
                cityError = "Limite de caracteres superado"
                false
            }

            else -> {
                cityError = null
                true
            }
        }

        val isBirthdayValid = when {
            birthday.isEmpty() -> {
                birthdayError = "Fecha requerida"
                false
            }

            else -> {
                birthdayError = null
                true
            }
        }

        val isGenderValid = when {
            gender.isEmpty() -> {
                genderError = "Genero requerido"
                false
            }

            else -> {
                genderError = null
                true
            }
        }

        val isTypeDocumentValid = when {
            typeDocument.isEmpty() -> {
                typeDocumentError = "Tipo requerido"
                false
            }

            else -> {
                typeDocumentError = null
                true
            }
        }

        val isNumberDocumentValid = when {
            numberDocument.isEmpty() -> {
                numberDocumentError = "Numero requerido"
                false
            }

            numberDocument.length > 35 -> {
                numberDocumentError = "Limite de caracteres superado"
                false
            }

            else -> {
                numberDocumentError = null
                true
            }
        }

        return isEmailValid && isPasswordValid && isFullNameValid && isAddressValid && isCountryValid && isCityValid && isBirthdayValid && isGenderValid && isTypeDocumentValid && isNumberDocumentValid
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Registro", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)

            NameRegisterField(
                value = fullName,
                onChange = {
                    fullName = it
                },
                errorMessage = fullNameError,
                modifier = Modifier.fillMaxWidth()
            )

            EmailRegisterField(
                value = email,
                onChange = {
                    email = it
                },
                errorMessage = emailError,
                modifier = Modifier.fillMaxWidth()
            )

            PasswordRegisterField(
                value = password,
                onChange = {
                    password = it
                },
                submit = {
                },
                errorMessage = passwordError,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                CountryRegisterField(
                    value = country,
                    onChange = {
                        country = it
                    },
                    errorMessage = countryError,
                    modifier = Modifier.weight(1f)
                )

                CityRegisterField(
                    value = city,
                    onChange = {
                        city = it
                    },
                    errorMessage = cityError,
                    modifier = Modifier.weight(1f)
                )
            }

            AddressRegisterField(
                value = address,
                onChange = {
                    address = it
                },
                errorMessage = addressError,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                TypeDocumentField(
                    value = typeDocument,
                    onChange = { typeDocument = it },
                    errorMessage = typeDocumentError,
                    modifier = Modifier.weight(1f),
                    types = typeDocuments?.data
                )

                NumberDocumentField(
                    value = numberDocument,
                    onChange = { numberDocument = it },
                    errorMessage = numberDocumentError,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                BirthdayRegisterField(
                    birthday = birthday,
                    onChange = {
                        birthday = it
                    },
                    errorMessage = birthdayError,
                    modifier = Modifier.weight(1f)
                )

                GenderField(
                    value = gender,
                    onChange = { gender = it },
                    errorMessage = genderError,
                    modifier = Modifier.weight(1f),
                    genders = genders?.data,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (validateForm()) {
                        viewModel.register(
                            RegisterRequest(
                                fullName = fullName,
                                email = email,
                                address = address,
                                typeDocument = typeDocument,
                                numberDocument = numberDocument,
                                city = city,
                                country = country,
                                gender = gender,
                                password = password,
                                birthday = birthday
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = registerState !is RegisterState.Loading && email.isNotBlank() && password.isNotBlank()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (registerState is RegisterState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Registrando...")
                    } else {
                        Text("Registrarse")
                    }
                }
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
    label: String = "Contraseña",
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
fun NumberDocumentField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String?,
    label: String = "Número documento",
    placeholder: String = "Ingrese documento"
) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        modifier = modifier,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeDocumentField(
    value: String,
    types: List<TypeDocument>?,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String?,
    label: String = "Tipo doc",
    placeholder: String = "Selección tipo"
) {

    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

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
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
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
            types?.forEach { type ->
                DropdownMenuItem(
                    text = { Text("${type.mask} - ${type.name}") },
                    onClick = {
                        onChange("${type.mask} - ${type.name}")
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderField(
    value: String,
    genders: List<Gender>?,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String?,
    label: String = "Género",
    placeholder: String = "Género"
) {

    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

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
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
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
            genders?.forEach { gender ->
                DropdownMenuItem(
                    text = { Text(gender.name) },
                    onClick = {
                        onChange(gender.name)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryRegisterField(
    value: String,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    errorMessage: String?,
    label: String = "País",
    placeholder: String = "Selección país"
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
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
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
    modifier: Modifier = Modifier,
    onChange: (String) -> Unit,
    errorMessage: String?,
    label: String = "Nacimiento",
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
        modifier = modifier
    ) {
        OutlinedTextField(
            value = birthday,
            onValueChange = { },
            placeholder = { Text(placeholder) },
            label = { Text(label) },
            readOnly = true,
            isError = errorMessage != null,
            supportingText = { if (errorMessage != null) Text(errorMessage) },
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
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