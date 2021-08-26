window.onload = function() {
	// 设置当前年份
	var now = new Date();
	var copy_current_year = now.getFullYear();
	$("#current-year").text("" + copy_current_year);

	utils.closeLoading();

}
// 示例表单Ajax提交
function toSubmit() {
	var _param1 = $('#param1').val();
	var _param2 = $('#param2').val();
	var _param3 = $('#param3').val();
	var _param4 = $('#param4').val();
	//var _param5 = $('#param5').val();
	//var _param6 = $('#param6').val();

	utils
		.ajax({
			service : 'childrenAction',
			method : 'quickFiling',
			params : {
				param1 : _param1,
				param2 : _param2,
				param3 : _param3,
				param4 : _param4,
				param5 : "dptId",
				param6 : "officeId"
			},
			control : 'ServiceControl',
			sucFun : function(mrk, bd, cd) {
			//debugger;
				console.log(mrk);
				console.log(bd);
				console.log(cd);
			}
		});
}
