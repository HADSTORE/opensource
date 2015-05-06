package com.operation.model;

import java.sql.Blob;

public class HsBoardInfo extends SuperModel{
	private String boardType;
	private Long boardSequence;
	private String boardUserId;
	private String boardUserNickName;
	private String boardTitle;
	private String boardCreateTime;
	private String boardContents;
	private Integer boardViewCount;
	private String boardImagePath;
	
	
	
	public String getBoardImagePath() {
		return boardImagePath;
	}
	public void setBoardImagePath(String boardImagePath) {
		this.boardImagePath = boardImagePath;
	}
	public Integer getBoardViewCount() {
		return boardViewCount;
	}
	public void setBoardViewCount(Integer boardViewCount) {
		this.boardViewCount = boardViewCount;
	}
	public String getBoardType() {
		return boardType;
	}
	public void setBoardType(String boardType) {
		this.boardType = boardType;
	}
	
	public Long getBoardSequence() {
		return boardSequence;
	}
	public void setBoardSequence(Long boardSequence) {
		this.boardSequence = boardSequence;
	}
	public String getBoardUserId() {
		return boardUserId;
	}
	public void setBoardUserId(String boardUserId) {
		this.boardUserId = boardUserId;
	}
	public String getBoardUserNickName() {
		return boardUserNickName;
	}
	public void setBoardUserNickName(String boardUserNickName) {
		this.boardUserNickName = boardUserNickName;
	}
	public String getBoardTitle() {
		return boardTitle;
	}
	public void setBoardTitle(String boardTitle) {
		this.boardTitle = boardTitle;
	}
	public String getBoardCreateTime() {
		return boardCreateTime;
	}
	public void setBoardCreateTime(String boardCreateTime) {
		this.boardCreateTime = boardCreateTime;
	}
	public String getBoardContents() {
		return boardContents;
	}
	public void setBoardContents(String boardContents) {
		this.boardContents = boardContents;
	}
	
}
