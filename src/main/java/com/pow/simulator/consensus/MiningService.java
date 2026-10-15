package com.pow.simulator.consensus;

import com.pow.simulator.core.Block;

/**
 * Interface defining Proof-of-Work mining services.
 */
public interface MiningService {

    /**
     * Mines a template block using default thread configuration.
     *
     * @param templateBlock unmined block template
     * @return MiningResult containing winning block and metrics
     */
    MiningResult mine(Block templateBlock);

    /**
     * Mines a template block with a specified worker concurrency level.
     *
     * @param templateBlock unmined block template
     * @param threadCount   number of concurrent worker threads
     * @return MiningResult containing winning block and metrics
     */
    MiningResult mine(Block templateBlock, int threadCount);
}
