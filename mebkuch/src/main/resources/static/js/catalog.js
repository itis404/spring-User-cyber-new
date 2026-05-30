// catalog.js
import { API_BASE } from './config.js';
import { showLoader, renderProductCard, escapeHtml } from './utils.js';

let currentPage = 0;
let pageSize = 12;
let currentSort = 'id,desc';
let currentFilters = {
    name: '',
    minPrice: null,
    maxPrice: null,
    minDiscount: null,
    maxDiscount: null,
    createdAt: null,
    categoryId: [],
    productTypeId: [],
    productStatusId: [],
    productStyleId: [],
    attributes: [],
    componentFilter: {}
};

let availableAttributes = [];
let allStyles = [], allTypes = [], allStatuses = [];
let stylesPage = 0, typesPage = 0, statusesPage = 0;
let stylesTotalPages = 1, typesTotalPages = 1, statusesTotalPages = 1;
const PAGE_SIZE_FILTERS = 10;

async function loadFilterOptions() {
    try {
        await loadRootCategories();
        await loadStyles();
        await loadTypes();
        await loadStatuses();

        const attrsResp = await fetch(`${API_BASE}/attributes`);
        if (attrsResp.ok) {
            availableAttributes = await attrsResp.json();
        }
    } catch(e) { console.error('Ошибка загрузки фильтров', e); }
}

async function loadRootCategories() {
    const container = document.getElementById('filter-categories');
    if (!container) return;
    try {
        const resp = await fetch(`${API_BASE}/categories/roots?size=100`);
        if (!resp.ok) throw new Error();
        const data = await resp.json();
        const categories = data.content || [];
        container.innerHTML = '';
        for (const cat of categories) {
            await renderCategoryNode(cat, container, 0);
        }
    } catch(e) { container.innerHTML = '<div>Ошибка загрузки категорий</div>'; }
}

async function renderCategoryNode(category, parentElement, level) {
    const div = document.createElement('div');
    div.className = 'category-node';
    div.style.marginLeft = `${level * 20}px`;

    const toggleBtn = document.createElement('span');
    toggleBtn.className = 'category-toggle';
    toggleBtn.textContent = '▶';
    toggleBtn.style.cursor = 'pointer';
    toggleBtn.style.display = 'inline-block';
    toggleBtn.style.width = '20px';

    const checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    checkbox.value = category.id;
    checkbox.name = 'categoryId';
    checkbox.addEventListener('change', () => applyFilters());

    const label = document.createElement('label');
    label.appendChild(checkbox);
    label.appendChild(document.createTextNode(` ${escapeHtml(category.name)}`));

    div.appendChild(toggleBtn);
    div.appendChild(label);

    let childrenContainer = null;
    let childrenLoaded = false;

    toggleBtn.addEventListener('click', async () => {
        if (!childrenLoaded) {
            if (!childrenContainer) {
                childrenContainer = document.createElement('div');
                childrenContainer.className = 'category-children';
                div.appendChild(childrenContainer);
            }
            const childResp = await fetch(`${API_BASE}/categories/childs/${category.id}`);
            if (childResp.ok) {
                const children = await childResp.json();
                for (const child of children) {
                    await renderCategoryNode(child, childrenContainer, level + 1);
                }
                childrenLoaded = true;
                toggleBtn.textContent = '▼';
            } else {
                toggleBtn.style.visibility = 'hidden';
            }
        } else {
            if (childrenContainer.style.display === 'none') {
                childrenContainer.style.display = 'block';
                toggleBtn.textContent = '▼';
            } else {
                childrenContainer.style.display = 'none';
                toggleBtn.textContent = '▶';
            }
        }
    });

    parentElement.appendChild(div);
}

async function loadStyles(reset = true) {
    if (reset) {
        stylesPage = 0;
        allStyles = [];
        const container = document.getElementById('filter-styles');
        if (container) container.innerHTML = '';
    }

    const container = document.getElementById('filter-styles');
    if (!container) return;

    try {
        const params = new URLSearchParams({ page: stylesPage, size: PAGE_SIZE_FILTERS, sort: 'id,asc' });
        const resp = await fetch(`${API_BASE}/product-style?${params}`);
        if (!resp.ok) throw new Error();
        const data = await resp.json();
        stylesTotalPages = data.totalPages;

        const newStyles = data.content || [];
        allStyles = [...allStyles, ...newStyles];

        renderFullCheckboxList(container, allStyles, 'productStyleId', 'id', 'name', stylesPage + 1 < stylesTotalPages);

        if (stylesPage + 1 < stylesTotalPages) {
            stylesPage++;
        }
    } catch(e) { console.error(e); }
}

