package com.tengjiao.tool.third.test.xml;

import com.tengjiao.tool.third.test.xml.model.Result;
import com.tengjiao.tool.third.xml.XmlTool;
import org.junit.Test;

public class XmlToolTest {

  @Test
  public void testSerialize() {
    Result result = new Result();
    result.setOrderNumber("0KHKF0320191009TV001");
    result.setAcctNo("15000090244196");
    result.setBusiType("00000");
    result.setCorpId("Q000205193");
    result.setCcyCode("RMB");
    result.setTranAmount(new java.math.BigDecimal("30"));
    result.setInAcctNo("6226096555840043");
    result.setInAcctName("招商银行");
    result.setInAcctBankNode("");
    result.setMobile("");
    result.setRemark("代发工资");
    final String xml = XmlTool.getInstance().serialize(result, "Result");
    System.out.println(xml);
  }

  @Test
  public void testDeserialize() {
    Result result = new Result();
    result.setOrderNumber("0KHKF0320191009TV001");
    result.setAcctNo("15000090244196");
    result.setBusiType("00000");
    result.setCorpId("Q000205193");
    result.setCcyCode("RMB");
    result.setTranAmount(new java.math.BigDecimal("30"));
    result.setInAcctNo("6226096555840043");
    result.setInAcctName("招商银行");
    result.setInAcctBankNode("");
    result.setMobile("");
    result.setRemark("代发工资");
    final String xml = XmlTool.getInstance().serialize(result, "Result");

    Result object = XmlTool.getInstance().deserialize(xml, "Result", Result.class);
    
    System.out.println(object.toString());
    
    System.out.println("0KHKF0320191009TV001".substring(7,15));
  }

}
