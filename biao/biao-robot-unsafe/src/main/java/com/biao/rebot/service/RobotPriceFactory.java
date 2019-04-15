package com.biao.rebot.service;

import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;

/**
 * The interface Robot price factory.
 *
 */
public interface RobotPriceFactory {
    /**
     * 处理的volume工厂实现；
     *
     * @param range 因子范围；
     * @return 结果 ；
     */
    BigDecimal price(Pair<BigDecimal, BigDecimal> range);
}