async function loadTypes(reset = true) {
    if (reset) {
        typesPage = 0;
        allTypes = [];
        const container = document.getElementById('filter-types');
        if (container) container.innerHTML = '';
    }

    const container = document.getElementById('filter-types');
    if (!container) return;

    try {
        const params = new URLSearchParams({ page: typesPage, size: PAGE_SIZE_FILTERS, sort: 'id,asc' });
        const resp = await fetch(`${API_BASE}/product-type?${params}`);
        if (!resp.ok) throw new Error();
        const data = await resp.json();
        typesTotalPages = data.totalPages;

        const newTypes = data.content || [];
        allTypes = [...allTypes, ...newTypes];

        renderFullCheckboxList(container, allTypes, 'productTypeId', 'id', 'name', typesPage + 1 < typesTotalPages);

        if (typesPage + 1 < typesTotalPages) {
            typesPage++;
        }
    } catch(e) { console.error(e); }
}

async function loadStatuses(reset = true) {
    if (reset) {
        statusesPage = 0;
        allStatuses = [];
        const container = document.getElementById('filter-statuses');
        if (container) container.innerHTML = '';
    }

    const container = document.getElementById('filter-statuses');
    if (!container) return;

    try {
        const params = new URLSearchParams({ page: statusesPage, size: PAGE_SIZE_FILTERS, sort: 'id,asc' });
        const resp = await fetch(`${API_BASE}/product-status?${params}`);
        if (!resp.ok) throw new Error();
        const data = await resp.json();
        statusesTotalPages = data.totalPages;

        const newStatuses = data.content || [];
        allStatuses = [...allStatuses, ...newStatuses];

        renderFullCheckboxList(container, allStatuses, 'productStatusId', 'id', 'name', statusesPage + 1 < statusesTotalPages);

        if (statusesPage + 1 < statusesTotalPages) {
            statusesPage++;
        }
    } catch(e) { console.error(e); }
}

function renderFullCheckboxList(container, items, name, valueField, labelField, hasMore) {
    container.innerHTML = '';
    items.forEach(item => {
        const label = document.createElement('label');
        const cb = document.createElement('input');
        cb.type = 'checkbox';
        cb.name = name;
        cb.value = item[valueField];
        cb.addEventListener('change', () => applyFilters());
        label.appendChild(cb);
        label.appendChild(document.createTextNode(` ${escapeHtml(item[labelField])}`));
        container.appendChild(label);
    });

    if (hasMore) {
        const loadMoreBtn = document.createElement('button');
        loadMoreBtn.type = 'button';
        loadMoreBtn.textContent = 'Загрузить ещё';
        loadMoreBtn.className = 'load-more';
        loadMoreBtn.onclick = () => {
            if (name === 'productStyleId') loadStyles(false);
            else if (name === 'productTypeId') loadTypes(false);
            else if (name === 'productStatusId') loadStatuses(false);
        };
        container.appendChild(loadMoreBtn);
    }
}

function attachFilterEvents() {
    const filterForm = document.getElementById('filter-form');
    if (filterForm) {
        filterForm.addEventListener('submit', (e) => {
            e.preventDefault();
            collectFiltersFromForm();
            currentPage = 0;
            fetchProducts();
        });
        filterForm.addEventListener('reset', () => {
            setTimeout(() => {
                collectFiltersFromForm();
                currentPage = 0;
                fetchProducts();
            }, 10);
        });
    }

    const addAttrBtn = document.getElementById('add-attribute-btn');
    if (addAttrBtn) {
        addAttrBtn.onclick = () => addAttributeRow();
    }
}

function setupSorting() {
    const sortSelect = document.getElementById('sort-select');
    if (sortSelect) {
        sortSelect.addEventListener('change', (e) => {
            currentSort = e.target.value;
            currentPage = 0;
            fetchProducts();
        });
    }
}

