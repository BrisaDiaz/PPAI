package com.mycompany.ppai.boundaries;

public interface IObservadorSismografo {
    void actualizar( String identificador,
                     java.time.LocalDateTime fechasHora,
                     String estado,
                     java.util.List<String> motivos,
                     java.util.List<String> comentarios);
}
