package com.emmascode.pizza

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

class PizzaCommand : CliktCommand(
    name = "pizza",
    help = "Manage pizzas"
) {
    init {
        subcommands(
            PizzaListCommand(),
            PizzaViewCommand(),
            PizzaCreateCommand(),
            PizzaDeleteCommand()
        )
    }

    override fun run() = Unit
}

class PizzaListCommand : CliktCommand(
    name = "list",
    help = "List all pizzas"
) {
    private val terminal = Terminal()
    private val category by option("-c", "--category", help = "Filter by category")
        .choice("classic", "specialty", "vegan")

    override fun run() = runBlocking {
        terminal.println(cyan("🍕 Fetching pizzas..."))

        val path = if (category != null) "/pizzas?category=$category" else "/pizzas"
        val result = HttpClientManager.get<JsonArray>(path, requireAuth = false)

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    val pizzas = kotlinx.serialization.json.Json.decodeFromJsonElement(
                        kotlinx.serialization.builtins.ListSerializer(Pizza.serializer()),
                        response.data
                    )

                    if (pizzas.isEmpty()) {
                        terminal.println(yellow("No pizzas found"))
                        return@fold
                    }

                    terminal.println(table {
                        header {
                            row("ID", "Name", "Category", "Base Price", "Description")
                        }
                        body {
                            pizzas.forEach { pizza ->
                                row(
                                    pizza.id.toString(),
                                    brightWhite(pizza.name),
                                    yellow(pizza.category),
                                    green("$${pizza.basePrice}"),
                                    gray(pizza.description?.take(40) ?: "")
                                )
                            }
                        }
                    })

                    terminal.println(gray("\nTotal: ${pizzas.size} pizzas"))
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

class PizzaViewCommand : CliktCommand(
    name = "view",
    help = "View pizza details"
) {
    private val terminal = Terminal()
    private val id by argument("id", help = "Pizza ID").int()

    override fun run() = runBlocking {
        terminal.println(cyan("🍕 Fetching pizza details..."))

        val result = HttpClientManager.get<Pizza>("/pizzas/$id", requireAuth = false)

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    val pizza = response.data
                    terminal.println(brightWhite("\n${pizza.name}"))
                    terminal.println(gray("━".repeat(50)))
                    terminal.println("${brightCyan("ID:")} ${pizza.id}")
                    terminal.println("${brightCyan("Category:")} ${yellow(pizza.category)}")
                    terminal.println("${brightCyan("Base Price:")} ${green("$${pizza.basePrice}")}")
                    terminal.println("${brightCyan("Description:")} ${pizza.description ?: "N/A"}")
                    pizza.imageUrl?.let { terminal.println("${brightCyan("Image:")} $it") }
                    terminal.println("${brightCyan("Created:")} ${gray(pizza.createdAt)}")
                } else {
                    terminal.println(red("❌ Pizza not found"))
                }
            },
            onFailure = { error ->
                terminal.println(red("❌ Error: ${error.message}"))
            }
        )
    }
}

class PizzaCreateCommand : CliktCommand(
    name = "create",
    help = "Create a new pizza"
) {
    private val terminal = Terminal()
    private val name by option("-n", "--name", help = "Pizza name").prompt("Pizza name")
    private val description by option("-d", "--description", help = "Description")
    private val basePrice by option("-p", "--price", help = "Base price").prompt("Base price")
    private val category by option("-c", "--category", help = "Category")
        .choice("classic", "specialty", "vegan")
        .prompt("Category", default = "classic")
    private val imageUrl by option("-i", "--image", help = "Image URL")

    override fun run() = runBlocking {
        if (!HttpClientManager.isAuthenticated()) {
            terminal.println(red("❌ Authentication required"))
            terminal.println(gray("Login with: pizza-cli login"))
            return@runBlocking
        }

        terminal.println(cyan("🍕 Creating pizza..."))

        val request = CreatePizzaRequest(name, description, imageUrl, basePrice, category)
        val result = HttpClientManager.post<CreatePizzaRequest, Pizza>(
            "/pizzas",
            request,
            requireAuth = true
        )

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    terminal.println(green("✅ Pizza created successfully!"))
                    terminal.println(brightWhite("Name: ${response.data.name}"))
                    terminal.println(gray("ID: ${response.data.id}"))
                    terminal.println(gray("Price: $${response.data.basePrice}"))
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

class PizzaDeleteCommand : CliktCommand(
    name = "delete",
    help = "Delete a pizza"
) {
    private val terminal = Terminal()
    private val id by argument("id", help = "Pizza ID").int()

    override fun run() = runBlocking {
        if (!HttpClientManager.isAuthenticated()) {
            terminal.println(red("❌ Authentication required"))
            return@runBlocking
        }

        terminal.print(yellow("⚠️  Are you sure you want to delete pizza #$id? (y/N): "))
        val confirm = readlnOrNull()?.lowercase()

        if (confirm != "y" && confirm != "yes") {
            terminal.println(gray("Cancelled"))
            return@runBlocking
        }

        terminal.println(cyan("🗑️  Deleting pizza..."))

        val result = HttpClientManager.delete("/pizzas/$id", requireAuth = true)

        result.fold(
            onSuccess = { response ->
                if (response.success) {
                    terminal.println(green("✅ Pizza deleted successfully!"))
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


