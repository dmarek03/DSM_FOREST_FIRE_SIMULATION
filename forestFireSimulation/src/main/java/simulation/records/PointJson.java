package simulation.records;

import simulation.PointStates;

public record PointJson(
        int x,
        int y,
        int elevation,
        float height,
        PointStates currentState,
        boolean litter,
        boolean floor,
        boolean understory,
        boolean coniferous,
        boolean deciduous
) {

}