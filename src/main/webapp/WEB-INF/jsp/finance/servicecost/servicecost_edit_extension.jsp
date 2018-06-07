<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <base href="<%=basePath%>">
    <!-- 下拉框 -->
    <link rel="stylesheet" href="static/ace/css/chosen.css"/>
    <!-- jsp文件头和头部 -->
    <%@ include file="../../system/index/top.jsp" %>
    <!-- 日期框 -->
    <link rel="stylesheet" href="static/ace/css/datepicker.css"/>

    <script type="text/javascript" src="static/js/jquery-1.7.2.js"></script>
    <script type="text/javascript" src="plugins/zTree/3.5/jquery.ztree.core-3.5.js"></script>
    <link rel="stylesheet" href="plugins/zTree/3.5/zTreeStyle.css"/>

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

                        <form action="servicecost/${msg }.do" name="Form" id="Form" method="post">
                            <input type="hidden" name="SERVICECOST_ID" id="SERVICECOST_ID"
                                   value="${pd.SERVICECOST_ID}"/>
                            <input type="hidden" name="staffIds" id="staffIds">
                            <div id="zhongxin" style="padding-top: 13px;">
                                <table id="table_report" class="table table-striped table-bordered table-hover">
                                    <tr>
                                        <td style="width:75px;text-align: right;padding-top: 13px;">项目类别:</td>
                                        <td>
                                            <input type="hidden" name="CATEGORY_ID" id="CATEGORY_ID"
                                                   value="${pd.CATEGORY_ID}">
                                            <div id="treeDemo" class="ztree"></div>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="width:75px;text-align: right;padding-top: 13px;">项目名称:</td>
                                        <td>
                                            <select name="PID" id="PID">
                                                <option value="${pd.PID}" selected="selected">${pd.PNAME}</option>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="width:75px;text-align: right;padding-top: 13px;">主治医生:</td>
                                        <td>
                                            <%--<select name="STAFF_ID" id="STAFF_ID"></select>--%>
                                            <table>
                                                <c:forEach items="${staffPdlist}" var="staff" varStatus="status">
                                                    <c:if test="${staff.STATUS==0}">
                                                        <tr>
                                                            <td>
                                                                <label class="pos-rel" style="margin-right: 20px">
                                                                    <input type="checkbox" name="staffId"
                                                                           value="${staff.STAFF_ID}" class="ace">
                                                                    <span class="lbl">${staff.STAFF_NAME} (${staff.STORE_NAME})</span>
                                                                </label>
                                                            </td>
                                                        </tr>
                                                    </c:if>
                                                </c:forEach>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="width:75px;text-align: right;padding-top: 13px;">生效时间:</td>
                                        <td><input class="span10 date-picker" name="EFFECTIVE_DATE" id="EFFECTIVE_DATE"
                                                   value="${pd.EFFECTIVE_DATE}" type="text"
                                                   data-date-format="yyyy-mm-dd" readonly="readonly" placeholder="生效时间"
                                                   title="生效时间" style="width:98%;"/></td>
                                    </tr>
                                    <tr>
                                        <td style="width:75px;text-align: right;padding-top: 13px;">价格:</td>
                                        <td><input type="text" name="PRICE" id="PRICE" value="${pd.PRICE}"
                                                   maxlength="32" placeholder="这里输入价格" title="价格" style="width:98%;"/>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="width:75px;text-align: right;padding-top: 13px;">类型:</td>
                                        <td>
                                            <%-- <input type="number" name="ISFIRST" id="ISFIRST" value="${pd.ISFIRST}" maxlength="32" placeholder="这里输入类型" title="类型" style="width:98%;"/> --%>
                                            <select name="ISFIRST" id="ISFIRST">
                                                <option value="0" <c:if test="${pd.ISFIRST==0}">selected</c:if>>初诊
                                                </option>
                                                <option value="1" <c:if test="${pd.ISFIRST==1}">selected</c:if>>复诊
                                                </option>
                                                <option value="2" <c:if test="${pd.ISFIRST==2}">selected</c:if>>课程
                                                </option>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="width:75px;text-align: right;padding-top: 13px;">状态:</td>
                                        <td>
                                            <%-- <input type="number" name="STATUS" id="STATUS" value="${pd.STATUS}" maxlength="32" placeholder="这里输入状态" title="状态" style="width:98%;"/> --%>
                                            <select name="STATUS" id="STATUS">
                                                <option value="1" <c:if test="${pd.STATUS==1}">selected</c:if>>有效
                                                </option>
                                                <option value="0" <c:if test="${pd.STATUS==0}">selected</c:if>>无效
                                                </option>
                                            </select>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="text-align: center;" colspan="10">
                                            <a class="btn btn-mini btn-primary" onclick="save();">保存</a>
                                            <a class="btn btn-mini btn-danger" onclick="top.Dialog.close();">取消</a>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            <div id="zhongxin2" class="center" style="display:none"><br/><br/><br/><br/><br/><img
                                    src="static/images/jiazai.gif"/><br/><h4 class="lighter block green">提交中...</h4>
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
<%@ include file="../../system/index/foot.jsp" %>
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

        var staffIds = new Array();
        $('input[name="staffId"]:checked').each(function () {
            //向数组中添加元素
            staffIds.push($(this).val());
        });

        if (staffIds.length === 0) {
            $('input[name="staffId"]:first').tips({
                side: 3,
                msg: '请至少选择一个医生',
                bg: '#AE81FF',
                time: 2
            });
            $("#CATEGORY_ID").focus();
            return false;
        }

        // 将数组元素连接起来以构建一个字符串
        $("#staffIds").val(staffIds.join(','));

        if ($("#CATEGORY_ID").val() == "") {
            $("#CATEGORY_ID").tips({
                side: 3,
                msg: '请选择一个类别',
                bg: '#AE81FF',
                time: 2
            });
            $("#CATEGORY_ID").focus();
            return false;
        }
        if ($("#PID").val() == "") {
            $("#PID").tips({
                side: 3,
                msg: '请选择一个项目',
                bg: '#AE81FF',
                time: 2
            });
            $("#PID").focus();
            return false;
        }
        if ($("#EFFECTIVE_DATE").val() == "") {
            $("#EFFECTIVE_DATE").tips({
                side: 3,
                msg: '请选择生效时间',
                bg: '#AE81FF',
                time: 2
            });
            $("#EFFECTIVE_DATE").focus();
            return false;
        }
        if ($("#PRICE").val() == "") {
            $("#PRICE").tips({
                side: 3,
                msg: '请输入价格',
                bg: '#AE81FF',
                time: 2
            });
            $("#PRICE").focus();
            return false;
        }
        if ($("#ISFIRST").val() == "") {
            $("#ISFIRST").tips({
                side: 3,
                msg: '请输入类型',
                bg: '#AE81FF',
                time: 2
            });
            $("#ISFIRST").focus();
            return false;
        }
        if ($("#STATUS").val() == "") {
            $("#STATUS").tips({
                side: 3,
                msg: '请输入状态',
                bg: '#AE81FF',
                time: 2
            });
            $("#STATUS").focus();
            return false;
        }
        $("#Form").submit();
        $("#zhongxin").hide();
        $("#zhongxin2").show();
    }

    function treeNodeCssSet(treeNode, treeNode) {
        var color = {color: "red"};

        if (treeNode.digit % 2 == 0) {
            color = {color: "blue"};
        }

        return color;
    }

    $(function () {
        //日期框
        $('.date-picker').datepicker({autoclose: true, todayHighlight: true});

        var setting = {
            view: {
                showLine: true,
                selectedMulti: false,
                dblClickExpand: false,
                fontCss: treeNodeCssSet
            },
            data: {
                simpleData: {
                    enable: true
                }
            },
            callback: {
                onNodeCreated: this.onNodeCreated,
                onClick: onTreeClick
            }
        };

        $.ajax({
            url: 'servicecategory/getAllCategorys.do',
            dataType: 'json',
            contenttype: "application/x-www-form-urlencoded;charset=utf-8",
            type: 'POST',
            success: function (data) {
                if (data.success) {
                    $.fn.zTree.init($("#treeDemo"), setting, data.returnvalue);
                } else {
                    alert("加载分类树出错1！");
                }
            },
            error: function (data) {
                alert("加载分类树出错2");
            }
        });
    });

    function onTreeClick(e, treeId, treeNode) {
        if (treeNode.isParent) //如果不是叶子结点，结束
        {
            alert("不能选择父分类");
            return;
        }
        else {
            $("#CATEGORY_ID").val(treeNode.id);
            $("#PID").text("");
            $.ajax({
                type: "POST",
                url: "servicecost/queryProjectByCID",
                data: {cid: $("#CATEGORY_ID").val()},
                dataType: "json",
                success: function (data) {
                    for (var i = 0; i < data.length; i++) {
                        $("#PID").append("<option value=" + data[i].PROJECT_ID + ">" +
                            data[i].PNAME + "</option>");
                    }
                },
                error: function (msg) {
                    alert(msg);
                }
            });
        }
    };

</script>
</body>
</html>