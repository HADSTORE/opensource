package com.operation.model;

import java.sql.Blob;

public class HsPartnershipInfoHistory extends SuperModel{
	private String USERID;
	private String PARTNERNAME;
	private String PARTNERTITLEICON;
	private String PARTNERWEBADDRES;
	private Long PARTNERSEQ;
	private String PARTNERAPPSYSID;
	private String DOWNUSERID;
	private Long PARTNERAPPPOINT;
	private Long PARTNERMAXCOUNT;
	private String PARTNERAPPSTATUS;
	private String CREATETIME;
	private String EXPIRETIME;
	private String EVENTTIME;
	public String getUSERID() {
		return USERID;
	}
	public void setUSERID(String uSERID) {
		USERID = uSERID;
	}
	public String getPARTNERNAME() {
		return PARTNERNAME;
	}
	public void setPARTNERNAME(String pARTNERNAME) {
		PARTNERNAME = pARTNERNAME;
	}

	public String getPARTNERTITLEICON() {
		return PARTNERTITLEICON;
	}
	public void setPARTNERTITLEICON(String pARTNERTITLEICON) {
		PARTNERTITLEICON = pARTNERTITLEICON;
	}
	public String getPARTNERWEBADDRES() {
		return PARTNERWEBADDRES;
	}
	public void setPARTNERWEBADDRES(String pARTNERWEBADDRES) {
		PARTNERWEBADDRES = pARTNERWEBADDRES;
	}
	public Long getPARTNERSEQ() {
		return PARTNERSEQ;
	}
	public void setPARTNERSEQ(Long pARTNERSEQ) {
		PARTNERSEQ = pARTNERSEQ;
	}
	public String getPARTNERAPPSYSID() {
		return PARTNERAPPSYSID;
	}
	public void setPARTNERAPPSYSID(String pARTNERAPPSYSID) {
		PARTNERAPPSYSID = pARTNERAPPSYSID;
	}
	public String getDOWNUSERID() {
		return DOWNUSERID;
	}
	public void setDOWNUSERID(String dOWNUSERID) {
		DOWNUSERID = dOWNUSERID;
	}
	public Long getPARTNERAPPPOINT() {
		return PARTNERAPPPOINT;
	}
	public void setPARTNERAPPPOINT(Long pARTNERAPPPOINT) {
		PARTNERAPPPOINT = pARTNERAPPPOINT;
	}
	public Long getPARTNERMAXCOUNT() {
		return PARTNERMAXCOUNT;
	}
	public void setPARTNERMAXCOUNT(Long pARTNERMAXCOUNT) {
		PARTNERMAXCOUNT = pARTNERMAXCOUNT;
	}
	public String getPARTNERAPPSTATUS() {
		return PARTNERAPPSTATUS;
	}
	public void setPARTNERAPPSTATUS(String pARTNERAPPSTATUS) {
		PARTNERAPPSTATUS = pARTNERAPPSTATUS;
	}
	public String getCREATETIME() {
		return CREATETIME;
	}
	public void setCREATETIME(String cREATETIME) {
		CREATETIME = cREATETIME;
	}
	public String getEXPIRETIME() {
		return EXPIRETIME;
	}
	public void setEXPIRETIME(String eXPIRETIME) {
		EXPIRETIME = eXPIRETIME;
	}
	public String getEVENTTIME() {
		return EVENTTIME;
	}
	public void setEVENTTIME(String eVENTTIME) {
		EVENTTIME = eVENTTIME;
	}
	
}
