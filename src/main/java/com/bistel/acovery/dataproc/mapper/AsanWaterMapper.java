package com.bistel.acovery.dataproc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.bistel.acovery.dataproc.model.AssetParameter;
import com.bistel.acovery.dataproc.model.ParameterConfig;
import com.bistel.acovery.dataproc.model.ParameterData;
import com.bistel.acovery.dataproc.model.SpectrumData;

public interface AsanWaterMapper {
	List<ParameterData> selectParameterData(@Param("startTime") String startTime, @Param("endTime") String EndTime,
			@Param("ids") List<String> ids);

	List<ParameterConfig> selectParameterConfig();
	
	List<AssetParameter> selectAssetParameter();

	List<SpectrumData> selectSpectrumData(@Param("startTime") String startTime, @Param("endTime") String endTime,
			@Param("ids") List<String> ids);

	List<ParameterData> selectParameterDataTargetTime(@Param("targetTime") String targetTime, @Param("ids") List<String> ids);

}
