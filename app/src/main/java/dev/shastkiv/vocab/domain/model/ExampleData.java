package dev.shastkiv.vocab.domain.model;

/**
 * Модель даних для зберігання прикладів використання слів
 */
public class ExampleData {
    private String exampleText;
    private String translationText;

    public ExampleData(String exampleText, String translationText) {
        this.exampleText = exampleText;
        this.translationText = translationText;
    }

    public String getExampleText() {
        return exampleText;
    }

    public void setExampleText(String exampleText) {
        this.exampleText = exampleText;
    }

    public String getTranslationText() {
        return translationText;
    }

    public void setTranslationText(String translationText) {
        this.translationText = translationText;
    }
}
