package com.example.depositcalculator.data

import kotlinx.coroutines.flow.Flow

class DepositRepository(private val dao: DepositDao) {
    val allCalculations: Flow<List<DepositCalculation>> = dao.getAllCalculations()

    suspend fun insert(calculation: DepositCalculation) {
        dao.insertCalculation(calculation)
    }

    suspend fun getById(id: Long): DepositCalculation? {
        return dao.getCalculationById(id)
    }
}