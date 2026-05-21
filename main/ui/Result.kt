@Composable
fun ResultScreen(
    viewModel: DepositViewModel,
    onSave: () -> Unit,
    onHome: () -> Unit
) {
    val state by viewModel.inputState.collectAsStateWithLifecycle()
    val result = state.calculationResult ?: return // если нет результата

    Scaffold(
        topBar = { TopAppBar(title = { Text("Результат расчёта") }) }
    ) { padding ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Стартовый взнос: ${String.format("%.2f", state.initialAmount.toDouble())}")
                Text("Срок вклада: ${state.periodMonths} мес.")
                Text("Процентная ставка: ${state.interestRate?.toInt()}%")
                Text("Ежемесячное пополнение: ${state.monthlyTopUp.toDoubleOrNull()?.let { String.format("%.2f", it) } ?: "нет"}")
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Итоговая сумма: ${String.format("%.2f", result.finalAmount)}",
                    style = MaterialTheme.typography.titleMedium)
                Text("Начисленные проценты: ${String.format("%.2f", result.interestEarned)}",
                    color = MaterialTheme.colorScheme.primary)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onSave) { Text("Сохранить") }
            OutlinedButton(onClick = onHome) { Text("В начало") }
        }
    }
}