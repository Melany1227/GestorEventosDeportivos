package com.ces3.eventosdeportivos.Servlet;

import com.ces3.eventosdeportivos.DAO.EquipoDAO;
import com.ces3.eventosdeportivos.DAO.EventoDAO;
import com.ces3.eventosdeportivos.DAO.EstadoEvento;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet(name = "eventos", urlPatterns = "/eventos")
public class EventoServlet extends HttpServlet {
    private static List<EventoDAO> eventos = new ArrayList<>();
    private static int eventoCounter = 1;
    private static final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
    private static final List<EquipoDAO> equipos = EquipoServlet.equipos;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String fechaStr = request.getParameter("fecha");
        String lugar = request.getParameter("lugar");
        String deporte = request.getParameter("deporte");
        String equiposStr = request.getParameter("equiposParticipantes");
        String capacidadStr = request.getParameter("capacidad");
        String entradasVendidasStr = request.getParameter("entradasVendidas");
        String estadoStr = request.getParameter("estado");

        if (nombre != null && fechaStr != null && lugar != null && deporte != null && equiposStr != null && capacidadStr != null && entradasVendidasStr != null && estadoStr != null) {
            try {
                Date fecha = formato.parse(fechaStr);
                int capacidad = Integer.parseInt(capacidadStr);
                int entradasVendidas = Integer.parseInt(entradasVendidasStr);
                EstadoEvento estado = EstadoEvento.valueOf(estadoStr.toUpperCase());

                List<Integer> equiposId = new ArrayList<>();
                for (String id : equiposStr.split(",")) {
                    try {
                        equiposId.add(Integer.parseInt(id.trim()));
                    } catch (NumberFormatException e) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Formato de ID del equipo no es válido");
                        return;
                    }
                }

                List<EquipoDAO> equiposParticipantes = new ArrayList<>();
                for (Integer id : equiposId) {
                    EquipoDAO equipo = equipos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
                    if (equipo == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El equipo con ID " + id + " no existe.");
                        return;
                    }
                    equiposParticipantes.add(equipo);
                }

                if (equiposParticipantes.size() < 2) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Debe haber al menos dos equipos en el evento.");
                    return;
                }

                boolean mismoDeporte = equiposParticipantes.stream().allMatch(e -> e.getDeporte().equals(deporte));
                if (!mismoDeporte) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Todos los equipos deben ser del mismo deporte que el evento.");
                    return;
                }

                EventoDAO nuevoEvento = new EventoDAO(eventoCounter++, nombre, fecha, lugar, deporte, equiposId, capacidad, entradasVendidas, estado);
                eventos.add(nuevoEvento);
                response.setStatus(HttpServletResponse.SC_CREATED);
                return;
            } catch (Exception e) {
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
                Date jsonFecha = formato.parse(jsonObject.getString("fecha"));
                String jsonLugar = jsonObject.getString("lugar");
                String jsonDeporte = jsonObject.getString("deporte");
                int jsonCapacidad = jsonObject.getInt("capacidad");
                int jsonEntradasVendidas = jsonObject.getInt("entradasVendidas");
                EstadoEvento jsonEstado = EstadoEvento.valueOf(jsonObject.getString("estado").toUpperCase());

                JSONArray jsonEquipos = jsonObject.getJSONArray("equiposParticipantes");
                List<Integer> equiposId = new ArrayList<>();
                for (int i = 0; i < jsonEquipos.length(); i++) {
                    equiposId.add(jsonEquipos.getInt(i));
                }

                List<EquipoDAO> equiposParticipantes = new ArrayList<>();
                for (Integer id : equiposId) {
                    EquipoDAO equipo = equipos.stream().filter(e -> e.getId() == id).findFirst().orElse(null);
                    if (equipo == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El equipo con ID " + id + " no existe.");
                        return;
                    }
                    equiposParticipantes.add(equipo);
                }

                if (equiposParticipantes.size() < 2) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Debe haber al menos dos equipos en el evento.");
                    return;
                }

                boolean mismoDeporte = equiposParticipantes.stream().allMatch(e -> e.getDeporte().equals(jsonDeporte));
                if (!mismoDeporte) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Todos los equipos deben ser del mismo deporte que el evento.");
                    return;
                }

                EventoDAO nuevoEvento = new EventoDAO(eventoCounter++, jsonNombre, jsonFecha, jsonLugar, jsonDeporte, equiposId, jsonCapacidad, jsonEntradasVendidas, jsonEstado);
                eventos.add(nuevoEvento);
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
        for (EventoDAO evento : eventos) {
            JSONObject jsonEvento = new JSONObject(evento);
            JSONArray jsonEquipos = new JSONArray();
            for (Integer equipoId : evento.getEquiposParticipantes()) {
                Optional<EquipoDAO> equipoOpt = equipos.stream().filter(e -> e.getId() == equipoId).findFirst();
                equipoOpt.ifPresent(equipo -> {
                    JSONObject jsonEquipo = new JSONObject();
                    jsonEquipo.put("id", equipo.getId());
                    jsonEquipo.put("nombre", equipo.getNombre());
                    jsonEquipos.put(jsonEquipo);
                });
            }
            jsonEvento.put("equiposParticipantes", jsonEquipos);
            jsonArray.put(jsonEvento);
        }

        response.setContentType("application/json");
        response.getWriter().write(jsonArray.toString());
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo();

        if (path != null) {
            if (path.equals("/vender-entradas")) {
                venderEntradas(request, response);
            } else if (path.equals("/actualizar-estado")) {
                actualizarEstado(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ruta no válida.");
            }
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Debe proporcionar una ruta válida.");
        }
    }

    private void venderEntradas(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int eventoId = Integer.parseInt(request.getParameter("eventoId"));
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));

            EventoDAO evento = eventos.stream()
                    .filter(e -> e.getId() == eventoId)
                    .findFirst()
                    .orElse(null);

            if (evento == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Evento no encontrado.");
                return;
            }

            if (evento.getEntradasVendidas() + cantidad > evento.getCapacidad()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No se pueden vender más entradas de las disponibles.");
                return;
            }

            evento.setEntradasVendidas(evento.getEntradasVendidas() + cantidad);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Entradas vendidas exitosamente.");
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetros inválidos.");
        }
    }

    private void actualizarEstado(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int eventoId = Integer.parseInt(request.getParameter("eventoId"));
            String estadoStr = request.getParameter("estado");

            EventoDAO evento = eventos.stream()
                    .filter(e -> e.getId() == eventoId)
                    .findFirst()
                    .orElse(null);

            if (evento == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Evento no encontrado.");
                return;
            }

            try {
                EstadoEvento nuevoEstado = EstadoEvento.valueOf(estadoStr.toUpperCase());
                evento.setEstado(nuevoEstado);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Estado del evento actualizado correctamente.");
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Estado no válido.");
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetros inválidos.");
        }
    }

}
