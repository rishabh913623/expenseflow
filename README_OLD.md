# Expense Tracker Application

A comprehensive full-stack expense tracking application built with Java Spring Boot backend and vanilla JavaScript frontend. The application allows users to manage their expenses with separate handling for Cash and UPI payments, including detailed UPI transaction information.

## 🚀 Features

### Core Functionality
- **Add Expenses**: Create new expenses with amount, category, date, and payment method
- **Edit/Update**: Modify existing expense records
- **Delete**: Remove unwanted expense entries
- **View All**: Display expenses in a comprehensive table format
- **Filter & Search**: Filter expenses by date range, category, and payment method

### Payment Methods
- **Cash Payments**: Simple cash transaction recording
- **UPI Payments**: Detailed UPI transaction tracking with:
  - UPI VPA (Virtual Payment Address)
  - Transaction ID
  - Payer Name
  - Notes

### Reporting & Analytics
- **Summary Dashboard**: Overview of total expenses, transaction counts
- **Category-wise Reports**: Breakdown by expense categories
- **Payment Method Analysis**: Cash vs UPI spending analysis
- **Monthly Reports**: Time-based expense tracking
- **CSV Export**: Export filtered data for external analysis

### User Interface
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Real-time Validation**: Form validation with user-friendly error messages
- **Interactive Tables**: Sortable and filterable expense tables
- **Modal Dialogs**: Summary reports in overlay windows
- **Toast Notifications**: Success/error feedback messages

## 🛠 Technology Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Web**
- **MySQL Database**
- **Maven** (Build Tool)

### Frontend
- **HTML5**
- **CSS3** (with Flexbox/Grid)
- **Vanilla JavaScript** (ES6+)
- **Font Awesome** (Icons)

### Testing
- **JUnit 5**
- **Mockito**
- **Spring Boot Test**
- **H2 Database** (Test Environment)

## 📋 Prerequisites

Before running the application, ensure you have:

- **Java 17** or higher installed
- **Maven 3.6+** installed
- **MySQL 8.0+** running locally
- **Git** for version control

## 🚀 Quick Start

### 1. Clone the Repository
```bash
git clone <repository-url>
cd expense-tracker
```

### 2. Database Setup
Create a MySQL database:
```sql
CREATE DATABASE expense_tracker;
```

Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/expense_tracker?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Build and Run
```bash
# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 4. Access the Application
Open your web browser and navigate to:
```
http://localhost:8080
```

## 🗄 Database Schema

The application uses the following main table structure:

```sql
CREATE TABLE expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    expense_date DATE NOT NULL,
    payment_method ENUM('CASH', 'UPI') NOT NULL,
    cash_amount DECIMAL(10, 2) DEFAULT 0.00,
    upi_amount DECIMAL(10, 2) DEFAULT 0.00,
    upi_vpa VARCHAR(100),
    transaction_id VARCHAR(100),
    payer_name VARCHAR(100),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Expense Endpoints

#### Get All Expenses
```http
GET /api/expenses
```

**Query Parameters:**
- `category` (optional): Filter by category
- `paymentMethod` (optional): Filter by payment method (CASH/UPI)
- `startDate` (optional): Start date filter (yyyy-MM-dd)
- `endDate` (optional): End date filter (yyyy-MM-dd)
- `upiVpa` (optional): Filter by UPI VPA
- `transactionId` (optional): Filter by transaction ID

#### Get Expense by ID
```http
GET /api/expenses/{id}
```

#### Create New Expense
```http
POST /api/expenses
Content-Type: application/json

{
    "amount": 100.50,
    "category": "Food",
    "expenseDate": "2024-01-15",
    "paymentMethod": "UPI",
    "upiVpa": "user@paytm",
    "transactionId": "TXN123456789",
    "payerName": "John Doe",
    "notes": "Lunch at restaurant"
}
```

#### Update Expense
```http
PUT /api/expenses/{id}
Content-Type: application/json

{
    "amount": 150.75,
    "category": "Travel",
    "expenseDate": "2024-01-15",
    "paymentMethod": "CASH",
    "notes": "Bus fare"
}
```

#### Delete Expense
```http
DELETE /api/expenses/{id}
```

#### Get Expense Summary
```http
GET /api/expenses/summary
```

#### Get Categories
```http
GET /api/expenses/categories
```

#### Export to CSV
```http
GET /api/expenses/export/csv
```

### Report Endpoints

#### Monthly Summary
```http
GET /api/reports/monthly-summary
```

#### Category Totals
```http
GET /api/reports/category-totals
```

#### Payment Method Totals
```http
GET /api/reports/payment-method-totals
```

#### Dashboard Data
```http
GET /api/reports/dashboard
```

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn test -Dtest=*IntegrationTest
```

### Test Coverage
```bash
mvn jacoco:report
```

## 📱 Usage Guide

### Adding an Expense

1. **Fill Basic Information**:
   - Enter the expense amount
   - Select a category from the dropdown
   - Choose the expense date
   - Select payment method (Cash or UPI)

2. **UPI-Specific Fields** (if UPI is selected):
   - Enter UPI VPA (e.g., user@paytm)
   - Provide Transaction ID
   - Add Payer Name (optional)

3. **Additional Information**:
   - Add notes for context (optional)

4. **Submit**: Click "Add Expense" to save

### Filtering Expenses

Use the filter section to narrow down expenses:
- **Category**: Filter by specific expense category
- **Payment Method**: Show only Cash or UPI transactions
- **Date Range**: Set start and end dates
- **Apply/Clear**: Use buttons to apply or reset filters

### Viewing Reports

Click the "Summary" button to view:
- Total expenses and transaction count
- Cash vs UPI breakdown
- Category-wise spending analysis
- Payment method distribution

### Exporting Data

Click "Export CSV" to download expense data in CSV format for external analysis.

## 🔧 Configuration

### Application Properties

Key configuration options in `application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/expense_tracker
spring.datasource.username=root
spring.datasource.password=password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Logging
logging.level.com.expensetracker=DEBUG
```

### Default Categories

The application comes with predefined categories:
- Food
- Travel
- Utilities
- Entertainment
- Healthcare
- Shopping
- Education
- Miscellaneous

## 🚀 Deployment

### Production Build
```bash
mvn clean package -DskipTests
```

### Running JAR File
```bash
java -jar target/expense-tracker-0.0.1-SNAPSHOT.jar
```

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/expense-tracker-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Expense Tracker Team** - *Initial work*

## 🙏 Acknowledgments

- Spring Boot community for excellent documentation
- Font Awesome for beautiful icons
- MySQL for reliable database solution
