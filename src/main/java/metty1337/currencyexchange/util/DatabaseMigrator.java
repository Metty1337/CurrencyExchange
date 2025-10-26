package metty1337.currencyexchange.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flywaydb.core.Flyway;

import java.nio.file.Paths;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseMigrator {
    public static void migrate(String URL) {
        Flyway flyway = Flyway.configure()
                .dataSource(URL, null, null)
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();
    }
}
