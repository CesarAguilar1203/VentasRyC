package com.example.ventasryc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ventasryc.data.Appointment
import com.example.ventasryc.data.Customer
import com.example.ventasryc.data.Product
import com.example.ventasryc.data.Sale
import com.example.ventasryc.ui.AgendaScreen
import com.example.ventasryc.ui.InventoryScreen
import com.example.ventasryc.ui.RecommendationScreen
import com.example.ventasryc.ui.ReportScreen
import com.example.ventasryc.ui.SalesScreen
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesApp() {
    val products = remember { mutableStateListOf<Product>() }
    val sales = remember { mutableStateListOf<Sale>() }
    val appointments = remember { mutableStateListOf<Appointment>() }
    val customers = remember { mutableStateListOf<Customer>() }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Ventas", "Inventario", "Agenda", "Clientes", "Reporte")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                CenterAlignedTopAppBar(title = { Text("VentasRyC") })
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
