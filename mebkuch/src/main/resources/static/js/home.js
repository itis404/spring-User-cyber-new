import { API_BASE } from './config.js';
import { showLoader, renderProductCard, escapeHtml } from './utils.js';

let stylesPage = 0;
let stylesTotalPages = 1;
let isLoadingStyles = false;

export async function initHome() {
    await loadRootCategories();
    await loadDiscountProducts();
    await loadStyles();
    await loadTypes();
    setupGlobalSearch();
    setupStylesLoadMore();
    await loadSectionsBanner();
}

async function loadStyles(loadMore = false) {
    if (isLoadingStyles) return;
    isLoadingStyles = true;

    const container = document.getElementById('styles-list');
    if (!container) return;

    if (!loadMore) {
        stylesPage = 0;
        container.innerHTML = '';
    }

    try {
        const params = new URLSearchParams({
            page: stylesPage,
            size: 6,
            sort: 'id,asc'
        });
        const resp = await fetch(`${API_BASE}/product-style?${params}`);
        if (!resp.ok) throw new Error('Ошибка загрузки стилей');
        const data = await resp.json();

        stylesTotalPages = data.totalPages;
        const styles = data.content || [];

        styles.forEach(style => {
            const styleCard = createStyleCard(style);
            container.appendChild(styleCard);
        });

        const loadMoreBtn = document.getElementById('load-more-styles');
        if (loadMoreBtn) {
            if (stylesPage + 1 >= stylesTotalPages) {
                loadMoreBtn.style.display = 'none';
            } else {
                loadMoreBtn.style.display = 'block';
            }
        }

        stylesPage++;
    } catch (err) {
        console.error('Error loading styles:', err);
        if (!loadMore) {
            container.innerHTML = '<div class="error-message">Ошибка загрузки стилей</div>';
        }
    } finally {
        isLoadingStyles = false;
    }
}

function createStyleCard(style) {
    const card = document.createElement('div');
    card.className = 'style-card';
    card.dataset.id = style.id;
    card.onclick = () => {
        window.location.href = `/catalog.html?style=${style.id}`;
    };

    const imageUrl = style.imageUrl && style.imageUrl.trim() !== ''
        ? style.imageUrl
        : 'https://via.placeholder.com/400x300?text=No+Image';

    card.innerHTML = `
        <div class="style-card-image" style="background-image: url('${imageUrl}')">
            <div class="style-card-overlay">
                <h3 class="style-card-name">${escapeHtml(style.name)}</h3>
            </div>
        </div>
    `;

    return card;
}

function setupStylesLoadMore() {
    const stylesSection = document.getElementById('styles-list')?.parentElement;
    if (stylesSection && !document.getElementById('load-more-styles')) {
        const loadMoreBtn = document.createElement('button');
        loadMoreBtn.id = 'load-more-styles';
        loadMoreBtn.className = 'load-more-btn';
        loadMoreBtn.textContent = 'Загрузить ещё стили';
        loadMoreBtn.onclick = () => loadStyles(true);
        stylesSection.appendChild(loadMoreBtn);
    }
}

async function loadRootCategories() {
    const container = document.getElementById('root-categories');
    if (!container) return;
    try {
        const resp = await fetch(`${API_BASE}/categories/roots?size=20`);
        if (!resp.ok) throw new Error();
        const data = await resp.json();
        const categories = data.content || [];
        container.innerHTML = categories.map(cat => `
            <div class="cat-card" data-id="${cat.id}">
                <div class="cat-card-icon"></div>
                <div class="cat-card-name">${escapeHtml(cat.name)}</div>
            </div>
        `).join('');
        document.querySelectorAll('.cat-card').forEach(card => {
            card.addEventListener('click', () => {
                // Перенаправляем на каталог с выбранной категорией
                window.location.href = `/catalog.html?category=${card.dataset.id}`;
            });
        });

        const allCategoriesLink = document.createElement('div');
        allCategoriesLink.className = 'all-categories-link';
        allCategoriesLink.innerHTML = '<a href="/categories.html">Все категории →</a>';
        container.parentElement.appendChild(allCategoriesLink);

    } catch(e) {
        container.innerHTML = '<p>Не удалось загрузить категории</p>';
    }
}

