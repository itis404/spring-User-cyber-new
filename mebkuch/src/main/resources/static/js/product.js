import { API_BASE, DEFAULT_IMAGE } from './config.js';
import { renderProductCard, escapeHtml } from './utils.js';

let currentUserId = null;
let isFavourite = false;
let currentProductId = null;

export async function initProduct() {
    const urlParams = new URLSearchParams(window.location.search);
    const productId = urlParams.get('id');
    if (!productId) {
        document.querySelector('.product-details').innerHTML = '<p>Товар не указан</p>';
        return;
    }
    currentProductId = productId;

    await loadCurrentUserId();

    try {
        const resp = await fetch(`${API_BASE}/product/${productId}`, { credentials: 'include' });
        if (!resp.ok) throw new Error();
        const product = await resp.json();
        await renderProductDetail(product);
        await loadSimilarProducts(productId);

        if (currentUserId) {
            await checkFavouriteStatus(productId);
            addFavouriteButton(productId);
        } else {
            addFavouriteButtonForGuest(productId);
        }
    } catch(e) {
        document.querySelector('.product-details').innerHTML = '<p>Ошибка загрузки товара</p>';
    }
}

async function loadCurrentUserId() {
    try {
        const resp = await fetch(`${API_BASE}/auth/user/about-me`, {
            method: 'GET',
            credentials: 'include'
        });

        console.log('Step 1 - Get user info response:', resp.status);

        if (resp.ok) {
            const user = await resp.json();
            const email = user.email;
            console.log('Email получен:', email);

            if (email) {
                const params = new URLSearchParams({ mail: email });
                const idResp = await fetch(`${API_BASE}/user/profile?${params}`, {
                    method: 'GET',
                    credentials: 'include'
                });

                console.log('Step 2 - Get user ID response:', idResp.status);

                if (idResp.ok) {
                    currentUserId = await idResp.json();
                    console.log('User ID получен:', currentUserId);
                } else {
                    console.log('Не удалось получить ID пользователя');
                }
            }
        } else {
            console.log('Пользователь не авторизован');
        }
    } catch (err) {
        console.error('Error loading user ID:', err);
    }
}

async function checkFavouriteStatus(productId) {
    if (!currentUserId) {
        isFavourite = false;
        return;
    }

    try {
        const params = new URLSearchParams({
            productId: productId,
            userId: currentUserId
        });

        const resp = await fetch(`${API_BASE}/favourites?${params}`, {
            credentials: 'include'
        });

        isFavourite = resp.ok;
        console.log('Favourite status:', isFavourite);
    } catch (err) {
        console.error('Error checking favourite status:', err);
        isFavourite = false;
    }
}

function addFavouriteButton(productId) {
    const infoContainer = document.querySelector('.info');
    if (!infoContainer) return;

    if (document.querySelector('.favourite-btn')) return;

    const favouriteBtn = document.createElement('button');
    favouriteBtn.className = `favourite-btn ${isFavourite ? 'active' : ''}`;
    favouriteBtn.innerHTML = isFavourite ? ' В избранном' : ' В избранное';
    favouriteBtn.style.cssText = `
        margin-top: 20px;
        padding: 12px 24px;
        border-radius: 40px;
        border: 1px solid var(--border);
        background: ${isFavourite ? '#fee' : 'white'};
        color: ${isFavourite ? '#e53e3e' : '#1e2a3a'};
        cursor: pointer;
        font-weight: 600;
        transition: all 0.2s;
        width: 100%;
        max-width: 220px;
        font-size: 0.95rem;
    `;

    favouriteBtn.addEventListener('mouseenter', () => {
        if (!isFavourite) {
            favouriteBtn.style.background = '#f5f5f5';
        } else {
            favouriteBtn.style.background = '#fdd';
        }
    });

    favouriteBtn.addEventListener('mouseleave', () => {
        if (!isFavourite) {
            favouriteBtn.style.background = 'white';
        } else {
            favouriteBtn.style.background = '#fee';
        }
    });

    favouriteBtn.addEventListener('click', async () => {
        if (!currentUserId) {
            showToast('Пожалуйста, войдите в систему', 'error');
            setTimeout(() => {
                window.location.href = '/';
            }, 1500);
            return;
        }
        await toggleFavourite(productId, favouriteBtn);
    });

    const priceBlock = document.querySelector('.price');
    if (priceBlock) {
        priceBlock.insertAdjacentElement('afterend', favouriteBtn);
    } else {
        infoContainer.appendChild(favouriteBtn);
    }
}

