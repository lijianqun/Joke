package com.jamtu.bean;

import com.jamtu.db.library.db.annotation.Column;
import com.jamtu.db.library.db.annotation.Id;
import com.jamtu.db.library.db.annotation.Table;
import com.jamtu.db.library.db.annotation.Transient;

@Table(name = "ijoke")
public class Joke extends Entity {
	@Id
	private int id;
	@Column(column = "title")
	private String title;
	@Column(column = "content")
	private String content;
	@Column(column = "type")
	private String type;
	@Column(column = "typeDesc")
	private String typeDesc;
	@Transient
	private Author author;
	@Transient
	private String img;
	@Transient
	private String statsVote;

	public int getId() {
		_id = String.valueOf(id);
		return id;
	}

	public void setId(int id) {
		this.id = id;
		_id = String.valueOf(this.id);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeDesc() {
		return typeDesc;
	}

	public void setTypeDesc(String typeDesc) {
		this.typeDesc = typeDesc;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getStatsVote() {
		return statsVote;
	}

	public void setStatsVote(String statsVote) {
		this.statsVote = statsVote;
	}

	public boolean hasAuthor() {
		if (null == author)
			return false;
		else
			return true;
	}

	@Override
	public String toString() {
		return "Joke [id=" + id + ", title=" + title + ", content=" + content + ", type=" + type + ", typeDesc="
				+ typeDesc + ", author=" + author + ", img=" + img + ", statsVote=" + statsVote + "]";
	}

}
