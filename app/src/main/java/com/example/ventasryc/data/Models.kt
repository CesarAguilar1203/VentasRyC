package com.example.ventasryc.data

// Data model for a product in inventory
// photoUri stores the path to the product image
// cost represents investment per item, price is sale price
data class Product(
    val id: Int,
    val name: String,
    val cost: Double,
    val price: Double,
    val quantity: Int,
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// Represents a sale of a product
// timestamp allows real time calculation
data class Sale(
    val productId: Int,
    val quantity: Int,
    val timestamp: Long = System.currentTimeMillis()
)

// Represents an appointment with a customer
// alarmTime in millis can be used with AlarmManager
data class Appointment(
    val id: Int,
    val customer: String,
    val alarmTime: Long,
    val timestamp: Long = System.currentTimeMillis()
)

// Simple customer model used for recommendations
data class Customer(
    val id: Int,
    val name: String,
    val totalSpent: Double,
    val timestamp: Long = System.currentTimeMillis()
)
