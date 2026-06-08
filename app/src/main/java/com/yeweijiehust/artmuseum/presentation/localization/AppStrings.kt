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
    val timeout: String,
    val unreachable: String,
    val serverUnavailable: String,
    val invalidResponse: String,
    val rateLimited: String,
    val invalidCredentials: String,
    val emailExists: String,
    val unauthorized: String,
    val forbidden: String,
    val notFound: String,
    val invalidRequest: String,
    val fileRequired: String,
    val titleRequired: String,
    val storageFailure: String,
    val invalidEndpoint: String,
    val genericError: String,
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
    "Image must be 10 MiB or smaller", "Choose a JPEG, PNG, or WebP image",
    "No network connection. Reconnect and try again",
    "The request took too long. Check your connection and try again",
    "Could not reach the Art Museum service. Check your connection or API endpoint",
    "The Art Museum service is temporarily unavailable. Try again shortly",
    "The server returned an unexpected response. Try again later",
    "Too many requests were sent. Wait a moment and try again",
    "Email or password is incorrect", "An account already exists for this email. Try logging in instead",
    "Your session has expired. Log in to continue", "You do not have permission to change this work",
    "This work no longer exists", "Some information is invalid. Review the fields and try again",
    "Choose an image before uploading", "Add a title before uploading",
    "The image service could not complete this request. Try again shortly",
    "Enter a valid Art Museum API address", "Something went wrong. Try again",
    "Connection saved", "Edit", "Back", "Close", "Load more"
)

private val Chinese = AppStrings(
    "艺术博物馆", "展览", "上传", "我的博物馆", "设置", "账户", "刷新", "重试", "加载中",
    "还没有分享的作品", "你的博物馆正等待第一件作品", "作者", "暂无描述", "尺寸", "文件大小", "格式",
    "登录", "注册", "退出登录", "邮箱", "密码", "显示名称", "标题", "描述", "无障碍文本", "选择图片",
    "上传图片", "保存", "删除", "取消", "删除这件作品？", "API 地址", "测试并保存", "语言", "跟随设备",
    "English", "中文", "尚未登录", "创建账户", "已有账户？", "必填", "请输入有效邮箱",
    "密码长度必须为 8–128 个字符", "图片不能超过 10 MiB", "请选择 JPEG、PNG 或 WebP 图片",
    "没有网络连接，请恢复连接后重试", "请求超时，请检查网络连接后重试",
    "无法连接到艺术博物馆服务，请检查网络连接或 API 地址", "艺术博物馆服务暂时不可用，请稍后重试",
    "服务器返回了无法识别的数据，请稍后重试", "请求过于频繁，请稍后再试",
    "邮箱或密码不正确", "该邮箱已注册，请直接登录", "登录状态已过期，请重新登录",
    "你没有权限修改这件作品", "这件作品已不存在", "部分信息无效，请检查后重试",
    "请先选择要上传的图片", "请先填写作品标题", "图片服务暂时无法完成请求，请稍后重试",
    "请输入有效的艺术博物馆 API 地址", "出现问题，请重试", "连接已保存",
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
