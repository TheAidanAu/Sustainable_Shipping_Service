package com.amazon.ata.service;

import com.amazon.ata.cost.CostStrategy;
import com.amazon.ata.cost.MonetaryCostStrategy;
import com.amazon.ata.dao.PackagingDAO;
import com.amazon.ata.datastore.PackagingDatastore;
import com.amazon.ata.exceptions.NoPackagingFitsItemException;
import com.amazon.ata.exceptions.UnknownFulfillmentCenterException;
import com.amazon.ata.types.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.MockitoAnnotations.initMocks;

class ShipmentServiceTest {

    private Item testItem = Item.builder()
            .withHeight(BigDecimal.valueOf(1))
            .withWidth(BigDecimal.valueOf(1))
            .withLength(BigDecimal.valueOf(1))
            .withAsin("abcde")
            .build();

    private FulfillmentCenter testFC = new FulfillmentCenter("ABE2");
    //private FulfillmentCenter nonExistentFC = new FulfillmentCenter("NonExistentFC");
    private Packaging testPackaging = new PolyBag(BigDecimal.valueOf(5000));

    //// FIXME ?? the difference is here
    private ShipmentOption testOption
            = ShipmentOption.builder().withItem(testItem).withFulfillmentCenter(testFC).withPackaging(testPackaging).build();

    private ShipmentCost testCost = new ShipmentCost(testOption, BigDecimal.valueOf(10));

    private List<ShipmentOption> testOptions = Arrays.asList(testOption);

    @InjectMocks
    ShipmentService shipmentService ;

    @Mock
    PackagingDAO packagingDAO;

    @Mock
    MonetaryCostStrategy monetaryCostStrategy;

    @BeforeEach
    void setUp() {
        initMocks(this);
    }

    @Test
    void findBestShipmentOption_existentFCAndItemCanFit_returnsShipmentOption()
            throws UnknownFulfillmentCenterException, NoPackagingFitsItemException {
        // GIVEN

        when(packagingDAO.findShipmentOptions(testItem, testFC))
                .thenReturn(testOptions);
        when(monetaryCostStrategy.getCost(testOption)).thenReturn(testCost);

        // WHEN
        ShipmentOption shipmentOption = shipmentService.findShipmentOption(testItem, testFC);

        // THEN
        assertNotNull(shipmentOption);
    }

    @Test
    void findBestShipmentOption_nonExistentFCAndItemCanFit_returnsShipmentOption() throws UnknownFulfillmentCenterException, NoPackagingFitsItemException {
        // GIVEN & WHEN
        when(monetaryCostStrategy.getCost(testOption)).thenReturn(testCost);
        when(packagingDAO.findShipmentOptions(testItem, testFC))
                .thenThrow(NoPackagingFitsItemException.class);

        // THEN
        assertThrows(NoPackagingFitsItemException.class, () -> {
            shipmentService.findShipmentOption(testItem, testFC);
        }, "If there's no fitting packaging, we throw the NoPackagingFitsItemException. ");
    }

    @Test
    void findBestShipmentOption_existentFCAndItemCannotFit_returnsShipmentOption()
            throws UnknownFulfillmentCenterException, NoPackagingFitsItemException{
        // GIVEN & WHEN

//        when(packagingDAO.findShipmentOptions(any(Item.class), any(FulfillmentCenter.class)))
//                .thenThrow(NoPackagingFitsItemException.class);

        ShipmentOption shipmentOption = shipmentService.findShipmentOption(any(Item.class), any(FulfillmentCenter.class));

        // THEN
        assertNull(shipmentOption);
    }

//    @Test
//    void findBestShipmentOption_nonExistentFCAndItemCannotFit_returnsShipmentOption() {
//        // GIVEN & WHEN
//        ShipmentOption shipmentOption = shipmentService.findShipmentOption(any(Item.class), any(FulfillmentCenter.class));
//
//        // THEN
//        assertNull(shipmentOption);
//    }
}