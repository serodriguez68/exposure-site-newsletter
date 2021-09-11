package support

import com.serodriguez.exposuresitenewsletter.base.mail.FakeMailServer
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
abstract class BaseIntegrationTest(
    private val fakeMailServer: FakeMailServer? = null
) {
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

    @BeforeEach
    fun resetFakeMailServer() {
        fakeMailServer?.reset()
    }
}