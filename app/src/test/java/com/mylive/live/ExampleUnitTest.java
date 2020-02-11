package com.mylive.live;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
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
    public void testListGeneric() throws NoSuchFieldException {
        Type type = ExampleUnitTest.class.getDeclaredField("list").getGenericType();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            for (Type type1 : types) {
                System.out.println(type1);
            }
        }
    }

    @Test
    public void testProxy() {
        Proxy proxy = (Proxy) java.lang.reflect.Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[]{Proxy.class}, (proxy1, method, args) -> {
                    System.out.println(method.getDeclaringClass());
                    if (!Proxy.class.isAssignableFrom(method.getDeclaringClass())
                            || method.isDefault()) {
                        try {
                            final Constructor<MethodHandles.Lookup> lookupConstructor
                                    = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                            lookupConstructor.setAccessible(true);
                            Class<?> declaringClass = method.getDeclaringClass();
                            // Used mode -1 = TRUST, because Modifier.PRIVATE failed for me in If 8.
                            MethodHandles.Lookup lookup
                                    = lookupConstructor.newInstance(declaringClass, -1);
                            try {
                                return lookup.findSpecial(declaringClass, method.getName(),
                                        MethodType.methodType(method.getReturnType(),
                                                method.getParameterTypes()), declaringClass)
                                        .bindTo(proxy1)
                                        .invokeWithArguments(args);
                            } catch (IllegalAccessException e) {
                                try {
                                    return lookup.unreflectSpecial(method, declaringClass)
                                            .bindTo(proxy1)
                                            .invokeWithArguments(args);
                                } catch (IllegalAccessException ignore) {
                                }
                            }
                        } catch (Exception ignore) {
                        }
                        return null;
                    }
                    return "proxy";
                });
        System.out.println(proxy.toString());
        System.out.println(proxy.toString());
        System.out.println(proxy.hashCode());
        System.out.println(proxy.def("hhh"));
        System.out.println(proxy.proxy());
    }

    private interface Proxy {
        default String def(String text) {
            return "default:" + text;
        }

        String proxy();
    }
}