# 🎮 CS61B: Coin Rush - Build Your Own World Project

> A 2D tile-based world exploration game with procedural generation, line-of-sight mechanics, and coin collection gameplay.

Built for **UC Berkeley CS 61B: Data Structures** 🐻

📋 [**Project Specification**](https://sp25.datastructur.es/projects/proj3/)

## 🚀 Overview

Coin Rush is a sophisticated 2D tile-based exploration game featuring procedurally generated worlds, intelligent room connectivity, and advanced gameplay mechanics. Players navigate through randomly generated dungeons to collect coins while experiencing realistic line-of-sight and fog-of-war systems.

### ✨ Key Features

- **🌍 Procedural World Generation**: Unique worlds created using seeded random algorithms
- **🏠 Intelligent Room Placement**: Rooms connected via minimum spanning tree hallway generation
- **👁️ Line-of-Sight System**: Realistic vision mechanics with fog-of-war
- **💰 Coin Collection Gameplay**: Collect all coins to win the game
- **💾 Save/Load System**: Persistent game state with input replay
- **🎯 Interactive UI**: Mouse-over tile descriptions and real-time HUD
- **🎨 Rich Tile Graphics**: Comprehensive tile system with multiple terrain types

## 🌟 Ambition Features

This project implements two advanced **Ambition Features** from the BYOW specification:

### 🔍 **Line of Sight**
- **Ray casting algorithm** - 360-degree vision simulation from avatar position
- **Wall occlusion** - Walls block line-of-sight realistically  
- **Fog-of-war system** - Previously explored areas remain dimly visible
- **Dynamic updates** - Vision recalculated on every movement
- **Toggle functionality** - Press 'L' to switch between full vision and line-of-sight modes

### 💰 **Coin Collecting**
- **Strategic placement** - Coins distributed throughout the world away from starting position
- **Collection mechanics** - Automatic pickup when avatar moves onto coin tiles
- **Progress tracking** - Real-time HUD display showing collected/total coins
- **Victory condition** - Game completion when all coins are collected
- **Persistent state** - Coin collection progress saved with game state

These features add significant gameplay depth and demonstrate advanced programming concepts including computer graphics algorithms and game state management.

## 🛠️ Technology Stack

### Core Technologies
- **Java 17+** - Modern object-oriented programming
- **Princeton StdDraw** - 2D graphics rendering and input handling
- **Princeton Algorithms Library** - Advanced data structures and utilities

### Key Algorithms & Data Structures
- **Kruskal's MST Algorithm** - Optimal room connectivity
- **Disjoint Set (Union-Find)** - Efficient connectivity tracking
- **Ray Casting** - Line-of-sight visibility calculations
- **Procedural Generation** - Seeded random world creation
- **State Management** - Save/load game functionality

## 📦 Installation & Setup

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- IntelliJ IDEA (recommended) or any Java IDE
- Princeton CS library (included in project)

### 1. Clone the Repository
```bash
git clone <your-repository-url>
cd proj3
```

### 2. Open in IntelliJ IDEA
1. Open IntelliJ IDEA
2. File → Open → Navigate to project directory
3. Select the `proj3` folder
4. Wait for project indexing to complete

### 3. Configure Libraries
1. Ensure `library-sp25` is properly linked (should be automatic)
2. Verify Java SDK is set to version 17+
3. Check that all source folders are marked correctly

### 4. Run the Game
```bash
# Via IDE: Run Main.java
# Via command line:
javac -cp library-sp25 src/core/Main.java
java -cp .:library-sp25 core.Main
```

## 🎯 How to Play

### 1. **Main Menu**
- **N** - Start a new game (enter seed)
- **L** - Load saved game
- **Q** - Quit game

### 2. **Seed Input**
- Enter any numeric seed followed by **S**
- Seeds generate deterministic worlds
- Try different seeds for variety!

### 3. **Gameplay Controls**
- **WASD** - Move avatar (@ symbol)
- **L** - Toggle line-of-sight mode
- **:Q** - Save and quit game

### 4. **Objective**
- Navigate the procedurally generated world
- Collect all coins ($) to win
- Use line-of-sight strategically to explore efficiently

### 5. **UI Elements**
- **Mouse hover** - Shows tile descriptions
- **Coin counter** - Tracks collection progress
- **Line-of-sight indicator** - Shows current vision mode

## 🏗️ Architecture & Design

### Core Components

```
src/core/
├── Main.java              # Application entry point
├── Game.java              # Main game loop and logic
├── GameMenu.java          # Menu system and navigation
├── World.java             # World generation and management
├── Avatar.java            # Player character and movement
├── Room.java              # Individual room representation
├── HallwayGenerator.java  # MST-based room connectivity
├── Coin.java              # Coin placement and collection
├── Vision.java            # Line-of-sight calculations
├── HUD.java               # User interface overlay
└── DisjointSet.java       # Union-Find data structure
```

### Advanced Systems

#### **Procedural World Generation**
```java
public void generateWorld(long seed) {
    initializeGrid();        // Create empty world
    placeRooms();           // Generate random rooms
    connectRooms();         // MST hallway connections
    addWalls();             // Surround with walls
    coin.placeCoins();      // Distribute collectibles
}
```

#### **Line-of-Sight Algorithm**
- **Ray casting** from avatar position
- **360-degree vision** with configurable radius
- **Wall occlusion** blocks line-of-sight
- **Fog-of-war** system remembers explored areas

#### **Save System**
- **Input recording** - All keystrokes saved
- **Deterministic replay** - Recreate exact game state
- **Seed persistence** - Consistent world generation

## 🎮 Game Mechanics Deep Dive

### World Generation Process
1. **Room Placement**: Random non-overlapping rectangular rooms
2. **Connectivity Analysis**: Calculate distances between all room pairs
3. **MST Generation**: Use Kruskal's algorithm for optimal connections
4. **Hallway Creation**: L-shaped corridors between connected rooms
5. **Wall Addition**: Surround all floor tiles with walls
6. **Coin Distribution**: Place collectibles away from starting area

### Vision System
- **Standard Mode**: Full world visibility
- **Line-of-Sight Mode**: Realistic vision simulation
- **Explored Areas**: Previously seen tiles remain dimly visible
- **Dynamic Updates**: Vision recalculated on movement

### Avatar Mechanics
- **Collision Detection**: Cannot move through walls
- **Coin Collection**: Automatic pickup on contact
- **Optimal Placement**: Starts at center-most floor tile
- **Movement Validation**: Boundary and obstacle checking

## 🔧 Technical Implementation

### Key Algorithms

#### **Minimum Spanning Tree (Kruskal's)**
```java
// Connect all rooms with minimal total hallway length
Collections.sort(edges, Comparator.comparingInt(e -> e.distance));
for (Edge edge : edges) {
    if (!disjointSet.isConnected(edge.from, edge.to)) {
        createHallway(rooms.get(edge.from), rooms.get(edge.to));
        disjointSet.union(edge.from, edge.to);
    }
}
```

#### **Ray Casting for Vision**
```java
private void castRay(int startX, int startY, double angle, int maxDistance) {
    double dx = Math.cos(angle), dy = Math.sin(angle);
    for (double dist = 0.2; dist <= maxDistance; dist += 0.2) {
        // Cast ray and check for wall occlusion
        if (worldGrid[xi][yi] == Tileset.WALL) break;
        visibilityGrid[xi][yi] = true;
    }
}
```

#### **Collision Detection**
```java
private boolean isValidMove(int newX, int newY, TETile[][] world) {
    return newX >= 0 && newX < world.length && 
           newY >= 0 && newY < world[0].length &&
           world[newX][newY] != Tileset.WALL;
}
```

### Data Structures Used
- **2D Arrays** - World grid representation
- **ArrayList** - Dynamic room storage
- **Disjoint Set** - Efficient connectivity queries
- **Priority Queue** - Implicit in MST algorithm
- **StringBuilder** - Input history recording

## 🎨 Visual Design

### Tile System
- **Avatar (@)** - Player character in white
- **Walls (#)** - Brown barriers with dark gray background
- **Floors (·)** - Green walkable areas
- **Coins ($)** - Yellow collectibles
- **Nothing ( )** - Black unexplored space

### UI Elements
- **Real-time mouse hover** - Dynamic tile descriptions
- **Coin progress tracker** - Shows collection status
- **Vision mode indicator** - Current sight settings
- **Victory screen** - Animated completion message

## 🏆 Advanced Features

### **Intelligent Room Generation**
- Ensures minimum spacing between rooms
- Validates placement within world boundaries
- Prevents overlapping structures
- Maintains architectural coherence

### **Sophisticated Pathfinding**
- L-shaped hallway connections
- Optimal connection point selection
- Minimal total corridor length
- Guaranteed world connectivity

### **Immersive Vision System**
- Realistic sight limitations
- Memory of explored areas
- Dynamic visibility updates
- Strategic gameplay element

## 🚀 Future Enhancements

### Planned Features
- **🗝️ Keys and Doors** - Locked areas requiring items
- **👹 Enemies** - Mobile obstacles and challenges
- **🎒 Inventory System** - Multiple item types
- **🌟 Power-ups** - Temporary ability enhancements
- **🎵 Sound Effects** - Audio feedback system
- **📊 Statistics** - Performance tracking and scoring

### Technical Improvements
- **🔄 Multithreading** - Smoother rendering and input
- **💾 Binary Save Format** - More efficient storage
- **🎮 Gamepad Support** - Alternative input methods
- **📱 Mobile Port** - Touch-based controls

## 🧪 Testing & Validation

### Automated Tests
- **World Generation** - Validates connectivity and structure
- **Collision Detection** - Movement boundary testing
- **Save/Load System** - State persistence verification
- **Vision Algorithms** - Line-of-sight accuracy checks

### Manual Testing Scenarios
- Various seed values for world generation
- Complete gameplay sessions with save/load
- Line-of-sight behavior in different configurations
- UI responsiveness and visual feedback

## 📚 Educational Value

### CS61B Concepts Demonstrated
- **Object-Oriented Design** - Clean class hierarchy and encapsulation
- **Data Structures** - Practical application of learned structures
- **Algorithm Implementation** - MST, Union-Find, ray casting
- **Software Engineering** - Modular design and maintainability
- **Testing Strategies** - Validation and edge case handling

### Skills Developed
- Complex system architecture
- Real-time graphics programming
- Game state management
- User interface design
- Algorithm optimization

## 🤝 Contributing

### Development Guidelines
- Follow Java naming conventions
- Maintain clear class separation
- Document public methods thoroughly
- Add tests for new features
- Preserve deterministic behavior

### Code Structure
- Keep classes focused and cohesive
- Use appropriate data structures
- Optimize for readability and performance
- Handle edge cases gracefully

## 📝 Course Context

**UC Berkeley CS 61B: Data Structures**
- **Project**: Build Your Own World (BYOW)
- **Semester**: Spring 2025
- **Focus**: Practical application of data structures and algorithms
- **Learning Objectives**: 
  - Complex software design
  - Algorithm implementation
  - Testing and validation
  - User interface development

## 🙏 Acknowledgments

- **Song He** - My incredible teammate who made this project possible
- **UC Berkeley CS 61B Staff** - Course instruction and project framework
- **Princeton University** - StdDraw library and CS algorithms
- **Java Community** - Comprehensive development ecosystem
- **Open Source Contributors** - Tools and libraries that made development possible

---

**CS61B: Coin Rush** - Exploring the intersection of data structures, algorithms, and interactive entertainment. 🎮✨

## 📞 Support

For questions about this implementation:
- Review the CS 61B course materials
- Check the Princeton CS library documentation
- Refer to Java official documentation
- Consult course discussion forums

*This project demonstrates advanced Java programming concepts and serves as a comprehensive example of game development using fundamental computer science principles.*
