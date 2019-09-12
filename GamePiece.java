import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// Represents a LightEmAll game
class LightEmAll extends World {

  static final int PIECE_SIZE = 100;

  // a list of columns of GamePieces,
  // i.e., represents the board in column-major order
  ArrayList<ArrayList<GamePiece>> board;
  // a list of all nodes
  ArrayList<GamePiece> nodes;
  // a list of edges of the minimum spanning tree
  ArrayList<Edge> mst;
  // the width and height of the board
  int width;
  int height;
  // the current location of the power station,
  // as well as its effective radius
  int radius;
  Random rand;

  int powerStationX;
  int powerStationY;


  // Default constructor with a set height, width, and radius
  LightEmAll() {
    this.width = 10;
    this.height = 10;
    this.board = new ArrayList<ArrayList<GamePiece>>(width);
    this.nodes = new ArrayList<GamePiece>();
    this.makeFractalBoard();
    this.getAllNodes();
    this.updateNeighbors();
    this.connectNeighbors();

    // the current location of the power station,
    this.powerStationX = 0;
    this.powerStationY = 0;
    this.board.get(0).get(0).powerStation = true;
    //powerStation's effective radius (set to a constant for Part 2)
    this.radius = 12;
    //(this.gameDiameter() /2) + 1;
    this.givePower();
    mst = null;
  }
  
//Constructor used for playing the game
  LightEmAll(int width, int height) {
    this.width = width;
    this.height = height;
    this.rand = new Random();
    this.board = new ArrayList<ArrayList<GamePiece>>(width);
    this.nodes = new ArrayList<GamePiece>();
    this.makeBaseBoard();
    this.getAllNodes();
    this.mst = this.createEdgesInTree();
    this.updateMapWithEdges();
    this.updateNeighbors();
    this.connectNeighbors();

    // the current location of the power station,
    this.powerStationX = 0;
    this.powerStationY = 0;
    this.board.get(0).get(0).powerStation = true;
    this.furthestNodeFrom(this.furthestNodeFrom(this.mst.get(0).from));
    this.scrambleBoard(this.rand);
    this.givePower();
  }

  //Constructor used for testing with a set radius and given height and width
  LightEmAll(int width, int height, boolean test) {
    this.width = width;
    this.height = height;
    this.board = new ArrayList<ArrayList<GamePiece>>(width);
    this.nodes = new ArrayList<GamePiece>();
    this.makeSetBoard();
    this.getAllNodes();
    this.updateNeighbors();
    this.connectNeighbors();

    // the current location of the power station,
    this.powerStationX = 0;
    this.powerStationY = 0;
    this.board.get(0).get(0).powerStation = true;
    //powerStation's effective radius (set to a constant for Part 2)
    this.radius = 4;
    this.givePower();
    mst = null;
  }

  //Constructor used for testing with a set radius and given height and width and a Random
  LightEmAll(int width, int height, Random rand) {
    this.width = width;
    this.height = height;
    this.rand = rand;
    this.board = new ArrayList<ArrayList<GamePiece>>(width);
    this.nodes = new ArrayList<GamePiece>();
    this.makeBaseBoard();
    this.getAllNodes();
    this.mst = this.createEdgesInTree();
    this.updateMapWithEdges();
    this.updateNeighbors();
    this.furthestNodeFrom(this.furthestNodeFrom(this.mst.get(0).from));

    // the current location of the power station,
    this.powerStationX = 0;
    this.powerStationY = 0;
    this.board.get(0).get(0).powerStation = true;
    //powerStation's effective radius (set to a constant for Part 2)
    //this.radius = (this.gameDiameter() / 2) + 1;
    this.scrambleBoard(this.rand);
    this.connectNeighbors();
    this.givePower();
  }
  
//Constructor used for testing with a set radius and given height and width and a Random
  LightEmAll(boolean isScrambled, int width, int height, Random rand) {
    this.width = width;
    this.height = height;
    this.rand = rand;
    this.board = new ArrayList<ArrayList<GamePiece>>(width);
    this.nodes = new ArrayList<GamePiece>();
    this.makeBaseBoard();
    this.getAllNodes();
    this.mst = this.createEdgesInTree();
    this.updateMapWithEdges();
    this.updateNeighbors();
    this.connectNeighbors();

    // the current location of the power station,
    this.powerStationX = 0;
    this.powerStationY = 0;
    this.board.get(0).get(0).powerStation = true;
    //powerStation's effective radius (set to a constant for Part 2)
    this.radius = (this.gameDiameter() / 2) + 1;
    this.givePower();
  }

  //Constructor used for testing that is void of method calls that set up the game
  LightEmAll(int width, int height, int radius) {
    this.width = width;
    this.height = height;
    this.board = new ArrayList<ArrayList<GamePiece>>(width);
    nodes = null;
    mst = null;
    // the current location of the power station,
    // as well as its effective radius (set to a constant for Part 2)
    this.radius = radius;
    this.powerStationX = 2;
    this.powerStationY = 1;
  }

  // Makes a default 4x4 game board
  public void makeSetBoard() {
    // All rows
    board.add(new ArrayList<GamePiece>(4));
    board.add(new ArrayList<GamePiece>(4));
    board.add(new ArrayList<GamePiece>(4));
    board.add(new ArrayList<GamePiece>(4));

    // Column 0
    this.board.get(0).add(new GamePiece(0,0, false, false, true, false)); 
    this.board.get(0).add(new GamePiece(0,1, true, true, true, false)); 
    this.board.get(0).add(new GamePiece(0,2, true, false, true, false)); 
    this.board.get(0).add(new GamePiece(0,3, true, false, false, false)); 

    // Column 1
    this.board.get(1).add(new GamePiece(1,0, false, false, true, false)); 
    this.board.get(1).add(new GamePiece(1,1, true, true, true, true)); 
    this.board.get(1).add(new GamePiece(1,2, true, false, true, false)); 
    this.board.get(1).add(new GamePiece(1,3, true, false, false, false));

    // Column 2
    this.board.get(2).add(new GamePiece(2,0, false, false, true, false)); 
    this.board.get(2).add(new GamePiece(2,1, true, true, true, true)); 
    this.board.get(2).add(new GamePiece(2,2, true, false, true, false)); 
    this.board.get(2).add(new GamePiece(2,3, true, false, false, false)); 

    // Column 3
    this.board.get(3).add(new GamePiece(3,0, false, false, true, false)); 
    this.board.get(3).add(new GamePiece(3,1, true, false, true, true)); 
    this.board.get(3).add(new GamePiece(3,2, true, false, true, false)); 
    this.board.get(3).add(new GamePiece(3,3, true, false, false, false));

    this.board.get(2).get(1).powerStation = true;
  }
 
  
  // scrambles the edges in this LightEmAll
  public void scrambleBoard(Random random) {
    
    for (GamePiece p : this.nodes) {
      Integer tempInt = random.nextInt(4);
      for(int i = 0; i < tempInt; i++) {
        p.rotateGamePiece();
      }
    }
  }

