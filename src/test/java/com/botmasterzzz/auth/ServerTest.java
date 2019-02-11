package com.botmasterzzz.auth;

import org.junit.Assert;
import org.junit.Test;

public class ServerTest extends TestBase{

    private static final int DEFAULT_PORT = 8060;

    @Test
    public void testMethodOnePortAssertion(){
        LOGGER.info("Проверка порта для Tomcat'а: {}", DEFAULT_PORT);
        Assert.assertEquals("Порт не соответствует системному", DEFAULT_PORT, Server.getPort());
    }

}
