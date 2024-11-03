package com.luojie.config.myInterface.mybatisIntercept;

import com.luojie.common.Conditions;
import com.luojie.util.TxtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;

/**
 * 自定义打印日志功能的拦截器
 */
@Intercepts({
        // 拦截 Executor 接口的 query 方法，包含不同的参数组合
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "queryCursor", args = {MappedStatement.class, Object.class, RowBounds.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})
})
@Slf4j
public class SqlPrintInterceptor implements Interceptor {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 记录开始时间
        long startTime = System.currentTimeMillis();
        Object proceed = null;

        // 执行原始方法
        try {
            proceed = invocation.proceed();
        } catch (Throwable t) {
            log.error("Error during SQL execution", t);
            throw t; // 重新抛出异常
        }

        // 记录结束时间
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime; // 计算执行时间

        // 转换执行时间为 "XXs.XXms" 格式
        String formattedExecutionTime = formatExecutionTime(executionTime);

        // 生成打印的 SQL 语句
        String printSql = generateSql(invocation);
        // 输出 SQL 和执行时间
        System.out.println(Conditions.RED + "SQL: " + printSql);
        System.out.println("Execution time: " + formattedExecutionTime);
        System.out.print(Conditions.RESET);
        log.info("SQL: " + printSql);
        log.info("Execution time: " + formattedExecutionTime);

        // 记录慢sql(这里我为了方便观察，所以设置界限为0，各位可以根据实际情况设置)
        if ((executionTime / 1000) >= 0) {
            writeSlowSqlToLocation(printSql, formattedExecutionTime);
        }

        return proceed; // 返回原始方法的结果
    }

    // 记录慢sql
    private void writeSlowSqlToLocation(String sql, String executeTime) {
        String formattedDate = dateFormat.format(new Date());
        String logs = formattedDate + "  SQL: " + sql + "  执行耗时: " + executeTime;
        TxtUtil.writeLog(logs);
    }


    // 新增格式化执行时间的方法
    private String formatExecutionTime(long executionTime) {
        long seconds = executionTime / 1000; // 获取秒数
        long milliseconds = executionTime % 1000; // 获取剩余的毫秒数
        return String.format("%ds.%03dms", seconds, milliseconds); // 格式化为 "XXs.XXXms"
    }

    private String generateSql(Invocation invocation) {
        // 获取 MappedStatement 对象
        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
        Object parameter = null;

        // 获取参数对象
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }

        // 获取 MyBatis 配置
        Configuration configuration = mappedStatement.getConfiguration();
        // 获取 BoundSql 对象
        BoundSql boundSql = mappedStatement.getBoundSql(parameter);

        // 获取参数对象
        Object parameterObject = boundSql.getParameterObject();
        // 获取参数映射列表
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        // 获取执行的 SQL 语句
        String sql = boundSql.getSql();

        // 替换 SQL 中多个空格为一个空格
        sql = sql.replaceAll("[\\s]+", " ");

        // 如果参数对象和参数映射不为空
        if (!ObjectUtils.isEmpty(parameterObject) && !ObjectUtils.isEmpty(parameterMappings)) {
            // 如果只有一个参数，直接替换
            if (parameterObject instanceof String && parameterMappings.size() == 1) {
                return sql.replaceFirst("\\?", String.valueOf(parameterObject)); // 处理缺少值的情况
            }
            // 遍历每个参数映射
            for (ParameterMapping parameterMapping : parameterMappings) {
                String propertyName = parameterMapping.getProperty(); // 获取属性名
                MetaObject metaObject = configuration.newMetaObject(parameterObject); // 创建 MetaObject

                Object obj = null; // 初始化参数对象
                // 如果参数对象有对应的 getter 方法
                if (metaObject.hasGetter(propertyName)) {
                    obj = metaObject.getValue(propertyName); // 获取参数值
                } else if (boundSql.hasAdditionalParameter(propertyName)) {
                    obj = boundSql.getAdditionalParameter(propertyName); // 获取附加参数
                }

                // 替换 SQL 中的占位符
                if (obj != null) {
                    sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                } else {
                    sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(propertyName)); // 处理缺少值的情况
                }
            }
        }
        return sql; // 返回生成的 SQL 语句
    }

    private String getParameterValue(Object parameterObject) {
        // 如果参数对象为空，返回 "null"
        if (parameterObject == null) {
            return "null";
        }
        // 返回参数对象的字符串表示
        return parameterObject.toString();
    }

    @Override
    public Object plugin(Object target) {
        // 生成插件对象
        return Interceptor.super.plugin(target);
    }

    @Override
    public void setProperties(Properties properties) {
        // 设置属性
        Interceptor.super.setProperties(properties);
    }
}
