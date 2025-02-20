package com.example.learnwordstrainer;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

public class WordResultDialog extends BottomSheetDialog {

    private ImageView ivResult;
    private TextView tvResult;
    private MaterialButton btnNext;
    private final boolean isCorrect;
    private final OnNextWordListener listener;

    public interface OnNextWordListener {
        void onNextWord();
    }

    public WordResultDialog(@NonNull Context context,
                            boolean isCorrect,
                            OnNextWordListener listener) {
        super(context);
        this.isCorrect = isCorrect;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_word_result);

        initViews();
        setupDialog();
        setupData();
        setupListeners();
    }

    private void initViews() {
        ivResult = findViewById(R.id.ivResult);
        tvResult = findViewById(R.id.tvResult);
        btnNext = findViewById(R.id.btnNext);
    }

    private void setupDialog() {
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(
                (View) findViewById(com.google.android.material.R.id.design_bottom_sheet));
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setSkipCollapsed(true);

        behavior.setDraggable(false);

        setCancelable(false);
    }

    private void setupData() {

        if (isCorrect) {
            ivResult.setImageResource(R.drawable.ic_check);
            ivResult.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(getContext(), R.color.success)));
            tvResult.setText("Правильно!");
            playCorrectAnimation();
        } else {
            ivResult.setImageResource(R.drawable.ic_close);
            ivResult.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(getContext(), R.color.error)));
            tvResult.setText("Спробуйте ще раз");
            playWrongAnimation();
        }
    }

    private void setupListeners() {
        btnNext.setOnClickListener(v -> {
            dismiss();
            if (listener != null) {
                listener.onNextWord();
            }
        });
    }

    private void playCorrectAnimation() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(ivResult, "scaleX", 0f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(ivResult, "scaleY", 0f, 1.2f, 1f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        animatorSet.setDuration(1000);
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.start();
    }

    private void playWrongAnimation() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(ivResult, "rotation", 0f, 5f, -5f, 0f);
        rotation.setDuration(1000);
        rotation.setRepeatCount(1);
        rotation.start();
    }
}
