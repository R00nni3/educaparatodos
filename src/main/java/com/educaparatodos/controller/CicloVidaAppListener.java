package com.educaparatodos.controller;

import com.educaparatodos.util.JPAUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Escucha el ciclo de vida de la aplicación web:
 * - Cuando el servidor (Tomcat) LEVANTA la app, inicializa JPA.
 * - Cuando el servidor APAGA o redespliega la app, cierra JPA correctamente.
 */
@WebListener
public class CicloVidaAppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Forzamos a que JPAUtil cree el EntityManagerFactory al arrancar
            JPAUtil.getEntityManagerFactory();
            System.out.println("EducaParaTodos: EntityManagerFactory inicializado correctamente.");
        } catch (Exception e) {
            System.err.println("--- [ERROR] No se pudo inicializar JPA/Hibernate ---");
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            // Libera la conexión a la base de datos de forma ordenada
            JPAUtil.cerrar();
            System.out.println("EducaParaTodos: EntityManagerFactory cerrado correctamente.");
        } catch (Exception e) {
            System.err.println("--- [ERROR] Falló al cerrar EntityManagerFactory ---");
            e.printStackTrace();
        }
    }
}