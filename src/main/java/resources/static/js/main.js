// Game state
let gameState = {
    track: null,
    grid: [],
    robotPosition: { x: 0, y: 0 },
    robotDirection: 'RIGHT', // UP, DOWN, LEFT, RIGHT
    commands: [],
    isExecuting: false,
    attempts: 0
};

// Direction mappings
const DIRECTIONS = {
    UP: { x: 0, y: -1, rotation: 0 },
    RIGHT: { x: 1, y: 0, rotation: 90 },
    DOWN: { x: 0, y: 1, rotation: 180 },
    LEFT: { x: -1, y: 0, rotation: 270 }
};

const ROTATION_MAP = {
    UP: { left: 'LEFT', right: 'RIGHT' },
    RIGHT: { left: 'UP', right: 'DOWN' },
    DOWN: { left: 'RIGHT', right: 'LEFT' },
    LEFT: { left: 'DOWN', right: 'UP' }
};

// Initialize game
document.addEventListener('DOMContentLoaded', async () => {
    await loadRandomTrack();
    setupEventListeners();
});

// Setup event listeners
function setupEventListeners() {
    // Movement buttons
    document.querySelectorAll('.move-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const move = btn.dataset.move;
            if (move === 'loop') {
                addLoopCommand();
            } else {
                addCommand(move);
            }
        });
    });
    
    // Execute button
    document.getElementById('executeBtn').addEventListener('click', executeCommands);
    
    // Reset button
    document.getElementById('resetBtn').addEventListener('click', resetGame);
    
    // Clear commands button
    document.getElementById('clearCommands').addEventListener('click', clearCommands);
    
    // Modal close button
    document.getElementById('modalClose').addEventListener('click', () => {
        document.getElementById('messageModal').classList.remove('active');
        resetGame();
    });
}

// Load random track from API
async function loadRandomTrack() {
    try {
        const response = await fetch('/api/tracks/random');
        if (!response.ok) throw new Error('Failed to load track');
        
        const track = await response.json();
        gameState.track = track;
        gameState.grid = JSON.parse(track.gridData);
        gameState.robotPosition = { x: track.startX, y: track.startY };
        gameState.robotDirection = track.startDirection;
        
        renderGrid();
        updateRobotPosition();
    } catch (error) {
        console.error('Error loading track:', error);
        alert('Error al cargar la pista. Por favor recarga la página.');
    }
}

// Render the game grid
function renderGrid() {
    const gridElement = document.getElementById('grid');
    gridElement.innerHTML = '';
    
    const { gridHeight, gridWidth } = gameState.track;
    gridElement.style.gridTemplateColumns = `repeat(${gridWidth}, 1fr)`;
    
    for (let y = 0; y < gridHeight; y++) {
        for (let x = 0; x < gridWidth; x++) {
            const cell = document.createElement('div');
            cell.className = 'grid-cell';
            cell.dataset.x = x;
            cell.dataset.y = y;
            
            if (gameState.grid[y] && gameState.grid[y][x]) {
                cell.classList.add('path');
            }
            
            gridElement.appendChild(cell);
        }
    }
}

// Update robot position on the grid
function updateRobotPosition(animate = false) {
    const robot = document.getElementById('robot');
    const { x, y } = gameState.robotPosition;
    const cellSize = 85; // 80px + 5px gap
    
    const rotation = DIRECTIONS[gameState.robotDirection].rotation;
    
    robot.style.left = `${x * cellSize + 10}px`;
    robot.style.top = `${y * cellSize + 10}px`;
    robot.style.transform = `rotate(${rotation}deg)`;
}

// Add command to the list
function addCommand(command) {
    gameState.commands.push({ type: command, isLoop: false });
    renderCommands();
}

// Add loop command
function addLoopCommand() {
    const loopStart = { type: 'loop_start', isLoop: true };
    const loopEnd = { type: 'loop_end', isLoop: true };
    gameState.commands.push(loopStart, loopEnd);
    renderCommands();
}

// Render commands list
function renderCommands() {
    const commandsList = document.getElementById('commandsList');
    commandsList.innerHTML = '';
    
    if (gameState.commands.length === 0) {
        commandsList.innerHTML = '<li style="border: none; background: transparent;">No hay comandos programados</li>';
        return;
    }
    
    gameState.commands.forEach((cmd, index) => {
        const li = document.createElement('li');
        
        if (cmd.type === 'loop_start') {
            li.textContent = '🔁 Inicio Bucle';
            li.className = 'loop-command';
        } else if (cmd.type === 'loop_end') {
            li.textContent = '🔁 Fin Bucle';
            li.className = 'loop-command';
        } else {
            const icon = cmd.type === 'forward' ? '⬆️' : cmd.type === 'left' ? '⬅️' : '➡️';
            const text = cmd.type === 'forward' ? 'Adelante' : cmd.type === 'left' ? 'Izquierda' : 'Derecha';
            li.textContent = `${icon} ${text}`;
            
            if (cmd.isLoop) {
                li.classList.add('loop-command');
            }
        }
        
        const removeBtn = document.createElement('button');
        removeBtn.textContent = '✕';
        removeBtn.className = 'remove-cmd';
        removeBtn.addEventListener('click', () => removeCommand(index));
        
        li.appendChild(removeBtn);
        commandsList.appendChild(li);
    });
}

