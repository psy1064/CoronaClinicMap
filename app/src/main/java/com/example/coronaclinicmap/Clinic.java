package com.example.coronaclinicmap;

import java.io.Serializable;

public class Clinic implements Serializable {
    String number;      // 연변
    String sample;      // 채취가능여부
    String city;        // 도시명
    String district;    // 구
    String name;        // 병원명
    String address;     // 주소
    String phoneNumber; // 대표전화번호

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String findIndex(String name) {
        if(this.name.equals(name)) {
            return number;
        }
        else return null;
    }
}