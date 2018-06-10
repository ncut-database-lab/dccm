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

							<form action="refund_money/${msg }.do" name="Form" id="Form"
								method="post">
								<input type="hidden" name="REFUND_MONEY_ID" id="REFUND_MONEY_ID"
									value="${pd.REFUND_MONEY_ID}" />
								<div id="zhongxin" style="padding-top: 13px;">
									<table id="table_report"
										class="table table-striped table-bordered table-hover">
										<caption>
											<b>退款单（储值卡）</b>
										</caption>
										<input type="hidden" name="uid" id="uid" value="${pd.uid}"
											maxlength="255" style="width:98%;" />
										<tr>
											<td style="width:250px;text-align: right;padding-top: 13px;">用户姓名（微信昵称）：</td>
											<td><input type="text" name="name" id="name"
												value="${pd.name}" maxlength="255" style="width:98%;"
												readonly="true" /></td>
										</tr>

										<tr>
											<td style="width:250px;text-align: right;padding-top: 13px;">手机号:</td>
											<td><input type="text" name="phone" id="phone"
												value="${pd.phone}" maxlength="255" style="width:98%;"
												readonly="true" />
											</td>
										</tr>

										<tr>
											<td style="width:250px;text-align: right;padding-top: 13px;">可退储值卡余额:</td>
											<td><input type="text" name="REMAIN_MONEY" id="REMAIN_MONEY"
												value="${pd.REMAIN_MONEY}" maxlength="255" placeholder=""
												title="" style="width:98%;" readonly="true" />
											</td>
										</tr>
										
									 	
											<!-- <td style="width:250px;text-align: right;padding-top: 13px;">损失储值卡返点:</td> -->
												<input type="hidden" name="REMAIN_POINTS" id="REMAIN_POINTS"
												value="${pd.REMAIN_POINTS}" maxlength="255" placeholder=""
												title="" style="width:98%;" readonly="true"/>

												<input type="hidden" name="CARD_ID" id="CARD_ID"
											   value="${pd.CARD_ID}" maxlength="255" placeholder=""
											   title="" style="width:98%;" readonly="true"/>
											
										
										
										<tr>
											<td style="width:250px;text-align: right;padding-top: 13px;">手续费（可修改）:</td>
											<td><input type="text" name="shouxufei" id="shouxufei"
												value="${pd.shouxufei}" maxlength="255" style="width:98%;"
												onblur="getSumMoneyByShouXuFei(this)" />
											</td>
										</tr>
										
										<tr>
											<td style="width:250px;text-align: right;padding-top: 13px;">实退:</td>
											<td><input type="text" name="tuikuan" id="tuikuan"
												value="${pd.tuikuan}" maxlength="255" placeholder=""
												title="" style="width:98%;" readonly="true" />
											</td>
										</tr>
										
										<tr>
											<td style="width:250px;text-align: right;padding-top: 13px;">退款时间:</td>
											<td><input type="text" name="TIME" id="TIME"
												value="${pd.TIME}" maxlength="255" placeholder="这里输入退款时间"
												title="退款时间" style="width:98%;" readonly="true" />
											</td>
										</tr>
										<tr>
											<td style="width:250px;text-align: right;padding-top: 13px;">操作员:</td>
											<td><input type="text" name="STAFF_NAME" id="STAFF_NAME"
												value="${pd.STAFF_NAME}" maxlength="255" placeholder=""
												title="" style="width:98%;" readonly="true" />
											</td>
										</tr>

										<tr>
											<td style="text-align: center;" colspan="10"><a
												class="btn btn-mini btn-primary" onclick="save();">确认退卡</a> <a
												class="btn btn-mini btn-danger"
												onclick="top.Dialog.close();">取消</a></td>
										</tr>
									</table>
								</div>

								<div id="zhongxin2" class="center" style="display:none">
									<br /> <br /> <br /> <br /> <br /> <img
										src="static/images/jiazai.gif" /><br />
									<h4 class="lighter block green">提交中...</h4>
								</div>
							</form>
						</div>
						<!-- /.col -->
					</div>
					<!-- /.row -->
				</div>
				<!-- /.page-content -->
			</div>
		</div>
		<!-- /.main-content -->
	</div>
	<!-- /.main-container -->


	<!-- 页面底部js¨ -->
	<%@ include file="../../system/index/foot.jsp"%>
	<!-- 下拉框 -->
	<script src="static/ace/js/chosen.jquery.js"></script>
	<!-- 日期框 -->
	<script src="static/ace/js/date-time/bootstrap-datepicker.js"></script>
	<!--提示框-->
	<script type="text/javascript" src="static/js/jquery.tips.js"></script>
	<script type="text/javascript">
		$(top.hangge());
		//保存
		function save() {
			if ($("#UID").val() == "") {
				$("#UID").tips({
					side : 3,
					msg : '请输入用户ID',
					bg : '#AE81FF',
					time : 2
				});
				$("#UID").focus();
				return false;
			}
			if ($("#REFUND_PRESTORE_MONEY").val() == "") {
				$("#REFUND_PRESTORE_MONEY").tips({
					side : 3,
					msg : '请输入可退余额',
					bg : '#AE81FF',
					time : 2
				});
				$("#REFUND_PRESTORE_MONEY").focus();
				return false;
			}
			if ($("#REFUND_REMAIN_MONEY").val() == "") {
				$("#REFUND_REMAIN_MONEY").tips({
					side : 3,
					msg : '请输入可退储值卡',
					bg : '#AE81FF',
					time : 2
				});
				$("#REFUND_REMAIN_MONEY").focus();
				return false;
			}
			if ($("#TIME").val() == "") {
				$("#TIME").tips({
					side : 3,
					msg : '请输入退款时间',
					bg : '#AE81FF',
					time : 2
				});
				$("#TIME").focus();
				return false;
			}
			if ($("#SERVICE_ID").val() == "") {
				$("#SERVICE_ID").tips({
					side : 3,
					msg : '请输入操作员',
					bg : '#AE81FF',
					time : 2
				});
				$("#SERVICE_ID").focus();
				return false;
			}
			$("#Form").submit();
			$("#zhongxin").hide();
			$("#zhongxin2").show();
		}

		$(function() {
			//日期框
			$('.date-picker').datepicker({
				autoclose : true,
				todayHighlight : true
			});
			var REMAIN_MONEY = $("#REMAIN_MONEY").val();
			var shouxufei = $("#shouxufei").val();
			$("#tuikuan").val(REMAIN_MONEY - shouxufei);
		});
		
		function getSumMoneyByShouXuFei(e) {
			var SUM_MONEY = $("#REMAIN_MONEY").val();
			var shouxufei = $("#shouxufei").val();
			$.ajax({
				type : 'post',
				dataType : 'json',
				data : {
					SUM_MONEY : SUM_MONEY,
					shouxufei : shouxufei
				},
				url : "refund_money/getSumMoneyByShouXuFei",
				success : function(data) {
					$("#tuikuan").val(data[0].tuikuan);
				}
			});
		};
		
	</script>
</body>
</html>