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

  boolean compute() {
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

  @Override
  int getSpace() {
    return fromStart.space + fromEnd.space;
  }

  @Override
  int getVisitedNodes() {
    return fromStart.visitedNodes.size() + fromEnd.visitedNodes.size();
  }

  @Override
  void display() {
    fromStart.display();
    fromEnd.display();
  }
}