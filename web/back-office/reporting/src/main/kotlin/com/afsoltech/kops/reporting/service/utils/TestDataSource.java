///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package com.nanobnk.epayment.core.backoffice.reporting.service.utils;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//
//import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//public class TestDataSource {
//
//    static Connection connection=null;
//    public static Connection getConnection() {
//
//        PostgresDataSoure dataSource = new MysqlDataSource();
//        dataSource.setUser("root");
//        dataSource.setPassword("cimi-pc");
////    dataSource.setPort(3307);
//
//        dataSource.setServerName("localhost");
//        dataSource.setPort(3307);
//        dataSource.setDatabaseName("gestcommtn2");
//
//        try {
//            connection = dataSource.getConnection();
//
//            // utilisation de la connexion
//            System.out.println("Connexion reussie");
//            return connection;
////            connection.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    public static void getCloseConnection() throws SQLException {
//        connection.close();
//    }
//    public static void main(String[] args) {
//        try {
//            PuceMvtPeriodeReport pmpr = new PuceMvtPeriodeReport();
//            Map map = new HashMap();
//            map.put("msisdn", "237670005708");
//            map.put("date_debut", "2015/05/20");
//            map.put("date_fin", "2015/05/21");
//
//            String destfile = "."+File.separator + "PuceMvtPeriode"
//                    + System.currentTimeMillis() + ".pdf";
//
//            pmpr.buildReport(map, destfile, getConnection());
//            System.out.println(destfile);
//            getCloseConnection();
//        } catch (Exception ex) {
//            Logger.getLogger(TestDataSource.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//
//}
