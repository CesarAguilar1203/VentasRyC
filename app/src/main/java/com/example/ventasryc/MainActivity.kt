package com.example.ventasryc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ventasryc.data.*
import com.example.ventasryc.ui.*
import com.example.ventasryc.ui.theme.VentasRyCTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VentasRyCTheme {
                SalesApp()
            }
        }
    }
}

@Composable
fun SalesApp() {
    val products = remember { mutableStateListOf<Product>() }
    val sales = remember { mutableStateListOf<Sale>() }
    val appointments = remember { mutableStateListOf<Appointment>() }
    val customers = remember { mutableStateListOf<Customer>() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Ventas", "Inventario", "Agenda", "Clientes", "Reporte")

    Scaffold(
        topBar = {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> SalesScreen(products, sales, Modifier.padding(innerPadding))
            1 -> InventoryScreen(products, Modifier.padding(innerPadding))
            2 -> AgendaScreen(appointments, Modifier.padding(innerPadding))
            3 -> RecommendationScreen(customers, Modifier.padding(innerPadding))
            4 -> ReportScreen(sales, Modifier.padding(innerPadding))
        }
    }
}
