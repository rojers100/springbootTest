package com.luojie.config;

import com.luojie.config.myInterface.mybatisIntercept.SqlPrintInterceptor;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "com.luojie.dao.mapper1", sqlSessionFactoryRef = "sqlSessionFactory1")
public class DataSource1Config {

    @Value("${datasource1.url}")
    private String url;
    @Value("${datasource1.username}")
    private String username;
    @Value("${datasource1.password}")
    private String password;

    @Bean(name = "dataSource1")
    public DataSource dataSource1() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                // 使用HikariCP数据连接池管理
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "sqlSessionFactory1")
    public SqlSessionFactory sqlSessionFactory1(@Qualifier("dataSource1") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:rojerTestMapper/mapper1/*.xml"));
        // 增加自定义的sql日志打印器
        // 用new Interceptor[]{new SqlPrintInterceptor()}而不是直接new SqlPrintInterceptor()是为了后续方便扩展
        sessionFactoryBean.setPlugins(new Interceptor[]{new SqlPrintInterceptor()});
        return sessionFactoryBean.getObject();
    }
}