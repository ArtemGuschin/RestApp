package net.artem.restapp.repository.impl;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityGraph;
import net.artem.restapp.exception.EventNotFoundException;
import net.artem.restapp.exception.RepositoryException;
import net.artem.restapp.model.Event;
import net.artem.restapp.repository.EventRepository;
import net.artem.restapp.util.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;


import java.util.Collections;
import java.util.List;


public class EventRepositoryImpl implements EventRepository {
    @Override
    public Event getById(Integer id) {
        try (Session session = HibernateUtil.openSession()) {

            EntityGraph<Event> graph = session.createEntityGraph(Event.class);
            graph.addAttributeNodes("user", "file");

            Event event = session.find(Event.class, id,
                    Collections.singletonMap("javax.persistence.fetchgraph", graph));

            if (event == null) {
                throw new EventNotFoundException(id);
            }

            return event;
        } catch (HibernateException e) {
            throw new RepositoryException("Error event get ", e);
        }
    }

    @Override
    public List<Event> getAll() {
        try (Session session = HibernateUtil.openSession()) {

            return session.createQuery("FROM Event ", Event.class).getResultList();

        } catch (HibernateException e) {
            throw new RepositoryException("Error getting all event", e);
        }

    }

    @Override
    public Event save(Event event) {
        try (Session session = HibernateUtil.openSession()) {
            session.beginTransaction();

            session.persist(event);

            session.getTransaction().commit();

            return event;
        } catch (HibernateException e) {
            throw new RepositoryException("Error save event ", e);
        }
    }

    @Override
    public Event update(Event event) {
        try (Session session = HibernateUtil.openSession()) {
            session.beginTransaction();

            Event updateEvent = getById(event.getId());

            if (updateEvent == null) {
                throw new EventNotFoundException(event.getId());
            }

            session.merge(event);

            session.getTransaction().commit();

            return event;
        } catch (HibernateException e) {
            throw new RepositoryException("Error updating event ", e);
        }
    }

    @Override
    public void delete(Integer id) {
        try (Session session = HibernateUtil.openSession()) {
            session.beginTransaction();

            Event event = getById(id);

            if (event == null) {
                throw new EventNotFoundException(id);
            }

            session.remove(event);

            session.getTransaction().commit();
        } catch (HibernateException e) {
            throw new RepositoryException("Error deleting event ", e);
        }
    }


}
