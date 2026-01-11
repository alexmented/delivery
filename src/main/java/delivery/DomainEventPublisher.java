package delivery;

import libs.ddd.Aggregate;
public interface DomainEventPublisher {
    void publish(Iterable<? extends Aggregate<?>> aggregates);
}

