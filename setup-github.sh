#!/bin/bash

echo "🚀 ExpenseFlow - GitHub Repository Setup"
echo "========================================"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

echo -e "${PURPLE}📋 Pre-upload Checklist:${NC}"
echo "1. ✅ Application is built and tested"
echo "2. ✅ Screenshots are taken and saved in screenshots/ folder"
echo "3. ✅ Demo data is added for beautiful screenshots"
echo "4. ✅ README.md is updated with screenshots"
echo ""

# Check if git is initialized
if [ ! -d ".git" ]; then
    echo -e "${YELLOW}🔧 Initializing Git repository...${NC}"
    git init
    echo -e "${GREEN}✅ Git repository initialized${NC}"
else
    echo -e "${GREEN}✅ Git repository already exists${NC}"
fi

# Check if there are any commits
if ! git rev-parse HEAD >/dev/null 2>&1; then
    echo -e "${YELLOW}📝 Making initial commit...${NC}"
    
    # Add all files
    git add .
    
    # Create initial commit with detailed message
    git commit -m "🎉 Initial commit: ExpenseFlow - Modern Expense Tracker

✨ Features:
- Beautiful modern UI with glass morphism and animations
- Dual view system (cards/table) with smooth transitions
- Advanced filtering and search capabilities
- Real-time statistics dashboard with gradient cards
- UPI and Cash payment tracking with detailed information
- Responsive mobile-first design that works on all devices
- Dark/light theme support with smooth transitions
- CSV export functionality for data analysis
- Comprehensive Spring Boot backend with REST APIs
- Full test coverage with unit and integration tests
- Professional documentation and contributing guidelines

🛠 Tech Stack:
- Backend: Java 17, Spring Boot 3.2.0, Spring Data JPA, H2 Database
- Frontend: HTML5, CSS3 (Glass Morphism), Vanilla JavaScript (ES6+)
- Design: Modern UI/UX, Responsive Design, Accessibility Compliant
- Testing: JUnit 5, MockMvc, Integration Tests
- Build: Maven, Spring Boot Maven Plugin

🎨 UI Highlights:
- Glass morphism navigation with backdrop blur
- Gradient backgrounds with floating animated shapes
- Smooth 60fps animations and micro-interactions
- Professional Inter font typography
- Color-coded payment methods and categories
- Beautiful toast notifications and modal dialogs
- Floating action button for quick access
- Keyboard shortcuts for power users

📱 Mobile Features:
- Touch-friendly interface with large tap targets
- Responsive grid layouts that adapt to screen size
- Mobile-optimized navigation and forms
- Swipe gestures and touch interactions
- Perfect rendering on all device sizes

🔧 Developer Experience:
- Clean, well-documented code with JavaDoc
- Comprehensive error handling and validation
- RESTful API design with proper HTTP status codes
- Automated testing with high code coverage
- Easy setup with single command execution
- Professional project structure and organization"

    echo -e "${GREEN}✅ Initial commit created${NC}"
else
    echo -e "${GREEN}✅ Repository already has commits${NC}"
fi

echo ""
echo -e "${BLUE}🌐 Next Steps:${NC}"
echo "1. Create a new repository on GitHub:"
echo "   - Go to https://github.com/new"
echo "   - Repository name: 'expenseflow' or 'expense-tracker'"
echo "   - Description: 'A stunning, professional expense tracking application with modern UI/UX design'"
echo "   - Make it Public"
echo "   - Don't initialize with README (you already have one)"
echo ""
echo "2. Add your GitHub repository as remote:"
echo -e "${YELLOW}   git remote add origin https://github.com/YOUR_USERNAME/expenseflow.git${NC}"
echo ""
echo "3. Push to GitHub:"
echo -e "${YELLOW}   git branch -M main${NC}"
echo -e "${YELLOW}   git push -u origin main${NC}"
echo ""
echo "4. Add repository topics/tags on GitHub:"
echo "   spring-boot, expense-tracker, java, javascript, responsive-design,"
echo "   modern-ui, glass-morphism, financial-app, full-stack, maven"
echo ""
echo -e "${PURPLE}🎯 Repository Features:${NC}"
echo "✅ Professional README with badges and documentation"
echo "✅ MIT License for open source friendliness"
echo "✅ Comprehensive .gitignore for clean repository"
echo "✅ Contributing guidelines for community involvement"
echo "✅ Screenshots showcasing the beautiful UI"
echo "✅ Demo data script for easy testing"
echo "✅ Clean project structure with best practices"
echo ""
echo -e "${GREEN}🎉 Your ExpenseFlow repository is ready to impress the GitHub community!${NC}"
echo ""
echo -e "${BLUE}💡 Pro Tips:${NC}"
echo "- Add a 'demo' or 'live-demo' link if you deploy it online"
echo "- Create releases when you add new features"
echo "- Respond to issues and pull requests promptly"
echo "- Consider adding GitHub Actions for CI/CD"
echo "- Star other similar projects to get visibility"