function addFavouriteButtonForGuest(productId) {
    const infoContainer = document.querySelector('.info');
    if (!infoContainer) return;

    if (document.querySelector('.favourite-btn')) return;

    const favouriteBtn = document.createElement('button');
    favouriteBtn.className = 'favourite-btn guest';
    favouriteBtn.innerHTML = ' Войти, чтобы добавить в избранное';
    favouriteBtn.style.cssText = `
        margin-top: 20px;
        padding: 12px 24px;
        border-radius: 40px;
        border: 1px solid var(--border);
        background: #f5f5f5;
        color: #999;
        cursor: pointer;
        font-weight: 600;
        transition: all 0.2s;
        width: 100%;
        max-width: 280px;
        font-size: 0.9rem;
    `;

    favouriteBtn.addEventListener('click', () => {
        showToast('Пожалуйста, войдите в систему', 'error');
        setTimeout(() => {
            window.location.href = '/';
        }, 1500);
    });

    const priceBlock = document.querySelector('.price');
    if (priceBlock) {
        priceBlock.insertAdjacentElement('afterend', favouriteBtn);
    } else {
        infoContainer.appendChild(favouriteBtn);
    }
}

async function toggleFavourite(productId, button) {
    if (!currentUserId) {
        showToast('Пожалуйста, войдите в систему', 'error');
        return;
    }

    const originalText = button.innerHTML;
    button.innerHTML = '⏳ Загрузка...';
    button.disabled = true;

    try {
        if (isFavourite) {
            const params = new URLSearchParams({
                productId: productId,
                userId: currentUserId
            });

            const resp = await fetch(`${API_BASE}/favourites?${params}`, {
                method: 'DELETE',
                credentials: 'include'
            });

            if (resp.ok) {
                isFavourite = false;
                button.innerHTML = '🤍 В избранное';
                button.style.background = 'white';
                button.style.color = '#1e2a3a';
                showToast('Товар удалён из избранного', 'success');
            } else {
                throw new Error('Ошибка при удалении');
            }
        } else {
            const body = {
                productId: productId,
                userId: currentUserId
            };

            const resp = await fetch(`${API_BASE}/favourites`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(body),
                credentials: 'include'
            });

            if (resp.ok) {
                isFavourite = true;
                button.innerHTML = '❤️ В избранном';
                button.style.background = '#fee';
                button.style.color = '#e53e3e';
                showToast('Товар добавлен в избранное', 'success');
            } else {
                throw new Error('Ошибка при добавлении');
            }
        }
    } catch (err) {
        console.error('Error toggling favourite:', err);
        showToast('Ошибка при изменении избранного', 'error');
        button.innerHTML = originalText;
    } finally {
        button.disabled = false;
    }
}

