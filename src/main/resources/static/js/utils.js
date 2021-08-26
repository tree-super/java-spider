$(document).keydown(function(event){
	//360浏览器
	//屏蔽 Alt+ 方向键 → alert("不准你使用ALT+方向键前进或后退网页！");
	if((window.event.altKey)&&((window.event.keyCode==37)||(window.event.keyCode==39))){
        event.returnValue=false;
        return false;
    }
	//谷歌浏览器 删除键&是body触发 也会返回，所以也要屏蔽
	if(window.event.keyCode==8 && event.target.nodeName.toUpperCase()=='BODY'){
		event.returnValue=false;
		return false;
	}
}); 

//解决IE7/8不支持trim的问题
String.prototype.trim = function() {
	return this.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
}

String.prototype.endWith=function(s){
  if(s==null||s==""||this.length==0||s.length>this.length)
     return false;
  if(this.substring(this.length-s.length)==s)
     return true;
  else
     return false;
  return true;
 }

 String.prototype.startWith=function(s){
  if(s==null||s==""||this.length==0||s.length>this.length)
   return false;
  if(this.substr(0,s.length)==s)
     return true;
  else
     return false;
  return true;
 }
 

 
;(function($){
	// 当前日期
 	var today = new Date();
 
	var utils_ ={
		APP_NAME : "",//应用名称
		APP_PATH : "",//应用访问路径
		SYS_DATE : "",//页面初始化时服务器的时间
		CURRENT_DPT_ID : "",//当前登录人员的所在机构
		CURRENT_OFFICE_ID : "",//当前登录人员的所在科室
		
		// 当前年月日
		CURRENT_YEAR : today.getFullYear(),   //当前年
		CURRENT_MONTH : today.getMonth() + 1, //当前月
		CURRENT_DATE : today.getDate(),       //当前日
	};
	
	utils_.prototype={};
	utils_.replaceStr=function(str){
		str = str.replace(/%/g, "%25");
		str = str.replace(/&/g, "%26");
		str = str.replace(/\+/g, "%2B");
		str = str.replace(/\n/g, "<br/>");
		return str;
	}
	
	//缓存combobox的加载方法，供数据回填显示使用
	utils_.comboboxArray=[];
	
	//初始化combobox组件的下拉数据
	//opt数据格式：[{name:'',method:'',params:{}}]
	//method可选值：该参数可选，默认ApiControl（即与后台通讯需要权限），可选ServiceControl（即与后台通讯不需要权限）
	utils_.combobox=function(opt,method){
		var _method = "ApiControl";//默认是需要权限获取下拉框数据
		if(method){
			if(method!='ApiControl'&&_method!='ServiceControl'){
				alert("method可选值为ApiControl(需要权限)\ServiceControl(不需要权限)获取后台下拉框数据");
				return;
			}
			_method = method;
		}
		utils.comboboxArray = utils.comboboxArray.concat(opt);
		var param = {'bizData_':{},'custData_':{'combobox':opt}};
 		 $.ajax({
			url : "/"+_method,
			type : "post",
			dataType : "json",
			async:false,//只能用false同步模式，否则回填会出错，lbDoctorDetail.jsp
			data: {
				'ServiceName_':'comboboxAction',
				'ServiceMethod_':'getValue',
				'RequestJsonData_':encodeURIComponent(encodeURIComponent(JSON.stringify(param)))
			},//TODO:params的编码 以及form表单的序列化$("#form1").serialize()
			success : function(result) {
				var cd = result.CustJsonData_;
				for(var name in cd){
					$("[comboname="+name+"]").combobox('loadData',cd[name]);
//					$("#"+name).combobox('loadData',cd[name]);
				}

				if(opt.hasOwnProperty("sucFun")){
			 		opt.sucFun(result.ResponseStatus_, result.BizJsonData_, result.CustJsonData_);
			 	}
			},
			error:function(XMLHttpRequest, textStatus, errorThrown){
				if(opt.hasOwnProperty("errFun")){
			 		opt.errFun();
			 	}
			}
	     });		
	}
	
	
	//combobox的联动
	//name需要联动的combobox name属性，method请求后台的哪个方法，param rec的参数值
	utils_.relateCombobox=function(name,method,param){
		var oldValue = $("[comboname="+name+"]").combobox("getValue");//获取当前的值，也就是旧值
		$("[comboname="+name+"]").combobox('setValue','');
		var data = {};
		if(this.isJson(param)){
			data = param;
		}else{
			data = {'id':param};
		}
		if(param.val==null || param.val==undefined || param.val==""){
			return;
		}
		this.combobox([
			{name:name,method:method,params:data}
		]);
		
		//若联动回填的值里面有该旧值的翻译，则设置该combobox为旧值
		var datas = $("[comboname="+name+"]").combobox("getData");
		for(var i in datas){
			if(datas[i].v==oldValue){
				$("[comboname="+name+"]").combobox('setValue',oldValue);
			}
		}
		
	}
	//ajax提交数据
	//opt{async:true,forms:[],service:'',method:'',params:{},validate:true,sucFun:function(){},errFun:function(){}}
	utils_.ajax=function(opt){
		var validate=false;
	 	var service='';
	 	var method='';
	 	var params = {};
	 	var forms = [];
	 	var async=true;
	 	var control = "ApiControl";
	 	
	 	//可选ApiControl需要登录后才能访问，ServiceControl没有登录也可以访问
	 	if(opt.hasOwnProperty("control")){
	 		control=opt.control;
	 	}
	 	if(opt.hasOwnProperty("async")){
	 		async = opt.async;
	 	}
	 	if(opt.hasOwnProperty("validate")){
	 		validate = opt.validate;
	 	}
	 	if(opt.hasOwnProperty("forms")){
	 		forms = opt.forms;
	 	}else{
	 		//如果没有forms参数则，不需要校验
	 		validate = false;
	 	}
	 	if(opt.hasOwnProperty("params")){
	 		params = opt.params;
	 	}
	 	if(validate==true&&forms.length==0){
	 		alert('当validate为true时，forms参数必选且不能为空');
	 		return;
	 	}
	 	var validateResult = true;
	 	var bizData = [];
	 	//表单需要验证
	 	if(validate==true){
	 		//逐个验证表单
	 		for(var index in forms){
		 		validateResult = $("#"+forms[index]).form('enableValidation').form('validate') && validateResult;
		 	}
	 	}
	 	//需要验证
	 	if(validate==true && !validateResult){
	 		utils.alert('必填项不能为空');
	 		return false;
	 	}
	 	
	 	if(opt.hasOwnProperty("mask") && opt.mask==true){
	 		$("#doingMask").show();
	 	}
	 	
 		
 		for(var index in forms){
 			
 			var data =new Array();
 			var datagridMrk = false;//是否是datagrid的toolbar的id
 			var datagridId = "";
 			var formId = forms[index];
 			var formData = $("#"+formId).serializeJson();
 			//获取所有的datagrid
 			$(".easyui-datagrid").each(function(i,ele){
 				//获取对应的toolbar指定的id
 				var tbId = $(ele).datagrid('options').toolbar;
 				//如果当前的传进来的form的id和toolbar指定的id相同
 				if("#"+formId == tbId){
 					datagridMrk = true;
 					datagridId = $(ele).attr("id");
 				}
 			});
 			//设置分页参数
 			if(datagridMrk){
 				var pNum=1;
 				var pSize = 10;
 				try{
	 				pNum = $("#"+datagridId).datagrid('getPager').pagination("options").pageNumber;
	 				pSize = $("#"+datagridId).datagrid('getPager').pagination("options").pageSize;
 				}catch(e){}
 				var filter = $("#"+formId).serializeJson();
 				filter['PageNum_']=pNum;
 				filter['PageSize_']=pSize;
		 		bizData.push({'formId_':forms[index],'filter_':filter,'data_':data});
 			}else{
 				data.push(formData);
		 		bizData.push({'formId_':forms[index],'data_':data});
 			}
	 	}
	 	
	 	var param = {'bizData_':bizData,'custData_':params};
 		 $.ajax({
			url : "/"+control,
			type : "post",
			dataType : "json",
			async:async,
			data: {
				'RequestJsonData_':encodeURIComponent(encodeURIComponent(JSON.stringify(param)))
			},//TODO:params的编码 以及form表单的序列化$("#form1").serialize()
			success : function(result){
				var bd = result.BizJsonData_;
				for(var i in bd){
					var formId = bd[i].formId_;
					var isQuery = $('#'+formId).hasClass("easyui-datagrid");
					if(isQuery==true){
						//过滤包含html格式的信息
						var dataStr = JSON.stringify(bd[i].data_);
						var filterHtml=dataStr.replace(/<\/?.+?>/g,"");
						filterHtml=filterHtml.replace(/&nbsp;/g,"");
						var t1 = new Date();
						$('#'+formId).datagrid('loadData',JSON.parse(filterHtml));
						//console.log(new Date().getTime()-t1);
					}else{
						$('#'+formId).form('load', bd[i].data_.rows[0]);
					}
				}
				if(opt.hasOwnProperty("sucFun")){
			 		opt.sucFun(result.ResponseStatus_, bd, result.CustJsonData_);
			 	}
				if(opt.hasOwnProperty("mask") && opt.mask==true){
			 		$("#doingMask").hide();
			 	}
			},
			error:function(XMLHttpRequest, textStatus, errorThrown){
				if(opt.hasOwnProperty("mask") && opt.mask==true){
			 		$("#doingMask").hide();
			 	}
				if("Unauthorized"==errorThrown){
					utils.alert("登录过期，请重新登录！");
				}else{
					utils.alert("请求出错，请重试！");
				}
				//alert(XMLHttpRequest.status);
				//alert(XMLHttpRequest.readyState);
				//alert(textStatus);
				if(opt.hasOwnProperty("errFun")){
			 		opt.errFun();
			 	}
			}
	     });
	}
	// 返回用户基本信息
	// 返回结构
	//	{
	//		"phoneNo":"","userNme":"","CKfDptNme":"","CCity":"","CPro":"",
	//		"userId":"","CRole":"","password":"","CKfDptId":"","CZone":""
	//	}
	utils_.getUser = function(){
		return JSON.parse(localStorage.getItem("userUtils"));
	}
	
	//禁用表单
	utils_.disableForm=function(formId){
		$("#"+formId).find("input").each(function(i,ele){
			if($(ele).hasClass("easyui-combobox")){
				$(ele).textbox({"readonly":true})
			}else if($(ele).attr("type")=="button"){
				$(ele).attr("disabled",true);
			}else{
				$(ele).attr("disabled",true);
				$(ele).css("background-color","#fff");
			}
		});
	}
	//初始化datagrid 
	//ex:{"id1":getData,"datagrid2":queryAll}
	utils_.initDataGrid=function(params){
		$(".easyui-datagrid").each(function(i,ele){
			var tbId = $(ele).datagrid('options').toolbar;
			$(tbId).find(".queryBtn_").eq(0).click(function(){
				if(params){
					params[$(ele).attr("id")]();
				}else{
					try{
						query();
					}catch(e){
						alert($(ele).attr("id")+"未配置查询方法");
					}
				}
			});
			
			var pager = $(ele).datagrid('getPager');
			var opts = $(ele).datagrid('options');
			if(pager!=null){
				pager.pagination({
					onSelectPage:function(pageNum, pageSize){
						opts.pageNumber = pageNum;
						opts.pageSize = pageSize;
						if(params){
							params[$(ele).attr("id")]();
						}else{
							try{
								query();
							}catch(e){
								alert($(ele).attr("id")+"未配置查询方法");
							}
						}
						
					}
				});
			}
		});
	}
	

	//弹出提示框 msg需要弹出的信息，callback(可选)回调函数
	utils_.alert = function(msg,callback){
		$.messager.alert("提示",msg,"",function(){
			if(callback)
				callback();
		});
	}
	
	//右下角的提示框
	utils_.showMsg = function(msg,title){
		$.messager.show({
            "title":title?title:"提示",
            "msg":msg,
            "showType":'show',
            "timeout":2000
        });
	}
	
	//弹出模态窗口
	//{url:'',msg:'',width:'',height:'',title:'',onClose:function(){}} url和msg二选一
	utils_.popWin=function(options){
		var option ={
			width : options.width  || 600,
			height: options.height || 400,
			title : options.title  || "信息",
			url	  : '',
			modal : true,
			minimizable:options.min || false,
		    collapsible:options.collapsible || false,
		    maximizable:options.max || true,
		    onClose:function(){
		    	options.onClose && options.onClose();
		    }
		}
		
		if(option.url && option.msg){alert('url和msg参数不能同时为空'); }
		$.extend(true,option, options || {});
		if(option.url){
			var iframe="<iframe scrolling='yes' frameborder='0'  src='"+option.url+"' style='width:100%;height:100%;overflow:hidden;'></iframe>";
			$('#popWin_').html(iframe);
		}else if(option.msg){
			$('#popWin_').html(option.msg);
		}
		$('#popWin_').window(option).window("center");
	}
	/**
	 * 关闭模态窗口
	 */
	utils_.closeWin=function(){
		$('#popWin_').children().eq(0).remove();
		$('#popWin_').window('close');
	}
	/**
	 * 自定义弹出层布局   
	 * @param options	json 参数
	 */
	utils_.dialog = function(id,options){
		var dlg = document.getElementById(id);
		var option ={
            title: options.title || "信息",
            iconCls:"icon-edit",
            collapsible: true,
            minimizable: true,
            maximizable: true,
            resizable: true,
            width	: options.width 	|| 600,
            height	: options.height 	|| 370,
            modal: true,
            onClose: function () {
                options.onClose && options.onClose();
            },
            buttons: [{
                text: 'Ok',
                iconCls: 'icon-ok',
                handler: function () {
                    options.sucFn && options.sucFn();
                }
            }, {
                text: 'Cancel',
                iconCls: 'icon-cancel',
                handler: function () {
                	$(dlg).dialog('close');
                	$(dlg).remove();
                }
            }]
        }
		$.extend(true,option, options || {});
		$(dlg).dialog(option);
	}
	
	//创建tab
	//{url:'',title:'',tabId:''}
	utils_.addTab=function(options){
		var opts = {
			url:'',
			title:'标题',
			tabId:'tabs',
			id:new Date().getTime(),
			repeat:false

		}
		$.extend(true,opts,options || {});
		
		if(opts.url==""){return;}
		
		//不允许重复tab页面
		if(opts.repeat==false){
			//查看是否有相同的url，如果有则显示，如果没有则新增tab
			var tabs = $("#tabs").tabs('tabs');
			for(var i = tabs.length-1;i>=0;i--){
	      		var tabUrl = $("#tabs").tabs('getTab',i)[0].innerHTML
	      		//该url已经打开
	      		if(tabUrl.indexOf(opts.url)!=-1){
	      			$('#'+opts.tabId).tabs('select', i);
	      			return;
	      		}
	      	}
		}
		
		
		//没有相同的url，新增
		var	name = opts.url.slice(3,5),
		content = '<iframe scrolling="yes" frameborder="0" name='+name+' src="'+opts.url+'" style="width:100%;height:100%;overflow:hidden;" ></iframe>';
		$('#'+opts.tabId).tabs('add',{
			title:opts.title,
			content:content,
			closable:true,
			plain:true,
			narrow:true
		});
	}
	
	//获取url的参数 paramNme为空则返回整个参数json，否则返回指定参数
	utils_.getParam=function(paramNme){
		var jsonData={};
		var _url = decodeURIComponent(decodeURIComponent(location.href));
		var paramStr=_url.slice(_url.indexOf('?')+1);
		//多个参数
		if(paramStr.indexOf('&')!=-1){
			var paramArr=paramStr.split('&');
		    for(var i in paramArr){
		    	var tempArr=paramArr[i].split('=');
		    	jsonData[tempArr[0]]=tempArr[1];
		    }
		}else{
			//一个参数
			var tempArr=paramStr.split('=');
			jsonData[tempArr[0]]=tempArr[1];
		}
		if(paramNme){
			return jsonData[paramNme];
		}else{
		    return jsonData;
		}
	}
	//阻止body右击事件
	utils_.oncontextmenu = function(){
		document.getElementsByTagName("body")[0].oncontextmenu = function(){return false;}
	}
	//删除datagrid的一行
	//{datagrid:'',service:'',method:'',key:[]}
	//{datagrid:'',service:'',method:'',key:''}
	utils_.rowDel=function(opt){
		if(!opt.hasOwnProperty("datagrid")){
			alert('datagrid参数必选');
			return false;
		}
		var row = $('#'+opt.datagrid).datagrid('getSelected');
		if(!row){
		 	$.messager.alert('提示','请选择一条记录');
		 	return;
		 }else{
		 	$.messager.confirm('确认?','是否确认删除',function(r){
			    if (r){
			    	var cd = {};
			    	if(opt.hasOwnProperty("key")){
			    		//是数组
			    		if(typeof opt.key == 'object' && typeof opt.key.length == 'number'){
			    			for(var i=0;i<opt.key.length;i++){
			    				cd[opt.key[i]]=row[opt.key[i]];			   
			    			}
			    		}else{
							cd[opt.key]=row[opt.key];			    			
			    		}
					}else{
						cd['rowData'] = JSON.stringify(row);
					}
			    	
			    	utils.ajax({
						service:opt.service,
						method:opt.method,
						forms:[],
						params:cd,
						validate:false,
						sucFun:function(mrk,bd,cd){
							if(mrk){
								var rowIndex = $('#'+opt.datagrid).datagrid('getRowIndex', row);
								$('#'+opt.datagrid).datagrid('deleteRow',rowIndex);
								$.messager.show({
									title:'提示',
									msg:'删除成功',
									timeout:2000,
									showType:'slide'
								});
							}else{
								$.messager.show({
									title:'提示',
									msg:'删除失败',
									timeout:2000,
									showType:'slide'
								});
							}
							
						}
					});
			    }
			});
		 }
	}

	//查看某一行
	//{datagrid:'',url:'',title:'',width:'',height:'',key:[]}
	//{datagrid:'',url:'',title:'',width:'',height:'',key:''}
	utils_.rowDetail=function(opt){
		if(!opt.hasOwnProperty("datagrid")){
			alert('datagrid参数必选');
			return false;
		}
		if(!opt.hasOwnProperty("url")){
			alert('url参数必选');
			return false;
		}
		var title = '';
		if(opt.hasOwnProperty("url")){
			title = opt.title
		}
		
		var url = opt.url;
		if(url.indexOf('?')==-1){
			url = url+'?1=1';
		}
		
		var width="600";
		var height="400";
		if(opt.hasOwnProperty("width")){
			width = opt.width
		}
		if(opt.hasOwnProperty("height")){
			height = opt.height
		}
		
		var row = $('#'+opt.datagrid).datagrid('getSelected');
		if(!row){
		 	$.messager.alert('提示','请选择一条记录');
		 	return;
		 }else{
		 	if(opt.hasOwnProperty("key")){
	    		//是数组
	    		if(typeof opt.key == 'object' && typeof opt.key.length == 'number'){
	    			for(var i=0;i<opt.key.length;i++){
	    				url=url+'&'+opt.key[i]+'='+row[opt.key[i]];			   
	    			}
	    		}else{
	    			url=url+'&'+opt.key+'='+row[opt.key];	
	    		}
			}
		 	utils.popWin({
		 		url:url,
		 		title:title,
		 		width:width,
		 		height:height
		 	}); 
		 	
		 }
	}

	//清除指定formId表单数据，也可以传调用的this指代(则查找最近的父节点为form标签的 清空其内容)
	utils_.clearForm=function(param){
		if(typeof param=='undefined'){
			alert('参数必选');
			return;
		}
		
		if(typeof param == 'object'){
			$(param).parents('form').eq(0).form('clear');
		}else{
			$('#'+param).form('clear');
		}
	}
	
	
	utils_.isJson = function(obj){
		var isjson = typeof(obj) == "object" && Object.prototype.toString.call(obj).toLowerCase() == "[object object]" && !obj.length; 
		return isjson;
	}
	
	utils_.validate=function(formIdArray){
		var result = {};//验证的结果 {formid1:['domeNme1','domeId2']}
		var validateMrk = true;//验证不通过为false，验证通过true
		for(var index in formIdArray){
			var formData = $("#"+formIdArray[index]).serializeJson();
			var domArray = $("#"+formIdArray[index]).find("input");
			
			for(var i in domArray){
				var domNme = domArray[i].name;
				var value = formData[domNme];
				
				var obj = $("#"+formIdArray[index]).find("input[name='"+domNme+"'],input[textboxname='"+domNme+"'],select[textboxname='"+domNme+"']").eq(0);
				if(obj.attr("required")=="required"&&(value==null || value==undefined || value=='')){
					validateMrk = false;
					if(obj.hasClass("msgTips_")){
						obj.addClass("tips-err");
					}else{
						obj.parents(".msgTips_").addClass("tips-err");
					}
					
					if(result.hasOwnProperty(formIdArray[index])){
						if(result[formIdArray[index]].indexOf(domNme)==-1)
							result[formIdArray[index]].push(domNme);
					}else{
						result[formIdArray[index]] = [];
						result[formIdArray[index]].push(domNme);
					}
				}
				
			}
		}
		
		return validateMrk;
	}
	
	//绑定所有input输入元素的change事件，如果有值则移除掉tips-err的样式。
	//不能只判断自己添加的required属性，应该判断有没有tips-err样式，因为部分easyui校验的也使用了msgTips的提示，如08大类的basic里面的radio校验变色
	utils_.bindValidateEvent=function(formIds){
		for(var index in formIds){
			var formId = formIds[index];
			$("#"+formId).find("input").on("change",function(){
				var value = $(this).val();
//				if($(this).attr("required")=="required"&&(value!=null&&value!=undefined&&value.trim()!="")){
				if(value!=null&&value!=undefined&&value.trim()!=""){
					if($(this).hasClass("tips-err")){
						$(this).removeClass("tips-err");
					}else{
						$(this).parents(".tips-err").removeClass("tips-err");
					}
				}
			});
		}
	}
	
	//关闭加载中的遮罩层
	utils_.closeLoading=function(){
//		$('#loadingDiv').hide();
		$("div.ui-load").each(function(i,ele){
			$(ele).hide();
		});
	}
	
	//初始化帮助页面，autoOpen为true表示初始化页面的时候会自动打开（前提是当前浏览器必须支持localStorage）
	utils_.initHelpWindow = function (autoOpen){
		//量表标题后面的？的弹出事件
		var currentUrl = location.href;
		var helpHtmlNme = currentUrl.substring(currentUrl.lastIndexOf("/")+1,currentUrl.lastIndexOf("."));
		//部分页面可能没有设置basePath的js变量
		try{
			basePath;
		}catch(e){
			basePath = currentUrl.substring(0,currentUrl.indexOf("/jsp"));
		}
		$(".ui-help").on("click",function(){
			var opt={
					url:basePath+'/memo/html/'+helpHtmlNme+".htm",
					width:'80%',
					title:'操作提示',
					maximizable:false,
					onClose:function(){
						
					}
			};
			utils.popWin(opt);
		});

		return false;

		if(!autoOpen){
			return;
		}
		
		//延迟一个时间显示，否则由于页面还未渲染完毕，导致popWin的高度有问题
		setTimeout(function(){
			//支持本地缓存 且 本地缓存中没有设置说不在提示
			if(localStorage && localStorage.getItem(helpHtmlNme)!="false"){
				$(".ui-help").trigger("click");
			}
		},50);
	}
	utils_.console=function(){
//		console.log("这个页面有数据 " + basePath);
	}
	//关闭帮助页面，noShowAgainMrk不在显示标志，为true标示不在显示
	utils_.closeHelpWindow = function(noShowAgainMrk){
		var currentUrl = location.href;
		var helpHtmlNme = currentUrl.substring(currentUrl.lastIndexOf("/")+1,currentUrl.lastIndexOf("."));
		if(noShowAgainMrk && localStorage){
			localStorage.setItem(helpHtmlNme,"false");
		}
		utils.closeWin();
	}
	//在新窗口中开启帮助页面
	utils_.openInNewWindow = function(noShowAgainMrk){
		var currentUrl = location.href;
		var helpHtmlNme = currentUrl.substring(currentUrl.lastIndexOf("/")+1,currentUrl.lastIndexOf("."));
		var helpHtmlUrl=basePath+'/memo/html/'+helpHtmlNme+".htm";
		window.open(helpHtmlUrl);
	}
	window['utils']=utils_;
})(jQuery);



