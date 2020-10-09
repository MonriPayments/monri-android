package com.monri.android.model;

import org.junit.Assert;
import org.junit.Test;

public class MonriApiOptionsTest {

    @Test
    public void apiUrlProd() {
        MonriApiOptions monriApiOptions = new MonriApiOptions("", false);
        Assert.assertEquals("https://ipg.monri.com", monriApiOptions.url());
    }

    @Test
    public void apiUrlTest() {
        MonriApiOptions monriApiOptions = new MonriApiOptions("", true);
        Assert.assertEquals("https://ipgtest.monri.com", monriApiOptions.url());
    }
}
