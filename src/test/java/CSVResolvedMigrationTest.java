import com.mytechden.flyway_csv.impl.resolver.CSVMigrationResolver;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CSVResolvedMigrationTest {

    private final CSVMigrationResolver migrationResolver = new CSVMigrationResolver();

    @Test
    public void shouldProcessMigrationFile() throws Exception {
        final FluentConfiguration cf = Flyway.configure()
                .locations(getClass().getPackage().getName())
                .dataSource("jdbc:h2:mem:.;MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "sa")
                .resolvers(this.migrationResolver);
        final DataSource ds = cf.getDataSource();
        final Connection db = ds.getConnection("sa", "sa");
        final Flyway fw = new Flyway(cf);

        final int count = fw.migrate();

        assertThat(count, equalTo(2));

        final ResultSet countUsers = db.createStatement().executeQuery("SELECT count(id) FROM users");
        countUsers.next();
        assertThat(countUsers.getInt(1), equalTo(1));
    }
}
