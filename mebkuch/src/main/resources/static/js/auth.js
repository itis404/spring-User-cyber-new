// auth.js
import { API_BASE } from './config.js';
import { escapeHtml, validateEmail, validatePassword, validateFullname, validateTelephone } from './utils.js';

let authBox = null;
let currentUser = null;

export async function updateAuthUI() {
    authBox = document.getElementById('auth-box');
    if (!authBox) return;

    try {
        const resp = await fetch(`${API_BASE}/auth/user/about-me`, {
            method: 'GET',
            credentials: 'include'
        });

        if (resp.ok) {
            currentUser = await resp.json();
            authBox.innerHTML = `
                <div class="user-info">
                    <a href="/profile.html" class="user-name-link">${escapeHtml(currentUser.fullname || currentUser.email)}</a>
                    <button class="auth-link logout-btn" id="logout-btn">Выйти</button>
                </div>
            `;
            const logoutBtn = document.getElementById('logout-btn');
            if (logoutBtn) {
                logoutBtn.addEventListener('click', () => logout());
            }
        } else {
            authBox.innerHTML = `
                <div class="auth-buttons">
                    <button class="auth-link login-btn" id="show-login-btn">Вход</button>
                    <button class="auth-link register-btn" id="show-register-btn">Регистрация</button>
                </div>
            `;
            const loginBtn = document.getElementById('show-login-btn');
            const registerBtn = document.getElementById('show-register-btn');
            if (loginBtn) loginBtn.addEventListener('click', () => showAuthModal('login'));
            if (registerBtn) registerBtn.addEventListener('click', () => showAuthModal('register'));
        }
    } catch(e) {
        console.error('Auth error', e);
        authBox.innerHTML = `
            <div class="auth-buttons">
                <button class="auth-link login-btn">Вход</button>
                <button class="auth-link register-btn">Регистрация</button>
            </div>
        `;
        const loginBtn = document.getElementById('show-login-btn');
        const registerBtn = document.getElementById('show-register-btn');
        if (loginBtn) loginBtn.addEventListener('click', () => showAuthModal('login'));
        if (registerBtn) registerBtn.addEventListener('click', () => showAuthModal('register'));
    }
}

export async function logout() {
    try {
        await fetch(`${API_BASE}/auth/user/logout`, {
            method: 'POST',
            credentials: 'include'
        });
    } catch (err) {
        console.error('Logout error:', err);
    } finally {
        sessionStorage.removeItem('adminUser');
        window.location.href = '/';
    }
}

