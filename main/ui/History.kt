@Composable
fun HistoryScreen(
    viewModel: DepositViewModel,
    onBack: () -> Unit,
    onItemClick: (Long) -> Unit
) {
    val history by viewModel.history.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История расчётов") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("История пуста")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(history, key = { it.id }) { calc ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable { onItemClick(calc.id) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
                                    .format(java.util.Date(calc.calculationDate)),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text("Взнос: ${String.format("%.2f", calc.initialAmount)} → Итог: ${String.format("%.2f", calc.finalAmount)}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryDetailScreen(
    viewModel: DepositViewModel,
    id: Long,
    onBack: () -> Unit
) {
    val detail by viewModel.detailCalculation.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        viewModel.loadDetail(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали расчёта") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        detail?.let { calc ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(padding)
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Дата: ${java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(calc.calculationDate))}")
                    Text("Стартовый взнос: ${String.format("%.2f", calc.initialAmount)}")
                    Text("Срок: ${calc.periodMonths} мес.")
                    Text("Ставка: ${calc.interestRate.toInt()}%")
                    Text("Пополнение: ${calc.monthlyTopUp?.let { String.format("%.2f", it) } ?: "нет"}")
                    Text("Итоговая сумма: ${String.format("%.2f", calc.finalAmount)}", style = MaterialTheme.typography.titleMedium)
                    Text("Начисленные проценты: ${String.format("%.2f", calc.interestEarned)}", color = MaterialTheme.colorScheme.primary)
                }
            }
        } ?: Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}