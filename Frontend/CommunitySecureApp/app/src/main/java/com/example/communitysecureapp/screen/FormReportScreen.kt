package com.example.communitysecureapp.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.communitysecureapp.state.FormState
import com.example.communitysecureapp.viewmodel.FormReportViewModel
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormReportScreen(
    navController: NavController,
    formState: FormState,
    onFormStateChange: (FormState) -> Unit,
    onCloseSheet: () -> Unit,
    onSubmit: (GeoPoint, String, String) -> Unit,
    viewModel: FormReportViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val typeReports by viewModel.typeReports.collectAsState()
    var expandedTypeReportField by remember { mutableStateOf(false) }

    var reportType by remember(formState.reportType) { mutableStateOf(formState.reportType) }
    var description by remember(formState.description) { mutableStateOf(formState.description) }
    var incidentLocation by remember(formState.incidentLocation) { mutableStateOf<GeoPoint?>(formState.incidentLocation) }

    LaunchedEffect(reportType, description, incidentLocation) {
        onFormStateChange(
            FormState(
                incidentLocation = incidentLocation,
                reportType = reportType,
                description = description
            )
        )
    }

    LaunchedEffect(navController) {
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<GeoPoint?>(
            "selected_location",
            null
        )
            ?.collect { geoPoint ->
                if (geoPoint != null) {
                    incidentLocation = geoPoint
                    navController.currentBackStackEntry?.savedStateHandle?.remove<GeoPoint>("selected_location")
                }
            }
    }

    LaunchedEffect(Unit) {
        viewModel.getReportTypes()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
    ) {
        Text(
            "Nuevo Reporte",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(24.dp))

        ExposedDropdownMenuBox(
            expanded = expandedTypeReportField,
            onExpandedChange = { expandedTypeReportField = !expandedTypeReportField },
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = reportType,
                onValueChange = { reportType = it },
                label = { Text("Tipo de Incidente") },
                placeholder = { Text("Seleccione tipo reporte") },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                singleLine = true,
                isError = reportType.isBlank() && reportType.isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    errorBorderColor = MaterialTheme.colorScheme.error
                ),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTypeReportField)
                },
                supportingText = { if (reportType.isBlank() && reportType.isNotEmpty()) Text("Este campo es obligatorio") }
            )

            ExposedDropdownMenu(
                expanded = expandedTypeReportField,
                onDismissRequest = { expandedTypeReportField = false }
            ) {
                typeReports?.data?.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.name) },
                        onClick = {
                            reportType = type.name
                            expandedTypeReportField = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Ubicación del Incidente", style = MaterialTheme.typography.titleSmall)
        Spacer(modifier = Modifier.height(8.dp))

        if (incidentLocation != null) {
            Text(
                "Lat: ${"%.5f".format(incidentLocation!!.latitude)}, Lon: ${
                    "%.5f".format(
                        incidentLocation!!.longitude
                    )
                }",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    navController.navigate("map_selector?lat=${incidentLocation!!.latitude}&lon=${incidentLocation!!.longitude}")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Cambiar ubicación")
            }
        } else {
            Button(
                onClick = {
                    navController.navigate("map_selector")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Seleccionar ubicación")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción (máx. 500 caracteres)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 8,
            isError = description.isBlank() && description.isNotEmpty(),
            colors = OutlinedTextFieldDefaults.colors(
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            supportingText = { if (description.isBlank() && description.isNotEmpty()) Text("Este campo es obligatorio") }
        )

        Text("${description.length}/500", modifier = Modifier.align(Alignment.End))

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                focusManager.clearFocus()
                onCloseSheet()
            }) {
                Text("Cancelar")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (reportType.isNotBlank() && description.isNotBlank() && incidentLocation != null) {
                        focusManager.clearFocus()
                        onSubmit(incidentLocation!!, reportType, description)
                        onCloseSheet()
                    } else {
                        Toast.makeText(
                            context,
                            "Por favor, completa todos los campos",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                enabled = reportType.isNotBlank() && description.isNotBlank() && incidentLocation != null
            ) {
                Text("Enviar")
            }
        }
    }
}