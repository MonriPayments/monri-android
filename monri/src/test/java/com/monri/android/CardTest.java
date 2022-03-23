package com.monri.android;

import androidx.core.util.Pair;

import com.monri.android.model.Card;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Stream;

public class CardTest {

    private static final List<String> DINACARD_TEST_CARDS = Arrays.asList(
            "9891 2413 6144 9435",
            "9891247324971415",
            "6556732342675918",
            "9891 3170 2713 5509",
            "9891 0761 6659 0896",
            "9891 0721 4965 7282"
    );

    private static final List<String> MAESTRO_TEST_CARDS = Arrays.asList(
            "6759 6498 2643 8453",
            "6772 5565 4321 31279",
            "5892830000000000",
            "5890040000000016",
            "6759649826438453"
    );

    private static final List<String> VISA_TEST_CARDS = Arrays.asList(
            "4111 1111 1111 1111",
            "4111 1111 1111 1111 003"
    );

    @SuppressWarnings("ConstantConditions")
    @Test
    public void isValidCVV1() {
        Stream.of(
                Pair.create(new String[]{"4111 1111 1111 1111", "123"}, true),
                Pair.create(new String[]{"4111 1111 1111 1111", "1234"}, false),
                Pair.create(new String[]{"4111 1111 1111 1111", "12"}, false),
                Pair.create(new String[]{"3782 822463 10005", "1234"}, true),
                Pair.create(new String[]{"378282246310005", "123"}, true),
                Pair.create(new String[]{"378282246310005", "12345"}, false),
                Pair.create(new String[]{"378282246310005", "12"}, false),
                Pair.create(new String[]{"55554444 4444 4444", "123"}, true),
                Pair.create(new String[]{"55554444 4444 4444", "1234"}, false),
                Pair.create(new String[]{"55554444 4444 4444", "12"}, false),
                Pair.create(new String[]{MAESTRO_TEST_CARDS.get(0), "12"}, false),
                Pair.create(new String[]{MAESTRO_TEST_CARDS.get(0), "123"}, true),
                Pair.create(new String[]{MAESTRO_TEST_CARDS.get(0), "1234"}, false),
                Pair.create(new String[]{DINACARD_TEST_CARDS.get(0), "123"}, true),
                Pair.create(new String[]{DINACARD_TEST_CARDS.get(0), "1234"}, false)
        ).forEach(p -> {
            Card card = new Card(p.first[0], 12, 2026, p.first[1]);
            final String cvv = card.getCVC();
            final String pan = card.getNumber();
            if (p.second) {
                Assert.assertTrue(String.format("CVV = %s should be valid for pan = %s", cvv, pan), card.validateCVC());
            } else {
                Assert.assertFalse(String.format("CVV = %s should be invalid for pan = %s", cvv, pan), card.validateCVC());
            }
        });
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void isValidPan() {
        Stream<Pair<String, Boolean>> list = Stream.of(
                Pair.create("4111 1111 1111", false),
                Pair.create("4111 1111 1111 1112", false),
                Pair.create("4242 4242 4242 4242", true),
                Pair.create("4242 4242 4242 4243", false),
                Pair.create("5168441223630339", true),
                Pair.create("5555 4444 4444 4441", false),
                Pair.create("3782 822463 10005", true),
                Pair.create("3782 822463 10000", false)
        );

        Stream<Pair<String, Boolean>> maestroPanTest = MAESTRO_TEST_CARDS.stream().map(s -> {
            return Pair.create(s, true);
        });
        Stream<Pair<String, Boolean>> visaPanTest = VISA_TEST_CARDS.stream().map(s -> {
            return Pair.create(s, true);
        });
        Stream<Pair<String, Boolean>> dinaCardPanTest = DINACARD_TEST_CARDS.stream().map(s -> {
            return Pair.create(s, true);
        });

        Stream.of(list, maestroPanTest, visaPanTest, dinaCardPanTest)
                .flatMap(i -> i)
                .forEach(pair -> {
                    Card card = new Card(pair.first, 12, 2026, "123");
                    //noinspection ConstantConditions
                    if (pair.second) {
                        Assert.assertTrue(String.format("pan = %s should be valid", card.getNumber()), card.validateNumber());
                    } else {
                        Assert.assertFalse(String.format("pan = %s should be invalid", card.getNumber()), card.validateNumber());
                    }
                });
    }


    @SuppressWarnings("ConstantConditions")
    @Test
    public void detectBrand() {
        Stream<Pair<String, String>> pairs = Stream.of(
                Pair.create("411111", Card.VISA),
                Pair.create("5168 4412 2363 0339", Card.MASTERCARD),
                Pair.create("3782 822463 10005", Card.AMERICAN_EXPRESS),
                Pair.create("989100", Card.DINACARD),
                Pair.create("989101", Card.DINACARD),
                Pair.create("655688", Card.DINACARD),
                Pair.create("657371", Card.DINACARD)
        );

        Stream<Pair<String, String>> dinaCardBrand = DINACARD_TEST_CARDS.stream().map(s -> {
            return Pair.create(s, Card.DINACARD);
        });

        Stream<Pair<String, String>> maestroCardBrand = MAESTRO_TEST_CARDS.stream().map(s -> {
            return Pair.create(s, Card.MAESTRO);
        });

        Stream<Pair<String, String>> visaCardBrand = VISA_TEST_CARDS.stream().map(s -> {
            return Pair.create(s, Card.VISA);
        });

        Stream.of(pairs, maestroCardBrand, visaCardBrand, dinaCardBrand)
                .flatMap(i -> i)
                .forEach(pair -> {
                    Card card = new Card(pair.first, 12, 2026, "123");
                    Assert.assertEquals(String.format("Bin '%s' brand should be %s", card, pair.second),
                            pair.second,
                            card.getBrand()
                    );
                });
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void isValidExpiry() {
        final Calendar instance = Calendar.getInstance();
        // months are from 0 in java
        final int currentMonth = instance.get(Calendar.MONTH) + 1;
        final int currentYear = instance.get(Calendar.YEAR);


        final List<Pair<Integer[], Boolean>> testValues = new ArrayList<>(Arrays.asList(
                Pair.create(new Integer[]{currentYear, currentMonth}, true),
                Pair.create(new Integer[]{currentYear + 1, currentMonth}, true),
                Pair.create(new Integer[]{currentYear - 1, currentMonth}, false)
        ));

        if (currentMonth > 1) {
            testValues.add(Pair.create(new Integer[]{currentYear, currentMonth - 1}, false));
        }

        if (currentMonth < 12) {
            testValues.add(Pair.create(new Integer[]{currentYear, currentMonth + 1}, true));
        }

        Stream<Pair<String, String>> dinaCardBrand = DINACARD_TEST_CARDS.stream().map(s -> {
            return Pair.create(s, Card.DINACARD);
        });

        Stream<Pair<String, String>> maestroCardBrand = MAESTRO_TEST_CARDS.stream().map(s -> {
            return Pair.create(s, Card.MAESTRO);
        });

        Stream<Pair<String, String>> visaCardBrand = VISA_TEST_CARDS.stream().map(s -> {
            return Pair.create(s, Card.VISA);
        });


        Stream.of(maestroCardBrand, visaCardBrand, dinaCardBrand)
                .flatMap(i -> i)
                .forEach(cardPair -> {
                    testValues.forEach(pair -> {
                        final Integer month = pair.first[1];
                        final Integer year = pair.first[0];

                        Card card = new Card(cardPair.first, month, year, "123");

                        if (pair.second) {
                            Assert.assertTrue(String.format("Expiry date = %s/%s should be valid", year, month), card.validateExpiryDate());
                        } else {
                            Assert.assertFalse(String.format("Expiry date = %s/%s should be invalid", year, month), card.validateExpiryDate());
                        }
                    });

                });

    }


}
