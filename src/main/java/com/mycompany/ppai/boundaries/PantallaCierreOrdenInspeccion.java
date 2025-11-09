package com.mycompany.ppai.boundaries;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;

import com.google.gson.JsonObject;
import com.mycompany.ppai.controllers.GestorCierreOrdenInspeccion;

import net.miginfocom.swing.MigLayout;

public class PantallaCierreOrdenInspeccion extends JFrame {
	// Constantes de texto (Se mantienen sin cambios)
	private static final String TITULO_VENTANA = "Cierre de Orden de Inspección";
	private static final String LABEL_CARGANDO = "Cargando órdenes de inspección...";
	private static final String LABEL_PROCESANDO = "Procesando cierre de orden. Por favor espere..."; 

	private static final String LABEL_SIN_ORDENES = "No se encontraron órdenes de inspección completamente realizadas.";
	private static final String BOTON_SELECCIONAR_ORDEN = "Seleccionar Orden";
	private static final String LABEL_SELECCIONAR_ORDEN = "Seleccione una orden de inspección:";
	private static final String LABEL_OBSERVACION = "Ingrese la observación de cierre:";
	private static final String CHECKBOX_FUERA_SERVICIO = "¿Se desea registrar el sismógrafo como fuera de servicio?";
	private static final String BOTON_CONFIRMAR_OBSERVACION = "Confirmar Observación";
	private static final String LABEL_MOTIVOS = "Seleccione los motivos e ingrese un comentario:";
	private static final String LABEL_COMENTARIO = "Comentario:";
	private static final String BOTON_CONFIRMAR_MOTIVOS = "Confirmar Motivos";
	private static final String LABEL_CONFIRMACION = "¿Desea confirmar el cierre de la orden?";
	private static final String BOTON_CONFIRMAR_CIERRE = "Confirmar Cierre";
	private static final String BOTON_CANCELAR = "Cancelar";
	private static final String LABEL_CIERRE_EXITOSO = "Orden de inspección cerrada exitosamente.";
	private static final String BOTON_VOLVER_INICIO = "Volver al Inicio";
	private static final String BOTON_REGRESAR = "← Regresar";
	private static final String COLUMNA_SELECCIONAR = "Seleccionar";
	private static final String COLUMNA_NUMERO = "Nro. de orden";
	private static final String COLUMNA_ESTACION = "Estación";
	private static final String COLUMNA_SISMOGRAFO = "Sismógrafo";
	private static final String COLUMNA_FECHA_FIN = "Fecha de finalización";
	private static final String MENSAJE_SELECCIONAR_ORDEN = "Por favor, seleccione una orden.";
	private static final String TITULO_ADVERTENCIA = "Advertencia";
	private static final String MENSAJE_ERROR_OBSERVACION = "Por favor, corrija la observación. (La observación no debe estar vacía y debe ser coherente)";
	private static final String MENSAJE_ERROR_COMENTARIOS = "Por favor, complete los comentarios de los motivos seleccionados.";
	private static final String MENSAJE_ERROR_SELECCION_MOTIVO = "Debe seleccionar al menos un motivo si marca el sismógrafo como 'fuera de servicio'.";
	private static final String MENSAJE_ERROR_CONFIRMACION = "Error al confirmar el cierre. Por favor, revise la información.";

	// Constantes de Estilo
	private static final Color COLOR_CONFIRMAR = new Color(0x0276aa); // Color Azul/Cyan
	private static final Color COLOR_TEXTO_CONFIRMAR = Color.WHITE;

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
    private JPanel procesandoPanel;

	// Estado de la pantalla
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

		mainPanel.add(crearPanelCarga(), "loading");
		mainPanel.add(crearPanelProcesando(), "procesando"); 
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

	// --- MÉTODOS DE UTILIDAD DEL UI Y ESTILOS ---

	private JButton crearBotonRegresar(String targetCard) {
		JButton btn = new JButton(BOTON_REGRESAR);
		
		btn.addActionListener(e -> {
			if ("confirmacion".equals(targetCard)) {
				regresarDesdeConfirmacion();
			} else {
				regresarClick(targetCard);
			}
		});
		return btn;
	}

