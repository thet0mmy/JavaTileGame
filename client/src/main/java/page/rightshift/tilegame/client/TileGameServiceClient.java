package page.rightshift.tilegame.client;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import page.rightshift.tilegame.Protos;
import page.rightshift.tilegame.TileGameServiceGrpc;

public class TileGameServiceClient {
    private final TileGameServiceGrpc.TileGameServiceBlockingStub blockingStub;
    private final TileGameServiceGrpc.TileGameServiceStub asyncStub;
    private final Channel channel;

    public TileGameServiceClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    public TileGameServiceClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = TileGameServiceGrpc.newBlockingStub(channel);
        asyncStub = TileGameServiceGrpc.newStub(channel);
    }

    public Protos.ConnectResponse Connect() throws StatusRuntimeException {
        Protos.ConnectRequest request = Protos.ConnectRequest.newBuilder().setName("foo").build();
        return blockingStub.connect(request);
    }

    public static void main(String args[]) {
        System.out.println(new TileGameServiceClient("localhost", 8888).Connect());
    }
}
