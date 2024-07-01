package catrastro;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class PersonaForm extends JFrame {
    private JTextField dniField;
    private JTextField nombreField;
    private JTextField apellidoField;
    private JXDatePicker fechaNacimientoPicker;
    private JTextField lugarNacimientoField;
    private JTextField nacionalidadField;
    private JComboBox<String> generoComboBox;
    private JXTable personaTable;
    private DefaultTableModel tableModel;
    private PersonaDAO personaDAO;
    private Conexion conexion;
    private SimpleDateFormat dateFormat;

    private JPanel buttonPanel;
    private JButton agregarButton, modificarButton, eliminarButton, cancelarButton, inactivarButton, reactivarButton, actualizarButton, salirButton;

    private String operacion;

    public PersonaForm() {
        conexion = new Conexion();
        Connection conn = conexion.conectar();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        personaDAO = new PersonaDAO(conn);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        setTitle("Persona");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sección 1: Formulario
        JPanel formPanel = new JPanel();
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        formPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 2, 5, 30);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        dniField = new JTextField(8);
        nombreField = new JTextField(10);
        apellidoField = new JTextField(10);
        fechaNacimientoPicker = new JXDatePicker();
        fechaNacimientoPicker.setFormats(new SimpleDateFormat("yyyy-MM-dd"));
        lugarNacimientoField = new JTextField(10);
        nacionalidadField = new JTextField(10);
        generoComboBox = new JComboBox<>(new String[]{"Masculino", "Femenino", "Otro"});
        
        addLabelAndField(formPanel, gbc, "DNI:", dniField, 0, 0);
        addLabelAndField(formPanel, gbc, "Nombre:", nombreField, 1, 0);
        addLabelAndField(formPanel, gbc, "Apellido:", apellidoField, 2, 0);
        addLabelAndField(formPanel, gbc, "Fecha de Nacimiento:", fechaNacimientoPicker, 3, 0);
        addLabelAndField(formPanel, gbc, "Lugar de Nacimiento:", lugarNacimientoField, 4, 0);
        addLabelAndField(formPanel, gbc, "Nacionalidad:", nacionalidadField, 5, 0);
        addLabelAndField(formPanel, gbc, "Género:", generoComboBox, 6, 0);

        add(formPanel, BorderLayout.NORTH);

        // Sección 2: Tabla
        tableModel = new DefaultTableModel(
                new String[]{"DNI", "Nombre", "Apellido", "Fecha de Nacimiento", "Lugar de Nacimiento", "Nacionalidad", "Género", "Estado"}, 0);
        personaTable = new JXTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(personaTable);
        tableScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(tableScrollPane, BorderLayout.CENTER);

        // Sección 3: Botones
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 4, 5, 5));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        agregarButton = new JButton("Agregar");
        agregarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bloquear("Agregar");
                operacion = "Agregar";
            }
        });
        buttonPanel.add(agregarButton);

        modificarButton = new JButton("Modificar");
        modificarButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            bloquear("Modificar");
            operacion = "Modificar";

            int selectedRow = personaTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(PersonaForm.this, "Seleccione una persona para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                dniField.setText(tableModel.getValueAt(selectedRow, 0).toString());
                nombreField.setText((String) tableModel.getValueAt(selectedRow, 1));
                apellidoField.setText((String) tableModel.getValueAt(selectedRow, 2));
                try {
                    Date fechaNacimiento = dateFormat.parse((String) tableModel.getValueAt(selectedRow, 3));
                    fechaNacimientoPicker.setDate(fechaNacimiento);
                } catch (ParseException ex) {
                    fechaNacimientoPicker.setDate(null);
                }
                lugarNacimientoField.setText((String) tableModel.getValueAt(selectedRow, 4));
                nacionalidadField.setText((String) tableModel.getValueAt(selectedRow, 5));
                generoComboBox.setSelectedItem((String) tableModel.getValueAt(selectedRow, 6));
            }
            }
        });
        buttonPanel.add(modificarButton);

        eliminarButton = new JButton("Eliminar");
        eliminarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                bloquear("Eliminar");
                operacion = "Eliminar";
            }
        });
        buttonPanel.add(eliminarButton);

        cancelarButton = new JButton("Cancelar");
        cancelarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelar();
            }
        });
        buttonPanel.add(cancelarButton);

        inactivarButton = new JButton("Inactivar");
        inactivarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bloquear("Inactivar");
                operacion = "Inactivar";
            }
        });
        buttonPanel.add(inactivarButton);

        reactivarButton = new JButton("Reactivar");
        reactivarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bloquear("Reactivar");
                operacion = "Reactivar";
            }
        });
        buttonPanel.add(reactivarButton);

        actualizarButton = new JButton("Actualizar");
        actualizarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                proceder();
            }
        });
        buttonPanel.add(actualizarButton);

        salirButton = new JButton("Salir");
        salirButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        buttonPanel.add(salirButton);

        add(buttonPanel, BorderLayout.SOUTH);
        actualizarTabla();

        setResizable(false);
        setLocationRelativeTo(null); // This line centers the window
    }

    private static void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, int y, int x) {
        gbc.gridx = x;     // Columna para la etiqueta
        gbc.gridy = y;     // Fila para la etiqueta
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = x + 1; // Columna para el campo de entrada
        gbc.gridy = y;     // Fila para el campo de entrada
        panel.add(field, gbc);
    }

    private void proceder() {
        switch(operacion) {
            case "Agregar":
                agregarPersona();
                break;                    
            case "Modificar":
                modificarPersona();
                break;
            case "Eliminar":
                eliminarPersona();
                break;
            case "Inactivar":
                cambiarEstadoPersona("Inactivo");
                break;
            case "Reactivar":
                cambiarEstadoPersona("Activo");
                break;
            default: 
                break;
        }
        desbloquear(operacion);
    }

    private void bloquear(String button) {
        if (!agregarButton.getText().equals(button)) {
            agregarButton.setEnabled(false);
        }
        if (!modificarButton.getText().equals(button)) {
            modificarButton.setEnabled(false);
        }
        if (!eliminarButton.getText().equals(button)) {
            eliminarButton.setEnabled(false);
        }
        if (!inactivarButton.getText().equals(button)) {
            inactivarButton.setEnabled(false);
        }
        if (!reactivarButton.getText().equals(button)) {
            reactivarButton.setEnabled(false);
        }
        salirButton.setEnabled(false);
    }

    private void desbloquear(String button) {
        if (!agregarButton.getText().equals(button)) {
            agregarButton.setEnabled(true);
        }
        if (!modificarButton.getText().equals(button)) {
            modificarButton.setEnabled(true);
        }
        if (!eliminarButton.getText().equals(button)) {
            eliminarButton.setEnabled(true);
        }
        if (!inactivarButton.getText().equals(button)) {
            inactivarButton.setEnabled(true);
        }
        if (!reactivarButton.getText().equals(button)) {
            reactivarButton.setEnabled(true);
        }
        salirButton.setEnabled(true);
    }

    private void agregarPersona() {
        int dni;
        try {
            dni = Integer.parseInt(dniField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: El DNI debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (dniField.getText().length() != 8) {
            JOptionPane.showMessageDialog(this, "Error: El DNI debe tener 8 caracteres.", "Error", JOptionPane.ERROR_MESSAGE);
            return; // Salir del método si el DNI es inválido
        }

        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        Date fechaNacimientoDate = fechaNacimientoPicker.getDate();
        String fechaNacimiento = (fechaNacimientoDate != null) ? dateFormat.format(fechaNacimientoDate) : "";
        String lugarNacimiento = lugarNacimientoField.getText();
        String nacionalidad = nacionalidadField.getText();
        String genero = (String) generoComboBox.getSelectedItem();

        try {
            personaDAO.insertarPersona(dni, nombre, apellido, fechaNacimiento, lugarNacimiento, nacionalidad, genero);
            tableModel.addRow(new Object[]{dni, nombre, apellido, fechaNacimiento, lugarNacimiento, nacionalidad, genero, "Activo"});
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Persona agregada exitosamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        actualizarTabla();
    }

    private void modificarPersona() {
        int selectedRow = personaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una persona para modificar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String dniNuevoStr = dniField.getText();
        
        // Validar que el nuevo DNI tenga 8 dígitos
        if (dniNuevoStr.length() != 8) {
            JOptionPane.showMessageDialog(this, "Error: El DNI debe tener 8 dígitos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int dniNuevo;
        try {
            dniNuevo = Integer.parseInt(dniNuevoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: El DNI debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int dniAntiguo = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        Date fechaNacimientoDate = fechaNacimientoPicker.getDate();
        String fechaNacimiento = (fechaNacimientoDate != null) ? dateFormat.format(fechaNacimientoDate) : "";
        String lugarNacimiento = lugarNacimientoField.getText();
        String nacionalidad = nacionalidadField.getText();
        String genero = (String) generoComboBox.getSelectedItem();

        try {
            personaDAO.actualizarPersona(dniNuevo, nombre, apellido, fechaNacimiento, lugarNacimiento, nacionalidad, genero, dniAntiguo);
            
            tableModel.setValueAt(dniNuevo, selectedRow, 0);
            tableModel.setValueAt(nombre, selectedRow, 1);
            tableModel.setValueAt(apellido, selectedRow, 2);
            tableModel.setValueAt(fechaNacimiento, selectedRow, 3);
            tableModel.setValueAt(lugarNacimiento, selectedRow, 4);
            tableModel.setValueAt(nacionalidad, selectedRow, 5);
            tableModel.setValueAt(genero, selectedRow, 6);
            
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Persona modificada exitosamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        actualizarTabla();
    }

    private void eliminarPersona() {
        int selectedRow = personaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una persona para eliminar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int dni = (int) tableModel.getValueAt(selectedRow, 0); // Obtener directamente como int

        try {
            personaDAO.eliminarPersona(dni);
            tableModel.removeRow(selectedRow);
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Persona eliminada exitosamente.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar a la base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        actualizarTabla();
    }

    private void limpiarCampos() {
        nombreField.setText("");
        apellidoField.setText("");
        fechaNacimientoPicker.setDate(null);
        lugarNacimientoField.setText("");
        nacionalidadField.setText("");
        generoComboBox.setSelectedIndex(0);
    }

    private void cancelar() {
        desbloquear(operacion);
        limpiarCampos();
    }

    private void actualizarTabla() {
        try {
            List<Persona> personas = personaDAO.obtenerPersonas();
            tableModel.setRowCount(0);
            for (Persona persona : personas) {
                tableModel.addRow(new Object[]{
                        persona.getDni(),
                        persona.getNombre(),
                        persona.getApellido(),
                        persona.getFechaNacimiento(),
                        persona.getLugarNacimiento(),
                        persona.getNacionalidad(),
                        persona.getGenero(),
                        persona.getEstado()
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar personas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoPersona(String estado) {
        int selectedRow = personaTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una persona para cambiar el estado.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int dni = (int) tableModel.getValueAt(selectedRow, 0);

        try {
            personaDAO.cambiarEstadoPersona(dni, estado);
            tableModel.setValueAt(estado.equals("Activo") ? "Activo" : "Inactivo", selectedRow, 7); // Actualizar estado en la tabla
            limpiarCampos();
            JOptionPane.showMessageDialog(this, "Estado de la persona cambiado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cambiar el estado de la persona: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        actualizarTabla();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new PersonaForm().setVisible(true);
            }
        });
    }
}