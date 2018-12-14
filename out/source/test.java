import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class test extends PApplet {

ArrayList trials;
int numTrials = 1;
int numLetters = 26;
double branchesStat;
int frames = 2;

public void setup() {
  
  frameRate(frames);

  trials = new ArrayList();
  for(int i = 0; i < numTrials; i++) {
    trials.add(new Trial());
  }
  
  //Branch Statistics
  double branches = 0;
  for(Object t : trials) {
    branches += ((Trial)t).branches;
  }
  branchesStat = branches/numTrials;
}

public void draw() {
  double successes = 0;
  double pathSizes = 0;
  double timeTaken = 0;
  double timeComplexity = 0;
  double spaceComplexity = 0;
  for(Object t : trials) {
    Trial trial = (Trial)t;
    trial.draw();
    successes += trial.success;
    pathSizes += trial.pathSize;
    timeTaken += trial.timeTaken;
    timeComplexity += trial.timeComplexity;
    spaceComplexity += trial.spaceComplexity;
  }
  printStats(successes, pathSizes, timeTaken/1000, timeComplexity, spaceComplexity);
}

public void printStats(double successes, double pathSizes, double timeTaken, double timeComplexity, double spaceComplexity) {
  println("Successes: " + successes);
  println("Branches: " + branchesStat);
  println("Average Path Length: " + pathSizes/numTrials);
  println("Framerate: " + frames + " fps");
  println("Average Running Time (seconds): " + timeTaken/numTrials);
  println("Average Time Complexity (nodes): " + timeComplexity/numTrials);
  println("Average Space Complexity (nodes): " + spaceComplexity/numTrials);
}

class Trial {
  City[] cities;
  SearchAlgorithm algorithm;
  SearchAlgo algo = SearchAlgo.IDS;
  DistanceHeuristic heur = DistanceHeuristic.he;
  boolean stopSearch = false;
  int mapSize = 100;
  //Stats
  int branches = 0;
  int success = 0;
  int pathSize = 0;
  int timeTaken = 0;
  int timeComplexity = 0;
  int spaceComplexity = 0;

  public Trial() {
    init();
  }

  public void init() {
    cities = new City[numLetters];

    for (int i = 0; i < numLetters; i++) {
      int rx = (int)random(mapSize);
      int ry = (int)random(mapSize);
      float suffix = width/mapSize;
      char letter = 'A';
      cities[i] = new City(rx*suffix, ry*suffix, suffix, suffix, Character.toString(((char)(letter + i))));
    }

    branches = 0;
    for(City city : cities) {
      Neighbor[] closest = getClosestCitiesTo(city, 5);
      knuthShuffle(closest);
      int rand = (int)random(1,5);
      for(int i = 0; i < rand; i++) {
        branches += city.connectWith(closest[i]);
      }
    }

    for(City city : cities) {
      city.display(255,255);
    }
    int startCity = (int)random(numLetters);
    int endCity = (int)random(numLetters);
    City startNode = cities[startCity];
    City endNode = cities[endCity];

    startNode.display(200,200);
    endNode.display(200,200);
    startNode.display(100,-1);
    endNode.display(-1,100);
    println("Starting Node: " + startNode.letter);
    println("Goal Node: " + endNode.letter);

    switch(algo) {
      case BFS:
        algorithm = new BreadthFirstSearch(startNode, endNode);
        break;
      case DFS:
        algorithm = new DepthFirstSearch(startNode, endNode);
        break;
      case IDS:
        algorithm = new IterativeDeepeningSearch(startNode, endNode);
        break;
      case GBFS:
        algorithm = new GreedyBestFirstSearch(startNode, endNode, heur);
        break;
      case ASTAR:
        algorithm = new AStar(startNode, endNode, heur);
        break;
      case BIASTAR:
        algorithm = new AStarBiDirectional(startNode, endNode, heur);
        break;
      case BIBFS:
        algorithm = new BFSBiDirectional(startNode, endNode);
        break;
    }
  }

  public void draw() {
    if(!stopSearch) {
      stopSearch = algorithm.compute();
      algorithm.display();
      for(Neighbor n : algorithm.getPath()) {
        pathSize++;
        print(n.getCity().getLetter());
        print("->");
      }
      timeTaken = millis();
      timeComplexity = algorithm.getVisitedNodes();
      spaceComplexity = algorithm.getSpace();
    } else {
      if(algorithm.getSuccess())
        success = 1;
    }
  }

  public Neighbor[] getClosestCitiesTo(City city, int howMany) {
  double[] distances = new double[numLetters];
  for(int i = 0; i < numLetters; i++) {
    City c = cities[i];
    double dist = getEucledianDistanceBetween(city,c);
    distances[i] = dist;
  }

  int largest = distances.length - 1;
  long mask = -1l;
  while ((~mask & largest) != largest) mask <<= 1;
  for (int i = 0; i < distances.length; ++i)
    distances[i] = Double.longBitsToDouble(
                    Double.doubleToLongBits(distances[i]) & mask | i);
  Arrays.sort(distances);
  
  Neighbor[] closestCities = new Neighbor[howMany];
  for(int i = 1; i <= howMany;i++) {
    closestCities[i-1] = new Neighbor(cities[(int) (Double.doubleToLongBits(distances[i]) & ~mask)], distances[i]);
  }
  return closestCities;
}
}
abstract class BiDirectionalSearch extends SearchClass {
  Stack<Neighbor> queue;
  LinkedList<Neighbor> endQueue;
  SearchClass fromStart;
  SearchClass fromEnd;

  public BiDirectionalSearch(City start, City goal) {
    super(start,goal);
    endQueue = new LinkedList<Neighbor>();
    queue = new Stack<Neighbor>();
  }

  public boolean compute() {
    if(fromStart.compute()) {
      path = fromStart.path;
      success = fromStart.success;
      return true;
    }
    Neighbor startFrontier = fromStart.getFrontier();
    if(alreadyVisited(startFrontier,fromEnd.visitedNodes)) {
      Iterator<Neighbor> itr = fromEnd.visitedNodes.descendingIterator();
      Neighbor endFrontier = fromEnd.getFrontier();
      while(!endFrontier.theSameAs(startFrontier)) {
        endFrontier = itr.next();
      }
      fromStart.getPathFrom(startFrontier, queue);
      for(int i = queue.size(); i > 1; i--) {
        path.add(queue.pop());
      }
      fromEnd.getPathFrom(endFrontier, endQueue);
      for(int i = endQueue.size(); i > 0; i--) {
        path.add(endQueue.poll());
      }
      success = true;
      return success;
    }
    
    if(fromEnd.compute()) {
      Iterator<Neighbor> itr = fromEnd.path.descendingIterator();
      while(itr.hasNext()){
        path.add(itr.next());
      }
      success = fromEnd.success;
      return true;
    }
    Neighbor endFrontier = fromEnd.getFrontier();
    if(alreadyVisited(endFrontier,fromStart.visitedNodes)){
      Iterator<Neighbor> itr = fromStart.visitedNodes.descendingIterator();
      while(!startFrontier.theSameAs(endFrontier)) {
        startFrontier = itr.next();
      }
      fromStart.getPathFrom(startFrontier, queue);
      for(int i = queue.size(); i > 1; i--) {
        path.add(queue.pop());
      }
      fromEnd.getPathFrom(endFrontier, endQueue);
      for(int i = endQueue.size(); i > 0; i--) {
        path.add(endQueue.poll());
      }
      success = true;
      return success;
    }
    return success;
  }

  public @Override
  int getSpace() {
    return fromStart.space + fromEnd.space;
  }

  public @Override
  int getVisitedNodes() {
    return fromStart.visitedNodes.size() + fromEnd.visitedNodes.size();
  }

  public @Override
  void display() {
    fromStart.display();
    fromEnd.display();
  }
}

public class GBFSComparator implements Comparator<Neighbor> {
  DistanceHeuristic h;
  City goal;

  public GBFSComparator(DistanceHeuristic h, City goal) {
    this.h = h;
    this.goal = goal;
  }

  @Override
  public int compare(Neighbor x, Neighbor y) {
    return (int)(x.getDistance(goal,h) - y.getDistance(goal,h));
  }
}

public class AStarComparator implements Comparator<Neighbor> {
  DistanceHeuristic h;
  City goal;

  public AStarComparator(DistanceHeuristic h, City goal) {
    this.h = h;
    this.goal = goal;
  }

  @Override
  public int compare(Neighbor x, Neighbor y) {
    double xdist = x.getDistance(null,h) + x.getDistance(goal,h);
    double ydist = y.getDistance(null,h) + y.getDistance(goal,h);

    return (int)(xdist - ydist);
  }
}

abstract class BestFirstSearch extends SearchClass {
  PriorityQueue<Neighbor> queue;

  public BestFirstSearch(City start, City goal, Comparator<Neighbor> comp) {
    super(start,goal);
    Comparator<Neighbor> comparator = comp;
    queue = new PriorityQueue<Neighbor>(numLetters, comparator);
    queue.add(new Neighbor(startNode,0));
  }

  public boolean compute() {
    if(queue.isEmpty()){
      println("Goal NOT Found!!");
      return true;
    }
    current = queue.poll();
    return run(queue);
  }

}

class GreedyBestFirstSearch extends BestFirstSearch {
  public GreedyBestFirstSearch(City start, City goal, DistanceHeuristic h) {
    super(start, goal, new GBFSComparator(h, goal));
  }
}

class AStar extends BestFirstSearch {
  public AStar(City start, City goal, DistanceHeuristic h) {
    super(start, goal, new AStarComparator(h ,goal));
  }
}

class AStarBiDirectional extends BiDirectionalSearch {
  public AStarBiDirectional(City start, City goal, DistanceHeuristic h) {
    super(start,goal);
    fromStart = new AStar(start, goal, h);
    fromEnd = new AStar(goal, start, h);
  }
}
class Neighbor {
  private City city;
  private Neighbor parent = null;
  private double distance = 0.0f;
  public int depth = 0;


  Neighbor(City city, double distance) {
    this.city = city;
    this.distance = distance;
  }

  Neighbor(City city, double distance, int depth, Neighbor parent) {
    this.city = city;
    this.distance = distance;
    this.depth = depth;
    this.parent = parent;
  }

  public double getDistance(City goal, DistanceHeuristic heuristic) {
    if(goal != null)
    {
      switch (heuristic) {
        case h0:
          return getConstant0();
        case hm:
          return getManhattanDistanceBetween(this.city, goal);
        case he:
          return getEucledianDistanceBetween(this.city, goal);
      }
    }
    return distance;
  }

  public City getCity() {
    return city;
  }

  public Neighbor getParent() {
    return parent;
  }

  public void print() {
    println(city.getLetter() + " distance: " + Double.toString(distance));
  }

  public boolean theSameAs(Neighbor neighbor) {
    if(neighbor.getCity().getLetter() == this.city.getLetter()){
      return true;
    }
    return false;
  }
}
interface SearchAlgorithm {
  public boolean compute();
  public void display();
  public LinkedList<Neighbor> getPath();
  public boolean getSuccess();
  public int getVisitedNodes();
  public int getSpace();
}

abstract class SearchClass implements SearchAlgorithm {
  City startNode;
  City goalNode;
  LinkedList<Neighbor> visitedNodes;
  LinkedList<Neighbor> path;
  Neighbor current;
  boolean success = false;;
  int depth = 0;
  int space = 0;

  SearchClass (City start, City goal) {
    startNode = start;
    goalNode = goal;
    visitedNodes = new LinkedList<Neighbor>();
    path = new LinkedList<Neighbor>();
  }

  public boolean run(Collection queue) {
    City currentCity = current.getCity();
    visitedNodes.add(current);
    if(currentCity.equals(goalNode)) {
      return goalFound();
    }
    else{
      for(Neighbor n : currentCity.getNeighbors()) {
        if(alreadyVisited(n, queue) || alreadyVisited(n, visitedNodes))
          continue;
        n.depth = current.depth + 1;
        n.parent = current;
        queue.add(n);
        space++;
      }
    }
    return false;
  }

  public void display() {
    for(Neighbor n : visitedNodes) {
      n.getCity().display(100,100);
      //print(n.getCity().getLetter());
    }
    //println("");
  }

  public LinkedList<Neighbor> getPath() {
    return path;
  }

  public boolean getSuccess() {
    return success;
  }

  public int getVisitedNodes() {
    return visitedNodes.size();
  }

  public int getSpace() {
    return space;
  }

  public Neighbor getFrontier() {
    return current;
  }

  public void getPathFrom(Neighbor n, List<Neighbor> q) {
    while(n != null) {
      q.add(n);
      n = n.getParent();
    }
  }

  public boolean goalFound() {
    println("Goal Node Found!");
    Stack<Neighbor> s = new Stack<Neighbor>();
    getPathFrom(current, s);
    for(int i = s.size(); i > 0; i--) {
      path.add(s.pop());
    }
    depth = current.depth;
    success = true;
    return success;
  }

  public boolean alreadyVisited(City n, Collection<City> col) {
    for(City node : col) {
      if(node.theSameAs(n)) {
        return true;
      }
    }
    return false;
  }

  public boolean alreadyVisited(Neighbor n, Collection<Neighbor> col) {
    for(Neighbor node : col) {
      if(node.theSameAs(n)) {
        return true;
      }
    }
    return false;
  }
}
class BreadthFirstSearch extends SearchClass {
  LinkedList<Neighbor> queue;
  BreadthFirstSearch(City start, City goal){
    super(start,goal);
    queue = new LinkedList<Neighbor>();
    queue.add(new Neighbor(startNode,0));
  }

    public boolean compute() {
    if(queue.isEmpty()){
      println("Goal NOT Found!!");
      return true;
    }
    current = queue.remove();
    return run(queue);
  }
}

class BFSBiDirectional extends BiDirectionalSearch {
  public BFSBiDirectional(City start, City goal) {
    super(start,goal);
    fromStart = new BreadthFirstSearch(start, goal);
    fromEnd = new BreadthFirstSearch(goal, start);
  }
}

class DepthFirstSearch extends SearchClass {
  Stack<Neighbor> queue;
  public DepthFirstSearch(City start, City goal) {
      super(start, goal);
      queue = new Stack<Neighbor>();
      queue.add(new Neighbor(startNode,0));
  }

  public boolean compute() {
    if(queue.isEmpty()){
      println("Goal NOT Found!!");
      return true;
    }
    current = queue.pop();
    return run(queue);
  }
}

class DepthLimitedSearch extends SearchClass {
  int limit = 0;
  Stack<Neighbor> queue;

  public DepthLimitedSearch(City start, City goal) {
    super(start, goal);
    queue = new Stack<Neighbor>();
    queue.add(new Neighbor(startNode,0,0, null));
  }

  public boolean compute() {
    if(queue.isEmpty()){
      println("Goal NOT Found!!");
      return true;
    }
    current = queue.pop();
    return run(queue);
  }
}

class IterativeDeepeningSearch extends SearchClass {
  int limit = 0;
  DepthLimitedSearch dls;

  public IterativeDeepeningSearch(City start, City goal) {
    super(start,goal);
    dls = new DepthLimitedSearch(start,goal);
  }

  public boolean compute() {
    dls.limit = limit;
    if(dls.compute()) {
      limit++;
      success = dls.success;
      path = dls.path;
      return success;
    }
    return false;
  }

  public @Override
  int getSpace() {
    return dls.space;
  }

  public @Override
  int getVisitedNodes() {
    return dls.visitedNodes.size();
  }

  public @Override
  void display() {
    dls.display();
  }
}
interface Cell{
  public void display(int c, int d);
  public int connectWith(Neighbor neighbor);
}

abstract class CellLocation implements Cell {
  float x, y;   // x,y location
  float w, h;   // width and height

  CellLocation(float x, float y, float w, float h) {
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
  }
}

public class City extends CellLocation {
  private String letter;
  private float centerX, centerY;
  private ArrayList<Neighbor> neighbors;

  City(float x, float y, float w, float h, String letter) {
    super(x,y,w,h);
    this.letter = letter;
    centerX = x+(w/2);
    centerY = y+(h/2);
    neighbors = new ArrayList<Neighbor>();
  }

  public float getCenterX() {
    return centerX;
  }

  public float getCenterY() {
    return centerY;
  }

  public String getLetter() {
    return letter;
  }

  public void display(int c, int d) {
    stroke(255);
    strokeWeight(1);
    if(c != -1) {
      fill(c);
      rect(x,y,w/2,h);
    }
    if(d != -1) {
      fill(d);
      rect(x+w/2,y,w/2,h);
    }
    fill(0);
    textSize(w/1.25f);
    textAlign(CENTER,TOP);
    text(letter, x, y, w, h);
  }

  public int connectWith(Neighbor neighbor) {
    int temp = 0;
    City city = neighbor.getCity();
    if(isConnectedWith(city)) {
      return temp;
    }
    temp++;
    displayConnection(city, neighbor);
    neighbors.add(neighbor);
    temp += city.connectWith(new Neighbor(this,neighbor.getDistance(null, DistanceHeuristic.he)));
    return temp;
  }

  public void displayConnection(City city, Neighbor neighbor) {
    stroke(0);
    strokeWeight(2);
    line(centerX,centerY,city.getCenterX(),city.getCenterY());
    fill(50);
    textSize(w/1.25f);
    textAlign(CENTER,CENTER);
    text(Integer.toString((int)neighbor.getDistance(null, DistanceHeuristic.he)),x-(w/2),y-(w/2),city.getCenterX()-centerX+w,city.getCenterY()-centerY+h);
  }

  public boolean isConnectedWith(City city) {
    //TODO: finish this use ds
    for(int i = 0; i < neighbors.size(); i++) {
      Neighbor neighbor = neighbors.get(i);
      if(neighbor.getCity().getLetter() == city.getLetter()){
        return true;
      }
    }
    return false;
  }

  public ArrayList<Neighbor> getNeighbors() {
    return neighbors;
  }

  public boolean theSameAs(City city) {
    if(city.getLetter() == this.letter){
      return true;
    }
    return false;
  }
}
enum SearchAlgo {
    BFS, DFS, GBFS, ASTAR, IDS, BIASTAR, BIBFS
}

enum DistanceHeuristic {
    h0, hm, he
}

public double getEucledianDistanceBetween(City city1, City city2) {
  float x2 = sq(city1.getCenterX() - city2.getCenterX());
  float y2 = sq(city1.getCenterY() - city2.getCenterY());
  return (double)sqrt(x2 + y2);
}

public double getManhattanDistanceBetween(City city1, City city2) {
  return Math.abs(city2.getCenterX()-city1.getCenterX()) + Math.abs(city2.getCenterY()-city1.getCenterY());
}

public double getConstant0() {
  return 0;
}

//import java.util.Random;
public static final Random gen = new Random();
public static void knuthShuffle (Object[] array) {
    int n = array.length;
    while (n > 1) {
        int k = gen.nextInt(n--);
        Object temp = array[n];
        array[n] = array[k];
        array[k] = temp;
    }
}
  public void settings() {  size(1500, 1500); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "test" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
