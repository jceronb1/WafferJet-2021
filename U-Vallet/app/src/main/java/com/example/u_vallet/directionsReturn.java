package com.example.u_vallet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class directionsReturn {

    private List<List<List<HashMap<String,String>>>> routes1 = new ArrayList<List<List<HashMap<String,String>>>>() ;
    private List<String> durations = new ArrayList<>();

    public directionsReturn(){

    }

    public List<List<List<HashMap<String, String>>>> getRoutes1() {
        return routes1;
    }

    public void setRoutes1(List<List<List<HashMap<String, String>>>> routes1) {
        this.routes1 = routes1;
    }

    public List<String> getDurations() {
        return durations;
    }

    public void setDurations(List<String> durations) {
        this.durations = durations;
    }
}
