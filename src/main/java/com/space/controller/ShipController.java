package com.space.controller;

import com.space.model.RestRequestBody;
import com.space.model.RestRequestParam;
import com.space.model.RestResponseBody;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/rest")
@RestController
public class ShipController {

    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping("/ships")
    @ResponseBody
    public RestResponseBody[] getShipsList(RestRequestParam param) {
        return shipService.getShipsList(param);
    }

    @GetMapping("/ships/count")
    @ResponseBody
    public Integer getShipsCount(RestRequestParam param) {
        return shipService.getShipsCount(param);
    }

    @PostMapping("/ships")
    @ResponseBody
    public RestResponseBody createShip(@RequestBody RestRequestBody rq) {
        return shipService.createShip(rq);
    }

    @GetMapping("/ships/{id}")
    @ResponseBody
    public RestResponseBody getShip(@PathVariable Long id) {
        return shipService.getShip(id);
    }

    @PostMapping("/ships/{id}")
    @ResponseBody
    public RestResponseBody updateShip(@PathVariable Long id, @RequestBody RestRequestBody rq) {
        return shipService.updateShip(id, rq);
    }

    @DeleteMapping("/ships/{id}")
    @ResponseBody
    public void deleteShip(@PathVariable Long id) {
        shipService.deleteShip(id);
    }
}
