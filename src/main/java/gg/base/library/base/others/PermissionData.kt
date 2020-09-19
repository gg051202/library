package gg.base.library.base.others

internal class PermissionData {
    var permissionName: String? = null

    /**
     * 该权限是否拥有
     */
    var isGranted = false

    /**
     * 申请权限的结果，true表示用户通过，false表示用户未通过
     */
    var isResult = false
}