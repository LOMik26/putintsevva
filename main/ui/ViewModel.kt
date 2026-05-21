package com.example.depositcalculator.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.depositcalculator.data.AppDatabase
import com.example.depositcalculator.data.DepositCalculation
import com.example.depositcalculator.data.DepositRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.pow

class DepositViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DepositRepository

    // Состояние ввода
    data class InputState(
        val initialAmount: String = "",
        val periodMonths: String = "",
        val monthlyTopUp: String = "",   // пустая строка = не указано
        val interestRate: Double? = null, // будет определено после шага 1
        val calculationResult: CalculationResult? = null
    )

    data class CalculationResult(
        val finalAmount: Double,
        val interestEarned: Double
    )

    private val _inputState = MutableStateFlow(InputState())
    val inputState: StateFlow<InputState> = _inputState

    // История
    val history: StateFlow<List<DepositCalculation>> = repository.allCalculations
        .let { flow ->
            // Преобразуем Flow в StateFlow
            val stateFlow = MutableStateFlow<List<DepositCalculation>>(emptyList())
            viewModelScope.launch {
                flow.collect { stateFlow.value = it }
            }
            stateFlow
        }

    // Детальная запись
    private val _detailCalculation = MutableStateFlow<DepositCalculation?>(null)
    val detailCalculation: StateFlow<DepositCalculation?> = _detailCalculation

    // Ошибки валидации
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        val dao = AppDatabase.getDatabase(application).depositDao()
        repository = DepositRepository(dao)
    }

    // Обновление полей
    fun updateInitialAmount(value: String) {
        _inputState.value = _inputState.value.copy(initialAmount = value)
        _errorMessage.value = null
    }

    fun updatePeriodMonths(value: String) {
        _inputState.value = _inputState.value.copy(periodMonths = value)
        _errorMessage.value = null
    }

    fun updateMonthlyTopUp(value: String) {
        _inputState.value = _inputState.value.copy(monthlyTopUp = value)
        _errorMessage.value = null
    }

    // Валидация шага 1 и переход
    fun validateStep1(): Boolean {
        val state = _inputState.value
        val amount = state.initialAmount.toDoubleOrNull()
        val months = state.periodMonths.toIntOrNull()

        if (amount == null || amount <= 0) {
            _errorMessage.value = "Введите корректный стартовый взнос (положительное число)"
            return false
        }
        if (months == null || months <= 0) {
            _errorMessage.value = "Введите корректный срок в месяцах (целое положительное число)"
            return false
        }
        // Установка процентной ставки по правилам
        val rate = when {
            months < 6 -> 15.0
            months in 6..11 -> 10.0
            months >= 12 -> 5.0
            else -> null // не должно случиться
        }
        _inputState.value = state.copy(interestRate = rate)
        return true
    }

    // Расчёт итоговой суммы и процентов (сложный процент с ежемесячной капитализацией)
    fun calculate() {
        val state = _inputState.value
        val initial = state.initialAmount.toDouble()
        val months = state.periodMonths.toInt()
        val rate = state.interestRate ?: return // не должно быть null, но проверка
        val monthlyRate = rate / 100.0 / 12.0
        val topUp = state.monthlyTopUp.toDoubleOrNull() ?: 0.0

        val finalAmount = if (topUp > 0.0) {
            // Формула будущей стоимости с пополнениями
            val growth = (1.0 + monthlyRate).pow(months.toDouble())
            initial * growth + topUp * (growth - 1.0) / monthlyRate
        } else {
            initial * (1.0 + monthlyRate).pow(months.toDouble())
        }

        val totalInvested = initial + topUp * months
        val interestEarned = finalAmount - totalInvested

        _inputState.value = state.copy(
            calculationResult = CalculationResult(
                finalAmount = finalAmount,
                interestEarned = interestEarned
            )
        )
    }

    // Сохранение расчёта в БД
    fun saveCalculation() {
        val state = _inputState.value
        val result = state.calculationResult ?: return
        val calculation = DepositCalculation(
            initialAmount = state.initialAmount.toDouble(),
            periodMonths = state.periodMonths.toInt(),
            interestRate = state.interestRate!!,
            monthlyTopUp = state.monthlyTopUp.toDoubleOrNull(),
            finalAmount = result.finalAmount,
            interestEarned = result.interestEarned,
            calculationDate = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.insert(calculation)
            // Сброс состояния после сохранения
            _inputState.value = InputState()
        }
    }

    // Загрузка детальной информации
    fun loadDetail(id: Long) {
        viewModelScope.launch {
            _detailCalculation.value = repository.getById(id)
        }
    }

    // Сброс ошибки
    fun clearError() {
        _errorMessage.value = null
    }
}