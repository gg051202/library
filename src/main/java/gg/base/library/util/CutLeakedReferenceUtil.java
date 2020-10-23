package gg.base.library.util;


import android.app.Activity;
import android.app.FragmentController;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CutLeakedReferenceUtil {
    public static final String TAG = "剪断内存泄露的引用";

    /**
     * 剪短可能引起内存泄露的东西
     */
    public static void cut(Activity activity) {
        CutLeakedReferenceUtil.fixInputMethodManagerLeak(activity);
        CutLeakedReferenceUtil.fixHuaweiInputMethodManagerBugs(activity);
        CutLeakedReferenceUtil.fixHuaWeiGestureBoostManager(activity);
        CutLeakedReferenceUtil.sendCutLeaksMessage(activity.getMainLooper());
        CutLeakedReferenceUtil.removeFragments(activity);
    }

    /**
     * 修复内存泄漏
     */
    private static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

//        String[] arr = new String[]{"mCurRootView", "mServedView", "mNextServedView"};
        String[] arr = new String[]{"mServedView"};
        Field f;
        Object obj_get;
        for (String param : arr) {
            try {
                f = imm.getClass().getDeclaredField(param);
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                }
                obj_get = f.get(imm);
                if (obj_get instanceof View) {
                    f.set(imm, null);
                    LL.i(TAG, param + "has been set to null");

                }
            } catch (Throwable e) {
                LL.i(e.getMessage());
            }
        }
    }

    private static void fixHuaweiInputMethodManagerBugs(Activity paramActivity) {
        if (!"huawei".equalsIgnoreCase(Build.MANUFACTURER)) {
//            LL.i(TAG, "不是华为手机");
            return;
        }
        if (paramActivity == null) {
            return;
        }
        int count = 0;
        while (true) {
            //给个5次机会 省得无限循环
            count++;
            if (count == 5) return;
            try {
                InputMethodManager localInputMethodManager = (InputMethodManager) paramActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (localInputMethodManager != null) {
                    Method localMethod = InputMethodManager.class.getMethod("windowDismissed", new Class[]{IBinder.class});
                    localMethod.invoke(localInputMethodManager, new Object[]{paramActivity.getWindow().getDecorView().getWindowToken()});
                    Field mLastSrvView = InputMethodManager.class.getDeclaredField("mLastSrvView");
                    mLastSrvView.setAccessible(true);
                    mLastSrvView.set(localInputMethodManager, null);
//                    LL.i(TAG, "fixHuaweiInputMethodManagerBugs ->mLastSrvView successfully");
                    return;
                }
            } catch (Exception e) {
//                LL.i(TAG, "fixHuaweiInputMethodManagerBugs fail:" + e.getMessage());
            }
        }
    }


    /**
     * 修复华为手机内存的泄露
     */
    private static void fixHuaWeiGestureBoostManager(Activity activity) {
        if (!"huawei".equalsIgnoreCase(Build.MANUFACTURER)) {
//            LL.i(TAG, "不是华为手机");
            return;
        }
        try {
            Class<?> GestureBoostManagerClass = Class.forName("android.gestureboost.GestureBoostManager");
            Field sGestureBoostManagerField = GestureBoostManagerClass.getDeclaredField("sGestureBoostManager");
            sGestureBoostManagerField.setAccessible(true);
            Object gestureBoostManager = sGestureBoostManagerField.get(GestureBoostManagerClass);
            Field contextField = GestureBoostManagerClass.getDeclaredField("mContext");
            contextField.setAccessible(true);
            if (contextField.get(gestureBoostManager) == activity) {
                contextField.set(gestureBoostManager, null);
//                LL.i(TAG, "fixHuaWeiGestureBoostManager successfully");
            }
        } catch (ClassNotFoundException e) {
            LL.i(TAG, "fixHuaWeiGestureBoostManager：" + e.toString());
        } catch (NoSuchFieldException e) {
            LL.i(TAG, "fixHuaWeiGestureBoostManager：" + e.toString());
        } catch (IllegalAccessException e) {
            LL.i(TAG, "fixHuaWeiGestureBoostManager：" + e.toString());
        } catch (Throwable t) {
            LL.i(TAG, "fixHuaWeiGestureBoostManager：" + t.toString());
        }

    }


    private static void removeFragments(Activity activity) {
        Class<?> activityClass;
        try {
            activityClass = Activity.class;

            Field mFragmentsField = activityClass.getDeclaredField("mFragments");
            mFragmentsField.setAccessible(true);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                FragmentController mFragments = (FragmentController) mFragmentsField.get(activity);
                mFragments = null;
//                LL.i(TAG, "removeFragments 成功");
            }
        } catch (IllegalAccessException e) {
            LL.i(TAG, "removeFragments异常：" + e.toString());
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            LL.i(TAG, "removeFragments异常：" + e.toString());
        }
    }


    /**
     * 发送剪断消息队列的请求
     */
    private static void sendCutLeaksMessage(Looper looper) {
        final Handler handler = new Handler(looper);
        handler.post(() ->
                Looper.myQueue().addIdleHandler(() -> {
                    handler.sendMessageDelayed(handler.obtainMessage(), 1000);
                    return true;
                }));
    }
}