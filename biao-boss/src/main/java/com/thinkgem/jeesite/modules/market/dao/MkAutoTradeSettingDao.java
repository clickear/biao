/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.market.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.market.entity.MkAutoTradeSetting;

/**
 * 自动交易DAO接口
 * @author zhangzijun
 * @version 2018-08-07
 */
@MyBatisDao
public interface MkAutoTradeSettingDao extends CrudDao<MkAutoTradeSetting> {
	
}