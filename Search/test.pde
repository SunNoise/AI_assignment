ArrayList trials;
int numTrials = 1;
int numLetters = 26;
double branchesStat;
int frames = 2;

void setup() {
  size(1500, 1500);
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

void draw() {
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

void printStats(double successes, double pathSizes, double timeTaken, double timeComplexity, double spaceComplexity) {
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

  void init() {
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

  void draw() {
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

  Neighbor[] getClosestCitiesTo(City city, int howMany) {
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