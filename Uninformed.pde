class BreadthFirstSearch extends SearchClass {
  LinkedList<Neighbor> queue;
  BreadthFirstSearch(City start, City goal){
    super(start,goal);
    queue = new LinkedList<Neighbor>();
    queue.add(new Neighbor(startNode,0));
  }

    boolean compute() {
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

  boolean compute() {
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

  boolean compute() {
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

  @Override
  int getSpace() {
    return dls.space;
  }

  @Override
  int getVisitedNodes() {
    return dls.visitedNodes.size();
  }

  @Override
  void display() {
    dls.display();
  }
}