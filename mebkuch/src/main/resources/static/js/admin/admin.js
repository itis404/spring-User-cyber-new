import { API_BASE } from '../config.js';
import { escapeHtml } from '../utils.js';

let currentEditType = null;
let currentEditId = null;
let tempProductId = null;

let compCurrentPage = 0;
let compPageSize = 10;
let compTotalPages = 0;
let compFilters = { name: '', material: '', country: '', beginCost: null, endCost: null };

let prodCurrentPage = 0;
let prodPageSize = 10;
let prodTotalPages = 0;
let prodFilters = { name: '', minPrice: null, maxPrice: null, minDiscount: null, maxDiscount: null, categoryId: null, productStyleId: null, productTypeId: null, productStatusId: null };

let imageProductFilter = null;

document.addEventListener('DOMContentLoaded', async () => {
    console.log('Admin panel loaded');

    try {
        const resp = await fetch(`${API_BASE}/auth/user/about-me`, {
            method: 'GET',
            credentials: 'include'
        });

        if (!resp.ok) {
            window.location.href = '/';
            return;
        }

        const user = await resp.json();

        if (user.role !== 'ADMIN' && user.role !== 'ROLE_ADMIN') {
            alert('У вас нет доступа к админ-панели');
            window.location.href = '/';
            return;
        }

        const userNameSpan = document.getElementById('admin-user-name');
        if (userNameSpan) {
            userNameSpan.textContent = user.fullname || user.email;
        }

    } catch (err) {
        console.error('Auth error:', err);
        window.location.href = '/';
        return;
    }

    initAdminPanel();
});

function initAdminPanel() {
    const navBtns = document.querySelectorAll('.admin-nav-btn');
    navBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const section = btn.dataset.section;
            navBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            document.querySelectorAll('.admin-section').forEach(s => s.classList.remove('active'));
            const activeSection = document.getElementById(`${section}-section`);
            if (activeSection) activeSection.classList.add('active');
            loadSectionData(section);
        });
    });

    const logoutBtn = document.getElementById('admin-logout-btn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', async () => {
            try {
                await fetch(`${API_BASE}/auth/user/logout`, { method: 'POST', credentials: 'include' });
            } catch (err) {}
            window.location.href = '/';
        });
    }

    document.getElementById('create-category-btn')?.addEventListener('click', () => showCreateForm('category'));
    document.getElementById('cancel-category')?.addEventListener('click', () => hideCreateForm('category'));
    document.getElementById('save-category')?.addEventListener('click', () => createCategory());

    document.getElementById('create-status-btn')?.addEventListener('click', () => showCreateForm('status'));
    document.getElementById('cancel-status')?.addEventListener('click', () => hideCreateForm('status'));
    document.getElementById('save-status')?.addEventListener('click', () => createSimpleItem('status'));

    document.getElementById('create-section-btn')?.addEventListener('click', () => showCreateForm('section'));
    document.getElementById('cancel-section')?.addEventListener('click', () => hideCreateForm('section'));
    document.getElementById('save-section')?.addEventListener('click', () => createSimpleItem('section'));

    document.getElementById('create-style-btn')?.addEventListener('click', () => showCreateForm('style'));
    document.getElementById('cancel-style')?.addEventListener('click', () => hideCreateForm('style'));
    document.getElementById('save-style')?.addEventListener('click', () => createSimpleItem('style'));

    document.getElementById('create-type-btn')?.addEventListener('click', () => showCreateForm('type'));
    document.getElementById('cancel-type')?.addEventListener('click', () => hideCreateForm('type'));
    document.getElementById('save-type')?.addEventListener('click', () => createSimpleItem('type'));

    document.getElementById('create-component-category-btn')?.addEventListener('click', () => showCreateForm('component-category'));
    document.getElementById('cancel-component-category')?.addEventListener('click', () => hideCreateForm('component-category'));
    document.getElementById('save-component-category')?.addEventListener('click', () => createComponentCategory());

    document.getElementById('create-component-btn')?.addEventListener('click', () => showCreateForm('component'));
    document.getElementById('cancel-component')?.addEventListener('click', () => hideCreateForm('component'));
    document.getElementById('save-component')?.addEventListener('click', () => createComponent());
    document.getElementById('comp-search-btn')?.addEventListener('click', () => searchComponents());
    document.getElementById('comp-reset-btn')?.addEventListener('click', () => resetComponentsSearch());

    document.getElementById('create-product-step1-btn')?.addEventListener('click', () => showCreateForm('product-step1'));
    document.getElementById('cancel-product-step1')?.addEventListener('click', () => hideCreateForm('product-step1'));
    document.getElementById('save-product-step1')?.addEventListener('click', () => createProductStep1());

    document.getElementById('cancel-product-step2')?.addEventListener('click', () => hideCreateForm('product-step2'));
    document.getElementById('save-product-step2')?.addEventListener('click', () => createProductStep2());
    document.getElementById('back-product-step1')?.addEventListener('click', () => backToProductStep1());

    document.getElementById('prod-search-btn')?.addEventListener('click', () => searchProducts());
    document.getElementById('prod-reset-btn')?.addEventListener('click', () => resetProductsSearch());

    document.getElementById('create-attribute-btn')?.addEventListener('click', () => showCreateForm('attribute'));
    document.getElementById('cancel-attribute')?.addEventListener('click', () => hideCreateForm('attribute'));
    document.getElementById('save-attribute')?.addEventListener('click', () => createAttribute());

    document.getElementById('create-attribute-value-btn')?.addEventListener('click', () => {
        showCreateForm('attribute-value');
        loadAttributesForSelect('new-attr-value-attribute');
    });
    document.getElementById('cancel-attribute-value')?.addEventListener('click', () => hideCreateForm('attribute-value'));
    document.getElementById('save-attribute-value')?.addEventListener('click', () => createAttributeValue());
    document.getElementById('filter-attr-value-btn')?.addEventListener('click', () => {
        const attrId = document.getElementById('attr-value-filter').value;
        loadAttributeValues(attrId || null);
    });

    document.getElementById('create-image-btn')?.addEventListener('click', () => showCreateForm('image'));
    document.getElementById('cancel-image')?.addEventListener('click', () => hideCreateForm('image'));
    document.getElementById('save-image')?.addEventListener('click', () => createImage());
    document.getElementById('filter-images-btn')?.addEventListener('click', () => {
        imageProductFilter = document.getElementById('image-product-filter').value || null;
        loadProductImages();
    });
    document.getElementById('reset-images-btn')?.addEventListener('click', () => {
        document.getElementById('image-product-filter').value = '';
        imageProductFilter = null;
        loadProductImages();
    });

    document.getElementById('save-edit')?.addEventListener('click', () => saveEditItem());
    document.getElementById('cancel-edit')?.addEventListener('click', () => hideEditModal());
    document.getElementById('save-comp-edit')?.addEventListener('click', () => saveComponentEdit());
    document.getElementById('cancel-comp-edit')?.addEventListener('click', () => hideComponentEditModal());
    document.getElementById('save-attr-edit')?.addEventListener('click', () => saveAttributeEdit());
    document.getElementById('cancel-attr-edit')?.addEventListener('click', () => hideAttributeEditModal());
    document.getElementById('save-image-edit')?.addEventListener('click', () => saveImageEdit());
    document.getElementById('cancel-image-edit')?.addEventListener('click', () => hideImageEditModal());

    document.getElementById('add-similar-btn')?.addEventListener('click', () => addSimilarProduct());
    document.getElementById('remove-similar-btn')?.addEventListener('click', () => removeSimilarProduct());
    document.getElementById('cancel-similar')?.addEventListener('click', () => hideSimilarModal());

    loadSectionData('categories');
    loadAttributesForFilter();
}

