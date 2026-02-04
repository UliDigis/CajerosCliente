/**
 * Cliente API simple para llamadas AJAX
 * Usa Fetch API moderna
 */

const API_BASE = 'http://localhost:8080';

/**
 * Hacer una llamada HTTP genérica
 * @param {string} method - GET, POST, etc
 * @param {string} url - URL relativa o absoluta
 * @param {object} data - Datos para enviar (opcional)
 * @returns {Promise}
 */
async function apiCall(method, url, data = null) {
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        }
    };

    // Agregar body si hay datos
    if (data) {
        options.body = JSON.stringify(data);
    }

    // Hacer la llamada
    const response = await fetch(url, options);
    const result = await response.json();

    return result;
}

/**
 * Login - obtener token JWT
 * @param {string} correo 
 * @param {string} password 
 * @returns {Promise}
 */
async function login(correo, password) {
    return apiCall('POST', `${API_BASE}/auth/login`, {
        data: { correo, password }
    });
}

/**
 * Obtener datos del usuario autenticado
 * @returns {Promise}
 */
async function getAuthMe() {
    const token = localStorage.getItem('token');
    const options = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    const response = await fetch(`${API_BASE}/auth/me`, options);
    return await response.json();
}

/**
 * Autenticar tarjeta con NIP
 * @param {string} tarjeta 
 * @param {string} nip 
 * @returns {Promise}
 */
async function authenticateCard(tarjeta, nip) {
    return apiCall('POST', `${API_BASE}/atm/autenticar`, {
        data: { tarjeta, nip }
    });
}

/**
 * Obtener saldo de una cuenta
 * @param {number} idCuenta 
 * @returns {Promise}
 */
async function getBalance(idCuenta) {
    const token = localStorage.getItem('token');
    const options = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    const response = await fetch(`${API_BASE}/atm/saldo/${idCuenta}`, options);
    return await response.json();
}

/**
 * Realizar retiro de efectivo
 * @param {string} codigoCajero 
 * @param {string} tarjeta 
 * @param {string} nip 
 * @param {number} montoCentavos 
 * @returns {Promise}
 */
async function withdraw(codigoCajero, tarjeta, nip, montoCentavos) {
    const token = localStorage.getItem('token');
    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
            data: { codigoCajero, tarjeta, nip, montoCentavos }
        })
    };

    const response = await fetch(`${API_BASE}/atm/retirar`, options);
    return await response.json();
}

/**
 * Obtener lista de cajeros disponibles
 * @returns {Promise}
 */
async function getATMs() {
    const response = await fetch(`${API_BASE}/cajeros`);
    return await response.json();
}

/**
 * Recargar un cajero (Admin)
 * @param {string} codigoCajero 
 * @returns {Promise}
 */
async function reloadATM(codigoCajero) {
    const token = localStorage.getItem('token');
    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    const response = await fetch(`${API_BASE}/admin/cajeros/${codigoCajero}/recargar`, options);
    return await response.json();
}

/**
 * Recargar todos los cajeros (Admin)
 * @returns {Promise}
 */
async function reloadAllATMs() {
    const token = localStorage.getItem('token');
    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    };

    const response = await fetch(`${API_BASE}/admin/cajeros/recargar-todos`, options);
    return await response.json();
}

/**
 * Convertir centavos a dinero decimal
 * @param {number} centavos 
 * @returns {string}
 */
function formatCurrency(centavos) {
    if (!centavos) return '$0.00';
    return '$' + (centavos / 100).toFixed(2);
}

/**
 * Convertir dinero decimal a centavos
 * @param {number} dinero 
 * @returns {number}
 */
function toCentavos(dinero) {
    return Math.round(dinero * 100);
}
