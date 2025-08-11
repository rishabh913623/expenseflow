#!/bin/bash

echo "🎯 Adding Demo Data to ExpenseFlow"
echo "=================================="

# Base URL - adjust port if needed
BASE_URL="http://localhost:8081/api/expenses"

# Check if server is running
if ! curl -s "$BASE_URL" > /dev/null; then
    echo "❌ Server is not running on port 8081. Please start the application first."
    echo "Run: ./run.sh"
    exit 1
fi

echo "✅ Server is running. Adding demo expenses..."

# Sample expenses with variety
expenses=(
    '{"amount":150.50,"category":"Food","expenseDate":"2024-01-15","paymentMethod":"CASH","notes":"Lunch at Italian restaurant"}'
    '{"amount":2500.00,"category":"Travel","expenseDate":"2024-01-14","paymentMethod":"UPI","upiVpa":"user@paytm","transactionId":"TXN123456789","payerName":"John Doe","notes":"Flight booking to Mumbai"}'
    '{"amount":85.75,"category":"Food","expenseDate":"2024-01-13","paymentMethod":"UPI","upiVpa":"foodie@gpay","transactionId":"TXN987654321","payerName":"Jane Smith","notes":"Grocery shopping"}'
    '{"amount":1200.00,"category":"Utilities","expenseDate":"2024-01-12","paymentMethod":"UPI","upiVpa":"bills@phonepe","transactionId":"TXN456789123","payerName":"Utility Corp","notes":"Electricity bill payment"}'
    '{"amount":45.00,"category":"Entertainment","expenseDate":"2024-01-11","paymentMethod":"CASH","notes":"Movie tickets"}'
    '{"amount":320.25,"category":"Shopping","expenseDate":"2024-01-10","paymentMethod":"UPI","upiVpa":"shop@amazonpay","transactionId":"TXN789123456","payerName":"Amazon","notes":"Online shopping - electronics"}'
    '{"amount":75.00,"category":"Healthcare","expenseDate":"2024-01-09","paymentMethod":"CASH","notes":"Pharmacy - medicines"}'
    '{"amount":500.00,"category":"Education","expenseDate":"2024-01-08","paymentMethod":"UPI","upiVpa":"learn@gpay","transactionId":"TXN321654987","payerName":"Online Course","notes":"Programming course subscription"}'
    '{"amount":25.50,"category":"Miscellaneous","expenseDate":"2024-01-07","paymentMethod":"CASH","notes":"Stationery items"}'
    '{"amount":180.00,"category":"Food","expenseDate":"2024-01-06","paymentMethod":"UPI","upiVpa":"dinner@paytm","transactionId":"TXN654321789","payerName":"Restaurant","notes":"Dinner with friends"}'
    '{"amount":60.00,"category":"Travel","expenseDate":"2024-01-05","paymentMethod":"CASH","notes":"Local bus fare"}'
    '{"amount":95.25,"category":"Entertainment","expenseDate":"2024-01-04","paymentMethod":"UPI","upiVpa":"fun@phonepe","transactionId":"TXN147258369","payerName":"Gaming","notes":"Gaming subscription"}'
)

# Add each expense
count=0
for expense in "${expenses[@]}"; do
    echo "Adding expense $((count + 1))..."
    
    response=$(curl -s -X POST "$BASE_URL" \
        -H "Content-Type: application/json" \
        -d "$expense")
    
    if [ $? -eq 0 ]; then
        echo "✅ Added expense $((count + 1))"
    else
        echo "❌ Failed to add expense $((count + 1))"
    fi
    
    count=$((count + 1))
    sleep 0.5  # Small delay to avoid overwhelming the server
done

echo ""
echo "🎉 Demo data added successfully!"
echo "📊 Total expenses added: $count"
echo ""
echo "🌐 Open your browser to see the beautiful data:"
echo "   http://localhost:8081"
echo ""
echo "📸 Perfect for taking screenshots!"
echo "   - Dashboard with colorful stats"
echo "   - Expense cards with variety"
echo "   - Table view with filters"
echo "   - Category breakdown"
echo "   - Payment method analysis"
