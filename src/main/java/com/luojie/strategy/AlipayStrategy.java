package com.luojie.strategy;

import org.springframework.stereotype.Component;

/**
 * 支付宝支付策略实现
 * <p>
 * 这是支付策略接口的具体实现类，专门处理支付宝支付逻辑。
 * 在策略模式中，这个类扮演了"具体策略"角色，实现了策略接口定义的算法。
 * </p>
 * <p>
 * 通过Spring的@Component注解将该类标记为Spring组件，
 * 并指定Bean名称为"alipay"，这样策略工厂可以通过这个名称来获取该策略实例。
 * </p>
 */
@Component("alipay")  // 将该策略注册为Spring Bean，名称为"alipay"
public class AlipayStrategy implements PaymentStrategy {
    /**
     * 实现支付宝支付逻辑
     * <p>
     * 该方法提供了使用支付宝进行支付的具体实现。
     * 在实际应用中，这里会包含与支付宝API交互的代码，
     * 如构建支付请求、调用支付宝SDK、处理支付结果等。
     * </p>
     * <p>
     * 当前示例为了演示策略模式的基本结构，使用了简化的实现，
     * 仅返回一个描述支付过程的字符串。
     * </p>
     * @param amount 支付金额
     * @return 支付结果描述
     */
    @Override
    public String pay(double amount) {
        // 实际应用中，这里会包含真实的支付宝支付逻辑
        // 例如调用支付宝API、处理支付请求等
        return "使用支付宝支付了" + amount + "元";
    }
}