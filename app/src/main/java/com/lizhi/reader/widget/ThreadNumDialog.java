package com.lizhi.reader.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.lizhi.reader.R;
import com.lizhi.reader.utils.ScreenUtils;
import com.lizhi.reader.widget.explosion_field.Utils;

import java.lang.reflect.Field;

public class ThreadNumDialog extends Dialog {

    private NumberPicker numberPicker;
    private int value;

    public interface DialogClickListener {
        void confirm(int num);

        void cancel();
    }

    private DialogClickListener listener;

    public ThreadNumDialog(@NonNull Context context) {
        this(context, R.style.MyDialog);
    }

    public ThreadNumDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public void init(int value) {
        this.value = value;
        setCancelable(false);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_thread_num, null);
        numberPicker = view.findViewById(R.id.number_picker);
        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
        TextView tvCancel = view.findViewById(R.id.tv_cancel);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(100);
        numberPicker.setValue(value);
        //关闭编辑模式
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumberPickerDividerColor(numberPicker);
        tvConfirm.setOnClickListener(v -> {
            this.value = numberPicker.getValue();
            dismiss();
            if (listener != null) {
                listener.confirm(numberPicker.getValue());
            }
        });
        tvCancel.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.cancel();
            }
        });
        setContentView(view);
    }

    @Override
    public void show() {
        super.show();
        numberPicker.setValue(value);
    }

    /**
     * 自定义滚动框分隔线颜色
     */
    private void setNumberPickerDividerColor(NumberPicker number) {
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值
                    pf.set(number, new ColorDrawable(ContextCompat.getColor(getContext(), R.color.color_08000000)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            // 分割线高度
//            if (pf.getName().equals("mSelectionDividerHeight")) {
//                pf.setAccessible(true);
//                try {
//                    int result = 1;
//                    pf.set(number, result);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = ScreenUtils.getAppSize()[0] - Utils.dp2Px(40);
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
        }
    }

    public void setListener(DialogClickListener listener) {
        this.listener = listener;
    }
}
