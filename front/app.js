const app = document.getElementById('app');

let currentUser = null;
let contacts = [];
let socket;

const SERVER_URL = 'http://localhost:8080';

function renderLogin() {
    app.innerHTML = `
        <div class="container">
            <h1>P2P Chat</h1>
            <button onclick="renderLoginForm()">Вход</button>
            <button onclick="renderRegisterForm()">Регистрация</button>
        </div>
    `;
}

function renderLoginForm() {
    const app = document.getElementById('app');
    app.innerHTML = `
        <div class="container">
            <h2>Вход</h2>
            <input id="login" placeholder="Логин">
            <input id="password" type="password" placeholder="Пароль">
            <button onclick="login()">Войти</button>
            <button onclick="renderRegisterForm()">Регистрация</button>
        </div>
    `;
}

function renderRegisterForm() {
    const app = document.getElementById('app');
    app.innerHTML = `
        <div class="container">
            <h2>Регистрация</h2>
            <input id="registerUsername" placeholder="Логин">
            <input id="registerPassword" type="password" placeholder="Пароль">
            <button onclick="register()">Зарегистрироваться</button>
            <button onclick="renderLogin()">Назад</button>
        </div>
    `;
}

function renderMenu() {
    app.innerHTML = `
        <div class="container">
            <h2>Меню</h2>
            <button onclick="fetchContacts()">Список контактов</button>
            <button onclick="renderAddContact()">Добавить контакт</button>
        </div>
    `;
}


function renderContactList() {
    let contactHtml = contacts.map(contact => `
        <li onclick="createChat('${contact}')">${contact}</li>
    `).join('');

    app.innerHTML = `
        <div class="container">
            <h2>Ваши контакты</h2>
            <ul class="contact-list">${contactHtml}</ul>
            <button onclick="renderMenu()">Назад</button>
        </div>
    `;
}


function renderAddContact() {
    app.innerHTML = `
        <div class="container">
            <h2>Добавить контакт</h2>
            <input id="newContact" placeholder="Никнейм контакта">
            <button onclick="addContact()">Добавить</button>
            <button onclick="renderMenu()">Назад</button>
        </div>
    `;
}


function openChat(contact) {
    currentUser = contact;

    app.innerHTML = `
        <div class="container">
            <h2>Чат с ${contact}</h2>
            <textarea id="chatLog" readonly></textarea>
            <input id="messageInput" placeholder="Введите сообщение">
            <button onclick="sendMessage('${contact}')">Отправить</button>
            <button onclick="renderContactList()">Назад</button>
        </div>
    `;
}

// === Запросы к серверу ===

function connectWebSocket(username) {
    socket = new WebSocket(`ws://localhost:8080/ws/chat`);

    socket.onopen = () => {
        console.log('WebSocket подключен');
        socket.send(username); // Отправляем свой логин на сервер
    };

    socket.onmessage = (event) => {
        const chatLog = document.getElementById('chatLog');
        chatLog.value += `Собеседник: ${event.data}\n`;
    };

    socket.onclose = () => {
        console.log('WebSocket отключен');
    };
}

async function login() {
    const login = document.getElementById('login').value;
    const password = document.getElementById('password').value;

    const response = await fetch(`${SERVER_URL}/api/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: login, password: password })
    });

    if (response.ok) {
        currentUser = login;
        connectWebSocket(currentUser);
        renderMenu();
    } else {
        alert('Ошибка входа!');
    }
}

async function register() {
    const login = document.getElementById('registerUsername').value;
    const password = document.getElementById('registerPassword').value;

    const response = await fetch(`${SERVER_URL}/api/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: login, password: password })
    });

    if (response.ok) {
        alert('Регистрация успешна!');
        renderLoginForm();
    } else {
        alert('Ошибка регистрации!');
    }
}

async function fetchContacts() {
    const response = await fetch(`${SERVER_URL}/api/users/${currentUser}/contacts`);

    if (response.ok) {
        contacts = await response.json();
        renderContactList();
    } else {
        alert('Не удалось загрузить контакты');
    }
}


async function addContact() {
    const newContact = document.getElementById('newContact').value;

    const response = await fetch(`${SERVER_URL}/api/users/${currentUser}/contacts`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ currentUser , contactName: newContact })
    });

    if (response.ok) {
        alert(`Контакт ${newContact} добавлен!`);
        renderMenu();
    } else {
        alert('Ошибка добавления контакта!');
    }
}


async function createChat(contact) {
    const response = await fetch(`${SERVER_URL}/api/chats/create`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ user1: currentUser, user2: contact })
    });

    if (response.ok) {
        openChat(contact);
    } else {
        alert('Не удалось создать чат!');
    }
}


function sendMessage(contact) {
    const messageInput = document.getElementById('messageInput').value;
    const chatLog = document.getElementById('chatLog');

    if (messageInput) {
        chatLog.value += `Вы: ${messageInput}\n`;
        if (socket.readyState === WebSocket.OPEN) {
            socket.send(`${contact}:${messageInput}`); // Формат: recipient:message
        }
        document.getElementById('messageInput').value = '';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    renderLogin();
});