package com.ces3.eventosdeportivos.DAO;

import lombok.*;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoDAO {
    private int id;
    private String nombre;
    private String fecha;
    private String lugar;
    private String deporte;
    private List<Integer> equiposParticipantes;
    private int capacidad;
    private int entradasVendidas;
    private EstadoEvento estado;
}
