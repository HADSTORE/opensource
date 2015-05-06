package com.operation.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SuperModel implements Serializable{
	protected String img_path;
	protected String img_prvw_path;	
	
	public String eventTime2;
	
	public String file_path;
	public String file_prvw_path;	


	public String getEventTime2() {
		return eventTime2;
	}

	public void setEventTime2(String eventTime2) {
		this.eventTime2 = eventTime2;
	}

	public String getImg_path() {
		if(null != img_path){
			if(!img_path.contains("http://")){
				img_path = SnsConstants.getServerImageInfo() + img_path;
			}
		}
		return img_path;
	}

	public void setImg_path(String img_path) {
		this.img_path = img_path;
	}

	public String getFile_path() {
		if(null != file_path){
			if(!file_path.contains("http://")){
				file_path = SnsConstants.getServerImageInfo() + file_path;
			}
		}
		return file_path;
	}

	public void setFile_path(String file_path) {
		this.file_path = file_path;
	}

	public String getFile_prvw_path() {
		if(null != file_prvw_path){
			if(!file_prvw_path.contains("http://")){
				file_prvw_path = SnsConstants.getServerImageInfo() + file_prvw_path;
			}
		}
		return file_prvw_path;
	}

	public void setFile_prvw_path(String file_prvw_path) {
		this.file_prvw_path = file_prvw_path;
	}

	public String getImg_prvw_path() {
		if(null != img_prvw_path){
			if(!img_prvw_path.contains("http://")){
				img_prvw_path = SnsConstants.getServerImageInfo() + img_prvw_path;
			}
		}
		return img_prvw_path;
	}

	public void setImg_prvw_path(String img_prvw_path) {
		this.img_prvw_path = img_prvw_path;
	}
}
