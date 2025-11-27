package com.emmascode.order

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import com.emmascode.models.*
import com.emmascode.utils.HttpClientManager
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonArray

class OrderCommand : CliktCommand(
    name = "order",
    help = "Manage orders"
) {
    init {
        subcommands(
            OrderListCommand(),
            OrderViewCommand(),
            OrderCreateCommand(),
            OrderUpdateCommand()
        )
    }

    override fun run() = Unit
}

class OrderListCommand : CliktCommand(
    name = "list",
    help = "List orders"
) {
    private val terminal = Terminal()
    private val userId by option("-u", "--user", help = "Filter by user ID").int()
    private val restaurantId by option("-r", "--restaurant", help = "Filter by restaurant ID").int()
    private val status by option("-s", "--status", help = "Filter by status")
        .choice("pending", "confirmed", "preparing", "ready", "delivered", "cancelled")

    override fun run() = runBlocking {
        if (!HttpClientManager.isAuthenticated()) {
            terminal.println(red("❌ Authentication required"))
            return@runBlocking
        }

        terminal.println(cyan("📦 Fetching orders..."))

        val queryParams = buildList {
            userId?.let { add("userId=$it") }
            restaurantId?.let { add("restaurantId=$it") }
            status?.let { add("status=$it") }
        }
        val path = "/orders" + if (queryParams.isNotEmpty()) "?${queryParams.joinToString("&")}" else ""

        val result = HttpClientManager.get<JsonArray>(path, requireAuth = true)

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    val orders = kotlinx.serialization.json.Json.decodeFromJsonElement(
                        kotlinx.serialization.builtins.ListSerializer(Order.serializer()),
                        response.data
                    )

                    if (orders.isEmpty()) {
                        terminal.println(yellow("No orders found"))
                        return@fold
                    }

                    terminal.println(table {
                        header {
                            row("ID", "Restaurant", "Status", "Total", "Payment", "Created")
                        }
                        body {
                            orders.forEach { order ->
                                val statusColor = when (order.status) {
                                    "delivered" -> green
                                    "cancelled" -> red
                                    "preparing" -> yellow
                                    else -> brightWhite
                                }

                                row(
                                    order.id.toString(),
                                    gray("R#${order.restaurantId}"),
                                    statusColor(order.status),
                                    green("$${order.totalAmount}"),
                                    if (order.paymentStatus == "paid") green("✓") else red("✗"),
                                    gray(order.createdAt.take(10))
                                )
                            }
                        }
                    })

                    terminal.println(gray("\nTotal: ${orders.size} orders"))
                } else {
                    terminal.println(red("❌ Failed: ${response.message}"))
                }
            },
            onFailure = { error ->
                terminal.println(red("❌ Error: ${error.message}"))
            }
        )
    }
}

class OrderViewCommand : CliktCommand(
    name = "view",
    help = "View order details"
) {
    private val terminal = Terminal()
    private val id by argument("id", help = "Order ID").int()

    override fun run() = runBlocking {
        if (!HttpClientManager.isAuthenticated()) {
            terminal.println(red("❌ Authentication required"))
            return@runBlocking
        }

        terminal.println(cyan("📦 Fetching order details..."))

        val result = HttpClientManager.get<Order>("/orders/$id", requireAuth = true)

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    val order = response.data
                    terminal.println(brightWhite("\nOrder #${order.id}"))
                    terminal.println(gray("━".repeat(50)))
                    terminal.println("${brightCyan("User ID:")} ${order.userId}")
                    terminal.println("${brightCyan("Restaurant ID:")} ${order.restaurantId}")
                    terminal.println("${brightCyan("Status:")} ${yellow(order.status)}")
                    terminal.println("${brightCyan("Total Amount:")} ${green("$${order.totalAmount}")}")
                    terminal.println("${brightCyan("Delivery Address:")} ${order.deliveryAddress}")
                    terminal.println("${brightCyan("Payment Method:")} ${order.paymentMethod}")
                    terminal.println("${brightCyan("Payment Status:")} ${if (order.paymentStatus == "paid") green("Paid") else red("Unpaid")}")
                    order.notes?.let { terminal.println("${brightCyan("Notes:")} $it") }
                    terminal.println("${brightCyan("Created:")} ${gray(order.createdAt)}")
                } else {
                    terminal.println(red("❌ Order not found"))
                }
            },
            onFailure = { error ->
                terminal.println(red("❌ Error: ${error.message}"))
            }
        )
    }
}

