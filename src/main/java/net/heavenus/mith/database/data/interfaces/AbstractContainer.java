package net.heavenus.mith.database.data.interfaces;

import net.heavenus.mith.database.data.DataContainer;

public abstract class AbstractContainer {

    protected DataContainer dataContainer;

    public AbstractContainer(DataContainer dataContainer) {
        this.dataContainer = dataContainer;
    }

    public void gc() {
        this.dataContainer = null;
    }
}
