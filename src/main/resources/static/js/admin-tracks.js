let currentGrid = [];
let editingTrackId = null;

document.addEventListener('DOMContentLoaded', () => {
    loadTracks();
    generateGrid();
    
    document.getElementById('trackForm').addEventListener('submit', saveTrack);
});

async function loadTracks() {
    try {
        const response = await fetch('/api/tracks');
        const tracks = await response.json();
        
        const tbody = document.querySelector('#tracksTable tbody');
        tbody.innerHTML = '';
        
        tracks.forEach(track => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${track.id}</td>
                <td>${track.name}</td>
                <td>${track.gridWidth}x${track.gridHeight}</td>
                <td>${track.active ? '✅ Activa' : '❌ Inactiva'}</td>
                <td>
                    <button class="action-btn btn-edit" onclick="editTrack(${track.id})">✏️ Editar</button>
                    <button class="action-btn btn-export" onclick="exportTrack(${track.id})">📥 Exportar</button>
                    <button class="action-btn btn-delete" onclick="deleteTrack(${track.id})">🗑️ Eliminar</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error('Error loading tracks:', error);
        alert('Error al cargar las pistas');
    }
}

function generateGrid() {
    const width = parseInt(document.getElementById('gridWidth').value);
    const height = parseInt(document.getElementById('gridHeight').value);
    
    currentGrid = Array(height).fill(null).map(() => Array(width).fill(false));
    
    const gridEditor = document.getElementById('gridEditor');
    gridEditor.innerHTML = '';
    gridEditor.style.gridTemplateColumns = `repeat(${width}, 1fr)`;
    
    for (let y = 0; y < height; y++) {
        for (let x = 0; x < width; x++) {
            const cell = document.createElement('div');
            cell.className = 'grid-editor-cell';
            cell.dataset.x = x;
            cell.dataset.y = y;
            cell.onclick = () => toggleCell(x, y, cell);
            gridEditor.appendChild(cell);
        }
    }
}

function toggleCell(x, y, cellElement) {
    currentGrid[y][x] = !currentGrid[y][x];
    cellElement.classList.toggle('active');
}

function showCreateModal() {
    editingTrackId = null;
    document.getElementById('modalTitle').textContent = 'Nueva Pista';
    document.getElementById('trackForm').reset();
    document.getElementById('trackId').value = '';
    generateGrid();
    document.getElementById('trackModal').classList.add('active');
}

async function editTrack(id) {
    try {
        const response = await fetch(`/api/tracks/${id}`);
        const track = await response.json();
        
        editingTrackId = id;
        document.getElementById('modalTitle').textContent = 'Editar Pista';
        document.getElementById('trackId').value = track.id;
        document.getElementById('trackName').value = track.name;
        document.getElementById('gridWidth').value = track.gridWidth;
        document.getElementById('gridHeight').value = track.gridHeight;
        document.getElementById('startX').value = track.startX;
        document.getElementById('startY').value = track.startY;
        document.getElementById('startDirection').value = track.startDirection;
        document.getElementById('isActive').checked = track.active;
        
        currentGrid = JSON.parse(track.gridData);
        renderGridFromData();
        
        document.getElementById('trackModal').classList.add('active');
    } catch (error) {
        console.error('Error loading track:', error);
        alert('Error al cargar la pista');
    }
}

function renderGridFromData() {
    const width = currentGrid[0].length;
    const height = currentGrid.length;
    
    const gridEditor = document.getElementById('gridEditor');
    gridEditor.innerHTML = '';
    gridEditor.style.gridTemplateColumns = `repeat(${width}, 1fr)`;
    
    for (let y = 0; y < height; y++) {
        for (let x = 0; x < width; x++) {
            const cell = document.createElement('div');
            cell.className = 'grid-editor-cell';
            if (currentGrid[y][x]) {
                cell.classList.add('active');
            }
            cell.dataset.x = x;
            cell.dataset.y = y;
            cell.onclick = () => toggleCell(x, y, cell);
            gridEditor.appendChild(cell);
        }
    }
}

async function saveTrack(e) {
    e.preventDefault();
    
    const trackData = {
        name: document.getElementById('trackName').value,
        gridWidth: parseInt(document.getElementById('gridWidth').value),
        gridHeight: parseInt(document.getElementById('gridHeight').value),
        gridData: JSON.stringify(currentGrid),
        startX: parseInt(document.getElementById('startX').value),
        startY: parseInt(document.getElementById('startY').value),
        startDirection: document.getElementById('startDirection').value,
        active: document.getElementById('isActive').checked
    };
    
    try {
        let response;
        if (editingTrackId) {
            response = await fetch(`/api/tracks/${editingTrackId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(trackData)
            });
        } else {
            response = await fetch('/api/tracks', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(trackData)
            });
        }
        
        if (response.ok) {
            alert('Pista guardada exitosamente');
            closeModal();
            loadTracks();
        } else {
            alert('Error al guardar la pista');
        }
    } catch (error) {
        console.error('Error saving track:', error);
        alert('Error al guardar la pista');
    }
}

async function deleteTrack(id) {
    if (!confirm('¿Estás seguro de eliminar esta pista?')) return;
    
    try {
        const response = await fetch(`/api/tracks/${id}`, { method: 'DELETE' });
        if (response.ok) {
            alert('Pista eliminada exitosamente');
            loadTracks();
        } else {
            alert('Error al eliminar la pista');
        }
    } catch (error) {
        console.error('Error deleting track:', error);
        alert('Error al eliminar la pista');
    }
}

async function exportTrack(id) {
    try {
        const response = await fetch(`/api/tracks/${id}/export`);
        const data = await response.json();
        
        const blob = new Blob([data.data], { type: 'application/json' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `pista-${id}.json`;
        a.click();
    } catch (error) {
        console.error('Error exporting track:', error);
        alert('Error al exportar la pista');
    }
}

function closeModal() {
    document.getElementById('trackModal').classList.remove('active');
}