class OrderCreateCommand : CliktCommand(
    name = "create",
    help = "Create a new order (interactive)"
) {
    private val terminal = Terminal()

    override fun run() = runBlocking {
        if (!HttpClientManager.isAuthenticated()) {
            terminal.println(red("❌ Authentication required"))
            return@runBlocking
        }

        terminal.println(brightWhite("\n🛒 Create New Order"))
        terminal.println(gray("━".repeat(50)))

        // Collect order details
        terminal.print(brightCyan("User ID: "))
        val userId = readlnOrNull()?.toIntOrNull() ?: run {
            terminal.println(red("Invalid user ID"))
            return@runBlocking
        }

        terminal.print(brightCyan("Restaurant ID: "))
        val restaurantId = readlnOrNull()?.toIntOrNull() ?: run {
            terminal.println(red("Invalid restaurant ID"))
            return@runBlocking
        }

        terminal.print(brightCyan("Delivery Address: "))
        val deliveryAddress = readlnOrNull() ?: run {
            terminal.println(red("Invalid address"))
            return@runBlocking
        }

        terminal.print(brightCyan("Payment Method (cash/card/mobile_money): "))
        val paymentMethod = readlnOrNull() ?: "cash"

        terminal.print(brightCyan("Special Notes (optional): "))
        val notes = readlnOrNull()?.takeIf { it.isNotBlank() }

        // Collect order items
        val items = mutableListOf<OrderItem>()
        terminal.println(yellow("\n🍕 Add pizzas to order:"))

        while (true) {
            terminal.print(brightWhite("\nPizza ID (or 'done' to finish): "))
            val input = readlnOrNull()
            if (input == "done" || input.isNullOrBlank()) break

            val pizzaId = input.toIntOrNull() ?: continue

            terminal.print(brightCyan("Quantity: "))
            val quantity = readlnOrNull()?.toIntOrNull() ?: 1

            terminal.print(brightCyan("Special instructions (optional): "))
            val instructions = readlnOrNull()?.takeIf { it.isNotBlank() }

            items.add(OrderItem(pizzaId, quantity, instructions))
            terminal.println(green("✓ Added pizza #$pizzaId x$quantity"))
        }

        if (items.isEmpty()) {
            terminal.println(red("❌ No items added. Order cancelled."))
            return@runBlocking
        }

        // Confirm order
        terminal.println(yellow("\n📋 Order Summary:"))
        terminal.println(gray("━".repeat(40)))
        terminal.println("Restaurant ID: $restaurantId")
        terminal.println("Delivery: $deliveryAddress")
        terminal.println("Payment: $paymentMethod")
        terminal.println("Items: ${items.size} pizza(s)")
        items.forEach {
            terminal.println("  • Pizza #${it.pizzaId} x${it.quantity}")
        }

        terminal.print(yellow("\nConfirm order? (y/N): "))
        val confirm = readlnOrNull()?.lowercase()

        if (confirm != "y" && confirm != "yes") {
            terminal.println(gray("Order cancelled"))
            return@runBlocking
        }

        terminal.println(cyan("\n📦 Creating order..."))

        val request = CreateOrderRequest(userId, restaurantId, deliveryAddress, paymentMethod, notes, items)
        val result = HttpClientManager.post<CreateOrderRequest, Order>(
            "/orders",
            request,
            requireAuth = true
        )

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    terminal.println(green("\n✅ Order created successfully!"))
                    terminal.println(brightWhite("Order ID: #${response.data.id}"))
                    terminal.println(gray("Total: $${response.data.totalAmount}"))
                    terminal.println(gray("Status: ${response.data.status}"))
                    terminal.println(yellow("\n💡 View order: pizza-cli order view ${response.data.id}"))
                } else {
                    terminal.println(red("❌ Failed: ${response.message}"))
                }
            },
            onFailure = { error ->
                terminal.println(red("❌ Error: ${error.message}"))
            }
        )
    }
}

class OrderUpdateCommand : CliktCommand(
    name = "update",
    help = "Update order status"
) {
    private val terminal = Terminal()
    private val id by argument("id", help = "Order ID").int()
    private val status by option("-s", "--status", help = "New status")
        .choice("pending", "confirmed", "preparing", "ready", "delivered", "cancelled")
        .prompt("New status")

    override fun run() = runBlocking {
        if (!HttpClientManager.isAuthenticated()) {
            terminal.println(red("❌ Authentication required"))
            return@runBlocking
        }

        terminal.println(cyan("📦 Updating order..."))

        val updateData = mapOf("status" to status)
        val result = HttpClientManager.put<Map<String, String>, Order>(
            "/orders/$id",
            updateData,
            requireAuth = true
        )

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    terminal.println(green("✅ Order updated successfully!"))
                    terminal.println(gray("New status: ${response.data.status}"))
                } else {
                    terminal.println(red("❌ Failed: ${response.message}"))
                }
            },
            onFailure = { error ->
                terminal.println(red("❌ Error: ${error.message}"))
            }
        )
    }
}


