package com.mezyapps.reportanalyst.network_class;

public class IPAddress {
    public IPAddress() {

    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getUn() {
        return un;
    }

    public void setUn(String un) {
        this.un = un;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public IPAddress(String ip, String db, String un, String password) {
//        this.ip = ip;
//        this.db = db;
//        this.un = un;
//        this.password = password;
//    }

    String ip;
    String db;
    String un;
    String password;

//  String ip = "67.211.45.179:1091";
//    String db = "KENAN_APP";
//    String un = "JMDINFOTECH";
//    String password = "Jmd&23Info$10Tech~79";

}