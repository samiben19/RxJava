package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;

public class Product {
    private String name;
    private LocalDate bbd; // Best Before Date
    private long locationID;
    private long position;
    private long quantity;

    public enum Status{
        AVAILABLE, RESERVED
    }
    private Status status = Status.AVAILABLE;


    public Product(){}

    public Product( String name, LocalDate bbd, long locationID, long position, long quantity, Status status) {
        this.name = name;
        this.bbd = bbd;
        this.locationID = locationID;
        this.position = position;
        this.quantity = quantity;
        this.status = status;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("bbd")
    public LocalDate getBbd() {
        return bbd;
    }
    public void setBbd(LocalDate bbd) {
        this.bbd = bbd;
    }

    @JsonProperty("locationID")
    public long getLocationID() {
        return locationID;
    }
    public void setLocationID(long locationID) {
        this.locationID = locationID;
    }

    @JsonProperty("position")
    public long getPosition() {
        return position;
    }
    public void setPosition(long position) {
        this.position = position;
    }

    @JsonProperty("quantity")
    public long getQuantity() {
        return quantity;
    }
    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    @JsonProperty("Status")
    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", bbd=" + bbd +
                ", locationID=" + locationID +
                ", position=" + position +
                ", quantity=" + quantity +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return locationID == product.locationID && position == product.position && quantity == product.quantity && Objects.equals(name, product.name) && Objects.equals(bbd, product.bbd) && status == product.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, bbd, locationID, position, quantity, status);
    }
}
