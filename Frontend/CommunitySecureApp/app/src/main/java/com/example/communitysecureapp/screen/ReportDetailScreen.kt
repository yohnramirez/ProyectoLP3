package com.example.communitysecureapp.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.communitysecureapp.viewmodel.MapDataViewModel
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.communitysecureapp.model.comment.CommentRequest
import com.example.communitysecureapp.model.comment.CommentResponse
import com.example.communitysecureapp.model.report.MapMarkerData
import com.example.communitysecureapp.state.CreateCommentState
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import com.example.communitysecureapp.state.UpdateStatusState
import com.example.communitysecureapp.utils.navigation.Home
import com.example.communitysecureapp.viewmodel.LoginViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportDetailScreen(
    reportId: String,
    navController: NavController,
    viewModel: MapDataViewModel = hiltViewModel(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val commentCreateState by viewModel.commentCreated.collectAsState()
    val updateStatusState by viewModel.updateStatusState.collectAsState()
    val reportMarkers by viewModel.reportMarkers.collectAsState()
    val commentListResult by viewModel.comments.collectAsState()
    val comments = commentListResult?.data ?: emptyList()
    val userId by loginViewModel.userId.collectAsState()
    val usernames by viewModel.usernames.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val report = reportMarkers.find { it.id.toString() == reportId }

    var showDialog by remember { mutableStateOf(false) }
    val isOwner = report?.userId == userId.toString()
    val possibleStatuses = listOf("PENDIENTE", "RESUELTO", "DESCARTADO")
    val selectedStatus = remember(report) { mutableStateOf(report?.status ?: "PENDIENTE") }


    var commentText by remember { mutableStateOf("") }

    println("USERNAMES: $usernames")

    LaunchedEffect(report?.userId, usernames) {
        viewModel.fetchUsernameIfNeeded(report?.userId)
    }

    LaunchedEffect(userId) {
        viewModel.getUserById(userId.toString())
    }

    LaunchedEffect(reportId) {
        viewModel.getCommentsForReport(reportId.toLong())
    }

    LaunchedEffect(comments) {
        comments.forEach { comment ->
            viewModel.getUserById(comment.userId)
        }
    }

    LaunchedEffect(commentCreateState) {
        when (val state = commentCreateState) {
            is CreateCommentState.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(state.message)
                }
                commentText = ""
                viewModel.resetCommentCreatedState()
            }

            is CreateCommentState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Error: ${state.errorMessage}")
                }
                viewModel.resetCommentCreatedState()
            }

            else -> CreateCommentState.Idle
        }
    }

    LaunchedEffect(updateStatusState) {
        when (val state = updateStatusState) {
            is UpdateStatusState.Success -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(state.message)
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
                viewModel.resetUpdateStatusState()
            }

            is UpdateStatusState.Error -> {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Error: ${state.errorMessage}")
                }
                viewModel.resetUpdateStatusState()
            }

            else -> {}
        }
    }

    if (report == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Reporte no encontrado")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Volver")
                }
            }
        }
        return
    }

    Scaffold(
        topBar = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar"
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = "Detalle del Reporte",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 4.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Imagen del reporte", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val usernameReport = usernames[report.userId] ?: "Cargando..."
            ReportInfoSection(
                report = report,
                username = usernameReport,
                isOwner = isOwner,
                onEditStatusClick = { showDialog = true })

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Escribe un comentario") },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val newComment = CommentRequest(
                            reportId = reportId.toLong(),
                            userId = userId.toString(),
                            comment = commentText
                        )

                        if (commentText.isNotBlank()) {
                            viewModel.addComment(newComment)
                            commentText = ""
                        }
                    },
                    modifier = Modifier.height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = commentText.isNotBlank() && (commentCreateState !is CreateCommentState.Loading)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar"
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Comentarios",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (commentListResult == null && reportId.toLongOrNull() != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (comments.isEmpty()) {
                Text(
                    "No hay comentarios aún. ¡Sé el primero!",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .align(
                            Alignment.CenterHorizontally
                        )
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(
                        items = comments,
                        key = { comment -> comment.id }
                    ) { comment ->
                        val username = usernames[comment.userId] ?: "Cargando"
                        CommentItem(comment = comment, username = username)
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        coroutineScope.launch {
                            viewModel.updateReportStatus(report.id, selectedStatus.value)

                            val result = snackbarHostState.showSnackbar(
                                message = "Estado actualizado a ${selectedStatus.value}",
                                actionLabel = "OK"
                            )

                            if (result == SnackbarResult.Dismissed || result == SnackbarResult.ActionPerformed) {
                                navController.navigate(Home)
                            }
                        }
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Actualizar estado del reporte") },
                text = {
                    Column {
                        possibleStatuses.forEach { status ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedStatus.value = status
                                    }
                                    .padding(vertical = 8.dp)
                            ) {
                                RadioButton(
                                    selected = (selectedStatus.value == status),
                                    onClick = { selectedStatus.value = status }
                                )
                                Text(text = status)
                            }
                        }
                    }
                }
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportInfoSection(
    report: MapMarkerData,
    username: String,
    isOwner: Boolean,
    onEditStatusClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tipo de Reporte", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(report.typeName, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(8.dp))

            Text("Descripción", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(report.snippet, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Estado", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text(report.status ?: "Sin estado")
                if (isOwner) {
                    IconButton(onClick = onEditStatusClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar estado")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Text("Fecha", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(formatDate(report.dateCreated))

            Spacer(Modifier.height(8.dp))

            Text("Usuario", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(username)
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CommentItem(comment: CommentResponse, username: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = username,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text = comment.comment,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = formatDate(comment.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(dateString: String): String {
    return try {
        val parsedDate = LocalDateTime.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale("es", "ES"))
        parsedDate.format(formatter)
    } catch (e: Exception) {
        dateString
    }
}