function showCreateForm(type) {
    const form = document.getElementById(`create-${type}-form`);
    if (form) form.classList.remove('hidden');

    if (type === 'category') {
        loadParentCategoriesForSelect();
    }
    if (type === 'component') {
        loadComponentCategories();
    }
    if (type === 'product-step1') {
        loadProductSelects();
    }
    if (type === 'attribute-value') {
        document.getElementById('new-attr-value-text').value = '';
        document.getElementById('new-attr-value-number').value = '';
        document.getElementById('new-attr-value-boolean').value = 'true';
        updateAttrValueFormFields(null, 'new');
    }
}

function hideCreateForm(type) {
    const form = document.getElementById(`create-${type}-form`);
    if (form) form.classList.add('hidden');

    if (type === 'product-step1') {
        const inputs = form.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            if (input.type !== 'button' && input.type !== 'submit') {
                input.value = '';
            }
        });
        const multiSelects = form.querySelectorAll('select[multiple]');
        multiSelects.forEach(select => {
            Array.from(select.options).forEach(opt => opt.selected = false);
        });
    } else if (type === 'product-step2') {
        document.getElementById('extra-product-id').value = '';
        document.getElementById('extra-images').value = '';
        document.getElementById('extra-subproducts').value = '';
        document.getElementById('extra-attribute-values').value = '';
        const compSelect = document.getElementById('extra-components');
        if (compSelect) Array.from(compSelect.options).forEach(opt => opt.selected = false);
        tempProductId = null;
    } else {
        const nameInput = document.getElementById(`new-${type}-name`);
        if (nameInput) nameInput.value = '';
    }
}

async function loadParentCategoriesForSelect() {
    const select = document.getElementById('new-cat-parent');
    if (!select) return;
    try {
        const resp = await fetch(`${API_BASE}/admin/categories?size=100`, { credentials: 'include' });
        if (resp.ok) {
            const data = await resp.json();
            const categories = data.content || [];
            select.innerHTML = '<option value="">Нет (корневая категория)</option>';
            categories.forEach(cat => {
                const option = document.createElement('option');
                option.value = cat.id;
                option.textContent = escapeHtml(cat.name);
                select.appendChild(option);
            });
        }
    } catch (err) { console.error(err); }
}

async function createCategory() {
    const nameInput = document.getElementById('new-cat-name');
    const name = nameInput?.value.trim();
    if (!name) { showToast('Введите название категории', 'error'); return; }
    const parentSelect = document.getElementById('new-cat-parent');
    const parentId = parentSelect?.value;
    let url = `${API_BASE}/admin/categories/`;
    let body = { name };
    if (parentId && parentId !== '') {
        url += `${parentId}/child`;
    } else {
        url += `parent`;
    }
    try {
        const resp = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });
        if (!resp.ok) {
            const error = await resp.json();
            throw new Error(error.messageError || 'Ошибка создания');
        }
        showToast('Категория создана!', 'success');
        hideCreateForm('category');
        nameInput.value = '';
        if (parentSelect) parentSelect.value = '';
        loadSectionData('categories');
    } catch (err) { showToast(err.message, 'error'); }
}

async function createSimpleItem(type) {
    const nameInput = document.getElementById(`new-${type}-name`);
    const name = nameInput?.value.trim();
    if (!name) { showToast('Введите название', 'error'); return; }
    let url = '';
    switch(type) {
        case 'status': url = `${API_BASE}/admin/product-status`; break;
        case 'section': url = `${API_BASE}/admin/product-section`; break;
        case 'style': url = `${API_BASE}/admin/product-style`; break;
        case 'type': url = `${API_BASE}/admin/product-type`; break;
        default: return;
    }
    const body = { name };
    if (type === 'style') {
        const imageUrl = document.getElementById('new-style-image')?.value;
        if (imageUrl) body.imageUrl = imageUrl;
    }
    if (type === 'type') {
        body.hasComponents = document.getElementById('new-type-has-components')?.value === 'true';
    }
    try {
        const resp = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка создания');
        showToast('Создано успешно!', 'success');
        hideCreateForm(type);
        nameInput.value = '';
        loadSectionData(`${type}s`);
    } catch (err) { showToast(err.message, 'error'); }
}

async function createComponentCategory() {
    const name = document.getElementById('new-comp-cat-name')?.value.trim();
    if (!name) { showToast('Введите название', 'error'); return; }
    try {
        const resp = await fetch(`${API_BASE}/admin/component-category`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name }),
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка создания');
        showToast('Категория компонента создана!', 'success');
        hideCreateForm('component-category');
        loadSectionData('component-categories');
    } catch (err) { showToast(err.message, 'error'); }
}

async function loadComponentCategories() {
    const select = document.getElementById('new-comp-category');
    if (!select) return;
    try {
        const resp = await fetch(`${API_BASE}/admin/component-category?size=100`, { credentials: 'include' });
        if (resp.ok) {
            const data = await resp.json();
            const categories = data.content || [];
            select.innerHTML = '<option value="">Выберите категорию</option>';
            categories.forEach(cat => {
                const option = document.createElement('option');
                option.value = cat.id;
                option.textContent = escapeHtml(cat.name);
                select.appendChild(option);
            });
        }
    } catch (err) { console.error(err); }
}

