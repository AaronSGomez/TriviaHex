package levelup42.trivia.domain.model;

import java.util.Arrays;

public enum Subject {
    DESARROLLO_DE_INTERFACES("Desarrollo de Interfaces"),
    ACCESO_A_DATOS("Acceso a datos"),
    PROGRAMACION_MULTIMEDIA_Y_DISPOSITIVOS_MOVILES("Programación multimedia y dispositivos móviles"),
    PROGRAMACION_DE_SERVICIOS_Y_PROCESOS("Programación de Servicios y Procesos"),
    INGLES_PROFESIONAL("Inglés Profesional"),
    SOSTENIBILIDAD("Sostenibilidad"),
    DIGITALIZACION("Digitalización"),
    SISTEMAS_DE_GESTION_EMPRESARIAL("Sistemas de gestión empresarial"),
    ITINERARIO_PERSONAL_PARA_LA_EMPLEABILIDAD_II("Itinerario Personal para la Empleabilidad II");

    private final String displayName;

    Subject(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Subject fromDisplayName(String name) {
        if (name == null) return null;
        return Arrays.stream(values())
                .filter(s -> s.displayName.equalsIgnoreCase(name.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown subject: " + name));
    }
}
