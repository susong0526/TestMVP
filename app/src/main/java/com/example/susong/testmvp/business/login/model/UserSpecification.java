package com.example.susong.testmvp.business.login.model;

import com.example.susong.testmvp.database.Specification;

public class UserSpecification implements Specification {
    @Override
    public String toSqlQuery() {
        return "select * from user";
    }
}
