// components.js
import { API_BASE } from './config.js';
import { escapeHtml } from './utils.js';
import { updateAuthUI } from './auth.js';

let currentPage = 0;
let pageSize = 12;
let currentSort = 'id,desc';
let currentFilters = {
    name: '',
    material: '',
    country: '',
    beginCost: null,
    endCost: null
};

document.addEventListener('DOMContentLoaded', async () => {
    await updateAuthUI();
    await loadComponentCategories();
    attachFilterEvents();
    await loadComponents();
    setupGlobalSearch();
});

async function loadComponentCategories() {
    const select = document.getElementById('component-category');
    if (!select) return;

    try {
        const resp = await fetch(`${API_BASE}/component-category?size=100`);
        if (resp.ok) {
            const data = await resp.json();
            const categories = data.content || [];

            categories.forEach(cat => {
                const option = document.createElement('option');
                option.value = cat.id;
                option.textContent = escapeHtml(cat.name);
                select.appendChild(option);
            });
        }
    } catch (err) {
        console.error('Error loading categories:', err);
    }
}

function attachFilterEvents() {
    const filterForm = document.getElementById('components-filter-form');
    if (filterForm) {
        filterForm.addEventListener('submit', (e) => {
            e.preventDefault();
            collectFilters();
            currentPage = 0;
            loadComponents();
        });

        filterForm.addEventListener('reset', () => {
            setTimeout(() => {
                const nameInput = document.querySelector('[name="name"]');
                const materialInput = document.querySelector('[name="material"]');
                const countryInput = document.querySelector('[name="country"]');
                const beginCostInput = document.querySelector('[name="begin-cost"]');
                const endCostInput = document.querySelector('[name="end-cost"]');
                const categorySelect = document.getElementById('component-category');

                if (nameInput) nameInput.value = '';
                if (materialInput) materialInput.value = '';
                if (countryInput) countryInput.value = '';
                if (beginCostInput) beginCostInput.value = '';
                if (endCostInput) endCostInput.value = '';
                if (categorySelect) categorySelect.value = '';

                collectFilters();
                currentPage = 0;
                loadComponents();
            }, 10);
        });
    }

    const sortSelect = document.getElementById('component-sort');
    if (sortSelect) {
        sortSelect.addEventListener('change', (e) => {
            currentSort = e.target.value;
            currentPage = 0;
            loadComponents();
        });
    }
}

function collectFilters() {
    const nameInput = document.querySelector('[name="name"]');
    const materialInput = document.querySelector('[name="material"]');
    const countryInput = document.querySelector('[name="country"]');
    const beginCostInput = document.querySelector('[name="begin-cost"]');
    const endCostInput = document.querySelector('[name="end-cost"]');

    currentFilters = {
        name: nameInput?.value || '',
        material: materialInput?.value || '',
        country: countryInput?.value || '',
        beginCost: beginCostInput?.value ? parseFloat(beginCostInput.value) : null,
        endCost: endCostInput?.value ? parseFloat(endCostInput.value) : null
    };
}

async function loadComponents() {
    const container = document.getElementById('components-list');
    if (!container) return;

    container.innerHTML = '<div class="loader">Загрузка компонентов...</div>';

    const params = new URLSearchParams({
        page: currentPage,
        size: pageSize,
        sort: currentSort
    });

    if (currentFilters.name) params.append('name', currentFilters.name);
    if (currentFilters.material) params.append('material', currentFilters.material);
    if (currentFilters.country) params.append('country', currentFilters.country);
    if (currentFilters.beginCost) params.append('begin-cost', currentFilters.beginCost);
    if (currentFilters.endCost) params.append('end-cost', currentFilters.endCost);

    const url = `${API_BASE}/component?${params.toString()}`;

    try {
        const resp = await fetch(url, { credentials: 'include' });
        if (!resp.ok) throw new Error('Ошибка загрузки компонентов');

        const data = await resp.json();
        const components = data.content || [];

        renderComponents(components);
        renderPagination(data);
        updateComponentsCount(data.totalElements);
    } catch (err) {
        console.error(err);
        container.innerHTML = '<div class="loader error">Ошибка загрузки компонентов</div>';
    }
}

