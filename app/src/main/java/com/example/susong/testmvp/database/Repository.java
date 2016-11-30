package com.example.susong.testmvp.database;

import java.util.List;

public interface Repository<T> {
    void insert(T item);
    void update(T item);
    void remove(T item);
    void remove(Specification specification);
    List<T> query(Specification specification);
}
