package com.mycompany.ppai.boundaries;

import com.mycompany.ppai.controllers.GestorCierreOrdenInspeccion;
import com.google.gson.JsonObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PantallaCierreOrdenInspeccion extends JFrame {
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
    private static final String MENSAJE_ERROR_OBSERVACION = "Por favor, corrija la observación.";
    private static final String MENSAJE_ERROR_COMENTARIOS = "Por favor, complete los comentarios de los motivos.";
    private static final String MENSAJE_ERROR_SELECCION_MOTIVO = "Debe seleccionar al menos un motivo.";
    private static final String MENSAJE_ERROR_CONFIRMACION = "Error al confirmar el cierre. Por favor, revise la información.";

    private final GestorCierreOrdenInspeccion gestor;
    private final JPanel mainPanel;
    private final CardLayout cardLayout;
    private JLabel loadingLabel;
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
    private final Map<String, Integer> ordenMap = new HashMap<>();
    private final Map<String, JCheckBox> motivoCheckBoxes = new HashMap<>();
    private final Map<String, JTextField> comentarioTextFields = new HashMap<>();
    private int selectedOrderNumber = -1;
    private int lastSelectedRow = -1;
    private boolean esperandoReintento = false;
    private String observacionPendienteReintento = null;
    private boolean fueraServicioPendienteReintento = false;
    private List<String[]> motivosPendientesReintento = null;
    private JPanel cierreExitosoPanel;
    private JButton volverInicioBtn;
    private JPanel sinOrdenesPanel;
    private JLabel sinOrdenesLabel;
    private List<String> tiposMotivoFueraDeServicioActual = new ArrayList<>();

    public PantallaCierreOrdenInspeccion(GestorCierreOrdenInspeccion gestor) {
        this.gestor = gestor;
        setTitle(TITULO_VENTANA);
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(crearPanelCarga(), "loading");
        mainPanel.add(crearPanelSinOrdenes(), "sinOrdenes");
        mainPanel.add(crearPanelOrdenes(), "ordenes");
        mainPanel.add(crearPanelObservacion(), "observacion");
        mainPanel.add(crearPanelMotivos(), "motivos");
        mainPanel.add(crearPanelConfirmacion(), "confirmacion");
        mainPanel.add(crearPanelCierreExitoso(), "cierreExitoso");
        cierreExitosoPanel.setVisible(false);

        add(mainPanel);
        cardLayout.show(mainPanel, "loading");
        setVisible(true);
    }

    private JPanel crearPanelCarga() {
        loadingLabel = new JLabel(LABEL_CARGANDO);
        JPanel loadingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loadingPanel.add(loadingLabel);
        return loadingPanel;
    }

    private JPanel crearPanelSinOrdenes() {
        sinOrdenesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        sinOrdenesLabel = new JLabel(LABEL_SIN_ORDENES);
        sinOrdenesPanel.add(sinOrdenesLabel);
        return sinOrdenesPanel;
    }

    private JPanel crearPanelOrdenes() {
        ordenesPanel = new JPanel(new BorderLayout());
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
        ordenesTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        ordenesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordenesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ordenesTable.getSelectedRow();
                actualizarSeleccionTabla(selectedRow);
            }
        });
        JScrollPane ordenesScrollPane = new JScrollPane(ordenesTable);
        seleccionarOrdenBtn = new JButton(BOTON_SELECCIONAR_ORDEN);
        seleccionarOrdenBtn.addActionListener(this::seleccionarOrdenClick);
        JPanel seleccionarOrdenPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        seleccionarOrdenPanel.add(seleccionarOrdenBtn);
        ordenesPanel.add(new JLabel(LABEL_SELECCIONAR_ORDEN, SwingConstants.CENTER), BorderLayout.NORTH);
        ordenesPanel.add(ordenesScrollPane, BorderLayout.CENTER);
        ordenesPanel.add(seleccionarOrdenPanel, BorderLayout.SOUTH);
        return ordenesPanel;
    }

    private void actualizarSeleccionTabla(int selectedRow) {
        if (lastSelectedRow != -1 && selectedRow != lastSelectedRow) {
            ordenesTableModel.setValueAt(false, lastSelectedRow, 0);
        }
        if (selectedRow != -1) {
            ordenesTableModel.setValueAt(true, selectedRow, 0);
            lastSelectedRow = selectedRow;
        } else {
            lastSelectedRow = -1;
        }
    }

    private JPanel crearPanelObservacion() {
        observacionPanel = new JPanel();
        observacionPanel.setLayout(new BoxLayout(observacionPanel, BoxLayout.Y_AXIS));
        observacionTextArea = new JTextArea(3, 40);
        JScrollPane observacionScrollPane = new JScrollPane(observacionTextArea);
        fueraServicioCheckBox = new JCheckBox(CHECKBOX_FUERA_SERVICIO);
        confirmarObservacionBtn = new JButton(BOTON_CONFIRMAR_OBSERVACION);
        confirmarObservacionBtn.addActionListener(this::confirmarObservacionClick);
        observacionPanel.add(new JLabel(LABEL_OBSERVACION, SwingConstants.CENTER));
        observacionPanel.add(observacionScrollPane);
        observacionPanel.add(fueraServicioCheckBox);
        observacionPanel.add(confirmarObservacionBtn);
        return observacionPanel;
    }

    private JPanel crearPanelMotivos() {
        motivosPanelContainer = new JPanel(new BorderLayout());
        motivosPanel = new JPanel();
        motivosPanel.setLayout(new BoxLayout(motivosPanel, BoxLayout.Y_AXIS));
        JScrollPane motivosScrollPane = new JScrollPane(motivosPanel);
        confirmarMotivosBtn = new JButton(BOTON_CONFIRMAR_MOTIVOS);
        confirmarMotivosBtn.addActionListener(this::confirmarMotivosClick);
        motivosPanelContainer.add(new JLabel(LABEL_MOTIVOS, SwingConstants.CENTER), BorderLayout.NORTH);
        motivosPanelContainer.add(motivosScrollPane, BorderLayout.CENTER);
        motivosPanelContainer.add(confirmarMotivosBtn, BorderLayout.SOUTH);
        return motivosPanelContainer;
    }

    private JPanel crearPanelConfirmacion() {
        confirmacionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        confirmarCierreBtn = new JButton(BOTON_CONFIRMAR_CIERRE);
        cancelarCierreBtn = new JButton(BOTON_CANCELAR);
        confirmarCierreBtn.addActionListener(e -> confirmarCierreFinalClick(true));
        cancelarCierreBtn.addActionListener(e -> confirmarCierreFinalClick(false));
        confirmacionPanel.add(new JLabel(LABEL_CONFIRMACION, SwingConstants.CENTER));
        confirmacionPanel.add(confirmarCierreBtn);
        confirmacionPanel.add(cancelarCierreBtn);
        return confirmacionPanel;
    }

    private JPanel crearPanelCierreExitoso() {
        cierreExitosoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel cierreExitosoLabel = new JLabel(LABEL_CIERRE_EXITOSO);
        volverInicioBtn = new JButton(BOTON_VOLVER_INICIO);
        volverInicioBtn.addActionListener(e -> volverInicioClick());
        cierreExitosoPanel.add(cierreExitosoLabel);
        cierreExitosoPanel.add(volverInicioBtn);
        return cierreExitosoPanel;
    }

    public void opcionCerrarOrdenDeInspeccion() {
        cardLayout.show(mainPanel, "loading");
        habilitarBotonesConfirmacionCancelacion(true);
        limpiarCampos(); // Llamar al método para limpiar los campos
        gestor.nuevoCierreOrdenInspeccion();
        resetearEstadoReintento();
    }

    private void limpiarCampos() {
        // Limpiar la selección de la tabla de órdenes
        ordenesTable.clearSelection();
        if (ordenesTableModel.getRowCount() > 0) {
            for (int i = 0; i < ordenesTableModel.getRowCount(); i++) {
                ordenesTableModel.setValueAt(false, i, 0); // Desmarcar checkboxes
            }
        }
        lastSelectedRow = -1;
        selectedOrderNumber = -1;

        // Limpiar el área de texto de la observación
        observacionTextArea.setText("");
        fueraServicioCheckBox.setSelected(false);

        // Limpiar los motivos seleccionados
        motivosPanel.removeAll();
        motivoCheckBoxes.clear();
        comentarioTextFields.clear();
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
        tiposMotivoFueraDeServicioActual.clear();
    }

    public void mostrarInfoOrdenesInspeccion(List<JsonObject> ordenesInfo) {
        ordenMap.clear();
        ordenesTableModel.setRowCount(0);
        lastSelectedRow = -1;

        if (ordenesInfo.isEmpty()) {
            cardLayout.show(mainPanel, "sinOrdenes");
        } else {
            for (JsonObject info : ordenesInfo) {
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

    private void seleccionarOrdenClick(ActionEvent e) {
        int selectedRow = ordenesTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedOrderNumber = (Integer) ordenesTableModel.getValueAt(selectedRow, 1);
            gestor.tomarSelecOrdenInspeccion(selectedOrderNumber);
            ordenesTable.setEnabled(false);
            seleccionarOrdenBtn.setEnabled(false);
            cardLayout.show(mainPanel, "observacion");
        } else {
            JOptionPane.showMessageDialog(this, MENSAJE_SELECCIONAR_ORDEN, TITULO_ADVERTENCIA, JOptionPane.WARNING_MESSAGE);
        }
    }

    public void solicitarObservacionCierreOrden() {
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

    private void confirmarObservacionClick(ActionEvent e) {
        String observacion = observacionTextArea.getText();
        boolean fueraServicio = fueraServicioCheckBox.isSelected();
        gestor.tomarObservacionCierreOrden(observacion, fueraServicio);
        observacionTextArea.setEnabled(false);
        fueraServicioCheckBox.setEnabled(false);
        confirmarObservacionBtn.setEnabled(false);
        esperandoReintento = false;
        observacionPendienteReintento = null;
        fueraServicioPendienteReintento = false;
    }

    public void solicitarMotivosFueraDeServicio(List<String> tiposMotivo) {
        this.tiposMotivoFueraDeServicioActual = tiposMotivo;
        motivosPanel.removeAll();
        motivoCheckBoxes.clear();
        comentarioTextFields.clear();
        motivosPanel.setLayout(new BoxLayout(motivosPanel, BoxLayout.Y_AXIS));

        List<String[]> motivosReintento = (esperandoReintento && motivosPendientesReintento != null) ? motivosPendientesReintento : new ArrayList<>();

        for (String tipo : tiposMotivo) {
            JPanel motivoFila = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JCheckBox checkBox = new JCheckBox(tipo);
            JTextField comentarioField = new JTextField(30);
            comentarioField.setEnabled(false);
            checkBox.addActionListener(ev -> comentarioField.setEnabled(checkBox.isSelected()));

            for (String[] motivo : motivosReintento) {
                if (motivo[0].equals(tipo)) {
                    checkBox.setSelected(true);
                    comentarioField.setText(motivo[1]);
                    comentarioField.setEnabled(true);
                    break;
                }
            }

            motivoFila.add(checkBox);
            motivoFila.add(new JLabel(LABEL_COMENTARIO));
            motivoFila.add(comentarioField);

            motivoCheckBoxes.put(tipo, checkBox);
            comentarioTextFields.put(tipo, comentarioField);
            motivosPanel.add(motivoFila);
        }
        revalidate();
        repaint();
        cardLayout.show(mainPanel, "motivos");
    }

   private void confirmarMotivosClick(ActionEvent e) {
        List<String[]> motivosSeleccionados = new ArrayList<>();
        for (Map.Entry<String, JCheckBox> entry : motivoCheckBoxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                String motivoTipo = entry.getKey();
                String comentario = comentarioTextFields.get(motivoTipo).getText();
                motivosSeleccionados.add(new String[]{motivoTipo, comentario});
            }
        }
        gestor.tomarMotivosFueraDeServicio(motivosSeleccionados);
        cardLayout.show(mainPanel, "confirmacion");
    }

    public void solicitarConfirmacionCierreOrden() {
        cardLayout.show(mainPanel, "confirmacion");
        confirmarCierreBtn.setVisible(true); // Asegurar que sean visibles
        cancelarCierreBtn.setVisible(true);
        confirmarCierreBtn.setEnabled(true);
        cancelarCierreBtn.setEnabled(true);
        ordenesPanel.setEnabled(false);
        observacionPanel.setEnabled(false);
        motivosPanelContainer.setEnabled(false);
    }

    private void confirmarCierreFinalClick(boolean confirmacionFinal) {
        boolean resultado = gestor.tomarConfirmacionCierreOrden(confirmacionFinal);
        if (resultado) {
            if (confirmacionFinal) {
                // Ocultar botones de confirmar y cancelar
                confirmarCierreBtn.setVisible(false);
                cancelarCierreBtn.setVisible(false);
                cardLayout.show(mainPanel, "cierreExitoso");
            } else {
                opcionCerrarOrdenDeInspeccion(); // Reiniciar el proceso
            }
        } else {
            // Si la confirmación falla, volvemos a la pantalla donde ocurrió el error.
            // Esto podría ser la observación o los motivos.
            if (!gestor.esValidacionObservacionOk()) {
                cardLayout.show(mainPanel, "observacion");
                solicitarObservacionCierreOrden();
                JOptionPane.showMessageDialog(this, MENSAJE_ERROR_OBSERVACION, "Error", JOptionPane.ERROR_MESSAGE);
            } else if (gestor.esPonerSismografoFueraDeServicio() && (!gestor.esValidacionComentariosMotivosOk() || !gestor.esValidacionSelecMotivoOk())) {
                cardLayout.show(mainPanel, "motivos");
                solicitarMotivosFueraDeServicio(tiposMotivoFueraDeServicioActual);
                if (!gestor.esValidacionComentariosMotivosOk()) {
                    JOptionPane.showMessageDialog(this, MENSAJE_ERROR_COMENTARIOS, "Error", JOptionPane.ERROR_MESSAGE);
                } else if (!gestor.esValidacionSelecMotivoOk()) {
                    JOptionPane.showMessageDialog(this, MENSAJE_ERROR_SELECCION_MOTIVO, "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Si no se pudo determinar la causa específica, podrías mostrar un mensaje genérico
                JOptionPane.showMessageDialog(this, MENSAJE_ERROR_CONFIRMACION, "Error", JOptionPane.ERROR_MESSAGE);
                cardLayout.show(mainPanel, "confirmacion"); // Volver a la confirmación por defecto
            }
            esperandoReintento = true; // Marcar para posible reintento
            observacionPendienteReintento = observacionTextArea.getText();
            fueraServicioPendienteReintento = fueraServicioCheckBox.isSelected();
            List<String[]> motivosPendientes = new ArrayList<>();
            for (Map.Entry<String, JCheckBox> entry : motivoCheckBoxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    String motivoTipo = entry.getKey();
                    String comentario = comentarioTextFields.get(motivoTipo).getText();
                    motivosPendientes.add(new String[]{motivoTipo, comentario});
                }
            }
            motivosPendientesReintento = motivosPendientes;
        }
    }

    private void volverInicioClick() {
        gestor.nuevoCierreOrdenInspeccion();
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }
}