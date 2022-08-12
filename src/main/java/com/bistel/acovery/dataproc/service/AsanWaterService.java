package com.bistel.acovery.dataproc.service;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.bistel.acovery.dataproc.db.InterfaceSqlSessionManager;
import com.bistel.acovery.dataproc.mapper.AsanWaterMapper;
import com.bistel.acovery.dataproc.model.ParameterConfig;
import com.bistel.acovery.dataproc.model.AssetParameter;
import com.bistel.acovery.dataproc.model.ParameterData;
import com.bistel.acovery.dataproc.model.SpectrumData;

public class AsanWaterService {
	public static List<ParameterData> getParameterData(String startTime, String endTime, List<String> ids) {
		SqlSession sqlSession = InterfaceSqlSessionManager.getSqlSession();
		AsanWaterMapper mapper = sqlSession.getMapper(AsanWaterMapper.class);
		List<ParameterData> result = null;
		result = mapper.selectParameterData(startTime, endTime, ids);
		sqlSession.close();
		return result;
	}

	public static List<ParameterData> getParameterDataPoints(String targetTime, List<String> ids) {
		SqlSession sqlSession = InterfaceSqlSessionManager.getSqlSession();
		AsanWaterMapper mapper = sqlSession.getMapper(AsanWaterMapper.class);
		List<ParameterData> result = null;
		result = mapper.selectParameterDataTargetTime(targetTime, ids);
		sqlSession.close();
		return result;
	}

	public static List<ParameterConfig> getParameterConfig() {
		SqlSession sqlSession = InterfaceSqlSessionManager.getSqlSession();
		AsanWaterMapper mapper = sqlSession.getMapper(AsanWaterMapper.class);
		List<ParameterConfig> result = null;
		result = mapper.selectParameterConfig();
		sqlSession.close();
		return result;
	}
	
	public static List<AssetParameter> getAssetParameter() {
		SqlSession sqlSession = InterfaceSqlSessionManager.getSqlSession();
		AsanWaterMapper mapper = sqlSession.getMapper(AsanWaterMapper.class);
		List<AssetParameter> result = null;
		result = mapper.selectAssetParameter();
		sqlSession.close();
		return result;
	}

	public static List<SpectrumData> getSpectrumData(String startTime, String endTime, List<String> ids) {
		SqlSession sqlSession = InterfaceSqlSessionManager.getSqlSession();
		AsanWaterMapper mapper = sqlSession.getMapper(AsanWaterMapper.class);
		List<SpectrumData> result = null;
		result = mapper.selectSpectrumData(startTime, endTime, ids);
		sqlSession.close();
		return result;
	}

}
