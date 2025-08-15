import org.junit.jupiter.api.Test;
import java.util.Comparator;
import static org.junit.jupiter.api.Assertions.*;

public class AviaSoulsTest {
    // Группа тестов с пустым менеджером
    @Test
    public void testSearchWithEmptyManager() {
        AviaSouls manager = new AviaSouls();
        Ticket[] result = manager.search("Москва", "СПб");
        assertEquals(0, result.length);
    }

    @Test
    public void testSearchAndSortByWithEmptyManager() {
        AviaSouls manager = new AviaSouls();
        Ticket[] result = manager.searchAndSortBy("Москва", "СПб", new TicketTimeComparator());
        assertEquals(0, result.length);
    }

    // Группа тестов с маршрутом Москва-СПб
    @Test
    public void testSearchSortedByPrice() {
        AviaSouls manager = new AviaSouls();
        manager.add(new Ticket("Москва", "СПб", 5000, 10, 12));
        manager.add(new Ticket("Москва", "СПб", 3000, 8, 10));
        manager.add(new Ticket("Москва", "СПб", 7000, 12, 14));

        Ticket[] result = manager.search("Москва", "СПб");
        assertEquals(3, result.length);
        assertEquals(3000, result[0].getPrice());
        assertEquals(5000, result[1].getPrice());
        assertEquals(7000, result[2].getPrice());
    }

    @Test
    public void testSearchAndSortBy() {
        AviaSouls manager = new AviaSouls();
        manager.add(new Ticket("Москва", "СПб", 5000, 10, 12)); // 2 часа
        manager.add(new Ticket("Москва", "СПб", 3000, 8, 12));  // 4 часа
        manager.add(new Ticket("Москва", "СПб", 7000, 12, 14)); // 2 часа

        Comparator<Ticket> timeComparator = new TicketTimeComparator();
        Ticket[] result = manager.searchAndSortBy("Москва", "СПб", timeComparator);

        assertEquals(3, result.length);
        assertEquals(2, result[0].getTimeTo() - result[0].getTimeFrom());
        assertEquals(2, result[1].getTimeTo() - result[1].getTimeFrom());
        assertEquals(4, result[2].getTimeTo() - result[2].getTimeFrom());
    }

    // Группа тестов с отсутствием совпадений
    @Test
    public void testSearchWithNoMatches() {
        AviaSouls manager = new AviaSouls();
        manager.add(new Ticket("Москва", "СПб", 5000, 10, 12));
        Ticket[] result = manager.search("СПб", "Москва");
        assertEquals(0, result.length);
    }

    @Test
    public void testSearchWhenNoTickets() {
        AviaSouls manager = new AviaSouls();
        manager.add(new Ticket("Москва", "СПб", 5000, 10, 12));

        Ticket[] result = manager.search("СПб", "Москва");
        assertEquals(0, result.length);
    }

    // Группа тестов с граничными случаями
    @Test
    public void testSearchEdgeCases() {
        AviaSouls manager = new AviaSouls();

        // 1. Пустая коллекция
        Ticket[] emptyResult = manager.search("A", "B");
        assertEquals(0, emptyResult.length);

        // 2. Нет совпадений
        manager.add(new Ticket("A", "B", 100, 10, 12));
        Ticket[] noMatch = manager.search("X", "Y");
        assertEquals(0, noMatch.length);

        // 3. Совпадение только по from
        Ticket[] onlyFromMatch = manager.search("A", "X");
        assertEquals(0, onlyFromMatch.length);

        // 4. Совпадение только по to
        Ticket[] onlyToMatch = manager.search("X", "B");
        assertEquals(0, onlyToMatch.length);
    }

    @Test
    public void testSearchAndSortByEdgeCases() {
        AviaSouls manager = new AviaSouls();
        Comparator<Ticket> comparator = new TicketTimeComparator();

        // 1. Пустая коллекция
        Ticket[] emptyResult = manager.searchAndSortBy("A", "B", comparator);
        assertEquals(0, emptyResult.length);

        // 2. Один билет
        manager.add(new Ticket("A", "B", 100, 10, 12));
        Ticket[] singleResult = manager.searchAndSortBy("A", "B", comparator);
        assertEquals(1, singleResult.length);

        // 3. Несколько билетов с одинаковым временем
        manager.add(new Ticket("A", "B", 200, 10, 12));
        Ticket[] sameTime = manager.searchAndSortBy("A", "B", comparator);
        assertEquals(2, sameTime.length);
    }

