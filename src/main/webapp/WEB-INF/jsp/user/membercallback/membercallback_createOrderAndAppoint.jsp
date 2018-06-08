<%@page import="java.util.*"%>
<%@page import="cn.ncut.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
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
							<form action="membercallback/confirmAndPayOrder.do" name="Form"
								id="Form" method="post">
								<input type="hidden" name="projectcost_id" id="projectcost_id" value="">
								<input type="hidden" name="MEMBERCALLBACK_ID"
									id="MEMBERCALLBACK_ID" value="${pd.MEMBERCALLBACK_ID}" />
								<div id="zhongxin" style="padding-top: 13px;">
									<table id="table_report"
										class="table table-striped table-bordered table-hover">
										<input type="hidden" name="UID" id="UID" value="${pd.UID}" />
										<tr>
											<td style="width:75px;text-align: right;padding-top: 13px;">用户姓名:</td>
											<td><input type="text" name="NAME" id="NAME"
												value="${pd.NAME}" maxlength="32" placeholder="这里输入用户姓名"
												title="用户姓名" style="width:98%;" /></td>
										</tr>
										<tr>
											<td style="width:75px;text-align: right;padding-top: 13px;">用户手机号:</td>
											<td><input type="text" name="PHONE" id="PHONE"
												value="${pd.PHONE}" maxlength="32" placeholder="这里输入用户手机号"
												title="用户手机号" style="width:98%;" /></td>
										</tr>
										<tr>
											<td style="width:75px;text-align: right;padding-top: 13px;">请选择医生：</td>

											<td><select name="STAFF_ID" id="STAFF_ID"
												onchange="refreshProjectCost(this.value)">
													<c:forEach items="${staffPdlist}" var="staff">
														<c:if test="${staff.STATUS==0}">
															<option value="${staff.STAFF_ID}"
																<c:if test="${staff.STAFF_ID==pd.STAFF_ID}">selected</c:if>>${staff.STAFF_NAME}(${staff.STORE_NAME})</option>
														</c:if>
													</c:forEach>
											</select></td>
										</tr>
										<tr>
											<td style="width:75px;text-align: right;padding-top: 13px;">请选择项目：</td>
											<td>
												<div>
					   								<span class="input-icon">
														<input type="text" placeholder="请输入项目关键词" class="nav-search-input" id="searchProject" autocomplete="off"/>
														<i class="ace-icon fa fa-search nav-search-icon"></i>
													</span>
													<a class="btn btn-light btn-xs" onclick="tosearchServiceProject();"  title="检索"><i id="nav-search-icon" class="ace-icon fa fa-search bigger-110 nav-search-icon blue"></i></a>
					   							</div>
												<table id="simple-table"
													class="table table-striped table-bordered table-hover"
													style="margin-top:5px;">
													<thead>
														<tr>
															<th class="center" style="width:35px;"><label
																class="pos-rel"><span class="lbl"></span>
															</label></th>
															<th class="center">项目名称</th>
															<th class="center">价格</th>
															<th class="center">类型</th>
															<th class="center">次数</th>

														</tr>
													</thead>

													<tbody id="projectcost_tbody">

													</tbody>
												</table></td>
										</tr>
										<tr>

											<td style="width:75px;text-align: right;padding-top: 13px;">请选择预约日期:</td>
											<td><input class="span10 date-picker" name="THE_DATE"
												id="THE_DATE" value="${pd.THE_DATE}" type="text"
												data-date-format="yyyy-mm-dd" readonly="readonly"
												placeholder="日期" title="日期" style="width:98%;"
												onchange="findWeekServiceByDate(this)" /></td>
										</tr>
										<tr>
											<td style="width:220px;text-align: right;padding-top: 13px;">就诊时间:</td>
											<td align="center">
												<div class="main-booking">
													<div class="main-booking-detail">
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
															<tbody id="dabiao">
																<tr>
																	<c:forEach var="ifCanOrder" items="${ifCanOrder}"
																		varStatus="status">

																		<c:if
																			test="${status.count % 7 == 1 ||status.count==null}">
																</tr>
																<tr class="rows">
																	</c:if>
																	<fmt:formatNumber var="c"
																		value="${status.count%7==0?((status.count-status.count%7)/7-1):(status.count-status.count%7)/7}"
																		maxFractionDigits="0" pattern="#" />
																	<c:choose>
																		<c:when test="${ifCanOrder==1}">
																			<td class="single" status="0"><span
																				name="dearSpan" class="can-not-order"
																				id="${c},${(status.count % 7-1)>-1?(status.count % 7-1):6}"
																				idNum="${status.count}" items="${ifCanOrder}"
																				<%-- idx="${c}"
																			idy="${(status.count % 7-1)>-1?(status.count % 7-1):6}" --%>
																			onclick="bookControl(this);"></span>
																			</td>
																		</c:when>
																		<c:otherwise>
																			<td class="single" status="0"><span
																				name="dearSpan" class="can-order"
																				id="${c},${(status.count % 7-1)>-1?(status.count % 7-1):6}"
																				idNum="${status.count}" items="${ifCanOrder}"
																				<%-- idx="${c}"
																			idy="${(status.count % 7-1)>-1?(status.count % 7-1):6}" --%>
																			onclick="bookControl(this);"></span>
																			</td>
																		</c:otherwise>
																	</c:choose>
																	</c:forEach>
																</tr>
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
												</div></td>

										</tr>

										<tr>
											<!-- <td style="text-align: center;" colspan="10"><a
												class="btn btn-mini btn-primary" ">保存</a> -->
											<a class="btn btn-mini btn-danger"
												onclick="top.Dialog.close();">取消</a>
											</td>
										</tr>
										<tr>

											<td style="width:75px;text-align: right;padding-top: 13px;">请确认预约时间:</td>
											<td><input type="text" name="RECOMMEND_TIME"
												id="RECOMMEND_TIME" value="${pd.RECOMMEND_TIME}" /></td>
										</tr>
									</table>
								</div>
							</form>
							<a class="btn btn-mini btn-danger" onclick="save('确定确认该订单吗?');"
								title="确认订单">去支付</a>
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
		<a href="#" id="btn-scroll-up"
			class="btn-scroll-up btn btn-sm btn-inverse"> <i
			class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i> </a>
		<div id="hint"
			style="width:200px;height:100px;position:absolute;display:none;border:1px solid silver;background:#EDEDED;">
			<span style="font-size:18px;">您选择的医生该时间已经预约人数：<b id="hintText"
				style="color:red;"></b> </span>
		</div>
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

		//保存
		function save() {
			var id = document.getElementsByName('project');
    		var projectcost_id = new Array();
    		for(var i = 0; i < id.length; i++){
     			if(id[i].checked){
     				projectcost_id.push({"cost_id":id[i].value,"cishu":$('#cishu-'+id[i].value).val()});  
     			}
    		} 
			if ($("#STAFF_ID").val() == "") {
				$("#STAFF_ID").tips({
					side : 3,
					msg : '请选择医生',
					bg : '#AE81FF',
					time : 2
				});
				$("#STAFF_ID").focus();
				return false;
			}
			if (projectcost_id==undefined) {
				$("#projectcost_tbody").tips({
					side : 3,
					msg : '请选择服务项目',
					bg : '#AE81FF',
					time : 2
				});
				$("#projectcost_tbody").focus();
				return false;
			}
			if ($("#ORDER_MONEY").val() == "") {
				$("#ORDER_MONEY").tips({
					side : 3,
					msg : '请选择价格',
					bg : '#AE81FF',
					time : 2
				});
				$("#ORDER_MONEY").focus();
				return false;
			}
			if ($("#RECOMMEND_TIME").val() == "") {
				$("#RECOMMEND_TIME").tips({
					side : 3,
					msg : '请选择预约时间',
					bg : '#AE81FF',
					time : 2
				});
				$("#RECOMMEND_TIME").focus();
				return false;
			}
			$("#projectcost_id").val(JSON.stringify(projectcost_id));
			$("#Form").submit();
			$("#zhongxin").hide();
			$("#zhongxin2").show();
		}

		function savetime(id) {//获得当前时间的值
			var stime = id;
			var the_date = giveDate();
			if (stime == undefined || stime == "") {
				alert("请选择预约时间！");
				return;
			}

			$.ajax({
				type : 'POST',
				url : 'customappoint/changeTime.do',
				data : {
					ID : stime,
					THE_DATE : the_date
				},
				dataType : 'json',
				success : function(data) {
					console.log(data);
					if (data.msg == "ok") {
						$("#RECOMMEND_TIME").val(data.selectTime);
					} else {
						alert("您不能选择当前时间之前的时间！ ");
					}
				}
			});
		}

		function tosearchServiceProject(){
			var cx = $("#searchProject").val();
			var staff_id = $("#STAFF_ID").val();
			refreshProjectCost(staff_id, cx);						
		}
		
		
		function refreshProjectCost(staffid, pname) {
			var pname = arguments[1] ? arguments[1] : -1;
			$("#projectcost_tbody").html("");
			$.ajax({
				url : 'userpay/refreshProjectCostBySId_membercallback.do',
				data : {
					STAFF_ID : staffid,
					PNAME : pname
				},
				method : 'POST',
				dataType : 'json',
				success : function(data) {
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
				}
			})
		}
		
		function refreshCost(projectid, staffid) {
			$("#ORDER_MONEY").html("");
			$.ajax({
				url : 'userpay/refreshCostByProjectid.do',
				data : {
					PROJECT_ID : projectid,
					STAFF_ID : staffid
				},
				method : 'POST',
				dataType : 'json',
				success : function(data) {
					$("#ORDER_MONEY").empty();
					$("#ORDER_MONEY").append("<option value=''>请选择</option>");
					for ( var i = 0; i < data.length; i++) {
						$("#ORDER_MONEY").append(
								"<option value="
										+ data[i].PRICE + ">"
										+ data[i].PRICE + "</option>");
					}

				}

			});
		}
		var array = new Array();

		for ( var i = 0; i < document.getElementsByName("dearSpan").length; i++) {

			if (document.getElementsByName("dearSpan")[i].getAttribute("class") == "can-order") {
				array.push(0);
			} else if (document.getElementsByName("dearSpan")[i]
					.getAttribute("class") == "can-not-order") {
				array.push(1);
			}
		}
		function giveStaffToDate() {
			var staff_id = $("#STAFF_ID").val();
			return staff_id;
		}

		function giveDate() {
			var THE_DATE = $("#THE_DATE").val();
			return THE_DATE;
		}

		function findWeekServiceByDate(e) {
			var the_date = $("#THE_DATE").val();
			var staff_id = giveStaffToDate();
			$.ajax({
						type : "POST",
						url : 'servicetime/findWeekServiceByDate',
						data : {
							THE_DATE : the_date,
							STAFF_ID : staff_id
						},
						success : function(data) {
							$("#dabiao").empty();
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
								$("#dabiao").append(tr);
							}

							for ( var i = 0; i < 24; i++) {
								for ( var j = 0; j < 7; j++) {
									span.id = i + ',' + j;
									var element = document
											.getElementById(span.id);

									var clazz = element.getAttribute("class");
									element.setAttribute("name", "dearSpan");
									element.onmouseover = function(event) {
										$.ajax({
													url : 'userpay/queryAlreadyAppointAmount',
													data : {
														STAFF_ID : $(
																"#STAFF_ID")
																.val(),
														selecttime : $(
																"#THE_DATE")
																.val(),
														id : this.id
													},
													dataType : 'json',
													success : function(data) {
														$("#hint").show();
														$("#hintText").text(
																data.count);
														$("#hint")
																.css(
																		"left",
																		event.offsetX + 1000);
														$("#hint")
																.css(
																		"top",
																		event.offsetY + 900);
													},

												});
									};

									element.onclick = function() {
										if (this.className == "can-order") {
											$("#dabiao tr td span")
													.each(
															function() {
																if ($(this)
																		.attr(
																				"class") == "already-order") {
																	$(this)
																			.attr(
																					"class",
																					"can-order");
																}
															});

											var servicecost_id = $(
													"#SERVICECOST_ID").val();
											savetime(this.id);
											this.className = "already-order";
										}
									}
								}
							}
						}

					});
		}

		function bookControl(e) {
			var id = e.getAttribute("id");
			console.log(id);
			//alert(id);
			var clazz = e.getAttribute("class");
			if (clazz == "can-order") {
				var span = document.getElementById(id);
				span.className = 'can-not-order';
			} else if (clazz == "can-not-order") {
				var span = document.getElementById(id);
				span.className = 'can-order';
			}
		}
		$(function() {
			//日期框
			$('.date-picker').datepicker({
				autoclose : true,
				todayHighlight : true
			});
		});
	</script>
</body>
</html>