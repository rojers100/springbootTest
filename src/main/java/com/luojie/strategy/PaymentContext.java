package com.luojie.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 支付上下文类
 * <p>
 * 这是策略模式中的上下文类，负责持有和使用策略，为客户端提供统一的调用入口。
 * 在策略模式中，上下文类封装了策略的选择和使用细节，客户端不需要直接与策略交互，
 * 只需与上下文类交互即可，从而进一步降低了客户端与具体策略的耦合。
 * </p>
 * <p>
 * 该上下文类提供了两种使用策略的方式：
 * 1. 先设置策略，然后执行操作（分步式）
 * 2. 直接指定策略类型执行操作（一步式）
 * </p>
 */
@Component  // 注册为Spring组件，使其能够被自动发现和注入
public class PaymentContext {
    // 日志记录器，用于记录上下文的操作日志
    private static final Logger logger = LoggerFactory.getLogger(PaymentContext.class);
    
    // 策略工厂，用于获取具体的策略实例
    private final PaymentStrategyFactory paymentStrategyFactory;
    // 当前使用的支付策略实例
    private PaymentStrategy currentStrategy;

    /**
     * 构造函数，注入策略工厂
     * <p>
     * 通过Spring的依赖注入机制，自动获取PaymentStrategyFactory的实例，
     * 以便后续通过工厂获取具体的策略实现。
     * </p>
     * @param paymentStrategyFactory 支付策略工厂实例
     */
    @Autowired
    public PaymentContext(PaymentStrategyFactory paymentStrategyFactory) {
        this.paymentStrategyFactory = paymentStrategyFactory;
        logger.info("支付上下文初始化完成");
    }

    /**
     * 设置支付策略
     * <p>
     * 这是分步式使用策略的第一步，用于设置后续操作要使用的支付策略。
     * 通过策略工厂获取指定类型的策略实例，并存储在当前上下文中。
     * </p>
     * @param strategyType 策略类型（如"alipay"、"wechatPay"等）
     */
    public void setPaymentStrategy(String strategyType) {
        logger.debug("设置支付策略：{}", strategyType);
        this.currentStrategy = paymentStrategyFactory.getStrategy(strategyType);
        logger.info("支付策略设置成功：{}", strategyType);
    }

    /**
     * 执行支付操作
     * <p>
     * 这是分步式使用策略的第二步，使用已设置的支付策略执行支付操作。
     * 在调用此方法前，必须先调用setPaymentStrategy方法设置策略。
     * </p>
     * @param amount 支付金额
     * @return 支付结果描述
     * @throws IllegalStateException 如果未设置支付策略
     */
    public String executePayment(double amount) {
        // 检查是否已设置支付策略
        if (currentStrategy == null) {
            logger.error("执行支付失败：未设置支付策略");
            throw new IllegalStateException("请先设置支付策略");
        }
        
        logger.debug("执行支付操作，金额：{}元，使用策略：{}", 
                    amount, currentStrategy.getClass().getSimpleName());
        
        // 调用当前策略的pay方法执行支付
        String result = currentStrategy.pay(amount);
        
        logger.info("支付操作执行完成，结果：{}", result);
        return result;
    }

    /**
     * 直接执行支付，一步完成策略选择和支付操作
     * <p>
     * 这是一步式使用策略的方法，将策略选择和执行操作合并为一步，
     * 适用于只需单次使用特定策略的场景，无需先设置再执行。
     * </p>
     * @param strategyType 策略类型（如"alipay"、"wechatPay"等）
     * @param amount 支付金额
     * @return 支付结果描述
     */
    public String pay(String strategyType, double amount) {
        logger.debug("直接执行支付，策略类型：{}，金额：{}元", strategyType, amount);
        
        // 通过工厂获取指定的策略实例并直接调用其pay方法
        PaymentStrategy strategy = paymentStrategyFactory.getStrategy(strategyType);
        String result = strategy.pay(amount);
        
        logger.info("直接支付操作执行完成，结果：{}", result);
        return result;
    }
}