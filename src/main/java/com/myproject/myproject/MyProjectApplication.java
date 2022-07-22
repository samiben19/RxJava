package com.myproject.myproject;

import io.reactivex.CompletableObserver;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import lombok.extern.slf4j.Slf4j;
import model.Product;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import persistance.IRepoProduct;
import persistance.RepoProduct;
import service.ProductService;

import java.time.LocalDate;
import java.util.List;
@Slf4j
@SpringBootApplication
public class MyProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyProjectApplication.class, args);

        ProductService productService = new ProductService(new RepoProduct("src/main/resources/products.csv"));

        productService.reserveProduct("Product2", 1).subscribe();

//        productService.getProductByLocation(1, 5)
//                .subscribe(product -> System.out.println(product));

//        log.warn("Before");
//        productService.getAllProducts().subscribe(products -> products.forEach(System.out::println));
//
//        log.warn("After");
//        productService.addProduct("Product2", LocalDate.now(), 1, 2, 1);
//        productService.getAllProducts().subscribe(products -> products.forEach(System.out::println));


//        IRepoProduct repo = new RepoProduct("src/main/resources/products.csv");
//
//        repo.save(new Product("Produs1", LocalDate.now(), 1, 1, 250, Product.Status.AVAILABLE))
//                .subscribe(new CompletableObserver() {
//                    @Override
//                    public void onSubscribe(@NonNull Disposable d) {
//                        log.info("Beginning to save ");
//                    }
//
//                    @Override
//                    public void onComplete() {
//                    }
//
//                    @Override
//                    public void onError(@NonNull Throwable e) {
//                        log.error(e.getMessage());
//                    }
//                });
//
//        Single<List<Product>> products = repo.getAll();
//
//        products.subscribe(pr -> pr.forEach(System.out::println)).dispose();

//        Scanner sc = new Scanner(System.in);
//
//        LocalDate data = LocalDate.parse(sc.next()); //, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
//        System.out.println(data);
    }

}
