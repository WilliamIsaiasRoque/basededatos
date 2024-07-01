package catrastro;

public class Persona {
    private int dni;
    private String nombre;
    private String apellido;
    private String fechaNacimiento;
    private String lugarNacimiento;
    private String nacionalidad;
    private String genero;
    private String estado;

    public Persona(int dni, String nombre, String apellido, String fechaNacimiento, String lugarNacimiento, String nacionalidad, String genero) {
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.lugarNacimiento = lugarNacimiento;
        this.nacionalidad = nacionalidad;
        this.genero = genero;
        this.estado = "Activo";
    }

    public int getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public String getLugarNacimiento() {
        return lugarNacimiento;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public String getGenero() {
        return genero;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
