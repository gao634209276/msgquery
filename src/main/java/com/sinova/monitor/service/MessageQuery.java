package com.sinova.monitor.service;

import java.util.Date;

/**
 * 业务处理接口
 * Created by Noah on 2017/3/31.
 */
public interface MessageQuery {

	/**
	 * 根据提供的关键字和其他选项，分页查询Elasticsearch，返回对应页除详情外的基本信息
	 *
	 * @param indices   查询需要的索引
	 * @param inter     用户选择的业务编码
	 * @param keywords  用户选择的关键字，为：mobile或者transid
	 * @param startDate 查询索引和timestamp的起始时间
	 * @param endDate   查询索引和timestamp的终止时间
	 * @param pageNum   用户查询页面所在的页码
	 * @param pagesize  分页大小
	 * @return 返回json格式的数据，供前端ajax获取
	 */
	String queryMessage(String[] indices, String inter, String keywords,
	                    Date startDate, Date endDate,
	                    int pageNum, int pagesize);

	/**
	 * 根据指定某一条报文，进行详情查询，一般通过transid查询
	 *
	 * @param indices   查询需要的索引
	 * @param mobile    手机号
	 * @param transid   唯一id
	 * @param startDate 查询索引和timestamp的起始时间
	 * @param endDate   查询索引和timestamp的终止时间
	 * @return 返回json格式的数据，供前端ajax获取
	 */
	String queryDetail(String[] indices, String mobile, String transid,
	                   Date startDate, Date endDate);
}
