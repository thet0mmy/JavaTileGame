package page.rightshift.tilegame.server;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.ServerInterceptors;
import io.grpc.protobuf.services.ProtoReflectionService;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * The main class of the server.
 */
public class TileGameServer {

  private static final Metadata.Key<String> STREAM_ID_METADATA_KEY = Metadata.Key.of("stream-id",
      ASCII_STRING_MARSHALLER);
  private static final String STREAM_METHOD_NAME = "Stream";

  private static final Logger logger = Logger.getLogger(TileGameServer.class.getName());

  /**
   * Starts the server.
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    Server tileGameServer = ServerBuilder.forPort(8888)
        .addService(
            ServerInterceptors.intercept(new TileGameService(), new StreamIdServerInterceptor()))
        .addService(ProtoReflectionService.newInstance())
        .build();
    tileGameServer.start();

    logger.info("Server started!");
    tileGameServer.awaitTermination();

  }

  /**
   * Intercepts incoming calls to extract the stream ID to store in the call context.
   */
  public static class StreamIdServerInterceptor implements ServerInterceptor {

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
        Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
      String streamId = metadata.get(STREAM_ID_METADATA_KEY);
      Context ctx = Context.current().withValue(Constants.STREAM_ID_CONTEXT_KEY, streamId);

      return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler);
    }
  }
}