package com.example.springbatch.entity;

public class StudentJdbc {

    private Long id;

    private String address;


    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StudentJdbc [id=" + id + ", firstName=" + address + " email=" + name
                + "]";
    }

}
