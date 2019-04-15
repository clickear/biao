/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.market.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.market.entity.MkRelayRemitLog;

/**
 * 接力撞奖打款日志DAO接口
 * @author zzj
 * @version 2018-09-05
 */
@MyBatisDao
public interface MkRelayRemitLogDao extends CrudDao<MkRelayRemitLog> {
	
}