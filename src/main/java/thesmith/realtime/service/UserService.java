package thesmith.realtime.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import thesmith.realtime.model.User;

@Transactional
@Component
public class UserService {
    @PersistenceContext
    private EntityManager em;
    
    public void save(User user) {
        em.persist(user);
    }
    
    public User get(String gid) {
        try {
            return (User) em.createQuery("select u from User u where u.gid = :gid").setMaxResults(1).setParameter("gid", gid)
                            .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<User> get() {
        return (List<User>) em.createQuery("select u from User u").getResultList();
    }
    
    public void delete(User user) {
        em.remove(user);
    }
}
