//---------------------------------------------
//---------------------------------------------
//本页面是所有量表页面都需要引用的js，主要业务方法有如下几个：
//1、initCommon 初始化页面和场景
//2、initData 初始化页面的数据
//3、save 量表保存
//4、submit 提交并生成报告
//5、printQuest 打印答题
//6、heartBeat心跳包，每隔10分钟向后台请求一次，根据量表的具体完成时间情况在window.onload中调用调用
//---------------------------------------------
//---------------------------------------------

//全局js变量
var doctors_="";//申请医生
var executors_="";//执行人
var hospitals_="";//复查医院
var phoneQueryData_ = {//根据手机号码查询孩子数据
	"hasParent" : false,
	"hasChildren" : false
};

var childHtml_="";//排号孩子的html
var ref_=null;//野狗云存储地址引用


//初始化页面和场景等
function initCommon() {
	
	//初始化页面场景（主要是按钮的显示隐藏，页面是否可编辑等）
	initScene();
	
	//询问是否全屏
	confirmFullScreen();
	
	//初始化右侧的内容
	initRight();
	
	//量表标题后面的？的弹出事件
	utils.initHelpWindow(true);
	
	//初始化tooltips 录入人、申请医生、与儿童关系
	initTooltips();
	
	// 转诊通知徽标
	// initRfApplyCount();

	////答题模式的切换事件
	switchAnswerModel();
	
	//事件绑定 
	bindEvent();
	
	//绑定题目表单的onchange事件，校验通过时移除相关提示
	utils.bindValidateEvent(['basic','f1','f2','f3']);

	//初始化野狗云
	//RTM开关
	// if(_wilddogOrRtm=="wilddog"){
    //     // initWilddog();
    // }else{
    //     initRtm();
    // }

	//禁用页面的验证，避免初始化进入页面时 控件验证不通过变成红色，业务需要自己打开校验（utils.ajax方法当validate为true时，会自动打开校验）
	disableValidation();
}


function confirmFullScreen(){
	//如果是出于顶层页面，则询问是否全屏显示
	if(top.location==self.location){  
		$(".questionnaire-tit i").before("<div class='fullScreen'>按【F11】开启/退出全屏</div>");
        $.messager.confirm('确认?','是否全屏显示</br>注：按【F11】开启/退出全屏',function(r){
        	if(r){
        		//进入全屏模式
        		gotoFullScreen();
				//模拟点击右侧，将右侧隐藏起来
        		$(".show-more").click();
        	}
        });
    } 
}

//开启全屏
function gotoFullScreen(){
	// 判断各种浏览器，找到正确的方法  
	var element = document.documentElement;
	var requestMethod = element.requestFullScreen || //W3C  
						element.webkitRequestFullScreen || //Chrome等  
						element.mozRequestFullScreen || //FireFox  
						element.msRequestFullScreen; //IE11  
	if (requestMethod) {   
		requestMethod.call(element);  
	}  else if (typeof window.ActiveXObject !== "undefined") {//for Internet Explorer   
		var wscript = new ActiveXObject("WScript.Shell");   
		if (wscript !== null) {    
			wscript.SendKeys("{F11}");   
		}
	}
}
//初始化数据
//1、第一次进后台获取量表介绍，主要是页面右侧的数据，和填表人、申请医生、复查医院
//2、第二次进后台获取业务数据
function initData() {
	var pkId = null,lbId=null,model=null;
	pkId = utils.getParam("pkId");
	lbId = $("input[name='CLbId']").val();
	model = utils.getParam("model");
	
	//从后台获取量表配置信息，包括：填表人、申请医生、复查医院、量表右侧的配置数据
	utils.ajax({
		control : "lb/getLbBasInfo",
		params :{"lbId":lbId},
		validate : false,
		async:false,//必须用同步，否则有可能该方法还没返回就去后台获取业务数据了，而业务数据回填的时候需要用到lbMsg_对象。js会报错
		sucFun : function(mrk, bd, cd) {
			if(cd.tipsinfo &&  cd.tipsinfo.doctors_) window.doctors_=cd.tipsinfo.doctors_;
			if(cd.tipsinfo &&  cd.tipsinfo.executors_) window.executors_=cd.tipsinfo.executors_;
			if(cd.tipsinfo &&  cd.tipsinfo.hospitals_) window.hospitals_=cd.tipsinfo.hospitals_;
			if(cd.lbMsg_) window.lbMsg_ = cd.lbMsg_;
			//因为模式和适用年龄的长度是动态的，必须赋值后再确定高度，否则会出现竖向滚动条
			// 给右边量表结算等内容赋值
			document.title=lbMsg_[lbId].name;//解决IE标题兼容性问题
			$("div.questionnaire-tit").find("h4").html(lbMsg_[lbId].name);
			$("#introduce_").find("p").html(lbMsg_[lbId].introduce);
			$("#guide_").find("p").html(lbMsg_[lbId].guide);
			$("#age_").html(lbMsg_[lbId].age);
			//量表介绍、指导语动态设置高度
			var questionnaireHeight = $(".questionnaire-right").height();
			var rightBoxHeight = $(".questionnaire-box");
			var rightHeight = questionnaireHeight-rightBoxHeight.eq(0).outerHeight()-rightBoxHeight.eq(1).outerHeight();
			$(".ui-scan ol").css("height",rightBoxHeight-42)
			rightBoxHeight.eq(2).css("height",rightHeight/2);
			rightBoxHeight.eq(3).css("height",rightHeight/2);
		}
	});
	
	var defaultFormId = $("input[name='CFormId']").val();

	//只有主键不为空才去后台获取数据
	if (pkId != undefined && pkId != null && pkId != '') {
		utils.ajax({
			service : "pcAction",
			method : "getLbpcQuest",
			params : {
				"pkId" :pkId,
				"model":model
			},
			validate : false,
			sucFun : function(mrk, bd, cd) {
				if (mrk) {
					var formId = cd.formId;
					if (utils.getParam("model") == "print") {
						formId = $("#printFormId").val();
					}
					//如果是从编辑进来的且formId为* ，则标示是从体检套餐来的，按默认的formId展现表单
					if(utils.getParam("model") == "edit" && formId=="*"){
						formId = defaultFormId; 
						$("input[name='CFormId']").val(formId);
					}
					$("div.questionnaire-right").find("li[formId='" + formId + "']").eq(0).addClass("current").siblings().removeClass("current");
					var index = $("div.questionnaire-right").find("li[formId='" + formId + "']").eq(0).nextAll().length;
					$("div.subject").eq(2 - index).show().siblings("div.subject").hide();
					showData(formId, cd["formData"],cd["extraData"]);
					
					//如果后台返回有archid 则显示图标，并绑定click事件
					var archBasId = cd.archBasId;
					
					//不管档案id是否为空，都要去后台判定是否有数据，因为该childId在其他医院可能有记录,默认将按钮隐藏，若后台查询到有数据，则在方法内显示该按钮
					//debugger;
					//$("#arch").on("click",getChildDataPreview(cd["formData"].CChildId,cd["formData"].CParentId,archBasId));
					$("#arch").hide();
					
					disableBasicInfo();
					
					//如果不是由该用户创建的，则该用户不能修改
					if(!cd.editableMrk){
						hideBtn();
					}
				}
			}
		});
	}
	
	
	//如果是从工作平台过来的，则进行如下设置
	//如下的代码必须在initCommon之后调用，否则无法移除CPhoneNo的焦点离开事件（因为该事件是在initCommon里面绑定的）
	//如下代码必须在pcAction 的getLbBasInfo之后调用（该ajax是同步的），因为TBirthday的onchange事件会触发校验孩子的年龄是否符合测评年龄（而该判断以来后台回来的数据）
	if(utils.getParam("from") && utils.getParam("from")=="workflat"){
		$(".show-more").hide();
		var childId = utils.getParam("childId");
		utils.ajax({
			service:'archAction',
			method:'getChildInfoByPaidui',
			params:{
				basId:utils.getParam("archId"),
				parentId:utils.getParam("parentId"),
				childId:utils.getParam("childId")
			},
			sucFun:function(mrk,bd,cd){
				//选择孩子的时候开始校验数据库里面已经有的孩子是否符合当前报告的测评区间
				$("input[name='CPhoneNo']").val(cd.CPhoneNo).attr("readonly",true);
				$("input[name='CChildNme']").val(cd.CChildNme).attr("readonly",true);;
				$("#CChildSex").combobox('setValue', cd.CChildSex).combobox("readonly",true);;
				$("#TBirthday").datebox('setValue', cd.TBirthday).datebox("readonly",true);;
				
				$("input[name='NYear']").val(cd.NYear).trigger("change");
				$("input[name='NMonth']").val(cd.NMonth).trigger("change");
				$("input[name='NDay']").val(cd.NDay).trigger("change");
				
				$("#TPreBirth").datebox('setValue', cd.TPreBirth).trigger("change");
				$("input[name='NBirthHeight']").val( cd.NBirthHeight).trigger("change");
				$("input[name='NBirthWeight']").val( cd.NBirthWeight).trigger("change");
				$("input[name='NConceiveWeek']").val( cd.NConceiveWeek).trigger("change");
				$("input[name='NConceiveDay']").val( cd.NConceiveDay).trigger("change");
				
				//触发一次验证事件，否则红色不消失，但是不确定是否所有的量表孕周都是validatebox类型，所以用异常捕获不处理
				try{	$("input[name='NBirthHeight']").validatebox("validate");	}catch(e){}
				try{	$("input[name='NBirthWeight']").validatebox("validate");	}catch(e){}
				try{	$("input[name='NConceiveWeek']").validatebox("validate");	}catch(e){}
				try{	$("input[name='NConceiveDay']").validatebox("validate");	}catch(e){}
				
			}
		})
//		$("input[name='CPhoneNo']").val(utils.getParam("phoneNo")).attr("readonly",true);
//		$("input[name='CChildNme']").val(utils.getParam("childNme")).attr("readonly",true);
//		$("#CChildSex").combobox("setValue",utils.getParam("childSex")).combobox("readonly",true);
//		$("#TBirthday").datebox("setValue",utils.getParam("birthday")).datebox("readonly",true);
		$("#clearBasicInfo").hide();
		$("input[name='CPhoneNo']").eq(0).unbind("blur")
		//$(".questionnaire-right").hide();//隐藏右侧
	}
	
	//从身高管理过来的 不显示排队信息，否则与工作平台的患者信息会不一致
	if(utils.getParam("from")=="sggl"){
		$("input[name='CPhoneNo']").val(utils.getParam("phoneNo")).attr("readonly",true);
		$("input[name='CChildNme']").val(utils.getParam("childNme")).attr("readonly",true);
		$("#CChildSex").combobox('setValue', utils.getParam("childSex")).combobox("readonly",true);
		$("#TBirthday").datebox('setValue', utils.getParam("birthday")).datebox("readonly",true);
		if(utils.getParam("svcPkgId")){
			$("input[name='CSvcPkgId']").val(utils.getParam("svcPkgId"));
		}
		
		//$("#TBirthday").trigger("change");
		/*$("input[name='NYear']").val(cd.NYear).trigger("change");
		$("input[name='NMonth']").val(cd.NMonth).trigger("change");
		$("input[name='NDay']").val(cd.NDay).trigger("change");
		
		$("#TPreBirth").datebox('setValue', cd.TPreBirth).trigger("change");
		$("input[name='NBirthHeight']").val( cd.NBirthHeight).trigger("change");
		$("input[name='NBirthWeight']").val( cd.NBirthWeight).trigger("change");
		$("input[name='NConceiveWeek']").val( cd.NConceiveWeek).trigger("change");
		$("input[name='NConceiveDay']").val( cd.NConceiveDay).trigger("change");
		
		//触发一次验证事件，否则红色不消失，但是不确定是否所有的量表孕周都是validatebox类型，所以用异常捕获不处理
		try{	$("input[name='NBirthHeight']").validatebox("validate");	}catch(e){}
		try{	$("input[name='NBirthWeight']").validatebox("validate");	}catch(e){}
		try{	$("input[name='NConceiveWeek']").validatebox("validate");	}catch(e){}
		try{	$("input[name='NConceiveDay']").validatebox("validate");	}catch(e){}*/
	}
	
}

