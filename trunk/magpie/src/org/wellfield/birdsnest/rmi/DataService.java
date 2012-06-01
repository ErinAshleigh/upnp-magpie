package org.wellfield.birdsnest.rmi;

import java.util.Map;

public interface DataService {

    public abstract Integer receiveData(Map<String, Object> params);

}