async function createComponent() {
    const name = document.getElementById('new-comp-name')?.value.trim();
    if (!name) { showToast('Введите название компонента', 'error'); return; }
    const body = {
        name: name,
        categoryId: document.getElementById('new-comp-category')?.value || null,
        material: document.getElementById('new-comp-material')?.value || null,
        country: document.getElementById('new-comp-country')?.value || null,
        cost: parseFloat(document.getElementById('new-comp-cost')?.value) || null
    };
    try {
        const resp = await fetch(`${API_BASE}/admin/component`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка создания');
        showToast('Компонент создан!', 'success');
        hideCreateForm('component');
        compCurrentPage = 0;
        loadComponents();
    } catch (err) { showToast(err.message, 'error'); }
}

async function loadProductSelects() {
    const selects = ['category', 'type', 'status', 'style'];
    for (const s of selects) {
        const select = document.getElementById(`new-prod-${s}`);
        if (!select) continue;
        let url = '';
        if (s === 'category') url = `${API_BASE}/admin/categories?size=100`;
        else if (s === 'type') url = `${API_BASE}/admin/product-type?size=100`;
        else if (s === 'status') url = `${API_BASE}/admin/product-status?size=100`;
        else if (s === 'style') url = `${API_BASE}/admin/product-style?size=100`;
        const resp = await fetch(url, { credentials: 'include' });
        if (resp.ok) {
            const data = await resp.json();
            const items = data.content || [];
            select.innerHTML = `<option value="">Выберите ${getSelectLabel(s)}</option>`;
            items.forEach(item => {
                const option = document.createElement('option');
                option.value = item.id;
                option.textContent = escapeHtml(item.name);
                select.appendChild(option);
            });
        }
    }

    const sectionsSelect = document.getElementById('new-prod-sections');
    if (sectionsSelect) {
        const resp = await fetch(`${API_BASE}/admin/product-section?size=100`, { credentials: 'include' });
        if (resp.ok) {
            const data = await resp.json();
            const sections = data.content || [];
            sectionsSelect.innerHTML = '';
            sections.forEach(section => {
                const option = document.createElement('option');
                option.value = section.id;
                option.textContent = escapeHtml(section.name);
                sectionsSelect.appendChild(option);
            });
        }
    }
}

async function loadExtraFieldsSelects() {
    const compSelect = document.getElementById('extra-components');
    if (compSelect) {
        const resp = await fetch(`${API_BASE}/admin/component?size=100`, { credentials: 'include' });
        if (resp.ok) {
            const data = await resp.json();
            const components = data.content || [];
            compSelect.innerHTML = '';
            components.forEach(comp => {
                const option = document.createElement('option');
                option.value = comp.id;
                option.textContent = `${escapeHtml(comp.name)} (${comp.cost || 0} ₽)`;
                compSelect.appendChild(option);
            });
        }
    }
}

function getSelectLabel(type) {
    const labels = { category: 'категорию', type: 'тип', status: 'статус', style: 'стиль' };
    return labels[type] || type;
}

async function createProductStep1() {
    const name = document.getElementById('new-prod-name')?.value.trim();
    if (!name) { showToast('Введите название товара', 'error'); return; }
    const price = parseFloat(document.getElementById('new-prod-price')?.value);
    if (!price) { showToast('Введите цену товара', 'error'); return; }

    const sectionsSelect = document.getElementById('new-prod-sections');
    const sections = Array.from(sectionsSelect?.selectedOptions || []).map(opt => parseInt(opt.value));

    const body = {
        name: name,
        description: document.getElementById('new-prod-description')?.value || null,
        minPrice: price,
        discount: parseFloat(document.getElementById('new-prod-discount')?.value) || 0,
        categoryId: parseInt(document.getElementById('new-prod-category')?.value) || null,
        productTypeId: parseInt(document.getElementById('new-prod-type')?.value) || null,
        productStatusId: parseInt(document.getElementById('new-prod-status')?.value) || null,
        productStyleId: parseInt(document.getElementById('new-prod-style')?.value) || null,
        sectionIds: sections
    };

    try {
        const resp = await fetch(`${API_BASE}/admin/products`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });
        if (!resp.ok) {
            const error = await resp.json();
            throw new Error(error.messageError || 'Ошибка создания');
        }
        const product = await resp.json();
        tempProductId = product.id;
        showToast('Шаг 1 завершен! Теперь добавьте дополнительные поля', 'success');
        document.getElementById('create-product-step1-form').classList.add('hidden');
        document.getElementById('extra-product-id').value = tempProductId;
        await loadExtraFieldsSelects();
        document.getElementById('create-product-step2-form').classList.remove('hidden');
    } catch (err) { showToast(err.message, 'error'); }
}

async function createProductStep2() {
    const productId = parseInt(document.getElementById('extra-product-id')?.value);
    if (!productId || productId !== tempProductId) {
        showToast('Ошибка: товар не найден', 'error');
        return;
    }

    const componentsSelect = document.getElementById('extra-components');
    const components = Array.from(componentsSelect?.selectedOptions || []).map(opt => parseInt(opt.value));

    const imagesStr = document.getElementById('extra-images')?.value;
    const images = imagesStr ? imagesStr.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n)) : [];

    const subProductsStr = document.getElementById('extra-subproducts')?.value;
    const subProducts = subProductsStr ? subProductsStr.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n)) : [];

    const attrValuesStr = document.getElementById('extra-attribute-values')?.value;
    const attributeValues = attrValuesStr ? attrValuesStr.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n)) : [];

    const body = {
        description: null,
        productStyleId: null,
        images: images,
        components: components,
        subProducts: subProducts,
        attributeValues: attributeValues
    };

    try {
        const resp = await fetch(`${API_BASE}/admin/products/${productId}/add-extra-fields`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });
        if (!resp.ok) {
            const error = await resp.json();
            throw new Error(error.messageError || 'Ошибка добавления полей');
        }
        showToast('Товар полностью создан!', 'success');
        hideCreateForm('product-step2');
        hideCreateForm('product-step1');
        tempProductId = null;
        prodCurrentPage = 0;
        loadProducts();
    } catch (err) { showToast(err.message, 'error'); }
}

function backToProductStep1() {
    document.getElementById('create-product-step2-form').classList.add('hidden');
    document.getElementById('create-product-step1-form').classList.remove('hidden');
    tempProductId = null;
}