// Remove command
function removeCommand(index) {
    gameState.commands.splice(index, 1);
    renderCommands();
}

// Clear all commands
function clearCommands() {
    gameState.commands = [];
    renderCommands();
}

// Execute commands
async function executeCommands() {
    if (gameState.commands.length === 0) {
        alert('No hay comandos para ejecutar');
        return;
    }
    
    if (gameState.isExecuting) return;
    
    gameState.isExecuting = true;
    gameState.attempts++;
    document.getElementById('executeBtn').disabled = true;
    
    // Expand loop commands
    const expandedCommands = expandLoopCommands(gameState.commands);
    
    // Execute each command
    for (const cmd of expandedCommands) {
        if (cmd.type === 'forward') {
            const success = await moveForward();
            if (!success) {
                showMessage('❌ ¡Ups!', 'El robot se salió del camino. ¡Intenta de nuevo!', false);
                gameState.isExecuting = false;
                document.getElementById('executeBtn').disabled = false;
                recordGameSession(false);
                return;
            }
        } else if (cmd.type === 'left') {
            turnLeft();
        } else if (cmd.type === 'right') {
            turnRight();
        }
        
        await sleep(600);
    }
    
    // Check if reached the end of the track
    const reachedEnd = checkVictory();
    showMessage(
        reachedEnd ? '🎉 ¡Felicitaciones!' : '❌ Casi lo logras',
        reachedEnd ? '¡Has completado la misión exitosamente!' : 'No llegaste al final del camino. ¡Intenta de nuevo!',
        reachedEnd
    );
    
    gameState.isExecuting = false;
    document.getElementById('executeBtn').disabled = false;
    recordGameSession(reachedEnd);
}

// Expand loop commands
function expandLoopCommands(commands) {
    const expanded = [];
    let inLoop = false;
    let loopCommands = [];
    
    for (const cmd of commands) {
        if (cmd.type === 'loop_start') {
            inLoop = true;
            loopCommands = [];
        } else if (cmd.type === 'loop_end') {
            inLoop = false;
            // Execute loop commands twice
            expanded.push(...loopCommands, ...loopCommands);
        } else if (inLoop) {
            loopCommands.push(cmd);
        } else {
            expanded.push(cmd);
        }
    }
    
    return expanded;
}

// Move robot forward
async function moveForward() {
    const direction = DIRECTIONS[gameState.robotDirection];
    const newX = gameState.robotPosition.x + direction.x;
    const newY = gameState.robotPosition.y + direction.y;
    
    // Check if new position is valid
    if (!isValidPosition(newX, newY)) {
        document.getElementById('robot').classList.add('error');
        await sleep(500);
        document.getElementById('robot').classList.remove('error');
        return false;
    }
    
    gameState.robotPosition = { x: newX, y: newY };
    updateRobotPosition(true);
    return true;
}

// Turn left
function turnLeft() {
    gameState.robotDirection = ROTATION_MAP[gameState.robotDirection].left;
    updateRobotPosition(true);
}

// Turn right
function turnRight() {
    gameState.robotDirection = ROTATION_MAP[gameState.robotDirection].right;
    updateRobotPosition(true);
}

// Check if position is valid (within grid and on path)
function isValidPosition(x, y) {
    if (x < 0 || y < 0 || y >= gameState.grid.length || x >= gameState.grid[0].length) {
        return false;
    }
    return gameState.grid[y][x] === true;
}

// Check victory condition
function checkVictory() {
    const { x, y } = gameState.robotPosition;
    const { gridWidth, gridHeight } = gameState.track;
    
    // Check if at the edge of a path or at a designated end point
    // For simplicity, we'll check if the robot has moved significantly
    const startX = gameState.track.startX;
    const startY = gameState.track.startY;
    
    return (Math.abs(x - startX) > 1 || Math.abs(y - startY) > 1) && isValidPosition(x, y);
}

// Show message modal
function showMessage(title, message, isSuccess) {
    document.getElementById('modalTitle').textContent = title;
    document.getElementById('modalMessage').textContent = message;
    document.getElementById('messageModal').classList.add('active');
}

// Reset game
function resetGame() {
    gameState.robotPosition = { x: gameState.track.startX, y: gameState.track.startY };
    gameState.robotDirection = gameState.track.startDirection;
    gameState.commands = [];
    gameState.attempts = 0;
    
    renderCommands();
    updateRobotPosition();
    document.getElementById('executeBtn').disabled = false;
}

// Record game session
async function recordGameSession(completed) {
    try {
        await fetch('/api/game/session', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                trackId: gameState.track.id,
                completed: completed,
                attempts: gameState.attempts
            })
        });
    } catch (error) {
        console.error('Error recording session:', error);
    }
}

// Utility function to sleep
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}