import java.util.*;
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

  boolean compute() {
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