  //Ends the game if all of the nodes are powered
  public WorldEnd worldEnds() {
    WorldScene scene = this.makeScene();

    boolean temp = true;

    for (GamePiece g : this.nodes) {
      if (!g.isPowered) {
        temp = false;
      }
    }

    if (temp) {
      scene.placeImageXY(new TextImage("You Win!", this.width * LightEmAll.PIECE_SIZE / 
          13, Color.red), 
          this.width * LightEmAll.PIECE_SIZE / 2, this.height * 
          LightEmAll.PIECE_SIZE / 2);
      return new WorldEnd(true, scene);
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // updates this LightEmAll map with edges
  public void updateMapWithEdges() {

    Vertex edgeFrom;
    Vertex edgeTo;
    String toName;
    String fromName;


    int toX;
    int toY;

    int fromX;
    int fromY;

    for (Edge e : this.mst) {

      edgeFrom = e.from;
      edgeTo = e.to;
      toName = edgeTo.name;
      fromName = edgeFrom.name;

      toX = Integer.parseInt(toName.substring(0,1));
      toY = Integer.parseInt(toName.substring(1,2));

      fromX = Integer.parseInt(fromName.substring(0,1));
      fromY = Integer.parseInt(fromName.substring(1,2));

      edgeFrom.outEdges.add(e);
      edgeTo.outEdges.add(new Edge(edgeTo, edgeFrom, e.weight));
      
      // from right to left
      if (toX < fromX) {
        board.get(fromX).get(fromY).left = true;
        board.get(toX).get(toY).right = true;
      }

      // from left to Right
      else if (toX > fromX) {
        board.get(fromX).get(fromY).right = true;
        board.get(toX).get(toY).left = true;
      }

      // from top to bottom
      else if (toY < fromY) {
        board.get(fromX).get(fromY).top = true;
        board.get(toX).get(toY).bottom = true;
      }

      // from bottom to top
      else {
        board.get(fromX).get(fromY).bottom = true;
        board.get(toX).get(toY).top = true;
      }
    }

  }
  
  public Vertex furthestNodeFrom(Vertex start) {
    //  HashMap<String, Edge> cameFromEdge;
    int longestPath = 0;
    Vertex furthestPiece = start;
    HashMap<String, Integer> visitedValues = new HashMap<String,Integer>();
    //  ???<Node> worklist; // A Queue or a Stack, depending on the algorithm
    ArrayList<Vertex> worklist = new ArrayList<Vertex>();
    //
    //  initialize the worklist to contain the starting node
    worklist.add(start);
    //  While(the worklist is not empty)
    while(worklist.size() > 0) {
      //    Node next = the next item from the worklist
      Vertex next = worklist.get(0);
      //    If (next has already been processed)
      
      Vertex source = next;
      
      if(visitedValues.containsKey(next.name)){
        worklist.remove(0);
      } else {
        for (Edge e : next.outEdges) {
          if(visitedValues.containsKey(e.from.name)) {
            source = e.from;
          } else if(visitedValues.containsKey(e.to.name)) {
            source = e.to;
          }
          
          if(e.from.name.equals(next.name)) {
            worklist.add(e.to);
          } else {
            worklist.add(e.from);
          }
        }
        if(source.name.equals(next.name)) {
          visitedValues.put(next.name, 0);
        } else {
          visitedValues.put(next.name, 1 + visitedValues.get(source.name));
          if (visitedValues.get(next.name) > longestPath) {
            longestPath = visitedValues.get(next.name);
            furthestPiece = next;
          }
        }
        
        worklist.remove(0);
      }
    }
    this.radius = longestPath / 2 + 1;
    return furthestPiece;
    
  }

  // returns an arrayList of edges from the nodes in this LightEmAll
  public ArrayList<Edge> createEdgesInTree() {

    // Reprsentative
    HashMap<String, String> representatives = new HashMap<String,String>();
    // Initialized as empty
    ArrayList<Edge> edgesInTree = new ArrayList<Edge>();
    //all edges in graph, sorted by edge weights;
    ArrayList<Edge> worklist = this.makeSortedEdgeList(this.rand);

    // initialized representatives
    for (int i = 0; i < this.width; i++) {
      for (int k = 0; k < this.height; k ++) {
        representatives.put("" + i + k, "" + i + k);
      }
    }

    while (this.hasMultipleTrees(representatives) && worklist.size() > 0) {
      //      Pick the next cheapest edge of the graph: suppose it connects X and Y.
      Edge nextCheapestEdge = worklist.get(0);
      Vertex nextCheapestEdgeX = nextCheapestEdge.from;
      Vertex nextCheapestEdgeY = nextCheapestEdge.to;
      //If find(representatives, X) equals find(representatives, Y):
      String xEnd = nextCheapestEdgeX.name;
      String yEnd = nextCheapestEdgeY.name;

      while(!representatives.get(xEnd).equals(xEnd)) {
        xEnd = representatives.get(xEnd);
      }

      while(!representatives.get(yEnd).equals(yEnd)) {
        yEnd = representatives.get(yEnd);
      }

      if (xEnd.equals(yEnd)) {

      }
      else {
        edgesInTree.add(nextCheapestEdge);

        String xRep = representatives.get(nextCheapestEdgeX.name);
        String temp = nextCheapestEdgeY.name;

        while(!representatives.get(temp).equals(temp)) {
          temp = representatives.get(temp);
        }


        representatives.replace(temp, xRep);

      }
      worklist.remove(0);
    }
    return edgesInTree;
  }

  // determines whether the given HashMap contains multiple trees
  public boolean hasMultipleTrees(HashMap<String, String> representatives) {


    for (int i = 0; i < this.width; i++) {
      for (int k = 0; k <this.height; k ++) {
        if (!(representatives.get("" + i + k).equals(representatives.get("00")))) {
          return true;
        }
      }
    }

    return false;
  }

  // Creates an EdgeList that is sorted by weight
  public ArrayList<Edge> makeSortedEdgeList(Random random) {

    ArrayList<Edge> temp = new ArrayList<Edge>();
    ArrayList<ArrayList<Vertex>> tempVertex = new ArrayList<ArrayList<Vertex>>();
    int rand = 0;
    for (int i = 0; i < this.width; i++)  {
      tempVertex.add(new ArrayList<Vertex>(this.height));
    }

    for (int i = 0; i < this.width; i++) {
      for (int k = 0; k < this.height; k ++) {
        tempVertex.get(i).add(new Vertex(""+ i + k, new ArrayList<Edge>()));
      }
    }

    // vertical edges
    for (int i = 0; i < this.width; i++) {
      for (int k = 0; k < this.height - 1; k ++) {
        rand = random.nextInt(500);

        temp.add(new Edge(tempVertex.get(i).get(k),
            tempVertex.get(i).get(k + 1),rand));
      }
    }

    // horizontal edges
    for (int i = 0; i < this.width - 1; i++) {
      for (int k = 0; k < this.height; k ++) {
        rand = random.nextInt(500);

        temp.add(new Edge(tempVertex.get(i).get(k),
            tempVertex.get(i + 1).get(k),rand));
      }
    }

    // sorts temp 
    Edge minEdge;
    ArrayList<Edge> tempSorted = new ArrayList<Edge>();

    while (temp.size() > 0) {
      minEdge = temp.get(0);
      for (int k = 0; k < temp.size(); k ++) {

        if (temp.get(k).weight < minEdge.weight) {
          minEdge = temp.get(k);
        }

      }
      tempSorted.add(minEdge);
      temp.remove(minEdge);
    }
    return tempSorted;
  }

  // Creates a Game board using fractal generation and sets this LightEmAll's board
  // to it
  public void makeFractalBoard() {
    this.makeBaseBoard();
    this.makeFractalHelper(this.board, this.width, this.height);
  }

  // Using fractal generation, augments the given map to create a LightEmAll board with given
  // height and width
  public void makeFractalHelper(ArrayList<ArrayList<GamePiece>> map, int w, int h) {

    int halfWidth = w / 2;
    int halfHeight = h / 2;
    int halfWidth2 = halfWidth;
    int halfHeight2 = halfHeight;
    boolean horizBaseCase = h == 1 && w != 1;
    boolean vertBaseCase = h != 1 && w == 1;

    if (w % 2 != 0) {
      halfWidth2 = halfWidth + 1;
    }

    if (h % 2 != 0) {
      halfHeight2 = halfHeight + 1;
    }

    if ((h == 1) || (w == 1)) {

      if (vertBaseCase) {
        map.get(0).get(0).bottom = true;
        for (int i = 1; i < h - 1; i++) {
          map.get(0).get(i).bottom = true;
          map.get(0).get(i).top = true;
        }
        map.get(0).get(h - 1).top = true;
      }
      else if (horizBaseCase) {
        map.get(0).get(0).right = true;
        for (int i = 1; i < w - 1; i++) {
          map.get(i).get(0).right = true;
          map.get(i).get(0).left = true;
        }
        map.get(w - 1).get(0).left = true;
      }

    }

    else {

      ArrayList<ArrayList<GamePiece>> topLeft = new ArrayList<ArrayList<GamePiece>>();
      ArrayList<ArrayList<GamePiece>> topRight = new ArrayList<ArrayList<GamePiece>>();
      ArrayList<ArrayList<GamePiece>> bottomRight = new ArrayList<ArrayList<GamePiece>>();
      ArrayList<ArrayList<GamePiece>> bottomLeft = new ArrayList<ArrayList<GamePiece>>();

      this.splitFractal(topLeft, map, halfWidth, halfHeight, 0, 0);
      this.splitFractal(topRight, map, halfWidth2, halfHeight, halfWidth, 0);
      this.splitFractal(bottomRight, map, halfWidth2, halfHeight2, halfWidth, halfHeight);
      this.splitFractal(bottomLeft, map, halfWidth, halfHeight2, 0, halfHeight);


      topLeft.get(0).get(halfHeight - 1).bottom = true;// Top Left Bottom Left

      topRight.get(halfWidth2 - 1).get(halfHeight - 1).bottom = true;// Top Right Bottom Right

      bottomLeft.get(0).get(0).top = true;// Bottom Left Top Left
      bottomLeft.get(halfWidth - 1).get(halfHeight2 - 1).right = true;// Bottom Left Bottom Right

      bottomRight.get(halfWidth2 - 1).get(0).top = true;// Bottom Right Top Right
      bottomRight.get(0).get(halfHeight2 - 1).left = true;// Bottom Right Bottom Left

      this.makeFractalHelper(topLeft, halfWidth, halfHeight);
      this.makeFractalHelper(topRight, halfWidth2, halfHeight);
      this.makeFractalHelper(bottomRight, halfWidth2, halfHeight2);
      this.makeFractalHelper(bottomLeft, halfWidth, halfHeight2);
    }
  }

  // Splits the fractal up based on the given width, height, and subMap
  public void splitFractal(ArrayList<ArrayList<GamePiece>> subMap,
      ArrayList<ArrayList<GamePiece>> map, int halfWidth, int halfHeight,
      int xBoost, int yBoost) {

    for (int i = 0; i < halfWidth; i++) {
      subMap.add(new ArrayList<GamePiece>(halfHeight));
      for (int k = 0; k < halfHeight; k++) {
        subMap.get(i).add(map.get(i + xBoost).get(k + yBoost));
      }
    }
  }

  // Sets this LightEmAll's board to an "empty" list of GamePiece
  public void makeBaseBoard() {

    for (int i = 0; i < this.width; i++)  {
      this.board.add(new ArrayList<GamePiece>(this.height));
    }

    // Creates full map of Gamepiece
    for (int i = 0; i < this.width; i++) {
      for (int k = 0; k < this.height; k++) {
        this.board.get(i).add(new GamePiece(i,k));
      }
    }
  }

  // Updates each GamePiece with neighbors who are directly connected to it
  public void connectNeighbors() {
    for (int i = 0; i < this.nodes.size(); i++) {
      this.nodes.get(i).createConnectedNeighbors();
    }
  }

  // Gives the the necessary GamePieces power based on their distance from the powerStation
  public void givePower() {
    GamePiece powerStationPiece = this.board.get(this.powerStationX).get(this.powerStationY);
    powerStationPiece.extendPower(this.radius);
  }

  // Adds all the nodes in the graph to this game's nodes
  public void getAllNodes() {
    for (int i = 0; i < this.width; i++) {
      for (int k = 0; k < this.height; k++) {
        this.nodes.add(this.board.get(i).get(k));
      }
    }
  }

  // determines the diameter of this Game (
  public int gameDiameter() {

    Vertex powerStationPiece = this.mst.get(0).from;

    return 0;
  }

  // Draws the game
  public WorldScene makeScene() {
    int xPos = -PIECE_SIZE / 2;
    int yPos = -PIECE_SIZE / 2;
    WorldScene scene = new WorldScene(this.width * PIECE_SIZE, this.height * PIECE_SIZE);
    for (int i = 0; i < this.width; i++) {
      xPos += PIECE_SIZE;
      for (int k = 0; k < this.height; k++) {
        if (k == 0) {
          yPos = -PIECE_SIZE / 2;
        }
        yPos += PIECE_SIZE;
        scene.placeImageXY(this.board.get(i).get(k).drawGamePiece(), xPos, yPos);
      }
    }
    return scene;
  }

  // Rotates the GamePiece that is clicked and updates power and neighbors
  public void onMouseClicked(Posn p, String buttonName) {
    if (buttonName.equals("LeftButton")) {
      int xNode = p.x / LightEmAll.PIECE_SIZE;
      int yNode = p.y / LightEmAll.PIECE_SIZE;

      this.board.get(xNode).get(yNode).rotateGamePiece();
      this.connectNeighbors();
      this.clearPower();
      this.givePower();
    }
  }

  // Moves the powerStation if an arrow key is pressed and updates power
  public void onKeyEvent(String key) {
    if (key.equals("up") || key.equals("down") || key.equals("left") || key.equals("right")) {
      this.board.get(this.powerStationX).get(powerStationY).moveGamePieceStation(key, this);
      this.clearPower();
      this.givePower();
    }
  }

  // Clears all power from the GameBoard
  public void clearPower() {
    for (int i = 0; i < this.nodes.size(); i++) {
      this.nodes.get(i).isPowered = false;
    }
  }

  // updates all GamePiece's neighbors 
  public void updateNeighbors() {
    for (int i = 0; i < this.width; i++) {
      for (int k = 0; k < this.height; k++) {
        this.board.get(i).get(k).updateGamePieceNeighbors(this);
      }
    }
  }
}

// Represents a game piece in the LightEmAll game
class GamePiece {
  // in logical coordinates, with the origin
  // at the top-left corner of the screen
  int row;
  int col;
  // whether this GamePiece is connected to the
  // adjacent left, right, top, or bottom pieces
  boolean left;
  boolean right;
  boolean top;
  boolean bottom;
  // whether the power station is on this piece
  boolean powerStation;
  boolean isPowered;

  // An ArrayList of this GamePiece's direct neighbors (don't have to be able to supply power)
  ArrayList<GamePiece> neighbors;
  // A HashMap of this GamePiece's direct neighbors with each key describing the
  // direction of that neighbor
  HashMap<String, GamePiece> hashedNeighbors;
  // A list of this GamePiece's connected neighbors that can supply power to this GamePiece
  ArrayList<GamePiece> connectedNeighbors;


  GamePiece(int col, int row, boolean top, boolean right, boolean bottom, boolean left) {
    this.row = row;
    this.col = col;
    this.left = left;
    this.right = right;
    this.top = top;
    this.bottom = bottom;
    this.powerStation = false;
    this.hashedNeighbors = new HashMap<String, GamePiece>(4);
    this.neighbors = new ArrayList<GamePiece>(4);
    this.connectedNeighbors = new ArrayList<GamePiece>();
    this.hashedNeighbors.put("Top", null);
    this.hashedNeighbors.put("Right", null);
    this.hashedNeighbors.put("Bottom", null);
    this.hashedNeighbors.put("Left", null);
  }

  GamePiece(int col, int row) {
    this.row = row;
    this.col = col;
    this.left = false;
    this.right = false;
    this.top = false;
    this.bottom = false;
    this.powerStation = false;
    this.hashedNeighbors = new HashMap<String, GamePiece>(4);
    this.neighbors = new ArrayList<GamePiece>(4);
    this.connectedNeighbors = new ArrayList<GamePiece>();
    this.hashedNeighbors.put("Top", null);
    this.hashedNeighbors.put("Right", null);
    this.hashedNeighbors.put("Bottom", null);
    this.hashedNeighbors.put("Left", null);
  }

  // Updates this GamePiece with all of its neighbors that are connected and can supply power
  public void createConnectedNeighbors() {

    this.connectedNeighbors = new ArrayList<GamePiece>();

    GamePiece topPiece = this.hashedNeighbors.get("Top");
    GamePiece bottomPiece = this.hashedNeighbors.get("Bottom");
    GamePiece rightPiece = this.hashedNeighbors.get("Right");
    GamePiece leftPiece = this.hashedNeighbors.get("Left");

    if (this.isConnected(topPiece, "Top")) {
      this.connectedNeighbors.add(topPiece);
    }
    if (this.isConnected(rightPiece, "Right")) {
      this.connectedNeighbors.add(rightPiece);
    }
    if (this.isConnected(bottomPiece, "Bottom")) {
      this.connectedNeighbors.add(bottomPiece);
    }
    if (this.isConnected(leftPiece, "Left")) {
      this.connectedNeighbors.add(leftPiece);
    }
  }

  // Extends power to the connected GamePiece based on the gameDiameter and each GamePiece's
  // distance from the powerStation
  public void extendPower(int gameDiameter) {
    this.isPowered = true;

    if (gameDiameter != 0) {
      for (int i = 0; i < this.connectedNeighbors.size(); i ++) {
        this.connectedNeighbors.get(i).extendPower(gameDiameter - 1);
      }
    }
  }

  // Moves the PowerStation in the given direction if possible
  public void moveGamePieceStation(String direction, LightEmAll game) {
    if (direction.equals("up")) {
      direction = "Top";
    }
    else if (direction.equals("down")) {
      direction = "Bottom";
    }
    else if (direction.equals("right")) {
      direction = "Right";
    }
    else if (direction.equals("left")) {
      direction = "Left";
    }

    if (this.hashedNeighbors.get(direction) == null) {
      //Do nothing
    }
    else if (this.isConnected(this.hashedNeighbors.get(direction), direction)) {
      this.hashedNeighbors.get(direction).putPowerStation(game);
      this.powerStation = false;
    }
  }

  // Is this GamePiece connected to that GamePiece in the given direction
  public boolean isConnected(GamePiece that, String direction) {
    if (this.neighbors.contains(that)) {
      if (direction.equals("Top")) {
        return this.top && that.bottom;
      }
      else if (direction.equals("Bottom")) {
        return this.bottom && that.top;
      }
      else if (direction.equals("Right")) {
        return this.right && that.left;
      }
      else if (direction.equals("Left")) {
        return this.left && that.right;
      }
      else {
        return false;
      }
    }
    else {
      return false;
    }
  }

  // Puts the PowerStation on this GamePiece and updates the coordinates of the PowerStation
  public void putPowerStation(LightEmAll game) {
    this.powerStation = true;
    game.powerStationX = this.col;
    game.powerStationY = this.row;
  }

  // draws this GamePiece
  public WorldImage drawGamePiece() {

    WorldImage gamePieceImage;
    Color powerColor = Color.LIGHT_GRAY;

    if (isPowered) {
      powerColor = Color.yellow;
    }

    // Makes square with outline (for the lines)
    gamePieceImage = new RectangleImage(LightEmAll.PIECE_SIZE, LightEmAll.PIECE_SIZE,
        OutlineMode.OUTLINE, Color.black);

    // Fills the square with color
    gamePieceImage = new OverlayImage(new RectangleImage(LightEmAll.PIECE_SIZE - 1,
        LightEmAll.PIECE_SIZE - 1, OutlineMode.SOLID, Color.DARK_GRAY), gamePieceImage);

    if (left) {
      gamePieceImage = new OverlayOffsetImage(new RectangleImage(LightEmAll.PIECE_SIZE / 2,
          LightEmAll.PIECE_SIZE / 10, OutlineMode.SOLID, powerColor), 
          LightEmAll.PIECE_SIZE / 4, 0, gamePieceImage );
    }

    if (right) {
      gamePieceImage = new OverlayOffsetImage(new RectangleImage(LightEmAll.PIECE_SIZE / 2,
          LightEmAll.PIECE_SIZE / 10, OutlineMode.SOLID, powerColor), 
          -LightEmAll.PIECE_SIZE / 4, 0, gamePieceImage);
    }

    if (top) {
      gamePieceImage = new OverlayOffsetImage(new RectangleImage(LightEmAll.PIECE_SIZE / 10,
          LightEmAll.PIECE_SIZE / 2, OutlineMode.SOLID, powerColor), 0,  
          LightEmAll.PIECE_SIZE / 4, gamePieceImage );
    }
    if (bottom) {
      gamePieceImage = new OverlayOffsetImage(new RectangleImage(LightEmAll.PIECE_SIZE / 10,
          LightEmAll.PIECE_SIZE / 2, OutlineMode.SOLID, powerColor), 0, 
          -LightEmAll.PIECE_SIZE / 4, gamePieceImage );
    }

    if (this.powerStation) {
      gamePieceImage = new OverlayImage(new StarImage(LightEmAll.PIECE_SIZE / 4, 
          OutlineMode.SOLID, Color.blue), gamePieceImage);
    }
    return gamePieceImage;
  }

  //Rotates this GamePiece clockwise by one
  public void rotateGamePiece() {

    boolean leftTemp = this.left;
    boolean topTemp = this.top;
    boolean rightTemp = this.right;
    boolean bottomTemp = this.bottom;

    this.left = bottomTemp;
    this.top = leftTemp;
    this.right = topTemp;
    this.bottom = rightTemp;

  }

  // updates this GamePiece's neighbors starting clockwise from its top left
  public void updateGamePieceNeighbors(LightEmAll game) {

    boolean isLeftColumn = this.col == 0;
    boolean isRightColumn = this.col == game.width - 1;
    boolean isTopRow = this.row == 0;
    boolean isBottomRow = this.row == game.height - 1;
    if (isLeftColumn) {
      if (!isTopRow) { // if not top left
        this.neighbors.add(game.board.get(col).get(row - 1)); // adds top
        this.hashedNeighbors.replace("Top", game.board.get(col).get(row - 1)); // puts top
      }
      this.neighbors.add(game.board.get(col + 1).get(row)); // adds right
      this.hashedNeighbors.replace("Right", game.board.get(col + 1).get(row)); //puts right
      if (!isBottomRow) { // if not bottom left
        this.neighbors.add(game.board.get(col).get(row + 1)); // adds bottom
        this.hashedNeighbors.replace("Bottom", game.board.get(col).get(row + 1)); // puts bottom
      }
    }
    else if (isRightColumn) {
      if (!isTopRow) { // if not top
        this.neighbors.add(game.board.get(col).get(row - 1)); // adds top
        this.hashedNeighbors.replace("Top", game.board.get(col).get(row - 1)); // puts top

      }
      if (!isBottomRow) { // if not bottom left
        this.neighbors.add(game.board.get(col).get(row + 1)); // adds bottom
        this.hashedNeighbors.replace("Bottom", game.board.get(col).get(row + 1)); // puts bottom
      }
      this.neighbors.add(game.board.get(col - 1).get(row)); // adds left
      this.hashedNeighbors.replace("Left", game.board.get(col - 1).get(row)); // puts left
    }
    else { //in the middle
      if (!isTopRow) { // if not at top
        this.neighbors.add(game.board.get(col).get(row - 1)); // adds top
        this.hashedNeighbors.replace("Top", game.board.get(col).get(row - 1)); // puts top
      }
      this.neighbors.add(game.board.get(col + 1).get(row)); // adds right
      this.hashedNeighbors.replace("Right", game.board.get(col + 1).get(row)); //puts right
      if (!isBottomRow) { // if not at bottom
        this.neighbors.add(game.board.get(col).get(row + 1)); // adds bottom
        this.hashedNeighbors.replace("Bottom", game.board.get(col).get(row + 1)); // puts bottom
      }
      this.neighbors.add(game.board.get(col - 1).get(row)); // adds left
      this.hashedNeighbors.replace("Left", game.board.get(col - 1).get(row)); // puts left
    }
  }
}

// Represents a Stack Data Type
class Stack<T> {
  Deque<T> items;

  Stack() {
    this.items = new Deque<T>();
  }

  //add an item to the top of the stack
  public void add(T t) {
    this.items.addAtHead(t);

  }

  //remove an item from the top of the stack
  public T remove() {
    return this.items.removeFromHead();
  }

  //how many items in this stack?
  public int size() {
    return this.items.size();
  }

  public boolean isEmpty() {
    return false;
  }
}


//Represents a Queue Data Type
class Queue<T> {
  Deque<T> items;

  Queue() {
    this.items = new Deque<T>();
  }

  //add an item to the end of the queue
  public void add(T t) {
    this.items.addAtTail(t);
  }

  //remove from the front of the queue
  public T remove() {
    return this.items.removeFromHead();
  }

  //how many items in this queue?
  public int size() {
    return this.items.size();
  }

  public boolean isEmpty() {
    return false;
  }

}

// Represents the edges in a Graph
class Edge {
  Vertex from;
  Vertex to;
  int weight;

  Edge(Vertex from, Vertex to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }
}

// Represents a Vertex in a graph
class Vertex {
  String name;
  ArrayList<Edge> outEdges; // edges from this node

  Vertex(String name, ArrayList<Edge> outEdges) {
    this.name = name;
    this.outEdges = outEdges;
  }



}

interface IPred<T> {
  // Applies the predicate to t
  boolean apply(T t);
}

//Represents a Sentinel
class Sentinel<T> extends ANode<T> {

  //initializes the next and prev fields of the Sentinel to the Sentinel itself
  Sentinel() {
    this.next = this;
    this.prev = this;
  }

  // Searches through this Sentinel for a Node matching the given Predicate
  public ANode<T> searchHeader(IPred<T> pred) {
    return this.next.searchANode(pred);
  }

  // Counts how many Nodes are in this Sentinel's next
  public int countStart() {
    return this.next.count();
  }
}


//Represents a node with a next and previous ANode
abstract class ANode<T> {

  ANode<T> next;
  ANode<T> prev;

  // Returns the count of this ANode
  public int count() {
    return 0;
  }

  // Gets the data of this ANode
  public T getData() {
    return null;
  }

  //Returns this ANode that matches the predicate
  public ANode<T> searchANode(IPred<T> pred) {
    return this;
  }

  // Adds a node with the given type to Node
  public void addTo(T type, ANode<T> augmentedNode, ANode<T> nodeOne, ANode<T> nodeTwo) {
    augmentedNode = new Node<T>(type, nodeOne, nodeTwo);
  }

  // Updates this ANode's next or previous depending on whether it is necessary
  public void update(ANode<T> newNode, Boolean isChangingNext) {
    if (isChangingNext) {
      this.next = newNode;
    }
    else {
      this.prev = newNode;
    }
  }


  // Removes the given ANode from this Anode
  public void remove(ANode<T> nodeToRemove) {

    if (this == nodeToRemove) {
      throw new RuntimeException("Can't remove from empty list");
    }

    else {
      nodeToRemove.next.update(nodeToRemove.prev, false);
      nodeToRemove.prev.update(nodeToRemove.next, true);
    }
  }
}

//Represents a node containing data
class Node<T> extends ANode<T> {

  T data;

  Node(T t) {

    this.data = t;
    this.next = null;
    this.prev = null;

  }

  Node(T t, ANode<T> nextNode, ANode<T> prevNode) {

    this.data = t;
    this.next = nextNode;
    this.prev = prevNode;

    if (nextNode == null || prevNode == null) {
      throw new IllegalArgumentException("One of the given nodes is null");
    }

    nextNode.prev = this;
    prevNode.next = this;
  }

  // Counts how many Nodes are in this Node and the connected Nodes
  public int count() {
    return 1 + this.next.count();
  }

  // Gets the data from this Node
  public T getData() {
    return this.data;
  }

  // checks the rest of the Nodes
  public ANode<T> searchANode(IPred<T> pred) {

    if (pred.apply(this.data)) {
      return this;
    }

    else {
      return this.next.searchANode(pred);
    }
  }
}


//Represents a Deque Data Type
class Deque<T> {

  Sentinel<T> header;

  Deque() {
    this.header = new Sentinel<T>();
  }

  Deque(Sentinel<T> header) {
    this.header = header;
  }

  // Returns the size of this Deque
  int size() {
    return this.header.countStart(); 
  }

  // Adds a new Node with the given type to the front of this Deque
  void addAtHead(T type) {
    this.header.addTo(type,this.header.next, this.header.next, this.header);
  }

  //Adds a new Node with the given type to the tail of this Deque
  void addAtTail(T type) {
    this.header.addTo(type, this.header.prev, this.header,this.header.prev);
  }

  // Removes the head of this Deque
  T removeFromHead() {
    T temp = this.header.next.getData();
    this.header.remove(this.header.next);
    return temp;
  }

  // Removes the tail of this DequeS
  T removeFromTail() {
    T temp = this.header.prev.getData();
    this.header.remove(this.header.prev);
    return temp;
  }

  // Removes the given Node from this Deque
  void removeNode(ANode<T> t) {
    this.header.remove(t);
  }
}


class LightEmAllExamples {

  LightEmAll game1;
  LightEmAll game2;
  LightEmAll game3;

  GamePiece n00;
  GamePiece n01;
  GamePiece n02;
  GamePiece n03;

  GamePiece n10;
  GamePiece n11;
  GamePiece n12;
  GamePiece n13;

  GamePiece n20;
  GamePiece n21;
  GamePiece n22;
  GamePiece n23;

  GamePiece n30;
  GamePiece n31;
  GamePiece n32;
  GamePiece n33;

  ArrayList<ArrayList<GamePiece>> board1;
  ArrayList<GamePiece> c0;
  ArrayList<GamePiece> c1;
  ArrayList<GamePiece> c2;
  ArrayList<GamePiece> c3;

  Vertex v00;
  Vertex v01;
  Vertex v02;
  Vertex v03;

  Vertex v10;
  Vertex v11;
  Vertex v12;
  Vertex v13;

  Vertex v20;
  Vertex v21;
  Vertex v22;
  Vertex v23;

  Vertex v30;
  Vertex v31;
  Vertex v32;
  Vertex v33;

  ArrayList<ArrayList<Vertex>> listOfVertex1;

  HashMap<String, String> representatives;

  void initData() {

    n00 = new GamePiece(0,0, false, false, true, false); 
    n01 = new GamePiece(0,1, true, true, true, false); 
    n02 = new GamePiece(0,2, true, false, true, false); 
    n03 = new GamePiece(0,3, true, false, false, false); 

    // Column 1
    n10 = new GamePiece(1,0, false, false, true, false); 
    n11 = new GamePiece(1,1, true, true, true, true); 
    n12 = new GamePiece(1,2, true, false, true, false); 
    n13 = new GamePiece(1,3, true, false, false, false);

    // Column 2
    n20 = new GamePiece(2,0, false, false, true, false); 
    n21 = new GamePiece(2,1, true, true, true, true); 
    n22 = new GamePiece(2,2, true, false, true, false); 
    n23 = new GamePiece(2,3, true, false, false, false); 

    // Column 3
    n30 = new GamePiece(3,0, false, false, true, false); 
    n31 = new GamePiece(3,1, true, false, true, true); 
    n32 = new GamePiece(3,2, true, false, true, false); 
    n33 = new GamePiece(3,3, true, false, false, false);

    c0 = new ArrayList<GamePiece>(4);
    c1 = new ArrayList<GamePiece>(4);
    c2 = new ArrayList<GamePiece>(4);
    c3 = new ArrayList<GamePiece>(4);

    board1 = new ArrayList<ArrayList<GamePiece>>(4);
    board1.add(c0);
    board1.add(c1);
    board1.add(c2);
    board1.add(c3);

    c0.add(n00);
    c0.add(n01);
    c0.add(n02);
    c0.add(n03);

    c1.add(n10);
    c1.add(n11);
    c1.add(n12);
    c1.add(n13);

    c2.add(n20);
    c2.add(n21);
    c2.add(n22);
    c2.add(n23);

    c3.add(n30);
    c3.add(n31);
    c3.add(n32);
    c3.add(n33);

    n21.powerStation = true;

    game1 = new LightEmAll(4, 4,0);
    game1.board = board1;

    game2 = new LightEmAll(2,2,0);

    game3 = new LightEmAll(2,2,0);

    v00 = new Vertex("00", new ArrayList<Edge>());
    v01= new Vertex("01", new ArrayList<Edge>());
    v02= new Vertex("02", new ArrayList<Edge>());
    v03= new Vertex("03", new ArrayList<Edge>());

    v10= new Vertex("10", new ArrayList<Edge>());
    v11= new Vertex("11", new ArrayList<Edge>());
    v12= new Vertex("12", new ArrayList<Edge>());
    v13= new Vertex("13", new ArrayList<Edge>());

    v20= new Vertex("20", new ArrayList<Edge>());
    v21= new Vertex("21", new ArrayList<Edge>());
    v22= new Vertex("22", new ArrayList<Edge>());
    v23= new Vertex("23", new ArrayList<Edge>());

    v30= new Vertex("30", new ArrayList<Edge>());
    v31= new Vertex("31", new ArrayList<Edge>());
    v32= new Vertex("32", new ArrayList<Edge>());
    v33= new Vertex("33", new ArrayList<Edge>());

    listOfVertex1 = new ArrayList<ArrayList<Vertex>>(4);
    listOfVertex1.add(new ArrayList<Vertex>(4));
    listOfVertex1.add(new ArrayList<Vertex>(4));
    listOfVertex1.add(new ArrayList<Vertex>(4));
    listOfVertex1.add(new ArrayList<Vertex>(4));

    listOfVertex1.get(0).add(v00);
    listOfVertex1.get(0).add(v01);
    listOfVertex1.get(0).add(v02);
    listOfVertex1.get(0).add(v03);

    listOfVertex1.get(1).add(v10);
    listOfVertex1.get(1).add(v11);
    listOfVertex1.get(1).add(v12);
    listOfVertex1.get(1).add(v13);

    listOfVertex1.get(2).add(v20);
    listOfVertex1.get(2).add(v21);
    listOfVertex1.get(2).add(v22);
    listOfVertex1.get(2).add(v23);

    listOfVertex1.get(3).add(v30);
    listOfVertex1.get(3).add(v31);
    listOfVertex1.get(3).add(v32);
    listOfVertex1.get(3).add(v33);

    representatives = new HashMap<String, String>();
    representatives.put("00", "00");
    representatives.put("01", "01");
    representatives.put("02", "02");

    representatives.put("10", "10");
    representatives.put("11", "11");
    representatives.put("12", "12");

    representatives.put("20", "20");
    representatives.put("21", "21");
    representatives.put("22", "22");

  }

  void initNeighbors() {

    // column 1
    n00.neighbors.add(n10);
    n00.neighbors.add(n01);
    n00.hashedNeighbors.replace("Right", n10);
    n00.hashedNeighbors.replace("Bottom", n01);

    n01.neighbors.add(n00);
    n01.neighbors.add(n11);
    n01.neighbors.add(n02);
    n01.hashedNeighbors.replace("Top", n00);
    n01.hashedNeighbors.replace("Right", n11);
    n01.hashedNeighbors.replace("Bottom", n02);

    n02.neighbors.add(n01);
    n02.neighbors.add(n12);
    n02.neighbors.add(n03);
    n02.hashedNeighbors.replace("Top", n01);
    n02.hashedNeighbors.replace("Right", n12);
    n02.hashedNeighbors.replace("Bottom", n03);

    n03.neighbors.add(n02);
    n03.neighbors.add(n13);
    n03.hashedNeighbors.replace("Top", n02);
    n03.hashedNeighbors.replace("Right", n13);

    // column 2
    n10.neighbors.add(n20);
    n10.neighbors.add(n11);
    n10.neighbors.add(n00);
    n10.hashedNeighbors.replace("Right", n20);
    n10.hashedNeighbors.replace("Bottom", n11);
    n10.hashedNeighbors.replace("Left", n00);

    n11.neighbors.add(n10);
    n11.neighbors.add(n21);
    n11.neighbors.add(n12);
    n11.neighbors.add(n01);
    n11.hashedNeighbors.replace("Top", n10);
    n11.hashedNeighbors.replace("Right", n21);
    n11.hashedNeighbors.replace("Bottom", n12);
    n11.hashedNeighbors.replace("Left", n01);

    n12.neighbors.add(n11);
    n12.neighbors.add(n22);
    n12.neighbors.add(n13);
    n12.neighbors.add(n02);
    n12.hashedNeighbors.replace("Top", n11);
    n12.hashedNeighbors.replace("Right", n22);
    n12.hashedNeighbors.replace("Bottom", n13);
    n12.hashedNeighbors.replace("Left", n02);

    n13.neighbors.add(n12);
    n13.neighbors.add(n23);
    n13.neighbors.add(n03);
    n13.hashedNeighbors.replace("Top", n12);
    n13.hashedNeighbors.replace("Right", n23);
    n13.hashedNeighbors.replace("Left", n03);

    //column 3
    n20.neighbors.add(n30);
    n20.neighbors.add(n21);
    n20.neighbors.add(n10);
    n20.hashedNeighbors.replace("Right", n30);
    n20.hashedNeighbors.replace("Bottom", n21);
    n20.hashedNeighbors.replace("Left", n10);

    n21.neighbors.add(n20);
    n21.neighbors.add(n31);
    n21.neighbors.add(n22);
    n21.neighbors.add(n11);
    n21.hashedNeighbors.replace("Top", n20);
    n21.hashedNeighbors.replace("Right", n31);
    n21.hashedNeighbors.replace("Bottom", n22);
    n21.hashedNeighbors.replace("Left", n11);

    n22.neighbors.add(n21);
    n22.neighbors.add(n32);
    n22.neighbors.add(n23);
    n22.neighbors.add(n12);
    n22.hashedNeighbors.replace("Top", n21);
    n22.hashedNeighbors.replace("Right", n32);
    n22.hashedNeighbors.replace("Bottom", n23);
    n22.hashedNeighbors.replace("Left", n12);

    n23.neighbors.add(n22);
    n23.neighbors.add(n33);
    n23.neighbors.add(n13);
    n23.hashedNeighbors.replace("Top", n22);
    n23.hashedNeighbors.replace("Right", n33);
    n23.hashedNeighbors.replace("Left", n13);

    // column 4
    n30.neighbors.add(n31);
    n30.neighbors.add(n20);
    n30.hashedNeighbors.replace("Bottom", n31);
    n30.hashedNeighbors.replace("Left", n20);

    n31.neighbors.add(n30);
    n31.neighbors.add(n32);
    n31.neighbors.add(n21);
    n31.hashedNeighbors.replace("Top", n30);
    n31.hashedNeighbors.replace("Bottom", n32);
    n31.hashedNeighbors.replace("Left", n21);

    n32.neighbors.add(n31);
    n32.neighbors.add(n33);
    n32.neighbors.add(n22);
    n32.hashedNeighbors.replace("Top", n31);
    n32.hashedNeighbors.replace("Bottom", n33);
    n32.hashedNeighbors.replace("Left", n22);

    n33.neighbors.add(n32);
    n33.neighbors.add(n23);
    n33.hashedNeighbors.replace("Top", n32);
    n33.hashedNeighbors.replace("Left", n23);
  }

  void initConnectedNeighbors() {
    n00.connectedNeighbors.add(n01);

    n01.connectedNeighbors.add(n00);
    n01.connectedNeighbors.add(n11);
    n01.connectedNeighbors.add(n02);

    n02.connectedNeighbors.add(n01);
    n02.connectedNeighbors.add(n03);

    n03.connectedNeighbors.add(n02);

    n10.connectedNeighbors.add(n11);

    n11.connectedNeighbors.add(n10);
    n11.connectedNeighbors.add(n21);
    n11.connectedNeighbors.add(n12);
    n11.connectedNeighbors.add(n01);

    n12.connectedNeighbors.add(n11);
    n12.connectedNeighbors.add(n13);

    n13.connectedNeighbors.add(n12);

    n20.connectedNeighbors.add(n21);

    n21.connectedNeighbors.add(n20);
    n21.connectedNeighbors.add(n31);
    n21.connectedNeighbors.add(n22);
    n21.connectedNeighbors.add(n11);

    n22.connectedNeighbors.add(n21);
    n22.connectedNeighbors.add(n23);

    n23.connectedNeighbors.add(n22);

    n30.connectedNeighbors.add(n31);

    n31.connectedNeighbors.add(n30);
    n31.connectedNeighbors.add(n32);
    n31.connectedNeighbors.add(n21);

    n32.connectedNeighbors.add(n31);
    n32.connectedNeighbors.add(n33);

    n33.connectedNeighbors.add(n32);
  }
  
  // test scrambleBoard
  void testScrambleBoard(Tester t) {
    Random rand = new Random(3);
    LightEmAll temp = new LightEmAll(3,3,rand);
    temp.scrambleBoard(rand);
    ArrayList<ArrayList<GamePiece>> tempBoard = temp.board;
    
    tempBoard.get(0).get(0).top = true;
    tempBoard.get(0).get(0).bottom = true;
    tempBoard.get(0).get(0).left = true;
    tempBoard.get(0).get(0).right = false;

    tempBoard.get(0).get(1).top = true;
    tempBoard.get(0).get(1).right = false;
    tempBoard.get(0).get(1).bottom = false;
    tempBoard.get(0).get(1).left = false;
    

    tempBoard.get(0).get(2).top = true;
    tempBoard.get(0).get(2).right = false;
    tempBoard.get(0).get(2).bottom = false;
    tempBoard.get(0).get(2).left = true;
    
    tempBoard.get(1).get(0).top = false;
    tempBoard.get(1).get(0).right = false;
    tempBoard.get(1).get(0).bottom = false;
    tempBoard.get(1).get(0).left = false;
    
    tempBoard.get(1).get(1).top = false;
    tempBoard.get(1).get(1).right = false;
    tempBoard.get(1).get(1).bottom = false;
    tempBoard.get(1).get(1).left = false;
    
    tempBoard.get(1).get(2).top = true;
    tempBoard.get(1).get(2).right = false;
    tempBoard.get(1).get(2).bottom = false;
    tempBoard.get(1).get(2).left = false;

    tempBoard.get(2).get(0).top = true;
    tempBoard.get(2).get(0).right = false;
    tempBoard.get(2).get(0).bottom = false;
    tempBoard.get(2).get(0).left = false;
    
    tempBoard.get(2).get(1).left = true;
    tempBoard.get(2).get(1).top = false;
    tempBoard.get(2).get(1).right = false;
    tempBoard.get(2).get(1).bottom = false;
    
    tempBoard.get(2).get(2).top = true;
    tempBoard.get(2).get(2).right = true;
    tempBoard.get(2).get(2).left = true;
    tempBoard.get(2).get(2).bottom = false;
    t.checkExpect(temp.board, tempBoard);
  }

  // test worldEnds
  void testWorldends(Tester t) {
    LightEmAll game = new LightEmAll(4, 4, true);
    for (GamePiece p : game.nodes) {
      p.isPowered = true;
    }

    WorldScene tempScene = game.makeScene();
    tempScene.placeImageXY(new TextImage("You Win!", game.width * LightEmAll.PIECE_SIZE / 
        13, Color.red), 
        game.width * LightEmAll.PIECE_SIZE / 2, game.height * 
        LightEmAll.PIECE_SIZE / 2);

    t.checkExpect(game.worldEnds(), new WorldEnd(true, tempScene));


    game = new LightEmAll(4, 4, true);

    tempScene = game.makeScene();

    t.checkExpect(game.worldEnds(), new WorldEnd(false, tempScene));
  }

  // test makeSortedEdgeList
  void testMakeSortedEdgeList(Tester t) {
    this.initData();
    this.initNeighbors();
    this.initConnectedNeighbors();
    Random rand = new Random(3);

    LightEmAll game = new LightEmAll(false, 3,3, rand);
    game.makeBaseBoard();
    game.updateMapWithEdges();

    ArrayList<Edge> test = game.makeSortedEdgeList(rand);
    ArrayList<Edge> temp1 = new ArrayList<Edge>();

    temp1 = new ArrayList<Edge>();
    temp1.add(new Edge(v20, v21, 76));
    temp1.add(new Edge(v10, v20, 92));
    temp1.add(new Edge(v01, v11, 99));
    temp1.add(new Edge(v01, v02, 114));
    temp1.add(new Edge(v21, v22, 137));
    temp1.add(new Edge(v00, v01, 277));
    temp1.add(new Edge(v12, v22, 295));
    temp1.add(new Edge(v11, v12, 306));
    temp1.add(new Edge(v11, v21, 385));
    temp1.add(new Edge(v00, v10, 386));
    temp1.add(new Edge(v02, v12, 411));
    temp1.add(new Edge(v10, v11, 481));

    t.checkExpect(test, temp1);
  }

  // test createEdgesInTree
  void testCreateEdgesInTree(Tester t) {
    this.initData();
    Random rand = new Random(3);
    LightEmAll game = new LightEmAll(false, 3, 3, rand);
    ArrayList<Edge> test = game.createEdgesInTree();
    ArrayList<Edge> temp1 = new ArrayList<Edge>();

    temp1.add(new Edge(v20, v21, 76));
    temp1.add(new Edge(v10, v20, 92));
    temp1.add(new Edge(v01, v11, 99));
    temp1.add(new Edge(v01, v02, 114));
    temp1.add(new Edge(v21, v22, 137));
    temp1.add(new Edge(v00, v01, 277));
    temp1.add(new Edge(v12, v22, 295));
    temp1.add(new Edge(v11, v12, 306));

    t.checkExpect(temp1, test);

  }

  // test hasMultipleTrees
  void testHasMultipleTrees(Tester t) {
    this.initData();

    LightEmAll game = new LightEmAll(3,3, new Random(3));
    t.checkExpect(game.hasMultipleTrees(representatives), true);

    this.initData();
    game = new LightEmAll(3,3, new Random(3));

    representatives.replace("00", "02");
    t.checkExpect(game.hasMultipleTrees(representatives), true);

    this.initData();
    game = new LightEmAll(3,3, new Random(3));

    representatives.replace("00", "00");
    representatives.replace("01", "00");
    representatives.replace("02", "00");
    representatives.replace("10", "00");
    representatives.replace("11", "00");
    representatives.replace("12", "00");
    representatives.replace("20", "00");
    representatives.replace("21", "00");
    representatives.replace("22", "00");
    t.checkExpect(game.hasMultipleTrees(representatives), false);
  }

  // test testUpdateMapWithEdges
  void testUpDateMapWithEdge(Tester t) {
    this.initData();
    Random rand = new Random(3);
    LightEmAll game = new LightEmAll(3, 3, rand);
    ArrayList<Edge> test = game.createEdgesInTree();
    ArrayList<Edge> temp1 = new ArrayList<Edge>();

    v00.outEdges.add(new Edge(v00, v01, 277));

    v01.outEdges.add(new Edge(v01, v02, 114));

    v10.outEdges.add(new Edge(v10, v11, 481));

    v11.outEdges.add(new Edge(v11, v12, 306));

    v20.outEdges.add(new Edge(v20, v21, 76));

    v21.outEdges.add(new Edge(v21, v22, 137));

    //horizontal

    v00.outEdges.add(new Edge(v00, v10, 386));

    v10.outEdges.add(new Edge(v10, v20, 92));

    v01.outEdges.add(new Edge(v01, v11, 99));

    v11.outEdges.add(new Edge(v11, v21, 385));

    v02.outEdges.add(new Edge(v02, v12, 411));

    v12.outEdges.add(new Edge(v12, v22, 295));

    temp1.add(new Edge(v20, v21, 76));
    temp1.add(new Edge(v10, v20, 92));
    temp1.add(new Edge(v01, v11, 99));
    temp1.add(new Edge(v01, v02, 114));
    temp1.add(new Edge(v21, v22, 137));
    temp1.add(new Edge(v00, v01, 277));
    temp1.add(new Edge(v12, v22, 295));
    temp1.add(new Edge(v11, v12, 306));
    game.mst = temp1;
    ArrayList<ArrayList<GamePiece>> tempBoard = game.board;

    ArrayList<String> temp2 = new ArrayList<String>();
    for (GamePiece p : game.nodes) {
      temp2.add(p.top + ", " + p.right + ", " + p.bottom + ", " + p.left);
    }

    tempBoard.get(0).get(0).right = true;
    tempBoard.get(0).get(0).left = true;

    tempBoard.get(0).get(1).right = true;
    tempBoard.get(0).get(1).bottom = true;

    tempBoard.get(0).get(2).top = true;

    tempBoard.get(1).get(0).bottom = true;
    tempBoard.get(1).get(0).left = true;

    tempBoard.get(1).get(1).top = true;
    tempBoard.get(1).get(1).right = true;
    tempBoard.get(1).get(1).bottom = true;
    tempBoard.get(1).get(1).left = true;

    tempBoard.get(1).get(2).top = true;

    tempBoard.get(2).get(0).bottom = true;

    tempBoard.get(2).get(1).top = true;
    tempBoard.get(2).get(1).bottom = true;
    tempBoard.get(2).get(1).left = true;

    tempBoard.get(2).get(2).top = true;

    t.checkExpect(game.board, tempBoard);
  }

  // test makeBaseFractalBoard
  void testMakeBaseFractalBoard(Tester t) {
    ArrayList<ArrayList<GamePiece>> tempBoard = new ArrayList<ArrayList<GamePiece>>();

    LightEmAll gameTest = new LightEmAll(4,4,0);
    ArrayList<GamePiece> tempC0 = new ArrayList<GamePiece>();
    ArrayList<GamePiece> tempC1 = new ArrayList<GamePiece>();
    ArrayList<GamePiece> tempC2 = new ArrayList<GamePiece>();
    ArrayList<GamePiece> tempC3 = new ArrayList<GamePiece>();

    tempBoard.add(tempC0);
    tempBoard.add(tempC1);
    tempBoard.add(tempC2);
    tempBoard.add(tempC3);

    tempC0.add(new GamePiece(0,0));
    tempC0.add(new GamePiece(0,1));
    tempC0.add(new GamePiece(0,2));
    tempC0.add(new GamePiece(0,3));

    tempC1.add(new GamePiece(1,0));
    tempC1.add(new GamePiece(1,1));
    tempC1.add(new GamePiece(1,2));
    tempC1.add(new GamePiece(1,3));

    tempC2.add(new GamePiece(2,0));
    tempC2.add(new GamePiece(2,1));
    tempC2.add(new GamePiece(2,2));
    tempC2.add(new GamePiece(2,3));

    tempC3.add(new GamePiece(3,0));
    tempC3.add(new GamePiece(3,1));
    tempC3.add(new GamePiece(3,2));
    tempC3.add(new GamePiece(3,3));

    gameTest.makeBaseBoard();
    t.checkExpect(gameTest.board, tempBoard);

  }

  // test connectNeighbors
  void testConnectNeighbors(Tester t) {
    this.initData();
    this.initNeighbors();
    this.initConnectedNeighbors();
    LightEmAll gameTemp = new LightEmAll(4,4,0);
    gameTemp.nodes = new ArrayList<GamePiece>();
    gameTemp.makeSetBoard();
    gameTemp.getAllNodes();
    gameTemp.updateNeighbors();
    gameTemp.connectNeighbors();

    t.checkExpect(gameTemp.board.get(0).get(0), n00);
    t.checkExpect(gameTemp.board.get(2).get(0), n20);
    t.checkExpect(gameTemp.board.get(3).get(2), n32);
    t.checkExpect(gameTemp.board.get(0).get(2), n02);
    t.checkExpect(gameTemp.board.get(1).get(1), n11);
  }

  // test givePower
  void testGivePower(Tester t) {
    this.initData();
    this.initNeighbors();
    this.initConnectedNeighbors();
    game1.nodes = new ArrayList<GamePiece>();
    game1.powerStationX = 0;
    game1.powerStationY = 0;
    game1.radius = 1;
    game1.makeSetBoard();
    game1.getAllNodes();
    game1.updateNeighbors();
    game1.connectNeighbors();

    game1.givePower();
    t.checkExpect(game1.board.get(0).get(0).isPowered, true);
    t.checkExpect(game1.board.get(0).get(1).isPowered, true);
    t.checkExpect(game1.board.get(0).get(2).isPowered, false);

    this.initData();
    this.initNeighbors();
    this.initConnectedNeighbors();
    game1.nodes = new ArrayList<GamePiece>();
    game1.powerStationX = 0;
    game1.powerStationY = 0;
    game1.radius = 2;
    game1.makeSetBoard();
    game1.getAllNodes();
    game1.updateNeighbors();
    game1.connectNeighbors();

    game1.givePower();
    t.checkExpect(game1.board.get(0).get(0).isPowered, true);
    t.checkExpect(game1.board.get(0).get(1).isPowered, true);
    t.checkExpect(game1.board.get(0).get(2).isPowered, true);

    this.initData();
    this.initNeighbors();
    this.initConnectedNeighbors();
    game1.nodes = new ArrayList<GamePiece>();
    game1.powerStationX = 2;
    game1.powerStationY = 1;
    game1.radius = 1;
    game1.makeSetBoard();
    game1.getAllNodes();
    game1.updateNeighbors();
    game1.connectNeighbors();

    game1.givePower();
    t.checkExpect(game1.board.get(2).get(1).isPowered, true);
    t.checkExpect(game1.board.get(2).get(0).isPowered, true);
    t.checkExpect(game1.board.get(3).get(1).isPowered, true);
    t.checkExpect(game1.board.get(1).get(1).isPowered, true);

    this.initData();
    this.initNeighbors();
    this.initConnectedNeighbors();
    game1.nodes = new ArrayList<GamePiece>();
    game1.powerStationX = 2;
    game1.powerStationY = 2;
    game1.radius = 2;
    game1.makeSetBoard();
    game1.getAllNodes();
    game1.updateNeighbors();
    game1.connectNeighbors();

    game1.givePower();
    t.checkExpect(game1.board.get(2).get(1).isPowered, true);
    t.checkExpect(game1.board.get(2).get(0).isPowered, true);
    t.checkExpect(game1.board.get(3).get(1).isPowered, true);
    t.checkExpect(game1.board.get(2).get(2).isPowered, true);
    t.checkExpect(game1.board.get(2).get(3).isPowered, true);
    t.checkExpect(game1.board.get(1).get(1).isPowered, true);
  }

  // test getAllNodes
  void testGetAllNodes(Tester t) {

    this.initData();
    this.initNeighbors();
    LightEmAll gameTemp = new LightEmAll(4,4,0);
    gameTemp.nodes = new ArrayList<GamePiece>();
    gameTemp.makeSetBoard();
    gameTemp.getAllNodes();

    ArrayList<GamePiece> allNodes = new ArrayList<GamePiece>();


    allNodes.add(gameTemp.board.get(0).get(0));
    allNodes.add(gameTemp.board.get(0).get(1));
    allNodes.add(gameTemp.board.get(0).get(2));
    allNodes.add(gameTemp.board.get(0).get(3));

    allNodes.add(gameTemp.board.get(1).get(0));
    allNodes.add(gameTemp.board.get(1).get(1));
    allNodes.add(gameTemp.board.get(1).get(2));
    allNodes.add(gameTemp.board.get(1).get(3));

    allNodes.add(gameTemp.board.get(2).get(0));
    allNodes.add(gameTemp.board.get(2).get(1));
    allNodes.add(gameTemp.board.get(2).get(2));
    allNodes.add(gameTemp.board.get(2).get(3));

    allNodes.add(gameTemp.board.get(3).get(0));
    allNodes.add(gameTemp.board.get(3).get(1));
    allNodes.add(gameTemp.board.get(3).get(2));
    allNodes.add(gameTemp.board.get(3).get(3));


    t.checkExpect(gameTemp.nodes, allNodes);

  }

  // test clearPower
  void testClearPower(Tester t) {
    this.initData();
    this.initNeighbors();

    this.game1.makeSetBoard();
    LightEmAll w = new LightEmAll();
    w.clearPower();

    t.checkExpect(w.board.get(0).get(0).isPowered, false);
    t.checkExpect(w.board.get(2).get(1).isPowered, false);
    t.checkExpect(w.board.get(3).get(2).isPowered, false);
    t.checkExpect(w.board.get(0).get(3).isPowered, false);
    t.checkExpect(w.board.get(1).get(0).isPowered, false);
    t.checkExpect(w.board.get(1).get(1).isPowered, false);
    t.checkExpect(w.board.get(0).get(2).isPowered, false);
  }

  // test createConnectedNeighbors
  void testCreateConnectedNeighbors(Tester t) {
    this.initData();
    this.initNeighbors();

    this.n00.createConnectedNeighbors();
    ArrayList<GamePiece> cnTemp = new ArrayList<GamePiece>();
    cnTemp.add(n01);
    t.checkExpect(n00.connectedNeighbors, cnTemp);


    this.initData();
    this.initNeighbors();

    this.n11.createConnectedNeighbors();
    cnTemp = new ArrayList<GamePiece>();
    cnTemp.add(n10);
    cnTemp.add(n21);
    cnTemp.add(n12);
    cnTemp.add(n01);
    t.checkExpect(n11.connectedNeighbors, cnTemp);

    this.initData();
    this.initNeighbors();

    this.n22.createConnectedNeighbors();
    cnTemp = new ArrayList<GamePiece>();
    cnTemp.add(n21);
    cnTemp.add(n23);
    t.checkExpect(n22.connectedNeighbors, cnTemp);
  }

  // test extendPower
  void testExtendPower(Tester t) {

    this.initData();
    this.initNeighbors();
    this.initConnectedNeighbors();

    n00.extendPower(1);
    t.checkExpect(n00.isPowered, true);
    t.checkExpect(n01.isPowered, true);

    this.initData();
    this.initNeighbors();
    this.initConnectedNeighbors();

    n00.extendPower(2);
    t.checkExpect(n00.isPowered, true);
    t.checkExpect(n01.isPowered, true);
    t.checkExpect(n02.isPowered, true);

    this.initData();
    this.initNeighbors();
    this.initConnectedNeighbors();

    n21.extendPower(1);
    t.checkExpect(n21.isPowered, true);
    t.checkExpect(n20.isPowered, true);
    t.checkExpect(n31.isPowered, true);
    t.checkExpect(n22.isPowered, true);
    t.checkExpect(n11.isPowered, true);

    this.initData();
    this.initNeighbors();
    this.initConnectedNeighbors();

    n22.extendPower(2);
    t.checkExpect(n21.isPowered, true);
    t.checkExpect(n20.isPowered, true);
    t.checkExpect(n31.isPowered, true);
    t.checkExpect(n22.isPowered, true);
    t.checkExpect(n23.isPowered, true);
    t.checkExpect(n11.isPowered, true);
  }

  // test putPowerStation
  void testPutPowerStation(Tester t) {
    this.initData();
    n00.putPowerStation(this.game1);
    t.checkExpect(n00.powerStation, true);
    t.checkExpect(game1.powerStationX, 0);
    t.checkExpect(game1.powerStationY, 0);

    this.initData();
    n22.putPowerStation(this.game1);
    t.checkExpect(n22.powerStation, true);
    t.checkExpect(game1.powerStationX, 2);
    t.checkExpect(game1.powerStationY, 2);
  }

  //test isConnected
  void testIsConnected(Tester t) {
    this.initData();
    this.initNeighbors();

    t.checkExpect(n00.isConnected(n01, "Bottom"), true);
    t.checkExpect(n02.isConnected(n01, "Top"), true);
    t.checkExpect(n02.isConnected(n12, "Right"), false);
    t.checkExpect(n21.isConnected(n11, "Left"), true);
  }

  // test makeSetBoard
  void testMakeSetBoard(Tester t) {
    this.initData();
    LightEmAll temp = new LightEmAll(4,4,0);
    temp.makeSetBoard();

    t.checkExpect(temp.board, game1.board);
  }

  // test MoveGamePieceStation
  void testMoveGamePieceStation(Tester t) {
    this.initData();
    this.initNeighbors();
    n00.powerStation = true;

    n00.moveGamePieceStation("down", this.game1);
    t.checkExpect(n00.powerStation, false);
    t.checkExpect(this.game1.board.get(0).get(1).powerStation, true);

    this.initData();
    this.initNeighbors();

    n00.powerStation = true;
    n00.moveGamePieceStation("up", this.game1);
    t.checkExpect(n00.powerStation, true);

    this.initData();
    this.initNeighbors();
    n21.powerStation = true;
    n21.moveGamePieceStation("left", this.game1);
    t.checkExpect(n21.powerStation, false);
    t.checkExpect(this.game1.board.get(1).get(1).powerStation, true);

    this.initData();
    this.initNeighbors();
    n00.powerStation = true;
    n00.moveGamePieceStation("right", this.game1);
    t.checkExpect(n00.powerStation, true);
    t.checkExpect(this.game1.board.get(1).get(0).powerStation, false);
  }

  //test onKeyEvent
  void testOnKeyEvent(Tester t) {
    this.initData();
    this.initNeighbors();
    LightEmAll gameTemp = new LightEmAll(4,4,true);


    gameTemp.onKeyEvent("down");
    t.checkExpect(gameTemp.board.get(0).get(1).powerStation, true);

    this.initData();
    this.initNeighbors();
    gameTemp = new LightEmAll(4,4,true);
    gameTemp.onKeyEvent("down");
    t.checkExpect(gameTemp.board.get(0).get(1).powerStation, true);
    gameTemp.onKeyEvent("down");
    t.checkExpect(gameTemp.board.get(0).get(2).powerStation, true);
    gameTemp.onKeyEvent("left");
    t.checkExpect(gameTemp.board.get(1).get(3).powerStation, false);
    gameTemp.onKeyEvent("right");
    t.checkExpect(gameTemp.board.get(3).get(3).powerStation, false);
  }

  // test makeScene
  void testMakeScene(Tester t) {
    this.initData();
    WorldScene temp =  new WorldScene(4 * LightEmAll.PIECE_SIZE, 4 * LightEmAll.PIECE_SIZE);
    temp.placeImageXY(this.game1.board.get(0).get(0).drawGamePiece(),
        LightEmAll.PIECE_SIZE / 2, LightEmAll.PIECE_SIZE / 2);
    temp.placeImageXY(this.game1.board.get(0).get(1).drawGamePiece(),
        LightEmAll.PIECE_SIZE / 2, 150);
    temp.placeImageXY(this.game1.board.get(0).get(2).drawGamePiece(), 
        50, 250);
    temp.placeImageXY(this.game1.board.get(0).get(3).drawGamePiece(), 
        50, 350);
    temp.placeImageXY(this.game1.board.get(1).get(0).drawGamePiece(), 
        150, 50);
    temp.placeImageXY(this.game1.board.get(1).get(1).drawGamePiece(),
        150, 150);
    temp.placeImageXY(this.game1.board.get(1).get(2).drawGamePiece(),
        150, 250);
    temp.placeImageXY(this.game1.board.get(1).get(3).drawGamePiece(),
        150, 350);
    temp.placeImageXY(this.game1.board.get(2).get(0).drawGamePiece(),
        250, 50);
    temp.placeImageXY(this.game1.board.get(2).get(1).drawGamePiece(),
        250, 150);
    temp.placeImageXY(this.game1.board.get(2).get(2).drawGamePiece(),
        250, 250);
    temp.placeImageXY(this.game1.board.get(2).get(3).drawGamePiece(),
        250, 350);
    temp.placeImageXY(this.game1.board.get(3).get(0).drawGamePiece(),
        350, 50);
    temp.placeImageXY(this.game1.board.get(3).get(1).drawGamePiece(),
        350, 150);
    temp.placeImageXY(this.game1.board.get(3).get(2).drawGamePiece(),
        350, 250);
    temp.placeImageXY(this.game1.board.get(3).get(3).drawGamePiece(),
        350, 350);

    t.checkExpect(this.game1.makeScene(),temp);
  }

  //     test onMouseClicked
  void testOnMouseClicked(Tester t) {
    this.initData();

    LightEmAll temp = new LightEmAll(4,4,true);
    temp.makeSetBoard();

    GamePiece tempGamePiece = temp.board.get(0).get(0);
    temp.onMouseClicked(new Posn(14,14),"LeftButton");

    tempGamePiece.top = false;
    tempGamePiece.right = false;
    tempGamePiece.bottom = true;
    tempGamePiece.left = false;
    t.checkExpect(temp.board.get(0).get(0), tempGamePiece);

    this.initData();
    temp = new LightEmAll(4,4,true);
    temp.makeSetBoard();

    tempGamePiece = temp.board.get(0).get(0);
    temp.onMouseClicked(new Posn(14,14),"RightButton");
    tempGamePiece.top = false;
    tempGamePiece.right = false;
    tempGamePiece.bottom = false;
    tempGamePiece.left = true;
    t.checkExpect(temp.board.get(0).get(0), tempGamePiece);

    this.initData();
    temp = new LightEmAll(4,4,true);
    temp.makeSetBoard();

    temp.onMouseClicked(new Posn(205, 205),"LeftButton");
    tempGamePiece = temp.board.get(2).get(2);
    tempGamePiece.top = false;
    tempGamePiece.right = true;
    tempGamePiece.bottom = false;
    tempGamePiece.left = true;

    t.checkExpect(temp.board.get(2).get(2), tempGamePiece);


    this.initData();
    temp = new LightEmAll(4,4,true);
    temp.makeSetBoard();

    tempGamePiece = temp.board.get(2).get(1);
    temp.onMouseClicked(new Posn(205, 105),"LeftButton");
    tempGamePiece.top = true;
    tempGamePiece.right = true;
    tempGamePiece.bottom = true;
    tempGamePiece.left = true;
    t.checkExpect(temp.board.get(2).get(1), tempGamePiece);
  }

  // test updateNeighbors
  void testUpdateNeighbors(Tester t) {
    this.initData();

    LightEmAll tempGame = new LightEmAll(4,4,0);
    tempGame.makeSetBoard();
    tempGame.updateNeighbors();

    this.initNeighbors();
    t.checkExpect(tempGame.board.get(0).get(0), game1.board.get(0).get(0));


    this.initData();

    tempGame = new LightEmAll(4,4,0);
    tempGame.makeSetBoard();
    tempGame.updateNeighbors();

    this.initNeighbors();
    t.checkExpect(tempGame.board.get(3).get(3), game1.board.get(3).get(3));

    this.initData();

    tempGame = new LightEmAll(4,4,0);
    tempGame.makeSetBoard();
    tempGame.updateNeighbors();

    this.initNeighbors();
    t.checkExpect(tempGame.board, game1.board);

    this.initData();

    tempGame = new LightEmAll(4,4,0);
    tempGame.makeSetBoard();
    tempGame.updateNeighbors();

    this.initNeighbors();
    t.checkExpect(tempGame, game1);

  }

  // test drawGamePiece
  void testDrawGamePiece(Tester t) {
    this.initData();

    WorldImage gamePieceImage;
    Color powerColor = Color.LIGHT_GRAY;

    this.n01.powerStation = true;

    gamePieceImage = new RectangleImage(LightEmAll.PIECE_SIZE, LightEmAll.PIECE_SIZE,
        OutlineMode.OUTLINE, Color.black);

    gamePieceImage = new OverlayImage(new RectangleImage(LightEmAll.PIECE_SIZE - 1,
        LightEmAll.PIECE_SIZE - 1, OutlineMode.SOLID, Color.DARK_GRAY), gamePieceImage);

    gamePieceImage = new OverlayOffsetImage(new RectangleImage(LightEmAll.PIECE_SIZE / 2,
        LightEmAll.PIECE_SIZE / 10, OutlineMode.SOLID, powerColor), 
        -LightEmAll.PIECE_SIZE / 4, 0, gamePieceImage);


    gamePieceImage = new OverlayOffsetImage(new RectangleImage(LightEmAll.PIECE_SIZE / 10,
        LightEmAll.PIECE_SIZE / 2, OutlineMode.SOLID, powerColor), 0,  
        LightEmAll.PIECE_SIZE / 4, gamePieceImage );


    gamePieceImage = new OverlayOffsetImage(new RectangleImage(LightEmAll.PIECE_SIZE / 10,
        LightEmAll.PIECE_SIZE / 2, OutlineMode.SOLID, powerColor), 0, 
        -LightEmAll.PIECE_SIZE / 4, gamePieceImage );

    gamePieceImage = new OverlayImage(new StarImage(LightEmAll.PIECE_SIZE / 4, 
        OutlineMode.SOLID, Color.blue), gamePieceImage);


    t.checkExpect(n01.drawGamePiece(), gamePieceImage);

    gamePieceImage = new RectangleImage(LightEmAll.PIECE_SIZE, LightEmAll.PIECE_SIZE,
        OutlineMode.OUTLINE, Color.black);

    gamePieceImage = new OverlayImage(new RectangleImage(LightEmAll.PIECE_SIZE - 1,
        LightEmAll.PIECE_SIZE - 1, OutlineMode.SOLID, Color.DARK_GRAY), gamePieceImage);

    gamePieceImage = new OverlayOffsetImage(new RectangleImage(LightEmAll.PIECE_SIZE / 10,
        LightEmAll.PIECE_SIZE / 2, OutlineMode.SOLID, powerColor), 0,  
        LightEmAll.PIECE_SIZE / 4, gamePieceImage );


    gamePieceImage = new OverlayOffsetImage(new RectangleImage(LightEmAll.PIECE_SIZE / 10,
        LightEmAll.PIECE_SIZE / 2, OutlineMode.SOLID, powerColor), 0, 
        -LightEmAll.PIECE_SIZE / 4, gamePieceImage );

    t.checkExpect(n02.drawGamePiece(), gamePieceImage);

    gamePieceImage = new RectangleImage(LightEmAll.PIECE_SIZE, LightEmAll.PIECE_SIZE,
        OutlineMode.OUTLINE, Color.black);

    gamePieceImage = new OverlayImage(new RectangleImage(LightEmAll.PIECE_SIZE - 1,
        LightEmAll.PIECE_SIZE - 1, OutlineMode.SOLID, Color.DARK_GRAY), gamePieceImage);

    gamePieceImage = new OverlayOffsetImage(new RectangleImage(LightEmAll.PIECE_SIZE / 10,
        LightEmAll.PIECE_SIZE / 2, OutlineMode.SOLID, powerColor), 0,  
        LightEmAll.PIECE_SIZE / 4, gamePieceImage );

    t.checkExpect(n03.drawGamePiece(), gamePieceImage);
  }

  // test rotateGamePiece
  void testRotateGamePiece(Tester t) {
    this.initData();
    GamePiece tempGamePiece = new GamePiece(0,0, false, false, false, true);

    n00.rotateGamePiece();
    t.checkExpect(n00, tempGamePiece);
    this.initData();


    this.initData();
    n22.rotateGamePiece();
    tempGamePiece = new GamePiece(2,2, false, true, false, true);
    t.checkExpect(n22, tempGamePiece);


    this.initData();
    n21.rotateGamePiece();
    tempGamePiece = new GamePiece(2,1, true, true, true, true);
    tempGamePiece.powerStation = true;
    t.checkExpect(n21, tempGamePiece);
  }

  // test updateGamePieceNeighbors
  void testUpdateGamePieceNeighbors(Tester t) {

    this.initData();

    LightEmAll tempGame = new LightEmAll(4,4,0);
    tempGame.makeSetBoard();

    GamePiece tempGamePiece = new GamePiece(0,0,false,false,true,false);
    tempGamePiece.neighbors.add(n10);
    tempGamePiece.neighbors.add(n01);
    tempGamePiece.hashedNeighbors.replace("Right", n10);
    tempGamePiece.hashedNeighbors.replace("Bottom", n01);

    n00.updateGamePieceNeighbors(tempGame);

    t.checkExpect(n00, tempGamePiece);

    this.initData();

    tempGame = new LightEmAll(4,4,0);
    tempGame.makeSetBoard();

    tempGamePiece = new GamePiece(2,1,true,true,true,true);
    tempGamePiece.neighbors.add(n20);
    tempGamePiece.neighbors.add(n31);
    tempGamePiece.neighbors.add(n22);
    tempGamePiece.neighbors.add(n11);
    tempGamePiece.hashedNeighbors.replace("Top", n20);
    tempGamePiece.hashedNeighbors.replace("Right", n31);
    tempGamePiece.hashedNeighbors.replace("Bottom", n22);
    tempGamePiece.hashedNeighbors.replace("Left", n11);
    tempGamePiece.powerStation = true;

    n21.updateGamePieceNeighbors(tempGame);

    t.checkExpect(n21, tempGamePiece);

    this.initData();

    tempGame = new LightEmAll(4,4,0);
    tempGame.makeSetBoard();

    tempGamePiece = new GamePiece(2,3,true,false,false,false);
    tempGamePiece.neighbors.add(n22);
    tempGamePiece.neighbors.add(n33);
    tempGamePiece.neighbors.add(n13);
    tempGamePiece.hashedNeighbors.replace("Top", n22);
    tempGamePiece.hashedNeighbors.replace("Right", n33);
    tempGamePiece.hashedNeighbors.replace("Left", n13);

    n23.updateGamePieceNeighbors(tempGame);
    t.checkExpect(n23, tempGamePiece);

    this.initData();

    tempGame = new LightEmAll(4,4,0);
    tempGame.makeSetBoard();

    tempGamePiece = new GamePiece(3,2,true,false,true,false);
    tempGamePiece.neighbors.add(n31);
    tempGamePiece.neighbors.add(n33);
    tempGamePiece.neighbors.add(n22);
    tempGamePiece.hashedNeighbors.replace("Top", n31);
    tempGamePiece.hashedNeighbors.replace("Bottom", n33);
    tempGamePiece.hashedNeighbors.replace("Left", n22);

    n32.updateGamePieceNeighbors(tempGame);
    t.checkExpect(n32, tempGamePiece);
  }

  //tests makeFractalBoard
  void testMakeFractalBoard(Tester t) {
    ArrayList<ArrayList<GamePiece>> tempBoard = new ArrayList<ArrayList<GamePiece>>();

    LightEmAll gameTest = new LightEmAll(4,4, 0);
    ArrayList<GamePiece> tempC0 = new ArrayList<GamePiece>();
    ArrayList<GamePiece> tempC1 = new ArrayList<GamePiece>();
    ArrayList<GamePiece> tempC2 = new ArrayList<GamePiece>();
    ArrayList<GamePiece> tempC3 = new ArrayList<GamePiece>();

    tempBoard.add(tempC0);
    tempBoard.add(tempC1);
    tempBoard.add(tempC2);
    tempBoard.add(tempC3);

    tempC0.add(new GamePiece(0,0, false, false, true, false));
    tempC0.add(new GamePiece(0,1, true, true, true, false));
    tempC0.add(new GamePiece(0,2, true, false, true, false));
    tempC0.add(new GamePiece(0,3, true, true, false, false));

    tempC1.add(new GamePiece(1,0, false, false, true, false));
    tempC1.add(new GamePiece(1,1, true, false, false, true));
    tempC1.add(new GamePiece(1,2, false, false, true, false));
    tempC1.add(new GamePiece(1,3, true, true, false, true));

    tempC2.add(new GamePiece(2,0, false, false, true, false));
    tempC2.add(new GamePiece(2,1, true, true, false, false));
    tempC2.add(new GamePiece(2,2, false, false, true, false));
    tempC2.add(new GamePiece(2,3, true, true, false, true));

    tempC3.add(new GamePiece(3,0, false, false, true, false));
    tempC3.add(new GamePiece(3,1, true, false, true, true));
    tempC3.add(new GamePiece(3,2, true, false, true ,false));
    tempC3.add(new GamePiece(3,3, true, false, false, true));

    gameTest.makeFractalBoard();
    t.checkExpect(gameTest.board, tempBoard);
  }

  // tests makeFractalHelper
  void testMakeFractalHelper(Tester t) {
    ArrayList<ArrayList<GamePiece>> tempBoard = new ArrayList<ArrayList<GamePiece>>();

    LightEmAll gameTest = new LightEmAll(4,4, 0);
    ArrayList<GamePiece> tempC0 = new ArrayList<GamePiece>();
    ArrayList<GamePiece> tempC1 = new ArrayList<GamePiece>();
    ArrayList<GamePiece> tempC2 = new ArrayList<GamePiece>();
    ArrayList<GamePiece> tempC3 = new ArrayList<GamePiece>();

    tempBoard.add(tempC0);
    tempBoard.add(tempC1);
    tempBoard.add(tempC2);
    tempBoard.add(tempC3);

    tempC0.add(new GamePiece(0,0, false, false, true, false));
    tempC0.add(new GamePiece(0,1, true, true, true, false));
    tempC0.add(new GamePiece(0,2, true, false, true, false));
    tempC0.add(new GamePiece(0,3, true, true, false, false));

    tempC1.add(new GamePiece(1,0, false, false, true, false));
    tempC1.add(new GamePiece(1,1, true, false, false, true));
    tempC1.add(new GamePiece(1,2, false, false, true, false));
    tempC1.add(new GamePiece(1,3, true, true, false, true));

    tempC2.add(new GamePiece(2,0, false, false, true, false));
    tempC2.add(new GamePiece(2,1, true, true, false, false));
    tempC2.add(new GamePiece(2,2, false, false, true, false));
    tempC2.add(new GamePiece(2,3, true, true, false, true));

    tempC3.add(new GamePiece(3,0, false, false, true, false));
    tempC3.add(new GamePiece(3,1, true, false, true, true));
    tempC3.add(new GamePiece(3,2, true, false, true ,false));
    tempC3.add(new GamePiece(3,3, true, false, false, true));

    gameTest.makeBaseBoard();
    gameTest.makeFractalHelper(gameTest.board, gameTest.width, gameTest.height);
    t.checkExpect(gameTest.board, tempBoard);
  }

  //tests BigBang
  void testBigBang(Tester t) {
    int gameWidth = 6;
    int gameHeight = 6;
    LightEmAll w = new LightEmAll(gameWidth,gameHeight,new Random());
    w.bigBang(gameWidth * LightEmAll.PIECE_SIZE, 
        gameHeight * LightEmAll.PIECE_SIZE, 1.0);
  }
}
