package ci.nsu.mobile.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TemperatureScreen(
    viewModel: TemperatureViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Конвертер температуры", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = uiState.celsius,
                onValueChange = { viewModel.onCelsiusChanged(it) },
                label = { Text("Цельсий") },
                isError = !uiState.isCelsiusValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            if (!uiState.isCelsiusValid) {
                Text(text = "Введите корректное число", color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = uiState.fahrenheit,
                onValueChange = { viewModel.onFahrenheitChanged(it) },
                label = { Text("Фаренгейт") },
                isError = !uiState.isFahrenheitValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
            if (!uiState.isFahrenheitValid) {
                Text(text = "Введите корректное число", color = MaterialTheme.colorScheme.error)
            }

            Button(onClick = {
                // кнопка сброса значений
                viewModel.onCelsiusChanged("")
                viewModel.onFahrenheitChanged("")
            }, modifier = Modifier.padding(top = 24.dp)) {
                Text(text = "Сброс")
            }
        }
    }
}

