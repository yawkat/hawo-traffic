package at.yawk.hawo.traffic;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import spark.Spark;

/**
 * @author yawkat
 */
@Slf4j
@RequiredArgsConstructor
public class HawoTraffic {
    private final ObjectMapper objectMapper;
    private final EntityManagerFactory emf;

    public static void main(String[] args) throws InterruptedException {
        Spark.port(Integer.parseInt(args[0]));

        HawoTraffic traffic = new HawoTraffic(new ObjectMapper().findAndRegisterModules(),
                                              Persistence.createEntityManagerFactory("hawo_traffic"));
        traffic.listen();
        while (!Thread.interrupted()) {
            try {
                traffic.pollTraffic();
            } catch (Exception e) {
                log.error("Failed to poll traffic", e);
            }
            TimeUnit.MINUTES.sleep(5);
        }
    }

    public void listen() {
        Spark.get("/traffic/history/week", (request, response) -> sql(em -> {
            //noinspection CodeBlock2Expr
            return em.createNamedQuery("findPointsByBillingWeek", TrafficPoint.class)
                    .setParameter("week", BillingWeek.fromDate(LocalDate.now()))
                    .getResultList();
        }), objectMapper::writeValueAsString);
    }

    private void pollTraffic() throws IOException {
        // java ssl is apparently broken
        String uri = "https://useradmin.hawo.stw.uni-erlangen.de/traffic/";
        Process process = Runtime.getRuntime().exec(new String[]{ "curl", uri });

        Document document = Jsoup.parse(process.getInputStream(), Charset.defaultCharset().name(), uri);
        Elements columns = document.select("table > tbody > tr > td");

        TrafficPoint point = new TrafficPoint(
                new BillingWeek(Integer.parseInt(columns.get(0).text()), Integer.parseInt(columns.get(1).text())),
                Instant.now(),
                parseSize(columns.get(2).ownText()),
                parseSize(columns.get(3).ownText())
        );

        sql(em -> {
            em.persist(point);
            return null;
        });
        log.info("Saved traffic info {}", point);
    }

    private <T> T sql(Function<EntityManager, T> function) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            return function.apply(em);
        } finally {
            em.getTransaction().commit();
            em.close();
        }
    }

    static long parseSize(String size) {
        Matcher matcher = Pattern.compile("(\\d+\\.\\d+)[\\s\u00a0]([KMGT]?)B").matcher(size);
        if (!matcher.find()) { throw new IllegalArgumentException("Invalid size: '" + size + '\''); }
        double base = Double.parseDouble(matcher.group(1));
        String prefix = matcher.group(2);

        long multiplier = 1L;
        switch (prefix) {
        case "T":
            multiplier <<= 10L;
        case "G":
            multiplier <<= 10L;
        case "M":
            multiplier <<= 10L;
        case "K":
            multiplier <<= 10L;
        case "":
            break;
        default:
            throw new IllegalArgumentException("Invalid size: " + size);
        }
        return Math.round(base * multiplier);
    }
}
