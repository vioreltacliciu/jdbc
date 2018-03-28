package ro.db.appl.dao;

import ro.db.appl.domain.Location;

import java.util.List;

/**
 * Developer: Viorelt
 * <p>
 **/

public interface LocationDAO {
    Location findById(Long id);

    Long getNextIdVal(String tableName, String columnIdName);

    Location insert(Location entity);

    List<Location> findAll();

    Location update(Location entity);

    void delete(Location entity);
}
