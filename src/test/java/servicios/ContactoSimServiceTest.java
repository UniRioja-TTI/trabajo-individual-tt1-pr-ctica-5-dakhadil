package servicios;

import modelo.DatosSimulation;
import modelo.Entidad;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContactoSimServiceTest {

    @Test
    void getEntities_returnsNonEmptyList() {
        ContactoSimService s = new ContactoSimService();
        List<Entidad> entities = s.getEntities();
        assertNotNull(entities);
        assertTrue(entities.size() > 0);
    }

    @Test
    void isValidEntityId_returnsTrueForExistingId() {
        ContactoSimService s = new ContactoSimService();
        assertTrue(s.isValidEntityId(1));
        assertTrue(s.isValidEntityId(2));
        assertTrue(s.isValidEntityId(3));
    }

    @Test
    void isValidEntityId_returnsFalseForUnknownId() {
        ContactoSimService s = new ContactoSimService();
        assertFalse(s.isValidEntityId(99));
        assertFalse(s.isValidEntityId(0));
    }
}
