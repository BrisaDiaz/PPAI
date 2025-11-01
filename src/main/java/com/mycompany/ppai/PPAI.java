package com.mycompany.ppai;

import com.mycompany.ppai.entities.*;
import com.mycompany.ppai.lib.AppConfig;
import com.mycompany.ppai.controllers.GestorCierreOrdenInspeccion;
import com.mycompany.ppai.boundaries.NotificadorResponsableReparacion;
import com.mycompany.ppai.boundaries.MonitorCCRS;
import com.mycompany.ppai.repositories.*;
import com.mycompany.ppai.boundaries.PantallaCierreOrdenInspeccion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;


public class PPAI {

    public static void main(String[] args) {
        
        EntityManager em = null;
        try {
            AppConfig.getInstance();
        
            em = Persistence
                .createEntityManagerFactory("sismos")
                .createEntityManager();
    
            // Creación de instancias de Repositorio (método Singleton)
            EstadoRepository estadoRepository = EstadoRepository.getInstance(em);
            OrdenDeInspeccionRepository orderRepository = OrdenDeInspeccionRepository.getInstance(em);
            SismografoRepository sismografoRepository = SismografoRepository.getInstance(em);
            EmpleadoRepository empleadoRepository = EmpleadoRepository.getInstance(em);
            MotivoTipoRespository motivoTipoRepository = MotivoTipoRespository.getInstance(em);
            RolRepository rolRepository = RolRepository.getInstance(em);
            UsuarioRepository usuarioRepository = UsuarioRepository.getInstance(em);
            SesionRepository sesionRepository = SesionRepository.getInstance(em);
            EstacionSismologicaRepository estacionRepository = EstacionSismologicaRepository.getInstance(em);
            
            // Crear Roles
            Rol rolRI = Rol.builder()
            .nombre( NombreRolEnum.RESPONSABLE_DE_INSPECCION)
            .descripcionRol("Realiza inspecciones")
            .build();

            Rol rolReparacion = Rol.builder()
            .nombre( NombreRolEnum.RESPONSABLE_DE_REPARACION)
            .descripcionRol("Realiza inspecciones")
            .build();

            rolRepository.guardarTodos(List.of(rolRI, rolReparacion));

            // Crear empleados
            Empleado empleadoRI = Empleado.builder()
            .nombre("Juan")
            .apellido("Pérez")
            .telefono("123456789")
            .mail("juan.perez@example.com")
            .rol(rolRI)
            .build();

            Empleado empleadoReparacion = Empleado.builder()
            .nombre("Brisa")
            .apellido("Díaz")
            .telefono("987654321")
            .mail("brisaabigaildiaz@gmail.com")
            .rol(rolReparacion)
            .build();

            empleadoRepository.guardarTodos(List.of(empleadoRI, empleadoReparacion));

            // Crear usuarios
            Usuario usuarioRI = Usuario.builder()
            .nombreUsuario("jperez")
            .constraseña("password")
            .empleado(empleadoRI)
            .build();

            usuarioRepository.guardar(usuarioRI);

            // Crear sesión
            Sesion sesion = Sesion.builder()
            .fechaHoraDesde(LocalDateTime.now().minusHours(1))
            .usuario(usuarioRI)
            .build();
            
            sesionRepository.guardar(sesion);
            
            // Crear estados
            Estado estadoCompletamenteRealizada = Estado.builder()
            .nombreEstado( NombreEstadoEnum.COMPLETAMENTE_REALIZADA)
            .ambito(AmbitoEstadoEnum.ORDEN_DE_INSPECCION)
            .build();
            
            Estado estadoCerradaOrden = Estado.builder()
            .nombreEstado( NombreEstadoEnum.CERRADA)
            .ambito(AmbitoEstadoEnum.ORDEN_DE_INSPECCION)
            .build();
            
            Estado estadoFueraDeServicioSismografo = Estado.builder()
            .nombreEstado( NombreEstadoEnum.FUERA_DE_SERVICIO)
            .ambito(AmbitoEstadoEnum.SISMOGRAFO)
            .build();
            
            Estado estadoOnlineSismografo = Estado.builder()
            .nombreEstado( NombreEstadoEnum.ONLINE)
            .ambito(AmbitoEstadoEnum.SISMOGRAFO)
            .build();

            estadoRepository.guardarTodos(List.of(estadoCompletamenteRealizada, estadoCerradaOrden, estadoFueraDeServicioSismografo, estadoOnlineSismografo));

            // Crear estaciones sismológicas
            EstacionSismologica estacion1 = EstacionSismologica.builder()
            .nombre("Estación Central")
            .codigoEstacion("ESC01")
            .documentoCertificacionAdq("DOC123")
            .fechaSolicitudCertificacion(LocalDateTime.now().minusDays(30))
            .latitud(-34.6037F)
            .longitud(-58.3816F)
            .nroCertificacionAdquisicion(1)
            .build();

            EstacionSismologica estacion2 = EstacionSismologica.builder()
            .nombre("Estación Norte")
            .codigoEstacion("ESN02")
            .documentoCertificacionAdq("DOC456")
            .fechaSolicitudCertificacion(LocalDateTime.now().minusDays(25))
            .latitud(-33.0000F)
            .longitud(-59.0000F)
            .nroCertificacionAdquisicion(2)
            .build();

            estacionRepository.guardarTodos(List.of(estacion1, estacion2));

            // Crear sismógrafos
            Sismografo sismografo1 = new Sismografo(LocalDateTime.now().minusYears(2), "SMG001", 1001, estacion1, estadoOnlineSismografo, LocalDateTime.now(), empleadoRI);
            Sismografo sismografo2 = new Sismografo(LocalDateTime.now().minusYears(1), "SMG002", 2002, estacion2, estadoOnlineSismografo, LocalDateTime.now(), empleadoRI);

            sismografoRepository.guardarTodos(List.of(sismografo1, sismografo2));

            // Crear órdenes de inspección COMPLETAMENTE REALIZADAS
            OrdenDeInspeccion orden1 = new OrdenDeInspeccion(LocalDateTime.now().minusDays(7), 1, estadoCompletamenteRealizada, empleadoRI, estacion1);
            orden1.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(7).plusHours(3));
            OrdenDeInspeccion orden2 = new OrdenDeInspeccion(LocalDateTime.now().minusDays(5), 2, estadoCompletamenteRealizada, empleadoRI, estacion2);
            orden2.setFechaHoraFinalizacion(LocalDateTime.now().minusDays(5).plusHours(2));

            orderRepository.guardarTodos(List.of(orden1, orden2));

            // Crear Motivos de Fuera de Servicio
            MotivoTipo motivoTipo1 = MotivoTipo.builder()
            .descripcion("Falla de energía")
            .build();

            MotivoTipo motivoTipo2 = MotivoTipo.builder()
            .descripcion("Problema de sensor")
            .build();

            MotivoTipo motivoTipo3 = MotivoTipo.builder()
            .descripcion("Mantenimiento programado")
            .build();

            MotivoTipo motivoTipo4 = MotivoTipo.builder()
            .descripcion("Daño por condiciones climáticas")
            .build();

            MotivoTipo motivoTipo5 = MotivoTipo.builder()
            .descripcion("Desconexión accidental de red")
            .build();

            motivoTipoRepository.guardarTodos(List.of(
                motivoTipo1,
                motivoTipo2,
                motivoTipo3,
                motivoTipo4,
                motivoTipo5
            ));

            System.out.println("--- DATOS DE INICIALIZACIÓN GUARDADOS EXITOSAMENTE ---");


            // Crear Monitores CCRS
            MonitorCCRS monitor = new MonitorCCRS();

            // Crear Gestor y Pantalla (Inyección manual)
            GestorCierreOrdenInspeccion gestor = new GestorCierreOrdenInspeccion(sesion, monitor,
                                        estadoRepository, orderRepository, sismografoRepository, empleadoRepository,
                                        motivoTipoRepository);
            
            PantallaCierreOrdenInspeccion pantalla = new PantallaCierreOrdenInspeccion(gestor);
            gestor.setPantallaCierreOrdenInspeccion(pantalla);

            pantalla.opcionCerrarOrdenDeInspeccion();
            
        } catch (Exception e) {
            System.err.println("Ocurrió un error en la aplicación principal:");
            e.printStackTrace();
        }
    }
}