    // Группа тестов с null значениями
    @Test
    public void shouldSearchAndSortWithNullFrom() {
        AviaSouls manager = new AviaSouls();
        manager.add(new Ticket(null, "LED", 5000, 10, 12));
        manager.add(new Ticket("VKO", "LED", 4000, 8, 11));

        Ticket[] result = manager.searchAndSortBy(null, "LED", Comparator.comparingInt(Ticket::getPrice));
        assertEquals(1, result.length);
        assertNull(result[0].getFrom());
        assertEquals("LED", result[0].getTo());
    }

    @Test
    public void shouldSearchAndSortWithNullTo() {
        AviaSouls manager = new AviaSouls();
        manager.add(new Ticket("DME", null, 5000, 10, 12));
        manager.add(new Ticket("DME", "KZN", 4000, 8, 11));

        Ticket[] result = manager.searchAndSortBy("DME", null, Comparator.comparingInt(Ticket::getPrice));
        assertEquals(1, result.length);
        assertEquals("DME", result[0].getFrom());
        assertNull(result[0].getTo());
    }

    @Test
    public void testSearchAndSortByWithNullInTicketFields() {
        AviaSouls manager = new AviaSouls();
        manager.add(new Ticket(null, "B", 100, 10, 12));
        manager.add(new Ticket("A", null, 200, 11, 13));
        manager.add(new Ticket(null, null, 300, 12, 14));
        manager.add(new Ticket("A", "B", 400, 13, 15));

        Comparator<Ticket> comparator = Comparator.comparingInt(Ticket::getPrice);

        Ticket[] result1 = manager.searchAndSortBy("A", "B", comparator);
        assertEquals(1, result1.length);
        assertEquals(400, result1[0].getPrice());

        Ticket[] result2 = manager.searchAndSortBy(null, "B", comparator);
        assertEquals(1, result2.length);
        assertEquals(100, result2[0].getPrice());

        Ticket[] result3 = manager.searchAndSortBy("A", null, comparator);
        assertEquals(1, result3.length);
        assertEquals(200, result3[0].getPrice());

        Ticket[] result4 = manager.searchAndSortBy(null, null, comparator);
        assertEquals(1, result4.length);
        assertEquals(300, result4[0].getPrice());
    }

    // Группа тестов с исключениями
    @Test
    public void shouldThrowNPEWhenComparatorIsNull() {
        AviaSouls manager = new AviaSouls();
        manager.add(new Ticket("DME", "LED", 5000, 10, 12));

        assertThrows(NullPointerException.class, () -> {
            manager.searchAndSortBy("DME", "LED", null);
        });
    }

    // Группа тестов с временем полета
    @Test
    public void shouldSearchAndSortByFlightTime() {
        AviaSouls manager = new AviaSouls();
        manager.add(new Ticket("DME", "LED", 5000, 10, 12));
        manager.add(new Ticket("DME", "LED", 4000, 8, 11));
        manager.add(new Ticket("DME", "LED", 4500, 9, 13));

        Ticket[] result = manager.searchAndSortBy("DME", "LED", new TicketTimeComparator());
        assertEquals(3, result.length);
        assertEquals(5000, result[0].getPrice());
        assertEquals(4000, result[1].getPrice());
        assertEquals(4500, result[2].getPrice());
    }

    @Test
    public void testSearchAndSortByWithInvalidTimeRanges() {
        AviaSouls manager = new AviaSouls();
        manager.add(new Ticket("A", "B", 100, 12, 10));
        Comparator<Ticket> comparator = new TicketTimeComparator();
        Ticket[] result = manager.searchAndSortBy("A", "B", comparator);
        assertEquals(1, result.length);
        assertTrue(result[0].getTimeTo() - result[0].getTimeFrom() < 0);
    }

    @Test
    public void testSearchAndSortByWithSameFlightTimes() {
        AviaSouls manager = new AviaSouls();
        manager.add(new Ticket("A", "B", 100, 10, 12));
        manager.add(new Ticket("A", "B", 200, 10, 12));
        Comparator<Ticket> comparator = new TicketTimeComparator();
        Ticket[] result = manager.searchAndSortBy("A", "B", comparator);
        assertEquals(2, result.length);
        assertEquals(0, comparator.compare(result[0], result[1]));
    }

    // Группа тестов сравнения билетов
    @Test
    public void testTicketCompareTo() {
        Ticket ticket1 = new Ticket("A", "B", 5000, 10, 12);
        Ticket ticket2 = new Ticket("A", "B", 10000, 8, 12);

        assertTrue(ticket1.compareTo(ticket2) < 0);
        assertTrue(ticket2.compareTo(ticket1) > 0);
        assertEquals(0, ticket1.compareTo(new Ticket("A", "B", 5000, 9, 11)));
    }

