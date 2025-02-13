package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Book;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

@Component
public class BookDaoImpl implements BookDao {

    private final EntityManagerFactory emf;

    public BookDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Book getById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return getEntityManager().find(Book.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Book findBookByTitle(String title) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Book> query = em.createNamedQuery("book_find_by_title", Book.class);
            query.setParameter("title", title);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Book saveNewBook(Book book) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(book);
            em.flush();
            em.getTransaction().commit();
            return book;
        } finally {
            em.close();
        }
    }

    @Override
    public Book updateBook(Book book) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(book);
            em.flush();
            em.clear();
            Book savedBook = em.find(Book.class, book.getId());
            em.getTransaction().commit();
            return savedBook;
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteBookById(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Book book = em.find(Book.class, id);
            em.remove(book);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @Override
    public Book findByIsbn(String isbn) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class);
            query.setParameter("isbn", isbn);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Book> findAll() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Book> query = em.createNamedQuery("book_find_all", Book.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Book findBookByTitleCriteria(String title) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
            CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);

            Root<Book> root = criteriaQuery.from(Book.class);

            ParameterExpression<String> titleParameter = criteriaBuilder.parameter(String.class);
            Predicate titlePredicate = criteriaBuilder.equal(root.get("title"), titleParameter);

            criteriaQuery.select(root).where(titlePredicate);

            TypedQuery<Book> typedQuery = em.createQuery(criteriaQuery);
            typedQuery.setParameter(titleParameter, title);
            return typedQuery.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Book findBookByTitleNative(String title) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNativeQuery("SELECT * FROM book WHERE title = :title",
                    Book.class);
            query.setParameter("title", title);
            return (Book) query.getSingleResult();
        } finally {
            em.close();
        }
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