//手机号码变更确定孩子后，获取孩子在本院的治疗数据
function getChildDataPreview(childId,parentId,archId){
	//查看哪些tab有数据，并显示小红点
	utils.ajax({
		service:"workflatAction",
		method:"getDataPreview",
		params:{childId:childId,parentId:parentId},
		sucFun:function(mrk,bd,cd){
			//如果有数据则显示查看按钮
			if(cd['case']>0||cd['hweight']>0||cd['physique']>0||cd['teeth']>0||cd['hearing']>0||cd['vision']>0||cd['heart']>0||cd['hip']>0
					||cd['earNose']>0||cd['eyes']>0||cd['mouth']>0||cd['historyPc']>0||cd['word']>0||cd['action']>0){
				$("#arch").show();
			}
			$("#arch").tooltip({
				content: $("<div class='ui-tip-content'></div>"),
				showEvent: 'mouseover',//指定显示事件
				onUpdate: function(content) {
					var h4="<h4>该患者在系统内有如下检查记录：</h4>";
					var lis="";
					
					if(cd["case"]>0){
						lis = lis+"<li>专案管理</li>";
					}
					if(cd["hweight"]>0){
						lis = lis+"<li>身高体重</li>";
					}
					if(cd["physique"]>0){
						lis = lis+"<li>体格检查</li>";
					}
					if(cd["teeth"]>0){
						lis = lis+"<li>龋齿防治</li>";
					}
					if(cd["hearing"]>0){
						lis = lis+"<li>听力筛查</li>";
					}
					if(cd["vision"]>0){
						lis = lis+"<li>视力筛查</li>";
					}
					if(cd["heart"]>0){
						lis = lis+"<li>心脏病筛查</li>";
					}
					if(cd["hip"]>0){
						lis = lis+"<li>髋关节筛查</li>";
					}
					if(cd["earNose"]>0){
						lis = lis+"<li>耳鼻检查</li>";
					}
					if(cd["eyes"]>0){
						lis = lis+"<li>眼保检查</li>";
					}
					if(cd["mouth"]>0){
						lis = lis+"<li>口腔保健</li>";
					}
					if(cd["action"]>0){
						lis = lis+"<li>行为观察</li>";
					}
					if(cd["historyPc"]>0){
						lis = lis+"<li>量表测评</li>";
					}
					if(cd["word"]>0){
						lis = lis+"<li>非电子化档案</li>";
					}
					
					var str='';
					if(_hasRole101){
						str = h4+"<ul>"+lis+"<li class='ui-add' onclick=javascript:gotoChildWorkflat('"+archId+"')>点击查看</li>"+"</ul>";
					}else{
						str = h4+"<ul>"+lis+"</ul>";
						
					}
					content.panel({
						width: 200,
						border: false,
						content:str
					});
					
				},
				onShow : function() {
					//显示的时候进行事件的绑定
					var t = $(this);
					t.tooltip('tip').unbind().bind('mouseenter', function() {
						t.tooltip('show');
					}).bind('mouseleave', function() {
						t.tooltip('hide');
					}).bind('click', function() {
						t.tooltip('hide');
					});
				}
			});
		}
	});
}

function gotoChildWorkflat(archId){
	
	var CParentId =$("input[name='CParentId']").val();
	var CArchId=archId;
	var CChildId=$("input[name='CChildId']").val();
	var CChildNme=$("input[name='CChildNme']").val();
	var CChildSex=$("input[name='CChildSex']").val();
	var TBirthday=$("input[name='TBirthday']").val();
	var CPhoneNo=$("input[name='CPhoneNo']").val();
	
	parent.utils.addTab({
		url: "/jsp/workflat/mainFlat.jsp?from=lb&CParentId="+CParentId+"&CChildId="+CChildId+"&CChildNme="+CChildNme+"&CChildSex="+CChildSex+"&TBirthday="+TBirthday+"&CPhoneNo="+CPhoneNo+"&CArchId="+CArchId,
		title: "【"+CChildNme+"】的档案"
	});
}

//保存量表
function save(custData) {
	// 当前显示的formId
	var formId = $("div.questionnaire-right").find("li.current").eq(0).attr("formId");
	//没有产生效果
	//var mrk2 = $("#basic").form("enableValidation").form("validate");
	//必填项
	var birthday=$("#TBirthday").combobox('getValue');
	var sex=$("#CChildSex").combobox('getValue');
	var name=$("input[name='CChildNme']").val();
	var tel=$("input[name='CPhoneNo']").val();
	var msg="";
	var arr=["","","",""];
	if(tel == undefined ||tel == null ||tel == ''){
		arr[0]="联系手机";
	}
	if(name == undefined ||name == null ||name == ''){
		arr[1]="姓名";
	}
	if(sex == undefined ||sex == null ||sex == ''){
		arr[2]="性别";
	}
	if(birthday == undefined ||birthday == null ||birthday == ''){
		arr[3]="出生年月";
	}
	for(var i=0;i<arr.length;i++){
		if(arr[i]!=""){
			msg+=(arr[i]+"\n");
		}
	}
	if(msg!=""){
		msg+="不能为空";
		utils.alert(msg);
		return;
	}
	
	try{
		//该方法定义在每个量表中，如果有这个方法，则调用，如果没有，则异常被吃掉，继续保存业务
		//若有该方法，则在方法内部处理业务逻辑，如果需要中断保存方法，则在beforeSave中返回false
		if(!beforeSave()){
			return;
		}
	}catch(e){}
	
//	if(!mrk2){
//		alert("必填项不能为空");
//		return false;
//	}
	
	utils.ajax({
		service : "pcAction",
		method : "saveOrUpdate",
		forms : [ "basic", formId ],
		params:custData?custData:{},
		validate:false,
		mask:true,
		sucFun : function(mrk, bd, cd) {
			if (mrk) {
				$("input[name='CPkId']").val(cd.CPkId);
				$("input[name='CParentId']").val(cd.CParentId);
				$("input[name='CChildId']").val(cd.CChildId);
				//禁用基本信息的部分字段
				disableBasicInfo();
				utils.alert("保存成功");
			}else{
				if(cd.errorMsg)
					utils.alert(cd.errorMsg);
				else
					utils.alert("保存失败");
			}
		}
	});
}

