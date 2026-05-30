
import { API_BASE } from './config.js';
import { showLoader, renderProductCard, escapeHtml } from './utils.js';
import { updateAuthUI } from './auth.js';

let currentPage = 0;
let pageSize = 12;
let sectionId = null;
let sectionName = '';


document.addEventListener('DOMContentLoaded', async () => {
    await updateAuthUI();

    const urlParams = new URLSearchParams(window.location.search);
    sectionId = urlParams.get('sectionId');
    sectionName = urlParams.get('sectionName');

    if (!sectionId) {
        document.querySelector('.section-header').innerHTML = '<h1>Ошибка: секция не указана</h1>';
        return;
    }

    document.getElementById('section-name').innerText = sectionName ? escapeHtml(sectionName) : 'Товары секции';

    await loadSectionProducts();


    document.getElementById('pagination')?.addEventListener('click', (e) => {
        if (e.target.tagName === 'BUTTON' && e.target.dataset.page) {
            currentPage = parseInt(e.target.dataset.page);
            loadSectionProducts();
        }
    });
});

async function loadSectionProducts() {
    const container = document.getElementById('section-products');
    if (!container) return;
    showLoader('section-products');

    const params = new URLSearchParams({
        page: currentPage,
        size: pageSize,
        sort: 'id,desc'
    });

    try {
        const resp = await fetch(`${API_BASE}/product-section/${sectionId}?${params}`, {
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка загрузки товаров секции');
        const data = await resp.json();

        container.innerHTML = '';
        for (const product of data.content) {
            const card = await renderProductCard(product);
            container.appendChild(card);
        }
        renderPagination(data);
    } catch (err) {
        console.error(err);
        container.innerHTML = '<div class="loader">Не удалось загрузить товары</div>';
    }
}

function renderPagination(pageData) {
    const pagContainer = document.getElementById('pagination');
    if (!pagContainer) return;
    if (pageData.totalPages <= 1) {
        pagContainer.innerHTML = '';
        return;
    }
    let html = '';
    for (let i = 0; i < pageData.totalPages; i++) {
        html += `<button class="${i === currentPage ? 'active' : ''}" data-page="${i}">${i+1}</button>`;
    }
    pagContainer.innerHTML = html;
}