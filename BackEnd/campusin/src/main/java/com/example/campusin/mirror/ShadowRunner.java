package com.example.campusin.mirror;

@FunctionalInterface
public interface ShadowRunner<R> {
    R run() throws Exception;
}
