package org.abdelhafeez;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class ProductDao {

    private final Jedis jedis;
    private final ObjectMapper mapper;
    private static final String NAME_SPACE = "PRD";

    public ProductDao() {
        this.jedis = new Jedis("localhost", 6379);
        this.mapper = new ObjectMapper();
    }

    public void saveProduct(Product product) throws JsonProcessingException {
        Transaction trx = jedis.multi();
        Map<String, String> map = new HashMap<>();
        String key = NAME_SPACE + product.getId();
        String json = toJson(product);
        map.put(key, json);
        trx.hset(NAME_SPACE, map);
        trx.exec();
    }

    public void saveAll(List<Product> list) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        for (Product prd : list) {
            String key = NAME_SPACE + prd.getId();
            String json = toJson(prd);
            map.put(key, json);
        }
        Transaction trx = jedis.multi();
        trx.hmset(NAME_SPACE, map);
        trx.exec();
    }

    public Product getProduct(Long id) throws JsonProcessingException {
        String key = NAME_SPACE + id;
        String json = jedis.get(key);
        return fromJson(json);
    }

    public List<Product> getAllProducts() throws JsonProcessingException {
        Map<String, String> map = jedis.hgetAll(NAME_SPACE);
        List<Product> list = new LinkedList<>();
        for (String val : map.values()) {
            Product prd = mapper.readValue(val, Product.class);
            list.add(prd);
        }
        return list;
    }

    public void updateProduct(Product product) throws JsonProcessingException {

        Map<String, String> map = new HashMap<>();
        String key = NAME_SPACE + product.getId();
        String json = toJson(product);
        map.put(key, json);
        jedis.hset(NAME_SPACE, map);
    }

    public void deleteProduct(Long id) {
        String key = NAME_SPACE + id;
        jedis.hdel(NAME_SPACE, key);
    }

    // Utility method to convert Product object to JSON string
    private String toJson(Product product) throws JsonProcessingException {
        return mapper.writeValueAsString(product);
    }

    // Utility method to convert JSON string to Product object
    private Product fromJson(String json) throws JsonProcessingException {
        return mapper.readValue(json, Product.class);
    }

    // Close the Jedis connection when done
    public void close() {
        jedis.close();
    }
}
