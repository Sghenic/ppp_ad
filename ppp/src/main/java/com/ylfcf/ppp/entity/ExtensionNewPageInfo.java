package com.ylfcf.ppp.entity;

import java.util.List;

/**
 * �ƹ������ҳ���� ���µĽӿڣ�
 * @author Mr.liu
 *
 */
public class ExtensionNewPageInfo implements java.io.Serializable{

	private static final long serialVersionUID = 3268698894885514135L;
	
	private String list;
	private String total;
	private String extension_user_count;//�Ƽ��ĺ��ѵĸ���
	private String reward_total;//�ܹ�׬ȡ�˶���
	private List<ExtensionNewInfo> extensionList;
	
	
	public String getList() {
		return list;
	}


	public void setList(String list) {
		this.list = list;
	}


	public String getTotal() {
		return total;
	}


	public void setTotal(String total) {
		this.total = total;
	}


	public String getExtension_user_count() {
		return extension_user_count;
	}


	public void setExtension_user_count(String extension_user_count) {
		this.extension_user_count = extension_user_count;
	}


	public String getReward_total() {
		return reward_total;
	}


	public void setReward_total(String reward_total) {
		this.reward_total = reward_total;
	}


	public List<ExtensionNewInfo> getExtensionList() {
		return extensionList;
	}

	public void setExtensionList(List<ExtensionNewInfo> extensionList) {
		this.extensionList = extensionList;
	}

}
