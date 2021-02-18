package com.monri.android;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class MonriConfigTest {

    @Test
    public void testUrl() {
        Assert.assertEquals("https://ipgtest.monri.com", MonriConfig.TEST_ENV_HOST);
    }

    @Test
    public void prodUrl() {
        Assert.assertEquals("https://ipg.monri.com", MonriConfig.PROD_ENV_HOST);
    }
}