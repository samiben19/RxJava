package persistance;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import model.Product;

import java.util.List;

public interface IRepoProduct {
    Completable save(Product product);
    Completable delete(long locationID, long position);
    Completable update(long locationID, long position, Product newProduct);
    Observable<Product> getByName(String name);
    Maybe<Product> getByLocation(long locationID, long position);
    Maybe<Product> getAvailableProduct(String name, long quantity);
    Completable reserve(String name, long quantity);
    Single<List<Product>> getAll();
}
