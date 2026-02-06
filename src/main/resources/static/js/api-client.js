/**
 * Cliente API simple para llamadas AJAX (Fetch API).
 *
 * Este proyecto funciona como "cliente" (BFF): el navegador llama a endpoints
 * del propio CajerosCliente (mismo origen), y el backend de este proyecto
 * reenvía la petición al Service usando el JWT guardado en cookie HttpOnly.
 *
 * Ventajas:
 * - No dependes de CORS del Service desde el browser
 * - No necesitas guardar JWT en localStorage
 */

const API_PREFIX = '/api';

function decodeJwtPayload(token) {
    if (!token || typeof token !== 'string') return null;

    const parts = token.split('.');
    if (parts.length < 2) return null;

    try {
        const base64Url = parts[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
        const json = atob(padded);
        return JSON.parse(decodeURIComponent(Array.from(json).map(c => {
            const hex = c.charCodeAt(0).toString(16).padStart(2, '0');
            return `%${hex}`;
        }).join('')));
    } catch (e) {
        return null;
    }
}

const _moneyFormatter = new Intl.NumberFormat('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
});

function swalAvailable() {
    return typeof window !== 'undefined'
            && window.Swal
            && typeof window.Swal.fire === 'function';
}

let authRedirectInProgress = false;

async function handleAuthFailure(status, payload) {
    if (authRedirectInProgress) {
        return;
    }
    authRedirectInProgress = true;

    const title = status === 403 ? 'Acceso denegado' : 'Sesión expirada';
    const message = payload?.error?.message
            || payload?.error
            || (status === 403 ? 'No tienes permisos para realizar esta acción.' : 'Vuelve a iniciar sesión.');

    try {
        if (swalAvailable()) {
            await window.Swal.fire({
                icon: status === 403 ? 'warning' : 'info',
                title,
                text: message,
                confirmButtonText: 'Ir a login'
            });
        } else {
            alert(`${title}\n${message}`);
        }
    } finally {
        // Evita loops: manda al login con bandera
        window.location.href = '/login?expired=1';
    }
}

async function apiCall(method, url, data = null, extraHeaders = null) {
    const options = {
        method,
        credentials: 'same-origin',
        headers: {
            'Content-Type': 'application/json'
        }
    };

    if (extraHeaders && typeof extraHeaders === 'object') {
        options.headers = {...options.headers, ...extraHeaders};
    }

    if (data !== null) {
        options.body = JSON.stringify(data);
    }

    const response = await fetch(url, options);

    // Manejo centralizado de auth en endpoints /api/**
    if (response.status === 401 || response.status === 403) {
        let payload = null;
        try {
            const contentType = response.headers.get('content-type') || '';
            if (contentType.includes('application/json')) {
                payload = await response.json();
            }
        } catch (e) {
            payload = null;
        }

        const fallback = payload || {
            success: false,
            error: {message: response.status === 403 ? 'Prohibido' : 'No autorizado'}
        };

        await handleAuthFailure(response.status, fallback);
        return fallback;
    }

    const contentType = response.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
        return await response.json();
    }

    // Fallback: si Spring Security responde 401/403 sin JSON, devolvemos un objeto estándar
    if (response.status === 401 || response.status === 403) {
        const payload = {success: false, error: {message: response.status === 401 ? 'No autorizado' : 'Prohibido'}};
        await handleAuthFailure(response.status, payload);
        return payload;
    }

    const text = await response.text();
    return {success: false, error: text || 'Respuesta no JSON'};
}

async function authenticateCard(numeroTarjeta, nip) {
    return apiCall('POST', `${API_PREFIX}/atm/autenticar`, {
        data: {tarjeta: numeroTarjeta, nip}
    });
}

async function getBalance(idCuenta, token) {
    const headers = token ? {Authorization: `Bearer ${token}`} : null;
    return apiCall('GET', `${API_PREFIX}/atm/saldo/${idCuenta}`, null, headers);
}

async function withdraw(codigoCajero, numeroTarjeta, nip, montoCentavos, token) {
    const headers = token ? {Authorization: `Bearer ${token}`} : null;
    return apiCall('POST', `${API_PREFIX}/atm/retirar`, {
        data: {codigoCajero, tarjeta: numeroTarjeta, nip, montoCentavos}
    }, headers);
}

async function getATMs() {
    return apiCall('GET', `${API_PREFIX}/cajeros`);
}

async function getUsers() {
    return apiCall('GET', `${API_PREFIX}/usuarios`);
}

async function reloadATM(codigoCajero) {
    return apiCall('POST', `${API_PREFIX}/admin/cajeros/${codigoCajero}/recargar`);
}

async function reloadAllATMs() {
    return apiCall('POST', `${API_PREFIX}/admin/cajeros/recargar-todos`);
}

/**
 * Convertir centavos a dinero decimal
 * @param {number} centavos 
 * @returns {string}
 */
function formatCurrency(centavos) {
    const n = Number(centavos);
    if (!Number.isFinite(n)) {
        return '$0.00';
    }
    return '$' + _moneyFormatter.format(n / 100);
}

/**
 * Convertir dinero decimal a centavos
 * @param {number} dinero 
 * @returns {number}
 */
function toCentavos(dinero) {
    return Math.round(dinero * 100);
}
