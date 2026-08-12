package sunrise.util;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates unique, human-readable appointment numbers in the form
 * APT1001, APT1002, ... by inspecting the highest existing number
 * already stored, so numbering survives an application restart.
 */
public final class IdGenerator {

    private static final String PREFIX = "APT";
    private static final int START = 1000;

    private final AtomicInteger counter;

    public IdGenerator(List<String> existingAppointmentNumbers) {
        int highest = START;
        for (String number : existingAppointmentNumbers) {
            if (number != null && number.startsWith(PREFIX)) {
                try {
                    int value = Integer.parseInt(number.substring(PREFIX.length()));
                    highest = Math.max(highest, value);
                } catch (NumberFormatException ignored) {
                    // Skip malformed entries rather than fail startup.
                }
            }
        }
        this.counter = new AtomicInteger(highest);
    }

    public synchronized String nextAppointmentNumber() {
        return PREFIX + counter.incrementAndGet();
    }
}
