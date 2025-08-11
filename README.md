# 💰 ExpenseFlow - Modern Expense Tracker

<div align="center">

![ExpenseFlow Banner](https://img.shields.io/badge/ExpenseFlow-Modern%20Expense%20Tracker-6366f1?style=for-the-badge&logo=wallet&logoColor=white)

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![H2 Database](https://img.shields.io/badge/Database-H2-blue?style=flat-square&logo=h2)](https://www.h2database.com/)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat-square)](LICENSE)

**A stunning, professional expense tracking application with modern UI/UX design**

[🚀 Quick Start](#-quick-start) • [📖 Documentation](#-api-documentation) • [🎨 Features](#-features) • [🛠️ Tech Stack](#-technology-stack)

</div>

---

## 🎯 **Overview**

ExpenseFlow is a comprehensive expense tracking application that combines **powerful Spring Boot backend** with **stunning modern UI design**. Built with professional-grade architecture and featuring beautiful animations, glass morphism effects, and responsive design.

### 🌟 **Why ExpenseFlow?**

- **🎨 Beautiful Modern UI**: Glass morphism, gradient backgrounds, smooth animations
- **📱 Mobile-First Design**: Responsive layout that works perfectly on all devices  
- **⚡ Real-Time Updates**: Instant filtering, live statistics, smooth interactions
- **🔒 Robust Backend**: Spring Boot with JPA, comprehensive validation, error handling
- **📊 Advanced Analytics**: Detailed reports, category breakdowns, payment insights
- **🎯 User-Centric**: Intuitive interface designed for effortless expense tracking

## ✨ **Features**

### 💎 **Modern UI/UX Design**
- **🌈 Gradient Backgrounds**: Animated floating shapes with stunning visual effects
- **🔮 Glass Morphism**: Translucent navigation with backdrop blur effects
- **✨ Smooth Animations**: Buttery smooth transitions and micro-interactions
- **🎨 Professional Typography**: Inter font family for crisp, modern text
- **🌙 Dark/Light Themes**: Toggle between themes with smooth transitions
- **📱 Responsive Design**: Perfect experience on desktop, tablet, and mobile

### 💰 **Expense Management**
- **➕ Smart Form Design**: Beautiful form with animated validation
- **💳 Payment Methods**: Support for Cash and UPI payments with visual indicators
- **📱 UPI Integration**: Track VPA, transaction ID, and payer information
- **🏷️ Smart Categories**: 8 predefined categories with emoji icons
- **📝 Rich Notes**: Add detailed notes to expenses
- **📅 Date Management**: Easy date selection with modern date picker

### 📊 **Dashboard & Analytics**
- **📈 Live Statistics**: 4 beautiful stat cards with real-time updates
- **🎯 Category Breakdown**: Visual category analysis with icons
- **💸 Payment Insights**: Cash vs UPI spending analysis
- **📋 Dual View System**: Switch between card view and table view
- **🔍 Advanced Filtering**: Smart filters with smooth animations
- **📤 CSV Export**: Download data for external analysis

### 🎛️ **Advanced Features**
- **🔔 Toast Notifications**: Beautiful slide-in notifications
- **⌨️ Keyboard Shortcuts**: Power user shortcuts (Ctrl+N, Ctrl+E, Ctrl+D)
- **🎭 Modal System**: Elegant modals with backdrop blur
- **🎯 Floating Action Button**: Quick access to add expenses
- **💾 Auto-Save**: Automatic form state management
- **🔄 Real-Time Sync**: Live updates across all components

## 🛠 **Technology Stack**

### Backend
- **Java 17** - Modern Java features and performance
- **Spring Boot 3.2.0** - Enterprise-grade framework
- **Spring Data JPA** - Powerful data persistence
- **H2 Database** - Fast in-memory database for development
- **Maven** - Dependency management and build automation

### Frontend
- **HTML5** - Semantic markup
- **CSS3** - Modern styling with custom properties, flexbox, grid
- **Vanilla JavaScript (ES6+)** - Clean, modern JavaScript
- **Font Awesome 6.4.0** - Beautiful icons
- **Google Fonts (Inter)** - Professional typography

### Design System
- **CSS Custom Properties** - Consistent theming
- **Glass Morphism** - Modern UI trend
- **Gradient Backgrounds** - Eye-catching visuals
- **Smooth Animations** - 60fps performance
- **Mobile-First** - Responsive design approach

## 🚀 **Quick Start**

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Modern web browser

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/yourusername/expenseflow.git
cd expenseflow
```

2. **Build the application**
```bash
mvn clean package -DskipTests
```

3. **Run the application**
```bash
# Option 1: Use the smart script
./run.sh

# Option 2: Run manually
java -jar target/expense-tracker-0.0.1-SNAPSHOT.jar
```

4. **Open your browser**
```
http://localhost:8080
```

### 🎉 **First Time Setup**

1. **Add your first expense** - Try the beautiful form with smooth animations
2. **Explore categories** - 8 predefined categories with emoji icons
3. **Try different views** - Switch between card and table views
4. **Test filtering** - Use the advanced filter system
5. **Check dashboard** - View your expense analytics

## 📱 **Screenshots**

### 🏠 Dashboard View
Beautiful stat cards with gradient backgrounds and real-time updates.

![Dashboard](screenshots/dashboard.png)

### ➕ Add Expense Form
Modern form design with animated validation and payment method selection.

![Add Expense Form](screenshots/add-expense-form.png)

### 📋 Expense Cards View
Card layout with beautiful expense cards and category icons.

![Expense Cards](screenshots/expense-cards.png)

### 📊 Table View with Filters
Professional table layout with advanced filtering capabilities.

![Table View](screenshots/table-view.png)

### 📱 Mobile Experience
Fully responsive design that works perfectly on all devices.

![Mobile View](screenshots/mobile-view.png)

### 🌙 Dark Theme
Beautiful dark theme with smooth transitions.

![Dark Theme](screenshots/dark-theme.png)

## 🎨 **Design Highlights**

- **Color Palette**: Professional purple gradients with carefully chosen accent colors
- **Typography**: Inter font family for maximum readability
- **Animations**: Smooth 60fps animations with proper easing
- **Accessibility**: WCAG compliant with keyboard navigation support
- **Performance**: Optimized CSS and JavaScript for fast loading

## 📖 **API Documentation**

### Base URL
```
http://localhost:8080/api
```

### Key Endpoints

#### Expenses
- `GET /api/expenses` - Get all expenses with optional filtering
- `POST /api/expenses` - Create new expense
- `PUT /api/expenses/{id}` - Update expense
- `DELETE /api/expenses/{id}` - Delete expense
- `GET /api/expenses/categories` - Get all categories
- `GET /api/expenses/summary` - Get expense summary
- `GET /api/expenses/export/csv` - Export to CSV

#### Reports
- `GET /api/reports/dashboard` - Dashboard statistics
- `GET /api/reports/category-totals` - Category breakdown
- `GET /api/reports/payment-method-totals` - Payment method analysis

## 🧪 **Testing**

```bash
# Run all tests
mvn test

# Run integration tests
mvn test -Dtest=*IntegrationTest

# Generate test coverage report
mvn jacoco:report
```

## 🤝 **Contributing**

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 **License**

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 **Acknowledgments**

- Spring Boot team for the excellent framework
- Font Awesome for beautiful icons
- Google Fonts for typography
- The open-source community for inspiration

---

<div align="center">

**Made with ❤️ and lots of ☕**

[⭐ Star this repo](https://github.com/yourusername/expenseflow) if you found it helpful!

</div>
