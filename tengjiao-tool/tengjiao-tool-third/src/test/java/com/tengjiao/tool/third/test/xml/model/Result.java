package com.tengjiao.tool.third.test.xml.model;

public class Result {
  private String OrderNumber;
  private String AcctNo;
  private String BusiType;
  private String CorpId;
  private String CcyCode;
  private java.math.BigDecimal TranAmount;
  private String InAcctNo;
  private String InAcctName;
  private String InAcctBankName;
  private String InAcctBankNode;
  private String Mobile;
  private String Remark;
  
  public String getOrderNumber() {
    return OrderNumber;
  }
  public void setOrderNumber(String orderNumber) {
    OrderNumber = orderNumber;
  }
  public String getAcctNo() {
    return AcctNo;
  }
  public void setAcctNo(String acctNo) {
    AcctNo = acctNo;
  }
  public String getBusiType() {
    return BusiType;
  }
  public void setBusiType(String busiType) {
    BusiType = busiType;
  }
  public String getCorpId() {
    return CorpId;
  }
  public void setCorpId(String corpId) {
    CorpId = corpId;
  }
  public String getCcyCode() {
    return CcyCode;
  }
  public void setCcyCode(String ccyCode) {
    CcyCode = ccyCode;
  }
  public java.math.BigDecimal getTranAmount() {
    return TranAmount;
  }
  public void setTranAmount(java.math.BigDecimal tranAmount) {
    TranAmount = tranAmount;
  }
  public String getInAcctNo() {
    return InAcctNo;
  }
  public void setInAcctNo(String inAcctNo) {
    InAcctNo = inAcctNo;
  }
  public String getInAcctName() {
    return InAcctName;
  }
  public void setInAcctName(String inAcctName) {
    InAcctName = inAcctName;
  }
  public String getInAcctBankName() {
    return InAcctBankName;
  }
  public void setInAcctBankName(String inAcctBankName) {
    InAcctBankName = inAcctBankName;
  }
  public String getInAcctBankNode() {
    return InAcctBankNode;
  }
  public void setInAcctBankNode(String inAcctBankNode) {
    InAcctBankNode = inAcctBankNode;
  }
  public String getMobile() {
    return Mobile;
  }
  public void setMobile(String mobile) {
    Mobile = mobile;
  }
  public String getRemark() {
    return Remark;
  }
  public void setRemark(String remark) {
    Remark = remark;
  }
  @Override
  public String toString() {
    return "Result [OrderNumber=" + OrderNumber + ", AcctNo=" + AcctNo + ", BusiType=" + BusiType + ", CorpId="
        + CorpId + ", CcyCode=" + CcyCode + ", TranAmount=" + TranAmount + ", InAcctNo=" + InAcctNo + ", InAcctName="
        + InAcctName + ", InAcctBankName=" + InAcctBankName + ", InAcctBankNode=" + InAcctBankNode + ", Mobile="
        + Mobile + ", Remark=" + Remark + "]";
  }
  
  
}
