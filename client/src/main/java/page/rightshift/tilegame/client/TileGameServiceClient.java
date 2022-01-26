package page.rightshift.tilegame.client;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import page.rightshift.tilegame.Protos.ConnectRequest;
import page.rightshift.tilegame.Protos.ConnectResponse;
import page.rightshift.tilegame.Protos.Direction;
import page.rightshift.tilegame.Protos.Entity;
import page.rightshift.tilegame.TileGameServiceGrpc;
import page.rightshift.tilegame.TileGameServiceGrpc.TileGameServiceBlockingStub;
import page.rightshift.tilegame.client.ServerState.ServerStateListener;

/**
 * Client for calling the TileGameService.
 */
public class TileGameServiceClient {

  private static final Logger logger = Logger.getLogger(TileGameServiceClient.class.getName());
  private final TileGameServiceBlockingStub blockingStub;
  private String host;
  private int port;
  private ManagedChannel channel;
  private String streamId;


  public TileGameServiceClient(String host, int port) {
    this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    this.host = host;
    this.port = port;
  }

  public TileGameServiceClient(ManagedChannelBuilder<?> channelBuilder) {
    channel = channelBuilder.build();
    blockingStub = TileGameServiceGrpc.newBlockingStub(channel);
  }

  public static void main(String args[]) throws InterruptedException {
    logger.info("making test call");

    ServerState serverState = new TileGameServiceClient("localhost", 8888).connect("foo", "bar",
        new ServerStateListener() {
          @Override
          public void entityAdded(Entity entity) {
            logger.log(Level.INFO, "server added entity {0}", entity.getId());
          }

          @Override
          public void entityUpdated(Entity entity) {
            logger.log(Level.INFO, "server updated entity {0}", entity.getId());
          }

          @Override
          public void entityRemoved(String entityId) {
            logger.log(Level.INFO, "server removed entity {0}", entityId);
          }

          @Override
          public void error(Throwable t) {
            logger.log(Level.WARNING, "server returned an error", t);
          }

          @Override
          public void disconnected() {
            logger.log(Level.WARNING, "server disconnected");

          }
        });
    serverState.move(Direction.UP);

    Thread.sleep(2000);
  }

  public ServerState connect(String name, String password, ServerStateListener listener)
      throws StatusRuntimeException {
    // Make initial connect call to get the stream token.
    ConnectRequest request = ConnectRequest.newBuilder().setName("foo").build();
    ConnectResponse response = blockingStub.connect(request);
    channel.shutdownNow();

    // Recreate the channel so that it will include the stream token as a header.
    Metadata metadata = new Metadata();
    metadata.put(Metadata.Key.of("stream-token", ASCII_STRING_MARSHALLER),
        response.getStreamToken());
    channel = ManagedChannelBuilder.forAddress(host, port)
        .intercept(MetadataUtils.newAttachHeadersInterceptor(metadata)).usePlaintext().build();

    ServerState serverState = new ServerState(response.getEntitiesList(),
        TileGameServiceGrpc.newStub(channel), listener);

    return serverState;
  }
}