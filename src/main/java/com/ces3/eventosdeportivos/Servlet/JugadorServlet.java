package com.ces3.eventosdeportivos.Servlet;

import com.ces3.eventosdeportivos.DAO.EquipoDAO;
import com.ces3.eventosdeportivos.DAO.JugadorDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "jugadores", urlPatterns = "/jugadores")
public class JugadorServlet extends HttpServlet {
    protected static List<JugadorDAO> jugadores = new ArrayList<>();
    private static Integer jugadorCounter = 1;
    private static final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
    private static final List<EquipoDAO> equipos = EquipoServlet.equipos;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String fechaStr = request.getParameter("fechaNacimiento");
        String nacionalidad = request.getParameter("nacionalidad");
        String posicion = request.getParameter("posicion");
        String numeroStr = request.getParameter("numero");
        String equipoIdStr = request.getParameter("equipoId");
        String estadoActivoStr = request.getParameter("estadoActivo");

        if (nombre != null && apellido != null && fechaStr != null && nacionalidad != null
                && posicion != null && numeroStr != null && equipoIdStr != null && estadoActivoStr != null) {
            try {
                int numero = Integer.parseInt(numeroStr);
                int equipoId = Integer.parseInt(equipoIdStr);
                boolean estadoActivo = Boolean.parseBoolean(estadoActivoStr);
                Date fechaNacimiento = formato.parse(fechaStr);

                boolean equipoExiste = equipos.stream().anyMatch(e -> Integer.valueOf(e.getId()).equals(equipoId));
                if (!equipoExiste) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Equipo no encontrado");
                    return;
                }

                JugadorDAO nuevoJugador = new JugadorDAO();
                nuevoJugador.setId(jugadorCounter++);
                nuevoJugador.setNombre(nombre);
                nuevoJugador.setApellido(apellido);
                nuevoJugador.setFechaNacimiento(fechaNacimiento);
                nuevoJugador.setNacionalidad(nacionalidad);
                nuevoJugador.setPosicion(posicion);
                nuevoJugador.setNumero(numero);
                nuevoJugador.setEquipoId(equipoId);
                nuevoJugador.setEstadoActivo(estadoActivo);

                jugadores.add(nuevoJugador);
                response.setStatus(HttpServletResponse.SC_CREATED);
                return;
            } catch (NumberFormatException | ParseException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error en los datos ingresados");
                return;
            }
        }

        BufferedReader reader = request.getReader();
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            json.append(line);
        }

        if (json.length() > 0) {
            try {
                JSONObject jsonObject = new JSONObject(json.toString());

                String jsonNombre = jsonObject.getString("nombre");
                String jsonApellido = jsonObject.getString("apellido");
                String jsonFechaNacimiento = jsonObject.getString("fechaNacimiento");
                String jsonNacionalidad = jsonObject.getString("nacionalidad");
                String jsonPosicion = jsonObject.getString("posicion");
                Integer jsonNumero = jsonObject.getInt("numero");
                Integer jsonEquipoId = jsonObject.getInt("equipoId");
                Boolean jsonEstadoActivo = jsonObject.getBoolean("estadoActivo");

                Date fechaNacimiento = formato.parse(jsonFechaNacimiento);

                Optional<EquipoDAO> equipoOpt = equipos.stream().filter(e -> e.getId() == jsonEquipoId).findFirst();

                if (equipoOpt.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Equipo no encontrado");
                    return;
                }

                EquipoDAO equipo = equipoOpt.get();

                boolean numeroExiste = jugadores.stream()
                        .anyMatch(j -> j.getEquipoId() == jsonEquipoId && j.getNumero() == jsonNumero);

                if (numeroExiste) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ya existe un jugador con este número en el equipo");
                    return;
                }

                JugadorDAO nuevoJugador = new JugadorDAO();
                nuevoJugador.setId(jugadorCounter++);
                nuevoJugador.setNombre(jsonNombre);
                nuevoJugador.setApellido(jsonApellido);
                nuevoJugador.setFechaNacimiento(fechaNacimiento);
                nuevoJugador.setNacionalidad(jsonNacionalidad);
                nuevoJugador.setPosicion(jsonPosicion);
                nuevoJugador.setNumero(jsonNumero);
                nuevoJugador.setEquipoId(jsonEquipoId);
                nuevoJugador.setEstadoActivo(jsonEstadoActivo);

                equipo.getJugadores().add(nuevoJugador.getId());

                jugadores.add(nuevoJugador);
                response.setStatus(HttpServletResponse.SC_CREATED);
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error en el formato JSON");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos no proporcionados");
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONArray jsonArray = new JSONArray();

        for (JugadorDAO jugador : jugadores) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", jugador.getId());
            jsonObject.put("nombre", jugador.getNombre());
            jsonObject.put("apellido", jugador.getApellido());
            jsonObject.put("fechaNacimiento", formato.format(jugador.getFechaNacimiento()));
            jsonObject.put("nacionalidad", jugador.getNacionalidad());
            jsonObject.put("posicion", jugador.getPosicion());
            jsonObject.put("numero", jugador.getNumero());
            jsonObject.put("equipoId", jugador.getEquipoId());
            jsonObject.put("estadoActivo", jugador.getEstadoActivo());

            Optional<EquipoDAO> equipoOpt = equipos.stream().filter(e -> e.getId() == jugador.getEquipoId()).findFirst();
            jsonObject.put("equipoNombre", equipoOpt.map(EquipoDAO::getNombre).orElse("Equipo no encontrado"));

            jsonArray.put(jsonObject);
        }

        response.setContentType("application/json");
        response.getWriter().write(jsonArray.toString());
    }

}
