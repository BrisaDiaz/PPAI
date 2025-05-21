/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.ppai;

import com.mycompany.ppai.boundaries.InterfazNotificacion;
import com.mycompany.ppai.boundaries.MonitorCCRS;
import com.mycompany.ppai.boundaries.PantallaCierreOrdenInspeccion;
import com.mycompany.ppai.controllers.GestorCierreOrdenInspeccion;
import java.util.ArrayList;
import java.util.List;

// para generar los datos de prueba
import java.time.LocalDateTime;
import com.mycompany.ppai.entities.Sesion;
import com.mycompany.ppai.entities.Usuario;
import com.mycompany.ppai.entities.Empleado;
import com.mycompany.ppai.entities.Rol;
import com.mycompany.ppai.entities.OrdenDeInspeccion;
import com.mycompany.ppai.entities.EstacionSismologica;
import com.mycompany.ppai.entities.Sismografo;
import com.mycompany.ppai.entities.Estado;

/**
 *
 * @author brisa
 */
public class PPAI {
       
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            // Mock dependencies for the Gestor


            com.mycompany.ppai.entities.Sesion sesion = new com.mycompany.ppai.entities.Sesion(null, null); // Mock
            InterfazNotificacion notificacion = (mails, cuerpo) -> System.out.println("Notificando: " + cuerpo + " a " + mails);
            List<MonitorCCRS> pantallasCCRS = new ArrayList<>();

            com.mycompany.ppai.controllers.GestorCierreOrdenInspeccion gestor = new com.mycompany.ppai.controllers.GestorCierreOrdenInspeccion(sesion, null, notificacion, pantallasCCRS);
            PantallaCierreOrdenInspeccion pantalla = new PantallaCierreOrdenInspeccion(gestor);
            gestor.setPantallaCierreOrdenInspeccion(pantalla);
            pantalla.opcionCerrarOrdenDeInspeccion();
        });
    }
}
