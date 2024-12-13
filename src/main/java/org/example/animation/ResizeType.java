package org.example.animation;

public enum ResizeType {
    SCALE_TO_FILL,     // Растягивание до указанных размеров, без сохранения пропорций
    SCALE_TO_FIT,      // Масштабирование с сохранением пропорций (вписывание в рамки)
    SCALE_TO_COVER     // Масштабирование с сохранением пропорций (заполнение рамки)
}
