import { updateAuthUI } from './auth.js';
import { initCatalog } from './catalog.js';
import { initHome } from './home.js';
import { initProduct } from './product.js';
import { initProfile } from './profile.js';

document.addEventListener('DOMContentLoaded', async () => {
    await updateAuthUI();

    const path = window.location.pathname;

    if (path.includes('/catalog.html')) {
        await initCatalog();
    } else if (path.includes('/product.html')) {
        await initProduct();
    } else if (path.includes('/profile.html')) {
        await initProfile();
    } else {
        await initHome();
    }
});