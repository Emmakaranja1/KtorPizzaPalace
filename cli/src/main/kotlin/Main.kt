package com.emmascode

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal
import com.emmascode.auth.*
import com.emmascode.pizza.PizzaCommand
import com.emmascode.restaurant.RestaurantCommand
import com.emmascode.order.OrderCommand
import com.emmascode.utils.HttpClientManager

class PizzaCli : CliktCommand(
    name = "pizza-cli",
    help = """
        🍕 Pizza Palace CLI - Order Management System
        
        A command-line interface for managing pizzas, restaurants, and orders.
    """.trimIndent()
) {
    private val version by option("--version", "-v", help = "Show version").flag()
    private val terminal = Terminal()

    init {
        subcommands(
            // Auth commands
            LoginCommand(),
            RegisterCommand(),
            LogoutCommand(),
            WhoAmICommand(),

            // Entity commands
            PizzaCommand(),
            RestaurantCommand(),
            OrderCommand(),

            // Utility commands
            ConfigCommand()
        )
    }

    override fun run() {
        if (version) {
            terminal.println(brightWhite("🍕 Pizza Palace CLI"))
            terminal.println(gray("Version: 1.0.0"))
            terminal.println(gray("Backend: ${HttpClientManager.baseUrl}"))
            terminal.println(gray("Authenticated: ${if (HttpClientManager.isAuthenticated()) green("Yes") else red("No")}"))
            return
        }

        // Show welcome banner if no subcommand
        if (currentContext.invokedSubcommand == null) {
            showBanner()
        }
    }

    private fun showBanner() {
        terminal.println(yellow("""
            ╔═══════════════════════════════════════════════════╗
            ║                                                   ║
            ║          🍕  PIZZA PALACE CLI  🍕                ║
            ║                                                   ║
            ║              Order Management System              ║
            ║                                                   ║
            ╚═══════════════════════════════════════════════════╝
        """.trimIndent()))

        terminal.println(brightWhite("\nQuick Start:"))
        terminal.println(gray("━".repeat(50)))
        terminal.println("  ${brightCyan("pizza-cli register")}     - Create an account")
        terminal.println("  ${brightCyan("pizza-cli login")}        - Login to your account")
        terminal.println("  ${brightCyan("pizza-cli pizza list")}   - Browse pizzas")
        terminal.println("  ${brightCyan("pizza-cli order create")} - Place an order")

        terminal.println(brightWhite("\nCommands:"))
        terminal.println(gray("━".repeat(50)))
        terminal.println("  ${green("Auth:")}       login, register, logout, whoami")
        terminal.println("  ${green("Pizza:")}      list, view, create, delete")
        terminal.println("  ${green("Restaurant:")} list, view, create, menu, price")
        terminal.println("  ${green("Order:")}      list, view, create, update")

        terminal.println(brightWhite("\nConfiguration:"))
        terminal.println(gray("━".repeat(50)))
        terminal.println("  Base URL: ${HttpClientManager.baseUrl}")
        terminal.println("  Status:   ${if (HttpClientManager.isAuthenticated()) green("Authenticated ✓") else red("Not logged in ✗")}")

        terminal.println(yellow("\n💡 Run 'pizza-cli --help' for detailed help"))
        terminal.println(gray("   Run 'pizza-cli <command> --help' for command-specific help\n"))
    }
}

class ConfigCommand : CliktCommand(
    name = "config",
    help = "Configure CLI settings"
) {
    private val terminal = Terminal()
    private val url by option("--url", help = "Set backend URL")
    private val show by option("--show", help = "Show current configuration").flag()

    override fun run() {
        if (show) {
            terminal.println(brightWhite("📝 Current Configuration"))
            terminal.println(gray("━".repeat(50)))
            terminal.println("Base URL: ${HttpClientManager.baseUrl}")
            terminal.println("Token:    ${if (HttpClientManager.isAuthenticated()) "Set (${HttpClientManager.authToken?.take(20)}...)" else "Not set"}")
            terminal.println("Config:   ~/.pizza-cli-config")
            return
        }

        url?.let {
            HttpClientManager.baseUrl = it
            terminal.println(green("✅ Base URL updated to: $it"))
        } ?: run {
            terminal.println(yellow("Current base URL: ${HttpClientManager.baseUrl}"))
            terminal.println(gray("Use --url to change it"))
        }
    }
}

fun main(args: Array<String>) {
    try {
        PizzaCli().main(args)
    } catch (e: Exception) {
        val terminal = Terminal()
        terminal.println(red("❌ Error: ${e.message}"))
        if (System.getenv("DEBUG") == "true") {
            e.printStackTrace()
        }
    } finally {
        HttpClientManager.close()
    }
}
