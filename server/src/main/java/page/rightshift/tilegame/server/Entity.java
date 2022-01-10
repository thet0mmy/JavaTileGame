package page.rightshift.tilegame.server;

import page.rightshift.tilegame.Protos;

/**
 * Entity exists in the game at a given location on the game board.
 */
public abstract class Entity {

  private String id;
  private Location location;

  public Entity(String id, Location location) {
    this.id = id;
    this.location = location;
  }

  public String getId() {
    return id;
  }

  public Location getLocation() {
    return location;
  }

  /**
   * Moves the entity in one direction.
   */
  public void move(Protos.Direction direction) {
    switch (direction) {
      case UP:
        getLocation().setY(Math.min(Constants.BOARD_HEIGHT, getLocation().getY() + 1));
        break;
      case DOWN:
        getLocation().setY(Math.max(0, getLocation().getY() - 1));
        break;
      case RIGHT:
        getLocation().setX(Math.min(Constants.BOARD_WIDTH, getLocation().getX() + 1));
        break;
      case LEFT:
        getLocation().setX(Math.max(0, getLocation().getX() - 1));
        break;
    }
  }

  /**
   * Subclasses are responsible for returning an Entity proto representation.
   */
  public abstract Protos.Entity toProto();

  /**
   * An entity's location on the game board.
   */
  public static class Location {

    private int x;
    private int y;

    public Location(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public int getX() {
      return x;
    }

    public void setX(int x) {
      this.x = x;
    }

    public int getY() {
      return y;
    }

    public void setY(int y) {
      this.y = y;
    }
  }
}
