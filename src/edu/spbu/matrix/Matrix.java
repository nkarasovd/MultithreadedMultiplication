package edu.spbu.matrix;


public interface Matrix {
    Matrix mul(Matrix other);
    boolean equals(Object o);
}
