package net.artem.restapp.util;

import lombok.experimental.UtilityClass;
import net.artem.restapp.model.Event;
import net.artem.restapp.model.File;
import net.artem.restapp.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

@UtilityClass
public final class HibernateUtil {
    private static final SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration =
                    new Configuration()
                            .addAnnotatedClass(User.class)
                            .addAnnotatedClass(File.class)
                            .addAnnotatedClass(Event.class);

            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Не удалось создать экземпляр SessionFactory." + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Session openSession() {
        return sessionFactory.openSession();
    }
}


