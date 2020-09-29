package com.lizhi.basemvplib;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

/**
 * Activity管理器,管理项目中Activity的状态
 */
public class AppActivityManager {

    private static Stack<AppCompatActivity> activityStack;
    private static AppActivityManager instance;

    private AppActivityManager() {
    }

    /**
     * 单一实例
     */
    public static AppActivityManager getAppManager() {
        if (instance == null) {
            synchronized (AppActivityManager.class) {
                if (instance == null) {
                    instance = new AppActivityManager();
                }
            }
        }
        return instance;
    }

    /**
     * 判断activity是否在栈顶
     */
    public boolean isChlidStackTop(AppCompatActivity activity) {
        return activityStack != null && activityStack.size() > 1 && activity == activityStack.peek();
    }

    /**
     * 判断指定Activity是否存在
     */
    public Boolean isExist(Class<?> activityClass) {
        boolean result = false;
        for (int i = 0; i < activityStack.size(); i++) {
            AppCompatActivity activity = activityStack.get(i);
            if (activity != null && activity.getClass() == activityClass) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(AppCompatActivity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public AppCompatActivity currentActivity() {
        return activityStack.lastElement();
    }

    /**
     * 移除指定的Activity
     */
    public void removeActivity(AppCompatActivity activity) {
        if (activity != null) {
            activityStack.remove(activity);
        }
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        AppCompatActivity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(AppCompatActivity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<? extends AppCompatActivity> cls) {
        for (AppCompatActivity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
                return;
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        finishAllActivity();
        System.exit(0);
    }

    /**
     * 当前Activity跳转另一个Activity
     *
     * @param otherActivity 跳转的Activity
     */
    public void toOtherActivity(Class<? extends AppCompatActivity> otherActivity) {
        toOtherActivity(otherActivity, null, null, Integer.MIN_VALUE);
    }

    /**
     * 当前Activity跳转另一个Activity要求返回值
     *
     * @param otherActivity 跳转的Activity
     * @param requestCode   请求码
     */
    public void toOtherActivity(Class<? extends AppCompatActivity> otherActivity, int requestCode) {
        toOtherActivity(otherActivity, null, null, requestCode);
    }

    /**
     * 当前Activity跳转另一个Activity携带一个Bundle
     *
     * @param otherActivity 跳转的Activity
     * @param bundle        Bundle
     */
    public void toOtherActivity(Class<? extends AppCompatActivity> otherActivity, Bundle bundle) {
        toOtherActivity(otherActivity, null, bundle, Integer.MIN_VALUE);
    }

    /**
     * 当前Activity跳转到另一个Activity携带Bundle并要求返回值
     *
     * @param otherActivity 跳转的Activity
     * @param bundle        Bundle
     * @param requestCode   请求码
     */
    public void toOtherActivity(Class<? extends AppCompatActivity> otherActivity, Bundle bundle, int requestCode) {
        toOtherActivity(otherActivity, null, bundle, requestCode);
    }

    /**
     * 当前Activity跳转另一个Activity携带值
     * 可携带常用基本类型和字符串类型
     *
     * @param otherActivity 要跳转的Activity
     * @param key           key
     * @param value         value
     */
    public <T> void toOtherActivity(Class<? extends AppCompatActivity> otherActivity, String key, T value) {
        toOtherActivity(otherActivity, key, value, Integer.MIN_VALUE);
    }

    /**
     * 当前Activity跳转另一个Activity携带值并要求返回值
     * 可携带常用基本类型和字符串类型
     *
     * @param otherActivity 要跳转的Activity
     * @param key           key
     * @param value         value
     * @param requestCode   请求码
     */
    public <T> void toOtherActivity(Class<? extends AppCompatActivity> otherActivity, String key, T value, int requestCode) {
        AppCompatActivity currentActivity = currentActivity();
        Intent intent = new Intent();
        if (value != null) {
            if (value.getClass().equals(Byte.class)) {
                intent.putExtra(key, Byte.parseByte(value.toString()));
            } else if (value.getClass().equals(Short.class)) {
                intent.putExtra(key, Short.parseShort(value.toString()));
            } else if (value.getClass().equals(Integer.class)) {
                intent.putExtra(key, Integer.parseInt(value.toString()));
            } else if (value.getClass().equals(Long.class)) {
                intent.putExtra(key, Long.parseLong(value.toString()));
            } else if (value.getClass().equals(Double.class)) {
                intent.putExtra(key, Float.parseFloat(value.toString()));
            } else if (value.getClass().equals(Float.class)) {
                intent.putExtra(key, Double.parseDouble(value.toString()));
            } else if (value.getClass().equals(Boolean.class)) {
                intent.putExtra(key, Boolean.parseBoolean(value.toString()));
            } else if (value.getClass().equals(String.class)) {
                intent.putExtra(key, value.toString());
            } else if (value.getClass().equals(Bundle.class)) {
                intent.putExtras((Bundle) value);
            }
        }
        intent.setClass(currentActivity, otherActivity);
        if (requestCode == Integer.MIN_VALUE) {
            currentActivity.startActivity(intent);
        } else {
            currentActivity.startActivityForResult(intent, requestCode);
        }
    }
}