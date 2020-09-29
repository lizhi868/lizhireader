package com.lizhi.reader.view.popupwindow;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.lizhi.reader.R;
import com.lizhi.reader.help.ReadBookControl;
import com.lizhi.reader.help.permission.Permissions;
import com.lizhi.reader.help.permission.PermissionsCompat;
import com.lizhi.reader.view.activity.ReadBookActivity;
import com.lizhi.reader.view.activity.ReadStyleActivity;
import com.lizhi.reader.widget.explosion_field.Utils;
import com.lizhi.reader.widget.font.FontSelector;
import com.lizhi.reader.widget.page.animation.PageAnimation;
import com.makeramen.roundedimageview.RoundedImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import kotlin.Unit;

public class ReadInterfacePop extends FrameLayout {
    @BindView(R.id.fl_text_Bold)
    TextView flTextBold;
    @BindView(R.id.fl_text_font)
    TextView fl_text_font;
    @BindView(R.id.civ_bg_white)
    RoundedImageView civBgWhite;
    @BindView(R.id.civ_bg_yellow)
    RoundedImageView civBgYellow;
    @BindView(R.id.civ_bg_green)
    RoundedImageView civBgGreen;
    @BindView(R.id.civ_bg_black)
    RoundedImageView civBgBlack;
    @BindView(R.id.civ_bg_blue)
    RoundedImageView civBgBlue;
//    @BindView(R.id.tv0)
//    TextView tv0;
//    @BindView(R.id.tv1)
//    TextView tv1;
//    @BindView(R.id.tv2)
//    TextView tv2;
//    @BindView(R.id.tv3)
//    TextView tv3;
//    @BindView(R.id.tv4)
//    TextView tv4;

    @BindView(R.id.nbTextSize)
    TextView nbTextSize;
    @BindView(R.id.nbTextSizeAdd)
    TextView nbTextSizeAdd;
    @BindView(R.id.nbTextSizeDec)
    TextView nbTextSizeDec;
    @BindView(R.id.fl_indent)
    TextView tvIndent;
    @BindView(R.id.tvPageMode)
    TextView tvPageMode;
    @BindView(R.id.tvRowDef0)
    TextView tvRowDef0;
    @BindView(R.id.tvRowDef1)
    TextView tvRowDef1;
    @BindView(R.id.tvRowDef2)
    TextView tvRowDef2;
    @BindView(R.id.tvRowDef)
    TextView tvRowDef;
    @BindView(R.id.tvOther)
    ImageView tvOther;

    private RoundedImageView[] views;
    private ReadBookActivity activity;
    private ReadBookControl readBookControl = ReadBookControl.getInstance();
    private Callback callback;
    private int oldIndex = -1;

    public ReadInterfacePop(Context context) {
        super(context);
        init(context);
    }

