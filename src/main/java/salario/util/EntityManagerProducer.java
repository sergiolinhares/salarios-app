package salario.util;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@ApplicationScoped
public class EntityManagerProducer {

	private final EntityManagerFactory factory =
	        Persistence.createEntityManagerFactory("SalarioAppPU");

    @Produces                     
    @RequestScoped
    public EntityManager createEntityManager() {
        return factory.createEntityManager();
    }

    @Produces                     
    @ApplicationScoped
    public EntityManagerFactory produceFactory() {
        return factory;           
    }

    public void closeEntityManager(@Disposes EntityManager em) {
        if (em.isOpen()) em.close();
    }
}