package com.shastkiv.vocab.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

/**
 * Утиліта для створення ефекту друкування тексту
 */
public class TypewriterEffect {
    private static final int DEFAULT_TYPING_DELAY = 10; // затримка між символами в мс

    /**
     * Анімує текст, імітуючи введення з клавіатури
     *
     * @param textView TextView, в який буде виводитись текст
     * @param text Текст для анімації
     * @param onComplete Колбек, який викликається після завершення анімації
     */
    public static void typeText(TextView textView, String text, Runnable onComplete) {
        typeText(textView, text, DEFAULT_TYPING_DELAY, onComplete);
    }

    /**
     * Анімує текст, імітуючи введення з клавіатури із заданою швидкістю
     *
     * @param textView TextView, в який буде виводитись текст
     * @param text Текст для анімації
     * @param delay Затримка між символами в мс
     * @param onComplete Колбек, який викликається після завершення анімації
     */
    public static void typeText(TextView textView, String text, int delay, Runnable onComplete) {
        final Handler handler = new Handler(Looper.getMainLooper());
        textView.setText("");

        final int[] charIndex = {0};
        final Runnable typingRunnable = new Runnable() {
            @Override
            public void run() {
                if (charIndex[0] < text.length()) {
                    textView.append(String.valueOf(text.charAt(charIndex[0])));
                    charIndex[0]++;
                    handler.postDelayed(this, delay);
                } else if (onComplete != null) {
                    onComplete.run();
                }
            }
        };

        handler.postDelayed(typingRunnable, delay);
    }
}