async function loadDiscountProducts() {
    const container = document.getElementById('discount-products');
    if (!container) return;
    showLoader('discount-products');
    try {
        const params = new URLSearchParams({
            page: 0,
            size: 8,
            sort: 'discount,desc'
        });
        const resp = await fetch(`${API_BASE}/product/max-discount?${params}`, {
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка загрузки товаров со скидкой');
        const data = await resp.json();
        container.innerHTML = '';
        for (const prod of data.content) {
            const card = await renderProductCard(prod);
            container.appendChild(card);
        }
    } catch(e) {
        console.error(e);
        container.innerHTML = '<p>Нет товаров со скидкой</p>';
    }
}

async function loadTypes() {
    const container = document.getElementById('types-list');
    if (!container) return;
    try {
        const params = new URLSearchParams({
            page: 0,
            size: 50,
            sort: 'id,asc'
        });
        const resp = await fetch(`${API_BASE}/product-type?${params}`);
        if (!resp.ok) throw new Error();
        const data = await resp.json();
        const types = data.content || [];
        container.innerHTML = types.map(t => `
            <li class="type-tag" data-id="${t.id}">${escapeHtml(t.name)}</li>
        `).join('');
        document.querySelectorAll('#types-list li').forEach(li => {
            li.addEventListener('click', () => {
                window.location.href = `/catalog.html?type=${li.dataset.id}`;
            });
        });
    } catch(e) { console.error(e); }
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

let currentSlideIndex = 0;
let slidesData = [];
let autoSlideInterval = null;

async function loadSectionsBanner() {
    const container = document.getElementById('sections-banner');
    if (!container) return;

    try {
        const resp = await fetch(`${API_BASE}/product-section?page=0&size=20&sort=id,asc`, {
            credentials: 'include'
        });
        if (!resp.ok) throw new Error('Ошибка загрузки секций');
        const data = await resp.json();
        slidesData = data.content || [];

        if (slidesData.length === 0) {
            container.innerHTML = '<div class="banner-slider static-banner" style="background:#1e2a3a"><h2>Скидки до 50%</h2><p>Мебель и освещение для вашего дома</p></div>';
            return;
        }

        container.innerHTML = `
            <div class="slider-container">
                <div class="slides-wrapper" id="slidesWrapper"></div>
                <button class="slider-arrow prev-arrow" id="prevSlide">‹</button>
                <button class="slider-arrow next-arrow" id="nextSlide">›</button>
                <div class="slider-dots" id="sliderDots"></div>
            </div>
        `;

        const wrapper = document.getElementById('slidesWrapper');
        const dotsContainer = document.getElementById('sliderDots');

        slidesData.forEach((slide, idx) => {
            const slideDiv = document.createElement('div');
            slideDiv.className = 'slide';
            slideDiv.style.cursor = 'pointer';
            if (slide.imageUrl && slide.imageUrl.trim() !== '') {
                slideDiv.style.backgroundImage = `url(${slide.imageUrl})`;
                slideDiv.classList.add('has-image');
            } else {
                slideDiv.style.backgroundColor = '#1e2a3a';
                slideDiv.classList.add('no-image');
            }
            const contentDiv = document.createElement('div');
            contentDiv.className = 'slide-content';
            contentDiv.innerHTML = `<h2>${escapeHtml(slide.name)}</h2>`;
            slideDiv.appendChild(contentDiv);
            slideDiv.addEventListener('click', () => {
                window.location.href = `/section-products.html?sectionId=${slide.id}&sectionName=${encodeURIComponent(slide.name)}`;
            });
            wrapper.appendChild(slideDiv);

            const dot = document.createElement('span');
            dot.classList.add('slider-dot');
            if (idx === 0) dot.classList.add('active');
            dot.dataset.index = idx;
            dot.addEventListener('click', (e) => {
                e.stopPropagation();
                goToSlide(idx);
            });
            dotsContainer.appendChild(dot);
        });

        document.getElementById('prevSlide')?.addEventListener('click', () => changeSlide(-1));
        document.getElementById('nextSlide')?.addEventListener('click', () => changeSlide(1));

        updateSlidePosition();
        startAutoSlide();

        container.addEventListener('mouseenter', stopAutoSlide);
        container.addEventListener('mouseleave', startAutoSlide);

    } catch (err) {
        console.error('Ошибка загрузки секций:', err);
        container.innerHTML = '<div class="banner-slider static-banner" style="background:#1e2a3a"><h2>Скидки до 50%</h2><p>Мебель и освещение для вашего дома</p></div>';
    }
}

function updateSlidePosition() {
    const wrapper = document.getElementById('slidesWrapper');
    if (!wrapper) return;
    const slideWidth = wrapper.clientWidth;
    wrapper.style.transform = `translateX(-${currentSlideIndex * slideWidth}px)`;

    document.querySelectorAll('.slider-dot').forEach((dot, idx) => {
        if (idx === currentSlideIndex) dot.classList.add('active');
        else dot.classList.remove('active');
    });
}

function changeSlide(direction) {
    const newIndex = currentSlideIndex + direction;
    if (newIndex < 0) {
        currentSlideIndex = slidesData.length - 1;
    } else if (newIndex >= slidesData.length) {
        currentSlideIndex = 0;
    } else {
        currentSlideIndex = newIndex;
    }
    updateSlidePosition();
}

function goToSlide(index) {
    if (index >= 0 && index < slidesData.length) {
        currentSlideIndex = index;
        updateSlidePosition();
    }
}

function startAutoSlide() {
    if (autoSlideInterval) clearInterval(autoSlideInterval);
    autoSlideInterval = setInterval(() => {
        changeSlide(1);
    }, 5000);
}

function stopAutoSlide() {
    if (autoSlideInterval) {
        clearInterval(autoSlideInterval);
        autoSlideInterval = null;
    }
}