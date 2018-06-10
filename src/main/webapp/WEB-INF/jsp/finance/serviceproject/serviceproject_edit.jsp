<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
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
	
	<script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
	<script type="text/javascript" src="plugins/zTree/3.5/jquery.ztree.core-3.5.js"></script>
	<link rel="stylesheet" href="plugins/zTree/3.5/zTreeStyle.css" />
	
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
					
					<form action="serviceproject/${msg }.do" name="Form" id="Form" method="post">
						<input type="hidden" name="PROJECT_ID" id="PROJECT_ID" value="${pd.PROJECT_ID}"/>
						<div id="zhongxin" style="padding-top: 13px;">
						<table id="table_report" class="table table-striped table-bordered table-hover">
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">项目名:</td>
								<td><input type="text" name="PNAME" id="PNAME" value="${pd.PNAME}" maxlength="50" placeholder="这里输入项目名" title="项目名" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">所属类别:</td>
								<td><input type="hidden" name="CATEGORY_ID" id="CATEGORY_ID" value="${pd.CATEGORY_ID}" maxlength="32" placeholder="这里输入所属类别" title="所属类别" style="width:98%;"/>
									<div id="treeDemo" class="ztree"></div>
								</td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">单位:</td>
								<td><input type="text" name="UNIT" id="UNIT" value="${pd.UNIT}" maxlength="50" placeholder="这里输入单位" title="单位" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">服务-收入编码:</td>
								<td><input type="text" name="PCODE" id="PCODE" value="${pd.PCODE}" maxlength="50" placeholder="这里输入服务-收入编码" title="服务-收入编码" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">项目介绍:</td>
								<td><input type="text" name="REMARK" id="REMARK" value="${pd.REMARK}" maxlength="255" placeholder="这里输入项目/课程介绍" title="项目介绍" style="width:98%;"/></td>
							</tr>
							<tr>
								<td style="width:75px;text-align: right;padding-top: 13px;">状态:</td>
								<td><select name="STATUS" id="STATUS">
									<option value="1" <c:if test="${pd.STATUS==1}">selected</c:if>>有效</option>
									<option value="0" <c:if test="${pd.STATUS==0}">selected</c:if>>无效</option>
								</select></td>
							</tr>
							<tr>
								<td style="text-align: center;" colspan="10">
									<a class="btn btn-mini btn-primary" onclick="save();">保存</a>
									<a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
								</td>
							</tr>
						</table>
						</div>
						<div id="zhongxin2" class="center" style="display:none"><br/><br/><br/><br/><br/><img src="static/images/jiazai.gif" /><br/><h4 class="lighter block green">提交中...</h4></div>
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
		function save(){
			if($("#CATEGORY_ID").val()==""){
				$("#CATEGORY_ID").tips({
					side:3,
		            msg:'请选择该项目的类别',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#CATEGORY_ID").focus();
			return false;
			}
			if($("#PNAME").val()==""){
				$("#PNAME").tips({
					side:3,
		            msg:'请输入项目名',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PNAME").focus();
			return false;
			}
			if($("#UNIT").val()==""){
				$("#UNIT").tips({
					side:3,
		            msg:'请输入单位',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#UNIT").focus();
			return false;
			}
			
			if($("#PCODE").val()==""){
				$("#PCODE").tips({
					side:3,
		            msg:'请输入服务-收入编码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#PCODE").focus();
			return false;
			}
			if($("#REMARK").val()==""){
				$("#REMARK").tips({
					side:3,
		            msg:'请输入服务-收入编码',
		            bg:'#AE81FF',
		            time:2
		        });
				$("#REMARK").focus();
			return false;
			}
			$("#Form").submit();
			$("#zhongxin").hide();
			$("#zhongxin2").show();
		}
		
		$(function() {
			//日期框
			$('.date-picker').datepicker({autoclose: true,todayHighlight: true});
			
			var setting = {
	        view : {
	            showLine : true,
	            selectedMulti : false,
	            dblClickExpand : false
	        },
	        data : {
	            simpleData : {
	                enable : true
	            }
	        },
	        callback : {
	            onNodeCreated : this.onNodeCreated,
	            onClick : onTreeClick
	        }
	    };  

	    $.ajax({
	        url : 'servicecategory/getAllCategorys.do',
	        dataType : 'json',
	        contenttype : "application/x-www-form-urlencoded;charset=utf-8",
	        type : 'POST',
	        success : function(data) {
	            if (data.success) {
	                $.fn.zTree.init($("#treeDemo"), setting, data.returnvalue);
	            } else {
	                alert("加载分类树出错1！");
	            }
	        },
	        error : function(data) {
	            alert("加载分类树出错2");
	        }
	    });

		});
		
		function onTreeClick(e, treeId, treeNode) {
   			if (treeNode.isParent) //如果不是叶子结点，结束
   				alert("不能选择父分类");
  			//alert(treeNode.id); //获取当前结点上的相关属性数据，执行相关逻辑
  			$("#CATEGORY_ID").val(treeNode.id);
  		} 
		</script>
</body>
</html>