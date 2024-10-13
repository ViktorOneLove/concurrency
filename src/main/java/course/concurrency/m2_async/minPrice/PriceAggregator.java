package course.concurrency.m2_async.minPrice;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.*;

public class PriceAggregator {

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        Executor ex = Executors.newCachedThreadPool();

        var fp = shopIds.stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), ex)
                        .completeOnTimeout(Double.NaN, 2900, TimeUnit.MILLISECONDS)
                        .exceptionally(__ -> Double.NaN)
                ).toList();

        // TODO seems redundant
        CompletableFuture
                .allOf(fp.toArray(CompletableFuture[]::new))
                .join();

        return fp
                .stream()
                .mapToDouble(CompletableFuture::join)
                .filter(Double::isFinite)
                .min()
                .orElse(Double.NaN);

    }
}
