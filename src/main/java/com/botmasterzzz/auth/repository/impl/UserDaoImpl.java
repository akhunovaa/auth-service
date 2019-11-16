package com.botmasterzzz.auth.repository.impl;

import com.botmasterzzz.auth.model.User;
import com.botmasterzzz.auth.model.UserAuthEntity;
import com.botmasterzzz.auth.repository.UserDao;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Long userAdd(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Long id = (Long) session.save(user);
        session.getTransaction().commit();
        session.close();
        return id;
    }

    @Override
    public Long userAuthEntityAdd(UserAuthEntity userAuthEntity) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Long id = (Long) session.save(userAuthEntity);
        session.getTransaction().commit();
        session.close();
        return id;
    }

    @SuppressWarnings({"deprecation"})
    public Optional<User> findByLogin(String login) {
        User user;
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq("login", login));
        criteria.addOrder(Order.asc("audWhenCreate"));
        user = (User) criteria.list().get(0);
        session.close();
        return Optional.of(user);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Optional<User> findByEmail(String email) {
        User user;
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq("email", email));
        criteria.addOrder(Order.asc("audWhenCreate"));
        user = (User) criteria.list().get(0);
        session.close();
        return Optional.of(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        Session session = sessionFactory.openSession();
        Transaction updateTransaction = session.beginTransaction();
        User user = session.get(User.class, id);
        updateTransaction.commit();
        session.close();
        return Optional.of(user);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Boolean existsByLogin(String login) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq("login", login));
        criteria.setProjection(Projections.id());
        boolean exists;
        try{
            exists = criteria.uniqueResult() != null;
        }catch (NonUniqueResultException e){
            session.close();
            return true;
        }finally {
            session.close();
        }
        session.close();
        return exists;
    }

    @Override
    @SuppressWarnings("deprecation")
    public Boolean existsByEmail(String email) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq("email", email));
        criteria.setProjection(Projections.id());
        boolean exists;
        try{
          exists = criteria.uniqueResult() != null;
        }catch (NonUniqueResultException e){
            session.close();
            return true;
        }finally {
            session.close();
        }
        session.close();
        return exists;
    }
}