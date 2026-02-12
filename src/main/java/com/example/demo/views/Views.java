package com.example.demo.views;

/**
 * JSON视图定义类
 * 用于控制不同API接口返回不同的字段集合
 */
public class Views {

    public interface Public {}
    public interface Internal extends Public {}
    public interface Detail extends Internal {}
    public interface Admin extends Detail {}
    public interface MLFeatures extends Public {}
    public interface Create extends Public {}
    public interface Update extends Public {}
}