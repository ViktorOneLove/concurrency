package course.concurrency.m3_shared.immutable;

import org.mockito.internal.matchers.Or;

import java.util.Collections;
import java.util.List;

import static course.concurrency.m3_shared.immutable.Order.Status.NEW;

public final class Order {

    public enum Status { NEW, IN_PROGRESS, DELIVERED }

    private final Long id;
    private final List<Item> items;
    private final PaymentInfo paymentInfo;
    private final boolean isPacked;
    private final Status status;

    public Order(List<Item> items, Long id) {
        this.id = id;
        this.items = List.copyOf(items);
        paymentInfo = null;
        isPacked = false;
        status = NEW;
    }

    private Order(Long id, List<Item> items, PaymentInfo paymentInfo, boolean isPacked, Status status) {
        this.id = id;
        this.items = List.copyOf(items);
        this.paymentInfo = paymentInfo;
        this.isPacked = isPacked;
        this.status = status;
    }

    public synchronized boolean checkStatus() {
        return items != null && !items.isEmpty() && paymentInfo != null && isPacked && !status.equals(Status.DELIVERED);
    }

    public Long getId() {
        return id;
    }

    public Order withId(Long id) {
        return new Order(id, this.items, this.paymentInfo, this.isPacked, this.status);
    }

    public List<Item> getItems() {
        return items;
    }

    public PaymentInfo getPaymentInfo() {
        // todo return hardcopy?
        return paymentInfo;
    }

    public Order withPaymentInfo(PaymentInfo paymentInfo) {
        return new Order(this.id, this.items, paymentInfo, this.isPacked, Status.IN_PROGRESS);
    }

    public boolean isPacked() {
        return isPacked;
    }

    public Order withPacked(boolean packed) {
        return new Order(this.id, this.items, this.paymentInfo, packed, Status.IN_PROGRESS);
    }

    public Status getStatus() {
        return status;
    }

    public Order withStatus(Status status) {
        return new Order(this.id, this.items, this.paymentInfo, this.isPacked, status);
    }
}
