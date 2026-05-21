@Composable
fun Step1Screen(
    viewModel: DepositViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    val state by viewModel.inputState.collectAsStateWithLifecycle()
    val error by viewModel.errorMessage.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Шаг 1: Параметры вклада") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.initialAmount,
                onValueChange = viewModel::updateInitialAmount,
                label = { Text("Стартовый взнос") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = state.periodMonths,
                onValueChange = viewModel::updatePeriodMonths,
                label = { Text("Срок вклада (месяцев)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (error != null) {
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack) {
                    Text("В начало")
                }
                Button(onClick = {
                    viewModel.clearError()
                    if (viewModel.validateStep1()) onNext()
                }) {
                    Text("Далее")
                }
            }
        }
    }
}