@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2Screen(
    viewModel: DepositViewModel,
    onBack: () -> Unit,
    onCalculate: () -> Unit
) {
    val state by viewModel.inputState.collectAsStateWithLifecycle()
    val rate = state.interestRate
    // Если ставка не определена (например, шаг 1 не пройден), показываем предупреждение
    val rateText = if (rate != null) "${rate.toInt()}%" else "Не определена"

    var expanded by remember { mutableStateOf(false) }
    val options = if (rate != null) listOf(rateText) else listOf("Сначала укажите срок")

    Scaffold(
        topBar = { TopAppBar(title = { Text("Шаг 2: Дополнительно") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Выпадающий список ставки
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = rateText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Процентная ставка") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = { expanded = false }
                        )
                    }
                }
            }
            if (rate == null) {
                Text(
                    "Срок не указан или некорректен. Вернитесь на предыдущий шаг.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = state.monthlyTopUp,
                onValueChange = viewModel::updateMonthlyTopUp,
                label = { Text("Ежемесячное пополнение (необязательно)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack) {
                    Text("Назад")
                }
                Button(
                    onClick = {
                        if (rate != null) {
                            viewModel.calculate()
                            onCalculate()
                        }
                    },
                    enabled = rate != null
                ) {
                    Text("Рассчитать")
                }
            }
        }
    }
}