    public ReadInterfacePop(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ReadInterfacePop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_read_interface, this);
        ButterKnife.bind(this, view);
        views = new RoundedImageView[]{civBgWhite, civBgYellow, civBgGreen, civBgBlue, civBgBlack};
    }

    public void setListener(ReadBookActivity readBookActivity, @NonNull Callback callback) {
        this.activity = readBookActivity;
        this.callback = callback;
        initData();
        bindEvent();
    }

    private void initData() {
        setBg();
        updateBg(readBookControl.getTextDrawableIndex());
        updateBoldText(readBookControl.getTextBold());
        updatePageMode(readBookControl.getPageMode());

        nbTextSize.setText(String.format("%d", readBookControl.getTextSize()));
    }

    /**
     * 控件事件
     */
    private void bindEvent() {
        //字号减
        nbTextSizeDec.setOnClickListener(v -> {
            int fontSize = readBookControl.getTextSize() - 1;
            if (fontSize < 10) fontSize = 10;
            readBookControl.setTextSize(fontSize);
            nbTextSize.setText(String.format("%d", readBookControl.getTextSize()));
            callback.upTextSize();
        });
        //字号加
        nbTextSizeAdd.setOnClickListener(v -> {
            int fontSize = readBookControl.getTextSize() + 1;
            if (fontSize > 40) fontSize = 40;
            readBookControl.setTextSize(fontSize);
            nbTextSize.setText(String.format("%d", readBookControl.getTextSize()));
            callback.upTextSize();
        });
        //缩进
        tvIndent.setOnClickListener(v -> {
//            AlertDialog dialog = new AlertDialog.Builder(activity, R.style.alertDialogTheme)
//                    .setTitle(activity.getString(R.string.indent))
//                    .setSingleChoiceItems(activity.getResources().getStringArray(R.array.indent),
//                            readBookControl.getIndent(),
//                            (dialogInterface, i) -> {
//                                readBookControl.setIndent(i);
//                                callback.refresh();
//                                dialogInterface.dismiss();
//                            })
//                    .create();
//            dialog.show();
//            ATH.setAlertDialogTint(dialog);
            AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.indent))
                    .setSingleChoiceItems(activity.getResources().getStringArray(R.array.indent),
                            readBookControl.getIndent(),
                            (dialogInterface, i) -> {
                                readBookControl.setIndent(i);
                                callback.refresh();
                                dialogInterface.dismiss();
                            })
                    .create();
            dialog.show();
        });
        //翻页模式
        tvPageMode.setOnClickListener(view -> {
//            AlertDialog dialog = new AlertDialog.Builder(activity, R.style.alertDialogTheme)
//                    .setTitle(activity.getString(R.string.page_mode))
//                    .setSingleChoiceItems(PageAnimation.Mode.getAllPageMode(), readBookControl.getPageMode(), (dialogInterface, i) -> {
//                        readBookControl.setPageMode(i);
//                        updatePageMode(i);
//                        callback.upPageMode();
//                        dialogInterface.dismiss();
//                    })
//                    .create();
//            dialog.show();
//            ATH.setAlertDialogTint(dialog);
            AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.page_mode))
                    .setSingleChoiceItems(PageAnimation.Mode.getAllPageMode(), readBookControl.getPageMode(), (dialogInterface, i) -> {
                        readBookControl.setPageMode(i);
                        updatePageMode(i);
                        callback.upPageMode();
                        dialogInterface.dismiss();
                    })
                    .create();
            dialog.show();
        });
        //加粗切换
        flTextBold.setOnClickListener(view -> {
            readBookControl.setTextBold(!readBookControl.getTextBold());
            updateBoldText(readBookControl.getTextBold());
            callback.upTextSize();
        });
        //行距单倍
        tvRowDef0.setOnClickListener(v -> {
            readBookControl.setLineMultiplier(0.6f);
            readBookControl.setParagraphSize(1.5f);
            callback.upTextSize();
        });
        //行距双倍
        tvRowDef1.setOnClickListener(v -> {
            readBookControl.setLineMultiplier(1.2f);
            readBookControl.setParagraphSize(1.8f);
            callback.upTextSize();
        });
        //行距三倍
        tvRowDef2.setOnClickListener(v -> {
            readBookControl.setLineMultiplier(1.8f);
            readBookControl.setParagraphSize(2.0f);
            callback.upTextSize();
        });
        //行距默认
        tvRowDef.setOnClickListener(v -> {
            readBookControl.setLineMultiplier(1.0f);
            readBookControl.setParagraphSize(1.8f);
            callback.upTextSize();
        });
        //自定义间距
        tvOther.setOnClickListener(v -> {
            activity.readAdjustMarginIn();
        });
        //背景选择
        civBgWhite.setOnClickListener(v -> {
            updateBg(0);
            callback.bgChange();
        });
        civBgYellow.setOnClickListener(v -> {
            updateBg(1);
            callback.bgChange();
        });
        civBgGreen.setOnClickListener(v -> {
            updateBg(2);
            callback.bgChange();
        });
        civBgBlue.setOnClickListener(v -> {
            updateBg(3);
            callback.bgChange();
        });
        civBgBlack.setOnClickListener(v -> {
            updateBg(4);
            callback.bgChange();
        });
        //自定义阅读样式