//提交量表:
function submit(custData,relocate) {
	// 当前显示的formId
	var formId = $("div.questionnaire-right").find("li.current").eq(0).attr("formId");
	var mrk11 = utils.validate([ formId ]) ;
	var mrk12= $("#"+formId).form("enableValidation").form("validate");
	var mrk1= mrk11 && mrk12;
	
	var mrk21 = utils.validate([ "basic" ]);
	var mrk22= $("#basic").form("enableValidation").form("validate");
	var mrk2 = mrk21 && mrk22;
	
	// 校验不通过提示
	if (!mrk2 || !mrk1) {
		//找到所有easyui校验不通过的样式，查看其上级是否有msgTips的样式，如果有则需要自己变成红色必输
		$("input[class='easyui-validatebox validatebox-text validatebox-invalid']").parents(".msgTips_").addClass("tips-err");
		utils.alert("必填项不能为空");
		return false;
	}

	try{
		//该方法定义在每个量表中，如果有这个方法，则调用，如果没有，则异常被吃掉，继续提交业务
		//若有该方法，则在方法内部处理业务逻辑，如果需要中断保存方法，则在beforeSubmit中返回false
		if(!beforeSubmit()){
			return;
		}
	}catch(e){}

	/**
	utils.ajax({
		control : "lb/submit",
		method : "submit",
		mask:true,
        async:false,
		forms : [ "basic", formId ],
		params:custData?custData:{},
		sucFun : function(mrk, bd, cd) {
			if (mrk) {
				var lbId = $("input[name='CLbId']").val();
				var tag = "";
				if(cd.tag){//debugger;
					var tmp = cd.tag+"";
					tmp = tmp.replace(new RegExp(",,","gm"),",");
					tmp = tmp.replace(new RegExp(",","gm"),"，");
					tag="<br/>加入【"+tmp+"】专案，请在左侧[专案查询]菜单中查看"
				}
				utils.alert("提交成功"+tag,function(){
					var url = "/report/" + lbId+ "?pkId=" + cd.CPkId;
					
					//如果当前打开的页面有如下两个参数，则跳转到报告中去的时候需要带上这两个参数，否则从报告中点击修改的时候，打开的页面会显示右边的内容
					//主要用户mainFlat中的身高体重和体格检查
					if(utils.getParam("from")){
						url=url+"&from="+utils.getParam("from");
					}
					if(utils.getParam("showModel")){
						url=url+"&showModel="+utils.getParam("showModel")
					}
					$(".ui-btn").remove();
					if(relocate==undefined||relocate){
//						$("#saveBtn").remove();
//						$("#submitBtn").remove();
						location.href = url;
                    }
				});
			}else{
				if(cd.errorMsg)
					utils.alert(cd.errorMsg);
				else
					utils.alert("提交失败");
			}
		}
	});
	 **/


	fetch("http://localhost:8089/lb/submit", {
		"headers": {
			"accept": "application/json, text/javascript, */*; q=0.01",
			"accept-language": "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7",
			"cache-control": "no-cache",
			"content-type": "application/x-www-form-urlencoded; charset=UTF-8",
			"pragma": "no-cache",
			"sec-ch-ua": "\"Chromium\";v=\"92\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"92\"",
			"sec-ch-ua-mobile": "?0",
			"sec-fetch-dest": "empty",
			"sec-fetch-mode": "cors",
			"sec-fetch-site": "same-origin",
			"x-requested-with": "XMLHttpRequest"
		},
		"referrer": "http://localhost:8089/lb/0809",
		"referrerPolicy": "strict-origin-when-cross-origin",
		"body": "RequestJsonData_=%25257B%252522bizData_%252522%25253A%25255B%25257B%252522formId_%252522%25253A%252522basic%252522%25252C%252522data_%252522%25253A%25255B%25257B%252522CLbId%252522%25253A%2525220809%252522%25252C%252522CPkId%252522%25253A%252522%252522%25252C%252522CPkgId%252522%25253A%252522%252522%25252C%252522CParentId%252522%25253A%252522%252522%25252C%252522CChildId%252522%25253A%252522%252522%25252C%252522CUserId%252522%25253A%252522%252522%25252C%252522NYear%252522%25253A%2525229%252522%25252C%252522NMonth%252522%25253A%2525220%252522%25252C%252522NDay%252522%25253A%25252225%252522%25252C%252522COrgPkId%252522%25253A%252522%252522%25252C%252522NSeqNum%252522%25253A%252522%252522%25252C%252522CFormId%252522%25253A%252522f2%252522%25252C%252522CExt1%252522%25253A%252522%25255B%25257B%25255C%252522zq%25255C%252522%25253A10%25252C%25255C%252522cw%25255C%252522%25253A0%25252C%25255C%252522lb%25255C%252522%25253A5%25252C%25255C%252522zs%25255C%252522%25253A15%25257D%25252C%25257B%25255C%252522zq%25255C%252522%25253A10%25252C%25255C%252522cw%25255C%252522%25253A1%25252C%25255C%252522lb%25255C%252522%25253A5%25252C%25255C%252522zs%25255C%252522%25253A15%25257D%25252C%25257B%25255C%252522zq%25255C%252522%25253A11%25252C%25255C%252522cw%25255C%252522%25253A5%25252C%25255C%252522lb%25255C%252522%25253A4%25252C%25255C%252522zs%25255C%252522%25253A15%25257D%25252C%25257B%25255C%252522zq%25255C%252522%25253A12%25252C%25255C%252522cw%25255C%252522%25253A1%25252C%25255C%252522lb%25255C%252522%25253A3%25252C%25255C%252522zs%25255C%252522%25253A15%25257D%25252C%25257B%25255C%252522zq%25255C%252522%25253A14%25252C%25255C%252522cw%25255C%252522%25253A0%25252C%25255C%252522lb%25255C%252522%25253A1%25252C%25255C%252522zs%25255C%252522%25253A15%25257D%25252C%25257B%25255C%252522zq%25255C%252522%25253A16%25252C%25255C%252522cw%25255C%252522%25253A1%25252C%25255C%252522lb%25255C%252522%25253A0%25252C%25255C%252522zs%25255C%252522%25253A16%25257D%25255D%252522%25252C%252522CExt2%252522%25253A%252522901.2345679012345%252522%25252C%252522CExt3%252522%25253A%2525222%252522%25252C%252522hiddenNum%252522%25253A%2525221%252522%25252C%252522CPhoneNo%252522%25253A%25252215573150294%252522%25252C%252522CChildNme%252522%25253A%252522%2525E5%2525AE%252581%2525E6%252580%25259D%2525E7%25259D%2525BF%252522%25252C%252522CChildSex%252522%25253A%252522F%252522%25252C%252522TBirthday%252522%25253A%2525222012-08-01%252522%25252C%252522CMenzhenNo%252522%25253A%252522123412341234%252522%25252C%252522CInfoOffer%252522%25253A%252522%252522%25252C%252522CChildRel%252522%25253A%252522%252522%25252C%252522CIllHistory%252522%25253A%252522%252522%25252C%252522CExecutor%252522%25253A%252522aa1%252522%25252C%252522CDoctor%252522%25253A%252522aa2%252522%25257D%25255D%25257D%25252C%25257B%252522data_%252522%25253A%25255B%25257B%25257D%25255D%25257D%25255D%25252C%252522custData_%252522%25253A%25257B%25257D%25257D",
		"method": "POST",
		"mode": "cors",
		"credentials": "include"
	});

	utils.alert("提交成功",function(){
		var url = "/report/0809?pkId=df5c1fd2-9e23-4eee-8c99-c5c42fc336f2";

		//如果当前打开的页面有如下两个参数，则跳转到报告中去的时候需要带上这两个参数，否则从报告中点击修改的时候，打开的页面会显示右边的内容
		//主要用户mainFlat中的身高体重和体格检查
		if(utils.getParam("from")){
			url=url+"&from="+utils.getParam("from");
		}
		if(utils.getParam("showModel")){
			url=url+"&showModel="+utils.getParam("showModel")
		}
		$(".ui-btn").remove();
		if(relocate==undefined||relocate){
//						$("#saveBtn").remove();
//						$("#submitBtn").remove();
			location.href = url;
		}
	});
};

//打印答题
function printQuest(){
	//因打印的时候placeholder也会打印出来，故 对特殊疾病处理为空字符串，打印完毕后再设置为空
	var val = $("input[name='CIllHistory']").val();
	if(!val){
		$("input[name='CIllHistory']").val(" ");
	}
	
	$(".noprint").hide();
	$(".questionnaire-left").css("overflow","initial");
	window.print();
	$(".questionnaire-left").css("overflow","auto");
	$(".noprint").show();
	var CLbId=$("input[name='CLbId']").val();
//	if(CLbId.indexOf("17")==0){
//		$(".questionnaire-right").hide();
//	};

	if($("input[name='CIllHistory']").val()==" "){
		$("input[name='CIllHistory']").val("");
	}
	//如果是打印模式
	if(utils.getParam("model") == "print"||utils.getParam("model") == "view"){
		hideBtn();
	}
}


//事件绑定 
//1、手机号码的失去焦点事件
//2、孕周推算预产期
//3、预产期推孕周
//4、生日计算年龄
function bindEvent(){
	//-----------------------------------------------------------------------------
	//以下题目对应答案的事件绑定
	//-----------------------------------------------------------------------------
	//点击相应答案当前标签机上current
	$("div.each-question dl dd>label").find("input").click(function(){
		if($(this).parents("em").length<=0&&$(this).parents("ul").html()==undefined){
			//判断当前是否属于radio
			if($(this).attr("type")=="radio"){
				$(this).parents("dd").addClass("current").siblings().removeClass("current");
			//判断当前是否属于checkbox
			}else if($(this).attr("type")=="checkbox"){
				$(this).parents("dd").toggleClass("current");
			//判断当前点击input也增加current样式
			}else if(!$(this).parents("dd").hasClass("current")==true&&$(this).attr("type")=="text"){
				$(this).parents("dd").toggleClass("current").siblings().removeClass("current");
			}
		}
	});
	//答题卡模式点击选中
	$("div.answer-sheet dl dd label input").click(function(){
		if($(this).attr("type")=="checkbox"){
			$(this).parent().toggleClass("current");
		}else{
			$(this).parent().addClass("current").siblings().removeClass("current");
		}
	});
	
	
	//-----------------------------------------------------------------------------
	//以下是字段的事件绑定
	//-----------------------------------------------------------------------------
	//通过手机号码获取孩子信息，需要用绑定，便于后面只读的时候解绑事件
	$("input[name='CPhoneNo']").eq(0).on("blur",function(){
		getBasicByPhoneNo();
	});
	
	//孕周推算预产期,事件没有循环触发，可能是因为绑定在了validatebox上
	$("input[name='NConceiveWeek']").bind("change",function(){
		var weeks=$(this).val();
		var birthday=$("#TBirthday").datebox("getValue");
		if(weeks==null||weeks==""||weeks==undefined ||birthday==null||birthday==""||birthday==undefined){
			return ;
		}
		
		var days = $("input[name='NConceiveDay']").val();
		if(days==null||days==""||days==undefined){
			days= 0;
		}
		var ycq=getYCQ(birthday,weeks,days);
		$("#TPreBirth").datebox("setValue",ycq);
	});
	//孕天推算预产期,事件没有循环触发，可能是因为绑定在了validatebox上
	$("input[name='NConceiveDay']").bind("change",function(){
		var days=$(this).val();
		var birthday=$("#TBirthday").datebox("getValue");
		if(days==null||days==""||days==undefined ||birthday==null||birthday==""||birthday==undefined){
			return ;
		}
		
		var weeks = $("input[name='NConceiveWeek']").val();
		if(weeks==null||weeks==""||weeks==undefined){
			weeks= 0;
		}
		var ycq=getYCQ(birthday,weeks,days);
		$("#TPreBirth").datebox("setValue",ycq);
	});
	//预产期推孕周
	$("#TPreBirth").datebox({
		onChange:function(newdate,olddate){
			if(newdate=="")return;
			var birthday=$("#TBirthday").datebox("getValue");
			var prebirth=$("#TPreBirth").datebox("getValue");
			 if(isNaN(Date.parse(prebirth))){
				$("#TPreBirth").datebox("setValue","");
				return;
			}
			if(birthday==null ||birthday==""||birthday==undefined){//孕周预产期互推算法生日为必须条件
				//return;//不能返回，反悔后后面的量表页面的onchange就不执行了
			}else{
				var result=getYunZhou(birthday,prebirth);
				$("input[name='NConceiveWeek']").val(result.weeks);
				$("input[name='NConceiveDay']").val(result.days);
			}
			try{onTPreBirthChange();}catch(e){}
			
			//预产期发生变化，需要计算预产期的校正年龄
			var date = new Date(newdate);
			var sysdate=new Date(utils.SYS_DATE); 
			var result = calcAge(date, sysdate);//取出测评者年龄
			$("input[name='NYear1']").val(result.YEAR);
			$("input[name='NMonth1']").val(result.MONTH);
			$("input[name='NDay1']").val(result.DAY);
		}
	});
	// 绑定TBirthday的日期改变事件，当用户选择了日期后对用户选择的日期进行校验
	$("#TBirthday").datebox({
		onChange : function(newdate,olddate){
			if(newdate=="")return;
			if(isNaN(Date.parse(newdate))){
				$("#TBirthday").datebox("setValue","");
				return;
			}
			var sysdate=new Date(utils.SYS_DATE); 
			var date = new Date(newdate);
			// 不能选择今天之后的时间
			if (!dateValidate(sysdate, date)) {
				utils.alert("不能选择今天之后的时间");
				$('#TBirthday').datebox('setValue', '');// 重置编辑框
				return false;
			}
			
			var result = calcAge(date, sysdate);//取出测评者年龄
			if (!isInRange($("#CChildSex").combobox("getValue"), result)) {
				var lbid = $("input[name='CLbId']").val();//当前量表ID
				var range = lbMsg_[lbid].range;
				if(range.AlertType=="alert"){//必须拦截的表
					utils.alert("该儿童不符合测评适用年龄");//弹出提示
					$('#TBirthday').datebox('setValue', '');//清空不符合年龄段的生日
					// clearBasicInfo()//清楚所有的基本信息
					return ;//返回
				}else{//非必须拦截的表
					var isReturn=false;
					$.messager.confirm('提示','该儿童年龄不在工具推荐年龄范围内,是否继续测试?',function(r){
					    if (!r){
					    	$('#TBirthday').datebox('setValue', '');//清空不符合年龄段的生日
					    	isReturn=true;
					    } 
					    	
					});
					if(isReturn)return;
				}
				return;
			}
			//初始化隐藏字段
			$("input[name='NYear']").val(result.YEAR).trigger("change");
			$("input[name='NMonth']").val(result.MONTH);
			$("input[name='NDay']").val(result.DAY);
			//try{
			//该方法定义在每个量表中，如果有这个方法，则调用，如果没有，则异常被吃掉
			//若有该方法，则在方法内部处理业务逻辑
			try{
				var birthday=$("#TBirthday").datebox("getValue");
				var prebirth=null;
				try{prebirth=$("#TPreBirth").datebox("getValue");}catch(e){}//有的页面没有这个字段，不catch会报错，导致下面的都不执行了
				//date是Date类型数据,birthday参数时Str类型,互相转换的过程中可能存在某些问题
//						var birthday=date;
//						var prebirth=$("#TPreBirth").datebox("getValue");
				if(prebirth==null ||prebirth==""||prebirth==undefined){//孕周预产期互推算法生日为必须条件
					//return;//不能返回，反悔后后面的量表页面的onchange就不执行了
				}else{
					var result=getYunZhou(birthday,prebirth);
					$("input[name='NConceiveWeek']").val(result.weeks);
					$("input[name='NConceiveDay']").val(result.days);
				}
				try{onTBirthdayChange();}catch(e){}
			}catch(e){}
		},
		validateOnCreate:false,
		editable:true
	});
	
}

