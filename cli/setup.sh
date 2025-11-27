#!/bin/bash
# Pizza Palace CLI - Setup Script

set -e

BLUE='\033[0;34m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}╔═══════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                                           ║${NC}"
echo -e "${BLUE}║     🍕  Pizza Palace CLI Setup  🍕       ║${NC}"
echo -e "${BLUE}║                                           ║${NC}"
echo -e "${BLUE}╚═══════════════════════════════════════════╝${NC}"
echo ""

# Check prerequisites
echo -e "${YELLOW}Checking prerequisites...${NC}"

# Check Java
if ! command -v java &> /dev/null; then
    echo -e "${RED}❌ Java not found${NC}"
    echo -e "${YELLOW}Please install JDK 17 or higher${NC}"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo -e "${RED}❌ Java version too old: $JAVA_VERSION${NC}"
    echo -e "${YELLOW}Please install JDK 17 or higher${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Java $JAVA_VERSION${NC}"

# Check Gradle
if ! command -v gradle &> /dev/null && [ ! -f "./gradlew" ]; then
    echo -e "${RED}❌ Gradle not found${NC}"
    echo -e "${YELLOW}Please install Gradle or use included gradle wrapper${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Gradle${NC}"

# Build project
echo ""
echo -e "${BLUE}Building project...${NC}"

if [ -f "./gradlew" ]; then
    ./gradlew clean build
else
    gradle clean build
fi

echo -e "${GREEN}✅ Build successful${NC}"

# Create executable script
echo ""
echo -e "${BLUE}Creating executable...${NC}"

# Find the JAR file
JAR_PATH=$(find build/libs -name "*.jar" | head -n 1)

if [ -z "$JAR_PATH" ]; then
    echo -e "${RED}❌ JAR file not found${NC}"
    exit 1
fi

# Create wrapper script
cat > pizza-cli << EOF
#!/bin/bash
# Pizza Palace CLI Wrapper Script

SCRIPT_DIR="\$(cd "\$(dirname "\${BASH_SOURCE[0]}")" && pwd)"
JAR_PATH="\$SCRIPT_DIR/$JAR_PATH"

if [ ! -f "\$JAR_PATH" ]; then
    echo "Error: JAR file not found at \$JAR_PATH"
    echo "Please run ./setup.sh first"
    exit 1
fi

java -jar "\$JAR_PATH" "\$@"
EOF

chmod +x pizza-cli

echo -e "${GREEN}✅ Executable created: ./pizza-cli${NC}"

# Test CLI
echo ""
echo -e "${BLUE}Testing CLI...${NC}"

if ./pizza-cli --version &> /dev/null; then
    echo -e "${GREEN}✅ CLI working correctly${NC}"
else
    echo -e "${RED}❌ CLI test failed${NC}"
    exit 1
fi

# Ask to install globally
echo ""
echo -e "${YELLOW}Would you like to install pizza-cli globally? (requires sudo)${NC}"
read -p "Install to /usr/local/bin? (y/N): " install_global

if [ "$install_global" = "y" ] || [ "$install_global" = "Y" ]; then
    sudo cp pizza-cli /usr/local/bin/
    sudo chmod +x /usr/local/bin/pizza-cli
    echo -e "${GREEN}✅ Installed to /usr/local/bin/pizza-cli${NC}"
    echo -e "${YELLOW}You can now run 'pizza-cli' from anywhere${NC}"
else
    echo -e "${YELLOW}Skipped global installation${NC}"
    echo -e "${YELLOW}Run CLI with: ./pizza-cli${NC}"
fi

# Configuration
echo ""
echo -e "${BLUE}Configuration:${NC}"
read -p "Backend URL (default: http://localhost:8080): " backend_url
backend_url=${backend_url:-http://localhost:8080}

./pizza-cli config --url "$backend_url"

echo ""
echo -e "${GREEN}╔═══════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║                                           ║${NC}"
echo -e "${GREEN}║          ✅  Setup Complete!  ✅          ║${NC}"
echo -e "${GREEN}║                                           ║${NC}"
echo -e "${GREEN}╚═══════════════════════════════════════════╝${NC}"
echo ""
echo -e "${YELLOW}Quick Start:${NC}"
echo -e "  ${BLUE}./pizza-cli register${NC}     - Create account"
echo -e "  ${BLUE}./pizza-cli login${NC}        - Login"
echo -e "  ${BLUE}./pizza-cli pizza list${NC}   - Browse pizzas"
echo -e "  ${BLUE}./pizza-cli --help${NC}       - Show all commands"
echo ""
echo -e "${YELLOW}Configuration:${NC}"
echo -e "  Backend: $backend_url"
echo -e "  Config file: ~/.pizza-cli-config"
echo ""
echo -e "${GREEN}Happy ordering! 🍕${NC}"