package support

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
abstract class BaseIntegrationTest {
    @Autowired
    lateinit var flyway: Flyway

    @BeforeEach
    fun setupDB() {
        flyway.migrate()
    }

    /** TODO: this cleanup strategy is too aggressive as it drops all tables.
     * Take a look at DatabaseCleaner.kt
     * */
    @AfterEach
    fun teardownDB() {
        flyway.clean()
    }
}