//答题模式的切换事件
function switchAnswerModel(){
	// 获取当前显示的答题模式并给导航增加current
	var CFormIdVal = $("#basic").find("input[name='CFormId']").val().slice("1") - 1;
	$("div.questionnaire-box").find("li").eq(CFormIdVal).addClass("current");
	// 答题模式切换
	$("div.questionnaire-box ul li").click(function() {
		var currentFormId = $("div.questionnaire-right").find("li.current").eq(0).attr("formId");// 当前formId
		var destFormId = $(this).attr("formId");// 目标formId
		var formData = $("#" + currentFormId).serializeJson();// 当前formId对应的数据
		$("#basic").find("[name='CFormId']").val(destFormId);
		showData(destFormId, formData);// 加载目标form的表单选中样式

		if (!$(this).hasClass("not-click")) {
			$(this).addClass("current").siblings().removeClass("current");
			$("div.subject").eq($(this).index()).show().siblings("div.subject").hide();
		}

	});
}

//初始化tooltips 录入人、申请医生、与儿童关系
function initTooltips(){
	//测试者列表 的icon 鼠标进入事件
	$("#executortips").tooltip({
		content: $("<div class='ui-tip-content'></div>"),
		showEvent: 'mouseover',//指定显示事件
		onUpdate: function(content) {
			var str=getTipContent(0);
			content.panel({
				width: 200,
				border: false,
				content:str
			});
		},
		onShow : function() {
			//显示的时候进行事件的绑定
			var t = $(this);
			t.tooltip('tip').unbind().bind('mouseenter', function() {
				t.tooltip('show');
			}).bind('mouseleave', function() {
				t.tooltip('hide');
			}).bind('click', function() {
				t.tooltip('hide');
			});
		}
	});
	
	
	//儿童关系备选面板
	$("#reltips").tooltip({
		content: $("<div class='ui-tip-content'></div>"),
		showEvent: 'mouseover',//指定显示事件
		onUpdate: function(content) {
			var str=getTipContent(4);
			content.panel({
				width: 200,
				border: false,
				content:str
			});
		},
		onShow : function() {
			//显示的时候进行事件的绑定
			var t = $(this);
			t.tooltip('tip').unbind().bind('mouseenter', function() {
				t.tooltip('show');
			}).bind('mouseleave', function() {
				t.tooltip('hide');
			}).bind('click', function() {
				t.tooltip('hide');
			});
		}
	});
	
	//申请医生提示框鼠标滑过事件
	$("#doctortips").tooltip({
		content: $("<div class='ui-tip-content'></div>"),
		showEvent: 'mouseover',//指定显示事件
		onUpdate: function(content) {
			var str=getTipContent(1);
			content.panel({
				width: 200,
				border: false,
				content:str
			});
		},
		onShow : function() {
			//显示的时候进行事件的绑定
			var t = $(this);
			t.tooltip('tip').unbind().bind('mouseenter', function() {
				t.tooltip('show');
			}).bind('mouseleave', function() {
				t.tooltip('hide');
			}).bind('click', function() {
				t.tooltip('hide');
			});
		}
	});
	
}
//初始化右侧的内容，如量表介绍、指导语等
function initRight(){
	//右侧的模式、使用年龄、指导语、介绍在initData的 getLbBasInfo 的回调中。因为模式和适用年龄的长度是动态的，必须赋值后再确定高度，否则会出现竖向滚动条
	
	//获取扫码排队的的高度
	var rightBoxHeight = $(".questionnaire-right").height();
	
	$(".ui-scan").find("ol").css("height",rightBoxHeight-57);
	
	//判断题目内容是否非空
	$(".subject").each(function(i, ele) {
		if ($(ele).html() == "") {
			$("div.questionnaire-box ul li").eq(i).addClass("not-click");
		}
	});
	
	var currentPath = location.href;
	//var lbId = currentPath.substring(currentPath.lastIndexOf("/") + 1,currentPath.lastIndexOf("."));
	var lbId = $("input[name='CLbId']").val();//currentPath.substring(currentPath.lastIndexOf("/") + 1,currentPath.lastIndexOf("."));
	
	// 量表介绍和指导语 的鼠标点击显示更多的事件
	$("#introduce_").find("em").on("click",function(){
		var opt = {
				msg:"<div style='padding:10px'>"+lbMsg_[lbId].introduce+"</div>",
				width:'400px',
				height:'auto',
				title:'量表介绍',
				maximizable:false,
				onClose:function(){
					
					$('#popWin_').html("");
				}
			};
		utils.popWin(opt);
		
	});
	$("#guide_").find("em").on("click",function(){
		var opt = {
				msg:"<div style='padding:10px'>"+lbMsg_[lbId].guide+"</div>",
				width:'400px',
				height:'auto',
				title:'指导语',
				maximizable:false,
				onClose:function(){
					$('#popWin_').html("");
				}
			};
		utils.popWin(opt);
	});
	
//	扫码排队 全部、排号中、已测评切换
//	待添加后台功能   扫码
	$(".questionnaire-box span").click(function(){
		$(this).addClass("current").siblings().removeClass("current");
		if($(this).index()==0){
			$("#childInfoList").find("li").show();
		}
		if($(this).index()==1){
			$("#childInfoList").find("li").show();
			$("#childInfoList").find(".used").hide();
		}
		if($(this).index()==2){
			$("#childInfoList").find("li").hide();
			$("#childInfoList").find(".used").show();
		}
	});
	
	
	$("#noQueue").on("click",function(){
		if(top.location!=self.location){
			parent.utils.addTab({
				url:"/jsp/scan/print.jsp",
				title:"二维码"
			});
		}else{
			window.open("/jsp/scan/print.jsp");
		}
	});
}

// 初始化顶部转诊通知
function initRfApplyCount() {

	utils.ajax({
		service : "referralAction",
		method : "refreshRfApplyCount",
		params : {},
		validate : false,
		sucFun : function(mrk, bd, cd) {
//			alert("demo");
//			debugger;
//			if (mrk) {
//				if (cd.hasParent == true) {
//					$("input[name='CParentId']").val(cd.parentInfo.CParentId);
//				}else{
//					//新查询的手机号码没有孩子的时候,需要清理掉上一个有孩子的手机号码带出来的ParentId。否则将会把孩子保存到上一个手机号码的下面去。
//					$("input[name='CParentId']").val("");
//				}
//				if (cd.hasChildren == true) {
//					var lis = "";
//					for ( var index in cd.childrenInfo) {
//						var CChildId = cd.childrenInfo[index].CChildId;
//						var CChildNme = cd.childrenInfo[index].CChildNme;
//						var CChildSex = cd.childrenInfo[index].CChildSex;
//						var TBirthday = cd.childrenInfo[index].TBirthday;
//						//在这里将生日填入，因此也是在这里做时间校验以及时间改变后的时候
//						if (CChildSex == "M") {
//							CChildSex = "男";
//						} else {
//							CChildSex = "女";
//						}
//						var li = "<li onclick=\"chooseChild('" + CChildId + "')\">"+
//									"<div class='child-name'>"+CChildNme+"</div>"+
//									"<div class='child-sex'>"+CChildSex+"</div>"+
//									"<div class='birthday'>"+TBirthday+"</div>"+
//								 "</li>";
//						lis = lis + li;
//					}
//					var tips = "<h5>您是否需要输入：</h5>"
//					$("#phoneDown").find("ul").html("").append(tips).append(lis);
//					$("#phoneDown").show();
//				}
//			}
		}
	});
}

//禁用页面的验证，避免初始化进入页面时 控件验证不通过变成红色，业务需要自己打开校验（utils.ajax方法当validate为true时，会自动打开校验）
function disableValidation(){
	$("#basic").form("disableValidation");
	$("#f1").form("disableValidation");
	$("#f2").form("disableValidation");
	$("#f3").form("disableValidation");
}


//初始化页面场景（主要是按钮的显示隐藏，页面是否可编辑等）
//model可选值如下：
//print 打印场景
//view 查看场景
//new 新增测评
//edit 修改答题
//modify 修改报告
function initScene(){
// 获取答卷的宽度
var width = $("div.questionnaire-left").width();
$("div.tit-prompt").width(width - 40);

//打印场景
if (utils.getParam("model") == "print") {
	// 如果是打印模式，需要设置该页面的父级页面的高度
//			parent.setHeight("timu", $(window.document).outerHeight() + 200);
	// 将不需要打印的隐藏起来
	$(".noprint").hide();
	// 禁用表单
	disableForms();
	hideBtn();
}
//view 查看场景
else if (utils.getParam("model") == "view") {
	// 禁用表单
	disableForms();
	hideBtn();
	$(".questionnaire-btn-box").css("width","150px");
}
	//有clickSelect_的class绑定选中事件
	$(".clickSelect_").on("click",function(){
		//注意需要触发一次change事件，否则errTips不会消失，因为是绑定的change校验必输的事件
		$(this).find("input[type='radio']").prop("checked",true).trigger("change");
		//处理checkbox
		var checkboxObj = $(this).find("input[type='checkbox']");
		if(!checkboxObj.is(':checked')){
			checkboxObj.trigger("click");
		}else{
			checkboxObj.trigger("click");
		}
	});

	$("#clearBasicInfo").on("click",function(){
		clearBasicInfo();
	});
	//如果为修改模式，则不仅会执行上面的事件，还将修改按钮标题
	if(utils.getParam("model")=="modify"){
		$("#saveBtn").remove();//修正报告场景，不允许保存
		$("#submitBtn").find("i").html("确认修改");
	}
}

//点击电话号码后面的×清除相关信息
function clearBasicInfo(){
	//隐藏字段清空
	$("input[name='CParentId']").eq(0).val("");//父母ID
	$("input[name='CChildId']").eq(0).val("");//孩子ID
	$("input[name='NYear']").eq(0).val("");//几年
	$("input[name='NMonth']").eq(0).val("");//几个月
	$("input[name='NDay']").eq(0).val("");//几天
	//4个基本必填字段的输入框需要清空
	$("input[name='CPhoneNo']").eq(0).attr("readonly", false);//只读属性为假
	$("input[name='CPhoneNo']").val("");
	$("input[name='CPhoneNo']").eq(0).on("blur",function(){getBasicByPhoneNo();});//绑定事件
	$("input[name='CChildNme']").eq(0).attr("readonly", false);//只读属性为假
	$("input[name='CChildNme']").val("");
	$("#CChildSex").combobox({"readonly" : false});//只读属性为假
	$("#CChildSex").combobox('setValue', "");//内容清空
	$("#TBirthday").textbox({"readonly" : false});
	$("#TBirthday").datebox('setValue', "");
	$("input[name='CIllHistory']").attr("ills","")
	$("#illTips").hide();
	$("#arch").hide();
}

