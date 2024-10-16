package net.studioblueplanet.superset.gameplay;

import junit.framework.TestCase;

public class DeckTest extends TestCase
{

    public void testResetDeck()
    {
        Deck instance=new Deck();
        assertEquals(81, instance.cardsInDeck());
        Card card=instance.getCardByIndex(0);
        // First card in deck
        assertEquals(0, card.stackIndex);
        assertEquals(Card.CardColor.CARDCOLOR_BLUE, card.color);
        assertEquals(Card.CardFilling.CARDFILLING_SOLID, card.filling);
        assertEquals(Card.CardNumber.CARDNUMBER_ONE, card.number);
        assertEquals(Card.CardSymbol.CARDSYMBOL_ELLIPSE, card.symbol);
        // Last card in deck
        card=instance.getCardByIndex(80);
        assertEquals(80, card.stackIndex);
        assertEquals(Card.CardColor.CARDCOLOR_PURPLE, card.color);
        assertEquals(Card.CardFilling.CARDFILLING_HALF, card.filling);
        assertEquals(Card.CardNumber.CARDNUMBER_THREE, card.number);
        assertEquals(Card.CardSymbol.CARDSYMBOL_WAVE, card.symbol);
    }

    public void testShuffleDeck()
    {
        Deck instance=new Deck();
        instance.shuffleDeck();

        // We are going to check that at least one of four cards has changed position
        // after shuffling. We take the first two and the last two cards of the shuffled deck
        Card card0=instance.nextCard();
        Card card1=instance.nextCard();
        for (int i=2; i<79; i++)
        {
            instance.nextCard();
        }
        Card card3=instance.nextCard();
        Card card4=instance.nextCard();
        assertTrue(card0.stackIndex!=0 || card1.stackIndex!=1 || card3.stackIndex!=79 || card4.stackIndex!=80);
        // Note: chance of 1/81*1/80*1/79*1/78 = 1/39929760 that the test fails
    }

    public void testResetCards()
    {
        Deck instance=new Deck();
        Card card;
        card=instance.nextCard();
        assertEquals(0, card.stackIndex);
        card=instance.nextCard();
        assertEquals(1, card.stackIndex);
        instance.resetCards();
        card=instance.nextCard();
        assertEquals(0, card.stackIndex);
    }

    public void testNextCard()
    {
        Deck instance=new Deck();
        Card card;
        card=instance.nextCard();
        assertEquals(0, card.stackIndex);
        card=instance.nextCard();
        assertEquals(1, card.stackIndex);
        card=instance.nextCard();
        assertEquals(2, card.stackIndex);
        for (int i=3; i<81; i++)
        {
            card=instance.nextCard();
        }
        assertEquals(80, card.stackIndex);
        card=instance.nextCard();
        assertNull(card);
    }

    public void testCardsInDeck()
    {
        Deck instance=new Deck();
        Card card;
        assertEquals(81, instance.cardsInDeck());
        card=instance.nextCard();
        assertEquals(80, instance.cardsInDeck());
        card=instance.nextCard();
        assertEquals(79, instance.cardsInDeck());
        for (int i=2; i<81; i++)
        {
            card=instance.nextCard();
        }
        assertEquals(0, instance.cardsInDeck());
        card=instance.nextCard();
        assertEquals(0, instance.cardsInDeck());
    }

    public void testGetCardByIndex()
    {
        Deck instance=new Deck();
        assertEquals( 0, instance.getCardByIndex(0).stackIndex);
        assertEquals(80, instance.getCardByIndex(80).stackIndex);
        assertNull(instance.getCardByIndex(-1));
        assertNull(instance.getCardByIndex(81));
    }
}