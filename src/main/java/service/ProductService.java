package service;

import io.reactivex.*;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import persistance.IRepoProduct;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class ProductService {
    private final IRepoProduct repoProduct;

    @Autowired
    public ProductService(@Qualifier("repoFileProduct") IRepoProduct repoProduct) {
        this.repoProduct = repoProduct;
    }

    public Completable addProduct(String name, LocalDate bbd, long locationID, long position, long quantity){
        Product product = new Product(name, bbd, locationID, position, quantity, Product.Status.AVAILABLE);

        repoProduct.save(product).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                log.info("Beginning to save {}", product);
            }

            @Override
            public void onComplete() {
                log.info("{} saved !", product);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                log.error(e.getMessage());
            }
        });

        return Completable.complete();
    }

    public Maybe<Product> getProductByLocation(long locationID, long position){
        return repoProduct.getByLocation(locationID, position);
    }

    public Single<List<Product>> getAllProducts(){
        return repoProduct.getAll();
    }

    public Completable reserveProduct(String name, long quantity){
        return repoProduct.reserve(name, quantity);
    }
}