    @Test
    public void testTicketTimeComparator() {
        TicketTimeComparator comparator = new TicketTimeComparator();
        Ticket ticket1 = new Ticket("A", "B", 5000, 10, 12);
        Ticket ticket2 = new Ticket("A", "B", 3000, 8, 12);

        assertTrue(comparator.compare(ticket1, ticket2) < 0);
        assertTrue(comparator.compare(ticket2, ticket1) > 0);
        assertEquals(0, comparator.compare(
                new Ticket("A", "B", 5000, 10, 12),
                new Ticket("A", "B", 3000, 11, 13)
        ));
    }

    @Test
    public void testTicketTimeComparatorConsistency() {
        TicketTimeComparator comparator = new TicketTimeComparator();
        Ticket ticket1 = new Ticket("A", "B", 5000, 10, 12);
        Ticket ticket2 = new Ticket("A", "B", 3000, 8, 12);
        Ticket ticket3 = new Ticket("A", "B", 4000, 9, 12);

        assertEquals(comparator.compare(ticket1, ticket2), comparator.compare(ticket1, ticket2));
        assertTrue(comparator.compare(ticket1, ticket3) < 0);
        assertTrue(comparator.compare(ticket3, ticket2) < 0);
        assertTrue(comparator.compare(ticket1, ticket2) < 0);
    }

    // Группа тестов для findAll
    @Test
    public void testFindAll() {
        AviaSouls manager = new AviaSouls();
        Ticket ticket1 = new Ticket("Москва", "СПб", 5000, 10, 12);
        Ticket ticket2 = new Ticket("Москва", "Сочи", 10000, 8, 12);

        manager.add(ticket1);
        manager.add(ticket2);

        Ticket[] all = manager.findAll();
        assertEquals(2, all.length);
        assertArrayEquals(new Ticket[]{ticket1, ticket2}, all);
    }

    @Test
    public void testArrayGrowthInSearchAndSortBy() {
        AviaSouls manager = new AviaSouls();
        for (int i = 0; i < 15; i++) {
            manager.add(new Ticket("A", "B", 100 + i, 10, 12));
        }

        Comparator<Ticket> comparator = Comparator.comparingInt(Ticket::getPrice);
        Ticket[] result = manager.searchAndSortBy("A", "B", comparator);

        assertEquals(15, result.length);
        assertEquals(100, result[0].getPrice());
        assertEquals(114, result[14].getPrice());
    }

    // Группа тестов equals и hashCode
    @Test
    public void testTicketEquals() {
        Ticket ticket1 = new Ticket("Москва", "СПб", 5000, 10, 12);
        Ticket identicalToTicket1 = new Ticket("Москва", "СПб", 5000, 10, 12);
        Ticket differentSpelling = new Ticket("Москва", "СПб", 5000, 10, 12);

        assertEquals(ticket1, identicalToTicket1);
        assertEquals(ticket1, differentSpelling);

        Ticket differentCity = new Ticket("Питер", "СПб", 5000, 10, 12);
        assertNotEquals(ticket1, differentCity);

        Ticket differentLatinSpelling = new Ticket("Moskva", "СПб", 5000, 10, 12);
        assertNotEquals(ticket1, differentLatinSpelling);
    }

    @Test
    public void testTicketEqualsFullCoverage() {
        Ticket ticket = new Ticket("A", "B", 100, 10, 12);
        assertFalse(ticket.equals(null));
        assertFalse(ticket.equals(new Object()));
        assertNotEquals(ticket, new Ticket("X", "B", 100, 10, 12));
        assertNotEquals(ticket, new Ticket("A", "X", 100, 10, 12));
        assertNotEquals(ticket, new Ticket("A", "B", 200, 10, 12));
        assertNotEquals(ticket, new Ticket("A", "B", 100, 20, 12));
        assertNotEquals(ticket, new Ticket("A", "B", 100, 10, 22));

        Ticket sameTicket = new Ticket("A", "B", 100, 10, 12);
        assertTrue(ticket.equals(sameTicket));
        assertTrue(ticket.equals(ticket));
    }

    @Test
    public void testTicketHashCode() {
        Ticket ticket1 = new Ticket("Москва", "СПб", 5000, 10, 12);
        Ticket ticket2 = new Ticket("Москва", "СПб", 5000, 10, 12);
        Ticket ticket3 = new Ticket("Москва", "Сочи", 6000, 11, 13);

        assertEquals(ticket1.hashCode(), ticket2.hashCode());
        assertNotEquals(ticket1.hashCode(), ticket3.hashCode());
        assertEquals(ticket1.hashCode(), ticket1.hashCode());
    }
}