# Pizza Palace CLI 🍕

A powerful command-line interface for managing the Pizza Palace ordering system. Order pizzas, manage restaurants, and track orders - all from your terminal!

## Features

- 🔐 **Authentication** - Login, register, and manage sessions
- 🍕 **Pizza Management** - Browse, create, and manage pizzas
- 🏪 **Restaurant Management** - View restaurants and check pricing
- 📦 **Order Management** - Create and track orders
- 💰 **Smart Pricing** - View base prices and restaurant-specific pricing
- 🎨 **Beautiful CLI** - Colorful, table-formatted output
- ⚡ **Fast & Efficient** - Built with Kotlin and Ktor Client

## Installation

### Prerequisites

- JDK 17 or higher
- Gradle 8.0+
- Running Pizza Palace Backend (default: `http://localhost:8080`)

### Build from Source

```bash
cd cli

# Build the project
./gradlew build

# Create executable JAR
./gradlew jar

# Run CLI
./gradlew run --args="--help"
```

### Create Executable Script

```bash
# Create pizza-cli executable
cat > pizza-cli << 'EOF'
#!/bin/bash
java -jar "$(dirname "$0")/build/libs/cli.jar" "$@"
EOF

chmod +x pizza-cli

# Add to PATH (optional)
sudo ln -s $(pwd)/pizza-cli /usr/local/bin/pizza-cli
```

## Quick Start

### 1. Register an Account

```bash
pizza-cli register
# Follow the prompts:
# - Username
# - Email
# - Password
# - Role (customer/restaurant_owner/admin)
```

### 2. Login

```bash
pizza-cli login
# Enter your email and password
```

### 3. Browse Pizzas

```bash
pizza-cli pizza list
```

### 4. Create an Order

```bash
pizza-cli order create
# Interactive order creation
```

## Commands Reference

### Authentication Commands

#### Register
```bash
pizza-cli register
pizza-cli register -u john -e john@example.com -p secret123 -r customer
```

#### Login
```bash
pizza-cli login
pizza-cli login -e john@example.com -p secret123
```

#### Logout
```bash
pizza-cli logout
```

#### Check Authentication Status
```bash
pizza-cli whoami
```

### Pizza Commands

#### List All Pizzas
```bash
pizza-cli pizza list
pizza-cli pizza list -c specialty  # Filter by category
```

#### View Pizza Details
```bash
pizza-cli pizza view 1
```

#### Create Pizza (Requires Auth)
```bash
pizza-cli pizza create
pizza-cli pizza create -n "Margherita" -p "12.99" -c classic
```

#### Delete Pizza (Requires Auth)
```bash
pizza-cli pizza delete 1
```

### Restaurant Commands

#### List Restaurants
```bash
pizza-cli restaurant list
pizza-cli restaurant list --active  # Show only active restaurants
```

#### View Restaurant Details
```bash
pizza-cli restaurant view 1
```

#### Create Restaurant (Requires Auth)
```bash
pizza-cli restaurant create
pizza-cli restaurant create -n "Pizza Palace" -a "123 Main St" -o 2
```

#### View Restaurant Menu
```bash
pizza-cli restaurant menu 1
```

#### Check Pizza Price at Restaurant
```bash
# Shows if using custom price or base price fallback
pizza-cli restaurant price -r 1 -p 5
```

### Order Commands

#### List Orders (Requires Auth)
```bash
pizza-cli order list
pizza-cli order list -u 2          # Filter by user
pizza-cli order list -r 1          # Filter by restaurant
pizza-cli order list -s pending    # Filter by status
```

#### View Order Details (Requires Auth)
```bash
pizza-cli order view 1
```

#### Create Order (Requires Auth)
```bash
pizza-cli order create
# Interactive prompts:
# - User ID
# - Restaurant ID
# - Delivery address
# - Payment method
# - Add pizzas (ID, quantity, instructions)
```

#### Update Order Status (Requires Auth)
```bash
pizza-cli order update 1 -s preparing
```

Available statuses: `pending`, `confirmed`, `preparing`, `ready`, `delivered`, `cancelled`

### Configuration Commands

#### Show Configuration
```bash
pizza-cli config --show
```

#### Set Backend URL
```bash
pizza-cli config --url http://api.pizzapalace.com
```

#### Show Version
```bash
pizza-cli --version
```

## Examples

### Complete Ordering Workflow

```bash
# 1. Register
pizza-cli register -u alice -e alice@example.com -p pass123 -r customer

# 2. Login
pizza-cli login -e alice@example.com -p pass123

# 3. Browse pizzas
pizza-cli pizza list

# 4. Check restaurant menu and prices
pizza-cli restaurant list
pizza-cli restaurant price -r 1 -p 3

# 5. Create order
pizza-cli order create
# Add pizzas interactively

# 6. View your orders
pizza-cli order list -u 1

# 7. Check order status
pizza-cli order view 1
```

