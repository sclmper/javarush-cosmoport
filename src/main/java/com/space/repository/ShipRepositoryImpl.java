package com.space.repository;

import com.space.model.RestRequestBody;
import com.space.model.RestRequestParam;
import com.space.model.Ship;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ShipRepositoryImpl implements ShipRepositoryCustom {

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PLANET = "planet";
    private static final String SHIP_TYPE = "shipType";
    private static final String PROD_DATE = "prodDate";
    private static final String USED = "used";
    private static final String SPEED = "speed";
    private static final String CREW_SIZE = "crewSize";
    private static final String RATING = "rating";

    @PersistenceContext
    private EntityManager em;

    private RestRequestParam param;
    private CriteriaBuilder builder;
    private Root<Ship> root;
    private List<Predicate> predicates;

    @Override
    public List<Ship> findByCriteria(RestRequestParam param) {
        this.param = param;
        builder = em.getCriteriaBuilder();
        CriteriaQuery<Ship> query = builder.createQuery(Ship.class);
        root = query.from(Ship.class);

        predicates = setPredicates();
        if (!predicates.isEmpty()) {
            query.where(builder.and(predicates.toArray(new Predicate[0])));
        }
        if (param.getOrder() != null) {
            query.orderBy(builder.asc(root.get(param.getOrder().getFieldName())));
        }
        return em.createQuery(query)
                .setFirstResult(param.getPageNumber() * param.getPageSize())
                .setMaxResults(param.getPageSize())
                .getResultList();
    }

    @Override
    public Integer countByCriteria(RestRequestParam param) {
        this.param = param;
        builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        root = query.from(Ship.class);
        query.select(builder.count(root));

        predicates = setPredicates();
        if (!predicates.isEmpty()) {
            query.where(builder.and(predicates.toArray(new Predicate[0])));
        }
        return em.createQuery(query).getSingleResult().intValue();
    }

    @Override
    public void updateShipById(Long id, RestRequestBody rq, Double rating) {
        builder = em.getCriteriaBuilder();
        CriteriaUpdate<Ship> update = builder.createCriteriaUpdate(Ship.class);
        root = update.from(Ship.class);
        boolean isSet = false;

        if (rq.getName() != null) {
            update.set(root.get(NAME), rq.getName());
            isSet = true;
        }
        if (rq.getPlanet() != null) {
            update.set(root.get(PLANET), rq.getPlanet());
            isSet = true;
        }
        if (rq.getShipType() != null) {
            update.set(root.get(SHIP_TYPE), rq.getShipType().name());
            isSet = true;
        }
        if (rq.getProdDate() != null) {
            update.set(root.get(PROD_DATE), new Date(rq.getProdDate()));
            isSet = true;
        }
        if (rq.getUsed() != null) {
            update.set(root.get(USED), rq.getUsed());
            isSet = true;
        }
        if (rq.getSpeed() != null) {
            update.set(root.get(SPEED), rq.getSpeed());
            isSet = true;
        }
        if (rq.getCrewSize() != null) {
            update.set(root.get(CREW_SIZE), rq.getCrewSize());
            isSet = true;
        }
        if (rating != null) {
            update.set(root.get(RATING), rating);
            isSet = true;
        }

        if (isSet) {
            update.where(builder.equal(root.get(ID), id));
            em.createQuery(update).executeUpdate();
            em.clear();
        }
    }

    private List<Predicate> setPredicates() {
        List<Predicate> p = new ArrayList<>();

        if (param.getName() != null) {
            p.add(builder.like(builder.upper(root.get(NAME)), "%" + (param.getName().toUpperCase() + "%")));
        }
        if (param.getPlanet() != null) {
            p.add(builder.like(builder.upper(root.get(PLANET)), "%" + (param.getPlanet().toUpperCase() + "%")));
        }
        if (param.getShipType() != null) {
            p.add(builder.equal(root.get(SHIP_TYPE), param.getShipType().name()));
        }
        if (param.getAfter() != null) {
            p.add(builder.greaterThanOrEqualTo(root.get(PROD_DATE), new Date(param.getAfter())));
        }
        if (param.getBefore() != null) {
            p.add(builder.lessThanOrEqualTo(root.get(PROD_DATE), new Date(param.getBefore())));
        }
        if (param.getIsUsed() != null) {
            p.add(builder.equal(root.get(USED), param.getIsUsed()));
        }
        if (param.getMinSpeed() != null) {
            p.add(builder.greaterThanOrEqualTo(root.get(SPEED), param.getMinSpeed()));
        }
        if (param.getMaxSpeed() != null) {
            p.add(builder.lessThanOrEqualTo(root.get(SPEED), param.getMaxSpeed()));
        }
        if (param.getMinCrewSize() != null) {
            p.add(builder.greaterThanOrEqualTo(root.get(CREW_SIZE), param.getMinCrewSize()));
        }
        if (param.getMaxCrewSize() != null) {
            p.add(builder.lessThanOrEqualTo(root.get(CREW_SIZE), param.getMaxCrewSize()));
        }
        if (param.getMinRating() != null) {
            p.add(builder.greaterThanOrEqualTo(root.get(RATING), param.getMinRating()));
        }
        if (param.getMaxRating() != null) {
            p.add(builder.lessThanOrEqualTo(root.get(RATING), param.getMaxRating()));
        }
        return p;
    }
}
