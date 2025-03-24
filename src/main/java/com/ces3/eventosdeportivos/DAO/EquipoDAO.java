package com.ces3.eventosdeportivos.DAO;

import lombok.*;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDAO {
    private int id;
    private String nombre;
    private String deporte;
    private String ciudad;
    private String fechaFundacion;
    private String logo;
    private List<Integer> jugadores;
}
