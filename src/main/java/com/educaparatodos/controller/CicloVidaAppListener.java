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
        // Simplemente llamar a este método fuerza a que la clase JPAUtil
        // se cargue y su bloque "static { }" cree el EntityManagerFactory
        // apenas arranca la aplicación, en vez de esperar a la primera petición.
        JPAUtil.getEntityManagerFactory();
        System.out.println("EducaParaTodos: EntityManagerFactory inicializado correctamente.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Libera la conexión a la base de datos de forma ordenada cuando
        // el servidor se apaga o redespliegas la app.
        JPAUtil.cerrar();
        System.out.println("EducaParaTodos: EntityManagerFactory cerrado correctamente.");
    }
}