	private JPanel crearPanelCarga() {
		JPanel loadingPanel = new JPanel(new MigLayout("insets 50, align center, fill"));
		JLabel loadingLabel = new JLabel(LABEL_CARGANDO, SwingConstants.CENTER);
		loadingLabel.setFont(loadingLabel.getFont().deriveFont(Font.BOLD, 16f));
		loadingPanel.add(loadingLabel, "span, wrap, align center");
		return loadingPanel;
	}
    
	private JPanel crearPanelProcesando() {
		procesandoPanel = new JPanel(new MigLayout("insets 50, align center, fill"));
		JLabel procesandoLabel = new JLabel(LABEL_PROCESANDO, SwingConstants.CENTER);
		procesandoLabel.setFont(procesandoLabel.getFont().deriveFont(Font.BOLD, 16f));
        // Agregar un poco de margen visual o un icono si estuviera disponible
		procesandoPanel.add(procesandoLabel, "span, wrap, align center, gapy 20"); 
		return procesandoPanel;
	}

	private JPanel crearPanelSinOrdenes() {
		JPanel sinOrdenesPanel = new JPanel(new MigLayout("insets 50, align center, fill"));
		JLabel sinOrdenesLabel = new JLabel(LABEL_SIN_ORDENES, SwingConstants.CENTER);
		sinOrdenesLabel.setFont(sinOrdenesLabel.getFont().deriveFont(Font.BOLD, 16f));
		sinOrdenesPanel.add(sinOrdenesLabel, "span, wrap, align center");
		return sinOrdenesPanel;
	}

