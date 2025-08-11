#!/bin/bash

echo "📸 ExpenseFlow Screenshot Upload Helper"
echo "======================================"
echo ""

# Check if screenshots directory exists
if [ ! -d "screenshots" ]; then
    echo "❌ Screenshots directory not found!"
    exit 1
fi

# Check if user has added screenshots
screenshot_count=$(find screenshots -name "*.png" -o -name "*.jpg" -o -name "*.jpeg" | wc -l)

if [ $screenshot_count -eq 0 ]; then
    echo "📋 Ready to upload your ExpenseFlow screenshots!"
    echo ""
    echo "🎯 Please add these 3 screenshots to the screenshots/ folder:"
    echo "   • dashboard.png (main dashboard with stats)"
    echo "   • add-expense-form.png (form with payment methods)"
    echo "   • expense-details.png (UPI transaction details)"
    echo ""
    echo "💡 How to add them:"
    echo "   1. Right-click each screenshot in your browser"
    echo "   2. Save with the exact names above"
    echo "   3. Copy to: $(pwd)/screenshots/"
    echo "   4. Run this script again!"
    echo ""
    echo "⏳ Waiting for screenshots... (checking every 5 seconds)"

    # Monitor for screenshots
    while [ $screenshot_count -eq 0 ]; do
        sleep 5
        screenshot_count=$(find screenshots -name "*.png" -o -name "*.jpg" -o -name "*.jpeg" | wc -l)
        if [ $screenshot_count -gt 0 ]; then
            echo ""
            echo "🎉 Screenshots detected! Processing upload..."
            break
        fi
        echo -n "."
    done
fi

echo ""
echo "✅ Found $screenshot_count screenshot(s) in screenshots/ folder"
echo ""

# List found screenshots
echo "📁 Screenshots found:"
for file in screenshots/*.{png,jpg,jpeg}; do
    if [ -f "$file" ]; then
        echo "   • $(basename "$file")"
    fi
done

echo ""
echo "🚀 Uploading screenshots to GitHub..."

# Add screenshots to git
git add screenshots/

# Commit screenshots
git commit -m "📸 Add stunning ExpenseFlow application screenshots

✨ Features showcased:
- Modern glass morphism dashboard with expense statistics
- Beautiful gradient backgrounds and floating animations
- Payment method selection (Cash/UPI) with clean form design
- Complete UPI transaction details and validation
- Mobile-responsive layout with professional typography
- Real-time expense tracking and categorization

🎨 UI highlights:
- Purple to blue gradient backgrounds
- Translucent cards with backdrop blur effects
- Smooth hover animations and transitions
- Clean, intuitive user experience
- Professional color scheme and spacing"

# Push to GitHub
echo "📤 Pushing to GitHub repository..."
git push origin main

echo ""
echo "🎉 SUCCESS! Screenshots uploaded to GitHub!"
echo "🌐 View your repository: https://github.com/rishabh913623/expenseflow"
echo ""
echo "✨ Your ExpenseFlow repository now showcases:"
echo "   • 🎨 Beautiful modern UI design"
echo "   • 💎 Professional glass morphism effects"
echo "   • 💳 Complete expense tracking functionality"
echo "   • 📱 Mobile-responsive layout"
echo "   • 🚀 Production-ready application"
echo ""
echo "🎯 Recommended next steps:"
echo "   • Add repository topics: expense-tracker, java, spring-boot, ui-design"
echo "   • Create a release tag (v1.0.0)"
echo "   • Add to your portfolio/resume"
echo "   • Share with potential employers!"
echo ""
echo "🏆 Congratulations on building such a beautiful application!"
