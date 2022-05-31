package com.alibaba.json.bvt.serializer;

import com.alibaba.fastjson.annotation.JSONField;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RunWith(Parameterized.class)
public class MapTest {

    private final Object holder;    // will hold values to serialize in a JSON
    private final Set<SerializerFeature> features;  // what SerializerFeature we want to enable
    private final String expected;  // expected JSON string
    private final Class<?> jsonParser;  // what parser class we want to use to invoke toJSONString() method
    private final boolean isSerializerFaulty;    // true => the serializer will raise a IOException if we try to write with it

    @Mock
    private ObjectSerializer mockedSerializer;


    public MapTest(String expected, Object holder, Set<SerializerFeature> features, Class<?> jsonParser, Map<String, Object> couples, boolean isSerializerFaulty) throws Exception {
        this.expected = expected;
        this.holder = holder;
        this.features = features;
        this.jsonParser = jsonParser;
        this.isSerializerFaulty = isSerializerFaulty;

        if (holder instanceof JSONObject) {
            configure((JSONObject) holder, couples);
        } else if (holder instanceof MapNullValue) {
            configure((MapNullValue) holder, couples);
        }

   }

    private void configure(MapNullValue holder, Map<String, ?> couples){
        holder.setMap(new HashMap<>());
        configure(holder.getMap(), couples);
    }
    
    private void configure(Map<String, Object> map, Map<String, ?> couples){
        if (couples != null){
            couples.forEach((k, v) -> map.put(k, v));
        }

    }
    
    private static Map<String, Void> createKeysOnlyMap(String... keys){
        Map<String, Void> onlyKeysMap = new HashMap<>();
        for (String key : keys){
            onlyKeysMap.put(key, null);
        }
        return onlyKeysMap;
    }
    
    @Parameters
    public static Collection getParams() {
        return Arrays.asList(new Object[][] {

            // some key-value pairs
            {
                    "{'name':'jobs','id':33}",
                    (Object) new JSONObject(),
                    EnumSet.noneOf(SerializerFeature.class),
                    MapTest.class,
                    Map.of("name", "jobs", "id", 33),
                    false
            },
            // a key without a value
            {
                    "{\"name\":null}",
                    (Object) new JSONObject(),
                    EnumSet.of(SerializerFeature.WriteMapNullValue),
                    JSON.class,
                    createKeysOnlyMap("name"),
                    false
            },
            // a key without a value with a MapNull object as holder
            {
                    "{\"map\":{\"Ariston\":null}}",
                    (Object) new MapTest.MapNullValue(),
                    EnumSet.noneOf(SerializerFeature.class),
                    JSON.class,
                    createKeysOnlyMap("Ariston"),
                    false
            },
            // a null holder
            {
                    "null",
                    null,
                    EnumSet.noneOf(SerializerFeature.class),
                    MapTest.class,
                    null,
                    false
            },
            // triggers a JSONException caused by an IOException
            {
                    "{'name':'jobs','id':33}",
                    (Object) new JSONObject(),
                    EnumSet.noneOf(SerializerFeature.class),
                    MapTest.class,
                    Map.of("name", "jobs", "id", 33),
                    true
            },

        });
    }
    
    @Test
    public void test() throws Exception{
        
        try{
            String actual;
            if (jsonParser == MapTest.class){
                actual = this.toJSONString(holder);
            } else if (jsonParser == JSON.class){
                actual = JSON.toJSONString(holder, features.toArray(new SerializerFeature[0]));
            } else {
                throw new Exception(String.format("Unsupported json parser class specified: %s", jsonParser.getName()));
            }
            assertEquals(expected, actual);
        } catch (Exception e){
            String expectedExceptionName = JSONException.class.getName();
            assertEquals(expectedExceptionName, e.getClass().getName());
        }
    }

    public final String toJSONString(Object object) throws IOException {
        SerializeWriter out = new SerializeWriter();

        try {
            JSONSerializer serializer = new JSONSerializer(out);
            if (isSerializerFaulty) {
                serializer = spy(serializer);
                MockitoAnnotations.initMocks(this);
                doThrow(IOException.class).when(mockedSerializer).write(any(JSONSerializer.class), anyObject(), anyObject(), any(Type.class), anyInt());
                when(serializer.getObjectWriter(holder.getClass())).thenReturn(mockedSerializer);
            }

            
            serializer.config(SerializerFeature.SortField, false);
            serializer.config(SerializerFeature.UseSingleQuotes, true);

            serializer.write(object);

            
             

            return out.toString();
        } catch (StackOverflowError e) {
            throw new JSONException("maybe circular references", e);
        } finally {
            out.close();
        }
    }

    static class MapNullValue {
        @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
        private Map map;

        public Map getMap() {
            return map;
        }

        public void setMap( Map map ) {
            this.map = map;
        }
    }

}
