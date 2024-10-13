package course.concurrency.m3_shared.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private final AtomicMarkableReference<Bid> latestBid = new AtomicMarkableReference(null, false);

    public boolean propose(Bid bid) {
        Bid oldBid;
        boolean stopped = latestBid.isMarked();

        do {
            oldBid = latestBid.getReference();
            stopped = latestBid.isMarked();

            if (stopped || (oldBid != null && bid.getPrice() <= oldBid.getPrice())) {
                return false;
            }

        } while (!latestBid.compareAndSet(oldBid, bid, false, false));

        notifier.sendOutdatedMessage(latestBid.getReference());

        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        Bid latest;
        do {
            latest = latestBid.getReference();
        } while(!latestBid.attemptMark(latest, true));

        return latest;
    }
}
