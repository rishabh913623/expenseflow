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

## 🚀 **Deployment**

### Vercel Deployment Errors

When developing your application with Vercel, you may encounter a variety of errors. They can reflect issues that happen with external providers such as domain services or internal problems at the level of your application's deployment or your usage of platform features.

For general error handling guidance, that covers dashboard related errors, see General Errors.

#### Application Errors

- **BODY_NOT_A_STRING_FROM_FUNCTION** (Function, 502)
- **DEPLOYMENT_BLOCKED** (Deployment, 403)
- **DEPLOYMENT_DELETED** (Deployment, 410)
- **DEPLOYMENT_DISABLED** (Deployment, 402)
- **DEPLOYMENT_NOT_FOUND** (Deployment, 404)
- **DEPLOYMENT_NOT_READY_REDIRECTING** (Deployment, 303)
- **DEPLOYMENT_PAUSED** (Deployment, 503)
- **DNS_HOSTNAME_EMPTY** (DNS, 502)
- **DNS_HOSTNAME_NOT_FOUND** (DNS, 502)
- **DNS_HOSTNAME_RESOLVE_FAILED** (DNS, 502)
- **DNS_HOSTNAME_RESOLVED_PRIVATE** (DNS, 404)
- **DNS_HOSTNAME_SERVER_ERROR** (DNS, 502)
- **EDGE_FUNCTION_INVOCATION_FAILED** (Function, 500)
- **EDGE_FUNCTION_INVOCATION_TIMEOUT** (Function, 504)
- **FALLBACK_BODY_TOO_LARGE** (Cache, 502)
- **FUNCTION_INVOCATION_FAILED** (Function, 500)
- **FUNCTION_INVOCATION_TIMEOUT** (Function, 504)
- **FUNCTION_PAYLOAD_TOO_LARGE** (Function, 413)
- **FUNCTION_RESPONSE_PAYLOAD_TOO_LARGE** (Function, 500)
- **FUNCTION_THROTTLED** (Function, 503)
- **INFINITE_LOOP_DETECTED** (Runtime, 508)
- **INVALID_IMAGE_OPTIMIZE_REQUEST** (Image, 400)
- **INVALID_REQUEST_METHOD** (Request, 405)
- **MALFORMED_REQUEST_HEADER** (Request, 400)
- **MICROFRONTENDS_MIDDLEWARE_ERROR** (Function, 500)
- **MIDDLEWARE_INVOCATION_FAILED** (Function, 500)
- **MIDDLEWARE_INVOCATION_TIMEOUT** (Function, 504)
- **MIDDLEWARE_RUNTIME_DEPRECATED** (Runtime, 503)
- **NO_RESPONSE_FROM_FUNCTION** (Function, 502)
- **NOT_FOUND** (Deployment, 404)
- **OPTIMIZED_EXTERNAL_IMAGE_REQUEST_FAILED** (Image, 502)
- **OPTIMIZED_EXTERNAL_IMAGE_REQUEST_INVALID** (Image, 502)
- **OPTIMIZED_EXTERNAL_IMAGE_REQUEST_UNAUTHORIZED** (Image, 502)
- **OPTIMIZED_EXTERNAL_IMAGE_TOO_MANY_REDIRECTS** (Image, 502)
- **RANGE_END_NOT_VALID** (Request, 416)
- **RANGE_GROUP_NOT_VALID** (Request, 416)
- **RANGE_MISSING_UNIT** (Request, 416)
- **RANGE_START_NOT_VALID** (Request, 416)
- **RANGE_UNIT_NOT_SUPPORTED** (Request, 416)
- **REQUEST_HEADER_TOO_LARGE** (Request, 431)
- **RESOURCE_NOT_FOUND** (Request, 404)
- **ROUTER_CANNOT_MATCH** (Routing, 502)
- **ROUTER_EXTERNAL_TARGET_CONNECTION_ERROR** (Routing, 502)
- **ROUTER_EXTERNAL_TARGET_ERROR** (Routing, 502)
- **ROUTER_EXTERNAL_TARGET_HANDSHAKE_ERROR** (Routing, 502)
- **ROUTER_TOO_MANY_HAS_SELECTIONS** (Routing, 502)
- **SANDBOX_NOT_FOUND** (Sandbox, 404)
- **SANDBOX_NOT_LISTENING** (Sandbox, 502)
- **SANDBOX_STOPPED** (Sandbox, 410)
- **TOO_MANY_FILESYSTEM_CHECKS** (Routing, 502)
- **TOO_MANY_FORKS** (Routing, 502)
- **TOO_MANY_RANGES** (Request, 416)
- **URL_TOO_LONG** (Request, 414)

#### Platform Errors

The following errors are related to the Vercel platform. If you encounter one of these errors, contact Vercel support.

- **FUNCTION_THROTTLED** (Internal, 500)
- **INTERNAL_CACHE_ERROR** (Internal, 500)
- **INTERNAL_CACHE_KEY_TOO_LONG** (Internal, 500)
- **INTERNAL_CACHE_LOCK_FULL** (Internal, 500)
- **INTERNAL_CACHE_LOCK_TIMEOUT** (Internal, 500)
- **INTERNAL_DEPLOYMENT_FETCH_FAILED** (Internal, 500)
- **INTERNAL_EDGE_FUNCTION_INVOCATION_FAILED** (Internal, 500)
- **INTERNAL_EDGE_FUNCTION_INVOCATION_TIMEOUT** (Internal, 500)
- **INTERNAL_FUNCTION_INVOCATION_FAILED** (Internal, 500)
- **INTERNAL_FUNCTION_INVOCATION_TIMEOUT** (Internal, 500)
- **INTERNAL_FUNCTION_NOT_FOUND** (Internal, 500)
- **INTERNAL_FUNCTION_NOT_READY** (Internal, 500)
- **INTERNAL_FUNCTION_SERVICE_UNAVAILABLE** (Internal, 500)
- **INTERNAL_MICROFRONTENDS_BUILD_ERROR** (Internal, 500)
- **INTERNAL_MICROFRONTENDS_INVALID_CONFIGURATION_ERROR** (Internal, 500)
- **INTERNAL_MICROFRONTENDS_UNEXPECTED_ERROR** (Internal, 500)
- **INTERNAL_MISSING_RESPONSE_FROM_CACHE** (Internal, 500)
- **INTERNAL_OPTIMIZED_IMAGE_REQUEST_FAILED** (Internal, 500)
- **INTERNAL_ROUTER_CANNOT_PARSE_PATH** (Internal, 500)
- **INTERNAL_STATIC_REQUEST_FAILED** (Internal, 500)
- **INTERNAL_UNARCHIVE_FAILED** (Internal, 500)
- **INTERNAL_UNEXPECTED_ERROR** (Internal, 500)

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
