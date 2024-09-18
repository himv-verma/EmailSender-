package com.himanshu.emailservice.model;

public class Product {
	private String name;
	private String link;
	private String imgUrl;
	private String desc;
	
	public Product(String name, String link, String imgUrl, String desc) {
		super();
		this.name = name;
		this.link = link;
		this.imgUrl = imgUrl;
		this.desc = desc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	
}
