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

    private val repository = DepositRepository(
        AppDatabase.getDatabase(application).depositDao()
    )

    // Состояние ввода
    data class InputState(
        val initialAmount: String = "",
        val periodMonths: String = "",
        val monthlyTopUp: String = "",
        val interestRate: Double? = null,
        val calculationResult: CalculationResult? = null
    )

    data class CalculationResult(
        val finalAmount: Double,
        val interestEarned: Double
    )

    private val _inputState = MutableStateFlow(InputState())
    val inputState: StateFlow<InputState> = _inputState

    // История
    private val _history =
        MutableStateFlow<List<DepositCalculation>>(emptyList())

    val history: StateFlow<List<DepositCalculation>> = _history

    // Детальная запись
    private val _detailCalculation =
        MutableStateFlow<DepositCalculation?>(null)

    val detailCalculation: StateFlow<DepositCalculation?> =
        _detailCalculation

    // Ошибки
    private val _errorMessage =
        MutableStateFlow<String?>(null)

    val errorMessage: StateFlow<String?> =
        _errorMessage

    init {
        viewModelScope.launch {
            repository.allCalculations.collect {
                _history.value = it
            }
        }
    }

    fun updateInitialAmount(value: String) {
        _inputState.value =
            _inputState.value.copy(initialAmount = value)

        _errorMessage.value = null
    }

    fun updatePeriodMonths(value: String) {
        _inputState.value =
            _inputState.value.copy(periodMonths = value)

        _errorMessage.value = null
    }

    fun updateMonthlyTopUp(value: String) {
        _inputState.value =
            _inputState.value.copy(monthlyTopUp = value)

        _errorMessage.value = null
    }

    fun validateStep1(): Boolean {

        val state = _inputState.value

        val amount = state.initialAmount.toDoubleOrNull()
        val months = state.periodMonths.toIntOrNull()

        if (amount == null || amount <= 0) {
            _errorMessage.value =
                "Введите корректный стартовый взнос"

            return false
        }

        if (months == null || months <= 0) {
            _errorMessage.value =
                "Введите корректный срок"

            return false
        }

        val rate = when {
            months < 6 -> 15.0
            months in 6..11 -> 10.0
            else -> 5.0
        }

        _inputState.value =
            state.copy(interestRate = rate)

        return true
    }

    fun calculate() {

        val state = _inputState.value

        val initial =
            state.initialAmount.toDouble()

        val months =
            state.periodMonths.toInt()

        val rate =
            state.interestRate ?: return

        val monthlyRate =
            rate / 100.0 / 12.0

        val topUp =
            state.monthlyTopUp.toDoubleOrNull() ?: 0.0

        val finalAmount =
            if (topUp > 0.0) {

                val growth =
                    (1.0 + monthlyRate)
                        .pow(months.toDouble())

                initial * growth +
                        topUp * (growth - 1.0) / monthlyRate

            } else {

                initial *
                        (1.0 + monthlyRate)
                            .pow(months.toDouble())
            }

        val totalInvested =
            initial + topUp * months

        val interestEarned =
            finalAmount - totalInvested

        _inputState.value =
            state.copy(
                calculationResult = CalculationResult(
                    finalAmount = finalAmount,
                    interestEarned = interestEarned
                )
            )
    }

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

            _inputState.value = InputState()
        }
    }

    fun loadDetail(id: Long) {

        viewModelScope.launch {

            _detailCalculation.value =
                repository.getById(id)
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}