package com.emmascode.auth

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.terminal.Terminal
import com.emmascode.models.*
import com.emmascode.utils.HttpClientManager
import kotlinx.coroutines.runBlocking

class LoginCommand : CliktCommand(
    name = "login",
    help = "Login to your account"
) {
    private val terminal = Terminal()
    private val email by option("-e", "--email", help = "Email address").prompt("Email")
    private val password by option("-p", "--password", help = "Password").prompt("Password", hideInput = true)

    override fun run() = runBlocking {
        terminal.println(cyan("🔐 Logging in..."))

        val request = LoginRequest(email, password)
        val result = HttpClientManager.post<LoginRequest, LoginResponse>(
            "/auth/login",
            request,
            requireAuth = false
        )

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    HttpClientManager.authToken = response.data.token
                    terminal.println(green("✅ Login successful!"))
                    terminal.println(brightWhite("Welcome back, ${response.data.user.username}!"))
                    terminal.println(gray("Role: ${response.data.user.role}"))
                } else {
                    terminal.println(red("❌ Login failed: ${response.message}"))
                }
            },
            onFailure = { error ->
                terminal.println(red("❌ Error: ${error.message}"))
            }
        )
    }
}

class RegisterCommand : CliktCommand(
    name = "register",
    help = "Create a new account"
) {
    private val terminal = Terminal()
    private val username by option("-u", "--username", help = "Username").prompt("Username")
    private val email by option("-e", "--email", help = "Email address").prompt("Email")
    private val password by option("-p", "--password", help = "Password").prompt("Password", hideInput = true)
    private val role by option("-r", "--role", help = "User role")
        .choice("customer", "restaurant_owner", "admin")
        .required()

    override fun run() = runBlocking {
        terminal.println(cyan("📝 Creating account..."))

        val request = RegisterRequest(username, email, password, role)
        val result = HttpClientManager.post<RegisterRequest, User>(
            "/auth/register",
            request,
            requireAuth = false
        )

        result.fold(
            onSuccess = { response ->
                if (response.success && response.data != null) {
                    terminal.println(green("✅ Account created successfully!"))
                    terminal.println(brightWhite("Username: ${response.data.username}"))
                    terminal.println(gray("Email: ${response.data.email}"))
                    terminal.println(gray("Role: ${response.data.role}"))
                    terminal.println(yellow("\n💡 Now login with: pizza-cli login"))
                } else {
                    terminal.println(red("❌ Registration failed: ${response.message}"))
                }
            },
            onFailure = { error ->
                terminal.println(red("❌ Error: ${error.message}"))
            }
        )
    }
}

class LogoutCommand : CliktCommand(
    name = "logout",
    help = "Logout from your account"
) {
    private val terminal = Terminal()

    override fun run() {
        HttpClientManager.authToken = null
        terminal.println(green("✅ Logged out successfully!"))
        terminal.println(gray("Your session has been cleared."))
    }
}

class WhoAmICommand : CliktCommand(
    name = "whoami",
    help = "Show current user information"
) {
    private val terminal = Terminal()

    override fun run() {
        if (!HttpClientManager.isAuthenticated()) {
            terminal.println(yellow("⚠️  Not logged in"))
            terminal.println(gray("Login with: pizza-cli login"))
            return
        }

        terminal.println(green("✅ Authenticated"))
        terminal.println(gray("Token: ${HttpClientManager.authToken?.take(20)}..."))
        terminal.println(gray("Base URL: ${HttpClientManager.baseUrl}"))
    }
}
