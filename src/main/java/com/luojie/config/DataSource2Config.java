package com.luojie.config;

import com.zaxxer.hikari.HikariDataSource;
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
@MapperScan(basePackages = "com.luojie.dao.mapper2", sqlSessionFactoryRef = "sqlSessionFactory2")
public class DataSource2Config {

    @Value("${datasource2.url}")
    private String url;
    @Value("${datasource2.username}")
    private String username;
    @Value("${datasource2.password}")
    private String password;

    @Bean(name = "dataSource2")
    public DataSource dataSource1() {
        return DataSourceBuilder.create()
                .url(url)
                .username(username)
                .password(password)
                // 使用HikariCP数据连接池管理
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "sqlSessionFactory2")
    public SqlSessionFactory sqlSessionFactory1(@Qualifier("dataSource2") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:rojerTestMapper/mapper2/*.xml"));
        return sessionFactoryBean.getObject();
    }

}