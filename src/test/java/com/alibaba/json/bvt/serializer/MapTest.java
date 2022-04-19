package com.alibaba.json.bvt.serializer;

import com.alibaba.fastjson.annotation.JSONField;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.OrderWith;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class MapTest {

    private enum MapTests{
        TEST_NO_SORT("test_no_sort"),
        TEST_NULL("test_null"),
        TEST_ON_JSON_FIELD("test_onJSONField");

        public final String testName;

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
    
    private JSONObject obj;
    private MapNullValue mapNullValue;
    private String expected;
    @Rule
    public TestName testName = new TestName();

    
    public MapTest(){
        
    }

    
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

    @Test
    public void test_onJSONField() {
        String json = JSON.toJSONString( mapNullValue );
        Assert.assertEquals(expected, json);
    }

    class MapNullValue {
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
