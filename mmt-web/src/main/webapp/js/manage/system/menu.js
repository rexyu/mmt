$(function() {
	$.fn.modal.defaults.spinner = $.fn.modalmanager.defaults.spinner = '<div class="loading-spinner" style="width: 200px; margin-left: -100px;">'
			+ '<div class="progress progress-striped active">'
			+ '<div class="progress-bar" style="width: 100%;"></div>'
			+ '</div>' + '</div>';

	$modal = $('#menu-ajax-modal');
	
	//监听加载添加/编辑页面事件
	$('.ajax .menu').on('click', function(){
		var opType = this.id;
		var menu_id = 0;
		if(opType === 'menu-update'){
			menu_id = getSelectedValues('menu_id');
		}
		loadPage(url_showPage, {menu_id:menu_id}, $modal);
	});
	
	//监听保存数据事件
	$modal.on('click', '.save', function(){
		var params_save = {
			menu_id		: $('#menu_id').val(),
			menu_name	: $('#menu_name').val(),
			supmenucode	: $('#supmenucode').val(),
			menuurl		: $('#menuurl').val(),
			menuico		: $('#menuico').val(),
			sort		: $('#sort').val()
	 	};
		mmtPost(url_save, params_save, $modal, $modal.find('.modal-body'));
	});
	
	//监听删除事件
	$('#menu-delete').on('click', function(){
		var menu_id = getSelectedValues("menu_id");
		mmtPost(url_delete, {menu_id:menu_id}, $('body'), $('.text-left'));
	});
});