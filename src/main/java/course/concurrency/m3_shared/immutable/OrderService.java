package course.concurrency.m3_shared.immutable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class OrderService {

    private Map<Long, Order> currentOrders = new ConcurrentHashMap<>();
    private AtomicLong nextId = new AtomicLong(0);

    private long nextId() {
        return nextId.incrementAndGet();
    }

    public long createOrder(List<Item> items) {
        long id = nextId();
        Order order = new Order(items, id);
        currentOrders.put(id, order);
        return id;
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        var updatedOrder = currentOrders.compute(orderId, (k, v) -> v.withPaymentInfo(paymentInfo));
        if (updatedOrder.checkStatus()) {
            deliver(updatedOrder);
        }
    }

    public void setPacked(long orderId) {
        var updatedOrder = currentOrders.compute(orderId, (k, v) -> v.withPacked(true));
        if (updatedOrder.checkStatus()) {
            deliver(updatedOrder);
        }
    }

    private void deliver(Order order) {
        /* ... */
        currentOrders.compute(order.getId(), (k, v) -> v.withStatus(Order.Status.DELIVERED));
    }

    public boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus().equals(Order.Status.DELIVERED);
    }
}
