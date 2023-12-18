package ch.zhaw.prog2.io.picturedb;

import java.util.Collection;

/**
 * Generic data source interface to persist items of type T extending {@link Record}<br>
 * What kind of persistence media is used, is defined by the concrete implementation.
 * e.g. InMemory, Files, Database, ...<br>
 * The datatype T to be persisted, must extend {@link Record} which contains the id field to uniquely identify the record.
 */
public interface Datasource<T extends Record> {
    /**
     * Insert a new record to the data source.
     * The id field of the record is ignored, and a new unique id has to be generated, which will be set in the record.
     * This id is used to identify the record in the dataset by the other methods (i.e. find, update or delete methods)
     *
     * @param record of type T to insert into the data set.
     */
    void insert(T record);

    /**
     * Update the content of an existing record in the data set, which is identified by the unique identifier,
     * with the new values from the given record object.
     * If the identifier can not be found in the data set, an {@link RecordNotFoundException} is thrown.
     *
     * @param record to be updated in the dataset
     * @throws RecordNotFoundException if the record is not existing
     */
    void update(T record) throws RecordNotFoundException;

    /**
     * Deletes the record, identified by the id of the given record from the data set.
     * All other fields of the record are ignored.
     * If the identifier can not be found in the data set, an {@link RecordNotFoundException} is thrown.
     *
     * @param record to be deleted
     * @throws RecordNotFoundException if the record is not existing
     */
    void delete(T record) throws RecordNotFoundException;

    /**
     * Returns the number of records in the data set
     * @return number of records
     */
    long count();

    /**
     * Retrieves an instance of the record identified by the given id.
     * If the record can not be found, null is returned.
     * (better return type would be an {@link java.util.Optional} which is covered in part Functional Programming)
     * An empty result is not an error. Therefore, we do not throw an exception.
     *
     * @param id of the record to be retrieved
     * @return record of type T or null if not found
     */
    T findById(long id);

    /**
     * Retrieves all records of the data set.
     * If the dataset is empty an empty collection is returned.
     *
     * @return collection of all records of the data set
     */
    Collection<T> findAll();
}
