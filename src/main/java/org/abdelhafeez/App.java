package org.abdelhafeez;

import java.util.Arrays;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {

        // String x = "prd:1";
        // System.out.println(x.substring(x.indexOf(":") + 1));

        Product prd = new Product(1l, "product-1", 20);
        Product prd2 = new Product(2l, "product-2", 35);
        Product prd3 = new Product(3l, "product-3", 45);

        List<Product> ins = Arrays.asList(prd, prd2, prd3);

        System.out.println("Hello World!");
        try {
            ProductDao dao = new ProductDao();
            dao.saveAll(ins);
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
