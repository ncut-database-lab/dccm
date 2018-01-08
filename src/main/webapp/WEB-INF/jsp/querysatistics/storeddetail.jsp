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
<%@ include file="../system/index/top.jsp"%>
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
							
						<!-- 检索  -->
						<form action="queryStoreddetail/list.do" method="post" name="Form" id="Form">
						<div id="zhongxin" style="padding-top: 13px;">
						<input type="hidden" id="UID" name="UID" value="${HiddenUid}"/>
						<table style="margin-top:5px;">
							<tr>
								
								<td>
									<div class="nav-search">
											服务板块:<select onchange="modelChange();"
													class="chosen-select form-control" data-placeholder="服务板块"
													name="mId" id="mId">
													<option value=""></option>
													<c:forEach items="${serviceModules}" var="serviceModule">
														<option value="${serviceModule.SERVICEMODULE_ID}"
															<c:if test="${serviceModule.SERVICEMODULE_ID == pd.mId}">selected="selected"</c:if>>
															${serviceModule.M_NAME}</option>
													</c:forEach>
											</div>
										</td>

										<td>选择门店：<select onchange="storeChange();"
											class="chosen-select form-control" name="store" id="store"
											data-placeholder="门店">
												<%-- <option value=""/>
												<c:forEach items="${storeList}" var="store">
													<option value="${store.STORE_ID}" <c:if test="${store.STORE_ID == pd.store}">selected="selected"</c:if> >
														${store.STORE_NAME}</option>
												</c:forEach> --%>
										</select>
										<td>客服姓名:<select onchange=""
											class="chosen-select form-control" name="staffName"
											id="staffName" data-placeholder="客服姓名">

										</select>
										</td>	
										</td>
										<td>请选择创建日期：</td>
								<td style="padding-left:2px;"><input class="span10 date-picker" name="firstDate" value="${pd.firstDate}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" style="width:88px;" placeholder="开始日期" title="开始日期"/></td>
								<td style="padding-left:2px;"><input class="span10 date-picker" name="lastDate"   value="${pd.lastDate}" type="text" data-date-format="yyyy-mm-dd" readonly="readonly" style="width:88px;" placeholder="结束日期" title="结束日期"/></td>
								
				
								
								<td style="vertical-align:top;padding-left:2px"><a class="btn btn-light btn-xs" onclick="tosearch();"  title="检索"><i id="nav-search-icon" class="ace-icon fa fa-search bigger-110 nav-search-icon blue"></i></a></td>
								
						 <!--	<c:if test="${QX.toExcel == 1 }">  -->
									<td style="vertical-align:top;padding-left:100px;">
										<a class="btn btn-light btn-xs" onclick="toExcel('${HiddenUid}')" title="导出到EXCEL">
										<i id="nav-search-icon" class="ace-icon fa fa-download bigger-110 nav-search-icon blue"></i>
										</a>
									</td>
							<!--	</c:if>  -->
								<%-- <c:if test="${QX.toExcel == 1 }"><td style="vertical-align:top;padding-left:2px;"><a class="btn btn-light btn-xs" onclick="toExcel();" title="导出到EXCEL"><i id="nav-search-icon" class="ace-icon fa fa-download bigger-110 nav-search-icon blue"></i></a></td></c:if> --%>
							</tr>
						</table>
						<!-- 检索  -->
					
						<table id="simple-table" class="table table-striped table-bordered table-hover" style="margin-top:5px;">	
							<thead>
								<tr>
									<th class="center" style="width:50px;">序号</th>
									<th class="center">用户姓名</th>
									<th class="center">昵称</th>
									<th class="center">手机号</th>
									
									<th class="center">门店</th>
									<th class="center">客服</th>  
									<th class="center">创建日期</th>
									<th class="center">金额</th>
									<th class="center">微信支付</th>
									<th class="center">支付宝支付</th>
									<th class="center">银联支付</th>
									<th class="center">现金支付</th>
									
									<th class="center">返点</th>
									<th class="center">备注</th>
						
								</tr>
							</thead>
													
							<tbody>
							<!-- 开始循环 -->	
							<c:choose>
								<c:when test="${not empty varList}">
									<c:forEach items="${varList}" var="var" varStatus="vs">
										<tr>
											<td class='center' style="width: 30px;">${vs.index+1}</td>
											<%-- <td class='center'>${var.ID}</td> --%>
											<td class='center'>${var.name}</td>
											<td class='center'>${var.username}</td>
											<td class='center'>${var.phone}</td>
											
											<%-- <td class='center'><c:if test="${var.ISSCAN==0}">可以使用</c:if>
												   <c:if test="${var.ISSCAN==1}">不可使用</c:if></td> --%>
											<td class='center'>${var.STORE_NAME}</td>
											<td class='center'>${var.STAFF_NAME}</td>
											<td class='center'>${var.CREATE_TIME}</td>
											<td align='right'>
											<fmt:formatNumber type="number" value="${var.MONEY}" pattern="0.00" maxFractionDigits="2"/>
											</td>
											<td align='right'>
											<fmt:formatNumber type="number" value="${var.WECHATPAY_MONEY}" pattern="0.00" maxFractionDigits="2"/>
											</td>
											<td align='right'>
											<fmt:formatNumber type="number" value="${var.ALIPAY_MONEY}" pattern="0.00" maxFractionDigits="2"/>
											</td>
											<td align='right'>
											<fmt:formatNumber type="number" value="${var.BANKPAY_MONEY}" pattern="0.00" maxFractionDigits="2"/>
											</td>
											<td align='right'>
											<fmt:formatNumber type="number" value="${var.CASHPAY_MONEY}" pattern="0.00" maxFractionDigits="2"/>
											</td>
											<td align='right'>${var.POINTS}</td>
									        <td align='right'>${var.REMARK}</td>
										<%--	<td class='center'><c:if test="${var.STATUS==0}">已完成</c:if>
												   <c:if test="${var.STATUS==1}">待支付</c:if></td>
											 <td class="center">
												<c:if test="${QX.edit != 1 && QX.del != 1 }">
												<span class="label label-large label-grey arrowed-in-right arrowed-in"><i class="ace-icon fa fa-lock" title="无权限"></i></span>
												</c:if>
												<div class="hidden-sm hidden-xs btn-group">
													<c:if test="${QX.edit == 1 }">
													<a class="btn btn-xs btn-success" title="编辑" onclick="edit('${var.USERDISCOUNT_ID}');">
														<i class="ace-icon fa fa-pencil-square-o bigger-120" title="编辑"></i>
													</a>
													</c:if>
													<c:if test="${QX.del == 1 }">
													<a class="btn btn-xs btn-danger" onclick="del('${var.USERDISCOUNT_ID}');">
														<i class="ace-icon fa fa-trash-o bigger-120" title="删除"></i>
													</a>
													</c:if>
												</div>
												<div class="hidden-md hidden-lg">
													<div class="inline pos-rel">
														<button class="btn btn-minier btn-primary dropdown-toggle" data-toggle="dropdown" data-position="auto">
															<i class="ace-icon fa fa-cog icon-only bigger-110"></i>
														</button>
			
														<ul class="dropdown-menu dropdown-only-icon dropdown-yellow dropdown-menu-right dropdown-caret dropdown-close">
															<c:if test="${QX.edit == 1 }">
															<li>
																<a style="cursor:pointer;" onclick="edit('${var.USERDISCOUNT_ID}');" class="tooltip-success" data-rel="tooltip" title="修改">
																	<span class="green">
																		<i class="ace-icon fa fa-pencil-square-o bigger-120"></i>
																	</span>
																</a>
															</li>
															</c:if>
															<c:if test="${QX.del == 1 }">
															<li>
																<a style="cursor:pointer;" onclick="del('${var.USERDISCOUNT_ID}');" class="tooltip-error" data-rel="tooltip" title="删除">
																	<span class="red">
																		<i class="ace-icon fa fa-trash-o bigger-120"></i>
																	</span>
																</a>
															</li>
															</c:if>
														</ul>
													</div>
												</div>
											</td> --%>
										</tr>
									
									</c:forEach>
									
									<c:if test="${QX.cha == 0 }">
										<tr>
											<td colspan="100" class="center">您无权查看</td>
										</tr>
									</c:if>
								</c:when>
								<c:otherwise>
									<tr class="main_info">
										<td colspan="100" class="center" >没有相关数据</td>
									</tr>
								</c:otherwise>
							</c:choose>
							</tbody>
						</table>
						<div class="page-header position-relative">
						<table style="width:100%;">
							<tr>
								<td style="vertical-align:top;">
									<%-- <c:if test="${QX.add == 1 }">
									<a class="btn btn-mini btn-success" onclick="add();">新增</a>
									</c:if>
									<c:if test="${QX.del == 1 }">
									<a class="btn btn-mini btn-danger" onclick="makeAll('确定要删除选中的数据吗?');" title="批量删除" ><i class='ace-icon fa fa-trash-o bigger-120'></i></a>
									</c:if> --%>
								</td>
								<td style="vertical-align:top;"><div class="pagination" style="float: right;padding-top: 0px;margin-top: 0px;">${page.pageStr}</div></td>
							</tr>
						</table>
						</div>
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

		<!-- 返回顶部 -->
		<a href="#" id="btn-scroll-up" class="btn-scroll-up btn btn-sm btn-inverse">
			<i class="ace-icon fa fa-angle-double-up icon-only bigger-110"></i>
		</a>

	</div>
	<!-- /.main-container -->

	<!-- basic scripts -->
	<!-- 页面底部js¨ -->
	<%@ include file="../system/index/foot.jsp"%>
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
		//检索
		function tosearch(){
			top.jzts();
			$("#Form").submit();
		}
		$(function() {
		
			//日期框
			$('.date-picker').datepicker({
				autoclose: true,
				todayHighlight: true
			});
			
			//下拉框
			if(!ace.vars['touch']) {
				$('.chosen-select').chosen({allow_single_deselect:true}); 
				$(window)
				.off('resize.chosen')
				.on('resize.chosen', function() {
					$('.chosen-select').each(function() {
						 var $this = $(this);
						 $this.next().css({'width': $this.parent().width()});
					});
				}).trigger('resize.chosen');
				$(document).on('settings.ace.chosen', function(e, event_name, event_val) {
					if(event_name != 'sidebar_collapsed') return;
					$('.chosen-select').each(function() {
						 var $this = $(this);
						 $this.next().css({'width': $this.parent().width()});
					});
				});
				$('#chosen-multiple-style .btn').on('click', function(e){
					var target = $(this).find('input[type=radio]');
					var which = parseInt(target.val());
					if(which == 2) $('#form-field-select-4').addClass('tag-input-style');
					 else $('#form-field-select-4').removeClass('tag-input-style');
				});
			}
			
			
			//复选框全选控制
			var active_class = 'active';
			$('#simple-table > thead > tr > th input[type=checkbox]').eq(0).on('click', function(){
				var th_checked = this.checked;//checkbox inside "TH" table header
				$(this).closest('table').find('tbody > tr').each(function(){
					var row = this;
					if(th_checked) $(row).addClass(active_class).find('input[type=checkbox]').eq(0).prop('checked', true);
					else $(row).removeClass(active_class).find('input[type=checkbox]').eq(0).prop('checked', false);
				});
			});
			
			modelChange();
		});
		
		//模块选择触发门店
