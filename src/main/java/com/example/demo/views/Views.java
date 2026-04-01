package com.example.demo.views;

public class Views {

    public interface Public {}
    public interface Internal extends Public {}
    public interface Detail extends Internal {}
    public interface Admin extends Detail {}
    public interface MLFeatures extends Public {}
    public interface Create extends Public {}
    public interface Update extends Public {}
}