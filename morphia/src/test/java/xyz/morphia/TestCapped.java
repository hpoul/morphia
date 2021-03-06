package xyz.morphia;

import org.bson.types.ObjectId;
import org.junit.Test;
import xyz.morphia.annotations.CappedAt;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.query.FindOptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestCapped extends TestBase {
    @Test
    public void testCappedEntity() {
        // given
        getMorphia().map(CurrentStatus.class);
        getDs().ensureCaps();

        // when-then
        final CurrentStatus cs = new CurrentStatus("All Good");
        getDs().save(cs);
        assertEquals(1, getDs().getCount(CurrentStatus.class));
        getDs().save(new CurrentStatus("Kinda Bad"));
        assertEquals(1, getDs().getCount(CurrentStatus.class));
        assertTrue(getDs().find(CurrentStatus.class)
                          .find(new FindOptions().limit(1))
                          .next()
                       .message.contains("Bad"));
        getDs().save(new CurrentStatus("Kinda Bad2"));
        assertEquals(1, getDs().getCount(CurrentStatus.class));
        getDs().save(new CurrentStatus("Kinda Bad3"));
        assertEquals(1, getDs().getCount(CurrentStatus.class));
        getDs().save(new CurrentStatus("Kinda Bad4"));
        assertEquals(1, getDs().getCount(CurrentStatus.class));
    }

    @Entity(cap = @CappedAt(count = 1))
    private static class CurrentStatus {
        @Id
        private ObjectId id;
        private String message;

        private CurrentStatus() {
        }

        CurrentStatus(final String msg) {
            message = msg;
        }
    }

}
