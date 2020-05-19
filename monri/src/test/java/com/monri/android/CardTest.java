package com.monri.android;

import androidx.core.util.Pair;

import com.monri.android.model.Card;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CardTest {

    private static Card getVisaExampleCard() {
        return new Card("4111 1111 1111 1111", 12, 2024, "123");
    }

    private static Card[] getVisaExampleCards() {
        final List<String> VISA_TEST_CARDS = Arrays.asList(
                "4111 1111 1111 1111",
                "4111 1111 1111 1111 003"
        );

        return VISA_TEST_CARDS.stream().map(cardNumber -> {
            return new Card(cardNumber, 12, 2023, "113");
        }).toArray(Card[]::new);
    }

    private static Card getMastercardExampleCard() {
        return new Card("5168 4412 2363 0339", 12, 2024, "123");
    }

    private static Card[] getMastercardExampleCards(){
        final List<String> MASTERCARD_TEST_CARDS = Arrays.asList(
                "55554444 4444 4444",
                "5168 4412 2363 0339"
        );

        return MASTERCARD_TEST_CARDS.stream().map(cardNumber -> {
            return new Card(cardNumber, 12, 2030, "013");
        }).toArray(Card[]::new);

    }

    private static Card getMaestroExampleCard() {
        return new Card("6772 5565 4321 31279", 12, 2024, "123");
    }

    private static Card[] getMaestroExampleCards() {
        final List<String> MAESTRO_TEST_CARDS = Arrays.asList(
                "6759 6498 2643 8453",
                "6772 5565 4321 31279",
                "5892830000000000",
                "5890040000000016",
                "6759649826438453"
        );

        return MAESTRO_TEST_CARDS.stream().map(cardNumber -> {
            return new Card(cardNumber, 12, 2024, "123");
        }).toArray(Card[]::new);

    }

    @Test
    public void detectBrand() {

        final Card mastercardExampleCard = getMastercardExampleCard();
        final Card maestroExampleCard = getMaestroExampleCard();
        final Card visaExampleCard = getVisaExampleCard();

        Assert.assertEquals(Card.MAESTRO, maestroExampleCard.getBrand());
        Assert.assertEquals(Card.VISA, visaExampleCard.getBrand());
        Assert.assertEquals(Card.MASTERCARD, mastercardExampleCard.getBrand());

        Assert.assertTrue(maestroExampleCard.validateCard());

    }

    @Test
    public void areTheCardsValid() {

        Arrays.stream(getMastercardExampleCards()).forEach(Card::validateCard);
        Arrays.stream(getVisaExampleCards()).forEach(Card::validateCard);
        Arrays.stream(getMaestroExampleCards()).forEach(Card::validateCard);

    }


}
