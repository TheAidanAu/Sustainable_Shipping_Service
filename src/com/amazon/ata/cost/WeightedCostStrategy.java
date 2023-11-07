package com.amazon.ata.cost;

import com.amazon.ata.types.ShipmentCost;
import com.amazon.ata.types.ShipmentOption;

import java.math.BigDecimal;

/**
 * A strategy to calculate the cost of a ShipmentOption based on its environmental cost.
 */

public class WeightedCostStrategy implements CostStrategy {

    private MonetaryCostStrategy monetaryCostStrategy;
    private CarbonCostStrategy carbonCostStrategy;

    /**
     * Initializes a CarbonCostStrategy.
     * NOTE: look back the Plant UML class diagram
     * NOTE: WeightedCostStrategy is a function of MonetaryCostStrategy and CarbonCostStrategy
     */
    public WeightedCostStrategy(MonetaryCostStrategy monetaryCostStrategy,
                                CarbonCostStrategy carbonCostStrategy) {
        this.monetaryCostStrategy = monetaryCostStrategy;
        this.carbonCostStrategy = carbonCostStrategy;
    }

    @Override
    public ShipmentCost getCost(ShipmentOption shipmentOption) {
        BigDecimal weightedMonetary = monetaryCostStrategy.getCost(shipmentOption).getCost().multiply(BigDecimal.valueOf(0.8));
        BigDecimal weightedCarbon = carbonCostStrategy.getCost(shipmentOption).getCost().multiply(BigDecimal.valueOf(0.2));
        BigDecimal blendedCost = weightedMonetary.add(weightedCarbon);
        return new ShipmentCost(shipmentOption, blendedCost);
    }


}
