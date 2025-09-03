package com.luojie.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 策略模式配置类
 * <p>
 * 该类是整个策略模式实现的核心配置类，主要负责：
 * 1. 启用策略包的组件扫描
 * 2. 配置策略监控机制
 * 3. 提供策略配置信息管理
 * <p>
 * 通过Spring的@Configuration注解标记为配置类，确保在Spring Boot应用启动时被自动加载
 * </p>
 */
@Configuration
// 主组件扫描由Spring Boot自动处理，这里使用StrategyConfigurationInfo中的packageToScan进行更灵活的配置
// @ComponentScan注解本身不支持动态路径，但我们在后续的BeanPostProcessor中使用packageToScan参数
public class StrategyConfig {
    // 日志记录器，使用SLF4J框架记录配置和监控信息
    private static final Logger logger = LoggerFactory.getLogger(StrategyConfig.class);

    /**
     * 构造函数，用于记录配置类初始化信息
     * <p>
     * 在配置类实例化时记录初始化日志，便于跟踪系统启动过程中的策略模式初始化状态
     * </p>
     */
    public StrategyConfig() {
        logger.info("策略模式配置类[StrategyConfig]初始化完成");
        logger.info("开始扫描策略包: com.luojie.strategy");
    }

    /**
     * 创建策略模式监控的BeanPostProcessor
     * <p>
     * 通过Spring的BeanPostProcessor机制，在所有Bean初始化完成后进行拦截处理，
     * 专门用于监控和记录策略实现类的加载情况，帮助开发者确认所有策略都已正确注册到Spring容器
     * </p>
     * @param strategyConfigurationInfo 策略配置信息，用于控制监控行为
     * @return 自定义的BeanPostProcessor实例，用于策略实现类的监控
     */
    @Bean
    public BeanPostProcessor strategyBeanPostProcessor(StrategyConfigurationInfo strategyConfigurationInfo) {
        return new BeanPostProcessor() {
            /**
             * 在Bean初始化完成后进行处理
             * <p>
             * 此方法会在每个Spring Bean初始化完成后被调用，我们在这里专门处理策略包中的Bean
             * </p>
             * @param bean 初始化完成的Bean实例
             * @param beanName Bean在Spring容器中的名称
             * @return 处理后的Bean实例（此处直接返回原实例，不做修改）
             * @throws BeansException Bean处理过程中的异常
             */
            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                // 根据配置信息决定是否启用监控
                if (strategyConfigurationInfo.isMonitorEnabled()) {
                    // 检查是否为支付策略实现类（排除配置类、工厂类和上下文类本身）
            // 使用配置中的packageToScan参数进行判断，实现动态配置
            if (bean.getClass().getPackage() != null && 
                strategyConfigurationInfo.getPackageToScan() != null &&
                bean.getClass().getPackage().getName().startsWith(strategyConfigurationInfo.getPackageToScan()) && 
                !beanName.equals("strategyConfig") && 
                !beanName.equals("paymentStrategyFactory") && 
                !beanName.equals("paymentContext")) {
                        // 记录策略实现类的初始化和注册信息
                        logger.info("策略实现类[{}] (Bean名称: {}) 初始化完成并注册到Spring容器", 
                                    bean.getClass().getSimpleName(), beanName);
                    }
                }
                return bean;  // 返回原Bean，不做任何修改
            }
        };
    }

    /**
     * 获取策略模式配置信息的方法
     * <p>
     * 创建并配置一个StrategyConfigurationInfo实例，用于封装当前策略模式的配置状态
     * 将其注册为Spring Bean，便于在系统其他地方获取和使用这些配置信息
     * </p>
     * <p>
     * 该配置实例包含三个核心参数，都有实际应用场景：
     * 1. packageToScan：用于策略实现类的包路径识别，在BeanPostProcessor中使用
     * 2. monitorEnabled：控制是否启用策略监控功能
     * 3. autoRegisterStrategies：控制策略工厂是否自动注册所有策略
     * </p>
     * @return 配置完成的StrategyConfigurationInfo实例
     */
    @Bean
    public StrategyConfigurationInfo strategyConfigurationInfo() {
        StrategyConfigurationInfo info = new StrategyConfigurationInfo();
        // 设置要扫描的策略包路径 - 用于BeanPostProcessor中的策略类识别
        info.setPackageToScan("com.luojie.strategy");
        // 启用策略监控功能 - 控制是否记录策略实现类的加载日志
        info.setMonitorEnabled(true);
        // 启用策略自动注册功能 - 控制策略工厂是否自动注册所有策略实现
        info.setAutoRegisterStrategies(true);
        // 记录配置信息日志
        logger.info("策略模式配置信息: {}", info);
        return info;
    }

    /**
     * 内部类，用于封装策略模式的配置信息
     * <p>
     * 该类作为StrategyConfig的内部类，专门用于封装和管理策略模式的配置参数，
     * 提供了对策略扫描、监控和自动注册等功能的配置管理
     * </p>
     */
    public static class StrategyConfigurationInfo {
        // 策略实现类所在的包路径，用于组件扫描
        private String packageToScan;
        // 是否启用策略监控功能
        private boolean monitorEnabled;
        // 是否自动注册策略实现到工厂类
        private boolean autoRegisterStrategies;

        /**
         * 获取策略实现类所在的包路径
         * @return 包路径字符串
         */
        public String getPackageToScan() {
            return packageToScan;
        }

        /**
         * 设置策略实现类所在的包路径
         * @param packageToScan 包路径字符串
         */
        public void setPackageToScan(String packageToScan) {
            this.packageToScan = packageToScan;
        }

        /**
         * 获取是否启用策略监控功能
         * @return true表示启用监控，false表示禁用监控
         */
        public boolean isMonitorEnabled() {
            return monitorEnabled;
        }

        /**
         * 设置是否启用策略监控功能
         * @param monitorEnabled true表示启用监控，false表示禁用监控
         */
        public void setMonitorEnabled(boolean monitorEnabled) {
            this.monitorEnabled = monitorEnabled;
        }

        /**
         * 获取是否自动注册策略实现
         * @return true表示自动注册，false表示手动注册
         */
        public boolean isAutoRegisterStrategies() {
            return autoRegisterStrategies;
        }

        /**
         * 设置是否自动注册策略实现
         * @param autoRegisterStrategies true表示自动注册，false表示手动注册
         */
        public void setAutoRegisterStrategies(boolean autoRegisterStrategies) {
            this.autoRegisterStrategies = autoRegisterStrategies;
        }

        /**
         * 返回配置信息的字符串表示
         * @return 包含所有配置项的字符串
         */
        @Override
        public String toString() {
            return "StrategyConfigurationInfo{" +
                    "packageToScan='" + packageToScan + '\'' +
                    ", monitorEnabled=" + monitorEnabled +
                    ", autoRegisterStrategies=" + autoRegisterStrategies +
                    '}';
        }
    }
}