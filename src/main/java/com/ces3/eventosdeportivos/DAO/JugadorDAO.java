package com.ces3.eventosdeportivos.DAO;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JugadorDAO {
    private Integer id;
    private String nombre;
    private String apellido;
    private Date fechaNacimiento;
    private String nacionalidad;
    private String posicion;
    private Integer numero;
    private Integer equipoId;
    private Boolean estadoActivo;
}
