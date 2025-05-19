package com.example.communitysecureapp.utils.socket

import android.util.Log
import com.example.communitysecureapp.model.report.ReportBroadcast
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import ua.naiksoftware.stomp.Stomp
import javax.inject.Inject
import javax.inject.Singleton
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import java.util.concurrent.TimeUnit

@Singleton
class ReportSocketManager @Inject constructor(private val okHttpClient: OkHttpClient) {

    private var stompClient: StompClient? = null
    private val compositeDisposable = CompositeDisposable()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _newReportFlow = MutableSharedFlow<ReportBroadcast>()
    val newReportFlow: SharedFlow<ReportBroadcast> = _newReportFlow.asSharedFlow()

    private val webSocketUrl = "ws://10.0.2.2:8080/ws/websocket"

    fun connect() {

        if (stompClient?.isConnected == true) {
            Log.i("ReportWebSocketManager", "Ya conectado.")
            return
        }

        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, webSocketUrl, null, okHttpClient)
        compositeDisposable.clear()

        val headers: MutableList<StompHeader> = ArrayList()

        val dispLifecycle = stompClient!!.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> Log.i(
                        "ReportWebSocketManager",
                        "Conexión STOMP abierta."
                    )

                    LifecycleEvent.Type.CLOSED -> {
                        Log.i("ReportWebSocketManager", "Conexión STOMP cerrada.")
                    }

                    LifecycleEvent.Type.ERROR -> {
                        Log.e(
                            "ReportWebSocketManager",
                            "Error en conexión STOMP: ${lifecycleEvent.exception?.message}",
                            lifecycleEvent.exception
                        )
                    }

                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> Log.w(
                        "ReportWebSocketManager",
                        "STOMP: Falló el heartbeat del servidor."
                    )

                    else -> Log.d("ReportWebSocketManager", "Evento STOMP: ${lifecycleEvent.type}")
                }
            }, { throwable ->
                Log.e("ReportWebSocketManager", "Error en ciclo de vida STOMP: ", throwable)
            })

        compositeDisposable.add(dispLifecycle)

        val dispTopic =
            stompClient!!.topic("/topic/reports")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ stompMessage: StompMessage ->
                    Log.d(
                        "ReportWebSocketManager",
                        "Mensaje STOMP recibido: ${stompMessage.payload}"
                    )
                    try {
                        val reportDto =
                            Json.decodeFromString<ReportBroadcast>(stompMessage.payload)
                        scope.launch {
                            _newReportFlow.emit(reportDto)
                        }
                    } catch (e: Exception) {
                        Log.e(
                            "ReportWebSocketManager",
                            "Error al deserializar el reporte: ${e.message}",
                            e
                        )
                    }
                }, { throwable ->
                    Log.e("ReportWebSocketManager", "Error en subscripción al topic: ", throwable)
                })
        compositeDisposable.add(dispTopic)

        Log.i("ReportWebSocketManager", "Conectando a $webSocketUrl ...")
        stompClient!!.connect(headers)
    }

    fun disconnect() {
        Log.i("ReportWebSocketManager", "Desconectando STOMP...")
        compositeDisposable.clear()
        stompClient?.disconnect()
        stompClient = null
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
}