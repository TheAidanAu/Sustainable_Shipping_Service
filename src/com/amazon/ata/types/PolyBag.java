package com.amazon.ata.types;

import java.math.BigDecimal;

public class PolyBag extends Packaging {
    private BigDecimal volume;

    /// need to remove the length, width, height varaibles from the Packaging and PolyBag class
    /**
     * Instantiates a new Packaging object.
     *
     * //@param material - the Material of the package
     */
    public PolyBag(BigDecimal volume) {
        super(Material.LAMINATED_PLASTIC);
        this.volume = volume;
    }

    // DONE_TODO: modify the canFitItem and getMass methods for this class
    /**
     * Returns whether the given item will fit in this PolyBag.
     *
     * @param item the item to test fit for
     * @return whether the item will fit in this PolyBag
     */
    @Override
    public boolean canFitItem(Item item) {
        BigDecimal itemVolume = item.getLength().multiply(item.getWidth()).multiply(item.getHeight());
        return this.volume.compareTo(itemVolume) > 0;
    }

    /**
     * Returns the mass of the packaging in grams. The packaging weighs 1 gram per square centimeter.
     * @return the mass of the packaging
     */
    @Override
    public BigDecimal getMass() {
        double volumeInDouble = getVolume().doubleValue();
        double mass = Math.ceil(Math.sqrt(volumeInDouble) * 0.6);
        return BigDecimal.valueOf(mass);
    }

    public BigDecimal getVolume() {
        return volume;
    }

}
