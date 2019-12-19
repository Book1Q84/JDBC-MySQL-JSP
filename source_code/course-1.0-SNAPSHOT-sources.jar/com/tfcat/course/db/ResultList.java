package com.tfcat.course.db;

import java.util.ArrayList;
public class ResultList{
    ArrayList<String> strList;
    ArrayList<String> sqlList;

    public ResultList(){

    }

    public ResultList(ResultList resultList) {
        this.strList=resultList.getStrList();
        this.sqlList=resultList.getSqlList();
    }

    public ArrayList<String> getStrList() {
        return strList;
    }

    public void setStrList(ArrayList<String> strList) {
        this.strList = strList;
    }

    public ArrayList<String> getSqlList() {
        return sqlList;
    }

    public void setSqlList(ArrayList<String> sqlList) {
        this.sqlList = sqlList;
    }
}