// 禁用表单
function disableForms() {
	$("form").each(function(i, ele) {
		var formId = $(ele).attr("id")
		utils.disableForm(formId);
	});
	$(".easyui-datebox").datebox({"disabled":true});
	$(".easyui-validatebox").validatebox({"disabled":true});
}

// 隐藏保存和提交按钮
function hideBtn() {
	$("#saveBtn").parent().hide();//保存答题按钮
	$("#submitBtn").parent().hide();//生成报告按钮
	$("#executortips").hide();//录入人tips
	$("#doctortips").hide();//申请医生tips
	$("#hospitaltips").hide();//1701 的复查机构tips
}


// 通过手机号码获取孩子信息，用户在输入手机号码后，在CPhoneNo控件的onblur事件，该事件是动态绑定的
function getBasicByPhoneNo() {
	$("#diseaseDown").hide();
	var phoneNo = $("input[name='CPhoneNo']").eq(0).val();
	if(phoneNo.trim()==""){
		return false;
	}
	utils.ajax({
		control : "arch/getBasicByPhoneNo",
		params : {
			"phoneNo" : phoneNo
		},
		validate : false,
		sucFun : function(mrk, bd, cd) {
			phoneQueryData_ = cd;
			if (mrk) {
				if (cd.hasParent == true) {
					$("input[name='CParentId']").val(cd.parentInfo.CParentId);
				}else{
					//新查询的手机号码没有孩子的时候,需要清理掉上一个有孩子的手机号码带出来的ParentId。否则将会把孩子保存到上一个手机号码的下面去。
					$("input[name='CParentId']").val("");
				}
				if (cd.hasChildren == true) {
					var divLists = "";
					for ( var index in cd.childrenInfo) {
						var CChildId = cd.childrenInfo[index].CChildId;
						var CChildNme = cd.childrenInfo[index].CChildNme;
						var CChildSex = cd.childrenInfo[index].CChildSex;
						var TBirthday = cd.childrenInfo[index].TBirthday;
						//在这里将生日填入，因此也是在这里做时间校验以及时间改变后的时候
						if (CChildSex == "M") {
							CChildSex = "男";
						} else {
							CChildSex = "女";
						}
						var divList = "<div class='drop-down-list' onclick=\"chooseChild('" + CChildId + "')\">"+
									"<div class='child-name'>"+CChildNme+"</div>"+
									"<div class='child-sex'>"+CChildSex+"</div>"+
									"<div class='birthday'>"+TBirthday+"</div>"+
								 "</div>";
						divLists = divLists + divList;
					}
					var tips = "<h5>您是否需要输入：</h5>"
					$("#phoneDown").find(".drop-down-div").html("").append(tips).append(divLists);
					$("#phoneDown").show();
				}
			}
		}
	});
}

// getBasicByPhoneNo 查询到有孩子数据库，弹出框，选择孩子的时候使用chooseChild
function chooseChild(childId) {
	var childrenInfo = phoneQueryData_.childrenInfo;
	for ( var index in childrenInfo) {
		if (childrenInfo[index].CChildId == childId) {//debugger;
			var childNme = childrenInfo[index].CChildNme;
			var childSex = childrenInfo[index].CChildSex;
			var birthday = childrenInfo[index].TBirthday;
			var illList = childrenInfo[index].IllList;
			//verson 1:var sysdate = $("#sysdate").val();	//获取系统时间
			var sysdate = utils.SYS_DATE;
			var result = calcAge(new Date(birthday), new Date(sysdate));//获取年龄
			//选择孩子的时候开始校验数据库里面已经有的孩子是否符合当前报告的测评区间
			$("input[name='CChildNme']").val(childNme);
			$("#CChildSex").combobox('setValue', childSex);
			$("#TBirthday").datebox('setValue', birthday);
			
			$("input[name='NYear']").val(result.YEAR).trigger("change");
			$("input[name='NMonth']").val(result.MONTH);
			$("input[name='NDay']").val(result.DAY);
			
			$("#TPreBirth").datebox('setValue', childrenInfo[index].TPreBirth).trigger("change");
			$("input[name='NBirthHeight']").val( childrenInfo[index].NBirthHeight).trigger("change");
			$("input[name='NBirthWeight']").val( childrenInfo[index].NBirthWeight).trigger("change");
			$("input[name='NBirthChest']").val( childrenInfo[index].NBirthChest).trigger("change");
			$("input[name='NBirthHead']").val( childrenInfo[index].NBirthHead).trigger("change");
			$("input[name='NBirthSheight']").val( childrenInfo[index].NBirthSheight).trigger("change");
			$("input[name='NConceiveWeek']").val( childrenInfo[index].NConceiveWeek).trigger("change");
			$("input[name='NConceiveDay']").val( childrenInfo[index].NConceiveDay).trigger("change");
			
			//触发一次验证事件，否则红色不消失，但是不确定是否所有的量表孕周都是validatebox类型，所以用异常捕获不处理
			try{	$("input[name='NBirthHeight']").validatebox("validate");	}catch(e){}
			try{	$("input[name='NBirthWeight']").validatebox("validate");	}catch(e){}
			try{	$("input[name='NBirthChest']").validatebox("validate");		}catch(e){}
			try{	$("input[name='NBirthHead']").validatebox("validate");		}catch(e){}
			try{	$("input[name='NBirthSheight']").validatebox("validate");	}catch(e){}
			try{	$("input[name='NConceiveWeek']").validatebox("validate");	}catch(e){}
			try{	$("input[name='NConceiveDay']").validatebox("validate");	}catch(e){}
			
			//如果档案id不为空，则显示档案的图标，并且绑定档案图标的click事件
			var archBasId = childrenInfo[index].CArchBasId;

			//不管档案id是否为空，都要去后台判定是否有数据，因为该childId在其他医院可能有记录,默认将按钮隐藏，若后台查询到有数据，则在方法内显示该按钮
			//debugger;
			//$("#arch").on("click",getChildDataPreview(childId,phoneQueryData_.parentInfo.CParentId,archBasId));
			$("#arch").hide();
			

			$("input[name='CChildId']").val(childId);
			//孩子id发生变化时触发change事件（在0104的时候，如果孩子id发生变化，需要判断有没有上次骨期数据，有没有上次x光片信息）
			try{$("input[name='CChildId']").trigger("change");}catch(e){alert("error");}
			$("input[name='CIllHistory']").attr("ills", illList);
			// 该孩子有历史疾病，则icon需要显示
			if (illList != null && illList != "") {
				$("#illTips").show();
			}
			disableBasicInfo();
			
			// 历史疾病icon的点击事件
			$("#illTips").tooltip({
				content : function() {
					var ills = $("input[name='CIllHistory']").attr("ills").split("$~$");
					var spans = "";
					for (var i = 0; i < ills.length; i++) {
						if (ills[i].trim() == "")
							continue;
						var li = "<li onclick=\"chooseIll(this)\">" + ills[i] + "</li>";
						spans = spans + li;
					}
					return "<ul>" + spans + "</ul>";
				},
				showEvent : 'mouseover',
				onShow : function() {
					var t = $(this);
					t.tooltip('tip').unbind().bind('mouseenter', function() {
						t.tooltip('show');
					}).bind('mouseleave', function() {
						t.tooltip('hide');
					});
				}
			});
			break;
		}
	}
	$('#phoneDown').hide();
	phoneQueryData_ = {
		"hasParent" : false,
		"hasChildren" : false
	};
}

function chooseIll(obj) {
	var val = $("input[name='CIllHistory']").val();
	if (val.indexOf($(obj).html()) == -1) {
		$("input[name='CIllHistory']").val(val + "  " + $(obj).html());
	}
}


function dropDownClose() {
	$("div.drop-down-box").hide();
}

function disableBasicInfo() {
	var parentId = $("input[name='CParentId']").eq(0).val();
	var childId = $("input[name='CChildId']").eq(0).val();
	if (parentId != null && parentId != undefined && parentId != '') {
		$("input[name='CPhoneNo']").eq(0).attr("readonly", "readonly");
		$("input[name='CPhoneNo']").eq(0).unbind("blur");
	}
	if (childId != null && childId != undefined && childId != '') {
	
		$("input[name='CChildNme']").eq(0).attr("readonly", "readonly");
		$("#CChildSex").combobox("readonly");
		$("#TBirthday").textbox({
			"readonly" : true
		});
	}
}

function showForm1Data(data) {
	$("#f1").form("load", data);
}

function showForm2Data(data) {
	$("#f2").form("load", data);
	var formId = "f2";
	for ( var key in data) {
		var value = data[key];
		
		// 如果是数组，那么回填的应该是checkbox
		if (value instanceof Array) {
			// 先将所有的name=key的current的class移除掉
			$("#" + formId).find("[name='" + key + "']").each(function(i, ele) {
				$(ele).parents("dd").removeClass("current");
			});
			for (var i = 0; i < value.length; i++) {
				var element = $("#" + formId).find("[name='" + key + "'][value='" + value[i] + "']").eq(0);
				//有的情况下checkbox是显示的(1702的Vojita姿势反射)，那么久不要加current的class了 20161106 Administrator
				if(element.is(":visible")==false){
					element.parents("dd").addClass("current");
				}
						
			}
		}
		// 回填的是radio
		else {
			// 若值为空 则不加current了 20161106 Administrator
			if(value!=""){
				// 先将所有的name=key的current的class移除掉
				$("#" + formId).find("[name='" + key + "']").each(function(i, ele) {
					$(ele).parents("dd").removeClass("current");
				});
				// 符合条件的li在class属性上加上class
				$("#" + formId).find("[name='" + key + "'][value='" + value + "']")
				.eq(0).parents("dd").addClass("current");
			}
		}
	}
};
function showForm3Data(data) {
	$("#f3").form("load", data);
	var formId = "f3";
	for ( var key in data) {
		var value = data[key];
		// 如果是数组，那么回填的应该是checkbox
		if (value instanceof Array) {
			// 先将所有的name=key的current的class移除掉
			$("#" + formId).find("[name='" + key + "']").each(function(i, ele) {
				$(ele).parents("label").removeClass("current");
			});
			for (var i = 0; i < value.length; i++) {
				$("#" + formId).find(
						"[name='" + key + "'][value='" + value[i] + "']").eq(0)
						.parents("label").addClass("current");
			}
		}
		// 回填的是radio
		else {
			// 先将所有的name=key的current的class移除掉
			$("#" + formId).find("[name='" + key + "']").each(function(i, ele) {
				$(ele).parents("label").removeClass("current");
			});
			// 符合条件的li在class属性上加上class
			$("#" + formId).find("[name='" + key + "'][value='" + value + "']")
					.eq(0).parents("label").addClass("current");
		}
	}
}
// 显示数据
function showData(formId, data,extraData) {
	if ("f1" == formId) {
		try {
			showF1Data(data,extraData);
			
		} catch (e) {
			showForm1Data(data,extraData);
			$("input[name='TBirthWeight']").val(data.TBirthWeight);
			$("input[name='TBirthHeight']").val(data.TBirthWeight);
			$("input[name='TBirthHead']").val(data.TBirthWeight);
			$("input[name='TBirthWeight']").val(data.TBirthWeight);
		}
	} else if ("f2" == formId) {
		try {
			showF2Data(data,extraData);
		} catch (e) {
			showForm2Data(data,extraData);
		}
	} else if ("f3" == formId) {
		try {
			showF3Data(data,extraData);
		} catch (e) {
			showForm3Data(data,extraData);
		}
	}
}

