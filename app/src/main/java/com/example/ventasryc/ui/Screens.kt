package com.example.ventasryc.ui

import android.content.Context
import android.graphics.pdf.PdfDocument
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.ventasryc.data.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util Locale

@Composable
fun SectionCard(title: String, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

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

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionCard("Resumen") {
            Text("Total ventas: $%.2f".format(totalSales))
            Text("Inversión: $%.2f".format(totalInvestment))
            Text("Ganancia: $%.2f".format(profit))
        }
        SectionCard("Registrar venta") {
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
        SectionCard("Historial de ventas") {
            if (sales.isEmpty()) {
                Text("Sin ventas registradas")
            } else {
                val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Producto", modifier = Modifier.weight(1f))
                        Text("Cant.", modifier = Modifier.weight(0.5f))
                        Text("Fecha", modifier = Modifier.weight(1f))
                    }
                    Divider()
                    LazyColumn(Modifier.height(200.dp)) {
                        items(sales) { sale ->
                            val product = products.find { it.id == sale.productId }
                            val date = dateFormat.format(Date(sale.timestamp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(product?.name ?: "ID ${sale.productId}", modifier = Modifier.weight(1f))
                                Text(sale.quantity.toString(), modifier = Modifier.weight(0.5f))
                                Text(date, modifier = Modifier.weight(1f))
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

// Inventory of products
@Composable
fun InventoryScreen(products: MutableList<Product>, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var cost by remember { mutableStateOf(TextFieldValue()) }
    var price by remember { mutableStateOf(TextFieldValue()) }
    var qty by remember { mutableStateOf(TextFieldValue()) }
    var photo by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val totalInvestment = products.sumOf { it.cost * it.quantity }
        val totalValue = products.sumOf { it.price * it.quantity }
        SectionCard("Resumen") {
            Text("Productos: ${products.size}")
            Text("Inversión: $%.2f".format(totalInvestment))
            Text("Valor potencial: $%.2f".format(totalValue))
        }
        SectionCard("Historial de productos") {
            if (products.isEmpty()) {
                Text("Sin productos registrados")
            } else {
                val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Producto", modifier = Modifier.weight(1f))
                        Text("Cant.", modifier = Modifier.weight(0.5f))
                        Text("Fecha", modifier = Modifier.weight(1f))
                    }
                    Divider()
                    LazyColumn(Modifier.height(200.dp)) {
                        items(products) { product ->
                            val date = dateFormat.format(Date(product.timestamp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(product.name, modifier = Modifier.weight(1f))
                                Text(product.quantity.toString(), modifier = Modifier.weight(0.5f))
                                Text(date, modifier = Modifier.weight(1f))
                            }
                            Divider()
                        }
                    }
                }
            }
        }
        SectionCard("Agregar producto") {
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
}

// Agenda of appointments
@Composable
fun AgendaScreen(appointments: MutableList<Appointment>, modifier: Modifier = Modifier) {
    var customer by remember { mutableStateOf(TextFieldValue()) }
    var alarm by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionCard("Resumen") {
            Text("Total apartados: ${appointments.size}")
        }
        SectionCard("Historial de apartados") {
            if (appointments.isEmpty()) {
                Text("Sin apartados registrados")
            } else {
                val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cliente", modifier = Modifier.weight(1f))
                        Text("Alarma", modifier = Modifier.weight(1f))
                        Text("Registro", modifier = Modifier.weight(1f))
                    }
                    Divider()
                    LazyColumn(Modifier.height(200.dp)) {
                        items(appointments) { appt ->
                            val alarmDate = dateFormat.format(Date(appt.alarmTime))
                            val created = dateFormat.format(Date(appt.timestamp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(appt.customer, modifier = Modifier.weight(1f))
                                Text(alarmDate, modifier = Modifier.weight(1f))
                                Text(created, modifier = Modifier.weight(1f))
                            }
                            Divider()
                        }
                    }
                }
            }
        }
        SectionCard("Nuevo apartado") {
            OutlinedTextField(value = customer, onValueChange = { customer = it }, label = { Text("Cliente") })
            OutlinedTextField(value = alarm, onValueChange = { alarm = it }, label = { Text("Alarma (ms)") })
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
}

// Simple recommendation: lists customers ordered by spending
@Composable
fun RecommendationScreen(customers: MutableList<Customer>, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var spent by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionCard("Resumen") {
            Text("Total clientes: ${customers.size}")
        }
        SectionCard("Historial de clientes") {
            if (customers.isEmpty()) {
                Text("Sin clientes registrados")
            } else {
                val ordered = customers.sortedByDescending { it.totalSpent }
                val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cliente", modifier = Modifier.weight(1f))
                        Text("Gastado", modifier = Modifier.weight(1f))
                        Text("Fecha", modifier = Modifier.weight(1f))
                    }
                    Divider()
                    LazyColumn(Modifier.height(200.dp)) {
                        items(ordered) { customer ->
                            val date = dateFormat.format(Date(customer.timestamp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(customer.name, modifier = Modifier.weight(1f))
                                Text("$${customer.totalSpent}", modifier = Modifier.weight(1f))
                                Text(date, modifier = Modifier.weight(1f))
                            }
                            Divider()
                        }
                    }
                }
            }
        }
        SectionCard("Agregar cliente") {
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
    val reports = remember { mutableStateListOf<File>() }
    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionCard("Reporte mensual") {
            Button(onClick = { reports.add(generateMonthlyReport(context, sales)) }) {
                Text("Generar PDF mensual")
            }
        }
        SectionCard("Historial de reportes") {
            if (reports.isEmpty()) {
                Text("Sin reportes generados")
            } else {
                val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Archivo", modifier = Modifier.weight(1f))
                        Text("Fecha", modifier = Modifier.weight(1f))
                    }
                    Divider()
                    LazyColumn(Modifier.height(200.dp)) {
                        items(reports) { file ->
                            val date = dateFormat.format(Date(file.lastModified()))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(file.name, modifier = Modifier.weight(1f))
                                Text(date, modifier = Modifier.weight(1f))
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryScreen(products: MutableList<Product>, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var cost by remember { mutableStateOf(TextFieldValue()) }
    var price by remember { mutableStateOf(TextFieldValue()) }
    var qty by remember { mutableStateOf(TextFieldValue()) }
    var photo by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val totalInvestment = products.sumOf { it.cost * it.quantity }
        val totalValue = products.sumOf { it.price * it.quantity }
        SectionCard("Resumen") {
            Text("Productos: ${products.size}")
            Text("Inversión: $%.2f".format(totalInvestment))
            Text("Valor potencial: $%.2f".format(totalValue))
        }
        SectionCard("Historial de productos") {
            if (products.isEmpty()) {
                Text("Sin productos registrados")
            } else {
                val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Producto", modifier = Modifier.weight(1f))
                        Text("Cant.", modifier = Modifier.weight(0.5f))
                        Text("Fecha", modifier = Modifier.weight(1f))
                    }
                    Divider()
                    LazyColumn(Modifier.height(200.dp)) {
                        items(products) { product ->
                            val date = dateFormat.format(Date(product.timestamp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(product.name, modifier = Modifier.weight(1f))
                                Text(product.quantity.toString(), modifier = Modifier.weight(0.5f))
                                Text(date, modifier = Modifier.weight(1f))
                            }
                            Divider()
                        }
                    }
                }
            }
        }
        SectionCard("Agregar producto") {
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
}


// Agenda of appointments with alarmTime placeholder.

@Composable
fun AgendaScreen(appointments: MutableList<Appointment>, modifier: Modifier = Modifier) {
    var customer by remember { mutableStateOf(TextFieldValue()) }
    var alarm by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionCard("Resumen") {
            Text("Total apartados: ${appointments.size}")
        }
        SectionCard("Historial de apartados") {
            if (appointments.isEmpty()) {
                Text("Sin apartados registrados")
            } else {
                val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cliente", modifier = Modifier.weight(1f))
                        Text("Alarma", modifier = Modifier.weight(1f))
                        Text("Registro", modifier = Modifier.weight(1f))
                    }
                    Divider()
                    LazyColumn(Modifier.height(200.dp)) {
                        items(appointments) { appt ->
                            val alarmDate = dateFormat.format(Date(appt.alarmTime))
                            val created = dateFormat.format(Date(appt.timestamp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(appt.customer, modifier = Modifier.weight(1f))
                                Text(alarmDate, modifier = Modifier.weight(1f))
                                Text(created, modifier = Modifier.weight(1f))
                            }
                            Divider()
                        }
                    }
                }
            }
        }
        SectionCard("Nuevo apartado") {
            OutlinedTextField(value = customer, onValueChange = { customer = it }, label = { Text("Cliente") })
            OutlinedTextField(value = alarm, onValueChange = { alarm = it }, label = { Text("Alarma (ms)") })
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
}

// Simple recommendation: lists customers ordered by spending
@Composable
fun RecommendationScreen(customers: MutableList<Customer>, modifier: Modifier = Modifier) {
    var name by remember { mutableStateOf(TextFieldValue()) }
    var spent by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionCard("Resumen") {
            Text("Total clientes: ${customers.size}")
        }
        SectionCard("Historial de clientes") {
            if (customers.isEmpty()) {
                Text("Sin clientes registrados")
            } else {
                val ordered = customers.sortedByDescending { it.totalSpent }
                val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cliente", modifier = Modifier.weight(1f))
                        Text("Gastado", modifier = Modifier.weight(1f))
                        Text("Fecha", modifier = Modifier.weight(1f))
                    }
                    Divider()
                    LazyColumn(Modifier.height(200.dp)) {
                        items(ordered) { customer ->
                            val date = dateFormat.format(Date(customer.timestamp))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(customer.name, modifier = Modifier.weight(1f))
                                Text("$${customer.totalSpent}", modifier = Modifier.weight(1f))
                                Text(date, modifier = Modifier.weight(1f))
                            }
                            Divider()
                        }
                    }
                }
            }
        }
        SectionCard("Agregar cliente") {
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
}

// Generates a very simple PDF summary of sales
// Generates a very simple PDF summary of sales
fun generateMonthlyReport(context: Context, sales: List<Sale>): File {
    val pdf = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 400, 1).create()
    val page = pdf.startPage(pageInfo)
    val canvas = page.canvas
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 12f
    }
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
    val reports = remember { mutableStateListOf<File>() }
    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionCard("Reporte mensual") {
            Button(onClick = { reports.add(generateMonthlyReport(context, sales)) }) {
                Text("Generar PDF mensual")
            }
        }
        SectionCard("Historial de reportes") {
            if (reports.isEmpty()) {
                Text("Sin reportes generados")
            } else {
                val dateFormat = remember { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()) }
                Column {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Archivo", modifier = Modifier.weight(1f))
                        Text("Fecha", modifier = Modifier.weight(1f))
                    }
                    Divider()
                    LazyColumn(Modifier.height(200.dp)) {
                        items(reports) { file ->
                            val date = dateFormat.format(Date(file.lastModified()))
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(file.name, modifier = Modifier.weight(1f))
                                Text(date, modifier = Modifier.weight(1f))
                            }
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
