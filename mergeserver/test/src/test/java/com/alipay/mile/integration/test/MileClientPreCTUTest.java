package com.alipay.mile.integration.test;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.alipay.mile.client.ApplationClientImpl;
import com.alipay.mile.client.result.MileDeleteResult;
import com.alipay.mile.client.result.MileInsertResult;
import com.alipay.mile.client.result.MileQueryResult;
import com.alipay.mile.integration.MileClientAbstract;


/**
 *mile基本应用的集成测试类
 * @author xiaoju.luo
 * @version $Id: MileClientSelectAllTest.java, v 0.1 2011-11-21 下午02:13:37 xiaoju.luo $
 *
 */
public class MileClientPreCTUTest extends MileClientAbstract {
    private static final Logger LOGGER     = Logger.getLogger(MileClientPreCTUTest.class.getName());
    int timeOut = 3000;
   ApplationClientImpl ac;
    @Before
    public void setUp() {
		try {
			super.setUp();
		} catch (UnsupportedEncodingException e) {
		   Assert.fail();
		} 
    }



    @Test
    public void testPrectu() {
         
//
   //     ac = new ApplationClientImpl();
//        String filePath = System.getProperty("user.dir") + File.separator
//                          + "mileCliClent.properties.prod";
//        ac.readProperties(filePath);
//        try {
//            ac.init();
//        } catch (UnsupportedEncodingException e1) {
//           //log.error("",e1);
//           Assert.fail("配置读取失败");
//        }
        

        SAXBuilder builder = new SAXBuilder(false); //表示默认解析器
        Document doc = null;
        try {
         String   filePath = System.getProperty("user.dir") + File.separator + "testdata.xml";
            doc = builder.build(new FileInputStream(filePath));
        } catch (JDOMException e) {
            Assert.fail("XML读取失败");
        } catch (IOException e) {
            Assert.fail("IO读取失败");
        }

        Element root = doc.getRootElement();
        @SuppressWarnings("unchecked")
        List<Element> groupList = root.getChildren();
        for (Element group : groupList) {
            @SuppressWarnings("unchecked")
            List<Element> statementList = group.getChild("statements").getChildren();
            for (Element statement : statementList) {
            	if(LOGGER.isInfoEnabled()) {
	                LOGGER.info("开始测试 ：第" + statement.getChildText("id") + "");
            	}
                String type = statement.getAttributeValue("type");
                String sql = statement.getChildText("sql");
                @SuppressWarnings("unchecked")
                List<Element> paramList = statement.getChild("params").getChildren();
                Object[] params = null;
                if (paramList != null && paramList.size() > 0) {
                    params = new Object[paramList.size()];
                    int i = 0;
                    for (Element param : paramList) {
                        params[i] = getObjectValue(param.getAttributeValue("type"), param.getText());
                        i++;
                    }
                }
                try {
                    if (type.equals("preInsert")) {
                       // MileInsertResult iResult = ac.preInsert(sql, params, timeOut);
                    	
                    	MileInsertResult iResult = applationClientImpl.preInsert(sql, params, timeOut);
                        if (iResult.isSuccessful()) {
                        	if(LOGGER.isInfoEnabled()) {
	                            LOGGER.info("插入成功：" + "docid是: " + iResult.getDocId());
                        	}
                        } else {
                            Assert.fail("插入失败");
                        }
                    } else if (type.equals("preQuery")) {
                        String result = statement.getChildText("result").trim();
                        MileQueryResult sResult = applationClientImpl.preQueryForList(sql, params, timeOut);
                        String actualResult = sResult.toString().trim();
                        if (result.equals(actualResult)) {
                        	if(LOGGER.isInfoEnabled()) {
	                            LOGGER.info("查询成功:" + "组" + group.getAttributeValue("id") + ":"
	                                               + group.getAttributeValue("name"));
                        	}
                        } else {
                            Assert.fail("查询失败:" + "组" + group.getAttributeValue("id") + "测试"
                                        + group.getAttributeValue("name") + ";" + "预期结果:" + result
                                        + "实际结果为" + sResult.toString());
                        }
                    } else if (type.equals("preDelete")) {
                        MileDeleteResult dResult = applationClientImpl.preDelete(sql, params, timeOut);
                        if (dResult.isSuccessful()) {
                        	if(LOGGER.isInfoEnabled()) {
	                            LOGGER.info("删除成功：" + dResult.toString());
                        	}
                        } else {
                            Assert.fail("删除失败");
                        }
                    }
                } catch (Exception e) {
                    //log.error("",e);
                }
            }
        }
        applationClientImpl.close();
    }

    private static Object getObjectValue(String paraType, String paraText) {
        Object objectValue;
        if ("Double".equalsIgnoreCase(paraType)) {
            objectValue = Double.valueOf(paraText);
        } else if ("Float".equalsIgnoreCase(paraType)) {
            objectValue = Float.valueOf(paraText);
        } else if ("Integer".equalsIgnoreCase(paraType)) {
            objectValue = Integer.valueOf(paraText);
        } else if ("Short".equalsIgnoreCase(paraType)) {
            objectValue = Short.valueOf(paraText);
        } else if ("Long".equalsIgnoreCase(paraType)) {
            objectValue = Long.valueOf(paraText);
        } else if ("Byte".equalsIgnoreCase(paraType)) {
            objectValue = Byte.valueOf(paraText);
        } else if ("Boolean".equalsIgnoreCase(paraType)) {
            objectValue = Boolean.valueOf(paraText);
        } else
            objectValue = paraText;

        return objectValue;
    }




    @After
    public void tearDown() throws Exception {
    }

}
