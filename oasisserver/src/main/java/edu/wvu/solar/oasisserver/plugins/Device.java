package edu.wvu.solar.oasisserver.plugins;

import java.util.List;

public abstract class Device {

    public abstract List<Parameter> getParameters();
    public abstract Parameter getParameter(String name);
    public abstract void setParameter(String name, Object value);

}