function renderComponents(components) {
    const container = document.getElementById('components-list');
    if (!container) return;

    if (components.length === 0) {
        container.innerHTML = '<div class="empty-message">Компоненты не найдены</div>';
        return;
    }

    container.innerHTML = '';

    components.forEach(component => {
        const card = createComponentCard(component);
        container.appendChild(card);
    });
}

function createComponentCard(component) {
    const card = document.createElement('div');
    card.className = 'component-card';
    card.style.cursor = 'pointer';

    card.addEventListener('click', () => {
        const params = new URLSearchParams();
        if (component.name) params.append('componentName', component.name);
        if (component.material) params.append('componentMaterial', component.material);
        if (component.country) params.append('componentCountry', component.country);

        window.location.href = `/catalog.html?${params.toString()}`;
    });

    const characteristics = [];
    if (component.material) characteristics.push(`Материал: ${escapeHtml(component.material)}`);
    if (component.country) characteristics.push(`Страна: ${escapeHtml(component.country)}`);

    card.innerHTML = `
        <div class="component-info">
            <h3 class="component-name">${escapeHtml(component.name)}</h3>
            ${characteristics.length > 0 ? `<div class="component-details">${characteristics.join(' • ')}</div>` : ''}
            <div class="component-price">${component.cost?.toFixed(2) || 0} ₽</div>
        </div>
    `;

    return card;
}

function renderPagination(pageData) {
    const pagContainer = document.getElementById('components-pagination');
    if (!pagContainer) return;

    const totalPages = pageData.totalPages;
    if (totalPages <= 1) {
        pagContainer.innerHTML = '';
        return;
    }

    let startPage = Math.max(0, currentPage - 2);
    let endPage = Math.min(totalPages - 1, currentPage + 2);

    if (endPage - startPage < 4) {
        if (startPage === 0) endPage = Math.min(4, totalPages - 1);
        else if (endPage === totalPages - 1) startPage = Math.max(0, totalPages - 5);
    }

    let html = '';
    if (startPage > 0) {
        html += `<button data-page="0">1</button><span class="pagination-dots">...</span>`;
    }
    for (let i = startPage; i <= endPage; i++) {
        html += `<button class="${i === currentPage ? 'active' : ''}" data-page="${i}">${i + 1}</button>`;
    }
    if (endPage < totalPages - 1) {
        html += `<span class="pagination-dots">...</span><button data-page="${totalPages - 1}">${totalPages}</button>`;
    }
    pagContainer.innerHTML = html;

    pagContainer.querySelectorAll('button').forEach(btn => {
        btn.addEventListener('click', () => {
            currentPage = parseInt(btn.dataset.page);
            loadComponents();
        });
    });
}

function updateComponentsCount(total) {
    const countElement = document.getElementById('components-count');
    if (countElement) {
        const word = getDeclension(total, ['компонент', 'компонента', 'компонентов']);
        countElement.textContent = `Найдено: ${total} ${word}`;
    }
}

function getDeclension(number, words) {
    const cases = [2, 0, 1, 1, 1, 2];
    return words[(number % 100 > 4 && number % 100 < 20) ? 2 : cases[Math.min(number % 10, 5)]];
}

function setupGlobalSearch() {
    const btn = document.getElementById('btn-global-search');
    const input = document.getElementById('global-search');

    if (btn && input) {
        btn.onclick = () => {
            const query = input.value.trim();
            if (query) {
                const nameInput = document.querySelector('[name="name"]');
                if (nameInput) {
                    nameInput.value = query;
                    collectFilters();
                    currentPage = 0;
                    loadComponents();
                }
            }
        };

        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') btn.click();
        });
    }
}