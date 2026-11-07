package org.delcom.ifs25055pbo2026springbootcashflow.adapter.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.delcom.ifs25055pbo2026springbootcashflow.domain.repository.IRepository;

/**
 * Implementasi generik IRepository yang menyimpan data di memori
 * (hilang saat aplikasi di-restart).
 *
 * @param <T>  tipe entitas
 * @param <ID> tipe pengenal entitas
 */
public class InMemoryRepository<T, ID> implements IRepository<T, ID> {

    private final Map<ID, T> storage = new LinkedHashMap<>();
    private final Function<T, ID> idExtractor;

    public InMemoryRepository(Function<T, ID> idExtractor) {
        this.idExtractor = idExtractor;
    }

    @Override
    public T save(T entity) {
        storage.put(idExtractor.apply(entity), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(storage.get(id));
    }

    /** Salinan defensif: mengubah hasilnya tidak memengaruhi penyimpanan. */
    @Override
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public boolean deleteById(ID id) {
        return storage.remove(id) != null;
    }

    @Override
    public int count() {
        return storage.size();
    }
}
