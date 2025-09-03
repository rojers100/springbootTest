package com.luojie.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.luojie.config.StrategyConfig.StrategyConfigurationInfo;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 支付策略工厂
 * <p>
 * 这是工厂模式与策略模式结合使用的核心类，负责管理和提供各种支付策略实例。
 * 在策略模式中，这个类扮演了"策略选择器"的角色，客户端通过工厂来获取具体策略，
 * 而不需要直接实例化具体策略类，从而实现了客户端与具体策略的解耦。
 * </p>
 * <p>
 * 通过Spring的依赖注入机制和StrategyConfigurationInfo配置，该工厂能够根据配置
 * 决定是否自动收集所有实现了PaymentStrategy接口的Bean，提供了更灵活的策略管理方式。
 * </p>
 */
@Component  // 注册为Spring组件，使其能够被自动发现和注入
public class PaymentStrategyFactory {
    // 日志记录器，用于记录工厂的操作日志
    private static final Logger logger = LoggerFactory.getLogger(PaymentStrategyFactory.class);
    
    // 使用ConcurrentHashMap存储所有策略实现，保证线程安全
    // 键为策略类型（即Bean名称），值为对应的策略实例
    private final Map<String, PaymentStrategy> strategyMap = new ConcurrentHashMap<>();
    
    // 注入策略配置信息，用于控制策略的自动注册行为
    private final StrategyConfigurationInfo configInfo;

    /**
     * 构造函数，注入策略配置信息和所有策略实现
     * <p>
     * 根据StrategyConfigurationInfo中的autoRegisterStrategies配置决定是否自动注册所有策略。
     * 这样可以灵活控制策略的管理方式。
     * </p>
     * @param strategyMap 包含所有策略实现的Map，由Spring自动注入
     * @param configInfo 策略配置信息，控制自动注册行为
     */
    @Autowired
    public PaymentStrategyFactory(Map<String, PaymentStrategy> strategyMap, 
                                 StrategyConfigurationInfo configInfo) {
        this.configInfo = configInfo;
        
        // 根据配置决定是否自动注册所有策略
        if (configInfo != null && configInfo.isAutoRegisterStrategies()) {
            this.strategyMap.putAll(strategyMap);
            // 记录策略注册信息，便于调试和监控
            logger.info("支付策略工厂初始化完成，共自动加载了{}种支付策略", strategyMap.size());
            logger.info("已加载的支付策略类型：{}", strategyMap.keySet());
        } else {
            logger.info("支付策略工厂初始化完成，策略自动注册功能已禁用");
        }
    }
    
    /**
     * 手动注册支付策略
     * <p>
     * 当autoRegisterStrategies设置为false时，可以通过此方法手动注册策略。
     * 这提供了更精细的策略管理控制。
     * </p>
     * @param strategyType 策略类型（唯一标识符）
     * @param strategy 策略实现实例
     * @return 当前工厂实例，支持链式调用
     */
    public PaymentStrategyFactory registerStrategy(String strategyType, PaymentStrategy strategy) {
        if (strategyType == null || strategy == null) {
            logger.error("注册策略失败：策略类型或策略实例不能为空");
            throw new IllegalArgumentException("策略类型或策略实例不能为空");
        }
        
        strategyMap.put(strategyType, strategy);
        logger.info("手动注册支付策略成功：{}", strategyType);
        return this;
    }
    
    /**
     * 移除已注册的支付策略
     * <p>
     * 提供移除策略的功能，便于动态调整可用的支付方式。
     * </p>
     * @param strategyType 要移除的策略类型
     * @return 被移除的策略实例，如果不存在则返回null
     */
    public PaymentStrategy unregisterStrategy(String strategyType) {
        PaymentStrategy removedStrategy = strategyMap.remove(strategyType);
        if (removedStrategy != null) {
            logger.info("移除支付策略成功：{}", strategyType);
        }
        return removedStrategy;
    }

    /**
     * 根据策略类型获取对应的策略实现
     * <p>
     * 这是工厂的核心方法，用于根据客户端提供的策略类型，返回对应的策略实例。
     * 客户端只需要知道策略类型（如"alipay"、"wechatPay"等），
     * 不需要知道具体的策略实现类，从而实现了客户端与具体策略的解耦。
     * </p>
     * @param strategyType 策略类型（即Bean名称）
     * @return 对应的策略实现实例
     * @throws IllegalArgumentException 如果请求的策略类型不存在
     */
    public PaymentStrategy getStrategy(String strategyType) {
        // 日志记录请求的策略类型
        logger.debug("请求获取支付策略：{}", strategyType);
        
        // 从Map中获取对应的策略实现
        PaymentStrategy strategy = strategyMap.get(strategyType);
        
        // 如果策略不存在，抛出异常
        if (strategy == null) {
            logger.error("不支持的支付方式：{}", strategyType);
            throw new IllegalArgumentException("不支持的支付方式：" + strategyType);
        }
        
        // 返回获取到的策略实例
        logger.debug("成功获取支付策略：{}，对应的实现类：{}", 
                   strategyType, strategy.getClass().getSimpleName());
        return strategy;
    }
}