	private JPanel crearPanelOrdenes() {
		ordenesPanel = new JPanel(
				new MigLayout("fill, insets 25 30 25 30", "[grow, fill]", "[pref!][grow, fill][pref!]"));

		JLabel headerLabel = new JLabel(LABEL_SELECCIONAR_ORDEN, SwingConstants.LEFT);
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));

		ordenesTableModel = new DefaultTableModel(
				new Object[] { COLUMNA_SELECCIONAR, COLUMNA_NUMERO, COLUMNA_ESTACION, COLUMNA_SISMOGRAFO, COLUMNA_FECHA_FIN }, 0) {
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

		centrarValoresTabla(ordenesTable);

		ordenesTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedRow = ordenesTable.getSelectedRow();
				actualizarSeleccionTabla(selectedRow);
			}
		});

		JScrollPane ordenesScrollPane = new JScrollPane(ordenesTable);
		seleccionarOrdenBtn = new JButton(BOTON_SELECCIONAR_ORDEN);
		seleccionarOrdenBtn.addActionListener(this::tomarSelecOrdenInspeccion);

		JPanel buttonPanel = new JPanel(new MigLayout("align center"));
		buttonPanel.add(seleccionarOrdenBtn, "align center");

		ordenesPanel.add(headerLabel, "wrap, gapy 5 5");
		ordenesPanel.add(ordenesScrollPane, "grow, push, wrap");
		ordenesPanel.add(buttonPanel, "align center, wrap , gapy 10 15");

		return ordenesPanel;
	}

	private void centrarValoresTabla(JTable table) {
		javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
	}

	private JPanel crearPanelObservacion() {
		observacionPanel = new JPanel(new MigLayout("fill, insets 25", "[grow, fill]", "[pref!][pref!][grow, fill][pref!][pref!]"));

		observacionPanel.add(crearBotonRegresar("ordenes"), "align left, wrap, gapy 0 10, w pref!");

		JLabel headerLabel = new JLabel("<html>" + LABEL_OBSERVACION + " <span style='color:red;'>*</span></html>", SwingConstants.LEFT);
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));

		observacionTextArea = new JTextArea(5, 40);
		observacionTextArea.setLineWrap(true);
		observacionTextArea.setWrapStyleWord(true);

		Border innerPadding = BorderFactory.createEmptyBorder(8, 8, 8, 8);
		Border lineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);
		observacionTextArea.setBorder(BorderFactory.createCompoundBorder(lineBorder, innerPadding));

		JScrollPane observacionScrollPane = new JScrollPane(observacionTextArea);

		fueraServicioCheckBox = new JCheckBox(CHECKBOX_FUERA_SERVICIO);

		confirmarObservacionBtn = new JButton(BOTON_CONFIRMAR_OBSERVACION);
		confirmarObservacionBtn.addActionListener(this::tomarObservacionCierreOrden);

		JPanel buttonPanel = new JPanel(new MigLayout("align center"));
		buttonPanel.add(confirmarObservacionBtn, "align center");

		observacionPanel.add(headerLabel, "wrap, gapy 5 5");
		observacionPanel.add(observacionScrollPane, "grow, push, wrap, hmin 100, gapy 5 5");
		observacionPanel.add(fueraServicioCheckBox, "wrap, gapy 0 10");
		observacionPanel.add(buttonPanel, "align center, wrap, gapy 0 15");

		return observacionPanel;
	}

	private JPanel crearPanelMotivos() {
		motivosPanelContainer = new JPanel(new MigLayout("fill, insets 25", "[grow, fill]", "[pref!][pref!][grow, fill][pref!]"));

		motivosPanelContainer.add(crearBotonRegresar("observacion"), "align left, wrap, gapy 0 10, w pref!");

		JLabel headerLabel = new JLabel("<html>" + LABEL_MOTIVOS + " <span style='color:red;'>*</span></html>", SwingConstants.LEFT);
		headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD, 16f));

		motivosPanel = new JPanel(new MigLayout("wrap 3, fillx, insets 10 0 0 0, gapy 10", "[pref!]30[pref!][grow, fill]"));

		JScrollPane motivosScrollPane = new JScrollPane(motivosPanel);
		motivosScrollPane.setBorder(BorderFactory.createEmptyBorder());
		motivosScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		confirmarMotivosBtn = new JButton(BOTON_CONFIRMAR_MOTIVOS);
		confirmarMotivosBtn.addActionListener(this::tomarComentarioPorMotivoTipo);

		JPanel buttonPanel = new JPanel(new MigLayout("align center"));
		buttonPanel.add(confirmarMotivosBtn, "align center");

		motivosPanelContainer.add(headerLabel, "wrap, gapy 5");
		motivosPanelContainer.add(motivosScrollPane, "grow, push, wrap, gapy 10 20");
		motivosPanelContainer.add(buttonPanel, "align center, wrap, gapy 0 15");

		return motivosPanelContainer;
	}

	private JPanel crearPanelConfirmacion() {

		confirmacionPanel = new JPanel(new MigLayout("fill, insets 25 50 25 50", "[grow, fill]", "[pref!][grow, fill][pref!]"));

		confirmacionPanel.add(crearBotonRegresar("confirmacion"), "align left, wrap, gapy 0 0, w pref!");

		JPanel contentPanel = new JPanel(new MigLayout("align center", "[center]"));

		JLabel confirmacionLabel = new JLabel(LABEL_CONFIRMACION, SwingConstants.CENTER);
		confirmacionLabel.setFont(confirmacionLabel.getFont().deriveFont(Font.BOLD, 16f));

		JPanel buttonPanel = new JPanel(new MigLayout("align center, gap 30"));
		confirmarCierreBtn = new JButton(BOTON_CONFIRMAR_CIERRE);
		cancelarCierreBtn = new JButton(BOTON_CANCELAR);

		confirmarCierreBtn.addActionListener(e -> tomarConfirmacionCierre(true));
		cancelarCierreBtn.addActionListener(e -> tomarConfirmacionCierre(false));

		buttonPanel.add(confirmarCierreBtn);
		buttonPanel.add(cancelarCierreBtn);

		contentPanel.add(confirmacionLabel, "wrap, align center, gapy 0 5"); 
		contentPanel.add(buttonPanel, "align center");

		confirmacionPanel.add(contentPanel, "grow, push, wrap, align center");

		return confirmacionPanel;
	}

	private JPanel crearPanelCierreExitoso() {
		cierreExitosoPanel = new JPanel(new MigLayout("insets 50, align center", "[center]"));

		JLabel cierreExitosoLabel = new JLabel(LABEL_CIERRE_EXITOSO, SwingConstants.CENTER);
		cierreExitosoLabel.setFont(cierreExitosoLabel.getFont().deriveFont(Font.BOLD, 16f));

		volverInicioBtn = new JButton(BOTON_VOLVER_INICIO);
		volverInicioBtn.addActionListener(e -> volverInicioClick());

		cierreExitosoPanel.add(cierreExitosoLabel, "wrap, align center, gapy 0 10");
		cierreExitosoPanel.add(volverInicioBtn, "");

		return cierreExitosoPanel;
	}

	// --- LÓGICA DE NAVEGACIÓN Y ESTADO ---

	private void regresarClick(String targetCard) {
		cardLayout.show(mainPanel, targetCard);

		switch (targetCard) {
			case "ordenes":
				ordenesTable.setEnabled(true);
				seleccionarOrdenBtn.setEnabled(true);
				break;
			case "observacion":
				solicitarObservacionCierreOrden(); 
				break;
			case "motivos":
				solicitarMotivosFueraDeServicio(motivosTipoFueraServicio); 
				break;
		}
	}

	private void regresarDesdeConfirmacion() {
		if (fueraServicioPendienteReintento) { 
			regresarClick("motivos");
		} else {
			regresarClick("observacion");
		}
	}

	public void opcionCerrarOrdenDeInspeccion() {
		cardLayout.show(mainPanel, "loading");
		habilitarBotonesConfirmacionCancelacion(true);
		limpiarCampos();
		resetearEstadoReintento();
		habilitarVentana();
		gestor.nuevoCierreOrdenInspeccion();
	}
    
    public void mostrarProcesandoCierre() {
        cardLayout.show(mainPanel, "procesando");
        // Aseguramos que la pantalla se redibuje inmediatamente
        revalidate();
        repaint();
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
		motivosTipoFueraServicio.clear();
	}

	private void actualizarSeleccionTabla(int selectedRow) {
		if (ordenesTableModel.getRowCount() == 0) {
			lastSelectedRow = -1;
			return;
		}

		if (lastSelectedRow != -1 && selectedRow != lastSelectedRow) {
			if (lastSelectedRow < ordenesTableModel.getRowCount()) {
				ordenesTableModel.setValueAt(false, lastSelectedRow, 0);
			}
		}

		if (selectedRow != -1) {
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
			DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
			DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

			for (JsonObject info : infoOrdenesInspeccion) {
				int numeroOrden = info.get("numeroOrden").getAsInt();
				String identificadorSismografo = info.get("identificadorSismografo").getAsString();
				String fechaHoraFinalizacion = info.get("fechaHoraFinalizacion").getAsString();
				String nombreEstacion = info.get("nombreEstacion").getAsString();

				String fechaFormateada;
				try {
					LocalDateTime dateTime = LocalDateTime.parse(fechaHoraFinalizacion, inputFormatter);

					fechaFormateada = dateTime.format(targetFormatter);

				} catch (DateTimeParseException e) {
					System.err.println(
							"Error al parsear fecha: " + fechaHoraFinalizacion + ". Usando el valor original.");
					fechaFormateada = fechaHoraFinalizacion;
				}

				ordenMap.put(identificadorSismografo + " - " + numeroOrden, numeroOrden);

				ordenesTableModel.addRow(new Object[] { false, numeroOrden, nombreEstacion, identificadorSismografo, fechaFormateada });
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

		if (observacionPendienteReintento != null) {
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

		if (observacion.isEmpty()) {
			JOptionPane.showMessageDialog(this, "La observación de cierre es obligatoria.", TITULO_ADVERTENCIA, JOptionPane.WARNING_MESSAGE);
			observacionTextArea.requestFocusInWindow();
			return; 
		}

		observacionPendienteReintento = observacion;
		fueraServicioPendienteReintento = fueraServicio;

		gestor.tomarObservacionCierreOrden(observacion, fueraServicio);
	}

	public void observacionCierreOrdenOK() {
		observacionTextArea.setEnabled(false);
		fueraServicioCheckBox.setEnabled(false);
		confirmarObservacionBtn.setEnabled(false);
		esperandoReintento = false;
	}

	public void solicitarMotivosFueraDeServicio(List<String> tiposMotivo) {
		this.motivosTipoFueraServicio = tiposMotivo;
		motivosPanel.removeAll();
		motivoCheckBoxes.clear();
		comentarioTextFields.clear();

		motivosPanel.setLayout(new MigLayout("wrap 3, fillx, insets 10 0 0 0, gapy 15", "[pref!]30[pref!][grow, fill]"));

		List<String[]> motivosReintento = motivosPendientesReintento != null ? motivosPendientesReintento : new ArrayList<>();

		Border fieldPadding = BorderFactory.createEmptyBorder(4, 5, 4, 5);
		Border fieldLineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1);

		for (String tipo : tiposMotivo) {

			JCheckBox checkBox = new JCheckBox(tipo);
			JTextField comentarioField = new JTextField(20);
			comentarioField.setEnabled(false);

			comentarioField.setBorder(BorderFactory.createCompoundBorder(fieldLineBorder, fieldPadding));

			checkBox.addActionListener(ev -> comentarioField.setEnabled(checkBox.isSelected()));

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
		boolean algunMotivoSeleccionado = false;
		boolean comentarioVacioEnSeleccionado = false;
		JTextField campoComentarioFallo = null; 

		for (Map.Entry<String, JCheckBox> entry : motivoCheckBoxes.entrySet()) {
			if (entry.getValue().isSelected()) {
				algunMotivoSeleccionado = true;
				String motivoTipo = entry.getKey();
				String comentario = comentarioTextFields.get(motivoTipo).getText().trim();

				if (comentario.isEmpty()) {
					comentarioVacioEnSeleccionado = true;
					if (campoComentarioFallo == null) {
						campoComentarioFallo = comentarioTextFields.get(motivoTipo);
					}
				}

				motivosSeleccionados.add(new String[]{motivoTipo, comentario});
			}
		}

		if (!algunMotivoSeleccionado && fueraServicioPendienteReintento) { 
			JOptionPane.showMessageDialog(this, MENSAJE_ERROR_SELECCION_MOTIVO, TITULO_ADVERTENCIA, JOptionPane.WARNING_MESSAGE);
			confirmarMotivosBtn.setEnabled(true);
			return; 
		}

		if (comentarioVacioEnSeleccionado) {
			JOptionPane.showMessageDialog(this, MENSAJE_ERROR_COMENTARIOS, TITULO_ADVERTENCIA, JOptionPane.WARNING_MESSAGE);
			if (campoComentarioFallo != null) {
				campoComentarioFallo.requestFocusInWindow();
			}
			confirmarMotivosBtn.setEnabled(true); 
			return; 
		}

		motivosPendientesReintento = motivosSeleccionados;

		gestor.tomarMotivosFueraDeServicio(motivosSeleccionados);
	}

	public void motivosFueraDeServicioOK() {
		confirmarMotivosBtn.setEnabled(false);
		esperandoReintento = false;
	}

	public void solicitarConfirmacionCierreOrden() {
		cardLayout.show(mainPanel, "confirmacion");
		confirmarCierreBtn.setVisible(true);
		cancelarCierreBtn.setVisible(true);
		confirmarCierreBtn.setEnabled(true);
		cancelarCierreBtn.setEnabled(true);

		ordenesTable.setEnabled(false);
		observacionTextArea.setEnabled(false);
		fueraServicioCheckBox.setEnabled(false);
		confirmarObservacionBtn.setEnabled(false);
		confirmarMotivosBtn.setEnabled(false);
	}

    // **MÉTODO CORREGIDO USANDO SWINGWORKER**
	private void tomarConfirmacionCierre(boolean confirmacionFinal) {
		confirmarCierreBtn.setEnabled(false);
		cancelarCierreBtn.setEnabled(false);

		// 1. Mostrar el panel de "Procesando" inmediatamente en el EDT
		mostrarProcesandoCierre();

		// 2. Ejecutar la lógica pesada en un hilo de fondo (SwingWorker)
		SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				// Este código se ejecuta en un hilo de fondo
				return gestor.tomarConfirmacionCierreOrden(confirmacionFinal);
			}

			@Override
			protected void done() {
				// Este código se ejecuta en el EDT después de que doInBackground termina
				try {
					boolean confirmacionCierreOrden = get();
					
					if (confirmacionCierreOrden) {
						if (confirmacionFinal) {
							confirmarCierreBtn.setVisible(false);
							cancelarCierreBtn.setVisible(false);
							cardLayout.show(mainPanel, "cierreExitoso");
						} else {
							opcionCerrarOrdenDeInspeccion(); // Cancelación
						}
					} else {
						// Manejo de Reintento y Error
						manejarErrorCierre(confirmacionFinal);
					}
					
				} catch (InterruptedException | ExecutionException e) {
					// Manejo de excepciones durante la ejecución
					JOptionPane.showMessageDialog(PantallaCierreOrdenInspeccion.this, 
                        "Error inesperado al procesar el cierre: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
					cardLayout.show(mainPanel, "confirmacion"); // Volver a la confirmación
					confirmarCierreBtn.setEnabled(true);
					cancelarCierreBtn.setEnabled(true);
				}
			}
		};
		worker.execute(); // Inicia el hilo de fondo
	}
    
    // **NUEVO MÉTODO PARA MANEJAR EL FLUJO DE ERRORES POST-PROCESAMIENTO**
    private void manejarErrorCierre(boolean confirmacionFinal) {
        esperandoReintento = true;

        if (!gestor.esValidacionObservacionOk()) {
            // Error en Observación
            cardLayout.show(mainPanel, "observacion");
            JOptionPane.showMessageDialog(this, MENSAJE_ERROR_OBSERVACION, "Error de Observación", JOptionPane.ERROR_MESSAGE);
            solicitarObservacionCierreOrden(); 

        } else if (gestor.esPonerSismografoFueraDeServicio() && (!gestor.esValidacionComentariosMotivosOk() || !gestor.esValidacionSelecMotivoOk())) {
            // Error en Motivos
            cardLayout.show(mainPanel, "motivos");
            solicitarMotivosFueraDeServicio(motivosTipoFueraServicio); 

            if (!gestor.esValidacionComentariosMotivosOk()) {
                JOptionPane.showMessageDialog(this, MENSAJE_ERROR_COMENTARIOS, "Error de Comentarios", JOptionPane.ERROR_MESSAGE);
            } else if (!gestor.esValidacionSelecMotivoOk()) {
                JOptionPane.showMessageDialog(this, MENSAJE_ERROR_SELECCION_MOTIVO, "Error de Motivos", JOptionPane.ERROR_MESSAGE);
            }
            
            confirmarMotivosBtn.setEnabled(true); 
            
        } else {
            // Error genérico o desconocido
            JOptionPane.showMessageDialog(this, MENSAJE_ERROR_CONFIRMACION, "Error en Cierre", JOptionPane.ERROR_MESSAGE);
            cardLayout.show(mainPanel, "confirmacion");
            confirmarCierreBtn.setEnabled(true); 
            cancelarCierreBtn.setEnabled(true);
        }
    }


	private void volverInicioClick() {
		volverInicioBtn.setEnabled(false);
		gestor.nuevoCierreOrdenInspeccion();
		revalidate();
		repaint();
	}

	public void mostrarMensaje(String mensaje, String titulo) {
		JOptionPane.showMessageDialog(this, mensaje, titulo, JOptionPane.INFORMATION_MESSAGE);
	}

	public void limpiarDatosFormulario() {
		limpiarCampos(); 
		resetearEstadoReintento(); 
	}
}