function collectFiltersFromForm() {
    const form = document.getElementById('filter-form');
    currentFilters.name = form.querySelector('[name="name"]')?.value || '';
    currentFilters.minPrice = parseFloat(form.querySelector('[name="minPrice"]')?.value) || null;
    currentFilters.maxPrice = parseFloat(form.querySelector('[name="maxPrice"]')?.value) || null;
    currentFilters.minDiscount = parseFloat(form.querySelector('[name="minDiscount"]')?.value) || null;
    currentFilters.maxDiscount = parseFloat(form.querySelector('[name="maxDiscount"]')?.value) || null;
    currentFilters.createdAt = form.querySelector('[name="createdAt"]')?.value || null;

    currentFilters.componentFilter = {
        name: form.querySelector('[name="componentName"]')?.value || null,
        material: form.querySelector('[name="componentMaterial"]')?.value || null,
        country: form.querySelector('[name="componentCountry"]')?.value || null,
        beginCost: parseFloat(form.querySelector('[name="componentBeginCost"]')?.value) || null,
        endCost: parseFloat(form.querySelector('[name="componentEndCost"]')?.value) || null
    };

    currentFilters.categoryId = Array.from(form.querySelectorAll('input[name="categoryId"]:checked')).map(cb => parseInt(cb.value));
    currentFilters.productStyleId = Array.from(form.querySelectorAll('input[name="productStyleId"]:checked')).map(cb => parseInt(cb.value));
    currentFilters.productTypeId = Array.from(form.querySelectorAll('input[name="productTypeId"]:checked')).map(cb => parseInt(cb.value));
    currentFilters.productStatusId = Array.from(form.querySelectorAll('input[name="productStatusId"]:checked')).map(cb => parseInt(cb.value));

    currentFilters.attributes = [];
    const attrRows = document.querySelectorAll('#attributes-container .attr-row');
    attrRows.forEach(row => {
        const select = row.querySelector('select');
        const attrId = parseInt(select.value);
        if (!attrId) return;

        const attr = availableAttributes.find(a => a.id === attrId);
        if (!attr) return;

        const inputContainer = row.querySelector('.attr-inputs');
        let filter = { attributeId: attrId };

        if (attr.type === 'TEXT') {
            const valueElement = inputContainer.querySelector('.attr-value-text');
            let value = '';
            if (valueElement.tagName === 'SELECT') {
                value = valueElement.value;
            } else {
                value = valueElement?.value.trim();
            }
            if (value) filter.valueText = value;
            else return;
        }
        else if (attr.type === 'NUMBER') {
            const minValue = inputContainer.querySelector('.attr-min-value')?.value;
            const maxValue = inputContainer.querySelector('.attr-max-value')?.value;
            if (minValue || maxValue) {
                if (minValue) filter.minValue = parseFloat(minValue);
                if (maxValue) filter.maxValue = parseFloat(maxValue);
            } else {
                return;
            }
        }
        else if (attr.type === 'BOOLEAN') {
            const value = inputContainer.querySelector('.attr-value-boolean')?.value;
            if (value === 'true') filter.valueBoolean = true;
            else if (value === 'false') filter.valueBoolean = false;
            else return;
        }

        currentFilters.attributes.push(filter);
    });
}

