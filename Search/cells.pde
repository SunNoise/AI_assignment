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

  float getCenterX() {
    return centerX;
  }

  float getCenterY() {
    return centerY;
  }

  String getLetter() {
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
    textSize(w/1.25);
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

  void displayConnection(City city, Neighbor neighbor) {
    stroke(0);
    strokeWeight(2);
    line(centerX,centerY,city.getCenterX(),city.getCenterY());
    fill(50);
    textSize(w/1.25);
    textAlign(CENTER,CENTER);
    text(Integer.toString((int)neighbor.getDistance(null, DistanceHeuristic.he)),x-(w/2),y-(w/2),city.getCenterX()-centerX+w,city.getCenterY()-centerY+h);
  }

  boolean isConnectedWith(City city) {
    //TODO: finish this use ds
    for(int i = 0; i < neighbors.size(); i++) {
      Neighbor neighbor = neighbors.get(i);
      if(neighbor.getCity().getLetter() == city.getLetter()){
        return true;
      }
    }
    return false;
  }

  ArrayList<Neighbor> getNeighbors() {
    return neighbors;
  }

  boolean theSameAs(City city) {
    if(city.getLetter() == this.letter){
      return true;
    }
    return false;
  }
}