// 时间校验
function calcAge(birthday, referDate) {
	var d1 = referDate;
	var d2 = birthday;
	var year = d1.getFullYear() - d2.getFullYear();
	var month = d1.getMonth() + 1 - d2.getMonth() - 1;
	var day = d1.getDate() - d2.getDate();
	if (day < 0) {
		// 如果日期不够减，说明没达到完整月份数，需要将月份数减1
		month = month - 1;
		// 如果月份数为负数（出生第二天就是另一个月开始的情况），则将月份数置为0
		if (month < 0) {
			month = month + 12;
			year = year - 1;
		}
		// 获取当前月份的前一个月(月份是从0开始计数的）
		var preMonth = (d1.getMonth()+1) - 1;
		// 如果当前是1月，前一个月应该是12月份
		if (preMonth <= 0)
			preMonth = 12;
		// 1,3,5,7,8,10,12月份是31天，应该用31减去c2对应的天数再加上c1的天数
		if (preMonth == 1 || preMonth == 3 || preMonth == 5 || preMonth == 7
				|| preMonth == 8 || preMonth == 10 || preMonth == 12) {
			day = 31 - d2.getDate() + d1.getDate();
		}
		if (preMonth == 4 || preMonth == 6 || preMonth == 9 || preMonth == 11) {
			day = 30 - d2.getDate() + d1.getDate();
		}
		if (preMonth == 2) {
			day = 28 - d2.getDate() + d1.getDate();
			var c1Year = d1.getFullYear();
			// 如果是闰年还需要加一天
			if (((c1Year % 100 == 0) && (c1Year % 400 == 0))
					|| ((c1Year % 100 != 0) && (c1Year % 4 == 0))) {
				day = day + 1;
			}
		}
	}
	
	//借位后可能日期计算出来还是负数，如：生日2017-08-31，当前日期2018-03-02
	if (day < 0) {
		// 如果日期不够减，说明没达到完整月份数，需要将月份数减1
		month = month - 1;
		// 如果月份数为负数（出生第二天就是另一个月开始的情况），则将月份数置为0
		if (month < 0) {
			month = month + 12;
			year = year - 1;
		}
		// 获取当前月份的前两个月(月份是从0开始计数的）
		var preMonth = (d1.getMonth()+1) - 2;
		// 如果当前是1月，前一个月应该是12月份
		if (preMonth <= 0)
			preMonth = 12;
		// 1,3,5,7,8,10,12月份是31天，应该用31减去c2对应的天数再加上c1的天数
		if (preMonth == 1 || preMonth == 3 || preMonth == 5 || preMonth == 7
				|| preMonth == 8 || preMonth == 10 || preMonth == 12) {
			day = 31 + day;
		}
		if (preMonth == 4 || preMonth == 6 || preMonth == 9 || preMonth == 11) {
			day = 30 + day;
		}
		if (preMonth == 2) {
			day = 28 + day;
			var c1Year = d1.getFullYear();
			// 如果是闰年还需要加一天
			if (((c1Year % 100 == 0) && (c1Year % 400 == 0))
					|| ((c1Year % 100 != 0) && (c1Year % 4 == 0))) {
				day = day + 1;
			}
		}
	}
	
	if (month < 0) {
		year = year - 1;
		month = month + 12;
	}
	// 字符串拼接,
	// var result = "{'Year':" + (year > 0 ? year : 0) + ",'month':" + (month >
	// 0 ? month : 0) + ",'day':" + day + "}";
	// 字符串到json对象
	var result = {
		"YEAR" : (year > 0 ? year : 0),
		"MONTH" : (month > 0 ? month : 0),
		"DAY" : day
	};
	return result;
}
// 检测是否落在区间内
function isInRange(sex, age) {
	var lbid = $("input[name='CLbId']").val();
	var range = lbMsg_[lbid].range;
	if (range == undefined) {// 没有区间说明，认为没有区间限制，即任意年龄段任意性别都可进行测评
		return true;// 直接返回真,表示不检测区间，没有年龄限制
	}
	var mrange = lbMsg_[lbid].range.M;
	var frange = lbMsg_[lbid].range.F;
	var arange = lbMsg_[lbid].range.A;
//	var therange = new Array();// 存储区间的数组
	var therange;
	var monthage=age.YEAR*12+age.MONTH;//月龄,只算满月龄
	var dayage=age.YEAR*12*30+age.MONTH*30+age.DAY;//天龄;估算孩子的天数
	if (sex == "M") {
		if (mrange != "") {
			therange = mrange.split("-");
		} else if (arange != "") {
			therange = arange.split("-");
		} else {
			return false;
		}
	}
	if (sex == "F") {
		if (frange != "") {
			therange = frange.split("-");
		} else if (arange != "") {
			therange = arange.split("-");
		} else {
			return false;
		}
	}
	var value=undefined;
	if(therange[0].charAt(0)=="M"){//按月龄测评
		value=monthage;
	}
	else if(therange[0].charAt(0)=="D"){//按天龄测评
		value=dayage;
	}
	else if(therange[0].charAt(0)=="T"){//按特殊区间进行处理
		var thearray1=therange[1].split("#");
		var thearray2=therange[2].split("#");
		therange[1]=parseInt(thearray1[0]*30)+parseInt(thearray1[1]);
		therange[2]=parseInt(thearray2[0]*30)+parseInt(thearray2[1]);
		value=dayage;
	}
	if (therange[1] <= value && value <= therange[2])
		return true;
	return false;
};
// 你可以选择的日期不能比系统时间更久远
function dateValidate(sysdate, yourdate) {
	 if(yourdate.getTime()-sysdate.getTime()>0){
		 return false;
	 }
	 return true;
}
//数据回调
function backfill(obj,type) {
	//var str=$(obj).find("span[class='l-btn-text']").html();
	var str=$(obj).html();
	if(type==0){//0是测试者
		$("input[name='CExecutor']").val(str).validatebox("validate");
	}
	else if(type==1){//1是医生
		$("input[name='CDoctor']").val(str).validatebox("validate");
	}else if(type==2){//2是医院
		$("input[name='CAns6']").val(str).validatebox("validate");
	}else if(type==4){
		$("input[name='CChildRel']").val(str).trigger("change");
		if(str=="其他")
			$("input[name='CChildRel']").attr("readonly",false);
		else 
			$("input[name='CChildRel']").attr("readonly", "readonly");
	}
}
function getTipContent(type){//0是测试者,1是医生,2是医院
	var titleArray={
			"1":'请选择申请医生', 
			"0":'请选择录入人',
			"4":'请选择儿童关系'
	};
	var tiptype=["填表人","申请医生","医疗机构"];
	var h4="<h4>"+titleArray[type]+"</h4>"
	if(type==4){
		var content='<li onclick="backfill(this,'+type+')"  >父母</li>';
		content+='<li onclick="backfill(this,'+type+')" >祖父母</li>';
		content+='<li onclick="backfill(this,'+type+')" >师生</li>';
		content+='<li onclick="backfill(this,'+type+')" >其他</li>';
		return '<ul>'+h4+content+"</ul>";
	}
	var body=(type==0)?executors_:doctors_;
	var content='<li onclick="backfill(this,'+type+')" >';
	if(body=="" ||body==undefined||body==null){//内容等于空,提示用户前往编辑页面
		content='<li onclick="gotoPcEdit('+type+')" class="ui-add"><i>没有'+tiptype[type]+'，</i>点击【添加】</li>';
		return '<ul>'+content+'</ul>';
	}
	var arr=body.split("\n");
	var result="";
	if(arr instanceof Array){//判断Array是否为arr的实例
		for(var i=0;i<arr.length;i++){
			result=result+content+arr[i]+'</li>';
		}
	}
	else{
		result=result+content+body+'</li>';
	}
	return '<ul>'+h4+result+'<li onclick="gotoPcEdit('+type+')" class="ui-add">【新增】</li>'+"</ul>";
}


//使用tab页的方式打开信息维护，上级调用是当填表人或者申请医生、听力筛查的医院为空的时候调用
function gotoPcEdit(type){
	var param={"0":"executor","1":"doctor","2":"hospital"};
	var title = "新增";
	if("0"==type){
		title = title+"录入人";
	}else if("1"==type){
		title = title+"申请医生";
	}else if("2"==type){
		title = title+"听力复查机构"
	}else{
		alert("未知参数，请联系IT");
	}
	var opt={
			url:'./jsp/bas/basPcEdit.jsp?from='+param[type],
			width:'50%',
			height:'380px',
			title:title,
			maximizable:false,
			onClose:function(){
				utils.ajax({//更新提示信息
					service : "pcAction",
					method : "getLbBasInfo",
					params:{lbId:$("input[name='CLbId']").val()},
					validate : false,
					sucFun : function(mrk, bd, cd) {
						doctors_=cd.tipsinfo.doctors_;
						executors_=cd.tipsinfo.executors_;
						hospitals_=cd.tipsinfo.hospitals_;
						if(type==2){
							refreshTooltips();
						}
						initTooltips();//初始化Tooltips
					}
				});
			}};
	utils.popWin(opt);
};


var growthStdData={
	"head":{data:[30,60],unit:"cm",title:"0-6岁头围"},//0-6岁头围 cm
	"chest":{data:[29,63],unit:"cm",title:"0-6岁胸围"},//0-6岁胸围 cm
	"sHeight":{data:[30,71],unit:"cm",title:"0-6岁坐高"},//0-6岁 坐高 cm
	"height":{data:[20,180],unit:"cm",title:"身高"},//身高 cm
	"weight":{data:[0.3,50],unit:"kg",title:"体重"},//体重 kg 0.3kg-50kg
	"week":{data:[20,50],unit:"周",title:"孕周"}//孕周  20-50周
};

