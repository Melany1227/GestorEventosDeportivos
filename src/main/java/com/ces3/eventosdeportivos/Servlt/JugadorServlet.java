package com.ces3.eventosdeportivos.Servlt;

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

@WebServlet(name = "jugadores", urlPatterns = "/jugadores")
public class JugadorServlet extends HttpServlet {
    private static List<JugadorDAO> jugadores = new ArrayList<>();
    private static Integer jugadorCounter = 1;
    private static final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

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
        JSONArray jsonArray = new JSONArray(jugadores);
        response.setContentType("application/json");
        response.getWriter().write(jsonArray.toString());
    }
}
