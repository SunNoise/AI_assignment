enum SearchAlgo {
    BFS, DFS, GBFS, ASTAR, IDS, BIASTAR, BIBFS
}

enum DistanceHeuristic {
    h0, hm, he
}

double getEucledianDistanceBetween(City city1, City city2) {
  float x2 = sq(city1.getCenterX() - city2.getCenterX());
  float y2 = sq(city1.getCenterY() - city2.getCenterY());
  return (double)sqrt(x2 + y2);
}

double getManhattanDistanceBetween(City city1, City city2) {
  return Math.abs(city2.getCenterX()-city1.getCenterX()) + Math.abs(city2.getCenterY()-city1.getCenterY());
}

double getConstant0() {
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