package com.emotiv.cortexv2example.basicfeatures;

class Data {

    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int imageId;
    private Class<?> classActivity;

    Data(String name, int imageId, Class<?> classActivity) {
        this.name = name;
        this.imageId = imageId;
        this.classActivity = classActivity;
    }

    public Class<?> getClassActivity() {
        return classActivity;
    }
}
