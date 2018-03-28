package ro.db.appl.dao;

import ro.db.api.em.EntityManager;
import ro.db.api.em.EntityManagerImpl;
import ro.db.appl.domain.Location;

import java.util.List;
import java.util.Map;

/**
 * Developer: Viorelt
 * <p>
 **/

public class LocationDAOImpl implements LocationDAO {
    private EntityManager<Location> entityManager;

    public LocationDAOImpl() {
        entityManager=new EntityManagerImpl<>(Location.class);
    }

    @Override
    public Location findById(Long id) {
        return entityManager.findById(id);
    }

    @Override
    public Long getNextIdVal(String tableName, String columnIdName) {
        return entityManager.getNextIdVal(tableName, columnIdName);
    }

    @Override
    public Location insert(Location entity) {
        return entityManager.insert(entity);
    }

    @Override
    public List<Location> findAll() {
        return entityManager.findAll();
    }

    @Override
    public Location update(Location entity) {
        return entityManager.update(entity);
    }

    @Override
    public void delete(Location entity) {
        entityManager.delete(entity);
    }

    public List<Location> findByParams(Map<String, Object> params) {
        return entityManager.findByParams(params);
    }
}
