package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by jt on 8/28/21.
 */
@Component
public class AuthorDaoImpl implements AuthorDao {

    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Author getById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return getEntityManager().find(Author.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Author> query = em.createNamedQuery("find_by_name", Author.class);
            query.setParameter("first_name", firstName);
            query.setParameter("last_name", lastName);

            return query.getSingleResult();
        } finally {
            em.close();
        }

    }

    @Override
    public Author saveNewAuthor(Author author) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(author);
            em.flush();
            em.getTransaction().commit();
            return author;
        } finally {
            em.close();
        }
    }

    @Override
    public Author updateAuthor(Author author) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(author);
            em.flush();
            em.clear();
            Author savedAuthor = em.find(Author.class, author.getId());
            em.getTransaction().commit();
            return savedAuthor;
        } finally {
            em.close();
        }

    }

    @Override
    public void deleteAuthorById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Author author = em.find(Author.class, id);
            em.remove(author);
            em.flush();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Author> listAuthorsByLastNameLike(String lastName) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createQuery("SELECT a FROM Author a WHERE a.lastName LIKE :last_name");
            query.setParameter("last_name", lastName + "%");
            List<Author> authors = query.getResultList();
            return authors;
        } finally {
            em.close();
        }

    }

    @Override
    public List<Author> findAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Author> typedQuery = em.createNamedQuery("author_find_all", Author.class);
            return typedQuery.getResultList();
        } finally {
            em.close();
        }
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
