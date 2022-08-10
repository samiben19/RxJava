package service;

import io.reactivex.CompletableObserver;
import io.reactivex.Maybe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistance.RepoProduct;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {
    private ProductService service;
    private final LocalDate date = LocalDate.now();

    @BeforeEach
    public void setUp(){
        service = new ProductService(new RepoProduct("src/test/resources/test.csv"));
    }

    @AfterEach
    public void tearDown() throws FileNotFoundException {
        File file = new File("src/test/resources/test.csv");
        PrintWriter printWriter = new PrintWriter(file);
        printWriter.print("");
        printWriter.close();
    }

    @Test
    void addProduct() {
        service.addProduct("Caramel", date, 1, 1, 250).subscribe();

        List<Product> products = service.getAllProducts().blockingGet();

        Product inserted = products.get(0);
        assertEquals(1, products.size());
        assertEquals("Caramel", inserted.getName());
        assertEquals(1, inserted.getLocationID());
        assertEquals(1, inserted.getPosition());
        assertEquals(date, inserted.getBbd());

        service.addProduct("fail", date, 1,1,1)
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        assertEquals("There is already a product at location 1, position 1", e.getMessage());
                    }
                });
    }

    @Test
    void getProductByLocation() {
        addSome();
        Maybe<Product> product = service.getProductByLocation(1,1);

        assertFalse(product.isEmpty().blockingGet());
        assertEquals("Product 1", product.blockingGet().getName());

        Maybe<Product> productNotFound = service.getProductByLocation(3,5);

        assertTrue(productNotFound.isEmpty().blockingGet());
    }

    @Test
    void getAllProducts() {
        List<Product> allProducts = service.getAllProducts().blockingGet();
        assertEquals(0, allProducts.size());

        addSome();
        allProducts = service.getAllProducts().blockingGet();
        assertEquals(5, allProducts.size());

        assertTrue(allProducts
                .stream()
                .map(Product::getName)
                .collect(Collectors.toList())
                .containsAll(Arrays.asList("Product 1", "Product 2", "Product 3", "Product 4", "Product 5")));
    }

    @Test
    void reserveProduct() {
        addSome();

        Maybe<Product> product = service.getProductByLocation(1,1);
        assertFalse(product.isEmpty().blockingGet());

        assertEquals("Product 1", product.blockingGet().getName());
        assertEquals(Product.Status.AVAILABLE, product.blockingGet().getStatus());

        service.reserveProduct("Product 1", 250).subscribe();

        product = service.getProductByLocation(1,1);
        assertEquals(Product.Status.RESERVED, product.blockingGet().getStatus());

        service.reserveProduct("Product 1", 250)
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onComplete() {
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        assertEquals("Cannot reserve ! There are no items available !", e.getMessage());
                    }
                });
    }

    private void addSome(){
        service.addProduct("Product 1", date, 1, 1, 250).subscribe();
        service.addProduct("Product 2", date, 1, 2, 250).subscribe();
        service.addProduct("Product 3", date, 1, 3, 250).subscribe();
        service.addProduct("Product 4", date, 2, 1, 250).subscribe();
        service.addProduct("Product 5", date, 3, 1, 250).subscribe();
    }
}