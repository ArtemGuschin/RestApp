package net.artem.restapp.repository.impl;

import net.artem.restapp.exception.FileNotFoundException;
import net.artem.restapp.exception.RepositoryException;
import net.artem.restapp.model.File;
import net.artem.restapp.repository.FileRepository;
import net.artem.restapp.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;


public class FileRepositoryImpl implements FileRepository {
    @Override
    public File getById(Integer id) {
        try (Session session = HibernateUtil.openSession()) {
            File file = session.get(File.class, id);
            if (file == null) {
                throw new FileNotFoundException(id);

            }
            return file;

        } catch (HibernateException e) {
            throw new RepositoryException("Error getting file", e);
        }

    }

    @Override
    public List<File> getAll() {
        try (Session session = HibernateUtil.openSession()) {
            return session.createQuery("FROM File", File.class).getResultList();
        } catch (HibernateException e) {
            throw new RepositoryException("Error getting files", e);
        }


    }

    @Override
    public File save(File file) {
        try (Session session = HibernateUtil.openSession()) {
            session.beginTransaction();

            session.persist(file);
            session.getTransaction().commit();

            return file;

        } catch (HibernateException e) {
            throw new RepositoryException("Error saving file", e);
        }

    }

    @Override
    public File update(File file) {
        try (Session session = HibernateUtil.openSession()) {
            session.beginTransaction();
            File updateFile = getById(file.getId());

            if (updateFile == null) {
                throw new FileNotFoundException(file.getId());
            }
            session.merge(file);
            session.getTransaction().commit();

            return file;

        } catch (HibernateException e) {
            throw new RepositoryException("Error updating file", e);
        }


    }

    @Override
    public void delete(Integer id) {
        try (Session session = HibernateUtil.openSession()) {
            session.beginTransaction();
            File file = getById(id);
            if (file == null) {
                throw new FileNotFoundException(id);

            }
            session.remove(file);

            session.getTransaction().commit();

        } catch (HibernateException e) {
            throw new RepositoryException("Error deleting file", e);
        }

    }
}
