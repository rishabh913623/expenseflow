/**
 * Authentication JavaScript for Expense Tracker
 * Handles login, registration, and token management
 */

// API Base URL
const API_BASE_URL = '/api/auth';

// DOM Elements
const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');
const toggleLink = document.getElementById('toggleLink');
const toggleText = document.getElementById('toggleText');
const formTitle = document.getElementById('formTitle');
const loadingSpinner = document.getElementById('loadingSpinner');
const toastContainer = document.getElementById('toastContainer');

// Global state
let isValidatingToken = false;
let isRedirecting = false;

// Initialize application
document.addEventListener('DOMContentLoaded', function() {
    setupEventListeners();
    checkExistingToken();
});

/**
 * Setup all event listeners
 */
function setupEventListeners() {
    // Login form submission
    loginForm.addEventListener('submit', handleLogin);

    // Register form submission
    registerForm.addEventListener('submit', handleRegister);

    // Toggle between login and register
    toggleLink.addEventListener('click', toggleAuthMode);
}

/**
 * Check if user already has a valid token with debouncing and loop prevention
 */
function checkExistingToken() {
    // Prevent multiple simultaneous checks
    if (isValidatingToken || isRedirecting) {
        return;
    }

    const token = localStorage.getItem('authToken');
    const currentPath = window.location.pathname;

    if (token && !isValidatingToken) {
        isValidatingToken = true;

        // Validate token with server (with timeout)
        Promise.race([
            validateToken(token),
            new Promise((_, reject) =>
                setTimeout(() => reject(new Error('Token validation timeout')), 3000)
            )
        ]).then(isValid => {
            if (isValid) {
                // Only redirect if not already on dashboard
                if (currentPath !== '/dashboard' && !isRedirecting) {
                    isRedirecting = true;
                    window.location.replace('/dashboard');
                }
            } else {
                // Clear invalid token only if we're not in a redirect loop
                if (currentPath !== '/login.html') {
                    localStorage.removeItem('authToken');
                    localStorage.removeItem('username');
                }
            }
        }).catch(error => {
            console.error('Token validation failed:', error);
            // Clear potentially invalid token
            localStorage.removeItem('authToken');
            localStorage.removeItem('username');
        }).finally(() => {
            isValidatingToken = false;
        });
    }
}

/**
 * Handle login form submission
 */
async function handleLogin(event) {
    event.preventDefault();

    const formData = new FormData(loginForm);
    const loginData = {
        username: formData.get('username'),
        password: formData.get('password')
    };

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(loginData)
        });

        const result = await response.json();

        if (response.ok && result.token) {
            // Store token and user info in localStorage
            localStorage.setItem('authToken', result.token);
            localStorage.setItem('username', result.username);

            // Also store token in cookie for server-side authentication
            document.cookie = `authToken=${result.token}; path=/; max-age=604800; samesite=strict`;

            showToast('Login successful! Redirecting...', 'success');

            // Redirect to dashboard after a short delay (prevent loops)
            setTimeout(() => {
                if (!isRedirecting) {
                    isRedirecting = true;
                    window.location.replace('/dashboard');
                }
            }, 1000);
        } else {
            showToast(result.message || 'Login failed', 'error');
        }
    } catch (error) {
        console.error('Login error:', error);
        showToast('Network error. Please try again.', 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Handle register form submission
 */
async function handleRegister(event) {
    event.preventDefault();

    const formData = new FormData(registerForm);
    const password = formData.get('password');
    const confirmPassword = formData.get('confirmPassword');

    // Validate password confirmation
    if (password !== confirmPassword) {
        showToast('Passwords do not match', 'error');
        return;
    }

    const registerData = {
        username: formData.get('username'),
        email: formData.get('email'),
        password: password
    };

    try {
        showLoading(true);

        const response = await fetch(`${API_BASE_URL}/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(registerData)
        });

        const result = await response.json();

        if (response.ok && result.token) {
            // Store token and user info in localStorage
            localStorage.setItem('authToken', result.token);
            localStorage.setItem('username', result.username);

            // Also store token in cookie for server-side authentication
            document.cookie = `authToken=${result.token}; path=/; max-age=604800; samesite=strict`;

            showToast('Account created successfully! Redirecting...', 'success');

            // Redirect to dashboard after a short delay (prevent loops)
            setTimeout(() => {
                if (!isRedirecting) {
                    isRedirecting = true;
                    window.location.replace('/dashboard');
                }
            }, 1000);
        } else {
            showToast(result.message || 'Registration failed', 'error');
        }
    } catch (error) {
        console.error('Registration error:', error);
        showToast('Network error. Please try again.', 'error');
    } finally {
        showLoading(false);
    }
}

/**
 * Toggle between login and register modes
 */
function toggleAuthMode(event) {
    event.preventDefault();

    const isLoginMode = loginForm.style.display !== 'none';

    if (isLoginMode) {
        // Switch to register mode
        loginForm.style.display = 'none';
        registerForm.style.display = 'block';
        formTitle.textContent = 'Create Account';
        toggleText.innerHTML = 'Already have an account? <a href="#" id="toggleLink">Sign in</a>';
    } else {
        // Switch to login mode
        registerForm.style.display = 'none';
        loginForm.style.display = 'block';
        formTitle.textContent = 'Welcome Back';
        toggleText.innerHTML = 'Don\'t have an account? <a href="#" id="toggleLink">Create one</a>';
    }

    // Re-attach event listener to the new toggle link
    const newToggleLink = document.getElementById('toggleLink');
    newToggleLink.addEventListener('click', toggleAuthMode);
}

/**
 * Validate JWT token with server (cached to prevent repeated calls)
 */
async function validateToken(token) {
    // Prevent multiple simultaneous validation calls
    if (isValidatingToken) {
        return new Promise((resolve) => {
            // Wait for current validation to complete
            const checkInterval = setInterval(() => {
                if (!isValidatingToken) {
                    clearInterval(checkInterval);
                    resolve(localStorage.getItem('authToken') === token);
                }
            }, 100);
        });
    }

    try {
        isValidatingToken = true;

        const response = await fetch(`${API_BASE_URL}/validate`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            },
            // Add timeout to prevent hanging requests
            signal: AbortSignal.timeout(3000)
        });

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
        if (error.name === 'AbortError' || error.name === 'TypeError') {
            localStorage.removeItem('authToken');
            localStorage.removeItem('username');
        }
        return false;
    } finally {
        isValidatingToken = false;
    }
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