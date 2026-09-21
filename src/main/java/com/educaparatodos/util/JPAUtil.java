package com.educaparatodos.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    private static final String UNIDAD_PERSISTENCIA = "educaParaTodosPU";
    private static EntityManagerFactory emf;

    static {
        Map<String, String> overrides = new HashMap<>();

        // Si existe la variable de entorno MYSQL_PASSWORD, la usamos en vez
        // del valor genérico que quedó escrito en persistence.xml.
        String claveEnv = System.getenv("MYSQL_PASSWORD");
        if (claveEnv != null && !claveEnv.isEmpty()) {
            overrides.put("javax.persistence.jdbc.password", claveEnv);
        }

        emf = Persistence.createEntityManagerFactory(UNIDAD_PERSISTENCIA, overrides);
    }

    private JPAUtil() {
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    public static void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}