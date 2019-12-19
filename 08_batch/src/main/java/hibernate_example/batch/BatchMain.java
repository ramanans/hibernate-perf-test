package hibernate_example.batch;

import java.util.Date;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class BatchMain {
    private static SessionFactory sessionFactory;

    public static void main(String[] args) {
        final Configuration configuration = new Configuration().configure();
        final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(
                configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        final Session session = sessionFactory.openSession();
        long startmillis = System.currentTimeMillis();
        try {
            session.beginTransaction();
            long time =  System.currentTimeMillis();
            for (int i = 0; i < 5_000_000; i++) {
                final Book book = new Book("111111111", "Java", new Date(), "me");
                book.setId((long)i);
                session.save(book);

                if (i % 20000 == 0) {
                    session.flush();
                    session.clear();
                    session.getTransaction().commit();
                    session.beginTransaction();
                }
            }
            session.flush();
            session.getTransaction().commit();
            System.out.println(" ===== time taken for insert===== "+(System.currentTimeMillis() - time)+" ms");
            long start = System.currentTimeMillis();
            for (int i = 0; i < 5_000_000; i++) {
                session.createQuery("from Book b where id= :id").setParameter("id", (long) i).uniqueResult();
            }
            System.out.println("select took : "+ (System.currentTimeMillis() - start) +" ms");
        } catch (final Exception e) {
            e.printStackTrace();
        }

        session.close();
        sessionFactory.close();
        System.out.println(" ===== total time taken ===== "+(System.currentTimeMillis() - startmillis)+" ms");
    }
}
