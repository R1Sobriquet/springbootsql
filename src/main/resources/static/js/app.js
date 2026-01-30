// API Base URLs
const API = {
    produits: '/catalogue/produits',
    users: '/demo',
    commandes: '/api/commandes'
};

// Data storage
let produits = [];
let users = [];
let commandes = [];

// Initialize app
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initStatusTabs();
    loadDashboard();
});

// Navigation
function initNavigation() {
    document.querySelectorAll('.nav-link').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const section = e.target.dataset.section;
            showSection(section);
        });
    });
}

function showSection(sectionId) {
    // Update nav
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.toggle('active', link.dataset.section === sectionId);
    });

    // Update sections
    document.querySelectorAll('.section').forEach(section => {
        section.classList.toggle('active', section.id === sectionId);
    });

    // Load data for section
    switch(sectionId) {
        case 'dashboard':
            loadDashboard();
            break;
        case 'produits':
            loadProduits();
            break;
        case 'utilisateurs':
            loadUsers();
            break;
        case 'commandes':
            loadCommandes();
            break;
    }
}

// Status tabs for orders
function initStatusTabs() {
    document.querySelectorAll('#status-tabs .tab').forEach(tab => {
        tab.addEventListener('click', () => {
            document.querySelectorAll('#status-tabs .tab').forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            const status = tab.dataset.status;
            filterCommandes(status);
        });
    });
}

// Dashboard
async function loadDashboard() {
    try {
        const [produitsRes, usersRes, commandesRes] = await Promise.all([
            fetch(API.produits),
            fetch(API.users + '/all'),
            fetch(API.commandes)
        ]);

        produits = await produitsRes.json();
        users = await usersRes.json();
        commandes = await commandesRes.json();

        // Update stats
        document.getElementById('stat-produits').textContent = produits.length;
        document.getElementById('stat-users').textContent = users.length;
        document.getElementById('stat-commandes').textContent = commandes.length;

        const revenue = commandes.reduce((sum, c) => sum + (c.total || 0), 0);
        document.getElementById('stat-revenue').textContent = revenue.toFixed(2) + ' €';

        // Recent orders
        const recentOrders = commandes.slice(0, 5);
        document.getElementById('recent-orders').innerHTML = recentOrders.length ?
            recentOrders.map(c => `
                <tr>
                    <td>#${c.id}</td>
                    <td>${c.user?.name || 'N/A'}</td>
                    <td>${(c.total || 0).toFixed(2)} €</td>
                    <td><span class="badge badge-status" style="background-color: ${c.statut?.color}">${c.statut?.label}</span></td>
                </tr>
            `).join('') : '<tr><td colspan="4" class="text-center">Aucune commande</td></tr>';

        // Popular wines
        const sortedWines = [...produits].sort((a, b) => (b.stock || 0) - (a.stock || 0)).slice(0, 5);
        document.getElementById('popular-wines').innerHTML = sortedWines.length ?
            sortedWines.map(p => `
                <tr>
                    <td>${p.nom}</td>
                    <td>${p.region}</td>
                    <td>${p.stock}</td>
                    <td>${p.prix?.toFixed(2)} €</td>
                </tr>
            `).join('') : '<tr><td colspan="4" class="text-center">Aucun produit</td></tr>';

    } catch (error) {
        console.error('Error loading dashboard:', error);
        showAlert('Erreur lors du chargement du dashboard', 'danger');
    }
}

// Produits
async function loadProduits() {
    try {
        const res = await fetch(API.produits);
        produits = await res.json();
        renderProduits();
    } catch (error) {
        console.error('Error loading produits:', error);
        showAlert('Erreur lors du chargement des produits', 'danger');
    }
}

