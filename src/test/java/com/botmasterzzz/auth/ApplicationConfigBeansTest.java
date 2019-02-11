package com.botmasterzzz.auth;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Test;
import org.springframework.core.env.Environment;

//@WebAppConfiguration
//@Ignore
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes={ApplicationConfig.class}, loader=AnnotationConfigWebContextLoader.class)
public class ApplicationConfigBeansTest extends TestBase {

  //@Autowired
  private BasicDataSource dataSource;

  //@Autowired
  private Environment environment;

    @Test
    public void testDataSourceBean() {
//      String driverClassName = environment.getProperty("app.db.worker.driver");
//      String url = environment.getProperty("app.db.worker.url");
//      String login = environment.getProperty("app.db.worker.login");
//      String password = environment.getProperty("app.db.worker.password");
//      LOGGER.info("Проверка BasicDataSource бина на соответствующие установленные данные из системы");
//      Assert.assertEquals("driverClassName не соответствует системному", driverClassName, dataSource.getDriverClassName());
//      Assert.assertEquals("url не соответствует системному", url, dataSource.getUrl());
//      Assert.assertEquals("login не соответствует системному", login, dataSource.getUsername());
//      Assert.assertEquals("password не соответствует системному", password, dataSource.getPassword());
    }

}
