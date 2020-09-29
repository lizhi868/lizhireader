package com.lizhi.reader.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.core.content.ContextCompat;

import com.lizhi.reader.R;
import com.lizhi.reader.view.activity.DisclaimerActivity;

/**
 * Created by lc on 2017/6/23.
 * 服务协议dialog
 */
public class AgreementDialog extends Dialog {

    public interface DialogClickListener {
        void left();

        void right();
    }

    private DialogClickListener listener;

    private Context context;

    public AgreementDialog(@NonNull Context context) {
        this(context, R.style.MyDialog);
    }

    public AgreementDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
        init();
    }

    public void setListener(DialogClickListener listener) {
        this.listener = listener;
    }

    private void init() {
        setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_agreement, null);
        TextView tv_content1 = view.findViewById(R.id.tv_content1);
        TextView tv_content3 = view.findViewById(R.id.tv_content3);
        TextView tvDisagree = view.findViewById(R.id.tv_disagree);
        TextView tvConsent = view.findViewById(R.id.tv_consent);
        SpannableStringBuilder builder = new SpannableStringBuilder(tv_content1.getText());
        ForegroundColorSpan redSpan = new ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_202E3D));
        builder.setSpan(redSpan, builder.length() - 17, builder.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_content1.setText(builder);
        tv_content3.setOnClickListener(view1 -> {
            DisclaimerActivity.startThis(getContext());
        });
        tvDisagree.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.left();
            }
        });
        tvConsent.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.right();
            }
        });
        setContentView(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width =WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(params);
            window.setGravity(Gravity.BOTTOM);
        }
    }

    /**
     * back键点击监听
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (listener != null) {
                listener.left();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
