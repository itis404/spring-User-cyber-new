import { API_BASE } from './config.js';
import { escapeHtml, validateTelephone } from './utils.js';

let currentUser = null;
let currentUserId = null;

export async function initProfile() {
    await loadUserProfile();
}

async function loadUserProfile() {
    const container = document.getElementById('user-info');
    if (!container) return;

    try {
        const resp = await fetch(`${API_BASE}/auth/user/about-me`, {
            method: 'GET',
            credentials: 'include'
        });

        console.log('Profile page - response status:', resp.status);

        if (!resp.ok) {
            console.log('Not authorized, redirecting to home');
            window.location.href = '/';
            return;
        }

        currentUser = await resp.json();
        console.log('Current user in profile:', currentUser);

        if (currentUser && currentUser.email) {
            await loadUserIdByEmail(currentUser.email);
        }

        if (currentUser.role === 'ADMIN' || currentUser.role === 'ROLE_ADMIN') {
            console.log('User is ADMIN, redirecting to admin panel');
            window.location.href = '/admin/admin.html';
            return;
        }

        console.log('User is regular user, showing profile');

        const initials = getInitials(currentUser.fullname || currentUser.email);
        const avatarInitials = document.getElementById('avatar-initials');
        if (avatarInitials) avatarInitials.textContent = initials;

        container.innerHTML = `
            <div class="info-row">
                <div class="info-label">ФИО:</div>
                <div class="info-value">${escapeHtml(currentUser.fullname || 'Не указано')}</div>
            </div>
            <div class="info-row">
                <div class="info-label">Email:</div>
                <div class="info-value">${escapeHtml(currentUser.email || 'Не указан')}</div>
            </div>
            <div class="info-row">
                <div class="info-label">Телефон:</div>
                <div class="info-value" id="phone-value">${escapeHtml(currentUser.telephoneNumber || 'Не указан')}</div>
            </div>
        `;

        setupEventListeners();
        loadOrders();

        setupFavoritesTab();

    } catch (err) {
        console.error('Error loading profile:', err);
        window.location.href = '/';
    }
}

async function loadUserIdByEmail(email) {
    if (!email) return null;

    try {
        const params = new URLSearchParams({ mail: email });
        const resp = await fetch(`${API_BASE}/user/profile?${params}`, {
            method: 'GET',
            credentials: 'include'
        });

        console.log('Get user ID response status:', resp.status);

        if (resp.ok) {
            currentUserId = await resp.json();
            console.log('User ID loaded by email in profile:', currentUserId);
            return currentUserId;
        }
    } catch (err) {
        console.error('Error loading user ID by email:', err);
    }
    return null;
}

function getInitials(name) {
    if (!name) return '?';
    const parts = name.trim().split(' ');
    if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
}

function setupFavoritesTab() {
    const favoritesTab = document.querySelector('[data-tab="favorites"]');
    if (favoritesTab) {
        favoritesTab.addEventListener('click', async () => {
            await loadFavorites();
        });
    }
}

async function loadFavorites() {
    const container = document.getElementById('favorites-list');
    if (!container) return;

    const userId = currentUserId;

    console.log('Loading favourites for user ID:', userId);

    if (!userId) {
        container.innerHTML = '<div class="empty-message">Ошибка: пользователь не найден</div>';
        return;
    }

    container.innerHTML = '<div class="info-loading">Загрузка избранного...</div>';

    try {
        const resp = await fetch(`${API_BASE}/favourites/user/${userId}`, {
            method: 'GET',
            credentials: 'include'
        });

        console.log('Favourites response status:', resp.status);

        if (!resp.ok) {
            throw new Error('Ошибка загрузки избранного');
        }

        const favourites = await resp.json();
        console.log('Favourites loaded:', favourites);

        if (favourites.length === 0) {
            container.innerHTML = `
                <div class="empty-message">
                    <p>У вас пока нет избранных товаров</p>
                    <a href="/catalog.html" class="edit-profile-btn" style="display: inline-block; margin-top: 16px;">Перейти в каталог</a>
                </div>
            `;
            return;
        }

        container.innerHTML = '';

        for (const fav of favourites) {
            try {
                const productResp = await fetch(`${API_BASE}/product/${fav.productId}`, {
                    credentials: 'include'
                });

                if (productResp.ok) {
                    const product = await productResp.json();
                    const card = await createFavouriteCard(product);
                    container.appendChild(card);
                } else {
                    console.error('Failed to load product:', fav.productId);
                }
            } catch (err) {
                console.error('Error loading product:', err);
            }
        }

    } catch (err) {
        console.error('Error loading favourites:', err);
        container.innerHTML = '<div class="empty-message">Ошибка загрузки избранного</div>';
    }
}

