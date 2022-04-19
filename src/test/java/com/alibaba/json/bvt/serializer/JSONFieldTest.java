package com.alibaba.json.bvt.serializer;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized.Parameters;

import edu.emory.mathcs.backport.java.util.Arrays;

import org.junit.runners.Parameterized;

import java.util.Collection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;


@RunWith(Parameterized.class)
public class JSONFieldTest {
	
	private String expected;
	private VO vo;
	
	private void configure(VO vo, int id, String name){

		vo.setId(id);
		vo.setName(name);
	}
	
	public JSONFieldTest(VO vo, int id, String name, String expected){
		configure(vo, id, name);
		this.vo = vo;
		this.expected = expected;
	}
	
	@Parameters
	public static Collection getParams(){
		return Arrays.asList(new Object[][]{
			{new VO(), 123, "xx", "{\"id\":123}"}
		});
	}
	
	@Test
	public void test_jsonField() throws Exception {
		
		String text = JSON.toJSONString(this.vo);
		Assert.assertEquals(expected, text);
	}

	public static class VO {
		private int id;
		
		@JSONField(serialize=false)
		private String name;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
}
