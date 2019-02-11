package com.botmasterzzz.auth;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationConfigBeansTest2 extends TestBase {

  @Mock
  private BasicDataSource dataSource;

  @Mock
  private Environment environment;

    @Test
    public void testDataSourceBean() {
      Mockito.when(environment.getProperty("app.db.worker.driver")).thenReturn("org.postgresql.Driver");
      Mockito.when(environment.getProperty("app.db.worker.url")).thenReturn("jdbc:postgresql://localhost:5432/postgres");
      Mockito.when(environment.getProperty("app.db.worker.login")).thenReturn("postgres");
      Mockito.when(environment.getProperty("app.db.worker.password")).thenReturn("qwerty");
      String driverClassName = environment.getProperty("app.db.worker.driver");
      String url = environment.getProperty("app.db.worker.url");
      String login = environment.getProperty("app.db.worker.login");
      String password = environment.getProperty("app.db.worker.password");
      Mockito.when(dataSource.getDriverClassName()).thenReturn(driverClassName);
      Mockito.when(dataSource.getUrl()).thenReturn(url);
      Mockito.when(dataSource.getUsername()).thenReturn(login);
      Mockito.when(dataSource.getPassword()).thenReturn(password);
      LOGGER.info("Проверка BasicDataSource бина на соответствующие установленные данные из системы");
      Assert.assertEquals("driverClassName не соответствует системному", driverClassName, dataSource.getDriverClassName());
      Assert.assertEquals("url не соответствует системному", url, dataSource.getUrl());
      Assert.assertEquals("login не соответствует системному", login, dataSource.getUsername());
      Assert.assertEquals("password не соответствует системному", password, dataSource.getPassword());
    }

}