async function createAttribute() {
    const name = document.getElementById('new-attr-name')?.value.trim();
    if (!name) { showToast('Введите название атрибута', 'error'); return; }
    const body = {
        name: name,
        type: document.getElementById('new-attr-type')?.value
    };
    try {
        const resp = await fetch(`${API_BASE}/admin/attributes`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка создания');
        showToast('Атрибут создан!', 'success');
        hideCreateForm('attribute');
        loadSectionData('attributes');
    } catch (err) { showToast(err.message, 'error'); }
}

async function loadAttributeForEdit(id) {
    const resp = await fetch(`${API_BASE}/admin/attributes/${id}`, { credentials: 'include' });
    if (resp.ok) {
        const attr = await resp.json();
        currentEditId = id;
        document.getElementById('edit-attr-name').value = attr.name || '';
        document.getElementById('edit-attr-type').value = attr.type || 'TEXT';
        document.getElementById('edit-attribute-modal').classList.remove('hidden');
    }
}

async function saveAttributeEdit() {
    const body = {
        name: document.getElementById('edit-attr-name').value,
        type: document.getElementById('edit-attr-type').value
    };
    try {
        const resp = await fetch(`${API_BASE}/admin/attributes/${currentEditId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка обновления');
        showToast('Атрибут обновлен!', 'success');
        hideAttributeEditModal();
        loadSectionData('attributes');
    } catch (err) { showToast(err.message, 'error'); }
}

function hideAttributeEditModal() {
    document.getElementById('edit-attribute-modal').classList.add('hidden');
}

async function loadAttributesForSelect(selectId) {
    const select = document.getElementById(selectId);
    if (!select) return;
    const resp = await fetch(`${API_BASE}/admin/attributes`, { credentials: 'include' });
    if (resp.ok) {
        const attrs = await resp.json();
        select.innerHTML = '<option value="">Выберите атрибут</option>';
        attrs.forEach(attr => {
            const option = document.createElement('option');
            option.value = attr.id;
            option.textContent = escapeHtml(attr.name);
            select.appendChild(option);
        });
        select.addEventListener('change', () => {
            const selectedAttr = attrs.find(a => a.id == select.value);
            updateAttrValueFormFields(selectedAttr?.type, 'new');
        });
    }
}

async function loadAttributesForFilter() {
    const select = document.getElementById('attr-value-filter');
    const resp = await fetch(`${API_BASE}/admin/attributes`, { credentials: 'include' });
    if (resp.ok) {
        const attrs = await resp.json();
        select.innerHTML = '<option value="">Все атрибуты</option>';
        attrs.forEach(attr => {
            const option = document.createElement('option');
            option.value = attr.id;
            option.textContent = escapeHtml(attr.name);
            select.appendChild(option);
        });
    }
}

function updateAttrValueFormFields(attrType, prefix = 'new') {
    const textGroup = document.getElementById(`${prefix}-attr-value-text-group`);
    const numberGroup = document.getElementById(`${prefix}-attr-value-number-group`);
    const booleanGroup = document.getElementById(`${prefix}-attr-value-boolean-group`);

    if (!attrType) {
        if (textGroup) textGroup.style.display = 'block';
        if (numberGroup) numberGroup.style.display = 'none';
        if (booleanGroup) booleanGroup.style.display = 'none';
        return;
    }

    if (attrType === 'TEXT') {
        if (textGroup) textGroup.style.display = 'block';
        if (numberGroup) numberGroup.style.display = 'none';
        if (booleanGroup) booleanGroup.style.display = 'none';
    } else if (attrType === 'NUMBER') {
        if (textGroup) textGroup.style.display = 'none';
        if (numberGroup) numberGroup.style.display = 'block';
        if (booleanGroup) booleanGroup.style.display = 'none';
    } else if (attrType === 'BOOLEAN') {
        if (textGroup) textGroup.style.display = 'none';
        if (numberGroup) numberGroup.style.display = 'none';
        if (booleanGroup) booleanGroup.style.display = 'block';
    }
}

async function createAttributeValue() {
    const attributeId = parseInt(document.getElementById('new-attr-value-attribute').value);
    if (!attributeId) { showToast('Выберите атрибут', 'error'); return; }

    const attrsResp = await fetch(`${API_BASE}/admin/attributes/${attributeId}`, { credentials: 'include' });
    const attr = await attrsResp.json();

    const body = { attributeId: attributeId };
    if (attr.type === 'TEXT') {
        body.valueText = document.getElementById('new-attr-value-text').value;
        if (!body.valueText) { showToast('Введите текст', 'error'); return; }
    } else if (attr.type === 'NUMBER') {
        body.valueNumber = parseFloat(document.getElementById('new-attr-value-number').value);
        if (isNaN(body.valueNumber)) { showToast('Введите число', 'error'); return; }
    } else if (attr.type === 'BOOLEAN') {
        body.valueBoolean = document.getElementById('new-attr-value-boolean').value === 'true';
    }

    try {
        const resp = await fetch(`${API_BASE}/admin/attribute-values`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка создания');
        showToast('Значение атрибута создано!', 'success');
        hideCreateForm('attribute-value');
        loadAttributeValues();
    } catch (err) { showToast(err.message, 'error'); }
}

async function loadAttributeValues(attributeId = null) {
    const container = document.getElementById('attribute-values-list');
    if (!container) return;
    container.innerHTML = '<div class="loader">Загрузка...</div>';

    try {
        let allValues = [];

        if (attributeId) {
            const resp = await fetch(`${API_BASE}/admin/attribute-values/by-attribute/${attributeId}`, { credentials: 'include' });
            if (resp.ok) allValues = await resp.json();
        } else {
            const attrsResp = await fetch(`${API_BASE}/admin/attributes`, { credentials: 'include' });
            const attrs = await attrsResp.json();
            for (const attr of attrs) {
                const resp = await fetch(`${API_BASE}/admin/attribute-values/by-attribute/${attr.id}`, { credentials: 'include' });
                if (resp.ok) {
                    const values = await resp.json();
                    allValues = [...allValues, ...values];
                }
            }
        }

        renderAttributeValuesList(container, allValues);
    } catch (err) {
        container.innerHTML = '<div class="empty-message">Ошибка загрузки</div>';
    }
}

function renderAttributeValuesList(container, values) {
    if (values.length === 0) {
        container.innerHTML = '<div class="empty-message">Нет значений атрибутов</div>';
        return;
    }
    container.innerHTML = '';
    for (const val of values) {
        const card = document.createElement('div');
        card.className = 'item-card';
        let displayValue = '';
        if (val.valueText) displayValue = val.valueText;
        else if (val.valueNumber) displayValue = val.valueNumber;
        else if (val.valueBoolean !== null) displayValue = val.valueBoolean ? 'Да' : 'Нет';

        card.innerHTML = `
            <div class="item-info">
                <span class="item-name">${escapeHtml(displayValue)}</span>
                <span class="item-id">ID: ${val.id}</span>
                <span class="item-badge">Атрибут ID: ${val.attributeId}</span>
            </div>
            <div class="item-actions">
                <button class="btn-danger" data-id="${val.id}">Удалить</button>
            </div>
        `;
        card.querySelector('.btn-danger').onclick = () => deleteAttributeValue(val.id);
        container.appendChild(card);
    }
}

async function deleteAttributeValue(id) {
    if (!confirm('Удалить значение атрибута?')) return;
    try {
        const resp = await fetch(`${API_BASE}/admin/attribute-values/${id}`, { method: 'DELETE', credentials: 'include' });
        if (!resp.ok) throw new Error('Ошибка удаления');
        showToast('Значение атрибута удалено!', 'success');
        loadAttributeValues();
    } catch (err) { showToast(err.message, 'error'); }
}

async function createImage() {
    const productId = parseInt(document.getElementById('new-image-product-id')?.value);
    if (!productId) { showToast('Введите ID товара', 'error'); return; }
    const imagePath = document.getElementById('new-image-path')?.value.trim();
    if (!imagePath) { showToast('Введите URL изображения', 'error'); return; }

    const body = {
        productId: productId,
        imagePath: imagePath,
        isMain: document.getElementById('new-image-is-main')?.value === 'true',
        sortOrder: parseInt(document.getElementById('new-image-sort-order')?.value) || 0
    };

    try {
        const resp = await fetch(`${API_BASE}/admin/product-image`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка создания');
        showToast('Изображение создано!', 'success');
        hideCreateForm('image');
        loadProductImages();
    } catch (err) { showToast(err.message, 'error'); }
}

async function loadProductImages() {
    const container = document.getElementById('product-images-list');
    if (!container) return;
    container.innerHTML = '<div class="loader">Загрузка...</div>';

    let url = `${API_BASE}/admin/product-image`;
    if (imageProductFilter) {
        url = `${API_BASE}/admin/product-image/product/${imageProductFilter}`;
    }

    try {
        let images = [];
        if (imageProductFilter) {
            const resp = await fetch(url, { credentials: 'include' });
            if (resp.ok) images = await resp.json();
        } else {
            const resp = await fetch(`${API_BASE}/admin/product-image?size=100`, { credentials: 'include' });
            if (resp.ok) {
                const data = await resp.json();
                images = data.content || [];
            }
        }

        if (images.length === 0) {
            container.innerHTML = '<div class="empty-message">Нет изображений</div>';
            return;
        }

        container.innerHTML = '';
        for (const img of images) {
            const card = document.createElement('div');
            card.className = 'item-card';
            card.innerHTML = `
                <div class="item-info">
                    <span class="item-name">${escapeHtml(img.imagePath)}</span>
                    <span class="item-id">ID: ${img.id}</span>
                    <span class="item-badge">Товар ID: ${img.productId}</span>
                    <span class="item-badge">${img.isMain ? 'Основное' : ''}</span>
                    <span class="item-badge">Порядок: ${img.sortOrder || 0}</span>
                </div>
                <div class="item-actions">
                    <button class="btn-edit" data-id="${img.id}">Ред.</button>
                    <button class="btn-danger" data-id="${img.id}">Удалить</button>
                </div>
            `;
            card.querySelector('.btn-edit').onclick = () => loadImageForEdit(img);
            card.querySelector('.btn-danger').onclick = () => deleteImage(img.id);
            container.appendChild(card);
        }
    } catch (err) {
        container.innerHTML = '<div class="empty-message">Ошибка загрузки</div>';
    }
}

async function loadImageForEdit(image) {
    currentEditId = image.id;
    document.getElementById('edit-image-path').value = image.imagePath || '';
    document.getElementById('edit-image-is-main').value = image.isMain ? 'true' : 'false';
    document.getElementById('edit-image-sort-order').value = image.sortOrder || 0;
    document.getElementById('edit-image-modal').classList.remove('hidden');
}

async function saveImageEdit() {
    const params = new URLSearchParams({
        imagePath: document.getElementById('edit-image-path').value,
        isMain: document.getElementById('edit-image-is-main').value === 'true',
        sortOrder: parseInt(document.getElementById('edit-image-sort-order').value) || 0
    });

    try {
        const resp = await fetch(`${API_BASE}/admin/product-image/${currentEditId}?${params}`, {
            method: 'PUT',
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка обновления');
        showToast('Изображение обновлено!', 'success');
        hideImageEditModal();
        loadProductImages();
    } catch (err) { showToast(err.message, 'error'); }
}

async function deleteImage(id) {
    if (!confirm('Удалить изображение?')) return;
    try {
        const resp = await fetch(`${API_BASE}/admin/product-image/${id}`, { method: 'DELETE', credentials: 'include' });
        if (!resp.ok) throw new Error('Ошибка удаления');
        showToast('Изображение удалено!', 'success');
        loadProductImages();
    } catch (err) { showToast(err.message, 'error'); }
}

function hideImageEditModal() {
    document.getElementById('edit-image-modal').classList.add('hidden');
}

function showSimilarModal(productId) {
    currentEditId = productId;
    document.getElementById('similar-product-id').value = productId;
    document.getElementById('similar-sub-id').value = '';
    loadSimilarList(productId);
    document.getElementById('edit-similar-modal').classList.remove('hidden');
}

async function loadSimilarList(productId) {
    const container = document.getElementById('similar-list');
    try {
        const resp = await fetch(`${API_BASE}/admin/product/similar/${productId}`, { credentials: 'include' });
        if (resp.ok) {
            const products = await resp.json();
            if (products.length === 0) {
                container.innerHTML = '<div class="empty-message">Нет похожих товаров</div>';
                return;
            }
            container.innerHTML = products.map(p => `
                <div class="similar-item">
                    <span>${escapeHtml(p.name)} (ID: ${p.id})</span>
                </div>
            `).join('');
        }
    } catch (err) {
        container.innerHTML = '<div class="empty-message">Ошибка загрузки</div>';
    }
}

async function addSimilarProduct() {
    const productId = parseInt(document.getElementById('similar-product-id').value);
    const subProductId = parseInt(document.getElementById('similar-sub-id').value);
    if (!productId || !subProductId) { showToast('Введите ID товаров', 'error'); return; }

    try {
        const resp = await fetch(`${API_BASE}/admin/product/similar/${productId}/${subProductId}`, {
            method: 'POST',
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка добавления');
        showToast('Похожий товар добавлен!', 'success');
        document.getElementById('similar-sub-id').value = '';
        loadSimilarList(productId);
    } catch (err) { showToast(err.message, 'error'); }
}

async function removeSimilarProduct() {
    const productId = parseInt(document.getElementById('similar-product-id').value);
    const subProductId = parseInt(document.getElementById('similar-sub-id').value);
    if (!productId || !subProductId) { showToast('Введите ID товаров', 'error'); return; }

    try {
        const resp = await fetch(`${API_BASE}/admin/product/similar/${productId}/${subProductId}`, {
            method: 'DELETE',
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка удаления');
        showToast('Похожий товар удален!', 'success');
        document.getElementById('similar-sub-id').value = '';
        loadSimilarList(productId);
    } catch (err) { showToast(err.message, 'error'); }
}

function hideSimilarModal() {
    document.getElementById('edit-similar-modal').classList.add('hidden');
}

function searchComponents() {
    compFilters = {
        name: document.getElementById('comp-search-name')?.value || '',
        material: document.getElementById('comp-search-material')?.value || '',
        country: document.getElementById('comp-search-country')?.value || '',
        beginCost: parseFloat(document.getElementById('comp-search-price-min')?.value) || null,
        endCost: parseFloat(document.getElementById('comp-search-price-max')?.value) || null
    };
    compCurrentPage = 0;
    loadComponents();
}

function resetComponentsSearch() {
    document.getElementById('comp-search-name').value = '';
    document.getElementById('comp-search-material').value = '';
    document.getElementById('comp-search-country').value = '';
    document.getElementById('comp-search-price-min').value = '';
    document.getElementById('comp-search-price-max').value = '';
    compFilters = { name: '', material: '', country: '', beginCost: null, endCost: null };
    compCurrentPage = 0;
    loadComponents();
}

async function loadComponents() {
    const container = document.getElementById('components-list');
    if (!container) return;
    container.innerHTML = '<div class="loader">Загрузка...</div>';

    const params = new URLSearchParams({
        page: compCurrentPage,
        size: compPageSize,
        sort: 'id,desc'
    });
    if (compFilters.name) params.append('name', compFilters.name);
    if (compFilters.material) params.append('material', compFilters.material);
    if (compFilters.country) params.append('country', compFilters.country);
    if (compFilters.beginCost) params.append('begin-cost', compFilters.beginCost);
    if (compFilters.endCost) params.append('end-cost', compFilters.endCost);

    try {
        const resp = await fetch(`${API_BASE}/admin/component?${params}`, { credentials: 'include' });
        if (!resp.ok) throw new Error();
        const data = await resp.json();
        const items = data.content || [];
        compTotalPages = data.totalPages;

        if (items.length === 0) {
            container.innerHTML = '<div class="empty-message">Нет компонентов</div>';
        } else {
            container.innerHTML = '';
            items.forEach(item => {
                const card = document.createElement('div');
                card.className = 'item-card';
                card.innerHTML = `
                    <div class="item-info">
                        <span class="item-name">${escapeHtml(item.name)}</span>
                        <span class="item-id">ID: ${item.id}</span>
                        ${item.material ? `<span class="item-badge">${escapeHtml(item.material)}</span>` : ''}
                        ${item.country ? `<span class="item-badge">${escapeHtml(item.country)}</span>` : ''}
                        <span class="item-badge">${item.cost || 0} ₽</span>
                    </div>
                    <div class="item-actions">
                        <button class="btn-edit" data-id="${item.id}">Ред.</button>
                        <button class="btn-danger" data-id="${item.id}">Удалить</button>
                    </div>
                `;
                card.querySelector('.btn-edit').onclick = () => loadComponentForEdit(item.id);
                card.querySelector('.btn-danger').onclick = () => deleteComponent(item.id);
                container.appendChild(card);
            });
        }
        renderComponentPagination();
    } catch (err) {
        container.innerHTML = '<div class="empty-message">Ошибка загрузки</div>';
    }
}

function renderComponentPagination() {
    const container = document.getElementById('components-pagination');
    if (!container) return;
    if (compTotalPages <= 1) { container.innerHTML = ''; return; }
    let html = '';
    for (let i = 0; i < compTotalPages; i++) {
        html += `<button class="${i === compCurrentPage ? 'active' : ''}" data-page="${i}">${i+1}</button>`;
    }
    container.innerHTML = html;
    container.querySelectorAll('button').forEach(btn => {
        btn.addEventListener('click', () => {
            compCurrentPage = parseInt(btn.dataset.page);
            loadComponents();
        });
    });
}

async function loadComponentForEdit(id) {
    const resp = await fetch(`${API_BASE}/admin/component/${id}`, { credentials: 'include' });
    if (resp.ok) {
        const comp = await resp.json();
        currentEditId = id;
        document.getElementById('edit-comp-name').value = comp.name || '';
        document.getElementById('edit-comp-material').value = comp.material || '';
        document.getElementById('edit-comp-country').value = comp.country || '';
        document.getElementById('edit-comp-cost').value = comp.cost || '';
        await loadComponentCategoriesForEdit(comp.categoryId);
        document.getElementById('edit-component-modal').classList.remove('hidden');
    }
}

async function loadComponentCategoriesForEdit(selectedId) {
    const select = document.getElementById('edit-comp-category');
    const resp = await fetch(`${API_BASE}/admin/component-category?size=100`, { credentials: 'include' });
    if (resp.ok) {
        const data = await resp.json();
        const categories = data.content || [];
        select.innerHTML = '<option value="">Выберите категорию</option>';
        categories.forEach(cat => {
            const option = document.createElement('option');
            option.value = cat.id;
            option.textContent = escapeHtml(cat.name);
            if (selectedId === cat.id) option.selected = true;
            select.appendChild(option);
        });
    }
}

async function saveComponentEdit() {
    const body = {
        name: document.getElementById('edit-comp-name').value,
        categoryId: document.getElementById('edit-comp-category').value || null,
        material: document.getElementById('edit-comp-material').value || null,
        country: document.getElementById('edit-comp-country').value || null,
        cost: parseFloat(document.getElementById('edit-comp-cost').value) || null
    };
    try {
        const resp = await fetch(`${API_BASE}/admin/component/${currentEditId}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка обновления');
        showToast('Компонент обновлен!', 'success');
        hideComponentEditModal();
        loadComponents();
    } catch (err) { showToast(err.message, 'error'); }
}

async function deleteComponent(id) {
    if (!confirm('Удалить компонент?')) return;
    try {
        const resp = await fetch(`${API_BASE}/admin/component/${id}`, { method: 'DELETE', credentials: 'include' });
        if (!resp.ok) throw new Error('Ошибка удаления');
        showToast('Компонент удален!', 'success');
        loadComponents();
    } catch (err) { showToast(err.message, 'error'); }
}

function hideComponentEditModal() {
    document.getElementById('edit-component-modal').classList.add('hidden');
}

async function loadProductSearchSelects() {
    const categories = ['category', 'style', 'type', 'status'];
    for (const c of categories) {
        const select = document.getElementById(`prod-search-${c}`);
        if (!select) continue;
        let url = '';
        if (c === 'category') url = `${API_BASE}/admin/categories?size=100`;
        else if (c === 'style') url = `${API_BASE}/admin/product-style?size=100`;
        else if (c === 'type') url = `${API_BASE}/admin/product-type?size=100`;
        else if (c === 'status') url = `${API_BASE}/admin/product-status?size=100`;
        const resp = await fetch(url, { credentials: 'include' });
        if (resp.ok) {
            const data = await resp.json();
            const items = data.content || [];
            select.innerHTML = `<option value="">Все ${c === 'category' ? 'категории' : c === 'style' ? 'стили' : c === 'type' ? 'типы' : 'статусы'}</option>`;
            items.forEach(item => {
                const option = document.createElement('option');
                option.value = item.id;
                option.textContent = escapeHtml(item.name);
                select.appendChild(option);
            });
        }
    }
}

function searchProducts() {
    prodFilters = {
        name: document.getElementById('prod-search-name')?.value || '',
        minPrice: parseFloat(document.getElementById('prod-search-price-min')?.value) || null,
        maxPrice: parseFloat(document.getElementById('prod-search-price-max')?.value) || null,
        minDiscount: parseFloat(document.getElementById('prod-search-discount-min')?.value) || null,
        maxDiscount: parseFloat(document.getElementById('prod-search-discount-max')?.value) || null,
        categoryId: parseInt(document.getElementById('prod-search-category')?.value) || null,
        productStyleId: parseInt(document.getElementById('prod-search-style')?.value) || null,
        productTypeId: parseInt(document.getElementById('prod-search-type')?.value) || null,
        productStatusId: parseInt(document.getElementById('prod-search-status')?.value) || null
    };
    prodCurrentPage = 0;
    loadProducts();
}

function resetProductsSearch() {
    document.getElementById('prod-search-name').value = '';
    document.getElementById('prod-search-price-min').value = '';
    document.getElementById('prod-search-price-max').value = '';
    document.getElementById('prod-search-discount-min').value = '';
    document.getElementById('prod-search-discount-max').value = '';
    document.getElementById('prod-search-category').value = '';
    document.getElementById('prod-search-style').value = '';
    document.getElementById('prod-search-type').value = '';
    document.getElementById('prod-search-status').value = '';
    prodFilters = { name: '', minPrice: null, maxPrice: null, minDiscount: null, maxDiscount: null, categoryId: null, productStyleId: null, productTypeId: null, productStatusId: null };
    prodCurrentPage = 0;
    loadProducts();
}

async function loadProducts() {
    const container = document.getElementById('products-list');
    if (!container) return;
    container.innerHTML = '<div class="loader">Загрузка...</div>';

    const body = {};
    if (prodFilters.name) body.name = prodFilters.name;
    if (prodFilters.minPrice) body.minPrice = prodFilters.minPrice;
    if (prodFilters.maxPrice) body.maxPrice = prodFilters.maxPrice;
    if (prodFilters.minDiscount) body.minDiscount = prodFilters.minDiscount;
    if (prodFilters.maxDiscount) body.maxDiscount = prodFilters.maxDiscount;
    if (prodFilters.categoryId) body.categoryId = [prodFilters.categoryId];
    if (prodFilters.productStyleId) body.productStyleId = [prodFilters.productStyleId];
    if (prodFilters.productTypeId) body.productTypeId = [prodFilters.productTypeId];
    if (prodFilters.productStatusId) body.productStatusId = [prodFilters.productStatusId];

    const params = new URLSearchParams({
        page: prodCurrentPage,
        size: prodPageSize,
        sort: 'id,desc'
    });

    try {
        const resp = await fetch(`${API_BASE}/admin/products/hard-search?${params}`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });
        if (!resp.ok) throw new Error();
        const data = await resp.json();
        const items = data.content || [];
        prodTotalPages = data.totalPages;

        if (items.length === 0) {
            container.innerHTML = '<div class="empty-message">Нет товаров</div>';
        } else {
            container.innerHTML = '';
            items.forEach(item => {
                const card = document.createElement('div');
                card.className = 'item-card';
                const finalPrice = item.minPrice * (1 - (item.discount || 0) / 100);
                card.innerHTML = `
                    <div class="item-info">
                        <span class="item-name">${escapeHtml(item.name)}</span>
                        <span class="item-id">ID: ${item.id}</span>
                        <span class="item-badge">${finalPrice.toFixed(2)} ₽</span>
                        ${item.discount ? `<span class="item-badge discount">-${item.discount}%</span>` : ''}
                    </div>
                    <div class="item-actions">
                        <button class="btn-edit" data-id="${item.id}">Ред.</button>
                        <button class="btn-similar" data-id="${item.id}">Похожие</button>
                        <button class="btn-danger" data-id="${item.id}">Удалить</button>
                    </div>
                `;
                card.querySelector('.btn-edit').onclick = () => loadProductForEdit(item.id);
                card.querySelector('.btn-similar').onclick = () => showSimilarModal(item.id);
                card.querySelector('.btn-danger').onclick = () => deleteProduct(item.id);
                container.appendChild(card);
            });
        }
        renderProductPagination();
    } catch (err) {
        container.innerHTML = '<div class="empty-message">Ошибка загрузки</div>';
    }
}

function renderProductPagination() {
    const container = document.getElementById('products-pagination');
    if (!container) return;
    if (prodTotalPages <= 1) { container.innerHTML = ''; return; }
    let html = '';
    for (let i = 0; i < prodTotalPages; i++) {
        html += `<button class="${i === prodCurrentPage ? 'active' : ''}" data-page="${i}">${i+1}</button>`;
    }
    container.innerHTML = html;
    container.querySelectorAll('button').forEach(btn => {
        btn.addEventListener('click', () => {
            prodCurrentPage = parseInt(btn.dataset.page);
            loadProducts();
        });
    });
}

async function loadProductForEdit(id) {
    const resp = await fetch(`${API_BASE}/admin/products/${id}`, { credentials: 'include' });
    if (resp.ok) {
        const prod = await resp.json();
        currentEditId = id;
        document.getElementById('edit-prod-name').value = prod.name || '';
        document.getElementById('edit-prod-description').value = prod.description || '';
        document.getElementById('edit-prod-price').value = prod.minPrice || '';
        document.getElementById('edit-prod-discount').value = prod.discount || 0;
        document.getElementById('edit-product-modal').classList.remove('hidden');
    }
}

async function saveProductEdit() {
    const body = {
        description: document.getElementById('edit-prod-description').value,
        productStyleId: null
    };
    try {
        const resp = await fetch(`${API_BASE}/admin/products/${currentEditId}/add-extra-fields`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(body),
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка обновления');
        showToast('Товар обновлен!', 'success');
        hideProductEditModal();
        loadProducts();
    } catch (err) { showToast(err.message, 'error'); }
}

async function deleteProduct(id) {
    if (!confirm('Удалить товар?')) return;
    try {
        const resp = await fetch(`${API_BASE}/admin/products/${id}`, { method: 'DELETE', credentials: 'include' });
        if (!resp.ok) throw new Error('Ошибка удаления');
        showToast('Товар удален!', 'success');
        loadProducts();
    } catch (err) { showToast(err.message, 'error'); }
}

function hideProductEditModal() {
    document.getElementById('edit-product-modal').classList.add('hidden');
}

async function loadSectionData(section) {
    const container = document.getElementById(`${section}-list`);
    if (!container) return;
    container.innerHTML = '<div class="loader">Загрузка...</div>';

    if (section === 'categories') {
        await loadCategoriesTree(container);
    } else if (section === 'component-categories') {
        await loadComponentCategoriesList(container);
    } else if (section === 'components') {
        await loadComponents();
    } else if (section === 'products') {
        await loadProductSearchSelects();
        await loadProducts();
    } else if (section === 'attributes') {
        await loadAttributesList(container);
    } else if (section === 'attribute-values') {
        await loadAttributeValues();
    } else if (section === 'product-images') {
        await loadProductImages();
    } else {
        await loadSimpleList(section, container);
    }
}

async function loadCategoriesTree(container) {
    const resp = await fetch(`${API_BASE}/admin/categories/roots?size=100`, { credentials: 'include' });
    const data = await resp.json();
    const categories = data.content || [];
    if (categories.length === 0) {
        container.innerHTML = '<div class="empty-message">Нет категорий</div>';
        return;
    }
    container.innerHTML = '';
    for (const cat of categories) {
        await renderCategoryNode(cat, container, 0);
    }
}

async function renderCategoryNode(category, parentElement, level) {
    const div = document.createElement('div');
    div.className = 'category-node-admin';
    const header = document.createElement('div');
    header.className = 'category-header-admin';
    const leftDiv = document.createElement('div');
    leftDiv.style.display = 'flex';
    leftDiv.style.alignItems = 'center';
    leftDiv.style.gap = '8px';
    leftDiv.style.flex = '1';
    const toggleBtn = document.createElement('button');
    toggleBtn.className = 'category-toggle-admin';
    toggleBtn.textContent = '▶';
    const nameSpan = document.createElement('span');
    nameSpan.className = 'item-name';
    nameSpan.textContent = escapeHtml(category.name);
    const idSpan = document.createElement('span');
    idSpan.className = 'item-id';
    idSpan.textContent = `ID: ${category.id}`;
    leftDiv.appendChild(toggleBtn);
    leftDiv.appendChild(nameSpan);
    leftDiv.appendChild(idSpan);
    const actions = document.createElement('div');
    actions.className = 'item-actions';
    const editBtn = document.createElement('button');
    editBtn.className = 'btn-edit';
    editBtn.textContent = 'Ред.';
    editBtn.onclick = () => showEditModal('category', category.id, category.name);
    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'btn-danger';
    deleteBtn.textContent = 'Удалить';
    deleteBtn.onclick = () => deleteSimpleItem('category', category.id);
    actions.appendChild(editBtn);
    actions.appendChild(deleteBtn);
    header.appendChild(leftDiv);
    header.appendChild(actions);
    div.appendChild(header);
    let childrenContainer = null;
    let childrenLoaded = false;
    toggleBtn.onclick = async () => {
        if (!childrenLoaded) {
            if (!childrenContainer) {
                childrenContainer = document.createElement('div');
                childrenContainer.className = 'category-children-admin';
                div.appendChild(childrenContainer);
            }
            const childResp = await fetch(`${API_BASE}/admin/categories/${category.id}/childs?size=100`, { credentials: 'include' });
            if (childResp.ok) {
                const data = await childResp.json();
                const children = data.content || [];
                for (const child of children) {
                    await renderCategoryNode(child, childrenContainer, level + 1);
                }
                childrenLoaded = true;
                toggleBtn.textContent = '▼';
            } else {
                toggleBtn.style.display = 'none';
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
    };
    parentElement.appendChild(div);
}

async function loadComponentCategoriesList(container) {
    const resp = await fetch(`${API_BASE}/admin/component-category?size=100`, { credentials: 'include' });
    const data = await resp.json();
    const items = data.content || [];
    if (items.length === 0) {
        container.innerHTML = '<div class="empty-message">Нет категорий компонентов</div>';
        return;
    }
    container.innerHTML = '';
    items.forEach(item => {
        const card = document.createElement('div');
        card.className = 'item-card';
        card.innerHTML = `
            <div class="item-info">
                <span class="item-name">${escapeHtml(item.name)}</span>
                <span class="item-id">ID: ${item.id}</span>
            </div>
            <div class="item-actions">
                <button class="btn-edit" data-id="${item.id}" data-name="${escapeHtml(item.name)}">Ред.</button>
                <button class="btn-danger" data-id="${item.id}">Удалить</button>
            </div>
        `;
        card.querySelector('.btn-edit').onclick = () => showEditModal('component-category', item.id, item.name);
        card.querySelector('.btn-danger').onclick = () => deleteSimpleItem('component-category', item.id);
        container.appendChild(card);
    });
}

async function loadAttributesList(container) {
    const resp = await fetch(`${API_BASE}/admin/attributes`, { credentials: 'include' });
    const items = await resp.json();
    if (items.length === 0) {
        container.innerHTML = '<div class="empty-message">Нет атрибутов</div>';
        return;
    }
    container.innerHTML = '';
    items.forEach(item => {
        const card = document.createElement('div');
        card.className = 'item-card';
        card.innerHTML = `
            <div class="item-info">
                <span class="item-name">${escapeHtml(item.name)}</span>
                <span class="item-id">ID: ${item.id}</span>
                <span class="item-badge">${item.type || 'TEXT'}</span>
            </div>
            <div class="item-actions">
                <button class="btn-edit" data-id="${item.id}">Ред.</button>
                <button class="btn-danger" data-id="${item.id}">Удалить</button>
            </div>
        `;
        card.querySelector('.btn-edit').onclick = () => loadAttributeForEdit(item.id);
        card.querySelector('.btn-danger').onclick = () => deleteSimpleItem('attribute', item.id);
        container.appendChild(card);
    });
}

async function loadSimpleList(section, container) {
    let url = '';
    let type = '';
    switch(section) {
        case 'statuses': url = `${API_BASE}/admin/product-status?size=100`; type = 'status'; break;
        case 'sections': url = `${API_BASE}/admin/product-section?size=100`; type = 'section'; break;
        case 'styles': url = `${API_BASE}/admin/product-style?size=100`; type = 'style'; break;
        case 'types': url = `${API_BASE}/admin/product-type?size=100`; type = 'type'; break;
        default: return;
    }
    const resp = await fetch(url, { credentials: 'include' });
    const data = await resp.json();
    const items = data.content || [];
    if (items.length === 0) {
        container.innerHTML = '<div class="empty-message">Нет элементов</div>';
        return;
    }
    container.innerHTML = '';
    items.forEach(item => {
        const card = document.createElement('div');
        card.className = 'item-card';
        card.innerHTML = `
            <div class="item-info">
                <span class="item-name">${escapeHtml(item.name)}</span>
                <span class="item-id">ID: ${item.id}</span>
            </div>
            <div class="item-actions">
                <button class="btn-edit" data-id="${item.id}" data-name="${escapeHtml(item.name)}">Ред.</button>
                <button class="btn-danger" data-id="${item.id}">Удалить</button>
            </div>
        `;
        card.querySelector('.btn-edit').onclick = () => showEditModal(type, item.id, item.name);
        card.querySelector('.btn-danger').onclick = () => deleteSimpleItem(type, item.id);
        container.appendChild(card);
    });
}

function showEditModal(type, id, name) {
    currentEditType = type;
    currentEditId = id;
    const modal = document.getElementById('edit-modal');
    document.getElementById('edit-name').value = name;
    modal.classList.remove('hidden');
}

async function saveEditItem() {
    const newName = document.getElementById('edit-name').value.trim();
    if (!newName) { showToast('Введите название', 'error'); return; }
    let url = '';
    switch(currentEditType) {
        case 'category': url = `${API_BASE}/admin/categories/${currentEditId}/name?name=${encodeURIComponent(newName)}`; break;
        case 'status': url = `${API_BASE}/admin/product-status/${currentEditId}/name?name=${encodeURIComponent(newName)}`; break;
        case 'section': url = `${API_BASE}/admin/product-section/${currentEditId}?name=${encodeURIComponent(newName)}`; break;
        case 'style': url = `${API_BASE}/admin/product-style/${currentEditId}/name?name=${encodeURIComponent(newName)}`; break;
        case 'type': url = `${API_BASE}/admin/product-type/${currentEditId}/name?name=${encodeURIComponent(newName)}`; break;
        case 'component-category': url = `${API_BASE}/admin/component-category/${currentEditId}?name=${encodeURIComponent(newName)}`; break;
        default: return;
    }
    try {
        const resp = await fetch(url, { method: 'PATCH', credentials: 'include' });
        if (!resp.ok) throw new Error('Ошибка обновления');
        showToast('Обновлено!', 'success');
        hideEditModal();
        if (currentEditType === 'component-category') loadSectionData('component-categories');
        else loadSectionData(currentEditType === 'category' ? 'categories' : `${currentEditType}s`);
    } catch (err) { showToast(err.message, 'error'); }
}

function hideEditModal() {
    document.getElementById('edit-modal').classList.add('hidden');
}

async function deleteSimpleItem(type, id) {
    if (!confirm(`Удалить ${type}?`)) return;
    let url = '';
    switch(type) {
        case 'category': url = `${API_BASE}/admin/categories/${id}`; break;
        case 'status': url = `${API_BASE}/admin/product-status/${id}`; break;
        case 'section': url = `${API_BASE}/admin/product-section/${id}`; break;
        case 'style': url = `${API_BASE}/admin/product-style/${id}`; break;
        case 'type': url = `${API_BASE}/admin/product-type/${id}`; break;
        case 'attribute': url = `${API_BASE}/admin/attributes/${id}`; break;
        case 'component-category': url = `${API_BASE}/admin/component-category/${id}`; break;
        default: return;
    }
    try {
        const resp = await fetch(url, { method: 'DELETE', credentials: 'include' });
        if (!resp.ok) throw new Error('Ошибка удаления');
        showToast('Удалено!', 'success');
        if (type === 'component-category') loadSectionData('component-categories');
        else if (type === 'attribute') loadSectionData('attributes');
        else loadSectionData(`${type}s`);
    } catch (err) { showToast(err.message, 'error'); }
}

function showToast(message, type) {
    const oldToast = document.querySelector('.admin-toast');
    if (oldToast) oldToast.remove();
    const toast = document.createElement('div');
    toast.className = `admin-toast ${type}`;
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
    `;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}