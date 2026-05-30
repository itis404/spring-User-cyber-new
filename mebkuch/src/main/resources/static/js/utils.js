
import { DEFAULT_IMAGE } from './config.js';

export function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, function(m) {
        if (m === '&') return '&amp;';
        if (m === '<') return '&lt;';
        if (m === '>') return '&gt;';
        return m;
    });
}

export function showLoader(containerId) {
    const el = document.getElementById(containerId);
    if (el) el.innerHTML = '<div class="loader">Загрузка...</div>';
}

export async function getProductImageUrl(productId) {
    try {
        const resp = await fetch(`${API_BASE}/product-image/product/${productId}`, {
            credentials: 'include'
        });
        if (resp.ok) {
            const images = await resp.json();
            const main = images.find(img => img.isMain) || images[0];
            if (main && main.imagePath) return main.imagePath;
        }
    } catch(e) {}
    return DEFAULT_IMAGE;
}

export async function renderProductCard(product) {
    const imgUrl = await getProductImageUrl(product.id);
    const finalPrice = product.minPrice * (1 - (product.discount || 0) / 100);
    const card = document.createElement('div');
    card.className = 'product-card';
    card.onclick = () => window.location.href = `/product.html?id=${product.id}`;
    card.innerHTML = `
        <img src="${imgUrl}" alt="${escapeHtml(product.name)}" onerror="this.src='${DEFAULT_IMAGE}'">
        <div class="product-info">
            <div class="product-name">${escapeHtml(product.name)}</div>
            <div class="product-price">
                ${finalPrice.toFixed(2)} ₽
                ${product.discount ? `<span class="discount-badge">-${product.discount}%</span>` : ''}
                ${product.discount ? `<span class="old-price">${product.minPrice.toFixed(2)} ₽</span>` : ''}
            </div>
        </div>
    `;
    return card;
}


export function validateEmail(email) {
    if (!email || email.trim() === '') {
        return { isValid: false, error: 'Email не может быть пустым' };
    }
    const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$/;
    if (!emailRegex.test(email.trim())) {
        return { isValid: false, error: 'Некорректный email (пример: name@domain.com)' };
    }
    return { isValid: true, error: null };
}

export function validateTelephone(telephone) {
    if (!telephone || telephone.trim() === '') {
        return { isValid: false, error: 'Телефон не может быть пустым' };
    }
    const phoneRegex = /^\+?[0-9]{10,15}$/;
    if (!phoneRegex.test(telephone.trim())) {
        return { isValid: false, error: 'Некорректный номер телефона. Используйте только цифры (10-15 символов), возможно с + в начале' };
    }
    return { isValid: true, error: null };
}

export function validatePassword(password) {
    if (!password || password.trim() === '') {
        return { isValid: false, error: 'Пароль не может быть пустым' };
    }
    if (password.length < 7) {
        return { isValid: false, error: 'Пароль должен быть не меньше 7 символов' };
    }
    return { isValid: true, error: null };
}

export function validateFullname(fullname) {
    if (!fullname || fullname.trim() === '') {
        return { isValid: false, error: 'ФИО не может быть пустым' };
    }
    if (fullname.trim().length < 2) {
        return { isValid: false, error: 'ФИО должно содержать хотя бы 2 символа' };
    }
    return { isValid: true, error: null };
}