package com.bistel.acovery.dataproc.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.bistel.acovery.dataproc.db.InterfaceSqlSessionManager;
import com.bistel.acovery.dataproc.mapper.AsanWaterMapper;
import com.bistel.acovery.dataproc.model.ParameterConfig;

public class ParameterConfigService {
	public static List<ParameterConfig> getParameterConfig() {
		List<ParameterConfig> result = null;
		try (SqlSession sqlSession = InterfaceSqlSessionManager.getSqlSession()) {
			AsanWaterMapper mapper = sqlSession.getMapper(AsanWaterMapper.class);
			result = mapper.selectParameterConfig();
		}
		return result;

	}

}