function renderProduits() {
    const container = document.getElementById('produits-list');

    if (!produits.length) {
        container.innerHTML = `
            <div class="empty-state">
                <p>Aucun produit dans le catalogue</p>
                <button class="btn btn-wine mt-2" onclick="openModal('produit')">Ajouter un vin</button>
            </div>
        `;
        return;
    }

    container.innerHTML = produits.map(p => `
        <div class="product-card">
            <div class="product-header">
                <h4>${p.nom}</h4>
                <span class="region">${p.region} - ${p.annee}</span>
            </div>
            <div class="product-body">
                <div class="product-info">
                    <span class="product-price">${p.prix?.toFixed(2)} €</span>
                    <span class="product-stock">📦 ${p.stock} en stock</span>
                </div>
                ${p.notes?.length ? `
                    <div class="product-notes">
                        ${p.notes.map(n => `<span class="note-tag">${n}</span>`).join('')}
                    </div>
                ` : ''}
                <div class="btn-group mt-2">
                    <button class="btn btn-sm btn-primary" onclick="editProduit('${p.nom}')">Modifier</button>
                    <button class="btn btn-sm btn-danger" onclick="deleteProduit('${p.nom}')">Supprimer</button>
                </div>
            </div>
        </div>
    `).join('');
}

async function submitProduit(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    const produit = {
        nom: formData.get('nom'),
        annee: parseInt(formData.get('annee')),
        region: formData.get('region'),
        prix: parseFloat(formData.get('prix')),
        stock: parseInt(formData.get('stock')),
        notes: formData.get('notes') ? formData.get('notes').split(',').map(n => n.trim()) : []
    };

    try {
        const res = await fetch(API.produits, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(produit)
        });

        if (res.ok) {
            showAlert('Produit ajouté avec succès', 'success');
            closeModal('produit');
            form.reset();
            loadProduits();
        } else {
            showAlert('Erreur lors de l\'ajout du produit', 'danger');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Erreur lors de l\'ajout du produit', 'danger');
    }
}

