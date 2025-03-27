package com.ces3.eventosdeportivos.Servlet;

import com.ces3.eventosdeportivos.DAO.JugadorDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.ces3.eventosdeportivos.DAO.EquipoDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "Servlt", urlPatterns = "/equipos")
public class EquipoServlet extends HttpServlet {
    public String message;
    protected static List<EquipoDAO> equipos = new ArrayList<>();
    private static int equipoCounter = 1;
    private final List<JugadorDAO> jugadoresRegistrados = JugadorServlet.jugadores;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONArray equiposArray = new JSONArray();

        for (EquipoDAO equipo : equipos) {
            JSONObject equipoJson = new JSONObject();
            equipoJson.put("id", equipo.getId());
            equipoJson.put("nombre", equipo.getNombre());
            equipoJson.put("deporte", equipo.getDeporte());
            equipoJson.put("ciudad", equipo.getCiudad());
            equipoJson.put("fechaFundacion", new SimpleDateFormat("yyyy-MM-dd").format(equipo.getFechaFundacion()));
            equipoJson.put("logo", equipo.getLogo());

            JSONArray jugadoresArray = new JSONArray();
            for (int jugadorId : equipo.getJugadores()) {
                JugadorDAO jugador = buscarJugadorPorId(jugadorId);
                if (jugador != null) {
                    JSONObject jugadorJson = new JSONObject();
                    jugadorJson.put("id", jugador.getId());
                    jugadorJson.put("nombre", jugador.getNombre());
                    jugadorJson.put("apellido", jugador.getApellido());
                    jugadorJson.put("posicion", jugador.getPosicion());
                    jugadoresArray.put(jugadorJson);
                }
            }

            equipoJson.put("jugadores", jugadoresArray);
            equiposArray.put(equipoJson);
        }

        response.setContentType("application/json");
        response.getWriter().write(equiposArray.toString());
    }

    private JugadorDAO buscarJugadorPorId(int id) {
        for (JugadorDAO jugador : jugadoresRegistrados) {
            if (jugador.getId() == id) {
                return jugador;
            }
        }
        return null;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String deporte = request.getParameter("deporte");
        String ciudad = request.getParameter("ciudad");
        String fechaStr = request.getParameter("fechaFundacion");
        String logo = request.getParameter("logo");
        String jugadores = request.getParameter("jugadores");

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

        if (nombre != null && deporte != null && ciudad != null && fechaStr != null && logo != null) {

            if(jugadores != null){
                List<Integer> jugadoresIds = new ArrayList<>();
                for (String idStr : jugadores.split(",")) {
                    try {
                        jugadoresIds.add(Integer.parseInt(idStr.trim()));
                    } catch (NumberFormatException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de ID de jugador inválido");
                        return;
                    }
                }

                List<Integer> jugadoresNoExistentes = new ArrayList<>();
                for (Integer id : jugadoresIds) {
                    if (!jugadoresRegistrados.contains(id)) {
                        jugadoresNoExistentes.add(id);
                    }
                }

                if (!jugadoresNoExistentes.isEmpty()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Los siguientes jugadores no existen: " + jugadoresNoExistentes);
                    return;
                }
            }

            EquipoDAO nuevoEquipo = new EquipoDAO();
            nuevoEquipo.setId(equipoCounter++);
            nuevoEquipo.setNombre(nombre);
            nuevoEquipo.setDeporte(deporte);
            nuevoEquipo.setCiudad(ciudad);

            try {
                Date fechaFundacion = formato.parse(fechaStr);
                nuevoEquipo.setFechaFundacion(fechaFundacion);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            nuevoEquipo.setLogo(logo);

            for (EquipoDAO equipo : equipos) {
                if (equipo.getNombre().equalsIgnoreCase(nombre) && equipo.getDeporte().equalsIgnoreCase(deporte)) {
                    response.sendError(HttpServletResponse.SC_CONFLICT, "Equipo ya registrado con el mismo nombre y deporte");
                    return;
                }
            }

            equipos.add(nuevoEquipo);
            response.setStatus(HttpServletResponse.SC_CREATED);
            return;
        }

        BufferedReader reader = request.getReader();
        StringBuilder json = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            json.append(line);
        }

        if (json.length() > 0) {
            JSONObject jsonObject = new JSONObject(json.toString());
            nombre = jsonObject.getString("nombre");
            deporte = jsonObject.getString("deporte");
            ciudad = jsonObject.getString("ciudad");
            fechaStr = jsonObject.getString("fechaFundacion");
            logo = jsonObject.getString("logo");
            JSONArray jugadoresArray = jsonObject.optJSONArray("jugadores");

            EquipoDAO nuevoEquipo = new EquipoDAO();
            nuevoEquipo.setId(equipoCounter++);
            nuevoEquipo.setNombre(nombre);
            nuevoEquipo.setDeporte(deporte);
            nuevoEquipo.setCiudad(ciudad);

            try {
                Date fechaFundacion = formato.parse(fechaStr); // Convertir String a Date
                nuevoEquipo.setFechaFundacion(fechaFundacion);
            } catch (ParseException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de fecha incorrecto");
                e.printStackTrace();
            }

            nuevoEquipo.setLogo(logo);

            List<Integer> jugadoresIds = new ArrayList<>();
            if (jugadoresArray != null) {
                for (int i = 0; i < jugadoresArray.length(); i++) {
                    //JugadorDAO jugador = buscarJugadorPorId(jugadoresArray.getInt(i));
                   // if (jugador != null) {
                        jugadoresIds.add(jugadoresArray.getInt(i));
                    //}
                }
            }

            nuevoEquipo.setJugadores(jugadoresIds);

            List<Integer> jugadoresNoEncontrados = new ArrayList<>();
            for (int jugadorId : jugadoresIds) {
                boolean existe = jugadoresRegistrados.stream().anyMatch(j -> j.getId() == jugadorId);
                if (!existe) {
                    jugadoresNoEncontrados.add(jugadorId);
                }
            }

            if (!jugadoresNoEncontrados.isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Los siguientes jugadores no existen: " + jugadoresNoEncontrados);
                return;
            }

            for (EquipoDAO equipo : equipos) {
                if (equipo.getNombre().equalsIgnoreCase(nombre) && equipo.getDeporte().equalsIgnoreCase(deporte)) {
                    response.sendError(HttpServletResponse.SC_CONFLICT, "Equipo ya registrado con el mismo nombre y deporte");
                    return;
                }
            }

            equipos.add(nuevoEquipo);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos no proporcionados");
        }
    }

}

