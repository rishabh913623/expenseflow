/**
 * Expense Tracker Application JavaScript
 * Handles all frontend functionality including API calls, form validation, and UI interactions
 */

// Global variables
let currentEditingId = null;
let allExpenses = [];
let currentView = 'card'; // 'card' or 'table'
let filtersVisible = false;
let isValidatingToken = false;
let isInitialized = false;
let isRedirecting = false;

// API Base URL
const API_BASE_URL = '/api/expenses';

// Get authentication token
function getAuthToken() {
    // First try localStorage
    let token = localStorage.getItem('authToken');
    if (token) {
        return token;
    }

    // Then try cookie
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === 'authToken' && value) {
            // Store in localStorage for future use
            localStorage.setItem('authToken', value);
            return value;
        }
    }

    return null;
}

// Create headers with authentication
function getAuthHeaders() {
    const token = getAuthToken();
    const headers = {
        'Content-Type': 'application/json',
    };

    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    return headers;
}

// Validate JWT token with server (prevents duplicate calls)
async function validateToken(token) {
    // Prevent multiple simultaneous validation calls
    if (isValidatingToken) {
        return new Promise((resolve) => {
            const checkInterval = setInterval(() => {
                if (!isValidatingToken) {
                    clearInterval(checkInterval);
                    resolve(!!localStorage.getItem('authToken'));
                }
            }, 100);
        });
    }

    try {
        isValidatingToken = true;

        const response = await Promise.race([
            fetch('/api/auth/validate', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                }
            }),
            new Promise((_, reject) =>
                setTimeout(() => reject(new Error('Token validation timeout')), 3000)
            )
        ]);

        const isValid = response.ok;

        if (!isValid) {
            // Clear invalid token
            localStorage.removeItem('authToken');
            localStorage.removeItem('username');
        }

        return isValid;
    } catch (error) {
        console.error('Token validation error:', error);
        // Clear potentially corrupted token on network errors
        if (error.message === 'Token validation timeout' || error.name === 'TypeError') {
            localStorage.removeItem('authToken');
            localStorage.removeItem('username');
        }
        return false;
    } finally {
        isValidatingToken = false;
    }
}

// DOM Elements
const expenseForm = document.getElementById('expenseForm');
const budgetForm = document.getElementById('budgetForm');
const upiFields = document.getElementById('upiFields');
const expensesTableBody = document.getElementById('expensesTableBody');
const expensesGrid = document.getElementById('cardView');
const loadingSpinner = document.getElementById('loadingSpinner');
const summaryModal = document.getElementById('summaryModal');
const budgetModal = document.getElementById('budgetModal');
const toastContainer = document.getElementById('toastContainer');
const filtersPanel = document.getElementById('filtersPanel');

// Initialize application
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

/**
 * Initialize the application (prevents multiple initializations)
 */
async function initializeApp() {
    // Prevent multiple initializations
    if (isInitialized || isRedirecting) {
        return;
    }

    try {
        // Check if user is authenticated
        const token = getAuthToken();
        const currentPath = window.location.pathname;

        if (!token) {
            if (!isRedirecting) {
                isRedirecting = true;
                window.location.replace('/login.html');
            }
            return;
        }

        // Validate token with timeout
        const isValid = await Promise.race([
            validateToken(token),
            new Promise((_, reject) =>
                setTimeout(() => reject(new Error('Authentication timeout')), 5000)
            )
        ]);

        if (!isValid) {
            localStorage.removeItem('authToken');
            localStorage.removeItem('username');
            if (currentPath !== '/login.html' && !isRedirecting) {
                isRedirecting = true;
                window.location.replace('/login.html');
            }
            return;
        }

        isInitialized = true;

        // Set today's date as default
        const dateElement = document.getElementById('expenseDate');
        if (dateElement) {
            dateElement.value = new Date().toISOString().split('T')[0];
        }

        // Initialize theme
        initializeTheme();

        // Setup event listeners first
        setupEventListeners();

        // Load data sequentially to avoid race conditions
        try {
            await loadCategories();
        } catch (error) {
            console.warn('Failed to load categories:', error);
        }

        try {
            await loadBudget();
        } catch (error) {
            console.warn('Failed to load budget:', error);
        }

        // Initialize view preference
        const savedView = localStorage.getItem('preferredView') || 'card';
        switchView(savedView);

        // Load expenses
        try {
            await loadExpenses();
        } catch (error) {
            console.warn('Failed to load expenses:', error);
            showToast('Failed to load expenses. Please refresh the page.', 'warning');
        }

        // Add smooth scrolling to all internal links
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', function (e) {
                e.preventDefault();
                const target = document.querySelector(this.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({ behavior: 'smooth' });
                }
            });
        });

    } catch (error) {
        console.error('Application initialization failed:', error);
        // Only redirect on critical authentication errors
        if (error.message === 'Authentication timeout' || error.message.includes('Token validation timeout')) {
            showToast('Authentication failed. Redirecting to login...', 'error');
            setTimeout(() => {
                localStorage.removeItem('authToken');
                localStorage.removeItem('username');
                window.location.replace('/login.html');
            }, 2000);
        } else {
            showToast('Failed to initialize application. Please refresh the page.', 'error');
        }
    }
}

