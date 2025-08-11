#!/bin/bash

echo "🎬 ExpenseFlow - Demo Setup"
echo "=========================="

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
PURPLE='\033[0;35m'
NC='\033[0m'

echo -e "${PURPLE}🚀 Starting ExpenseFlow Demo...${NC}"
echo ""

# Check if application is running
if curl -s http://localhost:8081 > /dev/null; then
    echo -e "${GREEN}✅ Application is already running on port 8081${NC}"
elif curl -s http://localhost:8080 > /dev/null; then
    echo -e "${GREEN}✅ Application is already running on port 8080${NC}"
    PORT=8080
else
    echo -e "${YELLOW}🔄 Starting application...${NC}"
    echo "This may take a few moments..."
    
    # Start application in background
    nohup java -jar target/expense-tracker-0.0.1-SNAPSHOT.jar --server.port=8081 > app.log 2>&1 &
    APP_PID=$!
    
    # Wait for application to start
    echo "Waiting for application to start..."
    for i in {1..30}; do
        if curl -s http://localhost:8081 > /dev/null; then
            echo -e "${GREEN}✅ Application started successfully!${NC}"
            PORT=8081
            break
        fi
        echo -n "."
        sleep 2
    done
    
    if [ -z "$PORT" ]; then
        echo -e "${RED}❌ Failed to start application. Check app.log for details.${NC}"
        exit 1
    fi
fi

# Set the correct port
PORT=${PORT:-8081}
BASE_URL="http://localhost:$PORT"

echo ""
echo -e "${BLUE}📊 Adding demo data for beautiful screenshots...${NC}"
./add-demo-data.sh

echo ""
echo -e "${PURPLE}🎯 Demo is ready!${NC}"
echo ""
echo -e "${GREEN}🌐 Open your browser to:${NC}"
echo "   $BASE_URL"
echo ""
echo -e "${YELLOW}📸 Perfect for taking screenshots:${NC}"
echo ""
echo "1. 🏠 Dashboard View:"
echo "   - Beautiful gradient background with floating shapes"
echo "   - 4 colorful stats cards with real data"
echo "   - Modern glass navigation bar"
echo ""
echo "2. ➕ Add Expense Form:"
echo "   - Smooth animations and modern design"
echo "   - Payment method selection with icons"
echo "   - Category dropdown with emojis"
echo ""
echo "3. 📋 Expense Views:"
echo "   - Card view with beautiful expense cards"
echo "   - Table view with professional styling"
echo "   - Toggle between views smoothly"
echo ""
echo "4. 🔍 Filters & Features:"
echo "   - Click 'Filters' to see animated filter panel"
echo "   - Try the search and category filters"
echo "   - Click 'Dashboard' for summary modal"
echo ""
echo "5. 📱 Mobile Testing:"
echo "   - Resize browser window to see responsive design"
echo "   - Test on mobile device for touch interactions"
echo ""
echo -e "${BLUE}💡 Screenshot Tips:${NC}"
echo "- Use full browser window for desktop screenshots"
echo "- Resize to mobile dimensions for mobile screenshots"
echo "- Try both light and dark themes if available"
echo "- Capture the beautiful animations in action"
echo "- Show the floating action button (+ button)"
echo ""
echo -e "${GREEN}🎉 Enjoy your beautiful ExpenseFlow demo!${NC}"
