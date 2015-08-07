package com.tscale.tsexplorer.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rd-19 on 2015/7/29.
 */
public class Product {
    public String product_num;

    public String product_name;

    public String abbr;

    public String barcode;

    public String price;

    public String unit_text;

    public String name_spell;

    public String abbr_spell;


    public String getProduct_num() {
        return product_num;
    }

    public void setProduct_num(String product_num) {
        this.product_num = product_num;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit_text() {
        return unit_text;
    }

    public void setUnit_text(String unit_text) {
        this.unit_text = unit_text;
    }

    public String getName_spell() {
        return name_spell;
    }

    public void setName_spell(String name_spell) {
        this.name_spell = name_spell;
    }

    public String getAbbr_spell() {
        return abbr_spell;
    }

    public void setAbbr_spell(String abbr_spell) {
        this.abbr_spell = abbr_spell;
    }

    public Map<String,Object> productToMap(){
        Map<String,Object> a = new HashMap<>();
        a.put("product_num",getProduct_num());
        a.put("product_name",getProduct_name());
        a.put("abbr",getAbbr());
        a.put("barcode",getBarcode());
        a.put("price",getPrice());
        a.put("unit_text", getUnit_text());
        a.put("name_spell",getName_spell());
        a.put("abbr_spell",getAbbr_spell());
        return a;
    }
}
