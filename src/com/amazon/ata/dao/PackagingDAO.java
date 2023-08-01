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

    /**
     * Instantiates a PackagingDAO object.
     * @param datastore Where to pull the data from for fulfillment center/packaging available mappings.
     */
    public PackagingDAO(PackagingDatastore datastore) {
        List<FcPackagingOption> packagingOptions = new ArrayList<>(datastore.getFcPackagingOptions());
        //Logic: since it's a set, even if you add a dup, no dup will be kept
        //Iterate through all the packing options, see if an option contains an existing key
        // or else add a new Set of packaging
        for (FcPackagingOption packingOption: packagingOptions) {
            if (fcPackagingOptions.containsKey(packingOption.getFulfillmentCenter())) {
                FulfillmentCenter existingKey = packingOption.getFulfillmentCenter();
                Packaging packaging = packingOption.getPackaging();
                //You get the HashSet of Packaging by looking it up with a key
                //You attempt to add it, if it's a dup, it won't add
                fcPackagingOptions.get(existingKey).add(packaging);
            } else {
                FulfillmentCenter newKey = packingOption.getFulfillmentCenter();
                Set<Packaging> newPackaingSet = new HashSet<>();
                newPackaingSet.add(packingOption.getPackaging());
                fcPackagingOptions.put(newKey, newPackaingSet);
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
