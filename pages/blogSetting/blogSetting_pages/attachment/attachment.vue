<template>
	<view class="container">
		<uni-popup ref="popup" type="message">
			<uni-popup-message :type="popupType" :message="popupMessage"></uni-popup-message>
		</uni-popup>
		<view class="block">
			<view class="view-input">
				<view class="view-input-titleView">上传图片时预览：</view>
				<switch :checked="attachment_upload_image_preview_enable"
					@change="attachmentUploadImagePreviewEnableSwitchChange" />
			</view>

			<view class="view-input">
				<view class="view-input-titleView">最大上传文件数：</view>
				<input class="input" type="number" v-model="attachment_upload_max_files" />
			</view>

			<view class="view-input">
				<view class="view-input-titleView">同时上传文件数：</view>
				<input class="input" type="number" v-model="attachment_upload_max_parallel_uploads" />
			</view>

			<view class="view-input">
				<view class="view-input-titleView">存储位置：</view>
				<view class="input">
					<picker @change="attachmentTypeChange" :value="attachment_typeIndex" :range="attachment_typeText">
						<view>{{attachment_typeText[attachment_typeIndex]}}</view>
					</picker>
				</view>
			</view>
				
			<view v-if="attachment_typeText[attachment_typeIndex] === 'SM.MS'">
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Secret Token：</view>
					<input class="input" type="text" v-model="smms_api_secret_token" placeholder="需要到 sm.ms 官网注册后获取" />
				</view>
			</view>
			

			<view v-if="attachment_typeText[attachment_typeIndex] === '又拍云'">
				<view class="view-input">
					<view class="view-input-titleView">绑定域名协议：</view>
					<view class="input">
						<picker @change="ossUpyunDomainProtocolChange" :value="oss_upyun_domain_protocolIndex"
							:range="oss_upyun_domain_protocolText">
							<view>{{oss_upyun_domain_protocolText[oss_upyun_domain_protocolIndex]}}</view>
						</picker>
					</view>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>绑定域名：</view>
					<input class="input" type="text" v-model="oss_upyun_domain" placeholder="无需再加上 http:// 或者 https://" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>空间名称：</view>
					<input class="input" type="text" v-model="oss_upyun_bucket" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>操作员名称：</view>
					<input class="input" type="text" v-model="oss_upyun_operator" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>操作员密码：</view>
					<input class="input" type="text" password v-model="oss_upyun_password" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>文件目录：</view>
					<input class="input" type="text" v-model="oss_upyun_source" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">图片处理策略：</view>
					<input class="input" type="text" v-model="oss_upyun_style_rule" placeholder="间隔标识符+图片处理版本名称" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">缩略图处理策略：</view>
					<input class="input" type="text" v-model="oss_upyun_thumbnail_style_rule"
						placeholder="间隔标识符+图片处理版本名称,一般为后台展示所用" />
				</view>
			</view>
			
			
			<view  v-if="attachment_typeText[attachment_typeIndex] === '七牛云'">
				<view class="view-input">
					<view class="view-input-titleView">绑定域名协议：</view>
					<view class="input">
						<picker @change="ossQiniuDomainProtocolChange" :value="oss_qiniu_domain_protocolIndex"
							:range="oss_qiniu_domain_protocolText">
							<view>{{oss_qiniu_domain_protocolText[oss_qiniu_domain_protocolIndex]}}</view>
						</picker>
					</view>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>绑定域名：</view>
					<input class="input" type="text" v-model="oss_qiniu_domain" placeholder="无需再加上 http:// 或者 https://" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">区域：</view>
					<view class="input">
						<picker @change="ossQiniuZoneChange" :value="oss_qiniu_zoneIndex" :range="oss_qiniu_zoneText">
							<view>{{oss_qiniu_zoneText[oss_qiniu_zoneIndex]}}</view>
						</picker>
					</view>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Access Key：</view>
					<input class="input" type="text" v-model="oss_qiniu_access_key" password />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Secret Key：</view>
					<input class="input" type="text" v-model="oss_qiniu_secret_key" password />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">文件目录：</view>
					<input class="input" type="text" v-model="oss_qiniu_source" placeholder="不写则上传到根目录" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Bucket：</view>
					<input class="input" type="text" v-model="oss_qiniu_bucket" placeholder="存储空间名称" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">图片处理策略：</view>
					<input class="input" type="text" v-model="oss_qiniu_style_rule" placeholder="间隔标识符+图片处理版本名称" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">缩略图处理策略：</view>
					<input class="input" type="text" v-model="oss_qiniu_thumbnail_style_rule"
						placeholder="间隔标识符+图片处理版本名称,一般为后台展示所用" />
				</view>
			</view>

			
			<view v-if="attachment_typeText[attachment_typeIndex] === '阿里云'">
				<view class="view-input">
					<view class="view-input-titleView">绑定域名协议：</view>
					<view class="input">
						<picker @change="ossAliDomainProtocolChange" :value="oss_ali_domain_protocolIndex"
							:range="oss_ali_domain_protocolText">
							<view>{{oss_ali_domain_protocolText[oss_ali_domain_protocolIndex]}}</view>
						</picker>
					</view>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">绑定域名：</view>
					<input class="input" type="text" v-model="oss_ali_domain" placeholder="如不填写,路径根域名将为 Bucket + EndPoint" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Bucket：</view>
					<input class="input" type="text" v-model="oss_ali_bucket_name" placeholder="存储空间名称" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>EndPoint(地域节点)：</view>
					<input class="input" type="text" v-model="oss_ali_endpoint"/>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Access Key：</view>
					<input class="input" type="text" v-model="oss_ali_access_key" password />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Access Secret：</view>
					<input class="input" type="text" v-model="oss_ali_access_secret" password />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">文件目录：</view>
					<input class="input" type="text" v-model="oss_ali_source" placeholder="不写则上传到根目录" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">图片处理策略：</view>
					<input class="input" type="text" v-model="oss_ali_style_rule" placeholder="请到阿里云控制台的图片处理获取" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">缩略图处理策略：</view>
					<input class="input" type="text" v-model="oss_ali_thumbnail_style_rule"
						placeholder="请到阿里云控制台的图片处理获取,一般为后台展示所用" />
				</view>
			</view>
			
			
			<view v-if="attachment_typeText[attachment_typeIndex] === '百度云'">
				<view class="view-input">
					<view class="view-input-titleView">绑定域名协议：</view>
					<view class="input">
						<picker @change="bosBaiduDomainProtocolChange" :value="bos_baidu_domain_protocolIndex"
							:range="bos_baidu_domain_protocolText">
							<view>{{bos_baidu_domain_protocolText[bos_baidu_domain_protocolIndex]}}</view>
						</picker>
					</view>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">绑定域名：</view>
					<input class="input" type="text" v-model="bos_baidu_domain" placeholder="如不填写,路径根域名将为 Bucket + EndPoint" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Bucket：</view>
					<input class="input" type="text" v-model="bos_baidu_bucket_name" placeholder="存储空间名称" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>EndPoint(地域节点)：</view>
					<input class="input" type="text" v-model="bos_baidu_endpoint"/>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Access Key：</view>
					<input class="input" type="text" v-model="bos_baidu_access_key" password />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Secret Key：</view>
					<input class="input" type="text" v-model="bos_baidu_secret_key" password />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">图片处理策略：</view>
					<input class="input" type="text" v-model="bos_baidu_style_rule" placeholder="请到百度云控制台的图片处理获取" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">缩略图处理策略：</view>
					<input class="input" type="text" v-model="bos_baidu_thumbnail_style_rule"
						placeholder="请到百度云控制台的图片处理获取,一般为后台展示所用" />
				</view>
			</view>
			
			
			
			<view v-if="attachment_typeText[attachment_typeIndex] === '腾讯云'">
				<view class="view-input">
					<view class="view-input-titleView">绑定域名协议：</view>
					<view class="input">
						<picker @change="cosTencentDomainProtocolChange" :value="cos_tencent_domain_protocolIndex"
							:range="cos_tencent_domain_protocolText">
							<view>{{cos_tencent_domain_protocolText[cos_tencent_domain_protocolIndex]}}</view>
						</picker>
					</view>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">绑定域名：</view>
					<input class="input" type="text" v-model="cos_tencent_domain" placeholder="如不填写,路径根域名将为 Bucket + EndPoint" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Bucket：</view>
					<input class="input" type="text" v-model="cos_tencent_bucket_name" placeholder="存储桶名称" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">区域：</view>
					<view class="input">
						<picker @change="cosTencentRegionChange" :value="cos_tencent_regionIndex" :range="cos_tencent_regionText">
							<view>{{cos_tencent_regionText[cos_tencent_regionIndex]}}</view>
						</picker>
					</view>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Secret Id：</view>
					<input class="input" type="text" v-model="cos_tencent_secret_id" password />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Secret Key：</view>
					<input class="input" type="text" v-model="cos_tencent_secret_key" password />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">文件目录：</view>
					<input class="input" type="text" v-model="cos_tencent_source"/>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">图片处理策略：</view>
					<input class="input" type="text" v-model="cos_tencent_style_rule" placeholder="请到腾讯云控制台的图片处理获取" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">缩略图处理策略：</view>
					<input class="input" type="text" v-model="cos_tencent_thumbnail_style_rule"
						placeholder="请到腾讯云控制台的图片处理获取,一般为后台展示所用" />
				</view>
			</view>

			
			<view v-if="attachment_typeText[attachment_typeIndex] === '华为云'">
				<view class="view-input">
					<view class="view-input-titleView">绑定域名协议：</view>
					<view class="input">
						<picker @change="obsHhuaweiDomainProtocolChange" :value="obs_huawei_domain_protocolIndex"
							:range="obs_huawei_domain_protocolText">
							<view>{{obs_huawei_domain_protocolText[obs_huawei_domain_protocolIndex]}}</view>
						</picker>
					</view>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">绑定域名：</view>
					<input class="input" type="text" v-model="obs_huawei_domain" placeholder="如不填写,路径根域名将为 Bucket + EndPoint" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Bucket：</view>
					<input class="input" type="text" v-model="obs_huawei_bucket_name" placeholder="桶名称" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>EndPoint(终端节点)：</view>
					<input class="input" type="text" v-model="obs_huawei_endpoint" placeholder="Endpoint"/>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Access Key：</view>
					<input class="input" type="text" v-model="obs_huawei_access_key" password />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Access Secret：</view>
					<input class="input" type="text" v-model="obs_huawei_access_secret" password />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">文件目录：</view>
					<input class="input" type="text" v-model="obs_huawei_source" placeholder="不填写则上传到根目录"/>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">图片处理策略：</view>
					<input class="input" type="text" v-model="obs_huawei_style_rule" placeholder="请到华为云控制台的图片处理获取" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">缩略图处理策略：</view>
					<input class="input" type="text" v-model="obs_huawei_thumbnail_style_rule"
						placeholder="请到华为云控制台的图片处理获取,一般为后台展示所用" />
				</view>
			</view>
			
			
			
			<view v-if="attachment_typeText[attachment_typeIndex] === 'MinIO'">
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>EndPoint(终端节点)：</view>
					<input class="input" type="text" v-model="minio_endpoint" placeholder="Endpoint"/>
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Bucket（桶名称）：</view>
					<input class="input" type="text" v-model="minio_bucket_name" placeholder="桶名称" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">Region（区域）：</view>
					<input class="input" type="text" v-model="minio_region" placeholder="区域" />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Access Key：</view>
					<input class="input" type="text" v-model="minio_access_key" password />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView"><text style="color: red;">*</text>Access Secret：</view>
					<input class="input" type="text" v-model="minio_access_secret" password />
				</view>
				
				<view class="view-input">
					<view class="view-input-titleView">文件目录：</view>
					<input class="input" type="text" v-model="minio_source" placeholder="不填写则上传到根目录"/>
				</view>
			</view>
			

			<button class="button save-button" type="primary" @click="saving">保存</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				attachment_upload_image_preview_enable: "",
				attachment_upload_max_files: "",
				attachment_upload_max_parallel_uploads: "",

				attachment_type: "",
				attachment_typeText: ["本地", "SM.MS", "又拍云", "七牛云", "阿里云", "百度云", "腾讯云", "华为云", "MinIO"],
				attachment_typeIndex: 0,
				attachment_typeValue: ["LOCAL", "SMMS", "UPOSS", "QINIUOSS", "ALIOSS", "BAIDUBOS", "TENCENTCOS",
					"HUAWEIOBS", "MINIO"
				],

				smms_api_secret_token: "",

				oss_upyun_domain_protocol: "",
				oss_upyun_domain_protocolText: ["HTTPS", "HTTP"],
				oss_upyun_domain_protocolIndex: 0,
				oss_upyun_domain_protocolValue: ["https://", "http://"],
				oss_upyun_domain: "",
				oss_upyun_bucket: "",
				oss_upyun_operator: "",
				oss_upyun_password: "",
				oss_upyun_source: "",
				oss_upyun_style_rule: "",
				oss_upyun_thumbnail_style_rule: "",


				oss_qiniu_domain_protocol: "",
				oss_qiniu_domain_protocolText: ["HTTPS", "HTTP"],
				oss_qiniu_domain_protocolIndex: 0,
				oss_qiniu_domain_protocolValue: ["https://", "http://"],
				oss_qiniu_domain: "",
				oss_qiniu_zone: "",
				oss_qiniu_zoneText: ["自动选择", "华东", "华北", "华南", "北美", "东南亚"],
				oss_qiniu_zoneIndex: 0,
				oss_qiniu_zoneValue: ["auto", "z0", "z1", "z2", "na0", "as0"],
				oss_qiniu_access_key: "",
				oss_qiniu_secret_key: "",
				oss_qiniu_source: "",
				oss_qiniu_bucket: "",
				oss_qiniu_style_rule: "",
				oss_qiniu_thumbnail_style_rule: "",
				
				
				oss_ali_domain_protocol: "",
				oss_ali_domain_protocolText: ["HTTPS", "HTTP"],
				oss_ali_domain_protocolIndex: 0,
				oss_ali_domain_protocolValue: ["https://", "http://"],
				oss_ali_domain: "",
				oss_ali_bucket_name: "",
				oss_ali_endpoint: "",
				oss_ali_access_key: "",
				oss_ali_access_secret: "",
				oss_ali_source: "",
				oss_ali_style_rule: "",
				oss_ali_thumbnail_style_rule: "",
				
				
			
				bos_baidu_domain_protocol: "",
				bos_baidu_domain_protocolText: ["HTTPS", "HTTP"],
				bos_baidu_domain_protocolIndex: 0,
				bos_baidu_domain_protocolValue: ["https://", "http://"],
				bos_baidu_domain: "",
				bos_baidu_bucket_name: "",
				bos_baidu_endpoint: "",
				bos_baidu_access_key: "",
				bos_baidu_secret_key: "",
				bos_baidu_style_rule: "",
				bos_baidu_thumbnail_style_rule: "",
				
				
				cos_tencent_domain_protocol: "",
				cos_tencent_domain_protocolText: ["HTTPS", "HTTP"],
				cos_tencent_domain_protocolIndex: 0,
				cos_tencent_domain_protocolValue: ["https://", "http://"],
				cos_tencent_domain: "",
				cos_tencent_bucket_name: "",
				cos_tencent_region: "",
				cos_tencent_regionText: ["北京一区", "北京", "上海（华东）", "广州（华南）", "成都（西南）", "重庆"],
				cos_tencent_regionIndex: 0,
				cos_tencent_regionValue: ["ap-beijing-1", "ap-beijing", "ap-shanghai", "ap-guangzhou", "ap-chengdu", "ap-chongqing"],
				cos_tencent_secret_id: "",
				cos_tencent_secret_key: "",
				cos_tencent_source: "",
				cos_tencent_style_rule: "",
				cos_tencent_thumbnail_style_rule: "",
				
				
				
				obs_huawei_domain_protocol: "",
				obs_huawei_domain_protocolText: ["HTTPS", "HTTP"],
				obs_huawei_domain_protocolIndex: 0,
				obs_huawei_domain_protocolValue: ["https://", "http://"],
				obs_huawei_domain: "",
				obs_huawei_bucket_name: "",
				obs_huawei_endpoint: "",
				obs_huawei_access_key: "",
				obs_huawei_access_secret: "",
				obs_huawei_source: "",
				obs_huawei_style_rule: "",
				obs_huawei_thumbnail_style_rule: "",
				
				minio_endpoint: "",
				minio_bucket_name: "",
				minio_region: "",
				minio_access_key: "",
				minio_access_secret: "",
				minio_source: "",

				popupType: "",
				popupMessage: ""
			}
		},

		mounted() {
			this.refreshData()
		},

		/**
		 * 下拉刷新事件
		 */
		onPullDownRefresh() {
			this.refreshData()
		},

		methods: {
			/**
			 * 刷新数据
			 */
			refreshData: function() {
				let array = ["attachment_upload_image_preview_enable", "attachment_upload_max_files",
					"attachment_upload_max_parallel_uploads", "attachment_type", "smms_api_secret_token",
					"oss_upyun_domain_protocol", "oss_upyun_domain", "oss_upyun_bucket",
					"oss_upyun_operator", "oss_upyun_password", "oss_upyun_source",
					"oss_upyun_style_rule", "oss_upyun_thumbnail_style_rule",
					"oss_qiniu_domain_protocol", "oss_qiniu_domain", "oss_qiniu_zone", "oss_qiniu_access_key",
					"oss_qiniu_secret_key", "oss_qiniu_source", "oss_qiniu_bucket", "oss_qiniu_style_rule",
					"oss_qiniu_thumbnail_style_rule",
					"oss_ali_domain_protocol", "oss_ali_domain", "oss_ali_bucket_name",
					"oss_ali_endpoint", "oss_ali_access_key", "oss_ali_access_secret", "oss_ali_source",
					"oss_ali_style_rule", "oss_ali_thumbnail_style_rule",
					"bos_baidu_domain_protocol", "bos_baidu_domain",
					"bos_baidu_bucket_name", "bos_baidu_endpoint", "bos_baidu_access_key", "bos_baidu_secret_key",
					"bos_baidu_style_rule", "bos_baidu_thumbnail_style_rule",
					"cos_tencent_domain_protocol", "cos_tencent_domain",
					"cos_tencent_bucket_name", "cos_tencent_region", "cos_tencent_secret_id",
					"cos_tencent_secret_key",
					"cos_tencent_source", "cos_tencent_style_rule", "cos_tencent_thumbnail_style_rule",
					"obs_huawei_domain_protocol", "obs_huawei_domain", "obs_huawei_bucket_name",
					"obs_huawei_endpoint",
					"obs_huawei_access_key", "obs_huawei_access_secret", "obs_huawei_source",
					"obs_huawei_style_rule",
					"obs_huawei_thumbnail_style_rule",
					"minio_endpoint", "minio_bucket_name", "minio_region",
					"minio_access_key", "minio_access_secret", "minio_source"
				]
				let that = this
				uni.request({
					method: "POST",
					dataType: "json",
					url: this.getUrl() + "/api/admin/options/map_view/keys",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.getAccessToken()
					},
					data: array,
					success: function(res) {
						uni.stopPullDownRefresh()
						if (res.statusCode !== 200) {
							that.popup("获取数据失败")
							// 登录过期
							if (that.isExpiredByRequest(res)) {
								that.setData("isLogin", "false")
								uni.reLaunch({
									url: "../../me/me"
								})
							}
							return
						}

						let data = res.data.data

						that.attachment_upload_image_preview_enable = data
							.attachment_upload_image_preview_enable
						that.attachment_upload_max_files = data.attachment_upload_max_files
						that.attachment_upload_max_parallel_uploads = data
							.attachment_upload_max_parallel_uploads

						that.attachment_type = data.attachment_type
						// 把服务端的数据和attachment_type比对，获得对应的选项索引
						that.attachment_typeIndex = that.attachment_typeValue.indexOf(that.attachment_type)

						that.smms_api_secret_token = data.smms_api_secret_token

						that.oss_upyun_domain_protocol = data.oss_upyun_domain_protocol
						// 把服务端的数据和oss_upyun_domain_protocol比对，获得对应的选项索引
						that.oss_upyun_domain_protocolIndex = that.oss_upyun_domain_protocolValue.indexOf(
							that.oss_upyun_domain_protocol)

						that.oss_upyun_domain = data.oss_upyun_domain
						that.oss_upyun_bucket = data.oss_upyun_bucket
						that.oss_upyun_operator = data.oss_upyun_operator
						that.oss_upyun_password = data.oss_upyun_password
						that.oss_upyun_source = data.oss_upyun_source
						that.oss_upyun_style_rule = data.oss_upyun_style_rule
						that.oss_upyun_thumbnail_style_rule = data.oss_upyun_thumbnail_style_rule

						that.oss_qiniu_domain_protocol = data.oss_qiniu_domain_protocol
						// 把服务端的数据和oss_qiniu_domain_protocol比对，获得对应的选项索引
						that.oss_qiniu_domain_protocolIndex = that.oss_qiniu_domain_protocolValue.indexOf(
							that.oss_qiniu_domain_protocol)
						that.oss_qiniu_domain = data.oss_qiniu_domain
						that.oss_qiniu_zone = data.oss_qiniu_zone
						// 把服务端的数据和oss_qiniu_zone比对，获得对应的选项索引
						that.oss_qiniu_zoneIndex = that.oss_qiniu_zoneValue.indexOf(
							that.oss_qiniu_zone)
						that.oss_qiniu_access_key = data.oss_qiniu_access_key
						that.oss_qiniu_secret_key = data.oss_qiniu_secret_key
						that.oss_qiniu_source = data.oss_qiniu_source
						that.oss_qiniu_bucket = data.oss_qiniu_bucket
						that.oss_qiniu_style_rule = data.oss_qiniu_style_rule
						that.oss_qiniu_thumbnail_style_rule = data.oss_qiniu_thumbnail_style_rule
						
						that.oss_ali_domain_protocol = data.oss_ali_domain_protocol
						// 把服务端的数据和oss_ali_domain_protocol比对，获得对应的选项索引
						that.oss_ali_domain_protocolIndex = that.oss_ali_domain_protocolValue.indexOf(
							that.oss_ali_domain_protocol)
						that.oss_ali_domain = data.oss_ali_domain
						that.oss_ali_bucket_name = data.oss_ali_bucket_name
						that.oss_ali_endpoint = data.oss_ali_endpoint
						that.oss_ali_access_key = data.oss_ali_access_key
						that.oss_ali_access_secret = data.oss_ali_access_secret
						that.oss_ali_source = data.oss_ali_source
						that.oss_ali_style_rule = data.oss_ali_style_rule
						that.oss_ali_thumbnail_style_rule = data.oss_ali_thumbnail_style_rule
						
						that.bos_baidu_domain_protocol = data.bos_baidu_domain_protocol
						// 把服务端的数据和bos_baidu_domain_protocol比对，获得对应的选项索引
						that.bos_baidu_domain_protocolIndex = that.bos_baidu_domain_protocolValue.indexOf(
							that.bos_baidu_domain_protocol)
						that.bos_baidu_domain = data.bos_baidu_domain
						that.bos_baidu_bucket_name = data.bos_baidu_bucket_name
						that.bos_baidu_endpoint = data.bos_baidu_endpoint
						that.bos_baidu_access_key = data.bos_baidu_access_key
						that.bos_baidu_secret_key = data.bos_baidu_secret_key
						that.bos_baidu_style_rule = data.bos_baidu_style_rule
						that.bos_baidu_thumbnail_style_rule = data.bos_baidu_thumbnail_style_rule
						
						
						that.cos_tencent_domain_protocol = data.cos_tencent_domain_protocol
						// 把服务端的数据和cos_tencent_domain_protocol比对，获得对应的选项索引
						that.cos_tencent_domain_protocolIndex = that.cos_tencent_domain_protocolValue.indexOf(
							that.cos_tencent_domain_protocol)
						that.cos_tencent_domain = data.cos_tencent_domain
						that.cos_tencent_bucket_name = data.cos_tencent_bucket_name
						that.cos_tencent_region = data.cos_tencent_region
						// 把服务端的数据和cos_tencent_region比对，获得对应的选项索引
						that.cos_tencent_regionIndex = that.cos_tencent_regionValue.indexOf(
							that.cos_tencent_region)
						that.cos_tencent_secret_id = data.cos_tencent_secret_id
						that.cos_tencent_secret_key = data.cos_tencent_secret_key
						that.cos_tencent_source = data.cos_tencent_source
						that.cos_tencent_style_rule = data.cos_tencent_style_rule
						that.cos_tencent_thumbnail_style_rule = data.cos_tencent_thumbnail_style_rule
						
						
						that.obs_huawei_domain_protocol = data.obs_huawei_domain_protocol
						// 把服务端的数据和obs_huawei_domain_protocol比对，获得对应的选项索引
						that.obs_huawei_domain_protocolIndex = that.obs_huawei_domain_protocolValue.indexOf(
							that.obs_huawei_domain_protocol)
						that.obs_huawei_domain = data.obs_huawei_domain
						that.obs_huawei_bucket_name = data.obs_huawei_bucket_name
						that.obs_huawei_endpoint = data.obs_huawei_endpoint
						that.obs_huawei_access_key = data.obs_huawei_access_key
						that.obs_huawei_access_secret = data.obs_huawei_access_secret
						that.obs_huawei_source = data.obs_huawei_source
						that.obs_huawei_style_rule = data.obs_huawei_style_rule
						that.obs_huawei_thumbnail_style_rule = data.obs_huawei_thumbnail_style_rule
						that.minio_endpoint = data.minio_endpoint
						that.minio_bucket_name = data.minio_bucket_name
						that.minio_region = data.minio_region
						that.minio_access_key = data.minio_access_key
						that.minio_access_secret = data.minio_access_secret
						that.minio_source = data.minio_source
					},
					fail: function(e) {
						uni.stopPullDownRefresh()
						uni.showModal({
							title: "获取数据失败",
							content: e.errMsg
						})
					}
				})
			},

			/**
			 * 上传图片时预览switch改变事件
			 * @param {Object} e
			 */
			attachmentUploadImagePreviewEnableSwitchChange: function(e) {
				this.attachment_upload_image_preview_enable = e.detail.value
			},

			/**
			 * 存储位置选项更改事件
			 * @param {Object} e
			 */
			attachmentTypeChange: function(e) {
				this.attachment_typeIndex = e.detail.value
			},

			/**
			 * 又拍云绑定域名协议选项更改事件
			 * @param {Object} e
			 */
			ossUpyunDomainProtocolChange: function(e) {
				this.oss_upyun_domain_protocolIndex = e.detail.value
			},

			/**
			 * 七牛云绑定域名协议选项更改事件
			 * @param {Object} e
			 */
			ossQiniuDomainProtocolChange: function(e) {
				this.oss_qiniu_domain_protocolIndex = e.detail.value
			},

			/**
			 * 七牛云区域选项更改事件
			 * @param {Object} e
			 */
			ossQiniuZoneChange: function(e) {
				this.oss_qiniu_zoneIndex = e.detail.value
			},
			
			/**
			 * 阿里云绑定域名协议选项更改事件
			 * @param {Object} e
			 */
			ossAliDomainProtocolChange: function(e) {
				this.oss_ali_domain_protocolIndex = e.detail.value
			},


			/**
			 * 百度云绑定域名协议选项更改事件
			 * @param {Object} e
			 */
			bosBaiduDomainProtocolChange: function(e) {
				this.bos_baidu_domain_protocolIndex = e.detail.value
			},

			/**
			 * 腾讯云绑定域名协议选项更改事件
			 * @param {Object} e
			 */
			cosTencentDomainProtocolChange: function(e) {
				this.cos_tencent_domain_protocolIndex = e.detail.value
			},
			
			/**
			 * 腾讯云区域选项更改事件
			 * @param {Object} e
			 */
			cosTencentRegionChange: function(e) {
				this.cos_tencent_regionIndex = e.detail.value
			},
			
			/**
			 * 华为云绑定域名协议选项更改事件
			 * @param {Object} e
			 */
			obsHhuaweiDomainProtocolChange: function(e) {
				this.obs_huawei_domain_protocolIndex = e.detail.value
			},

			/**
			 * 保存按钮事件
			 */
			saving: function() {
				let type = this.attachment_typeText[this.attachment_typeIndex]
				switch (type) {
					case "SM.MS":
						if (this.smms_api_secret_token.length <= 0) {
							this.popup("Secret Token 不能为空！")
							return 
						}
						break;
					case "又拍云":
						if (this.oss_upyun_domain.length <= 0) {
							this.popup("绑定域名不能为空！")
							return 
						}
						if (this.oss_upyun_bucket.length <= 0) {
							this.popup("空间名称不能为空！")
							return 
						}
						if (this.oss_upyun_operator.length <= 0) {
							this.popup("操作员名称不能为空！")
							return 
						}
						if (this.oss_upyun_password.length <= 0) {
							this.popup("操作员密码不能为空！")
							return 
						}
						if (this.oss_upyun_source.length <= 0) {
							this.popup("文件目录不能为空！")
							return 
						}
						break;
					case "七牛云":
						if (this.oss_qiniu_domain.length <= 0) {
							this.popup("绑定域名不能为空！")
							return 
						}
						if (this.oss_qiniu_access_key.length <= 0) {
							this.popup("Access Key 不能为空！")
							return 
						}
						if (this.oss_qiniu_secret_key.length <= 0) {
							this.popup("Secret Key 不能为空！")
							return 
						}
						if (this.oss_qiniu_bucket.length <= 0) {
							this.popup("Bucket 不能为空！")
							return 
						}
						break;
					case "阿里云":
						if (this.oss_ali_bucket_name.length <= 0) {
							this.popup("Bucket 不能为空！")
							return 
						}
						if (this.oss_ali_endpoint.length <= 0) {
							this.popup("EndPoint（地域节点） 不能为空！")
							return 
						}
						if (this.oss_ali_access_key.length <= 0) {
							this.popup("Access Key 不能为空！")
							return 
						}
						if (this.oss_ali_access_secret.length <= 0) {
							this.popup("Access Secret 不能为空！")
							return 
						}
						break;
					case "百度云":
						if (this.bos_baidu_bucket_name.length <= 0) {
							this.popup("Bucket 不能为空！")
							return 
						}
						if (this.bos_baidu_endpoint.length <= 0) {
							this.popup("EndPoint（地域节点） 不能为空！")
							return 
						}
						if (this.bos_baidu_access_key.length <= 0) {
							this.popup("Access Key 不能为空！")
							return 
						}
						if (this.bos_baidu_secret_key.length <= 0) {
							this.popup("Access Secret 不能为空！")
							return 
						}
						break;
					case "腾讯云":
						if (this.cos_tencent_bucket_name.length <= 0) {
							this.popup("Bucket 不能为空！")
							return 
						}
						if (this.cos_tencent_secret_id.length <= 0) {
							this.popup("Secret Id 不能为空！")
							return 
						}
						if (this.cos_tencent_secret_key.length <= 0) {
							this.popup("Secret Key 不能为空！")
							return 
						}
						break;
					case "华为云":
						if (this.obs_huawei_bucket_name.length <= 0) {
							this.popup("Bucket 不能为空！")
							return 
						}
						if (this.obs_huawei_endpoint.length <= 0) {
							this.popup("EndPoint（终端节点） 不能为空！")
							return 
						}
						if (this.obs_huawei_access_key.length <= 0) {
							this.popup("Access Key 不能为空！")
							return 
						}
						if (this.obs_huawei_access_secret.length <= 0) {
							this.popup("Accesss Secret 不能为空！")
							return 
						}
						break;
					case "MinIO":
						if (this.minio_endpoint.length <= 0) {
							this.popup("EndPoint（终端节点） 不能为空！")
							return 
						}
						if (this.minio_bucket_name.length <= 0) {
							this.popup("Bucket 不能为空！")
							return 
						}
						if (this.minio_access_key.length <= 0) {
							this.popup("Access Key 不能为空！")
							return 
						}
						if (this.minio_access_secret.length <= 0) {
							this.popup("Accesss Secret 不能为空！")
							return 
						}
						break;
				}
				
				
				let json = {
					"attachment_upload_image_preview_enable": this.attachment_upload_image_preview_enable,
					"attachment_upload_max_files": this.attachment_upload_max_files,
					"attachment_upload_max_parallel_uploads":this.attachment_upload_max_parallel_uploads,
					
					"attachment_type": 
						this.attachment_typeValue[this.attachment_typeIndex],
					
				
					"smms_api_secret_token": this.smms_api_secret_token,
					
					"oss_upyun_domain_protocol": 
					this.oss_upyun_domain_protocolValue[this.oss_upyun_domain_protocolIndex],
					"oss_upyun_domain": this.oss_upyun_domain,
					"oss_upyun_bucket": this.oss_upyun_bucket,
					"oss_upyun_operator": this.oss_upyun_operator,
					"oss_upyun_password": this.oss_upyun_password,
					"oss_upyun_source": this.oss_upyun_source,
					"oss_upyun_style_rule": this.oss_upyun_style_rule,
					"oss_upyun_thumbnail_style_rule": this.oss_upyun_thumbnail_style_rule,
					
					
					"oss_qiniu_domain_protocol": 
						this.oss_qiniu_domain_protocolValue[this.oss_qiniu_domain_protocolIndex],
					"oss_qiniu_domain": this.oss_qiniu_domain,
					"oss_qiniu_zone": 
						this.oss_qiniu_zoneValue[this.oss_qiniu_zoneIndex],
					"oss_qiniu_access_key": this.oss_qiniu_access_key,
					"oss_qiniu_secret_key": this.oss_qiniu_secret_key,
					"oss_qiniu_source": this.oss_qiniu_source,
					"oss_qiniu_bucket": this.oss_qiniu_bucket,
					"oss_qiniu_style_rule": this.oss_qiniu_style_rule,
					"oss_qiniu_thumbnail_style_rule": this.oss_qiniu_thumbnail_style_rule,
					
					
					"oss_ali_domain_protocol": 
						this.oss_ali_domain_protocolValue[this.oss_ali_domain_protocolIndex],
					"oss_ali_domain": this.oss_ali_domain,
					"oss_ali_bucket_name": this.oss_ali_bucket_name,
					"oss_ali_endpoint": this.oss_ali_endpoint,
					"oss_ali_access_key": this.oss_ali_access_key,
					"oss_ali_access_secret": this.oss_ali_access_secret,
					"oss_ali_source": this.oss_ali_source,
					"oss_ali_style_rule": this.oss_ali_style_rule,
					"oss_ali_thumbnail_style_rule": this.oss_ali_thumbnail_style_rule,
					
								
					"bos_baidu_domain_protocol": 
						this.bos_baidu_domain_protocolValue[this.bos_baidu_domain_protocolIndex],
					"bos_baidu_domain": this.bos_baidu_domain,
					"bos_baidu_bucket_name": this.bos_baidu_bucket_name,
					"bos_baidu_endpoint": this.bos_baidu_endpoint,
					"bos_baidu_access_key": this.bos_baidu_access_key,
					"bos_baidu_secret_key": this.bos_baidu_secret_key,
					"bos_baidu_style_rule": this.bos_baidu_style_rule,
					"bos_baidu_thumbnail_style_rule": this.bos_baidu_thumbnail_style_rule,
					
					
					"cos_tencent_domain_protocol": 
						this.cos_tencent_domain_protocolValue[this.cos_tencent_domain_protocolIndex],
					"cos_tencent_domain": this.cos_tencent_domain,
					"cos_tencent_bucket_name": this.cos_tencent_bucket_name,
					"cos_tencent_region": 
						this.cos_tencent_regionValue[this.cos_tencent_regionIndex],
					"cos_tencent_secret_id": this.cos_tencent_secret_id,
					"cos_tencent_secret_key": this.cos_tencent_secret_key,
					"cos_tencent_source": this.cos_tencent_source,
					"cos_tencent_style_rule": this.cos_tencent_style_rule,
					"cos_tencent_thumbnail_style_rule": this.cos_tencent_thumbnail_style_rule,
					
					
					"obs_huawei_domain_protocol": 
						this.obs_huawei_domain_protocolValue[this.obs_huawei_domain_protocolIndex],
					"obs_huawei_domain": this.obs_huawei_domain,
					"obs_huawei_bucket_name": this.obs_huawei_bucket_name,
					"obs_huawei_endpoint": this.obs_huawei_endpoint,
					"obs_huawei_access_key": this.obs_huawei_access_key,
					"obs_huawei_access_secret": this.obs_huawei_access_secret,
					"obs_huawei_source": this.obs_huawei_source,
					"obs_huawei_style_rule": this.obs_huawei_style_rule,
					"obs_huawei_thumbnail_style_rule": this.obs_huawei_thumbnail_style_rule,
					
					"minio_endpoint": this.minio_endpoint,
					"minio_bucket_name": this.minio_bucket_name,
					"minio_region": this.minio_region,
					"minio_access_key": this.minio_access_key,
					"minio_access_secret": this.minio_access_secret,
					"minio_source": this.minio_source,
				}
				let that = this
				uni.request({
					method: "POST",
					dataType: "json",
					url: this.getUrl() + "/api/admin/options/map_view/saving",
					header: {
						"Content-Type": "application/json",
						"ADMIN-Authorization": this.getAccessToken()
					},
					data: json,
					success: function(res) {
						if (res.statusCode !== 200) {
							that.popup("保存失败：" + res.statusCode)
							// 登录过期
							if (that.isExpiredByRequest(res)) {
								that.popup("保存失败，登录已过期，请重新登陆")
							}
							return
						}
						that.popup("保存成功", "success")
						that.refreshData()
					},
					fail: function(e) {
						uni.showModal({
							title: "保存数据失败",
							content: e.errMsg
						})
					}
				})
			},


			/**
			 * popup弹出层
			 */
			popup: function(message, type = "error") {
				this.popupMessage = message
				this.popupType = type
				this.$refs.popup.open()
			},
		}
	}
</script>

<style>
	.container {
		padding-bottom: 40px;
	}

	.block {
		padding: 10px;
	}

	.save-button {
		margin: 20rpx;
		margin-bottom: 30rpx;
	}
</style>
