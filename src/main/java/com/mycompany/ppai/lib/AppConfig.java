package com.mycompany.ppai.lib;

import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {

    private static final AppConfig INSTANCE = new AppConfig();
    private final Dotenv dotenv;

    private AppConfig() {
        // Carga el archivo .env una sola vez cuando se crea la instancia
        try {
            this.dotenv = Dotenv.load();
            System.out.println("✅ Configuración de .env cargada exitosamente.");
        } catch (Exception e) {
            // Manejar errores si el archivo .env no se encuentra o es ilegible
            System.err.println("❌ ERROR: No se pudo cargar el archivo .env. Asegúrate de que existe en la raíz del proyecto.");
            throw new ExceptionInInitializerError(e); 
        }
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    /**
     * Obtiene una variable del entorno cargado.
     * @param key La clave de la variable (ej: "MAIL_USERNAME")
     * @return El valor de la variable
     */
    public String get(String key) {
        // Usamos get() de dotenv para obtener el valor
        String value = dotenv.get(key);
        if (value == null) {
            throw new IllegalArgumentException("La variable de entorno '" + key + "' no está definida en el .env.");
        }
        return value;
    }
}