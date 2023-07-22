package com.amazon.ata.types;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a packaging option.
 *
 * This packaging supports standard boxes, having a length, width, and height.
 * Items can fit in the packaging so long as their dimensions are all smaller than
 * the packaging's dimensions.
 */
public class Packaging {
    /**
     * The material this packaging is made of.
     */
    private Material material;

    /**
     * Instantiates a new Packaging object.
     * @param material - the Material of the package
     * @param length - the length of the package
     * @param width - the width of the package
     * @param height - the height of the package
     */
    public Packaging(Material material) {
        this.material = material;
    }

    public Material getMaterial() {
        return material;
    }

    //This method should be implemented in its classes Box or PolyBag class
    public boolean canFitItem(Item item) {
        throw new UnsupportedOperationException("This method is not supported!");
    }

    //This method should be implemented in its classes Box or PolyBag class
    public BigDecimal getMass() {
        throw new UnsupportedOperationException("This method is not supported!");
    }

    @Override
    public boolean equals(Object o) {
        // Can't be equal to null
        if (o == null) {
            return false;
        }

        // Referentially equal
        if (this == o) {
            return true;
        }

        // Check if it's a different type
        if (getClass() != o.getClass()) {
            return false;
        }

        Packaging packaging = (Packaging) o;
        return getMaterial() == packaging.getMaterial();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaterial());
        // old line
        // return Objects.hash(getMaterial(), getLength(), getWidth(), getHeight());
    }
}
