/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.UUID;

/**
 *
 * @author jaime
 */
public class Brand {

    private int id;
    public String name;
    double[] drivers;

    /**
     * Constructor
     *
     * @param _id
     * @param drivers Nº of drivers
     */
    public Brand(int _id, int drivers, String _name) {
        this.id = _id;
        this.drivers = new double[(drivers)];
        this.name = _name;
    }

    /**
     *
     * @return
     */
    public double[] getDrivers() {
        return drivers;
    }

    /**
     *
     * @param driver
     * @param _value
     */
    public void setDrivers(int driver, double _value) {
        double aux = (double) Math.round(_value * 100) / 100;
        
        if (aux < 0.0 || aux > 1.0) {
            System.err.println("Error :Invalid driver: "+ _value);
        }
        
        this.drivers[driver] = _value;
    }

    /**
     *
     * @param _BrandId
     */
    public void setBrandId(int _BrandId) {
        this.id = _BrandId;
    }

    /**
     * Get Identify
     *
     * @return
     */
    public int getBrandId() {
        return id;
    }
}
