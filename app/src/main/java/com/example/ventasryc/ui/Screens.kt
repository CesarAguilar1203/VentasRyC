package com.example.ventasryc.ui

import android.content.Context
import android.graphics.pdf.PdfDocument
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.ventasryc.data.*
import java.io.File
import java.io.FileOutputStream

// Screen to manage sales in real time and calculate investment/profit
@Composable
fun SalesScreen(products: List<Product>, sales: MutableList<Sale>, modifier: Modifier = Modifier) {
    var productId by remember { mutableStateOf(TextFieldValue()) }
    var quantity by remember { mutableStateOf(TextFieldValue()) }

    val totalSales = remember(sales, products) {
        sales.sumOf { sale ->
            val product = products.find { it.id == sale.productId }
            (product?.price ?: 0.0) * sale.quantity
        }
    }
    val totalInvestment = remember(sales, products) {
        sales.sumOf { sale ->
            val product = products.find { it.id == sale.productId }
            (product?.cost ?: 0.0) * sale.quantity
        }
    }
    val profit = totalSales - totalInvestment

    Column(modifier.padding(16.dp)) {
        Text("Total ventas: $%.2f".format(totalSales))
        Text("Inversión: $%.2f".format(totalInvestment))
        Text("Ganancia: $%.2f".format(profit))
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = productId,
            onValueChange = { productId = it },
            label = { Text("ID Producto") }
        )
        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Cantidad") },
            modifier = Modifier.padding(top = 8.dp)
        )
        Button(
            onClick = {
                val id = productId.text.toIntOrNull()
                val qty = quantity.text.toIntOrNull()
                if (id != null && qty != null) {
                    sales.add(Sale(id, qty))
                    productId = TextFieldValue("")
                    quantity = TextFieldValue("")
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Agregar venta")
        }
    }
}

// Inventory of products. Photo is represented by a URL string for simplicity.
@Composable
fun InventoryScreen(products: MutableList<Product>, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var cost by remember { mutableStateOf(TextFieldValue()) }
    var price by remember { mutableStateOf(TextFieldValue()) }
    var qty by remember { mutableStateOf(TextFieldValue()) }
    var photo by remember { mutableStateOf(TextFieldValue()) }

    Column(modifier.padding(16.dp)) {
        LazyColumn(Modifier.height(200.dp)) {
            items(products) { product ->
                Text("${product.name} - ${product.quantity}")
            }
        }
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") })
        OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Costo") })
        OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Precio") })
        OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("Cantidad") })
        OutlinedTextField(value = photo, onValueChange = { photo = it }, label = { Text("Foto (URI)") })
        Button(onClick = {
            val pCost = cost.text.toDoubleOrNull()
            val pPrice = price.text.toDoubleOrNull()
            val pQty = qty.text.toIntOrNull()
            if (name.text.isNotBlank() && pCost != null && pPrice != null && pQty != null) {
                products.add(
                    Product(
                        id = products.size + 1,
                        name = name.text,
                        cost = pCost,
                        price = pPrice,
                        quantity = pQty,
                        photoUri = if (photo.text.isBlank()) null else photo.text
                    )
                )
                name = TextFieldValue("")
                cost = TextFieldValue("")
                price = TextFieldValue("")
                qty = TextFieldValue("")
                photo = TextFieldValue("")
            }
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Agregar producto")
        }
    }
}

// Agenda of appointments with alarmTime placeholder.
@Composable
fun AgendaScreen(appointments: MutableList<Appointment>, modifier: Modifier = Modifier) {
    var customer by remember { mutableStateOf(TextFieldValue()) }
    var alarm by remember { mutableStateOf(TextFieldValue()) }

    Column(modifier.padding(16.dp)) {
        LazyColumn(Modifier.height(200.dp)) {
            items(appointments) { appt ->
                Text("${appt.customer} - ${appt.alarmTime}")
            }
        }
        OutlinedTextField(value = customer, onValueChange = { customer = it }, label = { Text("Cliente") })
        OutlinedTextField(value = alarm, onValueChange = { alarm = it }, label = { Text("Alarm (ms)") })
        Button(onClick = {
            val time = alarm.text.toLongOrNull()
            if (customer.text.isNotBlank() && time != null) {
                appointments.add(Appointment(appointments.size + 1, customer.text, time))
                customer = TextFieldValue("")
                alarm = TextFieldValue("")
            }
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Agregar apartado")
        }
    }
}

// Simple recommendation: lists customers ordered by spending
@Composable
fun RecommendationScreen(customers: MutableList<Customer>, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var spent by remember { mutableStateOf(TextFieldValue()) }

    val ordered = customers.sortedByDescending { it.totalSpent }

    Column(modifier.padding(16.dp)) {
        LazyColumn(Modifier.height(200.dp)) {
            items(ordered) { customer ->
                Text("${customer.name} - $${customer.totalSpent}")
            }
        }
        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Cliente") })
        OutlinedTextField(value = spent, onValueChange = { spent = it }, label = { Text("Gastado") })
        Button(onClick = {
            val amount = spent.text.toDoubleOrNull()
            if (name.text.isNotBlank() && amount != null) {
                customers.add(Customer(customers.size + 1, name.text, amount))
                name = TextFieldValue("")
                spent = TextFieldValue("")
            }
        }, modifier = Modifier.padding(top = 8.dp)) {
            Text("Agregar cliente")
        }
    }
}

// Generates a very simple PDF summary of sales
fun generateMonthlyReport(context: Context, sales: List<Sale>): File {
    val pdf = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 400, 1).create()
    val page = pdf.startPage(pageInfo)
    val canvas = page.canvas
    val paint = android.graphics.Paint().apply { color = android.graphics.Color.BLACK; textSize = 12f }
    var y = 25f
    canvas.drawText("Reporte de ventas", 10f, y, paint)
    sales.forEach {
        y += 20f
        canvas.drawText("Producto ${it.productId} x${it.quantity}", 10f, y, paint)
    }
    pdf.finishPage(page)
    val file = File(context.cacheDir, "reporte_mensual.pdf")
    pdf.writeTo(FileOutputStream(file))
    pdf.close()
    return file
}

@Composable
fun ReportScreen(sales: List<Sale>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var generatedFile by remember { mutableStateOf<File?>(null) }
    Column(modifier.padding(16.dp)) {
        Button(onClick = { generatedFile = generateMonthlyReport(context, sales) }) {
            Text("Generar PDF mensual")
        }
        generatedFile?.let {
            Text("Generado: ${it.absolutePath}")
        }
    }
}
