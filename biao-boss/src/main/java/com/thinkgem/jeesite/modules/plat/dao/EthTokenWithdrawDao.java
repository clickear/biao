/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.plat.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.plat.entity.EthTokenWithdraw;

/**
 * eth token withdrawDAO接口
 * @author ruoyu
 * @version 2018-07-03
 */
@MyBatisDao
public interface EthTokenWithdrawDao extends CrudDao<EthTokenWithdraw> {
	
}