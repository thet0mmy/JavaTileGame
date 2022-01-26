package page.rightshift.tilegame.server;

import io.grpc.stub.StreamObserver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import page.rightshift.tilegame.Protos;
import page.rightshift.tilegame.TileGameServiceGrpc.TileGameServiceImplBase;

/**
 * The implementation of the gRPC TileGameService.
 */
public class TileGameService extends TileGameServiceImplBase {

  private static final Logger logger = Logger.getLogger(TileGameService.class.getName());

  // The StreamObservers for all the connected clients.
  private List<StreamObserver<Protos.StreamResponse>> clientStreamObservers = new ArrayList<>();

  // All the entites in play.
  private List<Entity> entities = new ArrayList<>();

  // Players need to be able to be looked up with the stream key
  private Map<String, Player> playersByStreamToken = new HashMap<>();

  @Override
  public void connect(Protos.ConnectRequest request,
      StreamObserver<Protos.ConnectResponse> responseObserver) {
    logger.info("connect called");

    // TODO: add authentication, for now just assign a stream ID.
    String streamToken = UUID.randomUUID().toString();

    // A new entity is needed to represent the new player.
    Player player = new Player(UUID.randomUUID().toString(), new Entity.Location(0, 0),
        request.getName());
    entities.add(player);
    playersByStreamToken.put(streamToken, player);

    // Return the stream token to the client so that they can make a stream call with it.
    responseObserver.onNext(
        Protos.ConnectResponse.newBuilder().setStreamToken(streamToken).build());
    responseObserver.onCompleted();
  }

  @Override
  public StreamObserver<Protos.StreamRequest> stream(
      StreamObserver<Protos.StreamResponse> responseObserver) {
    logger.info("stream called");

    // Make sure we have a valid stream token to retrieve a player with.
    String streamToken = Constants.STREAM_TOKEN_CONTEXT_KEY.get();
    if (!playersByStreamToken.containsKey(streamToken)) {
      throw new IllegalArgumentException("no valid stream-token provided");
    }

    // Newly connected clients get stored in a list so that they can be sent updates later.
    clientStreamObservers.add(responseObserver);

    // The client should add all the current entities to get started.
    for (Entity entity : entities) {
      responseObserver.onNext(Protos.StreamResponse.newBuilder()
          .setAddEntityEvent(Protos.AddEntityEvent.newBuilder().setEntity(entity.toProto()))
          .build());
    }

    // Now we start accepting updates from the client.
    return new StreamObserver<Protos.StreamRequest>() {
      @Override
      public void onNext(Protos.StreamRequest request) {
        logger.info("stream onNext");

        Player player = playersByStreamToken.get(Constants.STREAM_TOKEN_CONTEXT_KEY.get());

        // We only support move actions for now and assume the request has one...
        player.move(request.getMove().getDirection());

        // Send new player location to all clients.
        for (StreamObserver<Protos.StreamResponse> client : clientStreamObservers) {
          client.onNext(Protos.StreamResponse.newBuilder().setUpdateEntityEvent(
              Protos.UpdateEntityEvent.newBuilder().setEntity(player.toProto())).build());
          client.onCompleted();
        }
      }

      @Override
      public void onError(Throwable t) {
        logger.log(Level.WARNING, "Client stream request has failed", t);

        // The stream has failed and is closing, let's remove the client from the list, so we don't
        // try to send it further updates.
        clientStreamObservers.remove(responseObserver);
      }

      @Override
      public void onCompleted() {
        logger.info("Client stream has completed");

        // The client is done streaming and will disconnect, remove it from the list, so we don't
        // send it further updates.
        clientStreamObservers.remove(responseObserver);
      }
    };
  }
}
