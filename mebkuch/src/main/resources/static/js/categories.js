// categories.js
import { API_BASE } from './config.js';
import { escapeHtml } from './utils.js';
import { updateAuthUI } from './auth.js';

document.addEventListener('DOMContentLoaded', async () => {
    await updateAuthUI();
    await loadCategoriesTree();
    setupGlobalSearch();
});

async function loadCategoriesTree() {
    const container = document.getElementById('categories-tree');
    if (!container) return;

    try {
        const resp = await fetch(`${API_BASE}/categories/roots?size=100`);
        if (!resp.ok) throw new Error('Ошибка загрузки категорий');
        const data = await resp.json();
        const categories = data.content || [];

        if (categories.length === 0) {
            container.innerHTML = '<div class="empty-message">Категории не найдены</div>';
            return;
        }

        container.innerHTML = '';
        for (const category of categories) {
            await renderCategoryNode(category, container, 0);
        }
    } catch (err) {
        console.error('Error loading categories:', err);
        container.innerHTML = '<div class="error-message">Ошибка загрузки категорий</div>';
    }
}

async function renderCategoryNode(category, parentElement, level) {
    const node = document.createElement('div');
    node.className = 'category-node';
    node.style.marginLeft = `${level * 24}px`;

    const header = document.createElement('div');
    header.className = 'category-header';

    let hasChildren = false;
    let childrenLoaded = false;
    let childrenContainer = null;

    const toggleBtn = document.createElement('button');
    toggleBtn.className = 'category-toggle';
    toggleBtn.innerHTML = '▶';
    toggleBtn.style.display = 'none';

    const categoryLink = document.createElement('a');
    categoryLink.className = 'category-name';
    categoryLink.textContent = escapeHtml(category.name);
    categoryLink.href = `/catalog.html?category=${category.id}`;

    header.appendChild(toggleBtn);
    header.appendChild(categoryLink);
    node.appendChild(header);

    try {
        const childResp = await fetch(`${API_BASE}/categories/childs/${category.id}`);
        if (childResp.ok) {
            const children = await childResp.json();
            hasChildren = children.length > 0;
            toggleBtn.style.display = hasChildren ? 'inline-flex' : 'none';

            if (hasChildren) {
                childrenContainer = document.createElement('div');
                childrenContainer.className = 'category-children';
                childrenContainer.style.display = 'none';
                node.appendChild(childrenContainer);

                toggleBtn.addEventListener('click', async () => {
                    if (!childrenLoaded) {
                        for (const child of children) {
                            await renderCategoryNode(child, childrenContainer, level + 1);
                        }
                        childrenLoaded = true;
                        childrenContainer.style.display = 'block';
                        toggleBtn.innerHTML = '▼';
                    } else {
                        if (childrenContainer.style.display === 'none') {
                            childrenContainer.style.display = 'block';
                            toggleBtn.innerHTML = '▼';
                        } else {
                            childrenContainer.style.display = 'none';
                            toggleBtn.innerHTML = '▶';
                        }
                    }
                });
            }
        }
    } catch (err) {
        console.error('Error loading children:', err);
    }

    parentElement.appendChild(node);
}

function setupGlobalSearch() {
    const btn = document.getElementById('btn-global-search');
    const input = document.getElementById('global-search');
    if (btn && input) {
        btn.onclick = () => {
            const query = input.value.trim();
            if (query) window.location.href = `/catalog.html?search=${encodeURIComponent(query)}`;
        };
        input.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') btn.click();
        });
    }
}