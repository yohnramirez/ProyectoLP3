package com.example.communitysecureapp.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.communitysecureapp.viewmodel.LoginViewModel
import com.example.communitysecureapp.viewmodel.MapDataViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.communitysecureapp.model.report.ReportResponse
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import com.example.communitysecureapp.state.MyReportsState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyReportsScreen(
    navController: NavController,
    viewModel: MapDataViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {

    val reportsState by viewModel.myReportsState.collectAsState()
    val userId by loginViewModel.userId.collectAsState()

    LaunchedEffect(userId) {
        userId?.let {
            viewModel.getReportsByUserId(it)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Mis Reportes") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.Close, contentDescription = "Volver")
                }
            }
        )

        when (reportsState) {
            is MyReportsState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }

            is MyReportsState.Error -> {
                Text(
                    text = (reportsState as MyReportsState.Error).message,
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(),
                    color = MaterialTheme.colorScheme.error
                )
            }

            is MyReportsState.Empty -> {
                Text(
                    text = "No tienes reportes registrados.",
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize()
                )
            }

            is MyReportsState.Success -> {
                val reports = (reportsState as MyReportsState.Success).reports
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items = reports, key = { it.id }) { report ->
                        ReportItem(report = report)
                        HorizontalDivider()
                    }
                }
            }

            else -> {}
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportItem(report: ReportResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Título: ${report.type.name}", style = MaterialTheme.typography.titleMedium)
            Text("Descripcion: ${report.description}", style = MaterialTheme.typography.labelMedium)
            Text(
                "Fecha: ${formatDate(report.dateCreated)}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}