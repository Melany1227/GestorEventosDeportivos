package com.ces3.eventosdeportivos.DAO;

import lombok.*;

import java.util.Date;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EquipoDAO {
    private Integer id;
    private String nombre;
    private String deporte;
    private String ciudad;
    private Date fechaFundacion;
    private String logo;
    private List<Integer> jugadores;
}