async function createFavouriteCard(product) {
    const card = document.createElement('div');
    card.className = 'favourite-card';

    let imageUrl = 'https://via.placeholder.com/100x100?text=No+image';
    try {
        const imgResp = await fetch(`${API_BASE}/product-image/product/${product.id}`, {
            credentials: 'include'
        });
        if (imgResp.ok) {
            const images = await imgResp.json();
            if (images.length > 0 && images[0].imagePath) {
                imageUrl = images[0].imagePath;
            }
        }
    } catch(e) {
        console.error('Error loading product image:', e);
    }

    const finalPrice = product.minPrice * (1 - (product.discount || 0) / 100);

    card.innerHTML = `
        <div class="favourite-card-content" data-product-id="${product.id}" style="display: flex; gap: 16px; cursor: pointer;">
            <img src="${imageUrl}" alt="${escapeHtml(product.name)}" style="width: 100px; height: 100px; object-fit: cover; border-radius: 12px;" onerror="this.src='https://via.placeholder.com/100x100?text=No+image'">
            <div style="flex: 1;">
                <h3 class="favourite-product-name" style="margin: 0 0 8px 0; font-size: 1.1rem;">${escapeHtml(product.name)}</h3>
                <div class="favourite-product-price" style="font-size: 1.2rem; font-weight: bold; color: #1e2a3a;">${finalPrice.toFixed(2)} ₽</div>
                ${product.discount ? `<div class="favourite-product-discount" style="color: #e53e3e; font-size: 0.85rem;">Скидка: -${product.discount}%</div>` : ''}
            </div>
            <button class="remove-favourite-btn" data-product-id="${product.id}" style="align-self: flex-start; background: #fee; border: none; color: #e53e3e; padding: 6px 12px; border-radius: 20px; cursor: pointer; font-size: 0.8rem;">Удалить</button>
        </div>
    `;

    const cardContent = card.querySelector('.favourite-card-content');
    cardContent.addEventListener('click', (e) => {
        if (e.target.classList.contains('remove-favourite-btn')) return;
        window.location.href = `/product.html?id=${product.id}`;
    });

    const removeBtn = card.querySelector('.remove-favourite-btn');
    removeBtn.addEventListener('click', async (e) => {
        e.stopPropagation();
        await removeFromFavourites(product.id);
    });

    return card;
}

async function removeFromFavourites(productId) {
    const userId = currentUserId;

    if (!userId) {
        showToast('Ошибка: пользователь не найден', 'error');
        return;
    }

    try {
        const params = new URLSearchParams({
            productId: productId,
            userId: userId
        });

        const resp = await fetch(`${API_BASE}/favourites?${params}`, {
            method: 'DELETE',
            credentials: 'include'
        });

        if (!resp.ok) {
            throw new Error('Ошибка удаления из избранного');
        }

        showToast('Товар удалён из избранного', 'success');
        await loadFavorites();
    } catch (err) {
        console.error('Error removing from favourites:', err);
        showToast(err.message || 'Ошибка при удалении', 'error');
    }
}

function setupEventListeners() {
    const navBtns = document.querySelectorAll('.profile-nav-btn');
    navBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const tabId = btn.dataset.tab;
            navBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            document.querySelectorAll('.profile-tab').forEach(tab => tab.classList.remove('active'));
            const activeTab = document.getElementById(`tab-${tabId}`);
            if (activeTab) activeTab.classList.add('active');
        });
    });

    const editBtn = document.getElementById('edit-profile-btn');
    const editForm = document.getElementById('edit-form');
    const cancelBtn = document.getElementById('cancel-edit-btn');
    const saveBtn = document.getElementById('save-profile-btn');
    const phoneInput = document.getElementById('edit-phone');
    const phoneError = document.getElementById('phone-error');

    if (editBtn) {
        editBtn.addEventListener('click', () => {
            editForm.classList.remove('hidden');
            editBtn.style.display = 'none';
            if (phoneInput) {
                phoneInput.value = currentUser?.telephoneNumber || '';
            }
            if (phoneError) {
                phoneError.style.display = 'none';
                phoneError.textContent = '';
                phoneInput.classList.remove('error');
            }
        });
    }

    if (cancelBtn) {
        cancelBtn.addEventListener('click', () => {
            editForm.classList.add('hidden');
            editBtn.style.display = 'inline-block';
            if (phoneError) {
                phoneError.style.display = 'none';
                phoneInput.classList.remove('error');
            }
        });
    }

    if (phoneInput) {
        phoneInput.addEventListener('input', () => {
            const validation = validateTelephone(phoneInput.value);
            if (!validation.isValid) {
                phoneInput.classList.add('error');
                phoneError.textContent = validation.error;
                phoneError.style.display = 'block';
            } else {
                phoneInput.classList.remove('error');
                phoneError.style.display = 'none';
            }
        });
    }

    if (saveBtn) {
        saveBtn.addEventListener('click', async () => {
            const phone = phoneInput?.value.trim() || '';

            const validation = validateTelephone(phone);
            if (!validation.isValid) {
                phoneInput.classList.add('error');
                phoneError.textContent = validation.error;
                phoneError.style.display = 'block';
                return;
            }

            if (!currentUser || !currentUser.email) {
                showToast('Ошибка: пользователь не найден', 'error');
                return;
            }

            const originalText = saveBtn.textContent;
            saveBtn.textContent = 'Сохранение...';
            saveBtn.disabled = true;

            try {
                const params = new URLSearchParams({
                    mail: currentUser.email,
                    telephone: phone
                });

                const resp = await fetch(`${API_BASE}/user/profile?${params}`, {
                    method: 'PATCH',
                    credentials: 'include'
                });

                if (!resp.ok) {
                    const error = await resp.text();
                    throw new Error(error || 'Ошибка обновления телефона');
                }

                const updatedUser = await resp.json();
                currentUser = updatedUser;

                const phoneValue = document.getElementById('phone-value');
                if (phoneValue) {
                    phoneValue.textContent = escapeHtml(updatedUser.telephoneNumber || 'Не указан');
                }

                editForm.classList.add('hidden');
                editBtn.style.display = 'inline-block';

                showToast('Телефон успешно обновлён!', 'success');
            } catch (err) {
                console.error('Error updating phone:', err);
                showToast(err.message || 'Ошибка обновления телефона', 'error');
            } finally {
                saveBtn.textContent = originalText;
                saveBtn.disabled = false;
            }
        });
    }
}

function loadOrders() {
    const container = document.getElementById('orders-list');
    if (container) {
        container.innerHTML = `
            <div class="empty-message">
                <p>У вас пока нет заказов</p>
                <a href="/catalog.html" class="edit-profile-btn" style="display: inline-block; margin-top: 16px;">Перейти в каталог</a>
            </div>
        `;
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