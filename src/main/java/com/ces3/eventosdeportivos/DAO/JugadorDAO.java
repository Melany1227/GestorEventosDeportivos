package com.ces3.eventosdeportivos.DAO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JugadorDAO {
    private int id;
    private String nombre;
    private String apellido;
    private String fechaNacimiento;
    private String nacionalidad;
    private String posicion;
    private int numero;
    private int equipoId;
    private boolean estadoActivo;
}
