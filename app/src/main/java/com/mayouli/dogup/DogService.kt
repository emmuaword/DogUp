package com.mayouli.dogup

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import androidx.core.os.postDelayed
import java.util.concurrent.Executors

class DogService : AccessibilityService() {

    private val TAG: String = "dog_up"

    private var mLastStateTime: Long = 0
    private var mHasDog: Boolean = false
    private var uiHandler: Handler = Handler(Looper.getMainLooper())

    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d(TAG, event?.eventType.toString())
        if (event?.eventType == TYPE_WINDOW_STATE_CHANGED) {
            if (event.source != null) {
//                hasDogInfo(event.source)
//                if (mHasDog) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - mLastStateTime > 1 * 1000) {
                    mLastStateTime = currentTime
                    Log.d(TAG, "开始做任务")
                    performSth(0, event.source)
                }
//                }

//            }
            }
        }
    }


    private fun hasDogInfo(rootNode: AccessibilityNodeInfo) {
        if (rootNode.childCount > 0) {
            for (i in 0 until rootNode.childCount) {
                if (rootNode.getChild(i) != null) {
                    hasDogInfo(rootNode.getChild(i))
                }
            }
        } else {
            if (rootNode.text != null) {
//                Log.d(TAG, "text-->" + rootNode.text)
                if (rootNode.text.toString().contains("11.11 20点兑换分红哦")) {
                    mHasDog = true
                }
            }
        }
    }


    var isFind = false

    /**
     * 用于跳过邀请好友的那一步
     */
    var isFirstFind = true

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun performSth(index: Int, rootNode: AccessibilityNodeInfo) {

        if (rootNode.childCount > 0) {
            for (i in 0 until rootNode.childCount) {
                if (rootNode.getChild(i) != null) {
                    if (!isFind) {
                        performSth(i, rootNode.getChild(i))
                    }
                }
            }
        } else {
            if (rootNode.text != null && TextUtils.isEmpty(rootNode.text).not()) {
                if (rootNode.text.contains("浏览8秒") ||
                    rootNode.text.contains("浏览并关注") ||
                    rootNode.text.contains("浏览可得")
                ) {
                    if (Params.isSkip && isFirstFind) {
                        isFirstFind = false
                        return
                    }
                    var viewTime = 15 * 1000
                    if (rootNode.text.contains("浏览可得")) {
                        viewTime = 4 * 1000
                    }
                    val toFinishNode = rootNode.parent.getChild(index + 1)
                    if (toFinishNode.text.contains("已完成")) {
                        return
                    }
                    Log.d(TAG, "find-->" + rootNode.text + "  " + toFinishNode.text)
                    isFind = true
                    toFinishNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    uiHandler.postDelayed(Runnable {
                        isFind = false
                        isFirstFind = true
                        performGlobalAction(GLOBAL_ACTION_BACK)
                    }, viewTime.toLong())
                    return
                }
            }
        }
    }

}