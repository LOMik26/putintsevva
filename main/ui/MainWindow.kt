@Composable
fun MainScreen(
    onCalculate: () -> Unit,
    onHistory: () -> Unit,
    onExit: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Расчёт вкладов") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onCalculate, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text("Рассчитать")
            }
            Button(onClick = onHistory, modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text("История расчётов")
            }
            Button(onClick = onExit, modifier = Modifier.fillMaxWidth().padding(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Закрыть приложение")
            }
        }
    }
}