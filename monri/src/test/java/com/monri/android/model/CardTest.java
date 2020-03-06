package com.monri.android.model;


import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class CardTest {

    private static Card createExampleCard() {
        return Card.create("4111 1111 1111 1111", 12, 2024, "123");
    }


    @Test
    public void testBuilderDeepCopy() {
        final Card card = createExampleCard();
        final Card card1 = card.toBuilder().country("BIH").build();
        final Card card2 = card1.toBuilder().zip("71000").build();

        //card should have null ip and country
        Assert.assertNull(card.getMetaData().get(AdditionalData.COUNTRY.getFieldName()));
        Assert.assertNull(card.getMetaData().get(AdditionalData.ZIP.getFieldName()));

        //card1 should have only country set
        Assert.assertNull(card1.getMetaData().get(AdditionalData.ZIP.getFieldName()));
        Assert.assertEquals("BIH", card1.getMetaData().get(AdditionalData.COUNTRY.getFieldName()));

        //card2 should have zip and country
        Assert.assertEquals("BIH", card2.getMetaData().get(AdditionalData.COUNTRY.getFieldName()));
        Assert.assertEquals("71000", card2.getMetaData().get(AdditionalData.ZIP.getFieldName()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMetaDataImmutable() {
        final Card exampleCard = createExampleCard();
        exampleCard.getMetaData().put(AdditionalData.ZIP.getFieldName(), "71000");

        Assert.assertNull(exampleCard.getMetaData().get(AdditionalData.ZIP.getFieldName()));
    }

    @Test
    public void validName() {
        final Card exampleCard = createExampleCard();
        final String name = "Adnan";

        final Card modifiedCard = exampleCard.toBuilder().name(name).build();

        Assert.assertNull(exampleCard.getMetaData().get(AdditionalData.FULL_NAME.getFieldName()));
        Assert.assertEquals(name, modifiedCard.getMetaData().get(AdditionalData.FULL_NAME.getFieldName()));

        Assert.assertTrue(modifiedCard.toBuilder().validate());
        Assert.assertTrue(exampleCard.validateCard());
        Assert.assertTrue(modifiedCard.validateCard());

    }

    @Test
    public void validAddress() {
        final Card exampleCard = createExampleCard();
        final String address = "Sabita Užičanina br. 17";

        final Card modifiedCard = exampleCard.toBuilder()
                .address(address)
                .build();

        Assert.assertEquals(address, modifiedCard.getMetaData().get(AdditionalData.ADDRESS.getFieldName()));
        Assert.assertNull(exampleCard.getMetaData().get(AdditionalData.ADDRESS.getFieldName()));

        Assert.assertTrue(modifiedCard.toBuilder().validate());
        Assert.assertTrue(exampleCard.validateCard());
        Assert.assertTrue(modifiedCard.validateCard());
    }

    @Test
    public void validCity() {
        final Card exampleCard = createExampleCard();
        final String city = "Skoplje";

        final Card modifiedCard = exampleCard.toBuilder()
                .city(city)
                .build();

        Assert.assertEquals(city, modifiedCard.getMetaData().get(AdditionalData.CITY.getFieldName()));
        Assert.assertNull(exampleCard.getMetaData().get(AdditionalData.CITY.getFieldName()));

        Assert.assertTrue(modifiedCard.toBuilder().validate());
        Assert.assertTrue(exampleCard.validateCard());
        Assert.assertTrue(modifiedCard.validateCard());

    }

    @Test
    public void validZip() {
        final Card exampleCard = createExampleCard();
        final String zip = "10040";

        final Card modifiedCard = exampleCard.toBuilder()
                .zip(zip)
                .build();

        Assert.assertTrue(modifiedCard.toBuilder().validate());
        Assert.assertEquals(zip, modifiedCard.getMetaData().get(AdditionalData.ZIP.getFieldName()));
        Assert.assertNull(exampleCard.getMetaData().get(AdditionalData.ZIP.getFieldName()));

    }

    @Test
    public void validPhone() {
        final Card exampleCard = createExampleCard();
        final String phoneNumber = "+38763 589-521";

        final Card modifiedCard = exampleCard.toBuilder()
                .phone(phoneNumber)
                .build();

        Assert.assertEquals(phoneNumber, modifiedCard.getMetaData().get(AdditionalData.PHONE.getFieldName()));
        Assert.assertNull(exampleCard.getMetaData().get(AdditionalData.PHONE.getFieldName()));

        Assert.assertTrue(modifiedCard.toBuilder().validate());
        Assert.assertTrue(exampleCard.validateCard());
        Assert.assertTrue(modifiedCard.validateCard());

    }

    @Test
    public void validEmail() {
        final Card exampleCard = createExampleCard();
        final String email = "monri@monri.com";

        final Card modifiedCard = exampleCard.toBuilder()
                .email(email)
                .build();

        Assert.assertEquals(email, modifiedCard.getMetaData().get(AdditionalData.EMAIL.getFieldName()));
        Assert.assertNull(exampleCard.getMetaData().get(AdditionalData.EMAIL.getFieldName()));

        Assert.assertTrue(modifiedCard.toBuilder().validate());
        Assert.assertTrue(exampleCard.validateCard());
        Assert.assertTrue(modifiedCard.validateCard());

    }

    @Test
    public void validMetaData() {
        final Card exampleCard = createExampleCard();
        final Map<String, Object> testMetaData = new HashMap<String, Object>(){{
            put("vip","true");
            put("business","yea");
        }};


        final Card modifiedCard = exampleCard.toBuilder()
                .metaData(testMetaData)
                .build();

        Assert.assertTrue(modifiedCard.toBuilder().validate());
        Assert.assertTrue(exampleCard.validateCard());
        Assert.assertTrue(modifiedCard.validateCard());

    }

    @Test
    public void validAllMetaData() {
        final Card exampleCard = createExampleCard();
        final boolean isMetaDataValid = exampleCard.toBuilder()
                .name("Adnan")
                .address("Zagrebačka br. 19")
                .city("Sarajevo")
                .zip("71000")
                .phone("063722982")
                .email("zbregov@protein.com")
                .validate();

        final boolean isCardValidAfterAddingMetaData = exampleCard.validateCard();

        Assert.assertTrue(isCardValidAfterAddingMetaData);
        Assert.assertTrue(isMetaDataValid);

        Assert.assertTrue(exampleCard.validateCard());
    }

    @Test
    public void validateCard() {
        final Card exampleCard = createExampleCard();
        Assert.assertTrue(exampleCard.validateCard());
    }
}