async function deleteProduit(nom) {
    if (!confirm(`Supprimer le produit "${nom}" ?`)) return;

    try {
        const res = await fetch(`${API.produits}/${encodeURIComponent(nom)}`, {
            method: 'DELETE'
        });

        if (res.ok) {
            showAlert('Produit supprimé', 'success');
            loadProduits();
        } else {
            showAlert('Erreur lors de la suppression', 'danger');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Erreur lors de la suppression', 'danger');
    }
}

function editProduit(nom) {
    showAlert('Fonctionnalité en cours de développement', 'info');
}

// Users
async function loadUsers() {
    try {
        const res = await fetch(API.users + '/all');
        users = await res.json();
        renderUsers();
    } catch (error) {
        console.error('Error loading users:', error);
        showAlert('Erreur lors du chargement des utilisateurs', 'danger');
    }
}

function renderUsers() {
    const tbody = document.getElementById('users-list');

    if (!users.length) {
        tbody.innerHTML = '<tr><td colspan="4" class="text-center">Aucun utilisateur</td></tr>';
        return;
    }

    tbody.innerHTML = users.map(u => `
        <tr>
            <td>${u.id}</td>
            <td>${u.name}</td>
            <td>${u.email}</td>
            <td>
                <div class="btn-group">
                    <button class="btn btn-sm btn-primary" onclick="viewUserOrders(${u.id})">Commandes</button>
                </div>
            </td>
        </tr>
    `).join('');
}

async function submitUser(e) {
    e.preventDefault();
    const form = e.target;
    const formData = new FormData(form);

    try {
        const params = new URLSearchParams();
        params.append('name', formData.get('name'));
        params.append('email', formData.get('email'));

        const res = await fetch(API.users + '/add', {
            method: 'POST',
            body: params
        });

        if (res.ok) {
            showAlert('Utilisateur ajouté avec succès', 'success');
            closeModal('user');
            form.reset();
            loadUsers();
        } else {
            showAlert('Erreur lors de l\'ajout de l\'utilisateur', 'danger');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Erreur lors de l\'ajout de l\'utilisateur', 'danger');
    }
}

function viewUserOrders(userId) {
    showSection('commandes');
    // Filter by user could be added here
}

// Commandes
async function loadCommandes() {
    try {
        const res = await fetch(API.commandes);
        commandes = await res.json();
        renderCommandes();
        loadSelectOptions();
    } catch (error) {
        console.error('Error loading commandes:', error);
        showAlert('Erreur lors du chargement des commandes', 'danger');
    }
}

function filterCommandes(status) {
    if (status === 'all') {
        renderCommandes();
    } else {
        const filtered = commandes.filter(c => c.statut?.code === status);
        renderCommandes(filtered);
    }
}

function renderCommandes(data = commandes) {
    const container = document.getElementById('commandes-list');

    if (!data.length) {
        container.innerHTML = `
            <div class="empty-state">
                <p>Aucune commande</p>
                <button class="btn btn-success mt-2" onclick="openModal('commande')">Créer une commande</button>
            </div>
        `;
        return;
    }

    container.innerHTML = data.map(c => `
        <div class="order-card">
            <div class="order-header">
                <div>
                    <span class="order-id">Commande #${c.id}</span>
                    <span class="order-date">${c.dateFormatted || ''}</span>
                </div>
                <span class="badge badge-status" style="background-color: ${c.statut?.color}">${c.statut?.label}</span>
            </div>
            <div class="order-body">
                <div style="margin-bottom: 1rem;">
                    <strong>Client:</strong> ${c.user?.name || 'N/A'} (${c.user?.email || ''})
                </div>
                <div class="order-items">
                    ${c.lignes?.map(l => `
                        <div class="order-item">
                            <span>${l.produit?.nom} × ${l.quantite}</span>
                            <span>${l.sousTotal?.toFixed(2)} €</span>
                        </div>
                    `).join('') || ''}
                </div>
                <div class="order-total">
                    <span>Total (${c.nombreArticles || 0} articles)</span>
                    <span>${(c.total || 0).toFixed(2)} €</span>
                </div>
            </div>
            <div class="order-footer">
                <select class="form-control" style="width: auto;" onchange="updateStatut(${c.id}, this.value)">
                    <option value="EN_ATTENTE" ${c.statut?.code === 'EN_ATTENTE' ? 'selected' : ''}>En attente</option>
                    <option value="VALIDEE" ${c.statut?.code === 'VALIDEE' ? 'selected' : ''}>Validée</option>
                    <option value="EN_PREPARATION" ${c.statut?.code === 'EN_PREPARATION' ? 'selected' : ''}>En préparation</option>
                    <option value="EXPEDIEE" ${c.statut?.code === 'EXPEDIEE' ? 'selected' : ''}>Expédiée</option>
                    <option value="LIVREE" ${c.statut?.code === 'LIVREE' ? 'selected' : ''}>Livrée</option>
                    <option value="ANNULEE" ${c.statut?.code === 'ANNULEE' ? 'selected' : ''}>Annulée</option>
                </select>
                <button class="btn btn-sm btn-danger" onclick="deleteCommande(${c.id})">Supprimer</button>
            </div>
        </div>
    `).join('');
}

async function loadSelectOptions() {
    // Load users for select
    if (!users.length) {
        const res = await fetch(API.users + '/all');
        users = await res.json();
    }

    const userSelect = document.getElementById('select-user');
    userSelect.innerHTML = '<option value="">Sélectionner un client...</option>' +
        users.map(u => `<option value="${u.id}">${u.name} (${u.email})</option>`).join('');

    // Load produits for select
    if (!produits.length) {
        const res = await fetch(API.produits);
        produits = await res.json();
    }

    updateProduitSelects();
}

function updateProduitSelects() {
    document.querySelectorAll('#lignes-commande select[name="produit"]').forEach(select => {
        const currentValue = select.value;
        select.innerHTML = '<option value="">Sélectionner un vin...</option>' +
            produits.map(p => `<option value="${p.nom}" ${p.nom === currentValue ? 'selected' : ''}>${p.nom} - ${p.prix?.toFixed(2)} € (${p.stock} en stock)</option>`).join('');
    });
}

function addLigneCommande() {
    const container = document.getElementById('lignes-commande');
    const div = document.createElement('div');
    div.className = 'ligne-commande grid grid-2 gap-1 mb-1';
    div.innerHTML = `
        <select class="form-control" name="produit" required>
            <option value="">Sélectionner un vin...</option>
        </select>
        <input type="number" class="form-control" name="quantite" min="1" value="1" placeholder="Qté" required>
    `;
    container.appendChild(div);
    updateProduitSelects();
}

async function submitCommande(e) {
    e.preventDefault();
    const form = e.target;

    const userId = parseInt(document.getElementById('select-user').value);
    const lignes = [];

    document.querySelectorAll('#lignes-commande .ligne-commande').forEach(ligne => {
        const produit = ligne.querySelector('select[name="produit"]').value;
        const quantite = parseInt(ligne.querySelector('input[name="quantite"]').value);
        if (produit && quantite) {
            lignes.push({ nomProduit: produit, quantite });
        }
    });

    if (!userId || !lignes.length) {
        showAlert('Veuillez sélectionner un client et au moins un produit', 'danger');
        return;
    }

    try {
        const res = await fetch(API.commandes, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ userId, lignes })
        });

        if (res.ok) {
            showAlert('Commande créée avec succès', 'success');
            closeModal('commande');
            // Reset form
            document.getElementById('lignes-commande').innerHTML = `
                <div class="ligne-commande grid grid-2 gap-1 mb-1">
                    <select class="form-control" name="produit" required>
                        <option value="">Sélectionner un vin...</option>
                    </select>
                    <input type="number" class="form-control" name="quantite" min="1" value="1" placeholder="Qté" required>
                </div>
            `;
            document.getElementById('select-user').value = '';
            loadCommandes();
        } else {
            const error = await res.json();
            showAlert(Array.isArray(error) ? error.join(', ') : error, 'danger');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Erreur lors de la création de la commande', 'danger');
    }
}

