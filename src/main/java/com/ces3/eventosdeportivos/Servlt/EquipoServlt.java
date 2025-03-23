package com.ces3.eventosdeportivos.Servlt;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet(name = "Servlt", urlPatterns = "/api/servlt")
public class EquipoServlt extends HttpServlet {
    public String message;

    public void init() { message = "Hello World!";}

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", 1);
        jsonObject.put("name","Mel");
        jsonArray.put(jsonObject);

        PrintWriter out = response.getWriter();
        out.print(jsonArray.toString());
        out.flush();
    }
}

