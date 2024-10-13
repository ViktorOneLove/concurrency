package course.concurrency.m3_shared.auction;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private Notifier notifier;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid;

    private volatile boolean stopped = false;

    public boolean propose(Bid bid) {
        if (!stopped && (latestBid == null || bid.getPrice() > latestBid.getPrice())) {
            synchronized (this) {
                if (!stopped && (latestBid == null || bid.getPrice() > latestBid.getPrice())) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public synchronized Bid stopAuction() {
        stopped = true;
        return latestBid;
    }
}
