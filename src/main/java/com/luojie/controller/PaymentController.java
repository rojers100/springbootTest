package com.luojie.controller;

import com.luojie.strategy.PaymentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 支付控制器
 * 演示如何在实际应用中使用策略模式+工厂模式
 * <p>
 * 这是一个REST API控制器，负责处理前端发送的支付请求，
 * 并调用支付上下文执行相应的支付策略。
 * 该控制器是策略模式在实际应用中的客户端，它不直接与具体的支付策略交互，
 * 而是通过支付上下文间接使用策略，体现了依赖倒置原则。
 * </p>
 */
@RestController  // 声明为REST控制器，返回值直接作为HTTP响应体
@RequestMapping("/api/payment")
public class PaymentController {
    // 日志记录器，用于记录控制器的操作日志
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    // 支付上下文，用于执行具体的支付操作
    private final PaymentContext paymentContext;

    /**
     * 构造函数，注入支付上下文
     * <p>
     * 通过Spring的依赖注入机制，自动获取PaymentContext的实例，
     * 以便处理支付请求时调用上下文的支付方法。
     * </p>
     * @param paymentContext 支付上下文实例
     */
    @Autowired
    public PaymentController(PaymentContext paymentContext) {
        this.paymentContext = paymentContext;
        logger.info("支付控制器初始化完成");
    }

    /**
     * 使用指定的支付方式进行支付
     * <p>
     * 这是一个通用的支付接口，可以通过请求参数指定支付类型，
     * 支持多种支付方式，体现了策略模式的核心优势：运行时切换算法。
     * </p>
     * @param paymentMethod 支付方式（alipay/wechatPay/unionPay）
     * @param amount 支付金额
     * @return 支付结果
     */
    @GetMapping("/pay")
    public String pay(@RequestParam String paymentMethod, @RequestParam double amount) {
        logger.info("收到支付请求，类型：{}，金额：{}元", paymentMethod, amount);
        try {
            String result = paymentContext.pay(paymentMethod, amount);
            logger.info("支付请求处理完成，结果：{}", result);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("支付失败：{}", e.getMessage());
            return "支付失败：" + e.getMessage();
        }
    }

    /**
     * 使用支付宝支付
     * <p>
     * 这是一个专门用于支付宝支付的快捷接口，简化了客户端的调用，
     * 内部自动使用"alipay"类型的支付策略。
     * </p>
     * @param amount 支付金额
     * @return 支付结果
     */
    @GetMapping("/alipay")
    public String alipay(@RequestParam double amount) {
        logger.info("收到支付宝支付请求，金额：{}元", amount);
        String result = paymentContext.pay("alipay", amount);
        logger.info("支付宝支付请求处理完成，结果：{}", result);
        return result;
    }

    /**
     * 使用微信支付
     * <p>
     * 这是一个专门用于微信支付的快捷接口，内部自动使用"wechatPay"类型的支付策略。
     * </p>
     * @param amount 支付金额
     * @return 支付结果
     */
    @GetMapping("/wechat")
    public String wechatPay(@RequestParam double amount) {
        logger.info("收到微信支付请求，金额：{}元", amount);
        String result = paymentContext.pay("wechatPay", amount);
        logger.info("微信支付请求处理完成，结果：{}", result);
        return result;
    }

    /**
     * 使用银联支付
     * <p>
     * 这是一个专门用于银联支付的快捷接口，内部自动使用"unionPay"类型的支付策略。
     * </p>
     * @param amount 支付金额
     * @return 支付结果
     */
    @GetMapping("/unionpay")
    public String unionPay(@RequestParam double amount) {
        logger.info("收到银联支付请求，金额：{}元", amount);
        String result = paymentContext.pay("unionPay", amount);
        logger.info("银联支付请求处理完成，结果：{}", result);
        return result;
    }
}