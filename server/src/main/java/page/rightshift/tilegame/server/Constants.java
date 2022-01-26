package page.rightshift.tilegame.server;

import io.grpc.Context;

/**
 * Constants shared across the server codebase.
 */
public class Constants {

  public static final int BOARD_WIDTH = 100;
  public static final int BOARD_HEIGHT = 100;

  public static final Context.Key<String> STREAM_ID_CONTEXT_KEY = Context.key("stream-id");
}
