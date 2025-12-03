package delivery.core.application.commands;

import java.util.UUID;

public record DispatchOrderCommand(UUID orderId) {
}
