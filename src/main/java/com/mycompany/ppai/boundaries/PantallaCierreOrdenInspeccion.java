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
    private GestorCierreOrdenInspeccion gestor;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JLabel loadingLabel;
    private JPanel ordenesPanel;
    private JTable ordenesTable;
    private DefaultTableModel ordenesTableModel;
    private ButtonGroup ordenSelectionGroup;
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
    private Map<String, Integer> ordenMap = new HashMap<>();
    private Map<String, JCheckBox> motivoCheckBoxes = new HashMap<>();
    private Map<String, JTextField> comentarioTextFields = new HashMap<>();
    private int selectedOrderNumber = -1;

    public PantallaCierreOrdenInspeccion(GestorCierreOrdenInspeccion gestor) {
        this.gestor = gestor;
        setTitle("Cierre de Orden de Inspección");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panel de carga
        loadingLabel = new JLabel("Cargando órdenes de inspección...");
        JPanel loadingPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        loadingPanel.add(loadingLabel);
        mainPanel.add(loadingPanel, "loading");

        // Panel de selección de órdenes
        ordenesPanel = new JPanel(new BorderLayout());
        ordenesTableModel = new DefaultTableModel(new Object[]{"Seleccionar", "Número", "Sismógrafo", "Fecha Fin"}, 0) {
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
        JScrollPane ordenesScrollPane = new JScrollPane(ordenesTable);
        seleccionarOrdenBtn = new JButton("Seleccionar Orden");
        seleccionarOrdenBtn.addActionListener(this::seleccionarOrdenClick);
        JPanel seleccionarOrdenPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        seleccionarOrdenPanel.add(seleccionarOrdenBtn);
        ordenesPanel.add(new JLabel("Seleccione una orden de inspección:", SwingConstants.CENTER), BorderLayout.NORTH);
        ordenesPanel.add(ordenesScrollPane, BorderLayout.CENTER);
        ordenesPanel.add(seleccionarOrdenPanel, BorderLayout.SOUTH);
        mainPanel.add(ordenesPanel, "ordenes");

        // Panel de observación
        observacionPanel = new JPanel();
        observacionPanel.setLayout(new BoxLayout(observacionPanel, BoxLayout.Y_AXIS));
        observacionTextArea = new JTextArea(3, 40);
        JScrollPane observacionScrollPane = new JScrollPane(observacionTextArea);
        fueraServicioCheckBox = new JCheckBox("¿Se desea registrar el sismógrafo como fuera de servicio?");
        confirmarObservacionBtn = new JButton("Confirmar Observación");
        confirmarObservacionBtn.addActionListener(this::confirmarObservacionClick);
        observacionPanel.add(new JLabel("Ingrese la observación de cierre:", SwingConstants.CENTER));
        observacionPanel.add(observacionScrollPane);
        observacionPanel.add(fueraServicioCheckBox);
        observacionPanel.add(confirmarObservacionBtn);
        mainPanel.add(observacionPanel, "observacion");

        // Panel de motivos
        motivosPanelContainer = new JPanel(new BorderLayout());
        motivosPanel = new JPanel();
        motivosPanel.setLayout(new BoxLayout(motivosPanel, BoxLayout.Y_AXIS));
        JScrollPane motivosScrollPane = new JScrollPane(motivosPanel);
        confirmarMotivosBtn = new JButton("Confirmar Motivos");
        confirmarMotivosBtn.addActionListener(this::confirmarMotivosClick);
        motivosPanelContainer.add(new JLabel("Seleccione los motivos (y comentarios):", SwingConstants.CENTER), BorderLayout.NORTH);
        motivosPanelContainer.add(motivosScrollPane, BorderLayout.CENTER);
        motivosPanelContainer.add(confirmarMotivosBtn, BorderLayout.SOUTH);
        mainPanel.add(motivosPanelContainer, "motivos");

        // Panel de confirmación final
        confirmacionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        confirmarCierreBtn = new JButton("Confirmar Cierre");
        cancelarCierreBtn = new JButton("Cancelar");
        confirmarCierreBtn.addActionListener(this::confirmarCierreFinalClick);
        cancelarCierreBtn.addActionListener(e -> gestor.tomarConfirmacionCierreOrden(false));
        confirmacionPanel.add(new JLabel("¿Desea confirmar el cierre de la orden?", SwingConstants.CENTER));
        confirmacionPanel.add(confirmarCierreBtn);
        confirmacionPanel.add(cancelarCierreBtn);
        mainPanel.add(confirmacionPanel, "confirmacion");

        add(mainPanel);
        cardLayout.show(mainPanel, "loading");
        setVisible(true);
    }

    public void opcionCerrarOrdenDeInspeccion() {
        cardLayout.show(mainPanel, "loading");
        gestor.nuevoCierreOrdenInspeccion();
    }

    public void mostrarInfoOrdenesInspeccion(List<JsonObject> ordenesInfo) {
        ordenMap.clear();
        ordenesTableModel.setRowCount(0);
        ordenSelectionGroup = new ButtonGroup();

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
        revalidate();
        repaint();
    }

    private void seleccionarOrdenClick(ActionEvent e) {
        int selectedRow = -1;
        for (int i = 0; i < ordenesTableModel.getRowCount(); i++) {
            Boolean selected = (Boolean) ordenesTableModel.getValueAt(i, 0);
            if (selected != null && selected) {
                if (selectedRow != -1) {
                    JOptionPane.showMessageDialog(this, "Por favor, seleccione solo una orden.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                selectedRow = i;
            }
        }

        if (selectedRow != -1) {
            selectedOrderNumber = (Integer) ordenesTableModel.getValueAt(selectedRow, 1);
            tomarSelecOrdenInspeccion(selectedOrderNumber);
            // Deshabilitar la tabla y el botón después de la selección
            ordenesTable.setEnabled(false);
            seleccionarOrdenBtn.setEnabled(false);
            cardLayout.show(mainPanel, "observacion");
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una orden.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void tomarSelecOrdenInspeccion(int numeroOrden) {
        this.selectedOrderNumber = numeroOrden;
        gestor.tomarSelecOrdenInspeccion(numeroOrden);
    }

    public void solicitarObservacionCierreOrden() {
        // El panel de observación ya debería estar visible
        observacionTextArea.setEnabled(true);
        fueraServicioCheckBox.setEnabled(true);
        confirmarObservacionBtn.setEnabled(true);
    }

    private void confirmarObservacionClick(ActionEvent e) {
        String observacion = observacionTextArea.getText();
        boolean fueraServicio = fueraServicioCheckBox.isSelected();
        tomarObservacionCierreOrden(observacion, fueraServicio);
        observacionTextArea.setEnabled(false);
        fueraServicioCheckBox.setEnabled(false);
        confirmarObservacionBtn.setEnabled(false);
        if (fueraServicio) {
            cardLayout.show(mainPanel, "motivos");
        } else {
            cardLayout.show(mainPanel, "confirmacion");
        }
    }

    public void tomarObservacionCierreOrden(String observacion, boolean ponerFueraDeServicio) {
        gestor.tomarObservacionCierreOrden(observacion, ponerFueraDeServicio);
    }

    public void solicitarMotivosFueraDeServicio(List<String> tiposMotivo) {
        motivosPanel.removeAll();
        motivoCheckBoxes.clear();
        comentarioTextFields.clear();
        motivosPanel.setLayout(new BoxLayout(motivosPanel, BoxLayout.Y_AXIS));

        for (String tipo : tiposMotivo) {
            JPanel motivoFila = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JCheckBox checkBox = new JCheckBox(tipo);
            JTextField comentarioField = new JTextField(30);
            comentarioField.setEnabled(false);
            checkBox.addActionListener(ev -> comentarioField.setEnabled(checkBox.isSelected()));

            motivoFila.add(checkBox);
            motivoFila.add(new JLabel("Comentario:"));
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
        tomarMotivosFueraDeServicio(motivosSeleccionados);
        cardLayout.show(mainPanel, "confirmacion");
    }

    public void tomarMotivosFueraDeServicio(List<String[]> motivosSeleccionados) {
        gestor.tomarMotivosFueraDeServicio(motivosSeleccionados);
    }

    public void solicitarConfirmacionCierreOrden() {
        cardLayout.show(mainPanel, "confirmacion");
        confirmarCierreBtn.setEnabled(true);
        cancelarCierreBtn.setEnabled(true);
        // Deshabilitar paneles anteriores si no lo están ya
        ordenesPanel.setEnabled(false);
        observacionPanel.setEnabled(false);
        motivosPanelContainer.setEnabled(false);
    }

    private void confirmarCierreFinalClick(ActionEvent e) {
        tomarConfirmacionCierreOrden(true);
        confirmarCierreBtn.setEnabled(false);
        cancelarCierreBtn.setEnabled(false);
    }

    public void tomarConfirmacionCierreOrden(boolean confirmacion) {
        gestor.tomarConfirmacionCierreOrden(confirmacion);
    }

    public void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje);
    }

    public void habilitarVentana() {
        this.setEnabled(true);
    }

    public void setGestorCierreOrdenInspeccion(GestorCierreOrdenInspeccion gestor) {
        this.gestor = gestor;
    }
}