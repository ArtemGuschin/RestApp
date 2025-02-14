package net.artem.restapp.repository.impl;

import net.artem.restapp.exception.RepositoryException;
import net.artem.restapp.exception.UserNotFoundException;
import net.artem.restapp.model.User;
import net.artem.restapp.repository.UserRepository;
import net.artem.restapp.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;


public class UserRepositoryImpl implements UserRepository {
    @Override
    public User getById(Integer id) {
        try (Session session = HibernateUtil.openSession()) {
            User user = session
                    .createQuery(
                            "FROM User u " + "left join fetch u.events " + "where u.id = :userId", User.class)
                    .setParameter("userId", id)
                    .uniqueResult();
            if (user == null) {
                throw new UserNotFoundException(id);
            }
            return user;
        } catch (HibernateException e) {
            throw new RepositoryException("Could not get User with id " + id, e);
        }

    }

    @Override
    public List<User> getAll() {
        try (Session session = HibernateUtil.openSession()) {
            return session.createQuery("FROM User", User.class).getResultList();

        } catch (HibernateException e) {
            throw new RepositoryException("Could not get all Users", e);
        }


    }

    @Override
    public User save(User user) {
        try (Session session = HibernateUtil.openSession()) {
            session.beginTransaction();

            session.persist(user);

            session.getTransaction().commit();

            return user;

        } catch (HibernateException e) {
            throw new RepositoryException("Could not save User with id " + user.getId(), e);
        }

    }

    @Override
    public User update(User user) {
        try (Session session = HibernateUtil.openSession()) {
            User updatedUser = session.merge(user);

            session.getTransaction().commit();
            return updatedUser;

        } catch (HibernateException e) {
            throw new RepositoryException("Could not update User with id " + user.getId(), e);
        }

    }

    @Override
    public void delete(Integer id) {
        try (Session session = HibernateUtil.openSession()) {
            session.beginTransaction();
            User user = getById(id);
            if (user == null) {
                throw new UserNotFoundException(id);
            }
            session.remove(user);

            session.getTransaction().commit();


        } catch (HibernateException e) {
            throw new RepositoryException("Could not delete User with id " + id, e);
        }

    }
}