function showAuthModal(defaultTab = 'login') {
    const existingModal = document.getElementById('auth-modal');
    if (existingModal) existingModal.remove();

    const modal = document.createElement('div');
    modal.id = 'auth-modal';
    modal.className = 'modal';
    modal.innerHTML = `
        <div class="modal-content">
            <span class="close-modal">&times;</span>
            <div class="auth-tabs">
                <button class="auth-tab ${defaultTab === 'login' ? 'active' : ''}" data-tab="login">Вход</button>
                <button class="auth-tab ${defaultTab === 'register' ? 'active' : ''}" data-tab="register">Регистрация</button>
                <button class="auth-tab" data-tab="google">Google</button>
            </div>
            
            <div id="auth-login-form" class="auth-form ${defaultTab === 'login' ? 'active' : ''}">
                <h3>Вход</h3>
                <div class="form-group">
                    <input type="email" id="login-email" placeholder="Email">
                    <div id="login-email-error" class="error-message" style="display: none;"></div>
                </div>
                <div class="form-group">
                    <input type="password" id="login-password" placeholder="Пароль">
                    <div id="login-password-error" class="error-message" style="display: none;"></div>
                </div>
                <button id="do-login" class="auth-submit-btn">Войти</button>
            </div>
            
            <div id="auth-register-form" class="auth-form ${defaultTab === 'register' ? 'active' : ''}">
                <h3>Регистрация</h3>
                <div class="form-group">
                    <input type="text" id="register-fullname" placeholder="ФИО">
                    <div id="register-fullname-error" class="error-message" style="display: none;"></div>
                </div>
                <div class="form-group">
                    <input type="email" id="register-email" placeholder="Email">
                    <div id="register-email-error" class="error-message" style="display: none;"></div>
                </div>
                <div class="form-group">
                    <input type="text" id="register-phone" placeholder="Телефон">
                    <div id="register-phone-error" class="error-message" style="display: none;"></div>
                </div>
                <div class="form-group">
                    <input type="password" id="register-password" placeholder="Пароль">
                    <div id="register-password-error" class="error-message" style="display: none;"></div>
                </div>
                <button id="do-register" class="auth-submit-btn">Зарегистрироваться</button>
            </div>
            
            <div id="auth-google-form" class="auth-form ${defaultTab === 'google' ? 'active' : ''}">
                <h3>Вход через Google</h3>
                <p>Нажмите кнопку ниже для входа через Google аккаунт</p>
                <button id="do-google" class="auth-google-btn">
                    <span class="google-icon">G</span> Войти через Google
                </button>
            </div>
            
            <div id="auth-message" class="auth-message"></div>
        </div>
    `;

    document.body.appendChild(modal);

    const closeBtn = modal.querySelector('.close-modal');
    closeBtn.onclick = () => modal.remove();
    modal.onclick = (e) => { if (e.target === modal) modal.remove(); };

    const tabs = modal.querySelectorAll('.auth-tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const tabName = tab.dataset.tab;
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            modal.querySelectorAll('.auth-form').forEach(form => form.classList.remove('active'));
            const formId = `#auth-${tabName}-form`;
            const form = modal.querySelector(formId);
            if (form) form.classList.add('active');
            clearMessages(modal);
        });
    });

    const loginBtn = modal.querySelector('#do-login');
    loginBtn.addEventListener('click', async () => {
        const email = modal.querySelector('#login-email').value;
        const password = modal.querySelector('#login-password').value;

        const emailValidation = validateEmail(email);
        const passwordValidation = validatePassword(password);

        let isValid = true;

        if (!emailValidation.isValid) {
            showFieldError('login-email-error', emailValidation.error);
            isValid = false;
        } else {
            hideFieldError('login-email-error');
        }

        if (!passwordValidation.isValid) {
            showFieldError('login-password-error', passwordValidation.error);
            isValid = false;
        } else {
            hideFieldError('login-password-error');
        }

        if (!isValid) return;

        try {
            const resp = await fetch(`${API_BASE}/auth/user/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
                credentials: 'include'
            });

            if (!resp.ok) {
                const error = await resp.text();
                throw new Error(error || 'Неверный email или пароль');
            }

            showMessage(modal, 'Вход выполнен успешно!', 'success');
            setTimeout(() => {
                modal.remove();
                window.location.reload();
            }, 1000);
        } catch(err) {
            showMessage(modal, err.message, 'error');
        }
    });

    const registerBtn = modal.querySelector('#do-register');
    registerBtn.addEventListener('click', async () => {
        const fullname = modal.querySelector('#register-fullname').value;
        const email = modal.querySelector('#register-email').value;
        const phone = modal.querySelector('#register-phone').value;
        const password = modal.querySelector('#register-password').value;

        const fullnameValidation = validateFullname(fullname);
        const emailValidation = validateEmail(email);
        const phoneValidation = validateTelephone(phone);
        const passwordValidation = validatePassword(password);

        let isValid = true;

        if (!fullnameValidation.isValid) {
            showFieldError('register-fullname-error', fullnameValidation.error);
            isValid = false;
        } else {
            hideFieldError('register-fullname-error');
        }

        if (!emailValidation.isValid) {
            showFieldError('register-email-error', emailValidation.error);
            isValid = false;
        } else {
            hideFieldError('register-email-error');
        }

        if (!phoneValidation.isValid) {
            showFieldError('register-phone-error', phoneValidation.error);
            isValid = false;
        } else {
            hideFieldError('register-phone-error');
        }

        if (!passwordValidation.isValid) {
            showFieldError('register-password-error', passwordValidation.error);
            isValid = false;
        } else {
            hideFieldError('register-password-error');
        }

        if (!isValid) return;

        try {
            const resp = await fetch(`${API_BASE}/user/registration/register-with-password`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ fullname, email, telephoneNumber: phone, password }),
                credentials: 'include'
            });

            if (!resp.ok) {
                const error = await resp.text();
                throw new Error(error || 'Ошибка регистрации');
            }

            showMessage(modal, 'Регистрация успешна!', 'success');
            setTimeout(() => {
                modal.remove();
                window.location.reload();
            }, 1000);
        } catch(err) {
            showMessage(modal, err.message, 'error');
        }
    });

    const googleBtn = modal.querySelector('#do-google');
    googleBtn.addEventListener('click', () => {
        window.location.href = `${API_BASE}/auth/google`;
    });
}

function showFieldError(elementId, message) {
    const errorDiv = document.getElementById(elementId);
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
        const input = errorDiv.parentElement?.querySelector('input');
        if (input) input.classList.add('error');
    }
}

function hideFieldError(elementId) {
    const errorDiv = document.getElementById(elementId);
    if (errorDiv) {
        errorDiv.textContent = '';
        errorDiv.style.display = 'none';
        const input = errorDiv.parentElement?.querySelector('input');
        if (input) input.classList.remove('error');
    }
}

function showMessage(modal, message, type) {
    const messageDiv = modal.querySelector('#auth-message');
    if (messageDiv) {
        messageDiv.textContent = message;
        messageDiv.className = `auth-message ${type}`;
        setTimeout(() => {
            messageDiv.textContent = '';
            messageDiv.className = 'auth-message';
        }, 3000);
    }
}

function clearMessages(modal) {
    const messageDiv = modal.querySelector('#auth-message');
    if (messageDiv) {
        messageDiv.textContent = '';
        messageDiv.className = 'auth-message';
    }
}

function showToast(message, type) {
    const messageDiv = document.createElement('div');
    messageDiv.className = `temporary-message ${type}`;
    messageDiv.textContent = message;
    messageDiv.style.cssText = `
        position: fixed;
        bottom: 20px;
        right: 20px;
        padding: 12px 20px;
        border-radius: 10px;
        background: ${type === 'success' ? '#38a169' : '#e53e3e'};
        color: white;
        z-index: 1000;
        animation: slideIn 0.3s ease;
    `;
    document.body.appendChild(messageDiv);
    setTimeout(() => messageDiv.remove(), 3000);
}