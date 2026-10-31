package com.aismarttrader.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max
import kotlin.math.min

private val Navy = Color(0xFF101827)
private val Panel = Color(0xFF1B2638)
private val Green = Color(0xFF35C98A)
private val Red = Color(0xFFFF6B6B)
private val Muted = Color(0xFF9AA8BA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { TraderApp() }
    }
}

data class Stock(val symbol: String, val name: String, val price: Double, val change: Double)

private val sampleStocks = listOf(
    Stock("RELIANCE", "Reliance Industries", 1428.50, 0.82),
    Stock("TCS", "Tata Consultancy Services", 3892.00, -0.34),
    Stock("INFY", "Infosys", 1542.70, 1.16),
    Stock("HDFCBANK", "HDFC Bank", 1765.20, -0.58),
    Stock("ITC", "ITC Limited", 421.35, 0.45)
)

@Composable
fun TraderApp() {
    var selected by remember { mutableStateOf(sampleStocks.first()) }
    var cash by remember { mutableDoubleStateOf(100000.0) }
    var units by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("Demo account ready. No real orders are placed.") }

    MaterialTheme(colorScheme = darkColorScheme(
        background = Navy, surface = Panel, primary = Green,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Surface(Modifier.fillMaxSize(), color = Navy) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text("AI SMART TRADER", fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("NSE / BSE • Paper trading simulator", color = Muted, fontSize = 12.sp)
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricCard("DEMO CASH", "₹${"%,.0f".format(cash)}", Modifier.weight(1f))
                        MetricCard("HOLDING", "$units units", Modifier.weight(1f))
                    }
                }
                item {
                    Text("WATCHLIST", color = Muted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        sampleStocks.forEach { stock ->
                            StockRow(stock, selected.symbol == stock.symbol) { selected = stock }
                        }
                    }
                }
                item {
                    Column(
                        Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(16.dp)).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(selected.symbol, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(selected.name, color = Muted, fontSize = 12.sp)
                        Text("₹${"%,.2f".format(selected.price)}", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                        Text("${if (selected.change >= 0) "+" else ""}${selected.change}% (sample data)",
                            color = if (selected.change >= 0) Green else Red)
                        Text("PRICE CHART • illustrative", color = Muted, fontSize = 11.sp)
                        SampleChart(selected.change >= 0)
                    }
                }
                item {
                    val signal = if (selected.change >= 0.5) "UPWARD BIAS" else if (selected.change <= -0.5) "DOWNWARD BIAS" else "NEUTRAL"
                    Column(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(16.dp)).padding(16.dp)) {
                        Text("SIGNAL LAB", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(signal, color = if (signal == "UPWARD BIAS") Green else if (signal == "DOWNWARD BIAS") Red else Color.Yellow,
                            fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Rule-based demo signal from fixed sample percentage only. Not AI prediction or investment advice.",
                            color = Muted, fontSize = 12.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("RSI / MACD: Not connected • Live feed: Not connected", color = Muted, fontSize = 11.sp)
                    }
                }
                item {
                    Column(Modifier.fillMaxWidth().background(Panel, RoundedCornerShape(16.dp)).padding(16.dp)) {
                        Text("PAPER TRADING", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Selected: ${selected.symbol} • Sample price ₹${"%.2f".format(selected.price)}",
                            color = Muted, fontSize = 12.sp)
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    if (cash >= selected.price) {
                                        cash -= selected.price
                                        units += 1
                                        message = "Demo BUY recorded: ${selected.symbol}"
                                    } else message = "Not enough demo cash."
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Green)
                            ) { Text("DEMO BUY", color = Navy) }
                            Button(
                                onClick = {
                                    if (units > 0) {
                                        units -= 1
                                        cash += selected.price
                                        message = "Demo SELL recorded: ${selected.symbol}"
                                    } else message = "No demo units to sell."
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = Red)
                            ) { Text("DEMO SELL", color = Navy) }
                        }
                        Text(message, color = Muted, fontSize = 12.sp)
                        TextButton(onClick = {
                            cash = 100000.0; units = 0; message = "Demo account reset."
                        }) { Text("Reset demo account") }
                    }
                }
                item {
                    Text("IMPORTANT: This starter uses fixed sample prices. It does not connect to NSE/BSE, a broker, or place real trades. Use only as a learning simulator.",
                        color = Color(0xFFFFD27A), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MetricCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier.background(Panel, RoundedCornerShape(14.dp)).padding(14.dp)) {
        Text(label, color = Muted, fontSize = 10.sp)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 19.sp)
    }
}

@Composable
fun StockRow(stock: Stock, selected: Boolean, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(
            containerColor = if (selected) Color(0xFF293A50) else Panel,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(stock.symbol, fontWeight = FontWeight.Bold)
                Text(stock.name, color = Muted, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("₹${"%.2f".format(stock.price)}")
                Text("${if (stock.change >= 0) "+" else ""}${stock.change}%",
                    color = if (stock.change >= 0) Green else Red, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SampleChart(up: Boolean) {
    Canvas(Modifier.fillMaxWidth().height(125.dp).background(Color(0xFF131E2D), RoundedCornerShape(8.dp))) {
        val values = if (up) listOf(.72f,.66f,.70f,.53f,.58f,.42f,.48f,.30f,.36f,.22f,.27f,.16f)
                     else listOf(.20f,.27f,.24f,.39f,.34f,.48f,.44f,.60f,.53f,.72f,.66f,.80f)
        val step = size.width / (values.size - 1)
        val color = if (up) Green else Red
        for (i in 0 until values.size - 1) {
            drawLine(color, Offset(i * step, values[i] * size.height),
                Offset((i + 1) * step, values[i + 1] * size.height), strokeWidth = 4f)
        }
    }
}
