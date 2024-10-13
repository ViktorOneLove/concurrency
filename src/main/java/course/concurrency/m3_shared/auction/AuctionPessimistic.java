package course.concurrency.m3_shared.auction;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
    }

    private volatile Bid latestBid;

    public boolean propose(Bid bid) {
        if (latestBid == null || bid.getPrice() > latestBid.getPrice()) {
            synchronized (this) {
                if (latestBid == null || bid.getPrice() > latestBid.getPrice()) {
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

//    public synchronized boolean propose(Bid bid) {
//        if (latestBid == null) {
//            latestBid = bid;
//            return true;
//        } else if (bid.getPrice() > latestBid.getPrice()) {
//            notifier.sendOutdatedMessage(latestBid);
//            latestBid = bid;
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public synchronized Bid getLatestBid() {
//        return latestBid;
//    }
}
