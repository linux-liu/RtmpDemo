package com.liuxin.rtmpdemo;

//import cn.yunovo.nxos.settingsprovider.zenconfig.ZenChoiceType;
//import cn.yunovo.nxos.settingsprovider.zenconfig.ZenConfig;
//import cn.yunovo.nxos.settingsprovider.zenconfig.ZenRequiredType;
//import cn.yunovo.nxos.settingsprovider.zenconfig.ZenValueType;

public class ZenConstant {
//	public static final String CUSTOMER_NUMBER = "wachat.customer_number";
//
//	@ZenConfig(
//			moduleName = "settings",
//			moduleNameDes = "系统设置",
//			submoduleName = "link",
//			submoduleNameDes = "链接",
//			categoryName = "wlan",
//			categoryNameDes = "WLAN选项",
//			subcategoryName = "",
//			subcategoryNameDes = "",
//			propertyName = "remove",
//			propertyNameDes = "'系统设置->链接->WLAN选项' 是否显示",
//			choiceType = ZenChoiceType.SINGLE_CHECKBOX,
//			valueType = ZenValueType.PREDEFINE_BOOLEAN,
//			defvalue = "0",
//			required = ZenRequiredType.YES,
//			relativedApp = "com.spt.carengine.settings",
//			miniversion = "1.05.110",
//			platform = "mtk",
//			value = {"1","0"},
//			valueDes = {"隐藏'WLAN'选项","显示'WLAN'选项"})
//	public static final String LINK_WLAN = "settings.link.wlan.remove";
//
//	@ZenConfig(
//			moduleName = "settings",
//			moduleNameDes = "系统设置",
//			submoduleName = "link",
//			submoduleNameDes = "链接",
//			categoryName = "wlan_hotspot",
//			categoryNameDes = "WLAN热点",
//			subcategoryName = "",
//			subcategoryNameDes = "",
//			propertyName = "remove",
//			propertyNameDes = "'系统设置->链接->WLAN热点' 是否显示",
//			choiceType = ZenChoiceType.SINGLE_CHECKBOX,
//			valueType = ZenValueType.PREDEFINE_BOOLEAN,
//			defvalue = "0",
//			required = ZenRequiredType.YES,
//			relativedApp = "com.spt.carengine.settings",
//			miniversion = "1.05.110",
//			platform = "mtk",
//			value = {"1","0"},
//			valueDes = {"隐藏'WLAN热点'选项","显示'WLAN热点'选项"})
//	public static final String LINK_WLAN_HOTSPOT = "settings.link.wlan_hotspot.remove";
//
//	@ZenConfig(
//			moduleName = "settings",
//			moduleNameDes = "系统设置",
//			submoduleName = "link",
//			submoduleNameDes = "链接",
//			categoryName = "mobile",
//			categoryNameDes = "移动网络",
//			subcategoryName = "",
//			subcategoryNameDes = "",
//			propertyName = "remove",
//			propertyNameDes = "'系统设置->链接->移动网络' 是否显示",
//			choiceType = ZenChoiceType.SINGLE_CHECKBOX,
//			valueType = ZenValueType.PREDEFINE_BOOLEAN,
//			defvalue = "0",
//			required = ZenRequiredType.NO,
//			relativedApp = "com.spt.carengine.settings",
//			miniversion = "1.05.110",
//			platform = "mtk",
//			value = {"1","0"},
//			valueDes = {"隐藏'移动网络'选项","显示'移动网络'选项"})
//	public static final String LINK_MOBILE = "settings.link.mobile.remove";
//
//	@ZenConfig(
//			moduleName = "settings",
//			moduleNameDes = "系统设置",
//			submoduleName = "link",
//			submoduleNameDes = "链接",
//			categoryName = "traffic",
//			categoryNameDes = "流量管理",
//			subcategoryName = "",
//			subcategoryNameDes = "",
//			propertyName = "remove",
//			propertyNameDes = "'系统设置->链接->流量管理' 是否显示",
//			choiceType = ZenChoiceType.SINGLE_CHECKBOX,
//			valueType = ZenValueType.PREDEFINE_BOOLEAN,
//			defvalue = "0",
//			required = ZenRequiredType.NO,
//			relativedApp = "com.spt.carengine.settings",
//			miniversion = "1.05.110",
//			platform = "mtk",
//			value = {"1","0"},
//			valueDes = {"隐藏'流量管理'选项","显示'流量管理选'项"})
//	public static final String LINK_TRAFFIC = "settings.link.traffic.remove";
//
//	@ZenConfig(
//			moduleName = "settings",
//			moduleNameDes = "系统设置",
//			submoduleName = "link",
//			submoduleNameDes = "链接",
//			categoryName = "bt_phone",
//			categoryNameDes = "蓝牙电话",
//			subcategoryName = "",
//			subcategoryNameDes = "",
//			propertyName = "remove",
//			propertyNameDes = "'系统设置->链接->蓝牙电话' 是否显示",
//			choiceType = ZenChoiceType.SINGLE_CHECKBOX,
//			valueType = ZenValueType.PREDEFINE_BOOLEAN,
//			defvalue = "0",
//			required = ZenRequiredType.NO,
//			relativedApp = "com.spt.carengine.settings",
//			miniversion = "1.05.110",
//			platform = "mtk",
//			value = {"1","0"},
//			valueDes = {"隐藏'蓝牙电话'选项","显示'蓝牙电话'选项"})
//	public static final String LINK_BT_PHONE = "settings.link.bt_phone.remove";
//
//	@ZenConfig(
//			moduleName = "settings",
//			moduleNameDes = "系统设置",
//			submoduleName = "link",
//			submoduleNameDes = "链接",
//			categoryName = "bt_slave_mode",
//			categoryNameDes = "蓝牙电话",
//			subcategoryName = "",
//			subcategoryNameDes = "",
//			propertyName = "show",
//			propertyNameDes = "'系统设置->链接->主从蓝牙模式切换' 是否显示",
//			choiceType = ZenChoiceType.SINGLE_CHECKBOX,
//			valueType = ZenValueType.PREDEFINE_BOOLEAN,
//			defvalue = "0",
//			required = ZenRequiredType.NO,
//			relativedApp = "com.spt.carengine.settings",
//			miniversion = "1.05.110",
//			platform = "mtk",
//			value = {"1","0"},
//			valueDes = {"显示'主从蓝牙模式切换'选项","隐藏'主从蓝牙模式切换'选项"})
//	public static final String LINK_BT_MASTER_SLAVE_MODE = "settings.link.bt_slave_mode.show";
//
//	@ZenConfig(
//			moduleName = "settings",
//			moduleNameDes = "系统设置",
//			submoduleName = "link",
//			submoduleNameDes = "链接",
//			categoryName = "bt_master_mode",
//			categoryNameDes = "蓝牙电话",
//			subcategoryName = "",
//			subcategoryNameDes = "",
//			propertyName = "show",
//			propertyNameDes = "'系统设置->链接->主蓝牙模式' 是否显示",
//			choiceType = ZenChoiceType.SINGLE_CHECKBOX,
//			valueType = ZenValueType.PREDEFINE_BOOLEAN,
//			defvalue = "0",
//			required = ZenRequiredType.NO,
//			relativedApp = "com.spt.carengine.settings",
//			miniversion = "1.05.110",
//			platform = "mtk",
//			value = {"1","0"},
//			valueDes = {"显示'主蓝牙模式'选项","隐藏'主蓝牙模式'选项"})
//	public static final String LINK_BT_MASTER_MODE = "settings.link.bt_master_mode.show";
//
//	@ZenConfig(
//			moduleName = "settings",
//			moduleNameDes = "系统设置",
//			submoduleName = "system",
//			submoduleNameDes = "系统",
//			categoryName = "system_update",
//			categoryNameDes = "系统更新",
//			subcategoryName = "",
//			subcategoryNameDes = "",
//			propertyName = "remove",
//			propertyNameDes = "'系统设置->系统->系统更新' 是否显示",
//			choiceType = ZenChoiceType.SINGLE_CHECKBOX,
//			valueType = ZenValueType.PREDEFINE_BOOLEAN,
//			defvalue = "0",
//			required = ZenRequiredType.YES,
//			relativedApp = "com.spt.carengine.settings",
//			miniversion = "1.05.110",
//			platform = "mtk",
//			value = {"1","0"},
//			valueDes = {"隐藏'系统更新'选项","显示'系统更新'选项"})
//	public static final String SYSTEM_SYSTEM_UPDATE = "settings.system.system_update.remove";
//
//	@ZenConfig(
//			moduleName = "settings",
//			moduleNameDes = "系统设置",
//			submoduleName = "system",
//			submoduleNameDes = "系统",
//			categoryName = "about_device",
//			categoryNameDes = "关于本机",
//			subcategoryName = "",
//			subcategoryNameDes = "",
//			propertyName = "remove",
//			propertyNameDes = "'系统设置->系统->关于本机' 是否显示",
//			choiceType = ZenChoiceType.SINGLE_CHECKBOX,
//			valueType = ZenValueType.PREDEFINE_BOOLEAN,
//			defvalue = "0",
//			required = ZenRequiredType.YES,
//			relativedApp = "com.spt.carengine.settings",
//			miniversion = "1.05.110",
//			platform = "mtk",
//			value = {"1","0"},
//			valueDes = {"隐藏'关于本机'选项","显示'关于本机'选项"})
//	public static final String SYSTEM_ABOUT_DEVICE = "settings.system.about_device.remove";
//
//	@ZenConfig(
//			moduleName = "settings",
//			moduleNameDes = "系统设置",
//			submoduleName = "system",
//			submoduleNameDes = "系统",
//			categoryName = "reset_factory",
//			categoryNameDes = "恢复出厂设置",
//			subcategoryName = "",
//			subcategoryNameDes = "",
//			propertyName = "remove",
//			propertyNameDes = "'系统设置->系统->恢复出厂设置' 是否显示",
//			choiceType = ZenChoiceType.SINGLE_CHECKBOX,
//			valueType = ZenValueType.PREDEFINE_BOOLEAN,
//			defvalue = "0",
//			required = ZenRequiredType.YES,
//			relativedApp = "com.spt.carengine.settings",
//			miniversion = "1.05.110",
//			platform = "mtk",
//			value = {"1","0"},
//			valueDes = {"隐藏'恢复出厂设置'选项","显示'恢复出厂设置'选项"})
//	public static final String SYSTEM_RESET_FACTORY = "settings.system.reset_factory.remove";
}