(function($){  
    $.fn.serializeJson=function(){  
        var serializeObj={};  
        var array=this.serializeArray();  
        var str=this.serialize();  
        $(array).each(function(){  
            if(serializeObj[this.name]){  
                if($.isArray(serializeObj[this.name])){  
                    serializeObj[this.name].push(this.value);  
                }else{  
                    serializeObj[this.name]=[serializeObj[this.name],this.value];  
                }  
            }else{  
                serializeObj[this.name]=this.value;   
            }  
        });  
        return serializeObj;  
    };  
})(jQuery);



try{
	//combobox的输入校验 请选择的时候是显示为必须
	$.extend($.fn.validatebox.defaults.rules, {
		//正整数
		isNumber: {//验证是否是数字 num==>isNumber
	        validator: function(value,param){
	        	return  /^\d+(\.\d+)?$/i.test(value);
	        },
	        message: '您输入的不是数字'
	    },
	    //正负数值（可以带小数）
		num:{
			validator: function(value,param){
				var reg= /^[+-]?\d*\.?\d*$/;
	        	return reg.test(value);
	        },
	        message: '您输入的不是数字'
		},
		maxNum: {//验证最大数值
	        validator: function(value, param){
	        	$.fn.validatebox.defaults.rules.maxNum.message ='填写的数字不能大于'+param[0];
	            return value <= param[0];
	        },
	        message: ''
	    },
		"enum":{
			validator: function(value, param){
	        	for(var i in param){
	        		if(value == param[i]){
	        			return true;
	        		}
	        	}
	        	$.fn.validatebox.defaults.rules["enum"].message ='只能输入'+param;
	            return false;
	        },
	        message: ''
		},
		
	    minNum: {//验证最小数值
	        validator: function(value, param){
	        	$.fn.validatebox.defaults.rules.minNum.message ='填写的数字不能小于'+param[0];
	            return value >= param[0];
	        },
	        message: ''
	    },
	    comboboxRequired: {//comboVry==>comboboxRequired
			validator: function (value, param) {//param为默认值
				return value != param[0];
			},
			message: '该选项为必填项'
		},
		length: {//验证文本值长度 ,numlength==>length
			validator: function(value,param){
				$.fn.validatebox.defaults.rules.length.message ="长度不能超出"+param[0]+"位";
	            return value.length <= param[0];
	        },
	        message: ''
		},
		"number": {//验证数值文本长度,第1个参数指定整数位长度，第2个参数指定小数位长度
			validator: function(value,param){
				$.fn.validatebox.defaults.rules.number.message ="整数长度不能超出"+param[0]+"位数";
				var tmp=value.split(".");
				//如果为纯数字"123"，则tmp=[value];如果为"123.",则tmp=[value,""];如果为"123.1"，则tmp[value,"1"];
				if(tmp[1]!=""&&tmp[1]!=null&&tmp[1]!=undefined&&tmp[0].length<=param[0]){
					$.fn.validatebox.defaults.rules.number.message ="小数长度不能超出"+param[1]+"位数";
					return tmp[1].length<=param[1];
				};
	            return tmp[0].length <= param[0];
	        },
	        message: ''
		},
		isPhoneNo:{//验证手机号码 phoneRex==>isPhoneNo
			validator: function(value){
				value = value.trim();
				var rex=/^1[3|4|5|6|7|8|9]\d{9}$/;
				var rex2=/^((0\d{2,3})-)(\d{7,8})(-(\d{3,}))?$/;
				if(rex.test(value)||rex2.test(value)){
					return true;
				}else{
					return false;
				}
			},
			message: '手机号码格式不正确'
		},
		isDate:{//dateValidate==>isDate
			validator: function(date){
				return !isNaN(Date.parse(date));
			},
			message: '日期格式不正确'
		},
		idCard: {//验证身份证
	          validator: function (value) {
	              return /^\d{15}(\d{2}[A-Za-z0-9])?$/i.test(value);
	          },
	          message: '身份证号码格式不正确'
	      },
	      //ex:validType="equals['#pwd']"
	      equals: {
			  validator: function(value,param){
			      return value == $(param[0]).val();
			  },
			  message: '两次输入的密码不一致'
		},
		//ex:validType="radio['sex']"
		radioRequired:{//radio==>radioRequired
	      	validator: function (value,params) {
	      		
	      		var val = $("#"+params[0]+" input:radio[name='"+params[1]+"']:checked").val();
	      		//var val = $("#f3 input:radio[name='CAns1']:checked").val();
	      		if(val==null){
	                  return false;
	              }else{
	              	return true;
	              }
	      		
//	    		 $("#"+params[0]).find("input[name='" + params[1] + "']").each(function (i,ele) { //查找表单中所有此名称的radio
//	                if($(ele).checked){
//	                	return true;
//	                }
//	             });
//	    		 return false;
	          },
	          message: '该选项为必填项'
	      },
	      //ex:validType="radio['likes']"
	      "checkbox":{//checkboxRequired==>checkbox
	      	validator: function (value,params) {
	      		var objs = $("input[name='" + params[1] + "']",document[params[0]]);
	      		if(objs.is(':checked')){
	      			return true;
	      		}else{
	      			return false;
	      		}
	          },
	          message: '该选项为必填项'
	      },
	      cdtRequire:{
	      	validator: function (value,params) {
	      		var name = $(this).attr("name");
	      		var type = $(this).attr("type");
	      		if(params[0]==true){
	      			//如果为true的时候必须
	      			//如果为false的时候非必须
//	    			console.log("必须");
	      			if(type.toLowerCase()=="checkbox"){
	      				if($("input[name='"+name+"']").is(":checked")){
	      					return true;
	      				}else{
	      					return false;
	      				}
	      			}else if(type.toLowerCase()=="radio"){
	      				if($("input[name='"+name+"']").is(":checked")){
	      					return true;
	      				}else{
	      					return false;
	      				}
	      			}else if(type.toLowerCase()=="text"){
	      				if(value.trim()!=""){
	      					return true;	
	      				}else{
	      					return false;
	      				}
	      			}
	      		}else{
//	    			console.log("非必须");
	      			return true;
	      		}
	      		
	          },
	          message: '该选项为必填项'
	      }
	});

	//combobox点击非倒三角触发下拉框显示事件
	$(".combo").click(function(){
		$(this).prev().combobox("showPanel");
	});
	
}catch(e){}




