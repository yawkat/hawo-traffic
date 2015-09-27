package at.yawk.hawo.traffic;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import lombok.*;

/**
 * @author yawkat
 */
@Embeddable
@Access(AccessType.FIELD)

@Data
@Setter(AccessLevel.NONE)
@AllArgsConstructor
@NoArgsConstructor
public class BillingWeek {
    public static final DayOfWeek START = DayOfWeek.SUNDAY;
    private static final TemporalField WEEK_OF_YEAR_FIELD = WeekFields.of(START, 7).weekOfYear();

    private int year;
    private int week;

    public static BillingWeek fromDate(LocalDate date) {
        int weekOfYear = date.get(WEEK_OF_YEAR_FIELD);
        if (weekOfYear == 0) {
            return fromDate(LocalDate.of(date.getYear() - 1, 12, 31));
        } else {
            return new BillingWeek(date.getYear(), weekOfYear);
        }
    }
}
