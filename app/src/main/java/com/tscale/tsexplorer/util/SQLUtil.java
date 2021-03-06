package com.tscale.tsexplorer.util;

/**
 * Created by rd-19 on 2015/7/10.
 */

import android.util.Log;

import com.tscale.tsexplorer.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLUtil {

    public static String TAB_PRODUCT = "tab_product";

    public static String TAB_CHANGE = "tab_change";

    public static String TAB_BAR_REPORT = "tab_bar_report";

    public static String MYSQL_QUERY = "SELECT * FROM " + TAB_PRODUCT;

    public static String MYSQL_DETAILS_WITHOUT_ID = "SELECT * FROM " + TAB_PRODUCT + " WHERE _id = ";

    public static String PLU_REPORT = "SELECT " +
            "\treport_product,\n" +
            "\treport_price,\n" +
            "\tCOUNT(1) AS 'count',\n" +
            "\tSUM(report_net) AS 'total_net',\n" +
            "\tSUM(report_total) AS 'total_price'\n" +
            "FROM\n" +
            TAB_BAR_REPORT +
            "GROUP BY\n" +
            "\treport_product";
    public static String SALE_REPORT = "SELECT\n" +
            "\tsale.suspend_num,\n" +
            "\tpro.product_name,\n" +
            "\tsale.weight,\n" +
            "\tsale.sale_price,\n" +
            "\tsale.sale_total,\n" +
            "\tsale.add_time\n" +
            "FROM\n" +
            "\ttemp_tab_sale sale\n" +
            "INNER JOIN tab_product pro ON sale.product_num = pro.product_num\n" +
            "ORDER BY\n" +
            "\tsuspend_num;";

    public static int CONNECTION_ERROR = 1;

    public static Connection openConnection(String url, String user,
                                            String password) {

        Connection conn = null;
        try {
            final String DRIVER_NAME = "com.mysql.jdbc.Driver";
            Class.forName(DRIVER_NAME);
            conn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            Log.d("SQLUTIL", "ClassNotFoundException");
            return null;
        } catch (SQLException e) {
            Log.d("SQLUTIL", "SQLException" + e.getSQLState());
            return null;
        } catch (Exception e) {
            Log.d("SQLUTIL", "Just exception");
            return null;
        }

        return conn;
    }

    public static JSONArray query(Connection conn, String search) {

        JSONArray array = new JSONArray();

        if (conn == null) {
            return array;
        }

        Statement statement = null;
        ResultSet result = null;
        String sql = SQLUtil.MYSQL_QUERY;
        if (search != null)
            sql += " WHERE name_spell LIKE '%" + search + "%' OR product_num LIKE '%" + search
                    + "%' OR product_name LIKE BINARY '%" + search + "%'";
        sql += " ORDER BY name_spell ASC;";
        try {
            statement = conn.createStatement();
            Log.d("SQLUtil", sql);
            result = statement.executeQuery(sql);
            if (result != null && result.first()) {
                int idColumnIndex = result.findColumn("_id");
                int nameColumnIndex = result.findColumn("product_name");
                int nameSpellColumnIndex = result.findColumn("name_spell");
                while (!result.isAfterLast()) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("id", result.getString(idColumnIndex));
                        object.put("name", result.getString(nameColumnIndex));
                        object.put("name_spell", result.getString(nameSpellColumnIndex));
                        array.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    result.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                    result = null;
                }
                if (statement != null) {
                    statement.close();
                    statement = null;
                }

            } catch (SQLException sqle) {

            }
        }
        return array;
    }

    public static boolean execSQL(Connection conn, String sql) {
        boolean execResult = false;
        if (conn == null) {
            return execResult;
        }

        Statement statement = null;

        try {
            statement = conn.createStatement();
            if (statement != null) {
                execResult = statement.execute(sql);
            }
        } catch (SQLException e) {
            execResult = false;
        }

        return execResult;
    }


    public static JSONArray detail(Connection conn, String id) {
        JSONArray array = new JSONArray();
        if (conn == null) return array;
        Statement statement = null;
        ResultSet result = null;

        try {
            statement = conn.createStatement();
            String sql = SQLUtil.MYSQL_DETAILS_WITHOUT_ID + id + ";";
            result = statement.executeQuery(sql);
            if (result != null && result.first()) {


                while (!result.isAfterLast()) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("product_id", result.getString("_id"));
                        object.put("product_num", result.getString("product_num"));
                        object.put("barcode", result.getString("barcode"));
                        object.put("product_name", result.getString("product_name"));
                        object.put("name_spell", result.getString("name_spell"));
                        object.put("abbr", result.getString("abbr"));
                        object.put("abbr_spell", result.getString("abbr_spell"));
                        object.put("price", result.getString("price"));
                        object.put("unit_text", result.getString("unit_text"));
                        array.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    result.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                    result = null;
                }
                if (statement != null) {
                    statement.close();
                    statement = null;
                }

            } catch (SQLException sqle) {

            }
        }
        return array;
    }

    public static Integer fix(Connection conn, String id, String key, String value) {
        int execResult = 0;
        if (conn == null) {
            return execResult;
        }

        Statement statement = null;
        String sql = "UPDATE " + TAB_PRODUCT + " SET " + key + " = '" + value + "' where _id = " + id + ";";
        Log.d("fix", sql);
        try {
            statement = conn.createStatement();
            if (statement != null) {
                execResult = statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            execResult = 0;
        }

        return execResult;

    }

    public static Integer add(Connection conn, Product product) {
        int execResult = 0;
        ResultSet resultSet = null;
        if (conn == null) {
            return execResult;
        }
        Statement statement = null;

        int max_product_num = 0;
        try {
            statement = conn.createStatement();
            if (statement != null) {
                resultSet = statement.executeQuery("SELECT MAX(product_num) FROM " + TAB_PRODUCT + " ;");
                resultSet.next();
                max_product_num = resultSet.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "INSERT INTO " + TAB_PRODUCT +
                "(product_name,name_spell,product_num,price,unit_text,abbr,abbr_spell,barcode) " +
                "VALUES('" + product.getProduct_name() + "','" + product.getName_spell() + "'," + max_product_num +
                "," + product.getPrice() + ",'" + product.getUnit_text() + "','" + product.getAbbr() + "','" +
                product.getAbbr_spell() + "'," + product.getBarcode() + ");";
        Log.d("add", sql);
        try {
            if (max_product_num != 0) {
                execResult = statement.executeUpdate(sql);
            }
        } catch (SQLException e) {
            execResult = 0;
        }
        if (execResult != 0) {
            try {
                resultSet = statement.executeQuery("SELECT @@IDENTITY;");
                resultSet.next();
                execResult = resultSet.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return execResult;
    }


    public static JSONArray queryForm(Connection conn) {

        JSONArray array = new JSONArray();

        if (conn == null) {
            return array;
        }

        Statement statement = null;
        ResultSet result = null;
        String sql = "SELECT * FROM " + TAB_BAR_REPORT;
        try {
            statement = conn.createStatement();
            Log.d("SQLUtil", sql);
            result = statement.executeQuery(sql);
            if (result != null && result.first()) {
                while (!result.isAfterLast()) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("report_product", result.getInt("report_product"));
                        object.put("report_price", result.getDouble("report_price"));
                        object.put("report_total", result.getDouble("report_total"));
                        object.put("report_net", result.getDouble("report_net"));
                        array.put(object);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    result.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (result != null) {
                    result.close();
                    result = null;
                }
                if (statement != null) {
                    statement.close();
                    statement = null;
                }

            } catch (SQLException sqle) {

            }
        }
        return array;
    }

}