async function renderProductDetail(product) {
    document.getElementById('product-name').innerText = product.name;
    const finalPrice = product.minPrice * (1 - (product.discount || 0) / 100);
    document.getElementById('product-price').innerHTML = `${finalPrice.toFixed(2)} ₽ ${product.discount ? `<span class="discount-badge">-${product.discount}%</span>` : ''}`;
    document.getElementById('product-desc').innerText = product.description || 'Нет описания';

    const imagesResp = await fetch(`${API_BASE}/product-image/product/${product.id}`, { credentials: 'include' });
    let images = [];
    if (imagesResp.ok) images = await imagesResp.json();
    const mainImg = images.find(i => i.isMain) || images[0];
    const mainImgEl = document.getElementById('main-image');
    if (mainImg) mainImgEl.src = mainImg.imagePath;
    else mainImgEl.src = DEFAULT_IMAGE;
    const thumbContainer = document.getElementById('product-thumbnails');
    if (thumbContainer) {
        thumbContainer.innerHTML = images.map(img => `<img src="${img.imagePath}" data-img="${img.imagePath}" class="${img.id === mainImg?.id ? 'active' : ''}">`).join('');
        thumbContainer.querySelectorAll('img').forEach(thumb => {
            thumb.addEventListener('click', () => {
                mainImgEl.src = thumb.dataset.img;
                thumbContainer.querySelectorAll('img').forEach(t => t.classList.remove('active'));
                thumb.classList.add('active');
            });
        });
    }

    const attrList = document.getElementById('product-attributes');
    if (attrList && product.attributeValues && product.attributeValues.length) {
        const allAttrsResp = await fetch(`${API_BASE}/attributes`);
        const allAttrs = await allAttrsResp.json();
        const attrMap = new Map(allAttrs.map(a => [a.id, a.name]));
        const valuesPromises = product.attributeValues.map(async avId => {
            const valResp = await fetch(`${API_BASE}/attribute-values/${avId}`, { credentials: 'include' });
            if (valResp.ok) {
                const val = await valResp.json();
                let displayValue = '';
                if (val.valueText) displayValue = val.valueText;
                else if (val.valueNumber) displayValue = val.valueNumber;
                else if (val.valueBoolean) displayValue = val.valueBoolean ? 'Да' : 'Нет';
                return { attrName: attrMap.get(val.attributeId) || 'Атрибут', value: displayValue };
            }
            return null;
        });
        const values = (await Promise.all(valuesPromises)).filter(v => v && v.value);
        attrList.innerHTML = values.map(v => `<li><strong>${escapeHtml(v.attrName)}</strong>: ${escapeHtml(v.value)}</li>`).join('');
    } else {
        attrList.innerHTML = '<li>Нет характеристик</li>';
    }

    const compList = document.getElementById('product-components');
    if (compList && product.components && product.components.length) {
        const compHtml = await Promise.all(product.components.map(async compId => {
            try {
                const resp = await fetch(`${API_BASE}/component/${compId}`, { credentials: 'include' });
                if (resp.ok) {
                    const comp = await resp.json();
                    return `<li>${escapeHtml(comp.name)} — ${comp.cost} ₽</li>`;
                }
            } catch(e) {}
            return '';
        }));
        compList.innerHTML = compHtml.join('');
    } else {
        compList.innerHTML = '<li>Компоненты не указаны</li>';
    }
}

async function loadSimilarProducts(productId) {
    const container = document.getElementById('similar-products');
    if (!container) return;
    try {
        const resp = await fetch(`${API_BASE}/product/similar/${productId}`, { credentials: 'include' });
        if (!resp.ok) throw new Error();
        const products = await resp.json();
        container.innerHTML = '';
        if (products.length === 0) {
            container.innerHTML = '<p>Нет похожих товаров</p>';
            return;
        }
        for (const prod of products) {
            const card = await renderProductCard(prod);
            container.appendChild(card);
        }
    } catch(e) {
        container.innerHTML = '<p>Нет похожих товаров</p>';
    }
}

function showToast(message, type) {
    const toast = document.createElement('div');
    toast.className = `temporary-message ${type}`;
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        bottom: 20px;
        right: 20px;
        padding: 12px 20px;
        border-radius: 10px;
        background: ${type === 'success' ? '#38a169' : '#e53e3e'};
        color: white;
        z-index: 1000;
        animation: slideIn 0.3s ease;
        font-weight: 500;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    `;
    document.body.appendChild(toast);
    setTimeout(() => {
        toast.remove();
    }, 3000);
}