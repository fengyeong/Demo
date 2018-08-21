package cn.smbms.dao.provider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.mysql.jdbc.StringUtils;

import cn.smbms.dao.BaseDao;
import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;

@Repository
public class ProviderDaoImpl implements ProviderDao {

	/**
	 * 增加供应商
	 * @param connection
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	@Override
	public int add(Connection connection, Provider provider)
			throws Exception {
		// TODO Auto-generated method stub
		PreparedStatement pstm = null;
		int flag = 0;
		if(null != connection){
			String sql = "insert into smbms_provider (proCode,proName,proDesc," +
					"proContact,proPhone,proAddress,proFax,createdBy,creationDate) " +
					"values(?,?,?,?,?,?,?,?,?)";
			Object[] params = {provider.getProCode(),provider.getProName(),provider.getProDesc(),
								provider.getProContact(),provider.getProPhone(),provider.getProAddress(),
								provider.getProFax(),provider.getCreatedBy(),provider.getCreationDate()};
			flag = BaseDao.execute(connection, pstm, sql, params);
			BaseDao.closeResource(null, pstm, null);
		}
		return flag;
	}

	/**
	 * 通过proId删除Provider
	 * @param delId
	 * @return
	 * @throws Exception
	 */
	@Override
	public int deleteProviderById(Connection connection, String delId)
			throws Exception {
		// TODO Auto-generated method stub
		PreparedStatement pstm = null;
		int flag = 0;
		if(null != connection){
			String sql = "delete from smbms_provider where id=?";
			Object[] params = {delId};
			flag = BaseDao.execute(connection, pstm, sql, params);
			BaseDao.closeResource(null, pstm, null);
		}
		return flag;
	}

	/**
	 * 通过proId获取Provider
	 * @param connection
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@Override
	public Provider getProviderById(Connection connection, String id)
			throws Exception {
		// TODO Auto-generated method stub
		Provider provider = null;
		PreparedStatement pstm = null;
		ResultSet rs = null;
		if(null != connection){
			String sql = "select * from smbms_provider where id=?";
			Object[] params = {id};
			rs = BaseDao.execute(connection, pstm, rs, sql, params);
			if(rs.next()){
				provider = new Provider();
				provider.setId(rs.getInt("id"));
				provider.setProCode(rs.getString("proCode"));
				provider.setProName(rs.getString("proName"));
				provider.setProDesc(rs.getString("proDesc"));
				provider.setProContact(rs.getString("proContact"));
				provider.setProPhone(rs.getString("proPhone"));
				provider.setProAddress(rs.getString("proAddress"));
				provider.setProFax(rs.getString("proFax"));
				provider.setCreatedBy(rs.getInt("createdBy"));
				provider.setCreationDate(rs.getTimestamp("creationDate"));
				provider.setModifyBy(rs.getInt("modifyBy"));
				provider.setModifyDate(rs.getTimestamp("modifyDate"));
			}
			BaseDao.closeResource(null, pstm, rs);
		}
		return provider;
	}

	/**
	 * 修改供应商信息
	 * @param connection
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@Override
	public int modify(Connection connection, Provider provider)
			throws Exception {
		// TODO Auto-generated method stub
		int flag = 0;
		PreparedStatement pstm = null;
		if(null != connection){
			String sql = "update smbms_provider set proName=?,proDesc=?,proContact=?," +
					"proPhone=?,proAddress=?,proFax=?,modifyBy=?,modifyDate=? where id = ? ";
			Object[] params = {provider.getProName(),provider.getProDesc(),provider.getProContact(),provider.getProPhone(),provider.getProAddress(),
					provider.getProFax(),provider.getModifyBy(),provider.getModifyDate(),provider.getId()};
			flag = BaseDao.execute(connection, pstm, sql, params);
			BaseDao.closeResource(null, pstm, null);
		}
		return flag;
	}

	/**
	 * 通过模糊查询-供应商表记录数
	 * @param connection
	 * @param proCode
	 * @param proName
	 * @return
	 * @throws Exception
	 */
	@Override
	public int getUserCount(Connection connection, String proCode,
			String proName) throws Exception {
		// TODO Auto-generated method stub
		PreparedStatement pstm = null;
		ResultSet rs = null;
		int count = 0;
		
		if(connection != null){
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT COUNT(1) AS COUNT FROM smbms_provider");
			List<Object> list = new ArrayList<Object>();
			if(!StringUtils.isNullOrEmpty(proCode)){
				sql.append(" and proCode like ?");
				list.add("%"+proCode+"%");
			}
			if(!StringUtils.isNullOrEmpty(proName)){
				sql.append(" and proName like ?");
				list.add("%"+proName+"%");
			}
			Object[] params = list.toArray();
			System.out.println("sql ----> " + sql.toString());
			rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
			if(rs.next()){
				count = rs.getInt("count");
			}
			BaseDao.closeResource(null, pstm, rs);
		}
		return count;
	}
	
	/**
	 * 通过供应商名称、编码获取供应商列表-模糊查询-providerList
	 * @param connection
	 * @param proName
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<Provider> getProviderList(Connection connection, 
			String proName,String proCode,int currentPageNo, int pageSize)
			throws Exception {
		// TODO Auto-generated method stub
		PreparedStatement pstm = null;
		ResultSet rs = null;
		List<Provider> providerList = new ArrayList<Provider>();
		if(connection != null){
			StringBuffer sql = new StringBuffer();
			sql.append("select * from smbms_provider where 1=1 ");
			List<Object> list = new ArrayList<Object>();
			if(!StringUtils.isNullOrEmpty(proName)){
				sql.append(" and proName like ?");
				list.add("%"+proName+"%");
			}
			if(!StringUtils.isNullOrEmpty(proCode)){
				sql.append(" and proCode like ?");
				list.add("%"+proCode+"%");
			}
			sql.append(" order by creationDate DESC limit ?,?");
			currentPageNo = (currentPageNo-1)*pageSize;
			list.add(currentPageNo);
			list.add(pageSize);
			Object[] params = list.toArray();
			System.out.println("sql ----> " + sql.toString());
			rs = BaseDao.execute(connection, pstm, rs, sql.toString(), params);
			while(rs.next()){
				Provider _provider = new Provider();
				_provider.setId(rs.getInt("id"));
				_provider.setProCode(rs.getString("proCode"));
				_provider.setProName(rs.getString("proName"));
				_provider.setProDesc(rs.getString("proDesc"));
				_provider.setProContact(rs.getString("proContact"));
				_provider.setProPhone(rs.getString("proPhone"));
				_provider.setProAddress(rs.getString("proAddress"));
				_provider.setProFax(rs.getString("proFax"));
				_provider.setCreationDate(rs.getTimestamp("creationDate"));
				providerList.add(_provider);
			}
			BaseDao.closeResource(null, pstm, rs);
		}
		return providerList;
	}

}