//验证头围、胸围、坐高、身高、体重是否在常规范围之内
//data格式
//{"head":34} 头围一个值 
//{"head":[23,43]} 头围两个值（场景：出生时头围和出院时头围）
function validateGrowthData(datas){
	var msg = "";
	for(var key in datas){
		var data = datas[key];
		var std = growthStdData[key].data;
		if(data instanceof Array){
			for(var i in data){
				//如果不是数字，则继续下一个。可能是空
				if(data[i]==undefined || data[i]==null || data[i].trim=="" || isNaN(data[i])){
					continue;
				}
				var tmp = parseFloat(data[i]);
				if(tmp<std[0] || tmp>std[1]){
					var x = growthStdData[key].title+":"+tmp+growthStdData[key].unit;
					msg = (msg==""?x: msg+"\n"+x);
				}
			}
		}else{
			if(data==undefined || data==null || data.trim=="" || isNaN(data)){
				continue;
			}
			var tmp = parseFloat(data);
			if(tmp<std[0] || tmp>std[1]){
				var x = growthStdData[key].title+":"+tmp+growthStdData[key].unit;
				msg = (msg==""?x: msg+"\n"+x);
			}
		}
	}
	if(msg!=""){
		msg = "您填写的如下内容不在常规范围，请确认!"+"\n"+msg;
		
		//因为easyui的confirm不是阻断式的，所以只能用原生的confirm。（虽然easyui有成功的回调，但是如果将common.js中的save方法的ajax部分写入此处，明显对业务框架冲击太大，故使用原生confirm
//		$.messager.confirm('信息提示', msg, function(r){
//            if (r){
//               return true;
//            }else{
//            	return true;
//            }
//        });
		if(window.confirm(msg)){
			return true;
		}else{
			return false;
		}
	}
	return true;
};
//获取孕周(Date 生日,Date ycq)
function getYunZhou(birthday,ycq){
	var t1=new Date(birthday).getTime();
	var t2=new Date(ycq).getTime();
	
	// 出生日期-（预产期-40周）
	var gestationalweeks=t1-(t2-40*7*24*60*60*1000);
	var weeks=parseInt(gestationalweeks/(60*60*1000*24*7));
	var days=(gestationalweeks%(60*60*1000*24*7))/(60*60*1000*24);
	days=Math.round(days);
	var yunzhou={
			weeks:weeks,
			days:days
	};
	return yunzhou;
};
//获取预产期(Date 生日,yunzhou)[目前只考虑到孕周的情况]
function getYCQ(birthday,weeks,days){
	var t1=new Date(birthday).getTime();
	var t2=weeks*7*24*60*60*1000;
	var t3=days*24*60*60*1000;
	// 出生日期-(孕周+孕天）+ 固定的40周
	var ycq=t1-(t2+t3)+7*24*60*60*1000*40;
	var result=new Date(ycq);
	var str=result.getFullYear()+"-"+(result.getMonth()+1)+"-"+result.getDate();
	return str;
}

//报表页面 -右边选项卡切换
function paidu_switch(){
	if($(".ui-scan").is(":hidden")){
		$(".ui-scan").show();
		$(".paidui ").css("background","url(res/images/icon-pc.png)");
		
	}else{
		$(".questionnaire-box").show();
		$(".ui-scan").hide();
		$(".paidui ").css("background","url(res/images/icon-qrcode.png)");
	}
}

function showRightArea(){
	var rightDome = $(".questionnaire-right");
	$(".paidui").show(300);
	$(".show-more").css("background-image","url(res/images/showMoreR.png)");
	rightDome.animate({width:'250px'});
	$(".ui-scan").animate({width:'250px'});
	
	//重新计算底部按钮宽度，否则底部按钮块会顶住右侧信息
	setTimeout(function(){
		var divLeftWidth = $(".questionnaire-left").width();
		$(".ui-btn").css("width",divLeftWidth-17);//底部按钮宽度
		$(".tit-prompt").css('width',divLeftWidth-40);//部分量表底部黄色提示区域宽度
		try{
			initHeightWidth();
		}catch(e){}
	},401); 
}

function hideRightArea(){
	var rightDome = $(".questionnaire-right");
	$(".paidui").hide(300);
	$(".show-more").css("background-image","url(res/images/showMoreL.png)");
	rightDome.animate({width:'0'});
	$(".ui-scan").animate({width:'0'});
	
	//重新计算底部按钮宽度，否则底部按钮块会顶住右侧信息
	setTimeout(function(){
		var divLeftWidth = $(".questionnaire-left").width();
		$(".ui-btn").css("width",divLeftWidth-17);//底部按钮宽度
		$(".tit-prompt").css('width',divLeftWidth-40);//部分量表底部黄色提示区域宽度
		try{
			initHeightWidth();
		}catch(e){}
	},401); 
}
function showOrHideRightArea(type){
	
	if("hide"==type){
		hideRightArea();
		return;
	}
	if("show"==type){
		showRightArea();
		return;
	}
	
	var rightDome = $(".questionnaire-right");
	if(rightDome.width()>"245"&&rightDome.width()<"255"){
		hideRightArea();
		return;
	}else{
		showRightArea();
		return;
	}
}

function isNull(obj){
	if(obj==undefined || obj==null||obj=="")
		return true;
	return false;
}

var rtm1;
function initRtm(){
	//如果是17大类的，不管有没有排队数据，都显示排队页面
	var lbId = $("input[name='CLbId']").val();
	
	if(lbId == "" || lbId == undefined){
		// DO NOTHING
	}else if(lbId.startWith("17") && utils.getParam("model")=='new'){
		$(".ui-scan").show();
		$(".paidui ").css("background","url(res/images/icon-pc.png)");
	}
	
	var dptId=utils.CURRENT_DPT_ID;//机构主键
	var officeId=utils.CURRENT_OFFICE_ID;//部门ID
	try{
        rtm1 = new rtm();
        rtm1.listen("lbpc/paidui/"+dptId+"/"+officeId);
        var datas = null;;
        rtm1.websocket.onmessage=function(event){
//            console.log(event);
            try{
                var tmp = event.data;
                tmp = tmp.replace(new RegExp("\"","gm"),"\\\"");
                tmp = "\""+tmp+"\"";
                datas=JSON.parse(tmp);
                datas =JSON.parse(datas);
            }catch(e){
//                console.log(e);
                return;
            }

            var userNum=parseInt($("#list_all").html());
            var usedNum=parseInt($("#list_used").html());

            if(childHtml_==undefined || childHtml_==null ||childHtml_==""){
                childHtml_=$("#childInfoList");
                childHtml_.html("");
            }




            for(var index in datas){
                var data = datas[index];
                var key=data.key;

                //_eventType为RTM系统定义,值:1代表add 2代表update 3delete
                if(data._eventType=="1"){
                    var child=data.ChildInfo;
                    userNum=userNum+1;//当天用户扫码用户数+1
                    var e_item="";//单个孩子的li节点
                    if(data.IsUsed==true||data.IsUsed=="true"){
                        e_item="<li class='used' ";//使用过的li标签
                        usedNum=usedNum+1;
                    }else{
                        e_item="<li ";
                    }

                    e_item=e_item+" onclick='click_childList(this)' " +
                        " key='"+key+"'"+
                        " CChildId='"+child.CChildId+"'"+
                        " CParentId='"+child.CParentId+"'"+
                        " CBasId='"+child.CBasId+"'"+
                        " ><span>"+child.CChildNme+"</span><i>"+child.CPhoneNo+"</i></li>";

                    //dom节点进行累加
                    childHtml_.html(childHtml_.html()+e_item);
                    //更新数量
                    $("#list_all").html(userNum);
                    $("#list_ready").html(userNum-usedNum);
                    $("#list_used").html(usedNum);
                }else if(data._eventType=="2"){
                    if(data.data.IsUsed==true||data.data.IsUsed=="true"){
                        $("#childInfoList li[key='"+data.condition.key+"']").addClass("used");
                        var num1 =parseInt($('#list_used').html());
                        var num2 =parseInt($('#list_ready').html());
                        $('#list_used').html(num1+1);
                        $('#list_ready').html(num2-1);
                    }
                }
            }
            //当有数据时，显示排队信息
            if($(".ui-scan").is(":hidden") && utils.getParam("model")=='new'&& utils.getParam("from")!='workflat'){
                $(".ui-scan").show();
                $(".paidui ").css("background","url(res/images/icon-pc.png)");
            }
        }
	}catch (e) {
//		console.log(e);
    }

}
//初始化野狗云
// function initWilddog(){
// 	wilddog.initializeApp({
// 		authDomain: utils.WILDDOG_AUTH_DOMAIN,
// 		syncURL:utils.WILDDOG_SYNC_URL
// 	});
// 	var dptId=utils.CURRENT_DPT_ID;//机构主键
// 	var officeId=utils.CURRENT_OFFICE_ID;//部门ID
// 	ref_ = wilddog.sync().ref("/arch/"+dptId+"/"+officeId);
// 	var userNum=0,usedNum=0;
//
// 	ref_.orderByChild("TScanTm").on('child_added', function(data) {//当页面初始化和有新的孩子添加的时候会触发该事件,按扫码时间从大到小排序
// 		if(childHtml_==undefined || childHtml_==null ||childHtml_==""){
// 			childHtml_=$("#childInfoList");
// 			childHtml_.html("");
// 		}
// 		var key=data.key();
// 		var child=data.val();
// 		userNum=userNum+1;//当天用户扫码用户数+1
// 		var e_item="";//单个孩子的li节点
// 		if(child.IsUsed==true){
// 			e_item="<li class='used' ";//使用过的li标签
// 			usedNum=usedNum+1;
// 		}else{
// 			e_item="<li ";
// 		}
//
// 		//console.log(child);
// 		e_item=e_item+" onclick='click_childList(this)' " +
// 				" key='"+key+"'"+
// 				" CChildId='"+child.ChildInfo.CChildId+"'"+
// 				" CParentId='"+child.ChildInfo.CParentId+"'"+
// 				" CBasId='"+child.ChildInfo.CBasId+"'"+
// 				" ><span>"+child.ChildInfo.CChildNme+"</span><i>"+child.ChildInfo.CPhoneNo+"</i></li>";
// 		//dom节点进行累加
// 		childHtml_.html(childHtml_.html()+e_item);
// 		//更新数量
// 		$("#list_all").html(userNum);
// 		$("#list_ready").html(userNum-usedNum);
// 		$("#list_used").html(usedNum);
//
// 		//当有数据时，显示排队信息
// 		if($(".ui-scan").is(":hidden") && utils.getParam("model")=='new'&& utils.getParam("from")!='workflat'){
// 			$(".ui-scan").show();
// 			$(".paidui ").css("background","url(res/images/icon-pc.png)");
// 		}
// 	});
//
// 	ref_.orderByChild("TScanTm").on('child_changed',function(data){//通过扫码时间排序,当数据更新时(isUsed为true)时,会出发该事件
// 		var key=data.key();
// 		var child=data.val();
// 		var index=$("#childInfoList li[key='"+key+"']").index();
// 		if(child.IsUsed==true){
// 			$("#childInfoList li[key='"+key+"']").addClass("used");
// 		}else{
// 			$("#childInfoList li[key='"+key+"']").removeClass("used");
// 		}
// 		if(child.ChildInfo.CBasId!=""&&child.ChildInfo.CBasId!=null&&child.ChildInfo.CBasId!=undefined){
// 			$("#childInfoList li[key='"+key+"']").attr("CBasId",child.ChildInfo.CBasId);
// 		}else{
// 			$("#childInfoList li[key='"+key+"']").attr("CBasId","");
// 		}
//         // var num1 =parseInt($('#list_used').html());
//         // var num2 =parseInt($('#list_ready').html());
// 		usedNum++;
//         $('#list_used').html(usedNum);
//         $('#list_ready').html(userNum-usedNum);
// 	});
//
// 	//如果是17大类的，不管有没有排队数据，都显示排队页面
// 	var lbId = $("input[name='CLbId']").val();
// 	if(lbId.startWith("17")&& utils.getParam("model")=='new'){
// 		$(".ui-scan").show();
// 		$(".paidui ").css("background","url(res/images/icon-pc.png)");
// 	}
// }
//


