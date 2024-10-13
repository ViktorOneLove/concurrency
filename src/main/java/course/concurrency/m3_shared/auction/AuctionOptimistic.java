package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;

    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private AtomicReference<Bid> latestBid = new AtomicReference<>(null);

    public boolean propose(Bid bid) {
        Bid oldBid;

        do {
            oldBid = latestBid.get();

            if (oldBid != null && bid.getPrice() <= oldBid.getPrice()) {
                return false;
            }

        } while (!latestBid.compareAndSet(oldBid, bid));

        notifier.sendOutdatedMessage(latestBid.get());

        return true;
    }

    public Bid getLatestBid() {
        return latestBid.get();
    }
}
