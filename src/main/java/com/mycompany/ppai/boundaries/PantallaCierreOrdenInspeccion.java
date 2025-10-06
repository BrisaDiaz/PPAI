package com.mycompany.ppai.boundaries;

import com.mycompany.ppai.controllers.GestorCierreOrdenInspeccion;
import com.google.gson.JsonObject;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PantallaCierreOrdenInspeccion extends JFrame {
    // Constantes de texto (Se mantienen sin cambios)
    private static final String TITULO_VENTANA = "Cierre de Orden de Inspección";
    private static final String LABEL_CARGANDO = "Cargando órdenes de inspección...";
    private static final String LABEL_SIN_ORDENES = "No se encontraron órdenes de inspección completamente realizadas.";
    private static final String BOTON_SELECCIONAR_ORDEN = "Seleccionar Orden";
    private static final String LABEL_SELECCIONAR_ORDEN = "Seleccione una orden de inspección:";
    private static final String LABEL_OBSERVACION = "Ingrese la observación de cierre:";
    private static final String CHECKBOX_FUERA_SERVICIO = "¿Se desea registrar el sismógrafo como fuera de servicio?";
    private static final String BOTON_CONFIRMAR_OBSERVACION = "Confirmar Observación";
    private static final String LABEL_MOTIVOS = "Seleccione los motivos (y comentarios):";
    private static final String LABEL_COMENTARIO = "Comentario:";
    private static final String BOTON_CONFIRMAR_MOTIVOS = "Confirmar Motivos";
    private static final String LABEL_CONFIRMACION = "¿Desea confirmar el cierre de la orden?";
    private static final String BOTON_CONFIRMAR_CIERRE = "Confirmar Cierre";
    private static final String BOTON_CANCELAR = "Cancelar";
    private static final String LABEL_CIERRE_EXITOSO = "Orden de inspección cerrada exitosamente.";
    private static final String BOTON_VOLVER_INICIO = "Volver al Inicio";
    private static final String COLUMNA_SELECCIONAR = "Seleccionar";
    private static final String COLUMNA_NUMERO = "Número";
    private static final String COLUMNA_SISMOGRAFO = "Sismógrafo";
    private static final String COLUMNA_FECHA_FIN = "Fecha Fin";
    private static final String MENSAJE_SELECCIONAR_ORDEN = "Por favor, seleccione una orden.";
    private static final String TITULO_ADVERTENCIA = "Advertencia";
    private static final String MENSAJE_ERROR_OBSERVACION = "Por favor, corrija la observación. (La observación no debe estar vacía y debe ser coherente)";
    private static final String MENSAJE_ERROR_COMENTARIOS = "Por favor, complete los comentarios de los motivos seleccionados.";
    private static final String MENSAJE_ERROR_SELECCION_MOTIVO = "Debe seleccionar al menos un motivo si marca el sismógrafo como 'fuera de servicio'.";
    private static final String MENSAJE_ERROR_CONFIRMACION = "Error al confirmar el cierre. Por favor, revise la información.";

    private final GestorCierreOrdenInspeccion gestor;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;

    // Componentes de la UI
    private JPanel ordenesPanel;
    private JTable ordenesTable;
    private DefaultTableModel ordenesTableModel;
    private JButton seleccionarOrdenBtn;
    private JPanel observacionPanel;
    private JTextArea observacionTextArea;
    private JCheckBox fueraServicioCheckBox;
    private JButton confirmarObservacionBtn;
    private JPanel motivosPanelContainer;
    private JPanel motivosPanel;
    private JButton confirmarMotivosBtn;
    private JPanel confirmacionPanel;
    private JButton confirmarCierreBtn;
    private JButton cancelarCierreBtn;
    private JPanel cierreExitosoPanel;
    private JButton volverInicioBtn;

    // Estado de la pantalla (Se mantienen sin cambios para la interacción con el Gestor)
    private final Map<String, Integer> ordenMap = new HashMap<>();
    private final Map<String, JCheckBox> motivoCheckBoxes = new HashMap<>();
    private final Map<String, JTextField> comentarioTextFields = new HashMap<>();
    private int selectedOrderNumber = -1;
    private int lastSelectedRow = -1;
    private boolean esperandoReintento = false;
    private String observacionPendienteReintento = null;
    private boolean fueraServicioPendienteReintento = false;
    private List<String[]> motivosPendientesReintento = null;
    private List<String> motivosTipoFueraServicio = new ArrayList<>();

    public PantallaCierreOrdenInspeccion(GestorCierreOrdenInspeccion gestor) {

        this.gestor = gestor;
        setTitle(TITULO_VENTANA);
        setSize(800, 600); // Aumentar tamaño para mejor visibilidad
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar ventana

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Se mantienen los nombres de las tarjetas para no afectar el flujo
        mainPanel.add(crearPanelCarga(), "loading");
        mainPanel.add(crearPanelSinOrdenes(), "sinOrdenes");
        mainPanel.add(crearPanelOrdenes(), "ordenes");
        mainPanel.add(crearPanelObservacion(), "observacion");
        mainPanel.add(crearPanelMotivos(), "motivos");
        mainPanel.add(crearPanelConfirmacion(), "confirmacion");
        mainPanel.add(crearPanelCierreExitoso(), "cierreExitoso");

        add(mainPanel);
        cardLayout.show(mainPanel, "loading");
    }

    public void habilitarVentana() {
        setVisible(true);
    }

    // --- PANELES CON MEJORAS DE UI ---

    private JPanel crearPanelCarga() {
        // Usa MigLayout para centrado vertical y horizontal con insets de 50
        JPanel loadingPanel = new JPanel(new MigLayout("insets 50, align center, fill"));
        JLabel loadingLabel = new JLabel(LABEL_CARGANDO, SwingConstants.CENTER);
        loadingLabel.setFont(loadingLabel.getFont().deriveFont(Font.BOLD, 18f));
        loadingPanel.add(loadingLabel, "span, wrap, align center");
        return loadingPanel;
    }

    private JPanel crearPanelSinOrdenes() {
        // Usa MigLayout para centrado vertical y horizontal con insets de 50
        JPanel sinOrdenesPanel = new JPanel(new MigLayout("insets 50, align center, fill"));
        JLabel sinOrdenesLabel = new JLabel(LABEL_SIN_ORDENES, SwingConstants.CENTER);
        sinOrdenesLabel.setFont(sinOrdenesLabel.getFont().deriveFont(Font.BOLD, 16f));
        sinOrdenesPanel.add(sinOrdenesLabel, "span, wrap, align center");
        return sinOrdenesPanel;
    }

    private JPanel crearPanelOrdenes() {
        // Aumento de insets y uso de gaps para separar elementos del borde y entre sí.
        ordenesPanel = new JPanel(new MigLayout("fill, insets 25 30 25 30", "[grow, fill]", "[pref!][grow, fill][pref!]"));

        JLabel headerLabel = new JLabel(LABEL_SELECCIONAR_ORDEN, SwingConstants.CENTER);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));

        ordenesTableModel = new DefaultTableModel(new Object[]{COLUMNA_SELECCIONAR, COLUMNA_NUMERO, COLUMNA_SISMOGRAFO, COLUMNA_FECHA_FIN}, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Boolean.class : super.getColumnClass(column);
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        ordenesTable = new JTable(ordenesTableModel);
        ordenesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Listener para simular checkbox exclusivo y guardar selección
        ordenesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ordenesTable.getSelectedRow();
                actualizarSeleccionTabla(selectedRow);
            }
        });

        JScrollPane ordenesScrollPane = new JScrollPane(ordenesTable);
        seleccionarOrdenBtn = new JButton(BOTON_SELECCIONAR_ORDEN);
        seleccionarOrdenBtn.addActionListener(this::tomarSelecOrdenInspeccion);

        // Añadir componentes al panel con MigLayout
        ordenesPanel.add(headerLabel, "north, wrap, gapy 0 15"); // Más espacio después del título
        ordenesPanel.add(ordenesScrollPane, "grow, push, wrap, gapy 0 15"); // Espacio antes del botón
        ordenesPanel.add(seleccionarOrdenBtn, "south, align center"); // Asegurar centrado del botón

        return ordenesPanel;
    }

    private JPanel crearPanelObservacion() {
        // Aumento de insets y uso de padding interno en el área de texto.
        observacionPanel = new JPanel(new MigLayout("fill, insets 25", "[grow, fill]", "[pref!][grow, fill][pref!][pref!]"));

        JLabel headerLabel = new JLabel(LABEL_OBSERVACION, SwingConstants.LEFT);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 14f));

        observacionTextArea = new JTextArea(5, 40);
        observacionTextArea.setLineWrap(true);
        observacionTextArea.setWrapStyleWord(true);
        
        // ** Aplicar Borde y Padding Interno a JTextArea **
        Border innerPadding = BorderFactory.createEmptyBorder(8, 8, 8, 8);
        Border lineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
        observacionTextArea.setBorder(BorderFactory.createCompoundBorder(lineBorder, innerPadding));

        JScrollPane observacionScrollPane = new JScrollPane(observacionTextArea);

        fueraServicioCheckBox = new JCheckBox(CHECKBOX_FUERA_SERVICIO);

        confirmarObservacionBtn = new JButton(BOTON_CONFIRMAR_OBSERVACION);
        confirmarObservacionBtn.addActionListener(this::tomarObservacionCierreOrden);

        observacionPanel.add(headerLabel, "wrap, gapy 5 5");
        observacionPanel.add(observacionScrollPane, "grow, push, wrap, hmin 100, gapy 5 10"); // Espacio después del área
        observacionPanel.add(fueraServicioCheckBox, "wrap, gapy 10 20"); // Más espacio vertical antes del botón
        observacionPanel.add(confirmarObservacionBtn, "south, align center");

        return observacionPanel;
    }

    private JPanel crearPanelMotivos() {
        // Título, scrollable body y botón de confirmación.
        motivosPanelContainer = new JPanel(new MigLayout("fill, insets 25", "[grow, fill]", "[pref!][grow, fill][pref!]"));

        JLabel headerLabel = new JLabel(LABEL_MOTIVOS, SwingConstants.LEFT);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 14f));

        // Ajuste: se agrega 'gapy 10' para separar verticalmente cada fila de motivo y comentario.
        motivosPanel = new JPanel(new MigLayout("wrap 3, fillx, insets 10 0 0 0, gapy 10", "[pref!][pref!][grow, fill]"));

        JScrollPane motivosScrollPane = new JScrollPane(motivosPanel);
        motivosScrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        motivosScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        confirmarMotivosBtn = new JButton(BOTON_CONFIRMAR_MOTIVOS);
        confirmarMotivosBtn.addActionListener(this::tomarComentarioPorMotivoTipo);

        motivosPanelContainer.add(headerLabel, "wrap, gapy 5");
        motivosPanelContainer.add(motivosScrollPane, "grow, push, wrap, gapy 10 20"); // Más espacio antes del botón
        motivosPanelContainer.add(confirmarMotivosBtn, "south, align center");

        return motivosPanelContainer;
    }

    private JPanel crearPanelConfirmacion() {
        // **Aumento de Insets** para forzar el centrado visual y mejor espaciado vertical.
        confirmacionPanel = new JPanel(new MigLayout("insets 100, align center, fill", "[grow, fill]"));

        JLabel confirmacionLabel = new JLabel(LABEL_CONFIRMACION, SwingConstants.CENTER);
        confirmacionLabel.setFont(confirmacionLabel.getFont().deriveFont(Font.BOLD, 18f));

        // Panel para los botones, asegurando separación horizontal
        JPanel buttonPanel = new JPanel(new MigLayout("align center, gap 30"));
        confirmarCierreBtn = new JButton(BOTON_CONFIRMAR_CIERRE);
        cancelarCierreBtn = new JButton(BOTON_CANCELAR);

        confirmarCierreBtn.addActionListener(e -> tomarConfirmacionCierre(true));
        cancelarCierreBtn.addActionListener(e -> tomarConfirmacionCierre(false));

        buttonPanel.add(confirmarCierreBtn);
        buttonPanel.add(cancelarCierreBtn);

        confirmacionPanel.add(confirmacionLabel, "wrap, align center, gapy 20 40"); // Más espacio vertical
        confirmacionPanel.add(buttonPanel, "align center");

        return confirmacionPanel;
    }

    private JPanel crearPanelCierreExitoso() {
        // Mejorado con MigLayout para centrado y mejor presentación.
        cierreExitosoPanel = new JPanel(new MigLayout("insets 50, align center, fill", "[grow, fill]"));

        JLabel cierreExitosoLabel = new JLabel(LABEL_CIERRE_EXITOSO, SwingConstants.CENTER);
        cierreExitosoLabel.setFont(cierreExitosoLabel.getFont().deriveFont(Font.BOLD, 18f));
        cierreExitosoLabel.setForeground(new Color(60, 179, 113)); // Color verde éxito

        volverInicioBtn = new JButton(BOTON_VOLVER_INICIO);
        volverInicioBtn.addActionListener(e -> volverInicioClick());

        cierreExitosoPanel.add(cierreExitosoLabel, "wrap, align center, gapy 20");
        cierreExitosoPanel.add(volverInicioBtn, "align center");

        return cierreExitosoPanel;
    }

    // --- MÉTODOS DE LÓGICA DE INTERFAZ (Ajuste de lógica de reintento en toma de observación) ---

    public void opcionCerrarOrdenDeInspeccion() {
        cardLayout.show(mainPanel, "loading");
        habilitarBotonesConfirmacionCancelacion(true);
        limpiarCampos();
        resetearEstadoReintento();
        habilitarVentana();
        gestor.nuevoCierreOrdenInspeccion();
    }

    private void limpiarCampos() {
        ordenesTable.clearSelection();
        if (ordenesTableModel.getRowCount() > 0) {
            for (int i = 0; i < ordenesTableModel.getRowCount(); i++) {
                ordenesTableModel.setValueAt(false, i, 0);
            }
        }
        lastSelectedRow = -1;
        selectedOrderNumber = -1;
        observacionTextArea.setText("");
        fueraServicioCheckBox.setSelected(false);
        motivosPanel.removeAll();
        motivoCheckBoxes.clear();
        // Es importante revalidar y repintar después de removeAll()
        motivosPanel.revalidate();
        motivosPanel.repaint();
    }

    private void habilitarBotonesConfirmacionCancelacion(boolean habilitar) {
        if (confirmarCierreBtn != null) {
            confirmarCierreBtn.setVisible(true);
            confirmarCierreBtn.setEnabled(habilitar);
        }
        if (cancelarCierreBtn != null) {
            cancelarCierreBtn.setVisible(true);
            cancelarCierreBtn.setEnabled(habilitar);
        }
    }

    private void resetearEstadoReintento() {
        esperandoReintento = false;
        observacionPendienteReintento = null;
        fueraServicioPendienteReintento = false;
        motivosPendientesReintento = null;
        motivosTipoFueraServicio.clear();
    }

    private void actualizarSeleccionTabla(int selectedRow) {
        if (lastSelectedRow != -1 && selectedRow != lastSelectedRow) {
            ordenesTableModel.setValueAt(false, lastSelectedRow, 0);
        }
        if (selectedRow != -1) {
            // Deseleccionar todas las filas primero, excepto la seleccionada, si es necesario.
            for (int i = 0; i < ordenesTableModel.getRowCount(); i++) {
                if (i != selectedRow) {
                    ordenesTableModel.setValueAt(false, i, 0);
                }
            }
            ordenesTableModel.setValueAt(true, selectedRow, 0);
            lastSelectedRow = selectedRow;
        } else {
            lastSelectedRow = -1;
        }
    }

    public void mostrarInfoOrdenesInspeccion(List<JsonObject> infoOrdenesInspeccion) {
        ordenMap.clear();
        ordenesTableModel.setRowCount(0);
        lastSelectedRow = -1;

        if (infoOrdenesInspeccion.isEmpty()) {
            cardLayout.show(mainPanel, "sinOrdenes");
        } else {
            for (JsonObject info : infoOrdenesInspeccion) {
                int numeroOrden = info.get("numeroOrden").getAsInt();
                String identificadorSismografo = info.get("identificadorSismografo").getAsString();
                String fechaHoraFinalizacion = info.get("fechaHoraFinalizacion").getAsString();
                ordenMap.put(identificadorSismografo + " - " + numeroOrden, numeroOrden);
                ordenesTableModel.addRow(new Object[]{false, numeroOrden, identificadorSismografo, fechaHoraFinalizacion});
            }
            cardLayout.show(mainPanel, "ordenes");
            ordenesTable.setEnabled(true);
            seleccionarOrdenBtn.setEnabled(true);
        }
        revalidate();
        repaint();
    }

    private void tomarSelecOrdenInspeccion(ActionEvent e) {
        int selecOrdenInspeccion = ordenesTable.getSelectedRow();
        if (selecOrdenInspeccion != -1) {
            selectedOrderNumber = (Integer) ordenesTableModel.getValueAt(selecOrdenInspeccion, 1);
            ordenesTable.setEnabled(false);
            seleccionarOrdenBtn.setEnabled(false);
            gestor.tomarSelecOrdenInspeccion(selectedOrderNumber);
        } else {
            JOptionPane.showMessageDialog(this, MENSAJE_SELECCIONAR_ORDEN, TITULO_ADVERTENCIA, JOptionPane.WARNING_MESSAGE);
        }
    }

    public void solicitarObservacionCierreOrden() {
        cardLayout.show(mainPanel, "observacion");
        observacionTextArea.setEnabled(true);
        fueraServicioCheckBox.setEnabled(true);
        confirmarObservacionBtn.setEnabled(true);

        if (esperandoReintento && observacionPendienteReintento != null) {
            observacionTextArea.setText(observacionPendienteReintento);
            fueraServicioCheckBox.setSelected(fueraServicioPendienteReintento);
        } else {
            observacionTextArea.setText("");
            fueraServicioCheckBox.setSelected(false);
        }
    }

    private void tomarObservacionCierreOrden(ActionEvent e) {
        String observacion = observacionTextArea.getText().trim();
        boolean fueraServicio = fueraServicioCheckBox.isSelected();

        // Guardamos el estado actual de la observación por si hay que reintentar
        observacionPendienteReintento = observacion;
        fueraServicioPendienteReintento = fueraServicio;
        
        gestor.tomarObservacionCierreOrden(observacion, fueraServicio);
    }

    public void observacionCierreOrdenOK() {
        // Solo si el gestor valida, deshabilitamos la UI de observación
        observacionTextArea.setEnabled(false);
        fueraServicioCheckBox.setEnabled(false);
        confirmarObservacionBtn.setEnabled(false);
        esperandoReintento = false;
        observacionPendienteReintento = null;
        fueraServicioPendienteReintento = false;
    }

    public void solicitarMotivosFueraDeServicio(List<String> tiposMotivo) {
        this.motivosTipoFueraServicio = tiposMotivo;
        motivosPanel.removeAll();
        motivoCheckBoxes.clear();
        comentarioTextFields.clear();
        // Layout para motivos con gap vertical mejorado
        motivosPanel.setLayout(new MigLayout("wrap 3, fillx, insets 10 0 0 0, gapy 10", "[pref!][pref!][grow, fill]"));

        List<String[]> motivosReintento = (esperandoReintento && motivosPendientesReintento != null) ? motivosPendientesReintento : new ArrayList<>();

        Border fieldPadding = BorderFactory.createEmptyBorder(4, 5, 4, 5);
        Border fieldLineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
        
        for (String tipo : tiposMotivo) {

            // Fila de componentes: CheckBox | Label "Comentario" | TextField
            JCheckBox checkBox = new JCheckBox(tipo);
            JTextField comentarioField = new JTextField(20);
            comentarioField.setEnabled(false);
            
            // ** Aplicar Borde y Padding Interno a JTextField **
            comentarioField.setBorder(BorderFactory.createCompoundBorder(fieldLineBorder, fieldPadding));


            // Habilita/Deshabilita el comentario según el estado del checkbox
            checkBox.addActionListener(ev -> comentarioField.setEnabled(checkBox.isSelected()));

            // Restaurar estado si es un reintento
            for (String[] motivo : motivosReintento) {
                if (motivo[0].equals(tipo)) {
                    checkBox.setSelected(true);
                    comentarioField.setText(motivo[1]);
                    comentarioField.setEnabled(true);
                    break;
                }
            }

            motivoCheckBoxes.put(tipo, checkBox);
            comentarioTextFields.put(tipo, comentarioField);

            // Añadir al panel de motivos (usando wrap para el salto de línea después del textfield)
            motivosPanel.add(checkBox);
            motivosPanel.add(new JLabel(LABEL_COMENTARIO));
            motivosPanel.add(comentarioField, "wrap");
        }

        motivosPanel.revalidate();
        motivosPanel.repaint();
        cardLayout.show(mainPanel, "motivos");
        confirmarMotivosBtn.setEnabled(true);
    }

    private void tomarComentarioPorMotivoTipo(ActionEvent e) {
        confirmarMotivosBtn.setEnabled(false);
        List<String[]> motivosSeleccionados = new ArrayList<>();

        // Recolectamos la información actual de la UI (para validar y/o guardar en caso de error)
        for (Map.Entry<String, JCheckBox> entry : motivoCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                String motivoTipo = entry.getKey();
                String comentario = comentarioTextFields.get(motivoTipo).getText();
                motivosSeleccionados.add(new String[]{motivoTipo, comentario});
            }
        }

        // Guardamos el estado actual por si hay que reintentar
        motivosPendientesReintento = motivosSeleccionados;

        gestor.tomarMotivosFueraDeServicio(motivosSeleccionados);

        // La habilitación/deshabilitación final se hace en los métodos OK/Error
    }

    public void motivosFueraDeServicioOK() {
        confirmarMotivosBtn.setEnabled(false);
        esperandoReintento = false;
        motivosPendientesReintento = null;
    }

    public void solicitarConfirmacionCierreOrden() {
        cardLayout.show(mainPanel, "confirmacion");
        confirmarCierreBtn.setVisible(true);
        cancelarCierreBtn.setVisible(true);
        confirmarCierreBtn.setEnabled(true);
        cancelarCierreBtn.setEnabled(true);

        // Deshabilitamos componentes de las vistas anteriores (solo si el gestor nos llevó a confirmación)
        ordenesTable.setEnabled(false);
        observacionTextArea.setEnabled(false);
        fueraServicioCheckBox.setEnabled(false);
        confirmarObservacionBtn.setEnabled(false);
        confirmarMotivosBtn.setEnabled(false);
    }

    private void tomarConfirmacionCierre(boolean confirmacionFinal) {
        confirmarCierreBtn.setEnabled(false);
        cancelarCierreBtn.setEnabled(false);

        boolean confirmacionCierreOrden = gestor.tomarConfirmacionCierreOrden(confirmacionFinal);

        if (confirmacionCierreOrden) {
            if (confirmacionFinal) {
                // Flujo Exitoso
                confirmarCierreBtn.setVisible(false);
                cancelarCierreBtn.setVisible(false);
                cardLayout.show(mainPanel, "cierreExitoso");
            } else {
                // Cancelación
                opcionCerrarOrdenDeInspeccion(); // Reiniciar el proceso
            }
        } else {
            // Manejo de Reintento y Error
            esperandoReintento = true;

            if (!gestor.esValidacionObservacionOk()) {
                // Error en Observación
                cardLayout.show(mainPanel, "observacion");
                JOptionPane.showMessageDialog(this, MENSAJE_ERROR_OBSERVACION, "Error de Observación", JOptionPane.ERROR_MESSAGE);
                solicitarObservacionCierreOrden(); // Recarga con datos pendientes

            } else if (gestor.esPonerSismografoFueraDeServicio() && (!gestor.esValidacionComentariosMotivosOk() || !gestor.esValidacionSelecMotivoOk())) {
                // Error en Motivos
                cardLayout.show(mainPanel, "motivos");
                solicitarMotivosFueraDeServicio(motivosTipoFueraServicio); // Recarga con datos pendientes

                if (!gestor.esValidacionComentariosMotivosOk()) {
                    JOptionPane.showMessageDialog(this, MENSAJE_ERROR_COMENTARIOS, "Error de Comentarios", JOptionPane.ERROR_MESSAGE);
                } else if (!gestor.esValidacionSelecMotivoOk()) {
                    JOptionPane.showMessageDialog(this, MENSAJE_ERROR_SELECCION_MOTIVO, "Error de Motivos", JOptionPane.ERROR_MESSAGE);
                }
                
                // Los botones deben volver a habilitarse aquí para permitir el reintento
                confirmarMotivosBtn.setEnabled(true); 
                
            } else {
                // Error genérico o desconocido
                JOptionPane.showMessageDialog(this, MENSAJE_ERROR_CONFIRMACION, "Error en Cierre", JOptionPane.ERROR_MESSAGE);
                cardLayout.show(mainPanel, "confirmacion");
                confirmarCierreBtn.setEnabled(true); // Se vuelven a habilitar los botones de confirmación
                cancelarCierreBtn.setEnabled(true);
            }
        }
    }

    private void volverInicioClick() {
        gestor.nuevoCierreOrdenInspeccion();
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}