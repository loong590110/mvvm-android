package com.mylive.live;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void jsonArrayTest() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        Object[] objects = {"1000012", "1000013", true, false,
                new Object[]{"123", 123, new Object[]{"123", 123}},
                new JSONObject("{name: 'aaron', sex: 1}")};
        for (Object object : objects) {
            jsonArray.put(object);
        }
        System.out.println(jsonArray.toString());
    }

    List<String> list;

    @Test
    public void tesListGeneric() throws NoSuchFieldException {
        Type type = ExampleUnitTest.class.getDeclaredField("list").getGenericType();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            for (Type type1 : types) {
                System.out.println(type1);
            }
        }
    }
}