//点击右侧扫码的面板数据，将选定的孩子数据带入到basic表单
function click_childList(obj){//排号列表项被单击事件
	var p=$(obj);
	
	utils.ajax({
		service:'archAction',
		method:'getChildInfoByPaidui',
		params:{
			key:p.hasClass("used")?"":p.attr("key"),
			basId:p.attr("cbasId"),
			parentId:p.attr("cparentid"),
			childId:p.attr("cchildid"),
		},
		sucFun:function(mrk,bd,cd){
			//先清空basic表单的相关数据
			clearBasicInfo();
			
			//选择孩子的时候开始校验数据库里面已经有的孩子是否符合当前报告的测评区间
			var sysdate = utils.SYS_DATE;
			var birthday= cd.TBirthday;
			var result = calcAge(new Date(birthday), new Date(sysdate));
			if (!isInRange(cd.CChildSex,result)) {//判断是否落在区间内
				cd['IsInRange']=false;
			}else{
				cd['IsInRange']=true;
			}
			
			//将点选的数据带入到basic
			setBasicInfoFromPanel(cd);
			//该Li已经被点击过了,则只带入列表中的信息（不更新右侧面板，也不用访问野狗云）
//			if(p.hasClass("used")){
//				return;
//			}
			
		}
	});
	//不管是否更新成功,被点击过的Li标签都要变灰
	updatePanel(p);
//	
//	//先清空basic表单的相关数据
//	clearBasicInfo();
//	//将点选的数据带入到basic
//	setBasicInfoFromPanel(p);
//	//该Li已经被点击过了,则只带入列表中的信息（不更新右侧面板，也不用访问野狗云）
//	if(p.hasClass("used")){
//		return;
//	}
//	//不管是否更新成功,被点击过的Li标签都要变灰
//	updatePanel(p);
//	
//	var key=p.attr("key");//获取到欲更新的key
	
	//通知野狗更新 该数据已经被使用
//	utils.ajax({
//		service : "archAction",
//		method : "updateChildUsed",
//		params :{"key":key,"CDptId":utils.CURRENT_DPT_ID,"COfficeId":utils.CURRENT_OFFICE_ID},
//		validate : false,
//		sucFun : function(mrk, bd, cd) {
//		}
//	});
};
//点击右侧面板时将数据带入到basic表单
function setBasicInfoFromPanel(p){
	//跟选择孩子面板不同的是:chooseChildRen中Panel里的孩子唯一不重复,而排号面板里的孩子是可重复的,用来区分他们的是Key
	
	//选择孩子的时候开始校验数据库里面已经有的孩子是否符合当前报告的测评区间
//	var IsInRange=p.attr("IsInRange");
//	var IsInRange=p["IsInRange"];
//	if (!IsInRange) {//判断是否落在区间内
//		//utils.alert("该儿童不符合测评适用年龄");//不符合测评区间，给出提示并返回
//		//return;
//		
//		var lbid = $("input[name='CLbId']").val();//当前量表ID
//		var range = lbMsg_[lbid].range;
//		if(range.AlertType=="alert"){//必须拦截的表
//			utils.alert("该儿童不符合测评适用年龄");//弹出提示
//			$('#TBirthday').datebox('setValue', '');//清空不符合年龄段的生日
//			return ;//返回
//		}else{//非必须拦截的表
//			$.messager.confirm('提示','该儿童年龄不在工具推荐年龄范围内,是否继续测试?',function(r){
//			    if (!r){
//			    	$('#TBirthday').datebox('setValue', '');//清空不符合年龄段的生日
//			    	return;
//			    } 
//			});
//		}
//	}
	
//	var key=p.attr("key");
//	var CChildId=p.attr("CChildId");
//	var CParentId=p.attr("CParentId");
//	var CBasId=p.attr("CBasId");
//	var CChildNme=p.attr("CChildNme");
//	var CChildSex=p.attr("CChildSex");
//	var CPhoneNo=p.attr("CPhoneNo");
//	var TBirthday=p.attr("TBirthday");
//	var illList=p.attr("illList");
//	var NYear=p.attr("NYear");
//	var NMonth=p.attr("NMonth");
//	var NDay=p.attr("NDay");
//	var TPreBirth=p.attr("TPreBirth");
//	var NBirthHeight=p.attr("NBirthHeight");
//	var NBirthWeight=p.attr("NBirthWeight");
//	var NBirthHead=p.attr("NBirthHead");
//	var NBirthChest=p.attr("NBirthChest");
//	var NBirthSheight=p.attr("NBirthSheight");
//	var NConceiveWeek=p.attr("NConceiveWeek");
//	var NConceiveDay=p.attr("NConceiveDay");
	
	var key=p["key"];
	var CChildId=p["CChildId"];
	var CParentId=p["CParentId"];
	var CBasId=p["CBasId"];
	var CChildNme=p["CChildNme"];
	var CChildSex=p["CChildSex"];
	var CPhoneNo=p["CPhoneNo"];
	var TBirthday=p["TBirthday"];
	var illList=p["illList"];
	var NYear=p["NYear"];
	var NMonth=p["NMonth"];
	var NDay=p["NDay"];
	var TPreBirth=p["TPreBirth"];
	var NBirthHeight=p["NBirthHeight"];
	var NBirthWeight=p["NBirthWeight"];
	var NBirthHead=p["NBirthHead"];
	var NBirthChest=p["NBirthChest"];
	var NBirthSheight=p["NBirthSheight"];
	var NConceiveWeek=p["NConceiveWeek"];
	var NConceiveDay=p["NConceiveDay"];
	
	$("input[name='CChildId']").val(CChildId);
	//孩子id发生变化时触发change事件（在0104的时候，如果孩子id发生变化，需要判断有没有上次骨期数据，有没有上次x光片信息）
	try{$("input[name='CChildId']").trigger("change");}catch(e){}
	$("input[name='CParentId']").val(CParentId);
	$("input[name='CPhoneNo']").val(CPhoneNo);
	$("input[name='CChildNme']").val(CChildNme);
	$("#CChildSex").combobox("setValue",CChildSex);
	$("#TBirthday").datebox("setValue",TBirthday);
	$("input[name='NYear']").val(NYear).trigger("change");
	$("input[name='NMonth']").val(NMonth);
	$("input[name='NDay']").val(NDay);
	
	$("#TPreBirth").datebox('setValue', TPreBirth).trigger("change");
	$("input[name='NBirthHeight']").val( NBirthHeight).trigger("change");
	$("input[name='NBirthWeight']").val( NBirthWeight).trigger("change");
	$("input[name='NBirthChest']").val( NBirthChest).trigger("change");
	$("input[name='NBirthHead']").val( NBirthHead).trigger("change");
	$("input[name='NBirthSheight']").val( NBirthSheight).trigger("change");
	$("input[name='NConceiveWeek']").val( NConceiveWeek).trigger("change");
	$("input[name='NConceiveDay']").val( NConceiveDay).trigger("change");
	
	//触发一次验证事件，否则红色不消失，但是不确定是否所有的量表孕周都是validatebox类型，所以用异常捕获不处理
	try{	$("input[name='NBirthHeight']").validatebox("validate");	}catch(e){}
	try{	$("input[name='NBirthWeight']").validatebox("validate");	}catch(e){}
	try{	$("input[name='NBirthChest']").validatebox("validate");		}catch(e){}
	try{	$("input[name='NBirthHead']").validatebox("validate");		}catch(e){}
	try{	$("input[name='NBirthSheight']").validatebox("validate");	}catch(e){}
	try{	$("input[name='NConceiveWeek']").validatebox("validate");	}catch(e){}
	try{	$("input[name='NConceiveDay']").validatebox("validate");	}catch(e){}

	//不管档案id是否为空，都要去后台判定是否有数据，因为该childId在其他医院可能有记录,默认将按钮隐藏，若后台查询到有数据，则在方法内显示该按钮
	//debugger;
	//$("#arch").on("click",getChildDataPreview(CChildId,CParentId,CBasId));
	$("#arch").hide();
	
	$("input[name='CIllHistory']").attr("ills", illList);
	// 该孩子有历史疾病，则icon需要显示
	if (illList != null && illList != "") {
		$("#illTips").show();
	}
	disableBasicInfo();
	
	// 历史疾病icon的点击事件
	$("#illTips").tooltip({
		content : function() {
			var ills = $("input[name='CIllHistory']").attr("ills").split("$~$");
			var spans = "";
			for (var i = 0; i < ills.length; i++) {
				if (ills[i].trim() == "")
					continue;
				var li = "<li onclick=\"chooseIll(this)\">" + ills[i] + "</li>";
				spans = spans + li;
			}
			return "<ul>" + spans + "</ul>";
		},
		showEvent : 'mouseover',
		onShow : function() {
			var t = $(this);
			t.tooltip('tip').unbind().bind('mouseenter', function() {
				t.tooltip('show');
			}).bind('mouseleave', function() {
				t.tooltip('hide');
			});
		}
	});
}
//点击右侧面板时，更新面板数据（如变灰，已使用和排号中的序号）
function updatePanel(p){
	//添加变灰样式
	p.addClass("used");
	//页面上的数量要进行更新
	// if(_wilddogOrRtm=="wilddog"){
     //    var userNum=$("#childInfoList").children().length;
     //    var usedNum=$("#childInfoList").children("li.used").length;
     //    $("#list_all").html(userNum);
     //    $("#list_used").html(usedNum);
     //    $("#list_ready").html(userNum-usedNum);
     //
    // }
}

var lastMouseMove_ ;
//心跳包，每隔39分50秒调用一次，前提是调用了该方法的页面在29分40秒内有至少一次鼠标移动事件
function heartBeat(){
	$(document).mousemove(function(){ 
		lastMouseMove_ = new Date();
		//console.log(lastMouseMove_.getTime()); 
	});
	setInterval(function(){
		var now = new Date();
		if((now.getTime()-lastMouseMove_.getTime())>(1000*60*30-1000*20)){
			return;
		}
		utils.ajax({
			service:"pcAssistAction",
			method:"heartBeat",
			sucFun:function(mrk,bd,cd){
//				console.log("...heartBeat success...");
				
			},
			errFun:function(XMLHttpRequest, textStatus, errorThrown){
//				console.log("...heartBeat error...");
//				console.log(XMLHttpRequest, textStatus, errorThrown);
			}
		});
	},1000*60*30-1000*10);
}
//绑定快捷键事件
$(function(){
	//给整个dom帮上键盘按下事件
	document.onkeydown=function(){
		var e = window.event;
		// Ctrl+Alt+Shift+Up
		if(e.altKey&&e.shiftKey&&e.ctrlKey&&e.keyCode == '38'){
			try{
				hotkey()
			}catch(e){
				try{
					hotKey()	
				}catch(e){
//					console.log(e)
				}
			}	
		}
	}
	
});
	