package cn.yc.ssh.report.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.yc.ssh.admin.base.util.JsonUtil;
import cn.yc.ssh.report.entity.ReportEntity;

/** 
 * @author 作者姓名 yc  E-mail: ycssh2@163.com
 * @version 创建时间：2014-5-13 下午02:08:38 
 * 类说明 
 */
@RequestMapping("/report")
@Controller
public class ReportController {
	
	@RequestMapping("line")
	@RequiresPermissions("admin:report:line")
	public String line(Model model){
		Double[] data1 = new Double[]{7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6};
		ReportEntity entity1 = new ReportEntity("东京",data1);
		
		Double[] data2 = new Double[]{-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5};
		ReportEntity entity2 = new ReportEntity("纽约",data2);
		
		Double[] data3 = new Double[]{-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0};
		ReportEntity entity3 = new ReportEntity("柏林",data3);
		
		Double[] data4 = new Double[]{3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8};
		ReportEntity entity4 = new ReportEntity("伦敦",data4);
		
		ReportEntity[] datas = new ReportEntity[]{entity1,entity2,entity3,entity4};
        try {
			String result = JsonUtil.mapper.writeValueAsString(datas);
			model.addAttribute("result",result);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return "report/line";
	}
	

	/**
	 * 柱状图实例
	 * @param model
	 * @return
	 */
	@RequestMapping("histogram")
	@RequiresPermissions("report:histogram")
	public String histogram(Model model){
		Double[] data1 = new Double[]{49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4};
		ReportEntity entity1 = new ReportEntity("东京",data1);
		
		Double[] data2 = new Double[]{83.6, 78.8, 98.5, 93.4, 106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3};
		ReportEntity entity2 = new ReportEntity("纽约",data2);
		
		Double[] data3 = new Double[]{48.9, 38.8, 39.3, 41.4, 47.0, 48.3, 59.0, 59.6, 52.4, 65.2, 59.3, 51.2};
		ReportEntity entity3 = new ReportEntity("伦敦",data3);
		
		Double[] data4 = new Double[]{42.4, 33.2, 34.5, 39.7, 52.6, 75.5, 57.4, 60.4, 47.6, 39.1, 46.8, 51.1};
		ReportEntity entity4 = new ReportEntity("柏林",data4);
		
		ReportEntity[] datas = new ReportEntity[]{entity1,entity2,entity3,entity4};
        try {
			String result = JsonUtil.mapper.writeValueAsString(datas);
			model.addAttribute("result",result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "report/histogram";
	}

	@RequestMapping("pie")
	@RequiresPermissions("report:pie")
	public String pie(Model model){
		Object[] firefox = new Object[]{"Firefox",45.0};
		Object[] ie = new Object[]{"IE",26.8};
		Object[] chrome = new Object[]{"Firefox",12.8};
		Object[] safari = new Object[]{"Firefox",8.5};
		Object[] opera = new Object[]{"Firefox",6.2};
		Object[] others = new Object[]{"Firefox", 0.7};
		Object[] datas = new Object[]{firefox,ie,chrome,safari,opera,others};
		try {
			String result = JsonUtil.mapper.writeValueAsString(datas);
			model.addAttribute("result",result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "report/pie";
	}
	
	@RequestMapping("all")
	public String all(Model model){
		Object[] firefox = new Object[]{"Firefox",45.0};
		Object[] ie = new Object[]{"IE",26.8};
		Object[] chrome = new Object[]{"Firefox",12.8};
		Object[] safari = new Object[]{"Firefox",8.5};
		Object[] opera = new Object[]{"Firefox",6.2};
		Object[] others = new Object[]{"Firefox", 0.7};
		Object[] datas = new Object[]{firefox,ie,chrome,safari,opera,others};
		try {
			String result = JsonUtil.mapper.writeValueAsString(datas);
			model.addAttribute("result",result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		Double[] data1 = new Double[]{49.9, 71.5, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4};
		ReportEntity entity1 = new ReportEntity("东京",data1);
		
		Double[] data2 = new Double[]{83.6, 78.8, 98.5, 93.4, 106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3};
		ReportEntity entity2 = new ReportEntity("纽约",data2);
		
		Double[] data3 = new Double[]{48.9, 38.8, 39.3, 41.4, 47.0, 48.3, 59.0, 59.6, 52.4, 65.2, 59.3, 51.2};
		ReportEntity entity3 = new ReportEntity("伦敦",data3);
		
		Double[] data4 = new Double[]{42.4, 33.2, 34.5, 39.7, 52.6, 75.5, 57.4, 60.4, 47.6, 39.1, 46.8, 51.1};
		ReportEntity entity4 = new ReportEntity("柏林",data4);
		
		ReportEntity[] datas1 = new ReportEntity[]{entity1,entity2,entity3,entity4};
        try {
			String result1 = JsonUtil.mapper.writeValueAsString(datas1);
			model.addAttribute("result1",result1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		Double[] data11 = new Double[]{7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6};
		ReportEntity entity11 = new ReportEntity("东京",data11);
		
		Double[] data21 = new Double[]{-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5};
		ReportEntity entity21 = new ReportEntity("纽约",data21);
		
		Double[] data31 = new Double[]{-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0};
		ReportEntity entity31 = new ReportEntity("柏林",data31);
		
		Double[] data41 = new Double[]{3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8};
		ReportEntity entity41 = new ReportEntity("伦敦",data41);
		
		ReportEntity[] datas2 = new ReportEntity[]{entity11,entity21,entity31,entity41};
        try {
			String result3 = JsonUtil.mapper.writeValueAsString(datas2);
			model.addAttribute("result3",result3);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "report/all";
	}
}
