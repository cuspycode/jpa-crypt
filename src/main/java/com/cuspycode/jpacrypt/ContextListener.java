package com.cuspycode.jpacrypt;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {
    private static final String CONFIG_PATH = "${CATALINA_BASE}/webapps/jpa-crypt/WEB-INF/config.properties";
    private static EntityManagerFactory emf;
    private static Properties config;

    @Override
    public void contextInitialized(ServletContextEvent event) {
	loadConfig();
        emf = Persistence.createEntityManagerFactory("JPA-CRYPT");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        emf.close();
    }

    public static EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    public static String getConfig(String key) {
	return config.getProperty(key);
    }

    public void loadConfig() {
	config = new Properties();
	try {
	    InputStream s = new FileInputStream(interpolatePath(CONFIG_PATH));
	    if (s != null) {
		config.load(s);
		s.close();
	    }
	} catch (IOException e) {
	    throw new RuntimeException(e);
	}
    }

    private String interpolatePath(String x) {
	String catalinaBase = System.getenv("CATALINA_BASE");
	if (catalinaBase == null) {
	    catalinaBase = "";
	}
	return x.replaceAll("\\$\\{CATALINA_BASE\\}", catalinaBase);
    }
}
