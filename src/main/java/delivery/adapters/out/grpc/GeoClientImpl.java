package delivery.adapters.out.grpc;

import clients.geo.GeoGrpc;
import clients.geo.GeoProto;
import delivery.config.GrpcProperties;
import delivery.core.domain.model.Location;
import delivery.core.ports.GeoClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.annotation.PreDestroy;
import libs.errs.Err;
import libs.errs.Error;
import libs.errs.Result;
import org.springframework.stereotype.Service;

@Service
public class GeoClientImpl implements GeoClient {
    private final ManagedChannel channel;
    private final GeoGrpc.GeoBlockingStub stub;

    public GeoClientImpl(GrpcProperties properties) {
        this.channel = ManagedChannelBuilder
                .forAddress(
                        properties.getHost(),
                        properties.getPort())
                .usePlaintext()
                .build();
        this.stub = GeoGrpc.newBlockingStub(channel);
    }

    @PreDestroy
    public void shutdown() {
        if (!channel.isShutdown()) {
            channel.shutdown();
        }
    }

    @Override
    public Result<Location, Error> getGeolocation(String street) {
        if (street == null || street.isBlank()) {
             return Result.failure(Err.againstNullOrEmpty(street, "street").getError());
        }

        var request = GeoProto.GetGeolocationRequest.newBuilder()
                .setStreet(street)
                .build();

        try {
            var response = stub.getGeolocation(request);
            
            var protoLocation = response.getLocation();
            
            return Location.create(protoLocation.getX(), protoLocation.getY());
            
        } catch (Exception e) {
            return Result.failure(Error.of("geo.service.error", e.getMessage()));
        }
    }
}
