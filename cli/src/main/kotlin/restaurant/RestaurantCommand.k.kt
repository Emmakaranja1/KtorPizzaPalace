package com.emmascode.restaurant

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import com.emmascode.models.*
import com.emmascode.utils.HttpClientManager
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonArray

class RestaurantCommand : CliktCommand(
    name = "restaurant",
    help = "Manage restaurants"
) {
    init {
        subcommands(
            RestaurantListCommand(),
            RestaurantViewCommand(),
            RestaurantCreateCommand(),
            RestaurantMenuCommand(),
            RestaurantPriceCommand()
        )
    }

    override fun run() = Unit
}

class RestaurantListCommand : CliktCommand(
    name = "list",
    help = "List all restaurants"
) {
    private val terminal = Terminal()
    private val active by option("--active", help = "Show only active restaurants")

    override fun run() = runBlocking {
        terminal.println(cyan("🏪 Fetching restaurants..."))

        val path = if (active != null) "/restaurants?active=true" else "/restaurants"
        val result = HttpClientManager.get<JsonArray>(path, requireAuth = false)

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    val restaurants = kotlinx.serialization.json.Json.decodeFromJsonElement(
                        kotlinx.serialization.builtins.ListSerializer(Restaurant.serializer()),
                        response.data
                    )

                    if (restaurants.isEmpty()) {
                        terminal.println(yellow("No restaurants found"))
                        return@fold
                    }

                    terminal.println(table {
                        header {
                            row("ID", "Name", "Address", "Rating", "Status")
                        }
                        body {
                            restaurants.forEach { restaurant ->
                                row(
                                    restaurant.id.toString(),
                                    brightWhite(restaurant.name),
                                    gray(restaurant.address.take(40)),
                                    yellow("⭐ ${restaurant.rating}"),
                                    if (restaurant.isActive) green("Active") else red("Inactive")
                                )
                            }
                        }
                    })

                    terminal.println(gray("\nTotal: ${restaurants.size} restaurants"))
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

class RestaurantViewCommand : CliktCommand(
    name = "view",
    help = "View restaurant details"
) {
    private val terminal = Terminal()
    private val id by argument("id", help = "Restaurant ID").int()

    override fun run() = runBlocking {
        terminal.println(cyan("🏪 Fetching restaurant details..."))

        val result = HttpClientManager.get<Restaurant>("/restaurants/$id", requireAuth = false)

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    val restaurant = response.data
                    terminal.println(brightWhite("\n${restaurant.name}"))
                    terminal.println(gray("━".repeat(50)))
                    terminal.println("${brightCyan("ID:")} ${restaurant.id}")
                    terminal.println("${brightCyan("Address:")} ${restaurant.address}")
                    terminal.println("${brightCyan("Phone:")} ${restaurant.phone ?: "N/A"}")
                    terminal.println("${brightCyan("Email:")} ${restaurant.email ?: "N/A"}")
                    terminal.println("${brightCyan("Rating:")} ${yellow("⭐ ${restaurant.rating}")}")
                    terminal.println("${brightCyan("Status:")} ${if (restaurant.isActive) green("Active") else red("Inactive")}")
                    terminal.println("${brightCyan("Owner ID:")} ${restaurant.ownerId}")
                    terminal.println("${brightCyan("Created:")} ${gray(restaurant.createdAt)}")
                } else {
                    terminal.println(red("❌ Restaurant not found"))
                }
            },
            onFailure = { error ->
                terminal.println(red("❌ Error: ${error.message}"))
            }
        )
    }
}

class RestaurantCreateCommand : CliktCommand(
    name = "create",
    help = "Create a new restaurant"
) {
    private val terminal = Terminal()
    private val name by option("-n", "--name", help = "Restaurant name").prompt("Restaurant name")
    private val address by option("-a", "--address", help = "Address").prompt("Address")
    private val phone by option("-p", "--phone", help = "Phone number")
    private val email by option("-e", "--email", help = "Email address")
    private val ownerId by option("-o", "--owner", help = "Owner user ID").int().prompt("Owner ID")
    private val imageUrl by option("-i", "--image", help = "Image URL")

    override fun run() = runBlocking {
        if (!HttpClientManager.isAuthenticated()) {
            terminal.println(red("❌ Authentication required"))
            return@runBlocking
        }

        terminal.println(cyan("🏪 Creating restaurant..."))

        val request = CreateRestaurantRequest(name, address, phone, email, ownerId, imageUrl)
        val result = HttpClientManager.post<CreateRestaurantRequest, Restaurant>(
            "/restaurants",
            request,
            requireAuth = true
        )

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    terminal.println(green("✅ Restaurant created successfully!"))
                    terminal.println(brightWhite("Name: ${response.data.name}"))
                    terminal.println(gray("ID: ${response.data.id}"))
                    terminal.println(gray("Address: ${response.data.address}"))
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

class RestaurantMenuCommand : CliktCommand(
    name = "menu",
    help = "View restaurant menu (pizzas)"
) {
    private val terminal = Terminal()
    private val id by argument("id", help = "Restaurant ID").int()

    override fun run() = runBlocking {
        terminal.println(cyan("🍕 Fetching restaurant menu..."))

        val result = HttpClientManager.get<JsonArray>(
            "/restaurant-pizzas?restaurantId=$id",
            requireAuth = false
        )

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    terminal.println(green("✅ Menu loaded"))
                    terminal.println(yellow("\n💡 Note: Pizzas not listed here use base price (fallback pricing)"))
                    terminal.println(gray("All pizzas from catalog are available at this restaurant\n"))
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

class RestaurantPriceCommand : CliktCommand(
    name = "price",
    help = "Check pizza price at restaurant (shows fallback)"
) {
    private val terminal = Terminal()
    private val restaurantId by option("-r", "--restaurant", help = "Restaurant ID").int().prompt("Restaurant ID")
    private val pizzaId by option("-p", "--pizza", help = "Pizza ID").int().prompt("Pizza ID")

    override fun run() = runBlocking {
        terminal.println(cyan("💰 Checking price..."))

        val result = HttpClientManager.get<PizzaPrice>(
            "/restaurants/$restaurantId/pizzas/$pizzaId/price",
            requireAuth = false
        )

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    val price = response.data
                    terminal.println(green("\n✅ Price Information"))
                    terminal.println(gray("━".repeat(40)))
                    terminal.println("${brightCyan("Price:")} ${green("$${price.price}")}")
                    terminal.println("${brightCyan("Available:")} ${if (price.isAvailable) green("Yes") else red("No")}")
                    terminal.println("${brightCyan("Pricing Type:")} ${if (price.isCustomPrice) yellow("Custom") else brightWhite("Base Price (Fallback)")}")

                    if (!price.isCustomPrice) {
                        terminal.println(yellow("\n💡 This restaurant uses the base catalog price"))
                        terminal.println(gray("   Restaurant owner hasn't set custom pricing"))
                    }
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

