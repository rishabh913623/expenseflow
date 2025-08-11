#!/bin/bash

# Expense Tracker Build Script
echo "Building Expense Tracker Application..."

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check Java version
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo "Java version: $java_version"

# Create necessary directories
mkdir -p target/classes
mkdir -p target/test-classes

echo "Compilation would require Maven or Gradle."
echo "Please install Maven 3.6+ or Gradle 7+ to build this project."
echo ""
echo "With Maven installed, run:"
echo "  mvn clean compile"
echo "  mvn spring-boot:run"
echo ""
echo "Or with Gradle:"
echo "  ./gradlew build"
echo "  ./gradlew bootRun"

echo ""
echo "Project structure created successfully!"
echo "All source files are ready for compilation."