//        civBgWhite.setOnLongClickListener(view -> customReadStyle(0));
//        civBgYellow.setOnLongClickListener(view -> customReadStyle(1));
//        civBgGreen.setOnLongClickListener(view -> customReadStyle(2));
//        civBgBlue.setOnLongClickListener(view -> customReadStyle(3));
//        civBgBlack.setOnLongClickListener(view -> customReadStyle(4));


        //选择字体
        fl_text_font.setOnClickListener(view -> {
            new PermissionsCompat.Builder(activity)
                    .addPermissions(Permissions.READ_EXTERNAL_STORAGE, Permissions.WRITE_EXTERNAL_STORAGE)
                    .rationale(R.string.get_storage_per)
                    .onGranted((requestCode) -> {
                        new FontSelector(activity, readBookControl.getFontPath())
                                .setListener(new FontSelector.OnThisListener() {
                                    @Override
                                    public void setDefault() {
                                        clearFontPath();
                                    }

                                    @Override
                                    public void setFontPath(String fontPath) {
                                        setReadFonts(fontPath);
                                    }
                                })
                                .create()
                                .show();
                        return Unit.INSTANCE;
                    })
                    .request();
        });

        //长按清除字体
        fl_text_font.setOnLongClickListener(view -> {
            clearFontPath();
            activity.toast(R.string.clear_font);
            return true;
        });
    }

    //自定义阅读样式
    private boolean customReadStyle(int index) {
        Intent intent = new Intent(activity, ReadStyleActivity.class);
        intent.putExtra("index", index);
        activity.startActivity(intent);
        return false;
    }

    //设置字体
    public void setReadFonts(String path) {
        readBookControl.setReadBookFont(path);
        callback.refresh();
    }

    //清除字体
    private void clearFontPath() {
        readBookControl.setReadBookFont(null);
        callback.refresh();
    }

    private void updatePageMode(int pageMode) {
        tvPageMode.setText(String.format("%s", PageAnimation.Mode.getPageMode(pageMode)));
    }

    private void updateBoldText(Boolean isBold) {
        flTextBold.setSelected(isBold);
    }

    public void setBg() {
//        tv0.setTextColor(readBookControl.getTextColor(0));
//        tv1.setTextColor(readBookControl.getTextColor(1));
//        tv2.setTextColor(readBookControl.getTextColor(2));
//        tv3.setTextColor(readBookControl.getTextColor(3));
//        tv4.setTextColor(readBookControl.getTextColor(4));
        civBgWhite.setImageDrawable(readBookControl.getBgDrawable(0, activity, 100, 180));
        civBgYellow.setImageDrawable(readBookControl.getBgDrawable(1, activity, 100, 180));
        civBgGreen.setImageDrawable(readBookControl.getBgDrawable(2, activity, 100, 180));
        civBgBlue.setImageDrawable(readBookControl.getBgDrawable(3, activity, 100, 180));
        civBgBlack.setImageDrawable(readBookControl.getBgDrawable(4, activity, 100, 180));
    }

    private void updateBg(int index) {
        if (oldIndex == index) {
            return;
        }
        for (int i = 0; i < views.length; i++) {
            if (i == oldIndex) {
                views[i].setBorderColor(ContextCompat.getColor(activity, R.color.transparent));
            }
            if (i == index) {
                views[i].setBorderColor(ContextCompat.getColor(activity, R.color.color_202E3D));
            }
        }
        oldIndex = index;
        readBookControl.setTextDrawableIndex(index);
    }

    public interface Callback {
        void upPageMode();

        void upTextSize();

        void upMargin();

        void bgChange();

        void refresh();
    }

}