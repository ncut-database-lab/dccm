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
<style type="text/css">
#paymethod {
	font-size: 18px;
	width: 500px;
	height: 300px;
}
#hiddenProject{
	display:none; 
	position:absolute;
	width:260px;
	min-height:300px;
	background:#01457b ;
	color:#fff;
	opacity:0.7;
}

#hiddenProject ul li{
	width:250px;
	height:45px;
	text-align:left;
	line-height:25px;
}
</style>
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
							<div id="zhongxin">
							<table id="table_report"
								class="table table-striped table-bordered table-hover">
								<input type="hidden" name="UID" id="UID" value="${pd.UID}" />
								<input type="hidden" name="MEMBERCALLBACK_ID" id="MEMBERCALLBACK_ID" value="${pd.MEMBERCALLBACK_ID}" />
								<input type="hidden" name="lowProportion" id="lowProportion" value="${lowProportion}" />
								<span id="costIdAndNum" hidden>${costIdAndNumJson}</span>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">姓名:</td>
									<td>${userpd.name}</td>
								</tr>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">昵称:</td>
									<td>${userpd.username}</td>
								</tr>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">手机号:</td>
									<td>${userpd.phone}</td>
								</tr>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">最低折扣:</td>
									<td><span>${lowProportion}</span>
									</td>
								</tr>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">储值卡总剩余:</td>
									<td>
										<span>
											<fmt:formatNumber type="number" value="${userpd.REMAIN_MONEY+userpd.REMAIN_POINTS}" pattern="0.00" maxFractionDigits="2"/>
											&nbsp;&nbsp;元</span>
									</td>
								</tr>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">储值卡余额:</td>
									<td>
										<span id="chuzhika">
										<c:if test="${not empty userpd.REMAIN_MONEY}"><fmt:formatNumber type="number" value="${userpd.REMAIN_MONEY}" pattern="0.00" maxFractionDigits="2"/></c:if>
										<c:if test="${empty userpd.REMAIN_MONEY}">0</c:if></span>
										&nbsp;&nbsp;元
									</td>
								</tr>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">储值卡返点:</td>
									<td><span id="fandian">
										<c:if test="${not empty userpd.REMAIN_POINTS}"><fmt:formatNumber type="number" value="${userpd.REMAIN_POINTS}" pattern="0.00" maxFractionDigits="2"/></c:if>
										<c:if test="${empty userpd.REMAIN_POINTS}">0</c:if></span>
										&nbsp;&nbsp;元
									</td>
								</tr>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">钱包余额:</td>
									<td><span id="qianbao">
										<c:if test="${not empty userpd.SUM_MONEY}"><fmt:formatNumber type="number" value="${userpd.SUM_MONEY}" pattern="0.00" maxFractionDigits="2"/></c:if>
										<c:if test="${empty userpd.SUM_MONEY}">0</c:if></span>
										&nbsp;&nbsp;元
									</td>
								</tr>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">医生：</td>
									<td><span id="staffId">${doctor}</span>
									</td>
								</tr>
								<tr>
								<td style="width:150px;text-align: right;padding-top: 13px;">项目确认</td>
								<td>
									<table id="simple-table" class="table table-striped table-bordered table-hover">
										<tr>
											<th>项目/单价</th>
											<th>次数</th>
										</tr>
										<c:forEach var="item" items="${costMap}">  
										<tr>
											<td>${item.key}</td>
											<td>${item.value}</td>
										</tr>
										</c:forEach>
									</table>
								</td>
								</tr>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">预约时间:</td>
									<td><span id="serviceTime">${pd.RECOMMEND_TIME}</span>
									</td>
								</tr>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">总价:</td>
									<td><span id="sumOrderMoney">${sumOrderMoney}</span>元</td>
								</tr>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">折扣价:</td>
									<td><span id="zhekouPrice">${zhekouPrice}</span>元</td>
								</tr>
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">请选择优惠券</td>
									<td>
										<c:if test="${isSingleProject == 1}">
										<div class="groupDiv">
											<c:forEach items="${discountGroupPdList}" var="discountgroup">
												<div>
													 <c:if test="${discountgroup.sum!=0}">
													<div group="${discountgroup.discount_group_id}"
														style="color:red;font-size:16px;cursor:pointer;">
														<img src="${pageContext.request.contextPath}/static/images/libao.png"
															style="width:38px;height:38pd;float:left;"> 
															<span style="width:240px;padding:10px;float:left;">${discountgroup.GROUP_NAME}</span>
														<div style="clear:both;"></div>
													</div>
													<div name="xz" style="display:none">
														<table id="table_${discountgroup.discount_group_id}"
															class="table table-striped table-bordered table-hover"
															style="margin-top:5px;">
															<tr>
																<td class="center" style="width:50px;"></td>
																<td class="center">优惠券名称</td>
																<td class="center">金额</td>
																<td class="center">开始时间</td>
																<td class="center">截止时间</td>
																<td class="center">可用数量(张)</td>
																<td class="center">使用数量(张)</td>
															</tr>
															<c:forEach items="${discountgroup.userDiscounts}"
																var="discount">
																<tr>
																	<td class="center" style="width:50px;"><label
																		class="pos-rel"><input type='checkbox' price="${discount.discount_amount}"
																			name='discount' id="check_${discountgroup.discount_group_id}-${discount.discount_id}" value="${discountgroup.discount_group_id}-${discount.discount_id}"
																			class="ace" /><span class="lbl"></span>
																	</label>
																	</td>
																	<td class="center" style="cursor:pointer;">
																	  <span onclick="showAvaliableProject('${discount.discount_id}',event)" >${discount.discount_name}</span>
																	</td>
																	<td class="center" >${discount.discount_amount}</td>
																	<td class="center">${discount.start_time}</td>
																	<td class="center">${discount.end_time}</td>
																	<td class="center">${discount.count}</td>
																	<td class="center">
																		<select  id="${discountgroup.discount_group_id}-${discount.discount_id}">
																			<c:forEach begin="0" end="${discount.count}" step="1" varStatus="i">
																			<option value="${i.index}">${i.index}</option>
																			</c:forEach>
																		</select>
																	</td>
																</tr>
															</c:forEach>
														</table>
													</div>
													</c:if>
												</div>
											</c:forEach>
										</div>
										</c:if>
									</td>
								</tr>

								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">订单还需支付的金额：</td>
									<td><a class="btn btn-mini btn-primary" onclick="saveDiscount()">保存选中的优惠券</a> 
										<span margin-left="15">您选择的优惠券总金额为：<b id="DiscountMoney"></b> 元</span>
										<span style="margin-left:20px;color:red;font-size:20px;">还需支付的金额为：<b id="needMoney"></b> 元</span>
									<input type="hidden" id="jsonDiscount" value="">
									</td>
								</tr>

								
								<tr>
									<td style="width:150px;text-align: right;padding-top: 13px;">请添加订单备注：</td>
									<td><input type="text" id="REMARK" value="${userpd.remark}"maxlength="255"
										placeholder="这里输入备注" title="备注" style="width:98%;">
									</td>
								</tr>
								<tr>
									<td style="text-align: center;" colspan="10"><a
										class="btn btn-mini btn-primary"
										onclick="submitOrder('确定提交该订单吗？')">提交订单</a></td>
								</tr>
							</table>
							</div>
							
							<div id="zhongxin2" class="center" style="display:none;width:400px;height:400px;"><br/><br/><br/><br/><br/><img src="static/images/jiazai.gif" /><br/><h4 class="lighter block green">提交中...</h4></div>
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


	<div id="hiddenProject">
	</div>
	
	<!-- 返回顶部 -->
	<a href="#" id="btn-scroll-up"
		class="btn-scroll-up btn btn-sm btn-inverse"> <i
		class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i> </a>
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
	<script type="text/javascript" defer="defer">
		$(top.hangge());//关闭加载状态
	
		$(document).ready(function() {
				$(".groupDiv>div>div:first-child").click(function() {
						$xz = $(".groupDiv>div>div:first-child");
						$xz.not($(this)).siblings("[name='xz']").stop().slideUp(300).find('input').attr('checked', false);//隐藏
						
						$(this).siblings("[name='xz']").slideToggle(300);//显示
					});
					
				$("input[name='discount']").click(function(){
					 if($(this).is(":checked")){//点击
        			 	var s = $(this).val();//discountid
 				     	judgeAvaliableProject(s);
					}
				});
		});


		function judgeAvaliableProject(dicountid){
			var text = $("#PID").val();
			$.ajax({
				type:"POST",
				url:"membercallback/judgeAvaliableProject",
				dataType : "json",
				data:{
					DISCOUNT_ID:dicountid,
					PID:text,
				},
				success:function(data){
					if(data.result=="false"){
						alert("您选择的优惠券不能用于该项目！");
						$("#"+dicountid+" option").eq(0).attr("selected",true);
						$("#check_"+dicountid).prop('checked', false);
					};
				},
			}); 
		}
		
		function show(id) {
			if ($("#" + id + "_text").is(":hidden")) {
				$("#" + id + "_text").show();
				/* if(id=="storedpay"){
					$("#czk_password").show();
				} */
			} else {
				$("#" + id + "_text").hide();
				$("#" + id + "_text").children().val("0");
			/* 	if(id=="storedpay"){
					$("#czk_password").hide();
				} */
			}
			
		}

		var needMoney;
		
		//保存选中的优惠券
		function saveDiscount() {
			var json = "";
			var selectedDiscountMoney=0;
			$("input[name='discount']").each(function() {
						if ($(this).is(":checked")) {
							//数据填写正确，则拼成json串传递给后台
							var s = $(this).val();
							var number = Number($("#"+s).find("option:selected").val());//优惠券个数
							var price = $(this).attr("price");
							
							//所选择的优惠券的总额
							selectedDiscountMoney = Number(Number(selectedDiscountMoney) + number*price).toFixed(2);
													
							json += "{";
							json += "\"discountid\":" + "\"" + s + "\""
									+ ",\"number\":" + "\"" + number + "\""
									+ ",";
							json = json.substring(0, json.length - 1) + "},";
						}
					});
			needMoney = (Number($("#zhekouPrice").text())- selectedDiscountMoney).toFixed(2);
			if(needMoney<0){
				alert("您选择的优惠券金额大于订单金额，请重新选择！");
				$("input:checkbox[name='discount']").removeAttr("checked");
				$("#DiscountMoney").text("");
				$("#needMoney").text("");
				return;
			}else{
				json = "[" + json.substring(0, json.length - 1) + "]";
				$("#DiscountMoney").text(selectedDiscountMoney);
				$("#needMoney").text(needMoney);
				$("#jsonDiscount").val(json);
			}
		}

		
		
		//查看该优惠券的可用项目
		function showAvaliableProject(discountId,event){
			if($("#hiddenProject").is(":hidden")){
				$.ajax({
				type:"POST",
				url:"userpay/showAvaliableProject",
				dataType : "json",
				data:{DISCOUNT_ID:discountId},
				success:function(data){
					$("#hiddenProject").html("");
					var xx = event.clientX+document.body.scrollLeft+10;
    				var yy = event.clientY+document.body.scrollTop+10;
    				$('#hiddenProject').css({top: yy, left: xx});//注意这是用css的top和left属性来控制div的。
				
					$("#hiddenProject").show();
					$("#hiddenProject").append("<ul>");
					for(var i=0; i<data.length; i++){
						$("#hiddenProject").append("<li style='margin:10px;'>"+data[i]+"</li>");
					}
					$("#hiddenProject").append("</ul>");
				}
			}); 
			}else{
				$("#hiddenProject").hide();
			}
		}
		
		 //检查预存余额
		function checkPreStoreMoney(value){
			if(Number($("#qianbao").text())<Number(value)){
				alert("该用户钱包余额不足！");
				$("#prestorepay_text").children().val(Number($("#qianbao").text()));
				return;
			}
		} 
		
		//检查储值卡余额
		function checkChuzhikaMoney(value){
			var chuzhika = Number($("#chuzhika").text()) + Number($("#fandian").text());
			if(chuzhika<Number(value)){
				alert("该用户储值卡余额不足！");
				$("#storedpay_text").children().val(chuzhika);
				return;
			}
		}
		
		//检查储值卡密码是否正确
		function checkChuzhikaPsd(value){
			$.post("customstored/checkPassword.do",{psd:value,uid:$("#UID").val()},function(data){
				data = JSON.parse(data);
				if(data.result=="error"){
					$("#czk_password_value").tips({
						side:8,
			            msg:'您输入的密码不正确，请重新输入！',
			            bg:'#9400D3',
			            time:1
			        });
					$("#czk_password_value").focus();
					return;
				}
				if(data.result=="success"){
					$("#czk_password_value").tips({
						side:3,
			            msg:'密码正确',
			            bg:'#00CD00',
			            time:1
			        });
					return;
				}
			});
		}
		
		
		//提交订单
		function submitOrder(msg) {	
			if(needMoney==undefined){
				alert("请先保存优惠券信息！");
				return;
			}
			bootbox.confirm(msg, function(result) {
				if (result) {
				 	$("#zhongxin").hide();
			 		$("#zhongxin2").show(); 
					$.ajax({
						type : "POST",
						url : "membercallback/createOrder.do",
						data : {
							UID : $("#UID").val(),
							MEMBERCALLBACK_ID:$("#MEMBERCALLBACK_ID").val(),
							proportion : $("#lowProportion").val(),
							servicetime : $("#serviceTime").text(),
							costIdAndNum : $("#costIdAndNum").text(),
							DiscountJson : $("#jsonDiscount").val(),
							DiscountMoney : $("#DiscountMoney").text(),
							needMoney : $("#needMoney").text(),
							REMARK : $("#REMARK").val(),
							/* password:$("#czk_password_value").val() */
						},

						cache:false,
						success : function(data) {
							console.log(data);
							if(data.code === '200'){
								 bootbox.dialog({
									message: "<span class='bigger-110'>y预约成功!</span>",    
									buttons: 			
										{ 
											"button":
												{ 
													"label":"确定", 
													"className":"btn-sm btn-success", 
													"callback": function() { 
										 					top.Dialog.close();
															self.opener.location.reload();
										 				}		
												}
										},
								}); 
								top.Dialog.close();
							}else{
								bootbox.dialog({
									message: "<span class='bigger-110'>预约失败!</span>",    
								});
							}
						}
					});
				}

			});
		}
	</script>
</body>
</html>