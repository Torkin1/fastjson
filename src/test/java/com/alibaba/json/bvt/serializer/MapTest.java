package com.alibaba.json.bvt.serializer;

import com.alibaba.fastjson.annotation.JSONField;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.OrderWith;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import clojure.lang.Obj;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RunWith(Parameterized.class)
public class MapTest {

    /*private enum MapTests{
        TEST_NO_SORT("test_no_sort"),
        TEST_NULL("test_null"),
        TEST_ON_JSON_FIELD("test_onJSONField");

        public String testName;

        MapTests(String testName){
            this.testName = testName;
        }

        public static MapTests getTestFromName(String name){
            for (MapTests t : MapTests.values()){
                if (t.testName.equals(name)){
                    return t;
                }
            }
            return null;
        }


    }
    */
    //private JSONObject obj;
    //private MapNullValue mapNullValue;
    private final Object holder;
    private final Set<SerializerFeature> features;
    private final String expected;
    private final Class<?> jsonParser;
   // @Rule
   // public TestName testName = new TestName(); 
    
   public MapTest(String expected, Object holder, Set<SerializerFeature> features, Class<?> jsonParser, Map<String, Object>... couples) throws Exception {
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

    private void configure(MapNullValue holder, Map<String, ?>... couples){
        holder.setMap(new HashMap<>());
        configure(holder.getMap(), couples);
        features.add(SerializerFeature.WriteMapNullValue);
    }
    
    private void configure(Map<String, Object> map, Map<String, ?>... couples){
        for ( Map<String, ?> couple : couples){
            couple.forEach((k, v) -> map.put(k, v));
        }

    }
    
    private static Map<String, Void> createOnlyKeysMap(String... keys){
        Map<String, Void> onlyKeysMap = new HashMap<>();
        for (String key : keys){
            onlyKeysMap.put(key, null);
        }
        return onlyKeysMap;
    }
    
    @Parameters
    public static Collection getParams(){
        return Arrays.asList(new Object[][]{
            {"{'name':'jobs','id':33}", (Object)new JSONObject(), EnumSet.noneOf(SerializerFeature.class), MapTest.class, Arrays.asList(Map.of("name", "jobs"), Map.of("id", 33)).toArray((new Map[0]))},
            {"{\"name\":null}", (Object)new JSONObject(), EnumSet.of(SerializerFeature.WriteMapNullValue), JSON.class, Arrays.asList(createOnlyKeysMap("name")).toArray((new Map[0]))},
            {"{\"map\":{\"Ariston\":null}}", (Object)new MapTest.MapNullValue(), EnumSet.noneOf(SerializerFeature.class), JSON.class, Arrays.asList(createOnlyKeysMap("Ariston")).toArray((new Map[0]))}

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
     /*   
    @Before
    public void configure(){
        MapNullValue mapNullValue = new MapNullValue();
        mapNullValue.setMap(new HashMap<String,Object>());
        JSONObject obj = new JSONObject();
        String currentTestName =testName.getMethodName();
        MapTests currentTest = MapTests.getTestFromName(currentTestName);

        switch(currentTest){

            case TEST_NO_SORT:
                obj.put("name", "jobs");
                obj.put("id", 33);
                this.expected = "{'name':'jobs','id':33}";
                break;
            case TEST_NULL:
                obj.put("name", null);
                this.expected = "{\"name\":null}";
                break;

            case TEST_ON_JSON_FIELD:
                mapNullValue.getMap().put("Ariston", null);
                this.expected = "{\"map\":{\"Ariston\":null}}";
                break;
            }
        this.obj = obj;
        this.mapNullValue = mapNullValue;

    }

    
    @After
    public void clean(){
        this.obj = null;
        this.mapNullValue = null;
        this.expected = null;

    }
    
    @Test
    public void test_no_sort() throws Exception {
        String text = toJSONString(obj);
        Assert.assertEquals(expected, text);
    }
    
    @Test

    public void test_null() throws Exception {
        
        String text = JSON.toJSONString(obj, SerializerFeature.WriteMapNullValue);
        Assert.assertEquals(expected, text);
    }
*/
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
/*
    @Test
    public void test_onJSONField() {
        String json = JSON.toJSONString( mapNullValue );
        Assert.assertEquals(expected, json);
    }
*/
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
