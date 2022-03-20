import {
	getOptionsByMapViewsKeys,
	updateOptionsByMapViews
} from "../../common/api.js";

getOptionsByMapViewsKeys(array).then(data => {
	that.api_enabled = data.api_enabled;
	that.api_access_key = data.api_access_key;
	uni.stopPullDownRefresh();
}).catch(err => {
	uni.stopPullDownRefresh();
	uni.showModal({
		title: "获取数据失败",
		content: err
	});
});

updateOptionsByMapViews(json).then(data => {
	that.popup("保存成功", "success");
	that.refreshData();
}).catch(err => {
	uni.stopPullDownRefresh();
	uni.showModal({
		title: "保存数据失败",
		content: err
	});
});
