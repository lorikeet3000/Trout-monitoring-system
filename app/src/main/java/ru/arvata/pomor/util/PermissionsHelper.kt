package ru.arvata.pomor.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import java.lang.IllegalArgumentException

private const val TAG = "Permissions"

typealias PermissionsGrantedCallback = () -> Unit
typealias PermissionsRefusedCallback = (List<RefusedPermission>) -> Unit

fun android.support.v7.app.AppCompatActivity.askPermissions(vararg permissions: String,
                                                            onAllGrantedBlock: PermissionsGrantedCallback,
                                                            onRefusedBlock: PermissionsRefusedCallback
) {
    if(permissions.isEmpty()) {
        throw IllegalArgumentException("Please, request at least one permission")
    }

    PermissionsHelper(this, permissions, onAllGrantedBlock, onRefusedBlock)
}

class PermissionsHelper(activity: AppCompatActivity,
                        permissions: Array<out String>,
                        private val onAllGrantedBlock: PermissionsGrantedCallback,
                        private val onRefusedBlock: PermissionsRefusedCallback
) : PermissionListener {

    init {
        if(checkIfAllGranted(activity, permissions)) {
            onAllGranted()
        } else {
            val tag = "${activity.packageName}.PermissionsFragment"

            var fragment = activity.supportFragmentManager.findFragmentByTag(tag) as? PermissionsFragment
            if (fragment == null) {
                fragment = PermissionsFragment.newInstance(permissions)
                activity.supportFragmentManager
                    .beginTransaction()
                    .add(fragment, tag)
                    .commitNowAllowingStateLoss()
            }
            fragment.setPermissionsListener(this)
        }
    }

    override fun onAllGranted() {
        onAllGrantedBlock()
    }

    override fun onRefused(refused: List<RefusedPermission>) {
        onRefusedBlock(refused)
    }

    private fun checkIfAllGranted(context: Context, permissions: Array<out String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED ) {
                return false
            }
        }
        return true
    }
}

interface PermissionListener {
    fun onAllGranted()
    fun onRefused(refused: List<RefusedPermission>)
}

data class RefusedPermission(val name: String, val forever: Boolean)

class PermissionsFragment : Fragment() {
    private val ARG_PERMISSION_LIST = "permissions"
    private val REQUEST_CODE_PERMISSIONS = 999
    private var listener: PermissionListener? = null
    private var permissions: Array<out String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true
        arguments?.let {
            permissions = it.getStringArray(ARG_PERMISSION_LIST)
        }
    }

    fun setPermissionsListener(listener: PermissionListener) {
        this.listener = listener
    }

    override fun onResume() {
        super.onResume()
        permissions?.let {
            requestPermissions(it, REQUEST_CODE_PERMISSIONS)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == REQUEST_CODE_PERMISSIONS && permissions.isNotEmpty()) {
            val refusedList = mutableListOf<RefusedPermission>()
            permissions.forEachIndexed { index, name ->
                val result = grantResults[index]
                if(result != PackageManager.PERMISSION_GRANTED) {
                    val forever = !shouldShowRequestPermissionRationale(name)
                    refusedList.add(RefusedPermission(name, forever))
                }
            }

            if(refusedList.isEmpty()) {
                listener?.onAllGranted()
            } else {
                listener?.onRefused(refusedList)
            }

            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commitAllowingStateLoss()
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        @JvmStatic
        fun newInstance(permissions: Array<out String>) = PermissionsFragment().apply {
            arguments = Bundle().apply {
                putStringArray(ARG_PERMISSION_LIST, permissions)
            }
        }
    }
}
