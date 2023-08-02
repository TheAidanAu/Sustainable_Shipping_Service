package com.amazon.ata.dao;

import com.amazon.ata.datastore.PackagingDatastore;
import com.amazon.ata.exceptions.NoPackagingFitsItemException;
import com.amazon.ata.exceptions.UnknownFulfillmentCenterException;
import com.amazon.ata.types.FcPackagingOption;
import com.amazon.ata.types.FulfillmentCenter;
import com.amazon.ata.types.Item;
import com.amazon.ata.types.Packaging;
import com.amazon.ata.types.ShipmentOption;

import java.util.*;

/**
 * Access data for which packaging is available at which fulfillment center.
 */
public class PackagingDAO {
    /**
     * A list of fulfillment centers with a packaging options they provide.
     */
    // Previously, there was a list of <FcPackagingOption>
    private Map<FulfillmentCenter, Set<Packaging>> fcPackagingOptions = new HashMap<>();
    // So, at the end of this process, fcPackagingOptions map will contain each fulfillment center as a key
    // and a set of unique packaging options as the value associated with each key

    /**
     * Instantiates a PackagingDAO object.
     * @param datastore Where to pull the data from for fulfillment center/packaging available mappings.
     */
    public PackagingDAO(PackagingDatastore datastore) {
        // we need to keep the right half
        // so look at what that returns, it returns a list FcPackagingOption
        // so put that on the left half
        List<FcPackagingOption> packagingOptions = new ArrayList<>(datastore.getFcPackagingOptions());
        // Logic: since it's a set, even if you add a dup, no dup will be kept
        // Iterate through all the packing options, see if an option contains an existing key
        // If map already contains the key, add the packaging to the set.
        // If not, create a new set, add the packaging, and put it into the map.
        for (FcPackagingOption packingOption: packagingOptions) {
            FulfillmentCenter fc = packingOption.getFulfillmentCenter();
            Packaging packaging = packingOption.getPackaging();
            if (fcPackagingOptions.containsKey(fc)) {
                fcPackagingOptions.get(fc).add(packaging);
                // Retrieve the existing Packing Set by using the get method
                // You attempt to add the packaging option,
                // if the packaging option is a dup, the set stays the same
            } else {
                Set<Packaging> newPackagingSet = new HashSet<>();
                newPackagingSet.add(packaging);
                fcPackagingOptions.put(fc, newPackagingSet);
            }
        }
    }

    /**
     * Returns the packaging options available for a given item at the specified fulfillment center.
     * The API used to call this method handles null inputs, so we don't have to.
     *
     * @param item the item to pack
     * @param fulfillmentCenter fulfillment center to fulfill the order from
     * @return the shipping options available for that item; this can never be empty, because if there is no
     * acceptable option an exception will be thrown
     * @throws UnknownFulfillmentCenterException if the fulfillmentCenter is not in the fcPackagingOptions list
     * @throws NoPackagingFitsItemException if the item doesn't fit in any packaging at the FC
     */
    public List<ShipmentOption> findShipmentOptions(Item item, FulfillmentCenter fulfillmentCenter)
            throws UnknownFulfillmentCenterException, NoPackagingFitsItemException {

        // Check all FcPackagingOptions for a suitable Packaging in the given FulfillmentCenter
        List<ShipmentOption> result = new ArrayList<>();
        boolean fcFound = false;
        Set<Packaging> packagingOptions = fcPackagingOptions.get(fulfillmentCenter);
        // Be careful: you need to check if packagingOptions is null or not first
        if (packagingOptions != null) {
            for (Packaging packaging : packagingOptions) {
                String fcCode = fulfillmentCenter.getFcCode();

                if (fcCode.equals(fulfillmentCenter.getFcCode())) {
                    fcFound = true;
                    if (packaging.canFitItem(item)) {
                        result.add(ShipmentOption.builder()
                                .withItem(item)
                                .withPackaging(packaging)
                                .withFulfillmentCenter(fulfillmentCenter)
                                .build());
                    }
                }
            }
        }



        // Notify caller about unexpected results
        if (!fcFound) {
            throw new UnknownFulfillmentCenterException(
                    String.format("Unknown FC: %s!", fulfillmentCenter.getFcCode()));
        }

        if (result.isEmpty()) {
            throw new NoPackagingFitsItemException(
                    String.format("No packaging at %s fits %s!", fulfillmentCenter.getFcCode(), item));
        }

        return result;
    }
}
