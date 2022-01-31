package page.rightshift.tilegame.client;

import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import page.rightshift.tilegame.Protos.Direction;
import page.rightshift.tilegame.Protos.Entity;
import page.rightshift.tilegame.Protos.MoveAction;
import page.rightshift.tilegame.Protos.StreamRequest;
import page.rightshift.tilegame.Protos.StreamResponse;
import page.rightshift.tilegame.TileGameServiceGrpc.TileGameServiceStub;

public class ServerState {

  private static final Logger logger = Logger.getLogger(ServerState.class.getName());

  // Maps of all the entities on the server, keyed by their IDs.
  private final Map<String, Entity> entityMap = new HashMap<>();

  // Used to establish a bi-directional streaming connection to the server to keep the local
  // state up to date and to send local client changes to the server.
  private final TileGameServiceStub asyncStub;

  // Observer used to send requests to the server.
  private final StreamObserver<StreamRequest> clientStreamObserver;

  // Notified of server side state changes.
  private final ServerStateListener listener;

  private boolean completed;

  /**
   * Creates a new {@link ServerState} with the given entities from the Connect request.
   */
  public ServerState(List<Entity> initialEntities, TileGameServiceStub asyncStub,
      ServerStateListener listener) {
    for (Entity entity : initialEntities) {
      entityMap.put(entity.getId(), entity);
    }
    this.asyncStub = asyncStub;
    this.listener = listener;

    clientStreamObserver = asyncStub.stream(new StreamObserver<StreamResponse>() {
      @Override
      public void onNext(StreamResponse response) {
        switch (response.getEventCase()) {
          case ADD_ENTITY_EVENT -> listener.entityAdded(response.getAddEntityEvent().getEntity());
          case UPDATE_ENTITY_EVENT -> listener.entityUpdated(
              response.getUpdateEntityEvent().getEntity());
          case REMOVE_ENTITY_EVENT -> listener.entityRemoved(
              response.getAddEntityEvent().getEntity().getId());
        }
      }

      @Override
      public void onError(Throwable t) {
        listener.error(t);
      }

      @Override
      public void onCompleted() {
        completed = true;
        listener.disconnected();
      }
    });
  }

  /**
   * Moves the player one step in one direction.
   */
  public void move(Direction direction) {
    clientStreamObserver.onNext(
        StreamRequest.newBuilder().setMove(MoveAction.newBuilder().setDirection(direction))
            .build());
  }

  /**
   * Disconnect the {@link ServerState} from the server.
   */
  public void disconnect() {
    clientStreamObserver.onCompleted();
  }

  /**
   * Called when the state on the server changes.
   */
  public interface ServerStateListener {

    /**
     * A new entity was added.
     */
    void entityAdded(Entity entity);

    /**
     * An existsing entity has updated.
     */
    void entityUpdated(Entity entity);

    /**
     * An entity has been removed.
     */
    void entityRemoved(String entityId);

    void error(Throwable t);

    void disconnected();
  }
}
