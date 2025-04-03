package com.example.futbolitopocket.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.futbolitopocket.R
import kotlinx.coroutines.delay
import dev.ricknout.composesensors.accelerometer.rememberAccelerometerSensorValueAsState

@Composable
fun BallGameCanvas() {
    // Acelerómetro (compose-sensors)
    val sensorValue by rememberAccelerometerSensorValueAsState()

    // Estados para marcadores y pelota
    var scoreTop by remember { mutableStateOf(0) }
    var scoreBottom by remember { mutableStateOf(0) }

    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var ballX by remember { mutableStateOf(0f) }
    var ballY by remember { mutableStateOf(0f) }
    var velocityX by remember { mutableStateOf(0f) }
    var velocityY by remember { mutableStateOf(0f) }
    var isInitialized by remember { mutableStateOf(false) }

    // Parámetros de física
    val sensitivity = 1.5f
    val friction = 0.92f
    val restitution = 0.9f

    // Bucle de actualización
    LaunchedEffect(sensorValue) {
        while (true) {
            if (canvasSize.width > 0 && !isInitialized) {
                ballX = canvasSize.width / 2f
                ballY = canvasSize.height / 2f
                isInitialized = true
            }

            val (accX, accY, _) = sensorValue.value

            // Fuerza y fricción
            velocityX += -accX * sensitivity
            velocityY += accY * sensitivity
            velocityX *= friction
            velocityY *= friction

            // Posición
            ballX += velocityX
            ballY += velocityY

            delay(16) // ~60 FPS
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Imagen de fondo
        Image(
            painter = painterResource(R.drawable.medidas_campo_de_futbol),
            contentDescription = "Campo de Fútbol",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Canvas para pelota y colisiones
        Canvas(modifier = Modifier.fillMaxSize()) {
            canvasSize = size

            val fieldWidth = size.width
            val fieldHeight = size.height

            // Ajusta estos límites según tu imagen
            val leftBound = fieldWidth * 0.01f
            val rightBound = fieldWidth * 0.98f
            val topBound = fieldHeight * 0.07f
            val bottomBound = fieldHeight * 0.93f

            val ballRadius = 20f

            // Límite izquierdo
            if (ballX - ballRadius < leftBound) {
                ballX = leftBound + ballRadius
                velocityX = -velocityX * restitution
            }
            // Límite derecho
            else if (ballX + ballRadius > rightBound) {
                ballX = rightBound - ballRadius
                velocityX = -velocityX * restitution
            }

            // Portería arriba
            if (ballY - ballRadius < topBound) {
                // Ancho de la portería
                val goalWidth = (rightBound - leftBound) * 0.3f
                val goalLeftX = leftBound + ((rightBound - leftBound) - goalWidth) / 2f

                // Gol arriba
                if (ballX in goalLeftX..(goalLeftX + goalWidth)) {
                    scoreTop++
                    // Reubicar pelota al centro
                    ballX = (leftBound + rightBound) / 2f
                    ballY = (topBound + bottomBound) / 2f
                    velocityX = 0f
                    velocityY = 0f
                } else {
                    // Rebote
                    ballY = topBound + ballRadius
                    velocityY = -velocityY * restitution
                }
            }
            // Portería abajo
            else if (ballY + ballRadius > bottomBound) {
                val goalWidth = (rightBound - leftBound) * 0.3f
                val goalLeftX = leftBound + ((rightBound - leftBound) - goalWidth) / 2f

                // Gol abajo
                if (ballX in goalLeftX..(goalLeftX + goalWidth)) {
                    scoreBottom++
                    // Reubicar pelota al centro
                    ballX = (leftBound + rightBound) / 2f
                    ballY = (topBound + bottomBound) / 2f
                    velocityX = 0f
                    velocityY = 0f
                } else {
                    // Rebote
                    ballY = bottomBound - ballRadius
                    velocityY = -velocityY * restitution
                }
            }

            // Dibuja la pelota
            drawCircle(
                color = Color.White,
                radius = ballRadius,
                center = Offset(ballX, ballY)
            )
        }

        //  Marcador de la portería de arriba
        Text(
            text = "Marcador 1: $scoreTop",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        // 7) Marcador de la portería de abajo
        Text(
            text = "Marcador 2: $scoreBottom",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}
