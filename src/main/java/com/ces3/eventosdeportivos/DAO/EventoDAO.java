package com.ces3.eventosdeportivos.DAO;

import lombok.*;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventoDAO {
    private Integer id;
    private String nombre;
    private Date fecha;
    private String lugar;
    private String deporte;
    private List<Integer> equiposParticipantes;
    private Integer capacidad;
    private Integer entradasVendidas;
    private EstadoEvento estado;
}
