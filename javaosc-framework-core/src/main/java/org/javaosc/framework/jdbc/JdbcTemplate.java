package org.javaosc.framework.jdbc;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.javaosc.framework.constant.Constant;
import org.javaosc.framework.constant.Page;
import org.javaosc.framework.jdbc.core.JdbcHandler;
import org.javaosc.framework.jdbc.handler.BeanHandler;
import org.javaosc.framework.jdbc.handler.BeanListHandler;
import org.javaosc.framework.jdbc.handler.ColumnListHandler;
import org.javaosc.framework.jdbc.handler.MapHandler;
import org.javaosc.framework.jdbc.handler.MapListHandler;
import org.javaosc.framework.jdbc.handler.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @description
 * @author Dylan Tao
 * @date 2014-09-09
 * Copyright 2014 Javaosc Team. All Rights Reserved.
 */
public class JdbcTemplate{
	
	private static final Logger log = LoggerFactory.getLogger(JdbcTemplate.class);
	
	private static JdbcHandler jdbcHandler;
	
	static{
		jdbcHandler = new JdbcHandler();
	}
	
	/*=================== 查询单列操作 =====================*/
	
	public <T> T queryForColumn(String sql,String columnName, Class<T> cls, Object... param){
		T obj = null;
		try {
			if(param != null && param.length > 0){
				obj = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new ScalarHandler<T>(columnName), param);
			}else{
				obj = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new ScalarHandler<T>(columnName));
			}
		} catch (SQLException e) {
			log.error(Constant.JAVAOSC_EXCEPTION, e);
		}
		return obj;
	}
	
	public <T> List<T> queryForColumnList(String sql,String columnName,Class<T> cls, Object... param){
		List<T> list = null;
		try {
			if(param != null && param.length > 0){
				list = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new ColumnListHandler<T>(columnName), param);
			}else{
				list = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new ColumnListHandler<T>(columnName));
			}
		} catch (SQLException e) {
			log.error(Constant.JAVAOSC_EXCEPTION, e);
		}
		return list;
	}
	
	/*=================== 查询多列操作 =====================*/
	
	public Map<String, Object> queryForMap(String sql, Object... param){
		 Map<String, Object> map = null;
		 try {
			if(param != null && param.length > 0){
				map = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new MapHandler(), param);
			}else{
				map = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new MapHandler());
			}
		 } catch (SQLException e) {
			 log.error(Constant.JAVAOSC_EXCEPTION, e);
		 }
		 return map;	
	}
	
	public List<Map<String, Object>> queryForMapList(String sql, Object... param){
		 List<Map<String, Object>> list = null;
		 try {
			if(param != null && param.length > 0){
				list = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new MapListHandler(), param);
			}else{
				list = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new MapListHandler());
			}
		 } catch (SQLException e) {
			 log.error(Constant.JAVAOSC_EXCEPTION, e);
		 }
		 return list;	
	}
	
	/*=================== 查询单表或多表操作 =====================*/
	
	public <T> T queryForObject(String sql, Class<T> cls, Object... param){
		T obj = null;
		try {
			if(param != null && param.length > 0){
				obj = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new BeanHandler<T>(cls), param);
			}else{
				obj = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new BeanHandler<T>(cls));
			}
		} catch (SQLException e) {
			log.error(Constant.JAVAOSC_EXCEPTION, e);
		}
		return obj;
	}
	
	public <T> List<T> queryForList(String sql, Class<T> cls, Object... param){
		List<T> list = null;
		try {
			if(param != null && param.length > 0){
				list = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new BeanListHandler<T>(cls), param);
			}else{
				list = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new BeanListHandler<T>(cls));
			}
		} catch (SQLException e) {
			log.error(Constant.JAVAOSC_EXCEPTION, e);
		}
		return list;
	}
	
	public <T> Page<T> queryForPage(String sql, Page<T> page,Class<T> cls, Object... param){
		List<T> list = null;
		
		//where->group by->having-order by->limit
		if(page.isAutoCount()){
			String countSql = SqlHandler.createCount(sql);
			long count = getCount(countSql, Long.class, param);
			page.setTotalCount(count);
		}
		int pageNo = page.getPageNo();
		int pageSize = page.getPageSize();
		int startIndex =(pageNo-1) * pageSize;
		sql = SqlHandler.createLimit(sql, startIndex, pageSize); //limit
		
		list = queryForList(sql, cls, param);
		page.setResult(list);
		return page;
	}
	
	
	/*=================== 扩展操作 =====================*/
	
	public <T> T getCount(String sql, Class<T> cls, Object... param){
		T count = cls.cast(0);
		try {
			if(param != null && param.length > 0){
				count = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new ScalarHandler<T>(1), param);
			}else{
				count = jdbcHandler.query(ConnectionHandler.getConnection(), sql, new ScalarHandler<T>(1));
			}
		} catch (SQLException e) {
			log.error(Constant.JAVAOSC_EXCEPTION, e);
		}
		return count;
	}
	
	/*=================== 单条记录增删改=====================*/
	
	public boolean save(String sql, Object... param){
		return updateHandler(sql, param);
	}
	
	public boolean update(String sql, Object... param){
		return updateHandler(sql, param);
	}
	
	public boolean delete(String sql, Object... param){
		return updateHandler(sql, param);
	}
	
	/*=================== 多条记录增删改=====================*/
	
	public boolean batchSave(String sql, Object[][] params){
		return batchHandler(sql, params);
	}
	
	public boolean batchUpdate(String sql, Object[][] params){
		return batchHandler(sql, params);
	}
	
	public boolean batchDelete(String sql, Object[][] params){
		return batchHandler(sql, params);
	}
	
	
	private boolean updateHandler(String sql, Object... param){
		int index = 0;
		try {
			if(param != null && param.length > 0){
				index = jdbcHandler.update(ConnectionHandler.getConnection(), sql, param);
			}else{
				index = jdbcHandler.update(ConnectionHandler.getConnection(), sql);
			}
		} catch (SQLException e) {
			log.error(Constant.JAVAOSC_EXCEPTION, e);
		}
		return index > 0 ? true : false;
	}
	
	private boolean batchHandler(String sql, Object[][] params){
		try {
			int[] index = jdbcHandler.batch(ConnectionHandler.getConnection(), sql, params);
			return index.length == params.length;	
		} catch (SQLException e) {
			log.error(Constant.JAVAOSC_EXCEPTION, e);
		}
		return false;
	}
}
