interface SearchAlgorithm {
  boolean compute();
  void display();
  LinkedList<Neighbor> getPath();
  boolean getSuccess();
  int getVisitedNodes();
  int getSpace();
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

  boolean run(Collection queue) {
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

  void display() {
    for(Neighbor n : visitedNodes) {
      n.getCity().display(100,100);
      //print(n.getCity().getLetter());
    }
    //println("");
  }

  LinkedList<Neighbor> getPath() {
    return path;
  }

  boolean getSuccess() {
    return success;
  }

  int getVisitedNodes() {
    return visitedNodes.size();
  }

  int getSpace() {
    return space;
  }

  public Neighbor getFrontier() {
    return current;
  }

  void getPathFrom(Neighbor n, List<Neighbor> q) {
    while(n != null) {
      q.add(n);
      n = n.getParent();
    }
  }

  boolean goalFound() {
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

  boolean alreadyVisited(City n, Collection<City> col) {
    for(City node : col) {
      if(node.theSameAs(n)) {
        return true;
      }
    }
    return false;
  }

  boolean alreadyVisited(Neighbor n, Collection<Neighbor> col) {
    for(Neighbor node : col) {
      if(node.theSameAs(n)) {
        return true;
      }
    }
    return false;
  }
}