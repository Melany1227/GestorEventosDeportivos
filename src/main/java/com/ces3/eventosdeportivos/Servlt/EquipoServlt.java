package com.ces3.eventosdeportivos.Servlt;

import com.ces3.eventosdeportivos.DAO.JugadorDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.ces3.eventosdeportivos.DAO.EquipoDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "Servlt", urlPatterns = "/equipos")
public class EquipoServlt extends HttpServlet {
    public String message;
    public List<EquipoDAO> equipos = new ArrayList<>();
    private static int equipoCounter = 1;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONArray jsonArray = new JSONArray(equipos);
        response.setContentType("application/json");
        response.getWriter().write(jsonArray.toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nombre = request.getParameter("nombre");
        String deporte = request.getParameter("deporte");
        String ciudad = request.getParameter("ciudad");
        String fechaStr = request.getParameter("fechaFundacion");
        String logo = request.getParameter("logo");
        List<JugadorDAO> jugadore = new ArrayList<>();
        String jugadores = request.getParameter("jugadores");

        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");

        if (nombre != null && deporte != null && ciudad != null && fechaStr != null && logo != null) {
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

            EquipoDAO nuevoEquipo = new EquipoDAO();
            nuevoEquipo.setId(equipoCounter++);
            nuevoEquipo.setNombre(nombre);
            nuevoEquipo.setDeporte(deporte);
            nuevoEquipo.setCiudad(ciudad);

            try {
                Date fechaFundacion = formato.parse(fechaStr); // Convertir String a Date
                nuevoEquipo.setFechaFundacion(fechaFundacion);
            } catch (ParseException e) {
                e.printStackTrace(); // Manejo de error si el formato es incorrecto
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
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos no proporcionados");
        }
    }

}