async function updateStatut(id, statut) {
    try {
        const res = await fetch(`${API.commandes}/${id}/statut?statut=${statut}`, {
            method: 'PUT'
        });

        if (res.ok) {
            showAlert('Statut mis à jour', 'success');
            loadCommandes();
        } else {
            showAlert('Erreur lors de la mise à jour', 'danger');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Erreur lors de la mise à jour', 'danger');
    }
}

async function deleteCommande(id) {
    if (!confirm('Supprimer cette commande ?')) return;

    try {
        const res = await fetch(`${API.commandes}/${id}`, {
            method: 'DELETE'
        });

        if (res.ok) {
            showAlert('Commande supprimée', 'success');
            loadCommandes();
        } else {
            showAlert('Erreur lors de la suppression', 'danger');
        }
    } catch (error) {
        console.error('Error:', error);
        showAlert('Erreur lors de la suppression', 'danger');
    }
}

// Modal functions
function openModal(type) {
    document.getElementById(`modal-${type}`).classList.add('active');
    if (type === 'commande') {
        loadSelectOptions();
    }
}

function closeModal(type) {
    document.getElementById(`modal-${type}`).classList.remove('active');
}

// Close modal on overlay click
document.querySelectorAll('.modal-overlay').forEach(overlay => {
    overlay.addEventListener('click', (e) => {
        if (e.target === overlay) {
            overlay.classList.remove('active');
        }
    });
});

// Alert function
function showAlert(message, type = 'info') {
    const container = document.getElementById('alert-container');
    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.innerHTML = message;
    container.appendChild(alert);

    setTimeout(() => {
        alert.remove();
    }, 4000);
}
