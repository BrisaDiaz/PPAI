package com.mycompany.ppai.boundaries;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.mycompany.ppai.lib.AppConfig;

public class NotificadorResponsableReparacion implements IObservadorSismografo {
    private static final Session session;
    
    // ⭐ Variables que leerán del entorno
    final static String USERNAME = AppConfig.getInstance().get("MAIL_USERNAME");
    final static String APP_PASSWORD = AppConfig.getInstance().get("MAIL_APP_PASSWORD");

    static {
        // ... (Tu configuración de Properties) ...
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); 
        props.put("mail.smtp.port", "587"); 
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); 

        // ⚠️ VALIDACIÓN DE SEGURIDAD BÁSICA
        if (USERNAME == null || APP_PASSWORD == null) {
            System.err.println("ERROR: Las variables de entorno MAIL_USERNAME y/o MAIL_APP_PASSWORD no están definidas.");
            // Esto forzará una excepción y detendrá la ejecución si las credenciales faltan
            throw new ExceptionInInitializerError("Credenciales de correo faltantes en las variables de entorno.");
        }

        // 2. Crear el autenticador
        Authenticator auth = new Authenticator() {
            @Override 
            protected PasswordAuthentication getPasswordAuthentication() {
                // Usamos las variables de clase (leídas del entorno)
                return new PasswordAuthentication(USERNAME, APP_PASSWORD); 
            }
        };

        // 3. Crear la Session
        session = Session.getInstance(props, auth);
    }

    private List<String> emails;

    // Constructor
    public NotificadorResponsableReparacion(List<String> emails) {
        this.emails = emails;
    }

    public void actualizar( String identificador,
                            LocalDateTime fechasHora,
                            String estado,
                            List<String> motivos,
            List<String> comentarios) {

        String notificacion = generarNotificacion(identificador, fechasHora, estado, motivos, comentarios);
        // System.out.println(notificacion);
        enviarNotificacion(notificacion);
    }

    public String generarNotificacion(String identificadorSismografo, LocalDateTime fechasHora, String estado, List<String> motivos,
            List<String> comentarios) {

        StringBuilder cuerpo = new StringBuilder();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String fechaHoraFormateada = fechasHora.format(formatter);

        cuerpo.append("Fecha y Hora de Actualización: ").append(fechaHoraFormateada).append("\n");
        cuerpo.append("Identificador: ").append(identificadorSismografo).append("\n");
        cuerpo.append("Estado Actual: ").append(estado).append("\n");
        cuerpo.append("Motivos:\n");
        for (int i = 0; i < motivos.size(); i++) {
            cuerpo.append("- ").append(motivos.get(i)).append(": ");
            cuerpo.append(comentarios.get(i)).append("\n");
        }

        return cuerpo.toString();
    }

    public void enviarNotificacion(String notificacion){
        try
		{
			// 4. Crear el mensaje de correo
			MimeMessage msg = new MimeMessage(session);
			// 5. Configurar los encabezados del correo
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
			msg.addHeader("Content-Transfer-Encoding", "8bit");

			// 6. Configurar los campos del correo
            msg.setFrom(new InternetAddress("grupo_14@ppai.com", "NoReply-JD"));
            msg.setReplyTo(InternetAddress.parse("grupo_14@ppai.com", false));
            msg.setSubject("Sismógrafo fuera de servicio", "UTF-8");
            msg.setText(notificacion, "UTF-8");
			msg.setSentDate(new Date());
			
            // 7. Convertir la List<String> de correos a un array de InternetAddress
            InternetAddress[] address = new InternetAddress[this.emails.size()];
            for (int i = 0; i < this.emails.size(); i++) {
                address[i] = new InternetAddress(this.emails.get(i));
            }

            // 8. Usar el array de InternetAddress para establecer los destinatarios
            msg.setRecipients(Message.RecipientType.TO, address); 

            System.out.println("Message is ready");
            Transport.send(msg);  

            System.out.println("EMail Sent Successfully!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}