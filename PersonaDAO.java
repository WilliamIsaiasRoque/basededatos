package catrastro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {
    private Connection conn;

    public PersonaDAO(Connection conn) {
        this.conn = conn;
    }

    public void insertarPersona(int dni, String nombre, String apellido, String fechaNacimiento, String lugarNacimiento, String nacionalidad, String genero) throws SQLException {
        if (conn == null) {
            throw new SQLException("Connection is null");
        }

        String sql = "INSERT INTO Persona (PerNumDNI, PerNom, PerApe, PerFecNac, PerLugNac, PerNac, PerGen, Estado) VALUES (?, ?, ?, ?, ?, ?, ?, 'Activo')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dni);
            stmt.setString(2, nombre);
            stmt.setString(3, apellido);
            stmt.setString(4, fechaNacimiento);
            stmt.setString(5, lugarNacimiento);
            stmt.setString(6, nacionalidad);
            stmt.setString(7, genero);
            stmt.executeUpdate();
        }
        conn.commit();
    }

    public void actualizarPersona(int dniNuevo, String nombre, String apellido, String fechaNacimiento, 
                                  String lugarNacimiento, String nacionalidad, String genero, int dniAntiguo) throws SQLException {
        if (conn == null) {
            throw new SQLException("Connection is null");
        }

        String sql = "UPDATE Persona SET PerNumDNI=?, PerNom=?, PerApe=?, PerFecNac=?, PerLugNac=?, PerNac=?, PerGen=? WHERE PerNumDNI=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dniNuevo);
            stmt.setString(2, nombre);
            stmt.setString(3, apellido);
            stmt.setString(4, fechaNacimiento);
            stmt.setString(5, lugarNacimiento);
            stmt.setString(6, nacionalidad);
            stmt.setString(7, genero);
            stmt.setInt(8, dniAntiguo);
            
            int filasActualizadas = stmt.executeUpdate();
            if (filasActualizadas == 0) {
                throw new SQLException("No se encontró la persona con DNI: " + dniAntiguo);
            }
        }
        conn.commit();
    }

    public void eliminarPersona(int dni) throws SQLException {
        if (conn == null) {
            throw new SQLException("Connection is null");
        }

        String sql = "DELETE FROM Persona WHERE PerNumDNI = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, dni);
            stmt.executeUpdate();
        }
        conn.commit();
    }

    public void cambiarEstadoPersona(int dni, String estado) throws SQLException {
        if (conn == null) {
            throw new SQLException("Connection is null");
        }

        String sql = "UPDATE Persona SET Estado = ? WHERE PerNumDNI = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            stmt.setInt(2, dni);
            stmt.executeUpdate();
        }
        conn.commit();
    }

    public List<Persona> obtenerPersonas() throws SQLException {
        if (conn == null) {
            throw new SQLException("Connection is null");
        }

        String sql = "SELECT * FROM Persona";
        List<Persona> personas = new ArrayList<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Persona persona = new Persona(
                        rs.getInt("PerNumDNI"),
                        rs.getString("PerNom"),
                        rs.getString("PerApe"),
                        rs.getString("PerFecNac"),
                        rs.getString("PerLugNac"),
                        rs.getString("PerNac"),
                        rs.getString("PerGen")
                );

                if (rs.getString("Estado").equals("Inactivo")) {
                    persona.setEstado("Inactivo");
                }
                personas.add(persona);
            }
        }
        return personas;
    }
}