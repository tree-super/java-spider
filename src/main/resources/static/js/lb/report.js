//初始化报告页面
function initReport() {

    //禁用表单
    var formid = $("input[name='CFormId']").val();
    utils.disableForm("basic");
    utils.disableForm(formid);

    if ("wx" == utils.getParam("model")) {
        $(".ui-btn").hide();
    }
    //如果是嵌入到报告里面去的话 按钮也要隐藏
    if ("pkg" == utils.getParam("model")) {
        $(".ui-btn").hide();
    }
}

//获取数据
function initLbpcReport() {
    var pkId = utils.getParam("pkId");
    utils.ajax({
        control: "report/getLbpcReport",
        method: "getLbpcReport",
        params: {pkId: pkId},
        validate: false,
        sucFun: function (mrk, bd, cd) {
            var formid = $("input[name='CFormId']").val();
            $("#" + formid).form("load", cd.formData);
            //设置医院名称
            $("#hospitalNme").html(cd.hospitalNme);

            //初始化表头的量表名字
            $("#lbNme").html(cd.lbNme);
            //调用每个具体页面的方法，执行个性化的页面渲染
            showPage(mrk, bd, cd);

            if (cd.formData.CEffMrk == '0') {
                var obsolete = "<div class='void-icon'><img src='res/images/obsolete.png'></div>";
                $(".questionnaire-left").prepend(obsolete);
                //隐藏修正报告按钮
                $("#xzbg").hide();
            }
            //如果报告的所属机构不是当前登录人员的机构，则隐藏除打印外的所有按钮
            //目前该情况适用于 获取了患者授权查看到患者报告的场景（该种情况下，若是当天的报告，则另一家医院可以点击“修正报告”从而修改其他医院的报告）
            if (utils.CURRENT_DPT_ID != cd.formData.CDptId) {
                //$(".ui-btn").remove();
                $(".ui-btn").children().each(function (i, ele) {
                    if ($(ele).find("i").html() != '打印报告') {
                        $(ele).remove();
                    }
                });
            }
        }
    });
}

//打印报告
function printReport() {
    //打印的时候取消滚动条
    try {
        $(".questionnaire-left").css("overflow", "inherit");
    } catch (e) {
    }
    $(".noprint").hide();
    window.print();
    $(".noprint").show();
    //打印完毕恢复滚动条
    try {
        $(".questionnaire-left").css("overflow", "auto");
    } catch (e) {
    }
}

function modifyReport() {
    var createTime = $("input[name='TTestTm']").val();//当前报告的测试时间
    if (createTime == "") {//如果创建时间==空，返回
        return;
    }
    var v1 = new Date(utils.SYS_DATE);//获取当前系统时间对象
    var v2 = new Date(createTime);//获取创建时间的Date对象
    if (v1.getFullYear() == v2.getFullYear() && v1.getMonth() == v2.getMonth() && v1.getDate() == v2.getDate()) {
        //假设系统和当前时间相等，则当系统时间和报告创建时间相等的时候，可以跳转到修改页面，否则不再跳转
        var arr = location.href.replace(/-report/, "");
        window.location.href = arr + "&model=modify";
    } else {
        utils.alert("只能修正当天的报告");
    }
}

function viewReport() {
        var arr = location.href.replace(/-report/, "");
        window.location.href = arr + "&model=view";
}


function exportPDF() {
    $(".ui-btn").hide();
    //创建照片
    let c = document.createElement("canvas");
    //照片高度和宽度是页面元素的两倍
    c.width = document.body.clientWidth * 2
    c.height = document.body.clientHeight * 2
    c.getContext("2d").scale(2, 2);
    html2canvas(document.body, {background: '#FFFFFF'}).then(function (canvas) {
            var contentWidth = canvas.width;
            var contentHeight = canvas.height;
            // 一页pdf显示html页面生成的canvas高度;
            var pageHeight = contentWidth / 592.28 * 841.89;
            // 未生成pdf的html页面高度
            var leftHeight = contentHeight;
            // pdf页面偏移
            var position = 0;
            // a4纸的尺寸[595.28,841.89]，html页面生成的canvas在pdf中图片的宽高
            var imgWidth = 595.28;
            var imgHeight = 592.28 / contentWidth * contentHeight;
            var pageData = canvas.toDataURL('image/jpeg', 1.0);
            // eslint-disable-next-line
            var pdf = new jsPDF('', 'pt', 'a4');
            // 有两个高度需要区分，一个是html页面的实际高度，和生成pdf的页面高度(841.89)
            // 当内容未超过pdf一页显示的范围，无需分页
            if (leftHeight < pageHeight) {
                pdf.addImage(pageData, 'JPEG', 0, 0, imgWidth, imgHeight)
            } else {
                while (leftHeight > 0) {
                    pdf.addImage(pageData, 'JPEG', 0, position, imgWidth, imgHeight)
                    leftHeight -= pageHeight;
                    position -= 841.89;
                    // 避免添加空白页
                    if (leftHeight > 0) {
                        pdf.addPage()
                    }
                }
            }
            var name = $("[name = 'CChildNme']").val();
            var date = $("[name = 'TTestTm']").val();
            var lbNme = $("#lbNme").html();
            date = date.replace(/-/g, "");
            pdf.save(name + '_' + lbNme + '_' + date + '.pdf');
        }
    );
    $(".ui-btn").show();
}

