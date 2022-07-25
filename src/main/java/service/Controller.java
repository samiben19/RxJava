package service;


import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;
import model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import persistance.IRepoProduct;

import java.util.List;

@Slf4j
@ComponentScan("persistance")
@RestController
@RequestMapping("/rxjava/products")
public class Controller {

    private IRepoProduct repoProduct;

    @Autowired
    public Controller(@Qualifier("repoFileProduct") IRepoProduct repoProduct) { this.repoProduct = repoProduct; }

    @GetMapping
    public Single<List<Product>> getAll(){
        log.info("Called getAll for Products ...");
        return repoProduct.getAll();
    }

    @GetMapping(value="/{name}")
    public Observable<Product> getByName(@PathVariable String name){
        log.info("Called getByName for name = '{}' ...", name);
        return repoProduct.getByName(name);
    }

    @GetMapping(value="/location/{locationID}&{position}")
    public Maybe<Product> getByLocation(@PathVariable long locationID, @PathVariable long position){
        log.info("Called getByLocation for locationID = {} and position = {}", locationID, position);
        return repoProduct.getByLocation(locationID, position);
    }

    @PostMapping
    public Completable save(@RequestBody Product product){
        log.info("Beginning to save {}", product);
        return repoProduct.save(product).doOnComplete(() -> log.info("Saved successfully !"));
    }

    @PutMapping(value ="/reserve/{name}&{quantity}")
    public Completable reserve(@PathVariable String name, @PathVariable long quantity){
        log.info("Beginning to reserve the item {} with quantity {}", name, quantity);
        return repoProduct.reserve(name, quantity);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String getError(Exception e) {
        log.error(e.getMessage());
        return e.getMessage();
    }
}
