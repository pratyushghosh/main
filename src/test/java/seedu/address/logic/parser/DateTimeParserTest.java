
package seedu.address.logic.parser;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import seedu.address.commons.util.Pair;
import seedu.address.logic.parser.exceptions.ParseException;


public class DateTimeParserTest {

    private static final String VALID_TOMORROW = "tomorrow";
    private static final String VALID_THE_DAY_AFTER_TOMORROW = "the day after tomorrow";
    private static final String VALID_IN_SOME_SMALL_DAYS = "in 5 days";
    private static final String VALID_IN_SOME_BIG_DAYS = "in 365 days";
    private static final String VALID_IN_SOME_SMALL_WEEKS = "in 3 weeks";
    private static final String VALID_IN_SOME_BIG_WEEKS = "in 100 weeks";
    private static final String VALID_IN_SOME_SMALL_MONTHS = "in 5 months";
    private static final String VALID_IN_SOME_BIG_MONTHS = "in 99 months";
    private static final String VALID_THIS_WEEKDAY = "this Friday";
    private static final String VALID_NEXT_WEEKDAY = "next Monday";
    private static final String VALID_THIS_WEEK = "this week";
    private static final String VALID_NEXT_WEEK = "next week";
    private static final String VALID_THIS_MONTH = "this month";
    private static final String VALID_NEXT_MONTH = "next month";
    private static final String VALID_SOON = "soon";
    private static final String VALID_RECENTLY = "recently";
    private static final String VALID_IN_A_FEW_DAYS = "in a few days";
    private static final String VALID_SPECIFIED = "13/12/2018";

    private static final String INVALID_FORMAT_SPECIFIED = "13.12.2018";
    private static final String INVALID_DAY_NUMBER_SPECIFIED = "33/12/2018";
    private static final String INVALID_MONTH_NUMBER_SPECIFIED = "13/13/2018";
    private static final String INVALID_NON_INTEGER_NUMBER = "in 0.8 days";
    private static final String INVALID_CAPITALIZED = "neXt Month";
    private static final String INVALID_RANDOM = "elephant";

    private static final String REFINED_VALID_TIME_SLOT = "13/12/2018 9:30 - 10:30";
    private static final String REFINED_INVALID_TOO_EARLY = "13/12/2018 8:30 - 10:30";
    private static final String REFINED_INVALID_T00_LATE = "13/12/2018 19:30 - 23:30";
    private static final String REFINED_INVALID_END_EARLIER_THAN_START = "13/12/2018 10:30 - 9:30";
    private static final String REFINED_INVALID_FORMAT_TIME_SLOT = "13.12.2018 10:30 - 9:30";
    private static final String REFINED_INVALID_FORMAT_WRONG_SEQUENCE_TIME_SLOT = "10:30 - 9:30 13.12.2018";

    private DateTimeParser parser = new DateTimeParser();
    private Calendar actualCurrentTime;
    private Calendar dummyCurrentTime;
    private Calendar expectedCalendarStart;
    private Calendar expectedCalendarEnd;

    @Before
    public void initializeCalendars() {
        actualCurrentTime = Calendar.getInstance();
        actualCurrentTime.setFirstDayOfWeek(Calendar.MONDAY);
        dummyCurrentTime = (Calendar) actualCurrentTime.clone();
        expectedCalendarStart = (Calendar) actualCurrentTime.clone();
        expectedCalendarEnd = (Calendar) actualCurrentTime.clone();
        zeroOutMilliseconds(expectedCalendarStart, expectedCalendarEnd);
        dummyCurrentTime.set(2018, 9, 11, 12, 13, 20);
    }

    @Test
    public void parseDate_validInput_noWrapAround() throws ParseException {
        // testing "tomorrow"
        // no wrap around
        expectedCalendarStart.set(2018, 9, 12, 9, 0, 0);
        expectedCalendarEnd.set(2018, 9, 12, 18, 0, 0);
        assertEquals(new Pair<>(expectedCalendarStart, expectedCalendarEnd), parser.parseDate(VALID_TOMORROW, dummyCurrentTime));

        // testing "the day after tomorrow"
        // no wrap around
        expectedCalendarStart.set(2018, 9, 13, 9, 0, 0);
        expectedCalendarEnd.set(2018, 9, 13, 18, 0, 0);
        assertEquals(new Pair<>(expectedCalendarStart, expectedCalendarEnd), parser.parseDate(VALID_THE_DAY_AFTER_TOMORROW, dummyCurrentTime));
    }

    @Test
    public void parseDate_validInput_wrapAroundWeek() throws ParseException {
        // testing "in 5 days"
        expectedCalendarStart.set(2018, 9, 16, 9, 0, 0);
        expectedCalendarEnd.set(2018, 9, 16, 18, 0, 0);
        assertEquals(new Pair<>(expectedCalendarStart, expectedCalendarEnd), parser.parseDate(VALID_IN_SOME_SMALL_DAYS, dummyCurrentTime));
    }

    @Test
    public void parseDate_validInput_wrapAroundMonth() throws ParseException {
        // testing "in 3 weeks"
        expectedCalendarStart.set(2018, 9, 29, 9, 0, 0);
        expectedCalendarEnd.set(2018, 10, 4, 18, 0, 0);
        assertEquals(new Pair<>(expectedCalendarStart, expectedCalendarEnd), parser.parseDate(VALID_IN_SOME_SMALL_WEEKS, dummyCurrentTime));
        // there is still a bug when tried with 11/11
    }

    @Test
    public void parseDate_validInput_wrapAroundYear() {

    }

    @Test
    public void parseTimeSlot_validInput() {

    }

    @Test
    public void parseTimeSlot_invalidInput_throwsParseException() {

    }

    @Test
    public void parseDate_invalidInput_throwsParseException() {

    }

    private void zeroOutMilliseconds(Calendar... calendars) {
        for (Calendar calendar: calendars) {
            calendar.set(Calendar.MILLISECOND, 0);
        }
    }
}