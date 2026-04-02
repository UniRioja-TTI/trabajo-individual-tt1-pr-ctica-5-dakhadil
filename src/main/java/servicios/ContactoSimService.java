package servicios;

import interfaces.InterfazContactoSim;
import modelo.DatosSimulation;
import modelo.DatosSolicitud;
import modelo.Entidad;
import modelo.Punto;
import org.springframework.stereotype.Service;
import utilidades.ServicioSimulacionClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContactoSimService implements InterfazContactoSim {

    private static final String USUARIO = "usuario";

    private final List<Entidad> entities;
    private final ServicioSimulacionClient client;

    public ContactoSimService() {
        this.client = new ServicioSimulacionClient("http://localhost:8080");
        this.entities = new ArrayList<>();

        entities.add(entidad(1, "Alpha", "Entidad Alpha"));
        entities.add(entidad(2, "Beta",  "Entidad Beta"));
        entities.add(entidad(3, "Gamma", "Entidad Gamma"));
    }

    private Entidad entidad(int id, String name, String desc) {
        Entidad e = new Entidad();
        e.setId(id);
        e.setName(name);
        e.setDescripcion(desc);
        return e;
    }

    @Override
    public int solicitarSimulation(DatosSolicitud sol) {
        List<String>  nombres    = new ArrayList<>();
        List<Integer> cantidades = new ArrayList<>();

        for (Entidad e : entities) {
            nombres.add(e.getName());
            cantidades.add(sol.getNums().getOrDefault(e.getId(), 0));
        }

        ServicioSimulacionClient.Solicitud solicitud =
                new ServicioSimulacionClient.Solicitud(cantidades, nombres);
        client.solicitarSimulacion(USUARIO, solicitud);

        List<Integer> tokens = client.getSolicitudesUsuario(USUARIO);
        if (tokens == null || tokens.isEmpty()) return -1;
        return tokens.get(tokens.size() - 1);
    }

    @Override
    public DatosSimulation descargarDatos(int ticket) {
        String raw = client.descargarResultados(USUARIO, ticket);
        if (raw == null || raw.isBlank()) return null;
        return parsearResultado(raw);
    }

    @Override
    public List<Entidad> getEntities() {
        return entities;
    }

    @Override
    public boolean isValidEntityId(int id) {
        return entities.stream().anyMatch(e -> e.getId() == id);
    }

    // ──────────────────────────────────────────────────────
    // Parsing del formato de respuesta:
    //   primera línea → anchoTablero
    //   resto → tiempo,y,x,color
    // ──────────────────────────────────────────────────────
    private DatosSimulation parsearResultado(String raw) {
        String[] lines = raw.strip().split("\\r?\\n");

        DatosSimulation ds = new DatosSimulation();
        ds.setAnchoTablero(Integer.parseInt(lines[0].strip()));

        Map<Integer, List<Punto>> puntos = new HashMap<>();
        int maxT = 0;

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].strip();
            if (line.isEmpty()) continue;
            String[] parts = line.split(",");
            int t        = Integer.parseInt(parts[0]);
            int y        = Integer.parseInt(parts[1]);
            int x        = Integer.parseInt(parts[2]);
            String color = parts[3];

            if (t > maxT) maxT = t;
            puntos.computeIfAbsent(t, k -> new ArrayList<>()).add(buildPunto(x, y, color));
        }

        for (int t = 0; t <= maxT; t++) {
            puntos.putIfAbsent(t, new ArrayList<>());
        }

        ds.setMaxSegundos(maxT + 1);
        ds.setPuntos(puntos);
        return ds;
    }

    private Punto buildPunto(int x, int y, String color) {
        Punto p = new Punto();
        p.setX(x);
        p.setY(y);
        p.setColor(color);
        return p;
    }
}
