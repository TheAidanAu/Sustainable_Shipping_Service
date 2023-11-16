package com.amazon.ata.service;

import com.amazon.ata.cost.MonetaryCostStrategy;
import com.amazon.ata.dao.PackagingDAO;
import com.amazon.ata.datastore.PackagingDatastore;
import com.amazon.ata.types.FulfillmentCenter;
import com.amazon.ata.types.Item;
import com.amazon.ata.types.ShipmentOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

class ShipmentServiceTest {

    private Item smallItem = Item.builder()
            .withHeight(BigDecimal.valueOf(1))
            .withWidth(BigDecimal.valueOf(1))
            .withLength(BigDecimal.valueOf(1))
            .withAsin("abcde")
            .build();

    private Item largeItem = Item.builder()
            .withHeight(BigDecimal.valueOf(1000))
            .withWidth(BigDecimal.valueOf(1000))
            .withLength(BigDecimal.valueOf(1000))
            .withAsin("12345")
            .build();

    private FulfillmentCenter existentFC = new FulfillmentCenter("ABE2");
    private FulfillmentCenter nonExistentFC = new FulfillmentCenter("NonExistentFC");

    // FIXME uncomment below again if needed
//    private ShipmentService shipmentService = new ShipmentService(new PackagingDAO(new PackagingDatastore()),
//            new MonetaryCostStrategy());
    @InjectMocks
        ShipmentService shipmentService;

    @Mock
    PackagingDAO packagingDAO;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void findBestShipmentOption_existentFCAndItemCanFit_returnsShipmentOption() {
        // GIVEN & WHEN
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(smallItem, existentFC);

        // THEN
        assertNotNull(shipmentOption);
    }

    @Test
    void findBestShipmentOption_existentFCAndItemCannotFit_returnsShipmentOption() {
        // GIVEN & WHEN
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(largeItem, existentFC);

        // THEN
        assertNull(shipmentOption);
    }

    @Test
    void findBestShipmentOption_nonExistentFCAndItemCanFit_returnsShipmentOption() {
        // GIVEN & WHEN
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(smallItem, nonExistentFC);

        // THEN
        assertNull(shipmentOption);
    }

    @Test
    void findBestShipmentOption_nonExistentFCAndItemCannotFit_returnsShipmentOption() {
        // GIVEN & WHEN
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(largeItem, nonExistentFC);

        // THEN
        assertNull(shipmentOption);
    }
}