function parseUrlParams() {
    const urlParams = new URLSearchParams(window.location.search);
    const search = urlParams.get('search');
    const category = urlParams.get('category');
    const style = urlParams.get('style');
    const type = urlParams.get('type');
    const componentName = urlParams.get('componentName');
    const componentMaterial = urlParams.get('componentMaterial');
    const componentCountry = urlParams.get('componentCountry');

    if (search) {
        currentFilters.name = search;
        const searchInput = document.querySelector('[name="name"]');
        if (searchInput) searchInput.value = search;
    }

    if (category) {
        currentFilters.categoryId = [parseInt(category)];
        setTimeout(() => {
            const checkbox = document.querySelector(`input[name="categoryId"][value="${category}"]`);
            if (checkbox) checkbox.checked = true;
        }, 500);
    }

    if (style) {
        currentFilters.productStyleId = [parseInt(style)];
        setTimeout(() => {
            const checkbox = document.querySelector(`input[name="productStyleId"][value="${style}"]`);
            if (checkbox) checkbox.checked = true;
        }, 500);
    }

    if (type) {
        currentFilters.productTypeId = [parseInt(type)];
        setTimeout(() => {
            const checkbox = document.querySelector(`input[name="productTypeId"][value="${type}"]`);
            if (checkbox) checkbox.checked = true;
        }, 500);
    }

    if (componentName || componentMaterial || componentCountry) {
        const componentFilter = {};
        if (componentName) componentFilter.name = componentName;
        if (componentMaterial) componentFilter.material = componentMaterial;
        if (componentCountry) componentFilter.country = componentCountry;
        currentFilters.componentFilter = componentFilter;

        setTimeout(() => {
            const compNameInput = document.querySelector('[name="componentName"]');
            const compMaterialInput = document.querySelector('[name="componentMaterial"]');
            const compCountryInput = document.querySelector('[name="componentCountry"]');
            if (compNameInput && componentName) compNameInput.value = componentName;
            if (compMaterialInput && componentMaterial) compMaterialInput.value = componentMaterial;
            if (compCountryInput && componentCountry) compCountryInput.value = componentCountry;
        }, 500);
    }
}

export async function initCatalog() {
    await loadFilterOptions();
    attachFilterEvents();
    setupSorting();
    parseUrlParams();
    await fetchProducts();
}

function addAttributeRow() {
    const container = document.getElementById('attributes-container');
    if (!availableAttributes.length) {
        alert('Нет доступных характеристик');
        return;
    }
    const row = document.createElement('div');
    row.className = 'attr-row';

    const select = document.createElement('select');
    select.innerHTML = '<option value="">Выберите характеристику</option>' +
        availableAttributes.map(attr => `<option value="${attr.id}" data-type="${attr.type}">${escapeHtml(attr.name)}</option>`).join('');

    const inputContainer = document.createElement('div');
    inputContainer.className = 'attr-inputs';

    const removeBtn = document.createElement('button');
    removeBtn.textContent = '✕';
    removeBtn.className = 'remove-attr';
    removeBtn.title = 'Удалить';
    removeBtn.onclick = () => row.remove();

    row.appendChild(select);
    row.appendChild(inputContainer);
    row.appendChild(removeBtn);
    container.appendChild(row);

    async function updateInputs() {
        const selectedOption = select.selectedOptions[0];
        const attrId = parseInt(select.value);
        const attrType = selectedOption?.dataset.type;
        inputContainer.innerHTML = '';

        if (!attrId || !attrType) return;

        if (attrType === 'TEXT') {
            const wrapper = document.createElement('div');
            wrapper.className = 'attr-text-wrapper';

            const selectValue = document.createElement('select');
            selectValue.className = 'attr-value-text';
            selectValue.innerHTML = '<option value="">Любое значение</option>';

            try {
                const resp = await fetch(`${API_BASE}/attribute-values/by-attribute/${attrId}`);
                if (resp.ok) {
                    const values = await resp.json();
                    values.forEach(val => {
                        const option = document.createElement('option');
                        option.value = val.valueText || '';
                        option.textContent = val.valueText || '—';
                        selectValue.appendChild(option);
                    });
                }
            } catch(e) {
                console.error('Ошибка загрузки значений атрибута', e);
            }

            if (selectValue.children.length <= 1) {
                const input = document.createElement('input');
                input.type = 'text';
                input.placeholder = 'Введите значение';
                input.className = 'attr-value-text';
                wrapper.appendChild(input);
            } else {
                wrapper.appendChild(selectValue);
            }

            inputContainer.appendChild(wrapper);
        }
        else if (attrType === 'NUMBER') {
            const wrapper = document.createElement('div');
            wrapper.className = 'attr-number-wrapper';

            const minInput = document.createElement('input');
            minInput.type = 'number';
            minInput.placeholder = 'от';
            minInput.className = 'attr-min-value';

            const maxInput = document.createElement('input');
            maxInput.type = 'number';
            maxInput.placeholder = 'до';
            maxInput.className = 'attr-max-value';

            const separator = document.createElement('span');
            separator.textContent = '—';
            separator.className = 'attr-separator';

            wrapper.appendChild(minInput);
            wrapper.appendChild(separator);
            wrapper.appendChild(maxInput);
            inputContainer.appendChild(wrapper);
        }
        else if (attrType === 'BOOLEAN') {
            const selectBool = document.createElement('select');
            selectBool.className = 'attr-value-boolean';
            selectBool.innerHTML = '<option value="">Любое</option><option value="true">Да</option><option value="false">Нет</option>';
            inputContainer.appendChild(selectBool);
        }
    }

    select.onchange = updateInputs;
    updateInputs();
}

