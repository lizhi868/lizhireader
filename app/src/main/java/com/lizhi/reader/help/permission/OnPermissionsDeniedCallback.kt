package com.lizhi.reader.help.permission

interface OnPermissionsDeniedCallback {
    fun onPermissionsDenied(requestCode: Int, deniedPermissions: Array<String>)
}
