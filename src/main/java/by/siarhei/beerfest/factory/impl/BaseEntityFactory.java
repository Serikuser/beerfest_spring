package by.siarhei.beerfest.factory.impl;

import by.siarhei.beerfest.config.SpringAppContext;
import by.siarhei.beerfest.entity.Entity;
import by.siarhei.beerfest.factory.api.EntityFactory;

public class BaseEntityFactory implements EntityFactory {

    private BaseEntityFactory(){}

    @Override
    public Entity produce(String name) {
        return (Entity) SpringAppContext.getBean(name);
    }
}