/**
 * Initialize theme
 */
function initializeTheme() {
    const savedTheme = localStorage.getItem('theme') ||
                      (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');

    document.documentElement.setAttribute('data-theme', savedTheme);

    const themeIcon = document.querySelector('#themeToggle i');
    if (themeIcon) {
        themeIcon.className = savedTheme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
    }
}

/**
 * Setup all event listeners
 */
function setupEventListeners() {
    // Form submission
    expenseForm.addEventListener('submit', handleFormSubmit);
    budgetForm.addEventListener('submit', handleBudgetFormSubmit);

    // Payment method change (radio buttons)
    document.querySelectorAll('input[name="paymentMethod"]').forEach(radio => {
        radio.addEventListener('change', handlePaymentMethodChange);
    });

    // Form reset
    expenseForm.addEventListener('reset', handleFormReset);

    // Filter controls
    document.getElementById('toggleFilters').addEventListener('click', toggleFilters);
    document.getElementById('applyFiltersBtn').addEventListener('click', applyFilters);
    document.getElementById('clearFiltersBtn').addEventListener('click', clearFilters);

    // View toggle
    document.getElementById('cardViewBtn').addEventListener('click', () => switchView('card'));
    document.getElementById('tableViewBtn').addEventListener('click', () => switchView('table'));

    // Header actions
    document.getElementById('exportBtn').addEventListener('click', exportToCsv);
    document.getElementById('budgetBtn').addEventListener('click', showBudgetModal);
    document.getElementById('summaryBtn').addEventListener('click', showSummary);
    document.getElementById('cancelEditBtn').addEventListener('click', cancelEdit);
    document.getElementById('logoutBtn').addEventListener('click', logout);

    // FAB
    document.getElementById('fabBtn').addEventListener('click', () => {
        document.getElementById('amount').focus();
        document.getElementById('amount').scrollIntoView({ behavior: 'smooth' });
    });

    // Theme toggle
    document.getElementById('themeToggle').addEventListener('click', toggleTheme);

    // Modal close
    document.querySelector('#summaryModal .modal-close').addEventListener('click', closeSummaryModal);
    document.querySelector('#summaryModal .modal-backdrop').addEventListener('click', closeSummaryModal);
    document.querySelector('#budgetModal .modal-close').addEventListener('click', closeBudgetModal);
    document.querySelector('#budgetModal .modal-backdrop').addEventListener('click', closeBudgetModal);

    // Clear budget button
    document.getElementById('clearBudgetBtn').addEventListener('click', clearBudget);

    // Keyboard shortcuts
    document.addEventListener('keydown', handleKeyboardShortcuts);
}

/**
 * Handle form submission for creating/updating expenses
 */
async function handleFormSubmit(event) {
    event.preventDefault();
    
    if (!validateForm()) {
        return;
    }
    
    const formData = new FormData(expenseForm);
    const expenseData = {
        amount: parseFloat(formData.get('amount')),
        category: formData.get('category'),
        expenseDate: formData.get('expenseDate'),
        paymentMethod: formData.get('paymentMethod'),
        upiVpa: formData.get('upiVpa') || null,
        transactionId: formData.get('transactionId') || null,
        payerName: formData.get('payerName') || null,
        notes: formData.get('notes') || null
    };
    
    try {
        showLoading(true);
        
        let response;
        if (currentEditingId) {
            response = await fetch(`${API_BASE_URL}/${currentEditingId}`, {
                method: 'PUT',
                headers: getAuthHeaders(),
                body: JSON.stringify(expenseData)
            });
        } else {
            response = await fetch(API_BASE_URL, {
                method: 'POST',
                headers: getAuthHeaders(),
                body: JSON.stringify(expenseData)
            });
        }
        
        if (response.ok) {
            const savedExpense = await response.json();
            showToast(currentEditingId ? 'Expense updated successfully!' : 'Expense added successfully!', 'success');
            expenseForm.reset();
            handleFormReset();
            loadExpenses();
        } else {
            throw new Error('Failed to save expense');
        }
    } catch (error) {
        console.error('Error saving expense:', error);
        showToast('Error saving expense. Please try again.', 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Handle payment method change to show/hide UPI fields
 */
function handlePaymentMethodChange() {
    const selectedPaymentMethod = document.querySelector('input[name="paymentMethod"]:checked');
    const paymentMethod = selectedPaymentMethod ? selectedPaymentMethod.value : null;
    const upiInputs = upiFields.querySelectorAll('input');

    if (paymentMethod === 'UPI') {
        upiFields.style.display = 'block';
        // Make UPI fields required
        document.getElementById('upiVpa').required = true;
        document.getElementById('transactionId').required = true;
    } else {
        upiFields.style.display = 'none';
        // Remove required attribute and clear values
        upiInputs.forEach(input => {
            input.required = false;
            input.value = '';
        });
    }
}

/**
 * Handle form reset
 */
function handleFormReset() {
    currentEditingId = null;
    document.getElementById('formTitle').innerHTML = '<i class="fas fa-plus-circle"></i> Add New Expense';
    document.getElementById('submitBtnText').textContent = 'Add Expense';
    document.getElementById('cancelEditBtn').style.display = 'none';
    upiFields.style.display = 'none';

    // Reset payment method radio buttons
    document.querySelectorAll('input[name="paymentMethod"]').forEach(radio => {
        radio.checked = false;
    });

    // Reset UPI field requirements
    document.getElementById('upiVpa').required = false;
    document.getElementById('transactionId').required = false;

    // Set today's date
    document.getElementById('expenseDate').value = new Date().toISOString().split('T')[0];
}

/**
 * Validate form data
 */
function validateForm() {
    const amount = document.getElementById('amount').value;
    const category = document.getElementById('category').value;
    const expenseDate = document.getElementById('expenseDate').value;
    const selectedPaymentMethod = document.querySelector('input[name="paymentMethod"]:checked');
    const paymentMethod = selectedPaymentMethod ? selectedPaymentMethod.value : null;

    if (!amount || parseFloat(amount) <= 0) {
        showToast('Please enter a valid amount greater than 0', 'error');
        return false;
    }

    if (!category) {
        showToast('Please select a category', 'error');
        return false;
    }

    if (!expenseDate) {
        showToast('Please select an expense date', 'error');
        return false;
    }

    if (!paymentMethod) {
        showToast('Please select a payment method', 'error');
        return false;
    }

    // UPI specific validation
    if (paymentMethod === 'UPI') {
        const upiVpa = document.getElementById('upiVpa').value;
        const transactionId = document.getElementById('transactionId').value;

        if (!upiVpa) {
            showToast('UPI VPA is required for UPI payments', 'error');
            return false;
        }

        if (!transactionId) {
            showToast('Transaction ID is required for UPI payments', 'error');
            return false;
        }
    }

    return true;
}

/**
 * Load all expenses from the API
 */
async function loadExpenses() {
    try {
        showLoading(true);

        const response = await fetch(API_BASE_URL, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            allExpenses = await response.json();
            displayExpenses(allExpenses);
            updateExpenseStats(allExpenses);

            // Also load summary to update budget display
            loadSummaryForBudget();
        } else {
            console.error('Failed to load expenses:', response.status);
            // Don't throw error, just log it
        }
    } catch (error) {
        console.error('Error loading expenses:', error);
        // Don't show toast for initialization errors
    } finally {
        showLoading(false);
    }
}

/**
 * Display expenses in the selected view
 */
function displayExpenses(expenses) {
    if (expenses.length === 0) {
        document.getElementById('noExpenses').style.display = 'block';
        document.getElementById('cardView').style.display = 'none';
        document.getElementById('tableView').style.display = 'none';
        return;
    }

    document.getElementById('noExpenses').style.display = 'none';

    if (currentView === 'card') {
        displayExpenseCards(expenses);
        document.getElementById('cardView').style.display = 'grid';
        document.getElementById('tableView').style.display = 'none';
    } else {
        displayExpenseTable(expenses);
        document.getElementById('cardView').style.display = 'none';
        document.getElementById('tableView').style.display = 'block';
    }
}

/**
 * Display expenses as cards
 */
function displayExpenseCards(expenses) {
    const container = expensesGrid;
    container.innerHTML = '';

    expenses.forEach(expense => {
        const card = createExpenseCard(expense);
        container.appendChild(card);
    });
}

/**
 * Display expenses in table format
 */
function displayExpenseTable(expenses) {
    const tbody = expensesTableBody;
    tbody.innerHTML = '';

    expenses.forEach(expense => {
        const row = createExpenseRow(expense);
        tbody.appendChild(row);
    });
}

/**
 * Create an expense card
 */
function createExpenseCard(expense) {
    const card = document.createElement('div');
    card.className = 'expense-card';

    const categoryIcon = getCategoryIcon(expense.category);
    const paymentBadgeClass = expense.paymentMethod.toLowerCase();
    const upiDetails = expense.paymentMethod === 'UPI' ?
        `<div class="upi-details">
            <small><strong>VPA:</strong> ${expense.upiVpa || 'N/A'}</small><br>
            <small><strong>ID:</strong> ${expense.transactionId || 'N/A'}</small><br>
            <small><strong>Payer:</strong> ${expense.payerName || 'N/A'}</small>
        </div>` : '';

    card.innerHTML = `
        <div class="expense-card-header">
            <div class="expense-amount">₹${expense.amount.toFixed(2)}</div>
            <div class="expense-date">${formatDate(expense.expenseDate)}</div>
        </div>
        <div class="expense-category">
            ${categoryIcon} ${expense.category}
        </div>
        <div class="expense-payment">
            <span class="payment-badge ${paymentBadgeClass}">
                ${expense.paymentMethod === 'CASH' ? '💵' : '📱'} ${expense.paymentMethod}
            </span>
        </div>
        ${upiDetails}
        ${expense.notes ? `<div class="expense-notes">${expense.notes}</div>` : ''}
        <div class="expense-actions">
            <button class="action-btn edit-btn" onclick="editExpense(${expense.id})" title="Edit">
                <i class="fas fa-edit"></i>
            </button>
            <button class="action-btn delete-btn" onclick="deleteExpense(${expense.id})" title="Delete">
                <i class="fas fa-trash"></i>
            </button>
        </div>
    `;

    return card;
}

/**
 * Create a table row for an expense
 */
function createExpenseRow(expense) {
    const row = document.createElement('tr');

    const upiDetails = expense.paymentMethod === 'UPI' ?
        `VPA: ${expense.upiVpa || 'N/A'}<br>ID: ${expense.transactionId || 'N/A'}<br>Payer: ${expense.payerName || 'N/A'}` :
        'N/A';

    row.innerHTML = `
        <td>${formatDate(expense.expenseDate)}</td>
        <td>${getCategoryIcon(expense.category)} ${expense.category}</td>
        <td class="amount">₹${expense.amount.toFixed(2)}</td>
        <td><span class="payment-badge ${expense.paymentMethod.toLowerCase()}">${expense.paymentMethod}</span></td>
        <td class="amount">₹${expense.cashAmount.toFixed(2)}</td>
        <td class="amount">₹${expense.upiAmount.toFixed(2)}</td>
        <td class="upi-details">${upiDetails}</td>
        <td class="notes">${expense.notes || ''}</td>
        <td class="actions">
            <button class="action-btn edit-btn" onclick="editExpense(${expense.id})" title="Edit">
                <i class="fas fa-edit"></i>
            </button>
            <button class="action-btn delete-btn" onclick="deleteExpense(${expense.id})" title="Delete">
                <i class="fas fa-trash"></i>
            </button>
        </td>
    `;

    return row;
}

/**
 * Get category icon
 */
function getCategoryIcon(category) {
    const icons = {
        'Food': '🍽️',
        'Travel': '✈️',
        'Utilities': '⚡',
        'Entertainment': '🎬',
        'Healthcare': '🏥',
        'Shopping': '🛍️',
        'Education': '📚',
        'Miscellaneous': '📦'
    };
    return icons[category] || '📦';
}

/**
 * Update expense statistics
 */
function updateExpenseStats(expenses) {
    const count = expenses.length;
    const total = expenses.reduce((sum, expense) => sum + expense.amount, 0);
    const cashTotal = expenses.reduce((sum, expense) => sum + expense.cashAmount, 0);
    const upiTotal = expenses.reduce((sum, expense) => sum + expense.upiAmount, 0);

    // Update main stats
    document.getElementById('expenseCount').textContent = `${count} expense${count !== 1 ? 's' : ''}`;
    document.getElementById('totalAmount').textContent = `Total: ₹${total.toFixed(2)}`;

    // Update stat cards
    document.getElementById('totalExpenseAmount').textContent = `₹${total.toFixed(2)}`;
    document.getElementById('cashExpenseAmount').textContent = `₹${cashTotal.toFixed(2)}`;
    document.getElementById('upiExpenseAmount').textContent = `₹${upiTotal.toFixed(2)}`;
    document.getElementById('transactionCount').textContent = count.toString();

    // Update budget display (will be updated when summary is loaded)
    updateBudgetDisplay();
}

/**
 * Update budget display
 */
function updateBudgetDisplay() {
    // This will be called after summary is loaded
    // For now, just ensure the element exists
    const remainingBudgetElement = document.getElementById('remainingBudgetAmount');
    if (remainingBudgetElement && !remainingBudgetElement.dataset.loaded) {
        // Load budget from API
        loadBudget();
    }
}

/**
 * Load budget from API
 */
async function loadBudget() {
    try {
        const response = await fetch(`${API_BASE_URL}/budget`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            const budget = await response.json();
            // Store budget for later use
            localStorage.setItem('userBudget', budget.toString());
            // Update display will happen when summary is loaded
        } else {
            console.error('Failed to load budget:', response.status);
        }
    } catch (error) {
        console.error('Error loading budget:', error);
        // Don't throw error, just log it
    }
}

/**
 * Load summary data to update budget display on dashboard
 */
async function loadSummaryForBudget() {
    try {
        const response = await fetch(`${API_BASE_URL}/summary`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            const summary = await response.json();
            updateBudgetFromSummary(summary);
        } else {
            console.error('Failed to load summary for budget:', response.status);
        }
    } catch (error) {
        console.error('Error loading summary for budget:', error);
        // Don't throw error, just log it
    }
}

/**
 * Toggle filters panel
 */
function toggleFilters() {
    filtersVisible = !filtersVisible;
    filtersPanel.classList.toggle('active', filtersVisible);

    const toggleBtn = document.getElementById('toggleFilters');
    toggleBtn.innerHTML = filtersVisible ?
        '<i class="fas fa-times"></i> Hide Filters' :
        '<i class="fas fa-filter"></i> Filters';
}

/**
 * Switch between card and table view
 */
function switchView(view) {
    currentView = view;

    // Update button states
    document.getElementById('cardViewBtn').classList.toggle('active', view === 'card');
    document.getElementById('tableViewBtn').classList.toggle('active', view === 'table');

    // Re-display expenses in new view
    displayExpenses(allExpenses);
}

/**
 * Toggle theme
 */
function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute('data-theme');
    const newTheme = currentTheme === 'dark' ? 'light' : 'dark';

    document.documentElement.setAttribute('data-theme', newTheme);
    localStorage.setItem('theme', newTheme);

    const themeIcon = document.querySelector('#themeToggle i');
    themeIcon.className = newTheme === 'dark' ? 'fas fa-sun' : 'fas fa-moon';
}

/**
 * Handle keyboard shortcuts
 */
function handleKeyboardShortcuts(event) {
    // Ctrl/Cmd + N: New expense (focus amount field)
    if ((event.ctrlKey || event.metaKey) && event.key === 'n') {
        event.preventDefault();
        document.getElementById('amount').focus();
    }

    // Escape: Close modal or cancel edit
    if (event.key === 'Escape') {
        if (summaryModal.classList.contains('active')) {
            closeSummaryModal();
        } else if (currentEditingId) {
            cancelEdit();
        }
    }

    // Ctrl/Cmd + E: Export
    if ((event.ctrlKey || event.metaKey) && event.key === 'e') {
        event.preventDefault();
        exportToCsv();
    }

    // Ctrl/Cmd + D: Dashboard
    if ((event.ctrlKey || event.metaKey) && event.key === 'd') {
        event.preventDefault();
        showSummary();
    }
}

/**
 * Edit an expense
 */
async function editExpense(id) {
    try {
        showLoading(true);
        
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            const expense = await response.json();
            populateFormForEdit(expense);
        } else {
            throw new Error('Failed to load expense for editing');
        }
    } catch (error) {
        console.error('Error loading expense for edit:', error);
        showToast('Error loading expense for editing', 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Populate form with expense data for editing
 */
function populateFormForEdit(expense) {
    currentEditingId = expense.id;

    document.getElementById('amount').value = expense.amount;
    document.getElementById('category').value = expense.category;
    document.getElementById('expenseDate').value = expense.expenseDate;

    // Set payment method radio button
    const paymentMethodRadio = document.querySelector(`input[name="paymentMethod"][value="${expense.paymentMethod}"]`);
    if (paymentMethodRadio) {
        paymentMethodRadio.checked = true;
    }

    document.getElementById('upiVpa').value = expense.upiVpa || '';
    document.getElementById('transactionId').value = expense.transactionId || '';
    document.getElementById('payerName').value = expense.payerName || '';
    document.getElementById('notes').value = expense.notes || '';

    // Update UI
    document.getElementById('formTitle').innerHTML = '<i class="fas fa-edit"></i> Edit Expense';
    document.getElementById('submitBtnText').textContent = 'Update Expense';
    document.getElementById('cancelEditBtn').style.display = 'block';

    // Handle UPI fields
    handlePaymentMethodChange();

    // Scroll to form
    document.querySelector('.expense-form-card').scrollIntoView({ behavior: 'smooth' });
}

/**
 * Cancel editing
 */
function cancelEdit() {
    expenseForm.reset();
    handleFormReset();
}

/**
 * Delete an expense
 */
async function deleteExpense(id) {
    if (!confirm('Are you sure you want to delete this expense?')) {
        return;
    }

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });

        if (response.ok) {
            showToast('Expense deleted successfully!', 'success');
            loadExpenses();
        } else {
            throw new Error('Failed to delete expense');
        }
    } catch (error) {
        console.error('Error deleting expense:', error);
        showToast('Error deleting expense', 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Apply filters to expenses
 */
async function applyFilters() {
    const category = document.getElementById('filterCategory').value;
    const paymentMethod = document.getElementById('filterPaymentMethod').value;
    const startDate = document.getElementById('filterStartDate').value;
    const endDate = document.getElementById('filterEndDate').value;

    try {
        showLoading(true);

        const params = new URLSearchParams();
        if (category) params.append('category', category);
        if (paymentMethod) params.append('paymentMethod', paymentMethod);
        if (startDate) params.append('startDate', startDate);
        if (endDate) params.append('endDate', endDate);

        const response = await fetch(`${API_BASE_URL}?${params.toString()}`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            const filteredExpenses = await response.json();
            displayExpenses(filteredExpenses);
            updateExpenseStats(filteredExpenses);
        } else {
            throw new Error('Failed to apply filters');
        }
    } catch (error) {
        console.error('Error applying filters:', error);
        showToast('Error applying filters', 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Clear all filters
 */
function clearFilters() {
    document.getElementById('filterCategory').value = '';
    document.getElementById('filterPaymentMethod').value = '';
    document.getElementById('filterStartDate').value = '';
    document.getElementById('filterEndDate').value = '';

    loadExpenses();
}

/**
 * Export expenses to CSV
 */
async function exportToCsv() {
    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/export/csv`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            const csvContent = await response.text();
            const blob = new Blob([csvContent], { type: 'text/csv' });
            const url = window.URL.createObjectURL(blob);

            const a = document.createElement('a');
            a.href = url;
            a.download = `expenses_${new Date().toISOString().split('T')[0]}.csv`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);

            showToast('Expenses exported successfully!', 'success');
        } else {
            throw new Error('Failed to export expenses');
        }
    } catch (error) {
        console.error('Error exporting expenses:', error);
        showToast('Error exporting expenses', 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Show expense summary modal
 */
async function showSummary() {
    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/summary`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            const summary = await response.json();
            displaySummary(summary);
            summaryModal.classList.add('active');
        } else {
            throw new Error('Failed to load summary');
        }
    } catch (error) {
        console.error('Error loading summary:', error);
        showToast('Error loading summary', 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Display summary in modal
 */
function displaySummary(summary) {
    const summaryContent = document.getElementById('summaryContent');

    // Update budget display on dashboard
    updateBudgetFromSummary(summary);

    let categoryTotalsHtml = '';
    if (summary.categoryTotals) {
        for (const [category, total] of Object.entries(summary.categoryTotals)) {
            categoryTotalsHtml += `
                <div class="summary-item">
                    <span>${category}</span>
                    <span>₹${total.toFixed(2)}</span>
                </div>
            `;
        }
    }

    let paymentMethodTotalsHtml = '';
    if (summary.paymentMethodTotals) {
        for (const [method, total] of Object.entries(summary.paymentMethodTotals)) {
            paymentMethodTotalsHtml += `
                <div class="summary-item">
                    <span>${method}</span>
                    <span>₹${total.toFixed(2)}</span>
                </div>
            `;
        }
    }

    summaryContent.innerHTML = `
        <div class="summary-section">
            <h3>Overall Summary</h3>
            <div class="summary-grid">
                <div class="summary-card">
                    <h4>Total Amount</h4>
                    <p class="amount">₹${summary.totalAmount.toFixed(2)}</p>
                </div>
                <div class="summary-card">
                    <h4>Total Transactions</h4>
                    <p class="count">${summary.totalTransactions}</p>
                </div>
                <div class="summary-card">
                    <h4>Cash Total</h4>
                    <p class="amount">₹${summary.totalCashAmount.toFixed(2)}</p>
                </div>
                <div class="summary-card">
                    <h4>UPI Total</h4>
                    <p class="amount">₹${summary.totalUpiAmount.toFixed(2)}</p>
                </div>
                <div class="summary-card">
                    <h4>Budget</h4>
                    <p class="amount">₹${summary.budget ? summary.budget.toFixed(2) : '0.00'}</p>
                </div>
                <div class="summary-card">
                    <h4>Remaining Budget</h4>
                    <p class="amount">₹${summary.remainingBudget ? summary.remainingBudget.toFixed(2) : '0.00'}</p>
                </div>
            </div>
        </div>

        <div class="summary-section">
            <h3>Category Breakdown</h3>
            <div class="summary-list">
                ${categoryTotalsHtml}
            </div>
        </div>

        <div class="summary-section">
            <h3>Payment Method Breakdown</h3>
            <div class="summary-list">
                ${paymentMethodTotalsHtml}
            </div>
        </div>
    `;
}

/**
 * Update budget display from summary data
 */
function updateBudgetFromSummary(summary) {
    const budgetElement = document.getElementById('budgetAmount');
    const remainingBudgetElement = document.getElementById('remainingBudgetAmount');
    const budgetStatusElement = document.getElementById('budgetStatus');
    const remainingStatusElement = document.getElementById('remainingStatus');

    if (budgetElement && remainingBudgetElement && summary.budget !== undefined && summary.remainingBudget !== undefined) {
        const budget = parseFloat(summary.budget || 0);
        const remainingBudget = parseFloat(summary.remainingBudget);

        // Update budget amount card
        budgetElement.textContent = budget > 0 ? `₹${budget.toFixed(2)}` : '₹0.00';

        // Update remaining budget card
        remainingBudgetElement.textContent = `₹${remainingBudget.toFixed(2)}`;

        if (budget === 0) {
            budgetStatusElement.textContent = 'Set your budget';
            remainingStatusElement.textContent = 'No budget set';
            remainingBudgetElement.textContent = '₹0.00';
            remainingBudgetElement.style.color = ''; // Reset color
        } else if (remainingBudget >= 0) {
            budgetStatusElement.textContent = 'Active budget';
            remainingStatusElement.textContent = 'Within budget';
            remainingBudgetElement.style.color = ''; // Reset color
        } else {
            budgetStatusElement.textContent = 'Active budget';
            remainingStatusElement.textContent = `Over by ₹${Math.abs(remainingBudget).toFixed(2)}`;
            remainingBudgetElement.style.color = '#ef4444'; // Red color for over budget
        }

        budgetElement.dataset.loaded = 'true';
        remainingBudgetElement.dataset.loaded = 'true';
    }
}

/**
 * Close summary modal
 */
function closeSummaryModal() {
    summaryModal.classList.remove('active');
}

/**
 * Show budget modal
 */
async function showBudgetModal() {
    try {
        showLoading(true);

        // Load current budget
        const budgetResponse = await fetch(`${API_BASE_URL}/budget`, {
            headers: getAuthHeaders()
        });

        if (budgetResponse.ok) {
            const currentBudget = await budgetResponse.json();
            document.getElementById('budgetInputAmount').value = currentBudget > 0 ? currentBudget : '';
        }

        // Load current expenses for info display
        const summaryResponse = await fetch(`${API_BASE_URL}/summary`, {
            headers: getAuthHeaders()
        });

        if (summaryResponse.ok) {
            const summary = await summaryResponse.json();
            document.getElementById('currentExpenses').textContent = `₹${summary.totalAmount ? summary.totalAmount.toFixed(2) : '0.00'}`;
            document.getElementById('remainingBudget').textContent = `₹${summary.remainingBudget ? summary.remainingBudget.toFixed(2) : '0.00'}`;
            document.getElementById('budgetInfo').style.display = 'block';
        }

        budgetModal.classList.add('active');
    } catch (error) {
        console.error('Error loading budget data:', error);
        showToast('Error loading budget data', 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Close budget modal
 */
function closeBudgetModal() {
    budgetModal.classList.remove('active');
    budgetForm.reset();
    document.getElementById('budgetInfo').style.display = 'none';
}

/**
 * Handle budget form submission
 */
async function handleBudgetFormSubmit(event) {
    event.preventDefault();

    const budgetAmount = parseFloat(document.getElementById('budgetInputAmount').value);

    if (!budgetAmount || budgetAmount <= 0) {
        showToast('Please enter a valid budget amount greater than 0', 'error');
        return;
    }

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/budget`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${getAuthToken()}`,
                'Content-Type': 'text/plain'
            },
            body: budgetAmount.toString()
        });

        if (response.ok) {
            showToast('Budget updated successfully!', 'success');
            closeBudgetModal();
            // Reload expenses to update budget display
            loadExpenses();
        } else {
            throw new Error('Failed to update budget');
        }
    } catch (error) {
        console.error('Error updating budget:', error);
        showToast('Error updating budget. Please try again.', 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Clear budget
 */
async function clearBudget() {
    if (!confirm('Are you sure you want to clear your budget?')) {
        return;
    }

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/budget`, {
            method: 'PUT',
            headers: {
                'Authorization': `Bearer ${getAuthToken()}`,
                'Content-Type': 'text/plain'
            },
            body: '0'
        });

        if (response.ok) {
            showToast('Budget cleared successfully!', 'success');
            closeBudgetModal();
            // Reload expenses to update budget display
            loadExpenses();
        } else {
            throw new Error('Failed to clear budget');
        }
    } catch (error) {
        console.error('Error clearing budget:', error);
        showToast('Error clearing budget. Please try again.', 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Load categories from API and predefined list
 */
async function loadCategories() {
    // Predefined categories with icons
    const predefinedCategories = [
        'Food',
        'Travel',
        'Utilities',
        'Entertainment',
        'Healthcare',
        'Shopping',
        'Education',
        'Miscellaneous'
    ];

    try {
        // First, load predefined categories
        updateCategoryOptions(predefinedCategories);

        // Then try to load additional categories from API
        const response = await fetch(`${API_BASE_URL}/categories`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            const apiCategories = await response.json();

            // Merge predefined and API categories, removing duplicates
            const allCategories = [...new Set([...predefinedCategories, ...apiCategories])];
            updateCategoryOptions(allCategories);
        }
    } catch (error) {
        console.error('Error loading categories:', error);
        // Fallback to predefined categories
        updateCategoryOptions(predefinedCategories);
    }
}

/**
 * Update category options in select elements
 */
function updateCategoryOptions(categories) {
    const categorySelects = [
        document.getElementById('category'),
        document.getElementById('filterCategory')
    ];

    // Category icons mapping
    const categoryIcons = {
        'Food': '🍽️',
        'Travel': '✈️',
        'Utilities': '⚡',
        'Entertainment': '🎬',
        'Healthcare': '🏥',
        'Shopping': '🛍️',
        'Education': '📚',
        'Miscellaneous': '📦'
    };

    categorySelects.forEach(select => {
        if (!select) return; // Skip if element doesn't exist

        // Keep the first option (placeholder)
        const firstOption = select.firstElementChild;
        select.innerHTML = '';
        if (firstOption) {
            select.appendChild(firstOption);
        }

        // Add categories with icons
        categories.forEach(category => {
            const option = document.createElement('option');
            option.value = category;
            const icon = categoryIcons[category] || '📦';
            option.textContent = `${icon} ${category}`;
            select.appendChild(option);
        });
    });
}

/**
 * Show loading spinner
 */
function showLoading(show) {
    loadingSpinner.style.display = show ? 'flex' : 'none';
}

/**
 * Show toast notification
 */
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    const iconMap = {
        'success': 'check-circle',
        'error': 'exclamation-circle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };

    toast.innerHTML = `
        <i class="fas fa-${iconMap[type] || 'info-circle'}"></i>
        <div class="toast-message">${message}</div>
    `;

    toastContainer.appendChild(toast);

    // Show toast with animation
    requestAnimationFrame(() => {
        toast.classList.add('show');
    });

    // Auto-hide toast
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => {
            if (toast.parentNode) {
                toastContainer.removeChild(toast);
            }
        }, 300);
    }, 4000);

    // Click to dismiss
    toast.addEventListener('click', () => {
        toast.classList.remove('show');
        setTimeout(() => {
            if (toast.parentNode) {
                toastContainer.removeChild(toast);
            }
        }, 300);
    });
}

/**
 * Format date for display
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

/**
 * Logout user
 */
function logout() {
    if (confirm('Are you sure you want to logout?')) {
        // Clear stored authentication data
        localStorage.removeItem('authToken');
        localStorage.removeItem('username');

        // Clear auth cookie
        document.cookie = 'authToken=; path=/; max-age=0; samesite=strict';

        // Reset state
        isInitialized = false;
        isRedirecting = false;

        // Redirect to login page
        if (!isRedirecting) {
            isRedirecting = true;
            window.location.replace('/login.html');
        }
    }
}
