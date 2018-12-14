class Neighbor {
  private City city;
  private Neighbor parent = null;
  private double distance = 0.0;
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

  double getDistance(City goal, DistanceHeuristic heuristic) {
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

  City getCity() {
    return city;
  }

  Neighbor getParent() {
    return parent;
  }

  void print() {
    println(city.getLetter() + " distance: " + Double.toString(distance));
  }

  boolean theSameAs(Neighbor neighbor) {
    if(neighbor.getCity().getLetter() == this.city.getLetter()){
      return true;
    }
    return false;
  }
}