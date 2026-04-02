package servicios;

import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

class EnviarEmailsServiceTest {

    @Test
    void enviarEmail_returnsTrue() {
        EnviarEmailsService s = new EnviarEmailsService(LoggerFactory.getLogger("test"));
        boolean ok = s.enviarEmail(null, "a@a.com");
        assertTrue(ok);
    }
}
