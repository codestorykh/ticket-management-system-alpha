package com.codestorykh.common.repository;

import com.codestorykh.common.criteria.BaseCriteria;
import com.codestorykh.common.criteria.BaseSearchCriteria;
import com.codestorykh.common.dto.PageableRequestVO;
import com.codestorykh.common.dto.PageableResponseVO;
import jakarta.persistence.criteria.CriteriaQuery;

import java.io.Serializable;
import java.util.List;

/**
 * Base service interface providing common CRUD operations and query capabilities for entities.
 * This interface defines the contract for basic entity operations that can be extended by specific entity services.
 */
public interface BaseRepository {

    /**
     * Creates a criteria builder for the specified entity class.
     *
     * @param clazz The entity class
     * @param <T> The type of the entity
     * @return A BaseCriteria instance for building queries
     */
    <T> BaseCriteria<T> createCriteriaBuilder(Class<T> clazz);

    /**
     * Retrieves all entities of the specified class.
     *
     * @param clazz The entity class
     * @param <T> The type of the entity
     * @return List of all entities
     */
    <T> List<T> list(Class<T> clazz);

    /**
     * Retrieves all entities of the specified class with activation status.
     *
     * @param clazz The entity class
     * @param isActivate The activation status
     * @param <T> The type of the entity
     * @return List of entities matching the activation status
     */
    <T> List<T> list(Class<T> clazz, boolean isActivate);

    /**
     * Retrieves all entities of the specified class with sorting and activation status.
     *
     * @param clazz The entity class
     * @param isAsc Whether to sort in ascending order
     * @param isActivate The activation status
     * @param orders The fields to sort by
     * @param <T> The type of the entity
     * @return List of sorted entities
     */
    <T> List<T> list(Class<T> clazz, boolean isAsc, boolean isActivate, String... orders);

    /**
     * Retrieves entities with pagination.
     *
     * @param clazz The entity class
     * @param pageable The pagination parameters
     * @param <T> The type of the entity
     * @return List of entities for the requested page
     */
    <T> List<T> list(Class<T> clazz, PageableRequestVO pageable);

    /**
     * Retrieves entities where a field matches any value in the provided list.
     *
     * @param field The field to match
     * @param values The list of values to match against
     * @param clazz The entity class
     * @param <T> The type of the entity
     * @return List of matching entities
     */
    <T> List<T> listByFieldByListValue(String field, List<String> values, Class<T> clazz);

    /**
     * Retrieves entities matching the search criteria with pagination.
     *
     * @param clazz The entity class
     * @param baseSearchCriteria The search criteria
     * @param pageable The pagination parameters
     * @param <T> The type of the entity
     * @return List of matching entities for the requested page
     */
    <T> List<T> list(Class<T> clazz, BaseSearchCriteria baseSearchCriteria, PageableRequestVO pageable);

    /**
     * Retrieves a page of entities with pagination information.
     *
     * @param clazz The entity class
     * @param pageable The pagination parameters
     * @param <T> The type of the entity
     * @return Pageable response containing entities and pagination info
     */
    <T> PageableResponseVO<T> listPage(Class<T> clazz, PageableRequestVO pageable);

    /**
     * Retrieves a page of entities matching the search criteria with pagination information.
     *
     * @param clazz The entity class
     * @param baseSearchCriteria The search criteria
     * @param pageable The pagination parameters
     * @param <T> The type of the entity
     * @return Pageable response containing matching entities and pagination info
     */
    <T> PageableResponseVO<T> listPage(Class<T> clazz, BaseSearchCriteria baseSearchCriteria, PageableRequestVO pageable);

    /**
     * Retrieves entities matching the search criteria.
     *
     * @param clazz The entity class
     * @param baseSearchCriteria The search criteria
     * @param <T> The type of the entity
     * @return List of matching entities
     */
    <T> List<T> list(Class<T> clazz, BaseSearchCriteria baseSearchCriteria);

    /**
     * Retrieves an entity by its ID.
     *
     * @param id The entity ID
     * @param clazz The entity class
     * @param <T> The type of the entity
     * @return The entity with the specified ID
     */
    <T> T getEntityById(Serializable id, Class<T> clazz);

    /**
     * Saves or updates an entity.
     *
     * @param entity The entity to save or update
     * @param <T> The type of the entity
     */
    <T> void saveOrUpdate(T entity);

    /**
     * Saves or updates multiple entities.
     *
     * @param entities The list of entities to save or update
     * @param <T> The type of the entities
     */
    <T> void saveOrUpdate(List<T> entities);

    /**
     * Deletes an entity.
     *
     * @param entity The entity to delete
     * @param <T> The type of the entity
     */
    <T> void delete(T entity);

    /**
     * Deletes multiple entities.
     *
     * @param entities The list of entities to delete
     * @param <T> The type of the entities
     */
    <T> void delete(List<T> entities);

    /**
     * Retrieves entities where a field matches the specified value.
     *
     * @param field The field to match
     * @param value The value to match against
     * @param clazz The entity class
     * @param <T> The type of the entity
     * @return List of matching entities
     */
    <T> List<T> listByField(String field, Object value, Class<T> clazz);

    /**
     * Retrieves a single entity where a field matches the specified value.
     *
     * @param field The field to match
     * @param value The value to match against
     * @param clazz The entity class
     * @param <T> The type of the entity
     * @return The matching entity or null if not found
     */
    <T> T getByField(String field, Object value, Class<T> clazz);

    /**
     * Executes a native query and returns the results as entities.
     *
     * @param query The native query to execute
     * @param clazz The entity class
     * @param <T> The type of the entity
     * @return List of entities returned by the query
     */
    <T> List<T> query(String query, Class<T> clazz);

    /**
     * Executes a native query and returns the raw results.
     *
     * @param query The native query to execute
     * @return List of raw query results
     */
    List<Object[]> query(String query);

    /**
     * Executes a criteria query and returns the results.
     *
     * @param criteriaQuery The criteria query to execute
     * @param <T> The type of the entity
     * @return List of entities returned by the query
     */
    <T> List<T> list(CriteriaQuery<T> criteriaQuery);
}
