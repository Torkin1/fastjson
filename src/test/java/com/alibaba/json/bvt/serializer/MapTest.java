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
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RunWith(Parameterized.class)
public class MapTest {

    private final Object holder;
    private final Set<SerializerFeature> features;
    private final String expected;
    private final Class<?> jsonParser;
    
   public MapTest(String expected, Object holder, Set<SerializerFeature> features, Class<?> jsonParser, Map<String, Object> couples) throws Exception {
       this.expected = expected;
       this.holder = holder;
       this.features = features;
       this.jsonParser = jsonParser;

       if (holder instanceof JSONObject) {
           configure((JSONObject) holder, couples);
       } else if (holder instanceof MapNullValue) {
           configure((MapNullValue) holder, couples);
       } else {
           throw new Exception(String.format("unsupported holder type: %s", holder.getClass().getName()));
       }

   }

    private void configure(MapNullValue holder, Map<String, ?> couples){
        holder.setMap(new HashMap<>());
        configure(holder.getMap(), couples);
    }
    
    private void configure(Map<String, Object> map, Map<String, ?> couples){
        couples.forEach((k, v) -> map.put(k, v));

    }
    
    private static Map<String, Void> createKeysOnlyMap(String... keys){
        Map<String, Void> onlyKeysMap = new HashMap<>();
        for (String key : keys){
            onlyKeysMap.put(key, null);
        }
        return onlyKeysMap;
    }
    
    @Parameters
    public static Collection getParams(){
        return Arrays.asList(new Object[][]{
            {
                "{'name':'jobs','id':33}",
                (Object)new JSONObject(),
                EnumSet.noneOf(SerializerFeature.class),
                MapTest.class, 
                Map.of("name", "jobs", "id", 33)
            },
            {
                "{\"name\":null}",
                (Object)new JSONObject(), 
                EnumSet.of(SerializerFeature.WriteMapNullValue), 
                JSON.class, 
                createKeysOnlyMap("name")
            },
            {
                "{\"map\":{\"Ariston\":null}}", 
                (Object)new MapTest.MapNullValue(), 
                EnumSet.noneOf(SerializerFeature.class), 
                JSON.class, 
               createKeysOnlyMap("Ariston")
            }

		});
    }
    
    @Test
    public void test() throws Exception{
        
        String actual;
        if (jsonParser == MapTest.class){
            actual = MapTest.toJSONString(holder);
        } else if (jsonParser == JSON.class){
            actual = JSON.toJSONString(holder, features.toArray(new SerializerFeature[0]));
        } else {
            throw new Exception(String.format("Unsupported json parser class specified: %s", jsonParser.getName()));
        }
        assertEquals(expected, actual);
    }

    public static final String toJSONString(Object object) {
        SerializeWriter out = new SerializeWriter();

        try {
            JSONSerializer serializer = new JSONSerializer(out);
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
