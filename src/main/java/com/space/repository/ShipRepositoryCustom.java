package com.space.repository;

import com.space.model.RestRequestBody;
import com.space.model.RestRequestParam;
import com.space.model.Ship;

import java.util.List;

public interface ShipRepositoryCustom {

    List<Ship> findByCriteria(RestRequestParam rq);

    Integer countByCriteria(RestRequestParam rq);

    void updateShipById(Long id, RestRequestBody rq, Double rating);
}
