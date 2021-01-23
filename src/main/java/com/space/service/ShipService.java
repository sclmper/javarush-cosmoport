package com.space.service;

import com.space.exception.BadRequestException;
import com.space.exception.NotFoundException;
import com.space.model.*;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
@Transactional
public class ShipService {

    private final ShipRepository shipRepository;

    @Autowired
    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    public RestResponseBody[] getShipsList(RestRequestParam param) {

        if (param.getPageNumber() == null) {
            param.setPageNumber(0);
        }
        if (param.getPageSize() == null) {
            param.setPageSize(3);
        }

        return shipRepository.findByCriteria(param).stream()
                .map(this::convertShipToResponse)
                .toArray(RestResponseBody[]::new);
    }

    public Integer getShipsCount(RestRequestParam param) {
        return shipRepository.countByCriteria(param);
    }

    public RestResponseBody createShip(RestRequestBody rq) {
        if (rq == null || rq.getName() == null || rq.getName().isEmpty()
                || rq.getPlanet() == null || rq.getPlanet().isEmpty()
                || rq.getProdDate() < 0
                || rq.getSpeed() == null || rq.getCrewSize() == null) {
            throw new BadRequestException();
        }
        validateRequestBody(rq);

        if (rq.getUsed() == null) {
            rq.setUsed(false);
        }

        Ship ship = new Ship();
        ship.setName(rq.getName());
        ship.setPlanet(rq.getPlanet());
        ship.setShipType(rq.getShipType().name());
        ship.setProdDate(new Date(rq.getProdDate()));
        ship.setUsed(rq.getUsed());
        ship.setSpeed(rq.getSpeed());
        ship.setCrewSize(rq.getCrewSize());
        ship.setRating(calculateRating(ship));
        long id = shipRepository.save(ship).getId();

        return shipRepository.findById(id).map(this::convertShipToResponse)
                .orElseThrow(NotFoundException::new);
    }

    public RestResponseBody getShip(Long id) {
        validateId(id);
        return shipRepository.findById(id).map(this::convertShipToResponse)
                .orElseThrow(NotFoundException::new);
    }

    public RestResponseBody updateShip(Long id, RestRequestBody rq) {
        validateId(id);
        validateRequestBody(rq);

        Optional<Ship> ship = Optional.empty();
        if (rq.getProdDate() != null || rq.getUsed() != null || rq.getSpeed() != null) {
            ship = shipRepository.findById(id);
        }
        Double rating = ship.map(s -> {
            if (rq.getProdDate() != null) {
                s.setProdDate(new Date(rq.getProdDate()));
            }
            if (rq.getUsed() != null) {
                s.setUsed(rq.getUsed());
            }
            if (rq.getSpeed() != null) {
                s.setSpeed(rq.getSpeed());
            }
            return calculateRating(s);
        }).orElse(null);
        shipRepository.updateShipById(id, rq, rating);

        return shipRepository.findById(id).map(this::convertShipToResponse)
                .orElseThrow(NotFoundException::new);
    }

    public void deleteShip(Long id) {
        validateId(id);
        shipRepository.deleteById(id);
    }

    private Double calculateRating(Ship ship) {
        double v = ship.getSpeed();
        double k = Boolean.TRUE.equals(ship.getUsed()) ? 0.5 : 1;
        int y0 = 3019;
        int y1 = ship.getProdDate().toLocalDate().getYear();
        double result = (80 * v * k) / (y0 - y1 + 1);
        return Math.round(result * 100.0) / 100.0;
    }

    private RestResponseBody convertShipToResponse(Ship ship) {
        RestResponseBody rs = new RestResponseBody();
        rs.setId(ship.getId());
        rs.setName(ship.getName());
        rs.setPlanet(ship.getPlanet());
        rs.setShipType(ShipType.valueOf(ship.getShipType()));
        rs.setProdDate(ship.getProdDate().getTime());
        rs.setUsed(ship.getUsed());
        rs.setSpeed(ship.getSpeed());
        rs.setCrewSize(ship.getCrewSize());
        rs.setRating(ship.getRating());
        return rs;
    }

    private void validateId(Long id) {
        if (id <= 0) {
            throw new BadRequestException();
        }
        if (!shipRepository.existsById(id)) {
            throw new NotFoundException();
        }
    }

    private void validateRequestBody(RestRequestBody rq) {
        int prodDateYear = -1;
        if (rq.getProdDate() != null) {
            prodDateYear = Instant.ofEpochMilli(rq.getProdDate()).atOffset(ZoneOffset.UTC).getYear();
        }

        if (rq.getName() != null && (rq.getName().isEmpty() || rq.getName().length() > 50)
                || rq.getPlanet() != null && (rq.getPlanet().isEmpty() || rq.getPlanet().length() > 50)
                || prodDateYear > 0 && (prodDateYear < 2800 || prodDateYear > 3019)
                || rq.getSpeed() != null && (rq.getSpeed() < 0.01 || rq.getSpeed() > 0.99)
                || rq.getCrewSize() != null && (rq.getCrewSize() < 1 || rq.getCrewSize() > 9999)) {
            throw new BadRequestException();
        }
    }
}