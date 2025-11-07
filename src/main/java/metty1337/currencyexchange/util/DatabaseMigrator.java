package metty1337.currencyexchange.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import metty1337.currencyexchange.exceptions.DatabaseException;
import metty1337.currencyexchange.exceptions.ExceptionMessages;
import org.flywaydb.core.Flyway;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DatabaseMigrator {
    public static void migrate(String url) {
        Flyway flyway = Flyway.configure()
                .dataSource(url, null, null)
                .locations("classpath:db/migration")
                .load();
        try {
            flyway.migrate();
        } catch (RuntimeException e) {
            throw new DatabaseException(ExceptionMessages.DATABASE_EXCEPTION.getMessage(), e);
        }
    }
}