function modelChange(){
		var SERVICEMODULE_ID = $("#mId option:selected").val();
		if(null !=SERVICEMODULE_ID){
			$.ajax({
				type:"POST",
				url:"<%=basePath%>queryStaff/findStoreByModelId.do",
				data:{"SERVICEMODULE_ID":SERVICEMODULE_ID},
				dataType:"json",
				async: false, 
				success:function(data){
					$('#store').empty();
					$('#store').append("<option value=''></option>");
					for ( var i = 0; i < data.length; i++) {
						var id = '${pd.store}';
						if(id != data[i].STORE_ID){
							$('#store').append('<option value="' +data[i].STORE_ID+ '">' + data[i].STORE_NAME +'</option>');
						}else{
							$('#store').append('<option value="' +data[i].STORE_ID+ '"selected="selected">' + data[i].STORE_NAME +'</option>');
						}
					}
					$('#store').chosen("destroy").chosen();
				}
			});

			storeChange();
			
		}else{
			return;		
		}
}
//门店选择触发员工姓名,服务项目，客服，医生
function storeChange(){
		
			var storeId = $("#store option:selected").val();
			if(null !=storeId){
					$.ajax({
						type:"POST",
						url:"<%=basePath%>queryOrder/findstaffProjectServiceStaffByStoreId.do",
						data : {
						"storeId" : storeId
					},
					dataType : "json",
					success : function(data) {
						
						$('#staffName').empty();
						$('#staffName').append("<option value=''></option>");
						for ( var i = 0; i < data[1].length; i++) {
							var id = '${pd.staffName}';
							if(id != data[1][i].STAFF_ID){
								$('#staffName').append('<option value="' +data[1][i].STAFF_ID + '">'+ data[1][i].STAFF_NAME + '</option>"');
							}else{
								$('#staffName').append('<option value="' +data[1][i].STAFF_ID + '" selected="selected">'+ data[1][i].STAFF_NAME + '</option>"');
							}
						}
						$('#staffName').chosen("destroy").chosen();
					}
				});
			}else{
			return;
			}

	}
		
		//新增
		function add(){
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="新增";
			 diag.URL = '<%=basePath%>userdiscount/goAdd.do';
			 diag.Width = 450;
			 diag.Height = 355;
			
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
		
		
		//删除
		function del(Id){
			bootbox.confirm("确定要删除吗?", function(result) {
				if(result) {
					top.jzts();
					var url = "<%=basePath%>userdiscount/delete.do?USERDISCOUNT_ID="+Id+"&tm="+new Date().getTime();
					$.get(url,function(data){
						nextPage(${page.currentPage});
					});
				}
			});
		}
		
		//修改
		function edit(Id){
			 top.jzts();
			 var diag = new top.Dialog();
			 diag.Drag=true;
			 diag.Title ="编辑";
			 diag.URL = '<%=basePath%>userdiscount/goEdit.do?USERDISCOUNT_ID='+Id;
			 diag.Width = 450;
			 diag.Height = 355;
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
		
		//批量操作
		function makeAll(msg){
			bootbox.confirm(msg, function(result) {
				if(result) {
					var str = '';
					for(var i=0;i < document.getElementsByName('ids').length;i++){
					  if(document.getElementsByName('ids')[i].checked){
					  	if(str=='') str += document.getElementsByName('ids')[i].value;
					  	else str += ',' + document.getElementsByName('ids')[i].value;
					  }
					}
					if(str==''){
						bootbox.dialog({
							message: "<span class='bigger-110'>您没有选择任何内容!</span>",
							buttons: 			
							{ "button":{ "label":"确定", "className":"btn-sm btn-success"}}
						});
						$("#zcheckbox").tips({
							side:1,
				            msg:'点这里全选',
				            bg:'#AE81FF',
				            time:8
				        });
						return;
					}else{
						if(msg == '确定要删除选中的数据吗?'){
							top.jzts();
							$.ajax({
								type: "POST",
								url: '<%=basePath%>userdiscount/deleteAll.do?tm='+new Date().getTime(),
						    	data: {DATA_IDS:str},
								dataType:'json',
								//beforeSend: validateData,
								cache: false,
								success: function(data){
									 $.each(data.list, function(i, list){
											nextPage(${page.currentPage});
									 });
								}
							});
						}
					}
				}
			});
		};
		
		//导出excel
		function toExcel(Id){
			var url = '<%=basePath%>queryStoreddetail/storeddetailexcel.do?UID='+Id;
			var beforeUrl = $("#Form").attr("action");
			$("#Form").attr("action", url);
			$("#Form").submit();
			$("#Form").attr("action", beforeUrl);
		}
	</script>


</body>
</html>