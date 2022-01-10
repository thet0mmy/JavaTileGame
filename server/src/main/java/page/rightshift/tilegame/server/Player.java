package page.rightshift.tilegame.server;

import page.rightshift.tilegame.Protos;

/**
 * A player controlled entity.
 */
public class Player extends Entity {

  private String name;

  /**
   * Creates a new {@link Player}.
   */
  public Player(String id, Location location, String name) {
    super(id, location);
    this.name = name;
  }

  /**
   * Creates a new Player from a player Entity proto.
   */
  public static Player fromProto(Protos.Entity entityProto) {
    if (!entityProto.hasPlayer()) {
      throw new IllegalArgumentException("not a player entity");
    }

    return new Player(entityProto.getId(),
        new Location(entityProto.getLocation().getX(), entityProto.getLocation().getY()),
        entityProto.getPlayer().getName());
  }

  public String getName() {
    return name;
  }

  @Override
  public Protos.Entity toProto() {
    return Protos.Entity.newBuilder().setId(getId()).setLocation(
            Protos.Location.newBuilder().setX(getLocation().getX()).setY(getLocation().getY()))
        .setPlayer(Protos.Player.newBuilder().setName(getName())).build();
  }
}
