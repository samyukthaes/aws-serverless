package com.home.amazon.serverless.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.Objects;

@DynamoDbBean
public class Employee {

    public static final String PARTITION_KEY = "psno";

    @JsonProperty(PARTITION_KEY)
    private String psno;

    @JsonProperty("author")
    private String employeefullname;

    @JsonProperty("emailid")
    private String emailid;

    @JsonProperty("phonenumber")
    private String phonenumber;

    @JsonProperty("location")
    private String location;

    @JsonProperty("managername")
    private String managername;

    @JsonProperty("company")
    private String company;

    @DynamoDbPartitionKey
    public String getPsno() {
        return psno;
    }

    public void setPsno(String psno) {
        this.psno = psno;
    }

    public String getEmployeefullname() {
        return employeefullname;
    }

    public void setEmployeefullname(String employeefullname) {
        this.employeefullname = employeefullname;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getManagername() {
        return managername;
    }

    public void setManagername(String managername) {
        this.managername = managername;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee book = (Employee) o;
        return psno.equals(book.psno);
    }

    @Override
    public int hashCode() {
        return Objects.hash(psno);
    }

}