### Restaurant Owner Workflow

```bash
# 1. Register as restaurant owner
pizza-cli register -r restaurant_owner

# 2. Login
pizza-cli login

# 3. Create restaurant
pizza-cli restaurant create

# 4. View orders for your restaurant
pizza-cli order list -r 1

# 5. Update order status
pizza-cli order update 1 -s preparing
pizza-cli order update 1 -s ready
pizza-cli order update 1 -s delivered
```

### Admin Workflow

```bash
# 1. Login as admin
pizza-cli login

# 2. Create new pizza
pizza-cli pizza create -n "Supreme" -p "19.99" -c specialty

# 3. View all orders
pizza-cli order list

# 4. Manage restaurants
pizza-cli restaurant list
```

## Configuration

### Config File Location

The CLI stores configuration in: `~/.pizza-cli-config`

Contains:
- Backend URL
- Authentication token (JWT)

### Environment Variables

```bash
# Enable debug logging
export DEBUG=true

# Set default backend URL
export PIZZA_API_URL=http://localhost:8080
```

## Output Examples

### Pizza List
```
┌────┬─────────────────┬───────────┬─────────────┬──────────────────────────────┐
│ ID │ Name            │ Category  │ Base Price  │ Description                  │
├────┼─────────────────┼───────────┼─────────────┼──────────────────────────────┤
│ 1  │ Margherita      │ classic   │ $12.99      │ Classic pizza with mozzar... │
│ 2  │ Pepperoni       │ classic   │ $14.99      │ Traditional pepperoni wit... │
│ 3  │ Hawaiian        │ specialty │ $15.99      │ Ham, pineapple, and mozza... │
└────┴─────────────────┴───────────┴─────────────┴──────────────────────────────┘
```

### Order View
```
Order #1
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
User ID: 2
Restaurant ID: 1
Status: delivered
Total Amount: $43.97
Delivery Address: 100 Valley Road, Westlands, Nairobi
Payment Method: mobile_money
Payment Status: Paid
Created: 2025-01-15T10:30:00
```

### Restaurant Price Check
```
✅ Price Information
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Price: $15.99
Available: Yes
Pricing Type: Custom

💡 This restaurant uses the base catalog price
   Restaurant owner hasn't set custom pricing
```

## Troubleshooting

### Connection Refused
```bash
# Check if backend is running
curl http://localhost:8080

# Update CLI backend URL
pizza-cli config --url http://your-backend-url:8080
```

### Authentication Failed
```bash
# Clear stored token and login again
pizza-cli logout
pizza-cli login
```

### Command Not Found
```bash
# Make sure CLI is in PATH
which pizza-cli

# Or use full path
./pizza-cli --help
```

## Development

### Project Structure

```
cli/
├── build.gradle.kts
└── src/
    └── main/kotlin/
        ├── cli/
        │   ├── Main.kt              # Entry point
        │   ├── auth/
        │   │   └── LoginCommand.kt  # Auth commands
        │   ├── pizza/
        │   │   └── PizzaCommand.kt  # Pizza commands
        │   ├── restaurant/
        │   │   └── RestaurantCommand.kt
        │   ├── order/
        │   │   └── OrderCommand.kt
        │   ├── models/
        │   │   └── Models.kt        # Data models
        │   └── utils/
        │       └── HttpClient.kt    # HTTP client
```

### Running Tests

```bash
./gradlew test
```

### Building Distribution

```bash
# Create distribution ZIP
./gradlew distZip

# Output: build/distributions/pizza-cli-1.0.0.zip
```

## Technologies Used

- **Kotlin** - Programming language
- **Ktor Client** - HTTP client for API calls
- **Clikt** - Command-line interface framework
- **Mordant** - Terminal styling and tables
- **Kotlinx Serialization** - JSON serialization

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## License

MIT License

## Support

For issues and questions:
- 📧 Email: support@pizzapalace.com
- 🐛 GitHub Issues: [Report a bug](https://github.com/yourusername/pizza-cli/issues)
- 📖 Documentation: [Full Docs](https://docs.pizzapalace.com)

## Changelog

### v1.0.0 (2025-01-15)
- ✨ Initial release
- 🔐 Authentication system
- 🍕 Pizza management
- 🏪 Restaurant management
- 📦 Order management
- 💰 Smart pricing with fallback support
- 🎨 Beautiful CLI output

---

Made with ❤️ by Emma