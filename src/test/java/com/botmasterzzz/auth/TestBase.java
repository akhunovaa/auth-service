package com.botmasterzzz.auth;

import org.junit.*;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TestBase {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TestBase.class);

    @Rule public TestName name = new TestName();

    private long start;
    private long stop;

    @BeforeClass
    public static void beforeClass(){

    }

    @AfterClass
    public static void afterClass(){
        System.out.println("Закончился набор сценариев");
        printDelimeter();
    }

    @Before
    public void before(){
        start = System.currentTimeMillis();
        System.out.println(String.format("Начался шаг: %s ", name.getMethodName()));
        printDelimeter();
    }

    @After
    public void after(){
        stop = System.currentTimeMillis();
        printDelimeter();
        System.out.println(String.format("Закончился шаг: %s и занял %s секунд", name.getMethodName(), String.format("%.3f", (stop - start) / 10000.00)));
        printDelimeter();
    }

    private static void printDelimeter(){
        System.out.println("--------------------------------------------------------------");
    }

}
