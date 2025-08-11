#!/bin/bash

echo "🎨 Starting ExpenseFlow - Modern Expense Tracker"
echo "================================================="

# Check if JAR file exists
if [ ! -f "target/expense-tracker-0.0.1-SNAPSHOT.jar" ]; then
    echo "📦 JAR file not found. Building application..."
    mvn clean package -DskipTests
    if [ $? -ne 0 ]; then
        echo "❌ Build failed. Please check the error messages above."
        exit 1
    fi
fi

# Check if port 8080 is in use
if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null ; then
    echo "⚠️  Port 8080 is already in use. Using port 8081 instead."
    PORT=8081
    URL="http://localhost:8081"
else
    PORT=8080
    URL="http://localhost:8080"
fi

echo ""
echo "🚀 Starting ExpenseFlow with beautiful modern UI..."
echo "📍 Application will be available at: $URL"
echo "⏹️  Press Ctrl+C to stop the application"
echo ""
echo "✨ Features you'll see:"
echo "   • Beautiful gradient backgrounds with floating animations"
echo "   • Modern glass navigation bar"
echo "   • Interactive dashboard with stats cards"
echo "   • Dual view system (Cards & Table)"
echo "   • Smart filtering with smooth animations"
echo "   • Dark/Light theme toggle"
echo "   • Mobile-responsive design"
echo ""
echo "🔄 Starting Spring Boot on port $PORT..."
echo ""

# Run the application
exec java -jar target/expense-tracker-0.0.1-SNAPSHOT.jar --server.port=$PORT
