package com.bistel.acovery.dataproc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.bistel.acovery.asset.model.Asset;
import com.bistel.acovery.dataproc.model.ParameterConfig;
import com.bistel.acovery.dataproc.model.ParameterData;
import com.bistel.acovery.dataproc.model.SpectrumData;
import com.bistel.acovery.dataproc.service.AsanWaterService;
import com.bistel.acovery.message.ApmFFTMessage;
import com.bistel.acovery.message.ApmMessage;

public class AsanWaterFFTDataProc extends AbstractDataProc {
	private static Logger logger = Logger.getLogger(AsanWaterFFTDataProc.class);

	private SimpleDateFormat parseDateUntilMin = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleDateFormat parseDateUntilSec = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private long accessInterval;
	private long startTime;

	private List<String> idList;

	private Map<String, String> idMap;
	
	public AsanWaterFFTDataProc(Asset asset, BlockingQueue<ApmMessage> blockingQueue) {
		super(asset, blockingQueue);
	}

	@Override
	public void initialize(Properties interfaceConfig) throws Exception {
		String tempAccessInterval = interfaceConfig.getProperty("interface.datasource.accessInterval");
		if (tempAccessInterval == null || tempAccessInterval.isEmpty()) {
			throw new Exception("");
		}
		accessInterval = Long.parseLong(tempAccessInterval);
		String tempStartTime = interfaceConfig.getProperty("interface.datasource.startTime");
		if (tempStartTime == null || tempAccessInterval.isEmpty()) {
			throw new Exception("");
		}
		startTime = parseDateUntilSec.parse(tempStartTime).getTime();
		List<ParameterConfig> pcList = AsanWaterService.getParameterConfig();
		idList = new ArrayList<>();
		for (ParameterConfig pc : pcList) {
			if (pc.getName().contains(asset.getName())) {
				idList.add(pc.getId());
			}
		}
		idMap = asset.getParameterIdMap();
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
				String start = parseDateUntilSec.format(new Date(startTime));
				String end = parseDateUntilSec.format(new Date(startTime + accessInterval));
				List<SpectrumData> spectrumDataList = AsanWaterService.getSpectrumData(start, end, idList);
				TreeMap<String, HashMap<String, String>> treeMap = new TreeMap<>();
				for (SpectrumData spectrumData : spectrumDataList) {
					String time = spectrumData.getTime();
					String targetTime = parseStringDateWithOutSec(time);
					if (targetTime == null)
						continue;
//					String id = spectrumData.getId();
					HashMap<String, String> parameterDataMap = parameterDataListToMap(idMap,
							AsanWaterService.getParameterDataPoints(targetTime, idList));
					if (treeMap.get(targetTime) == null) {
						treeMap.put(targetTime, new HashMap<>());
					}
					HashMap<String, String> tempMap = treeMap.get(targetTime);
					String[] tempArr = spectrumData.getName().split("_");
					String pName = idMap.get(tempArr[1]);
					if (StringUtils.isBlank(pName)) {
						throw new Exception("Parameter format is invalid. parameter : " + pName);
					}

					tempMap.put(pName + "." + "VEL" + "." + "RMS", parameterDataMap.get(tempArr[1]));
					String fftValue = spectrumData.getValue();
					String[] fftArr = fftValue.split(",");
					tempMap.put(pName + "." + "VEL" + "." + "FFT", (Arrays.toString(fftArr)).replace(" ", ""));
					tempMap.put(pName + "." + "VEL" + "." + "FRQ_CNT", fftArr.length + "");
					tempMap.put(pName + "." + "VEL" + "." + "MAX_FRQ", "4096");
					if (tempArr[0].startsWith("로드시뮬레이터")) {
						tempMap.put(pName + "." + "VEL" + "." + "RPM", "4000");
					} else {
						tempMap.put(pName + "." + "VEL" + "." + "RPM", parameterDataMap.get("RPM"));
					}
					tempMap.put(pName + "." + "VEL" + "." + "RPM", parameterDataMap.get("RPM"));
					treeMap.put(targetTime, tempMap);
				}

				for (String time : treeMap.keySet()) {
					HashMap<String, String> tempMap = treeMap.get(time);
					long lTime = parseDateUntilSec.parse(time).getTime() - 27000000;
//					blockingQueue.put(new ApmFFTMessage(asset.getId(), lTime, tempMap));
				}

				startTime = startTime + accessInterval;
				Thread.sleep(accessInterval);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}
	
	private HashMap<String, String> parameterDataListToMap(Map<String, String> idMap,
			List<ParameterData> parameterDataList) {
		HashMap<String, String> result = new HashMap<>();
		for (ParameterData pd : parameterDataList) {
			String pName = pd.getName().split("_")[1];
			result.put(pName, pd.getValue());
		}
		return result;
	}

	private String parseStringDateWithOutSec(String source) throws ParseException {
		Date temp = parseDateUntilMin.parse(source);
		int min = temp.getMinutes();
		if (min % 10 != 0) {
			return null;
		}
		return parseDateUntilSec.format(temp);
	}

}
