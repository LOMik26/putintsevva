package com.example.depositcalculator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deposit_calculations")
data class DepositCalculation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val initialAmount: Double,
    val periodMonths: Int,
    val interestRate: Double,       // годовая ставка в процентах
    val monthlyTopUp: Double?,      // null, если не указано
    val finalAmount: Double,
    val interestEarned: Double,
    val calculationDate: Long       // System.currentTimeMillis()
)