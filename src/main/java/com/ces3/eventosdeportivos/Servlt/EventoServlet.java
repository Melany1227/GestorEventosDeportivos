package com.ces3.eventosdeportivos.Servlt;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebServlet(name = "eventos", urlPatterns = "/eventos")
public class EventoServlet extends HttpServlet {
    private static List<EventoDAO> eventos = new ArrayList<>();
    private static int eventoCounter = 1;
    private static final SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String fechaStr = request.getParameter("fecha");
        String lugar = request.getParameter("lugar");
        String deporte = request.getParameter("deporte");
        String equiposStr = request.getParameter("equiposParticipantes");
        String capacidadStr = request.getParameter("capacidad");
        String entradasVendidasStr = request.getParameter("entradasVendidas");
        String estadoStr = request.getParameter("estado");

        // ✅ Opción 1: Si los datos vienen en Query Params
        if (nombre != null && fechaStr != null && lugar != null && deporte != null
                && equiposStr != null && capacidadStr != null && entradasVendidasStr != null && estadoStr != null) {
            try {
                Date fecha = formato.parse(fechaStr);
                int capacidad = Integer.parseInt(capacidadStr);
                int entradasVendidas = Integer.parseInt(entradasVendidasStr);
                EstadoEvento estado = EstadoEvento.valueOf(estadoStr.toUpperCase());

                List<Integer> equipos = new ArrayList<>();
                for (String id : equiposStr.split(",")) {
                    equipos.add(Integer.parseInt(id.trim()));
                }

                EventoDAO nuevoEvento = new EventoDAO(eventoCounter++, nombre, fecha, lugar, deporte, equipos, capacidad, entradasVendidas, estado);
                eventos.add(nuevoEvento);
                response.setStatus(HttpServletResponse.SC_CREATED);
                return;
            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error en los datos ingresados");
                return;
            }
        }

        // ✅ Opción 2: Si los datos vienen en JSON (Body)
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
                List<Integer> equipos = new ArrayList<>();
                for (int i = 0; i < jsonEquipos.length(); i++) {
                    equipos.add(jsonEquipos.getInt(i));
                }

                EventoDAO nuevoEvento = new EventoDAO(eventoCounter++, jsonNombre, jsonFecha, jsonLugar, jsonDeporte, equipos, jsonCapacidad, jsonEntradasVendidas, jsonEstado);
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
        JSONArray jsonArray = new JSONArray(eventos);
        response.setContentType("application/json");
        response.getWriter().write(jsonArray.toString());
    }
}
