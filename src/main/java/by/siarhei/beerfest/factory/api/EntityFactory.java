package by.siarhei.beerfest.factory.api;

import by.siarhei.beerfest.entity.Entity;

public interface EntityFactory {
    Entity produce(String name);
}
