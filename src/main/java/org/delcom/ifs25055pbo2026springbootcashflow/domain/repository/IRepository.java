package org.delcom.ifs25055pbo2026springbootcashflow.domain.repository;

import java.util.List;
import java.util.Optional;

/**
 * Port penyimpanan generik.
 *
 * @param <T>  tipe entitas
 * @param <ID> tipe pengenal entitas
 */
public interface IRepository<T, ID> {

    /** Menyimpan entitas (menimpa jika ID sudah ada) dan mengembalikannya. */
    T save(T entity);

    Optional<T> findById(ID id);

    /** Mengembalikan salinan daftar seluruh entitas. */
    List<T> findAll();

    /** @return true bila ada entitas yang terhapus. */
    boolean deleteById(ID id);

    int count();
}
