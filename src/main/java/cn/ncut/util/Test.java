package cn.ncut.util;

import java.util.*;

public class Test {
	
	private static Integer payMethod = 6;
	private static Integer twice = 2;
	
	public static void main(String[] args) {
		String s = "id1_30_id2_50_id3_60";
		System.out.println(test(s));
	}
	
	public static Map<String, String> test(String s) {
		
		Map<String, String> returnMap = new HashMap<>();
		Double needMoney = 0.0;
		
		String[] serviceArr = s.split("_");
		Map<String, Double> serviceMap = new LinkedHashMap();
		try {
			for (int i = 0; i < serviceArr.length / twice; i++) {
				for (int j = 0; j < Integer.parseInt(serviceArr[i * twice + 1]); j++) {
					//value值为根据id（[i * twice ]）获取到的服务项目金额serviceArr
					serviceMap.put(serviceArr[i * 2] + "_" + (1 + j), Double.parseDouble(serviceArr[i * twice + 1]));
					needMoney += Double.parseDouble(serviceArr[i * 2 + 1]);
				}
			}
		} catch (Exception e) {
			returnMap.put("msg", "参数传递错误！");
			return returnMap;
		}
		try {
			//不同支付方式剩余的钱，从数据库根据uid取出来
		} catch (Exception e) {
			returnMap.put("msg", "用户不存在！");
			return returnMap;
		}
		List<Double> payList = new LinkedList<>();
		payList.add(1000.0);
		payList.add(2000.0);
		payList.add(3000.0);
		payList.add(400.0);
		payList.add(800.0);
		payList.add(1000.0);
		
		Double sumMoney = 0.0;
		for (int i = 0; i < payList.size(); i++) {
			sumMoney += payList.get(i);
		}
		
		if (needMoney > sumMoney) {
			returnMap.put("msg", "用户余额不足！");
			return returnMap;
		}
		
		
		Iterator<Map.Entry<String, Double>> it = serviceMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Double> entry = it.next();
			Double thisMoney = entry.getValue();
			String thisKey = entry.getKey();
			List<Double> eachPayList = new ArrayList<>();
			for (int i = 0; i < payMethod; i++) {
				if (payList.get(i) >= thisMoney) {
					payList.set(i, payList.get(i) - thisMoney);
					eachPayList.add(thisMoney);
					break;
				} else {
					eachPayList.add(payList.get(i));
					thisMoney -= payList.get(i);
					payList.set(i, 0.0);
				}
			}
			Integer list2Size = eachPayList.size();
			if (list2Size < payMethod) {
				for (int i = 0; i < payMethod - list2Size; i++) {
					eachPayList.add(0.0);
				}
			}
			try {
				//插入数据库操作，记得把用户余额放回去，最好使用一个service，里面放多个方法，这样是一个事务。
				System.out.println(thisKey + ":" + eachPayList);
			} catch (Exception e) {
				returnMap.put("msg", "数据存在异常！");
				return returnMap;
			}
		}
		returnMap.put("msg", "操作成功！");
		return returnMap;
	}
}
