<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
<base href="<%=basePath%>">
<!-- 下拉框 -->
<link rel="stylesheet" href="static/ace/css/chosen.css" />
<!-- jsp文件头和头部 -->
<%@ include file="../../system/index/top.jsp"%>
<!-- 日期框 -->
<style type="text/css">
	.paymethod{
		width:100%;
		min-height:350px;
		text-align:left;
		margin-top:50px;
	}
	.paymethod table{
		font-size:20px;
		width:500px;
		height:300px;
	}
	.paymethod input[type=checkbox]:checked {
   	  background-color: red;
	}
</style>
<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
<link rel="stylesheet" href="static/ace/css/datepicker.css" />
<link rel="stylesheet" href="static/css/default/venues.css" />
</head>
<body class="no-skin">

	<!-- /section:basics/navbar.layout -->
	<div class="main-container" id="main-container">
		<!-- /section:basics/sidebar -->
		<div class="main-content">
			<div class="main-content-inner">
				<div class="page-content">
					<div class="row">
						<div class="col-xs-12">
						 
						 <!-- 检索  -->
						<form action="userpay/show.do" method="post" name="Form" id="Form">
						
						<div>
							<span style="margin-top:8px;float:left;">请输入用户姓名/昵称/手机号查询用户：</span>
							<div class="nav-search" style="width:80%; float:left;text-align:left;">
								<span class="input-icon">
									<input type="text" placeholder="用户姓名/昵称/手机号" class="nav-search-input" id="nav-search-input" autocomplete="off" name="keywords" value="${pd.keywords }" placeholder="这里输入关键词"/>
									<i class="ace-icon fa fa-search nav-search-icon"></i>
								</span>
								<a class="btn btn-light btn-xs" onclick="tosearch();"  title="检索"><i id="nav-search-icon" class="ace-icon fa fa-search bigger-110 nav-search-icon blue"></i></a>
							</div>
						</div>
						
						<table id="simple-table" class="table table-striped table-bordered table-hover" style="margin-top:5px;">	
							<thead>
								<tr>
									<th class="center" style="width:35px;">
									<label class="pos-rel"><span class="lbl"></span></label>
									</th>
									<th class="center" style="width:50px;">序号</th>
									<th class="center">姓名</th>
									<th class="center">昵称</th>
									<th class="center">性别</th>
									<th class="center">出生年月</th>
									<th class="center">电话号码</th>
									<th class="center">所属城市</th>
									<th class="center">客户类别</th>
									<th class="center">折扣</th>
									<th class="center">储值卡</th>
									<th class="center">返点</th>
									<th class="center">钱包</th>
									<th class="center">优惠券</th>
								</tr>
							</thead>
													
							<tbody>
							<!-- 开始循环 -->	
							<c:choose>
								<c:when test="${not empty userList}">
									<c:forEach items="${userList}" var="user" varStatus="vs">
										<tr>
											<td class='center'>
												<input type='radio' name='uid' value="${user.uid}" class="ace"/><span class="lbl"></span>
											</td>
											<td class='center' style="width: 30px;">${vs.index+1}</td>
											<td class='center'>${user.name}</td>
											<td class='center'>${user.username}</td>
											<td class='center'>
												<c:if test="${user.sex==1}">男</c:if>
												<c:if test="${user.sex==2}">女</c:if>
												<c:if test="${user.sex==0}">未知</c:if>
											</td>
											<td class='center'>${user.age}</td>
											<td class='center'>${user.phone}</td>
											<td class='center'>${user.city}</td>
											<td class='center'>${user.CATEGORY_NAME}</td>
											<td class='center'>
												<c:if test="${user.proportion le user.category_porportion}">${user.proportion}</c:if>
												<c:if test="${user.proportion gt user.category_porportion}">${user.category_porportion}</c:if>
											</td>
											<td style="text-align:right"><fmt:formatNumber type="number" value="${user.remain_money}" pattern="0.00" maxFractionDigits="2"/></td>
											<td style="text-align:right"><fmt:formatNumber type="number" value="${user.remain_points}" pattern="0.00" maxFractionDigits="2"/></td>
											<td style="text-align:right"><fmt:formatNumber type="number" value="${user.SUM_MONEY}" pattern="0.00" maxFractionDigits="2"/></td>
											<td class='center'>
												<a class="btn btn-xs btn-info" title="查看用户优惠券" onclick="discount('${user.uid}');">
														<i class="ace-icon fa fa-file bigger-120" title="查看用户优惠券"></i>
												</a></td>
										</tr>
									</c:forEach>
								</c:when>
								<c:otherwise> 
									<tr class="main_info">
										<td colspan="100" class="center" >没有相关数据</td>
									</tr>
								</c:otherwise>
							</c:choose>
							</tbody>
						</table>
						
						<table style="width:100%;">
							<tr>
								<td style="vertical-align:top;"><div class="pagination" style="float: right;padding-top: 0px;margin-top: 0px;">${page.pageStr}</div></td>
							</tr>
						</table>
						</form>	
						
						<!--检索 end-->
						<div style="clear:both;"></div>
						
						<!--选择医生次数-->
						<div id="selectdoctor" style="width:100%;text-align: left;">
						请选择医生：
						<select name="STAFF_ID" id="STAFF_ID" onchange="refreshProjectCost(this.value)">
							<option value="" >--请选择--</option>
							<c:forEach items="${staffPdlist}" var="staff">
								<c:if test="${staff.STATUS==0}">
								<option value="${staff.STAFF_ID}" <c:if test="${staff.STAFF_ID==pd.STAFF_ID}">selected</c:if> >${staff.STAFF_NAME}(${staff.STORE_NAME})</option>
								</c:if>
							</c:forEach>
						</select>
						
					   </div>
					   
					   <div id="projectcost" style="width:100%;text-align:left;margin-top:20px;">
					   	请选择服务项目：
					   		<div>
					   			<span class="input-icon">
									<input type="text" placeholder="请输入项目关键词" class="nav-search-input" id="searchProject" autocomplete="off"/>
									<i class="ace-icon fa fa-search nav-search-icon"></i>
								</span>
								<a class="btn btn-light btn-xs" onclick="tosearchServiceProject();"  title="检索"><i id="nav-search-icon" class="ace-icon fa fa-search bigger-110 nav-search-icon blue"></i></a>
					   		</div>
					   		<table id="simple-table" class="table table-striped table-bordered table-hover" style="margin-top:5px;">	
							<thead>
								<tr>
									<th class="center" style="width:35px;">
									<label class="pos-rel"><span class="lbl"></span></label>
									</th>
									<th class="center">项目名称</th>
									<th class="center">价格</th>
									<th class="center">类型</th>
									<th class="center">次数</th>
									
								</tr>
							</thead>
													
							<tbody id="projectcost_tbody">
								
							</tbody>
							</table>
					   </div>
					   
					  <!--日期的大表-->
					  <div class="main-booking" id="main-booking" style="width:100%;min-height:800px;position:relative;text-align:left;margin-top:20px;border:0;">
						  <div style="margin:20px 0;">
						      <span>请选择日期和时间：</span>
						
						  <input class="date-picker" name="THE_DATE" id="THE_DATE" value="" type="text"
							data-date-format="yyyy-mm-dd" readonly="readonly"
							placeholder="请选择日期" title="请选择日期" onchange="findWeekServiceByDate(this)" />
							
						  </div>
						
						<div class="main-booking-detail">
							<div class="left-time-list">
								<ul style="padding-top: 23px;">
									<%
										for (int i = 0; i < 25; i++) {//循环24次 每次输出一行
									%>
									<li class="hour"><%=i%>:00</li>
									<%
										}
									%>
								</ul>
							</div>
							<table class="main-table" align="center" width="700">
								<thead align="center">
									<tr>
										<th class="heads"><span>周日</span>
										</th>
										<th class="heads"><span>周一</span>
										</th>
										<th class="heads"><span>周二</span>
										</th>
										<th class="heads"><span>周三</span>
										</th>
										<th class="heads"><span>周四</span>
										</th>
										<th class="heads"><span>周五</span>
										</th>
										<th class="heads"><span>周六</span>
										</th>
									</tr>
								</thead>
								<tbody id="servicetime">
									
								</tbody>
							</table>
						</div>
						<div class="right-order-plane">
							<div class="title">
								<ul>
									<li><span class="can-order"></span>
									</li>
									<li>可预定</li>
									<li><span class="can-not-order"></span>
									</li>
									<li>不可预定</li>
									<li><span class="already-order"></span>
									</li>
									<li>当前选定</li> 
								</ul>
							</div>
						</div>
					
						<div id="hint" style="width:200px;height:100px;position:absolute;display:none;border:1px solid silver;background:#EDEDED;right:90px;bottom:600px;">
							<span style="font-size:18px;">您选择的医生该时间已经预约人数：<b id="hintText" style="color:red;"></b></span>
						</div>
					</div>
					   
					<div style="clear:both;"></div>
					   
				  <div id="selectDateAndTime" style="width:100%;height:90px;text-align:left;margin-top:20px;">
				   	<!---<a class="btn btn-mini btn-primary" onclick="savetime();">保存选中时间</a>-->
					<span style="">您选择的时间是：<b id="selected_time" style="color:red;"></b></span>
				  </div>
				
				<div style="width:100%;height:90px;text-align:left;margin-top:20px;">
					<a class="btn btn-mini btn-danger" onclick="sell();" title="去结算">去结算</a>
				</div>
						</div>
						<!-- /.col -->
					</div>
					<!-- /.row -->
				</div>
				<!-- /.page-content -->
			</div>
		</div>
		<!-- /.main-content -->

		<!-- 返回顶部 -->
		<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
			<i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
		</a>
		
		

	</div>
	<!-- /.main-container -->

	<!-- basic scripts -->
	<!-- 页面底部js¨ -->
	<%@ include file="../../system/index/foot.jsp"%>
	<!-- 删除时确认窗口 -->
	<script src="static/ace/js/bootbox.js"></script>
	<!-- ace scripts -->
	<script src="static/ace/js/ace/ace.js"></script>
	<!-- 下拉框 -->
	<script src="static/ace/js/chosen.jquery.js"></script>
	<!-- 日期框 -->
	<script src="static/ace/js/date-time/bootstrap-datepicker.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
	<script type="text/javascript">
		$(top.hangge());//关闭加载状态
		
		$(function() {
			//日期框
			$('.date-picker').datepicker({
				autoclose : true,
				todayHighlight : true
			});
		});
		
		var servicetime;
		var correctservicetime;
		
		var serviceproject_json;
		
		//搜索用户		
		function tosearch(){
			top.jzts();
			$("#Form").submit();
		}
		
		function tosearchServiceProject(){
			var cx = $("#searchProject").val();
			var staff_id = $("#STAFF_ID").val();
			refreshProjectCost(staff_id, cx);						
		}
		
		//选择医生和服务项目
		function refreshProjectCost(staffid,pname){
			var pname=arguments[1]?arguments[1]:-1;
			$("#projectcost_tbody").html("");
			$.ajax({
				url:'userpay/refreshProjectCostBySId.do',
				data:{STAFF_ID:staffid, PNAME:pname},
				method:'POST',
				dataType:'json',
				success:function(data){
					serviceproject_json = data;
					for(var t=0; t<data.length; t++){
						//构造收费项目表
						var tr = document.createElement("tr");
						tr.id = "servicecost"+data[t].SERVICECOST_ID;
						var td1 = document.createElement("td");
						td1.className = "center";
						var radio = document.createElement("input");
						radio.setAttribute("type","checkbox");
						radio.setAttribute("name","project");
						radio.setAttribute("money",data[t].PRICE);
						
						radio.className="ace";
						radio.value=data[t].SERVICECOST_ID;
						
						var span = document.createElement("span");
						span.className="lbl";
						td1.appendChild(radio);
						td1.appendChild(span);
						var td2 = document.createElement("td");
						td2.className="center";
						td2.appendChild(document.createTextNode(data[t].PNAME));
						
						var td3 = document.createElement("td");
						td3.className="center";
						td3.appendChild(document.createTextNode(data[t].PRICE));
						
						var td4 = document.createElement("td");
						td4.className="center";
						if(data[t].ISFIRST==1){
							td4.appendChild(document.createTextNode("复诊"));
						}else if(data[t].ISFIRST==0){
							td4.appendChild(document.createTextNode("初诊"));
						}else{
							
						}
						
						var cishu = document.createElement("input");
						cishu.setAttribute("type","number");
						cishu.setAttribute("min","1");
						cishu.value = 1;
						cishu.setAttribute("id","cishu-"+data[t].SERVICECOST_ID);
						
						var td5 = document.createElement("td");
						td5.className="center";
						td5.appendChild(cishu);
						
						tr.appendChild(td1);
						tr.appendChild(td2);
						tr.appendChild(td3);
						tr.appendChild(td4);
						tr.appendChild(td5);
						$("#projectcost_tbody").append(tr);
					}
					$(top.hangge());//关闭加载状态
				}
			});
		}
		
		function findWeekServiceByDate(e) {
			var the_date = $("#THE_DATE").val();
			var staff_id = $("#STAFF_ID").val();
			$.ajax({
				type : "POST",
				url : 'servicetime/findWeekServiceByDate',
				data : {
					THE_DATE : the_date,
					STAFF_ID : staff_id
				},
				success : function(data) {
					$("#servicetime").empty();
					for ( var i = 0; i < 24; i++) {
						var tr = document.createElement("tr");
						tr.className = "rows";
						for ( var j = 0; j < 7; j++) {
							var td = document.createElement("td");
							td.className = "single";

							var span = document.createElement("span");
							//	span.appendChild(document.createTextNode(data[7*i +j]));
							span.id = i + ',' + j;
							if (data[7 * i + j] == 1) {
								span.className = "can-not-order";
							} else {
								span.className = "can-order";
							}
							td.appendChild(span);
							tr.appendChild(td);
						}
						$("#servicetime").append(tr);
					}

					for ( var i = 0; i < 24; i++) {
						for ( var j = 0; j < 7; j++) {
							span.id = i + ',' + j;
							var element = document.getElementById(span.id);
							var clazz = element.getAttribute("class");
							element.setAttribute("name","dearSpan");
							element.onmouseover = function(event){
								$.ajax({
									url:'userpay/queryAlreadyAppointAmount',
									data: {
										STAFF_ID:$("#STAFF_ID").val(),
										selecttime:$("#THE_DATE").val(),
										id: this.id
									},
									dataType:'json',
									success:function(data){
										$("#hint").show(); 
										$("#hintText").text(data.count);
										//var xx = event.clientX+150;
    								//	var yy = event.clientY+650;
										//$("#hint").css("left", xx); 
										//$("#hint").css("top", yy); 
									},
								});
							};
							element.onclick = function() {
								if (this.className == "can-order") {
									$("#servicetime tr td span").each(function(){
										if($(this).attr("class") == "already-order"){
											$(this).attr("class","can-order");
										}
									});
								savetime(this.id);
								this.className = "already-order";									
								}
							}
						}
					}
				}

			});
		}
		
		//保存预约时间并显示
		function savetime(id){
			servicetime=id;
			if(servicetime==undefined || servicetime==""){
				alert("请选择预约时间！");
				return;
			}
			$.ajax({
				type: "POST",
				url: "userpay/changeTime",
		    	data: {Id:servicetime,THE_DATE:$("#THE_DATE").val()},
				dataType:"json",
				cache:false,
				success: function(data){
					if(data.msg=="ok"){
						correctservicetime=data.selectTime;
						$("#selected_time").text(data.selectTime);
					}else{
						alert("您不能选择当前时间之前的时间！");
					}
				}
			});
		}
	 	
	 	
	 	//进行确认订单
	 	function sell(){
	 		var uid = $('input[name="uid"]:checked').val(); 
			//var projectcost_id = $('input[name="project"]:checked').val();
			
			var id = document.getElementsByName('project');
    		var projectcost_id = new Array();
    		for(var i = 0; i < id.length; i++){
     			if(id[i].checked){
     				projectcost_id.push({"cost_id":id[i].value,"cishu":$('#cishu-'+id[i].value).val()});  
     			}
    		} 
    		var costAndNum = JSON.stringify(projectcost_id);
					 
			if(uid==undefined){
				alert("请选择用户！");
				return;
			}
			if(projectcost_id==undefined){
				alert("请选择服务项目！");
				return;
			}
			
			if(servicetime==undefined){
				alert("请选择预约时间！");
				return;
			}
			
	 		
	 		if(uid!=undefined && projectcost_id!=undefined  && servicetime!=undefined){
	 			 top.jzts();
				 var diag = new top.Dialog();
				 diag.Drag=true;
				 diag.Title ="确认订单";
				 diag.URL = '<%=basePath%>userpay/confirmAndPayOrder.do?uid='+uid+'&SERVICECOST_ID='+escape(costAndNum)+'&servicetime='+correctservicetime;
				 diag.Width = 1200;
				 diag.Height = 1000;
				 diag.Modal = true;				//有无遮罩窗口
				 diag. ShowMaxButton = true;	//最大化按钮
			     diag.ShowMinButton = true;		//最小化按钮 
				 diag.CancelEvent = function(){ //关闭事件
					diag.close();
					window.location.reload();
				 };
				 diag.show();
			}
	 	}
	 	
	 	//查看用户优惠券
		function discount(Id){
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="用户优惠券";
			 diag.URL = '<%=basePath%>userdiscount/goShowDiscount.do?UID='+Id;
			 diag.Width = 1200;
			 diag.Height = 800;
			 diag.Modal = true;				//有无遮罩窗口
			 diag. ShowMaxButton = true;	//最大化按钮
		     diag.ShowMinButton = true;		//最小化按钮 
			 		 diag.CancelEvent = function(){ //关闭事件
				 if(diag.innerFrame.contentWindow.document.getElementById('zhongxin').style.display == 'none'){
					 nextPage(${page.currentPage});
				}
				diag.close();
			 };
			 diag.show();
		}
	</script>
</body>
</html>