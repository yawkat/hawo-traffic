package at.yawk.hawo.traffic;

import java.time.Instant;
import javax.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author yawkat
 */
@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "findPointsByBillingWeek",
                query = "select p from TrafficPoint p " +
                        "where p.week = :week")
})

@Data
@Setter(AccessLevel.NONE)
public class TrafficPoint {
    @Id
    @GeneratedValue
    private long id;

    @Embedded
    private BillingWeek week;

    private Instant timestamp;

    private long usedTrafficInternal;
    private long usedTrafficExternal;

    public TrafficPoint(BillingWeek week, Instant timestamp, long usedTrafficInternal, long usedTrafficExternal) {
        this.week = week;
        this.timestamp = timestamp;
        this.usedTrafficInternal = usedTrafficInternal;
        this.usedTrafficExternal = usedTrafficExternal;
    }
}