function applyFilters() {
    collectFiltersFromForm();
    currentPage = 0;
    fetchProducts();
}

async function fetchProducts() {
    const grid = document.getElementById('catalog-products');
    if (!grid) return;
    showLoader('catalog-products');

    const body = {};
    if (currentFilters.name) body.name = currentFilters.name;
    if (currentFilters.minPrice) body.minPrice = currentFilters.minPrice;
    if (currentFilters.maxPrice) body.maxPrice = currentFilters.maxPrice;
    if (currentFilters.minDiscount) body.minDiscount = currentFilters.minDiscount;
    if (currentFilters.maxDiscount) body.maxDiscount = currentFilters.maxDiscount;
    if (currentFilters.createdAt) body.createdAt = currentFilters.createdAt;
    if (currentFilters.categoryId && currentFilters.categoryId.length) body.categoryId = currentFilters.categoryId;
    if (currentFilters.productTypeId && currentFilters.productTypeId.length) body.productTypeId = currentFilters.productTypeId;
    if (currentFilters.productStatusId && currentFilters.productStatusId.length) body.productStatusId = currentFilters.productStatusId;
    if (currentFilters.productStyleId && currentFilters.productStyleId.length) body.productStyleId = currentFilters.productStyleId;
    if (currentFilters.attributes && currentFilters.attributes.length) body.attributes = currentFilters.attributes;

    const comp = currentFilters.componentFilter;
    if (comp && (comp.name || comp.material || comp.country || comp.beginCost || comp.endCost)) {
        body.componentFilter = {};
        if (comp.name) body.componentFilter.name = comp.name;
        if (comp.material) body.componentFilter.material = comp.material;
        if (comp.country) body.componentFilter.country = comp.country;
        if (comp.beginCost) body.componentFilter.beginCost = comp.beginCost;
        if (comp.endCost) body.componentFilter.endCost = comp.endCost;
    }

    const params = new URLSearchParams({
        page: currentPage,
        size: pageSize,
        sort: currentSort
    });

    console.log('Request body:', body);
    console.log('Request URL:', `${API_BASE}/product/hard-search?${params}`);

    try {
        const resp = await fetch(`${API_BASE}/product/hard-search?${params}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });

        console.log('Response status:', resp.status);

        if (!resp.ok) {
            const errorText = await resp.text();
            console.error('Error response:', errorText);
            throw new Error(`Ошибка загрузки товаров: ${resp.status}`);
        }

        const data = await resp.json();
        console.log('Products loaded:', data.content?.length || 0);

        grid.innerHTML = '';
        if (data.content && data.content.length > 0) {
            for (const product of data.content) {
                const card = await renderProductCard(product);
                grid.appendChild(card);
            }
        } else {
            grid.innerHTML = '<div class="loader">Товары не найдены</div>';
        }
        renderPagination(data);
    } catch (err) {
        console.error('Fetch products error:', err);
        grid.innerHTML = '<div class="loader">Ошибка загрузки товаров: ' + err.message + '</div>';
    }
}

function renderPagination(pageData) {
    const pagContainer = document.getElementById('pagination');
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
        html += `<button class="${i === currentPage ? 'active' : ''}" data-page="${i}">${i+1}</button>`;
    }
    if (endPage < totalPages - 1) {
        html += `<span class="pagination-dots">...</span><button data-page="${totalPages-1}">${totalPages}</button>`;
    }
    pagContainer.innerHTML = html;

    pagContainer.querySelectorAll('button').forEach(btn => {
        btn.addEventListener('click', () => {
            currentPage = parseInt(btn.dataset.page);
            fetchProducts();
        });
    });
}