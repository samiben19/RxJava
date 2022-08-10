package persistance;

import io.reactivex.*;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import model.Product;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Repository("repoFileProduct")
public class RepoProduct implements IRepoProduct{
    private List<Product> repo;
    private final String fileName;

    public RepoProduct(String fileName){
        this.repo = new ArrayList<>();
        this.fileName = fileName;
        loadData();

        log.info("Loaded RepoProduct with: ");
        repo.forEach(product -> log.info(String.valueOf(product)));
        log.info("-----------------------");
    }

    private String createProductAsString(Product product){
        return product.getName() + "," +
                product.getBbd() + "," +
                product.getLocationID() + "," +
                product.getPosition() + "," +
                product.getQuantity() + "," +
                product.getStatus();
    }

    private Product extractProduct(List<String> attributes){
        String name = attributes.get(0);
        LocalDate bbd = LocalDate.parse(attributes.get(1));
        long locationID = Long.parseLong(attributes.get(2));
        long position = Long.parseLong(attributes.get(3));
        long quantity = Long.parseLong(attributes.get(4));
        Product.Status status = Product.Status.valueOf(attributes.get(5));

        return new Product(name, bbd, locationID, position, quantity, status);
    }

    private void loadData(){
        Path path = Paths.get(fileName);

        log.info("Loaded data from {}", path);
        repo.clear();

        try{
            List<String> lines = Files.readAllLines(path);
            lines.forEach(line ->{
                List<String> attributes = Arrays.asList(line.split(","));
                Product product = extractProduct(attributes);

                repo.add(product);
            });
        }
        catch (IOException e){
            log.error(e.getMessage());
        }
    }

    private void appendToFile(Product product){
        String line = createProductAsString(product);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName,  true))){
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    protected void writeToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, false))) {
            for (Product product : repo) {
                String line = createProductAsString(product);
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public Completable save(Product product) {
        if (product == null)
            return Completable.error(new Exception("Product is null !"));


        if(repo.stream().anyMatch(product1 -> product1.getPosition() == product.getPosition()
                && product1.getLocationID() == product.getLocationID()))
            return Completable.error(new Exception("There is already a product at location " +
                    product.getLocationID() + ", position " + product.getPosition()));

        repo.add(product);
        appendToFile(product);

        return Completable.complete();
    }

    @Override
    public Completable delete(long locationID, long position) {
        return Completable.complete();
    }

    @Override
    public Completable update(long locationID, long position, Product newProduct) {
        AtomicReference<Completable> completable = new AtomicReference<>(Completable.complete());

        getByLocation(locationID, position)
                 .doOnEvent((value, error)-> {
                    if (value == null && error == null) {
                        completable.set(Completable.error(new Exception("No product at locationID=" +
                                locationID + " and position=" + position)));
                    }})
                .subscribe(x -> {
                    repo.removeIf(y -> y.getLocationID() == locationID && y.getPosition() == position);
                    repo.add(newProduct);
                    writeToFile();
                });


        return completable.get();
    }

    @Override
    public Observable<Product> getByName(String name) {
        return Observable.fromIterable(
                repo.stream()
                        .filter(product -> product.getName().equalsIgnoreCase(name))
                        .collect(Collectors.toList())
        );
    }

    @Override
    public Maybe<Product> getByLocation(long locationID, long position) {
        Optional<Product> searchedProduct =
                repo.stream()
                .filter(product -> product.getLocationID() == locationID && product.getPosition() == position)
                .findFirst();

        return searchedProduct.map(Maybe::just)
                .orElseGet(Maybe::empty);
    }

    @Override
    public Maybe<Product> getAvailableProduct(String name, long quantity){
        Optional<Product> searchedProduct =
                repo.stream()
                        .filter(product -> product.getName().equalsIgnoreCase(name) && product.getQuantity() == quantity && product.getStatus() == Product.Status.AVAILABLE)
                        .min(Comparator.comparing(Product::getBbd));

        return searchedProduct.map(Maybe::just)
                .orElseGet(Maybe::empty);
    }

    @Override
    public Completable reserve(String name, long quantity) {
        Maybe<Product> productMaybe = this.getAvailableProduct(name, quantity);
        AtomicReference<String> errorString = new AtomicReference<>(null);

        productMaybe
                .doOnEvent((value, error)-> {
                    if (value == null && error == null) {
                        errorString.set("Cannot reserve ! There are no items available !");
                        log.warn(errorString.get());
                    }})
                .subscribe(product -> this.update(product.getLocationID(), product.getPosition(), new Product(product.getName(), product.getBbd(), product.getLocationID(), product.getPosition(), product.getQuantity(), Product.Status.RESERVED))
                        .observeOn(Schedulers.io())
                        .doOnComplete(() -> log.info(productMaybe.blockingGet() + " reserved !"))
                        .subscribe());

        if(errorString.get() != null)
            return Completable.error(new Throwable(errorString.get()));
        return Completable.complete();
    }

    @Override
    public Single<List<Product>> getAll() {
        return Single.just(repo);
    }
}
