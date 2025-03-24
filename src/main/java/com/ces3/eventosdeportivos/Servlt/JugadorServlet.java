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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "jugadores", urlPatterns = "/jugadores")
public class JugadorServlet extends HttpServlet {
    private static List<JugadorDAO> jugadores = new ArrayList<>();
    private static int jugadorCounter = 1;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        JSONObject json = new JSONObject(sb.toString());

        JugadorDAO nuevoJugador = new JugadorDAO(jugadorCounter++, json.getString("nombre"), json.getString("apellido"), json.getString("fechaNacimiento"), json.getString("nacionalidad"), json.getString("posicion"), json.getInt("numero"), json.getInt("equipoId"), json.getBoolean("estadoActivo"));

        for (JugadorDAO jugador : jugadores) {
            if (jugador.getEquipoId() == nuevoJugador.getEquipoId() && jugador.getNumero() == nuevoJugador.getNumero()) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Número de jugador ya existe en este equipo");
                return;
            }
        }

        jugadores.add(nuevoJugador);
        response.setStatus(HttpServletResponse.SC_CREATED);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JSONArray jsonArray = new JSONArray(jugadores);
        response.setContentType("application/json");
        response.getWriter().write(jsonArray.toString());
    }
}
