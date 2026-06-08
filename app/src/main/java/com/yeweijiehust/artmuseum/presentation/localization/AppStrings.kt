package com.yeweijiehust.artmuseum.presentation.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import com.yeweijiehust.artmuseum.domain.model.AppLanguage

data class AppStrings(
    val appName: String,
    val gallery: String,
    val upload: String,
    val myMuseum: String,
    val settings: String,
    val account: String,
    val refresh: String,
    val retry: String,
    val loading: String,
    val emptyGallery: String,
    val emptyMine: String,
    val by: String,
    val noDescription: String,
    val dimensions: String,
    val fileSize: String,
    val format: String,
    val login: String,
    val register: String,
    val logout: String,
    val email: String,
    val password: String,
    val displayName: String,
    val title: String,
    val description: String,
    val altText: String,
    val chooseImage: String,
    val uploadImage: String,
    val save: String,
    val delete: String,
    val cancel: String,
    val confirmDelete: String,
    val endpoint: String,
    val testAndSave: String,
    val language: String,
    val deviceLanguage: String,
    val english: String,
    val chinese: String,
    val signedOut: String,
    val createAccount: String,
    val haveAccount: String,
    val required: String,
    val invalidEmail: String,
    val passwordLength: String,
    val fileTooLarge: String,
    val invalidFileType: String,
    val offline: String,
    val unauthorized: String,
    val forbidden: String,
    val notFound: String,
    val connectionSaved: String,
    val edit: String,
    val back: String,
    val close: String,
    val loadMore: String
)

private val English = AppStrings(
    "Art Museum", "Gallery", "Upload", "My Museum", "Settings", "Account", "Refresh", "Retry",
    "Loading", "No works have been shared yet", "Your museum is waiting for its first work", "By",
    "No description", "Dimensions", "File size", "Format", "Log in", "Register", "Log out", "Email",
    "Password", "Display name", "Title", "Description", "Accessibility text", "Choose image",
    "Upload image", "Save", "Delete", "Cancel", "Delete this work?", "API endpoint", "Test and save",
    "Language", "Device", "English", "中文", "You are not signed in", "Create account",
    "Already have an account?", "Required", "Enter a valid email", "Password must be 8–128 characters",
    "Image must be 10 MiB or smaller", "Choose a JPEG, PNG, or WebP image", "You appear to be offline",
    "Please log in to continue", "You cannot change this work", "This work no longer exists",
    "Connection saved", "Edit", "Back", "Close", "Load more"
)

private val Chinese = AppStrings(
    "艺术博物馆", "展览", "上传", "我的博物馆", "设置", "账户", "刷新", "重试", "加载中",
    "还没有分享的作品", "你的博物馆正等待第一件作品", "作者", "暂无描述", "尺寸", "文件大小", "格式",
    "登录", "注册", "退出登录", "邮箱", "密码", "显示名称", "标题", "描述", "无障碍文本", "选择图片",
    "上传图片", "保存", "删除", "取消", "删除这件作品？", "API 地址", "测试并保存", "语言", "跟随设备",
    "English", "中文", "尚未登录", "创建账户", "已有账户？", "必填", "请输入有效邮箱",
    "密码长度必须为 8–128 个字符", "图片不能超过 10 MiB", "请选择 JPEG、PNG 或 WebP 图片",
    "当前似乎处于离线状态", "请先登录", "你不能修改这件作品", "这件作品已不存在", "连接已保存",
    "编辑", "返回", "关闭", "加载更多"
)

val LocalAppStrings = staticCompositionLocalOf { English }

@Composable
fun stringsFor(language: AppLanguage): AppStrings {
    val deviceChinese = LocalConfiguration.current.locales[0].language.startsWith("zh")
    return when (language) {
        AppLanguage.Device -> if (deviceChinese) Chinese else English
        AppLanguage.English -> English
        AppLanguage.Chinese -> Chinese
    }
}
