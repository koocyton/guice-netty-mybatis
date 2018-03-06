package com.doopp.gauss.server;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.DruidDriver;
import com.doopp.gauss.rpc.dao.UserDao;
import com.doopp.gauss.server.application.ApplicationModule;
import com.doopp.gauss.server.application.ApplicationProperties;
import com.doopp.gauss.server.netty.NettyServer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.mybatis.guice.MyBatisModule;
import org.mybatis.guice.datasource.druid.DruidDataSourceProvider;
import org.mybatis.guice.datasource.helper.JdbcHelper;

public class KTApplication {

    public static void main(String[] args) throws Exception {
        System.setProperty("applicationPropertiesConfig", args[0]);
        Injector injector = Guice.createInjector(new MyBatisModule() {
            @Override
            protected void initialize() {
                install(JdbcHelper.MySQL);
                bindDataSourceProviderType(DruidDataSourceProvider.class);
                bindTransactionFactoryType(JdbcTransactionFactory.class);
                addMapperClass(UserDao.class);
                Names.bindProperties(binder(), new ApplicationProperties());
            }
        }, new ApplicationModule());
        final NettyServer server = injector.getInstance(NettyServer.class);
        server.run();
    }
}
