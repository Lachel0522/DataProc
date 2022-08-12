package com.bistel.acovery.dataproc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.bistel.acovery.asset.model.Asset;
import com.bistel.acovery.dataproc.model.AssetParameter;
import com.bistel.acovery.dataproc.model.ParameterConfig;
import com.bistel.acovery.dataproc.model.ParameterData;
import com.bistel.acovery.dataproc.service.AsanWaterService;
import com.bistel.acovery.message.ApmMessage;

public class AsanWaterDataProc extends AbstractDataProc{
	private static Logger logger = Logger.getLogger(AsanWaterDataProc.class);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private long accessInterval;
	private long startTime;
	
	private String sensortype;
	private String sensorid;

	private List<String> idList;
	private List<String> asset_parameter;
	
	Map<String, List<String>> parameter_asset_map = new HashMap<String, List<String>>();
	Map<List<String>, List<String>> sensorhash = new HashMap<List<String>, List<String>>();
	

	public AsanWaterDataProc(Asset asset, BlockingQueue<ApmMessage> blockingQueue) {
		super(asset, blockingQueue);
	}

	// 수행하기 전에 필요한 정보 Loading 하는 곳
	@Override
	public void initialize(Properties interfaceConfig) throws Exception {
		// get and initialize ( interval, starttime, idlist )
		
		String tempAccessInterval = interfaceConfig.getProperty("interface.datasource.accessInterval");
		if (tempAccessInterval == null || tempAccessInterval.isEmpty()) {
			throw new Exception("");
		}
		accessInterval = Long.parseLong(tempAccessInterval); 
		String tempStartTime = interfaceConfig.getProperty("interface.datasource.startTime");
		if (tempStartTime == null || tempAccessInterval.isEmpty()) {
			throw new Exception("");
		}
		startTime = sdf.parse(tempStartTime).getTime();
		
//		List<ParameterConfig> pcList = AsanWaterService.getParameterConfig();
//		idList = new ArrayList<>();
//		for (ParameterConfig pc : pcList) {
//			idList.add(pc.getName());		
//			System.out.println("===========List============");
//			System.out.println(idList);
//		}
		
		List<AssetParameter> param_list = AsanWaterService.getAssetParameter();
		

		for (AssetParameter pl : param_list) {
			asset_parameter = Arrays.asList(pl.toString().split(","));
			
			List<String> asset_list = new ArrayList<String>();

			sensorhash.put(Arrays.asList(asset_parameter.get(0),asset_parameter.get(1)),Arrays.asList(asset_parameter.get(2).trim(),asset_parameter.get(3).trim()));
			
			
			
			if( parameter_asset_map.containsKey(asset_parameter.get(0))) {
				asset_list = parameter_asset_map.get(asset_parameter.get(0));
				asset_list.add(asset_parameter.get(1));
				parameter_asset_map.put(asset_parameter.get(0),asset_list);
				
			}
			else {
				asset_list.add(asset_parameter.get(1));
				parameter_asset_map.put(asset_parameter.get(0),asset_list);
			}
		}
	}
	// 수행 할 곳
	@Override
	public void run() {
		try {
			while (true) {
				long beforeTime = System.currentTimeMillis();
				System.out.println("parameter_map" + parameter_asset_map.keySet());
				System.out.println("idList" + idList);
				// start 
//				System.out.println("current time : ");
//				SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
//				Date date = new Date(System.currentTimeMillis());
//				System.out.println(formatter.format(date));

				String start = sdf.format(new Date(startTime));
				String end = sdf.format(new Date(startTime + accessInterval));
				List<String> idList = parameter_asset_map.keySet().stream().collect(Collectors.toCollection(ArrayList::new));
				
				List<ParameterData> pdList = AsanWaterService.getParameterData(start, end, idList);
				logger.info("[" + asset.getId() + "] parameterCount : " + pdList.size() + ", during : " + start + " - " + end);
				TreeMap<String, HashMap<String, String>> treeMap = new TreeMap<>();
				
				// Asset 에 Physical Name, 실제 고객사 DB에 들어가이쓴 Parameter Name을 변환해주는 부분 필요.
				Map<String,String> parameterMap = asset.getParameterIdMap();

				// Map (고객사 DB Parameter Name, PhysicalName)
				for (ParameterData pd : pdList) {
					String time = pd.getTime();
					if (treeMap.get(time) == null) {
						treeMap.put(time, new HashMap<>());
					}
					
					String pName = pd.getName();
					HashMap<String, String> tempMap = treeMap.get(time);
					if (StringUtils.isBlank(pName)) {
						throw new Exception("Parameter format is invalid. parameter : " + pd.getName());
					}
					tempMap.put(pName, pd.getValue());
					treeMap.put(time, tempMap);
				}
				// end

				for (String time : treeMap.keySet()) { // 시간맵
					HashMap<String, String> tempMap = treeMap.get(time); // 파라미터 맵 
//					long lTime = sdf.parse(time).getTime() - 27000000;
					long lTime = sdf.parse(time).getTime();
					

					Set<String> keys = tempMap.keySet();
					
					for ( String key : keys ) {
						System.out.println("ttttttt ===================== " + keys);
						ArrayList<String> assetvalues = (ArrayList<String>) parameter_asset_map.get(key);
						
						for (String assetvalue : assetvalues ) {
							sensortype = sensorhash.get(Arrays.asList(key, assetvalue)).get(0);
							sensorid = sensorhash.get(Arrays.asList(key, assetvalue)).get(0);
//							System.out.println(new ApmMessage(assetvalue.trim(), sensortype, sensorid, lTime, tempMap));
//							blockingQueue.put(new ApmMessage(assetvalue.trim(), sensortype, sensorid, lTime, tempMap));
						}
					}
				}

				startTime = startTime + accessInterval;
				Thread.sleep(accessInterval );
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
