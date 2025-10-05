import org.example.entity.ToDo;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public class TestHibernate {

    public static SessionFactory buildSessionFactory() {
        Properties props = new Properties();
        props.put("hibernate.connection.driver_class", "org.h2.Driver");
        props.put("hibernate.connection.url", "jdbc:h2:mem:testdb;MODE=MySQL;DB_CLOSE_DELAY=-1");
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        props.put("hibernate.show_sql", "false");

        Configuration cfg = new Configuration();
        cfg.addAnnotatedClass(ToDo.class);
        cfg.setProperties(props);
        return cfg.buildSessionFactory();
    }
}
