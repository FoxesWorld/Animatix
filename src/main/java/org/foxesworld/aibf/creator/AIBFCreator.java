package org.foxesworld.aibf.creator;

import com.google.gson.Gson;
import org.foxesworld.aibf.AIBFHandler;
import org.foxesworld.aibf.AnimationConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class AIBFCreator {

    private final AIBFHandler aibfHandler;
    private final Gson gson = new Gson();

    public AIBFCreator() {
        this.aibfHandler = new AIBFHandler();
    }

    /**
     * Создает AIBF-файл на основе JSON-конфигурации.
     *
     * @param jsonConfigPath Путь к JSON-файлу конфигурации.
     * @param outputAIBFPath Путь для сохранения результирующего AIBF-файла.
     * @throws IOException В случае ошибок ввода-вывода.
     */
    public void createAIBF(String jsonConfigPath, String outputAIBFPath) throws IOException {
        // Проверка расширения файла и добавление .aibf, если его нет
        if (!outputAIBFPath.endsWith(".aibf")) {
            outputAIBFPath += ".aibf";
        }

        aibfHandler.processAIBF(jsonConfigPath, outputAIBFPath);
    }

    /**
     * Создает секцию META на основе конфигурации.
     *
     * @param config Конфигурация анимации.
     * @return Байтовый массив для секции META.
     */
    private byte[] createMetaSection(AnimationConfig config) {
        String metaJson = gson.toJson(config.getMeta());
        return metaJson.getBytes();
    }

    /**
     * Создает секцию IMG из указанного пути к изображению.
     *
     * @param imagePath Путь к изображению.
     * @return Байтовый массив для секции IMG.
     * @throws IOException В случае ошибок чтения файла.
     */
    private byte[] createImageSection(String imagePath) throws IOException {
        Path path = Paths.get(imagePath);
        if (!Files.exists(path)) {
            throw new IOException("Файл изображения не существует: " + imagePath);
        }
        return Files.readAllBytes(path);
    }

    /**
     * Создает секцию PHSE на основе фаз анимации.
     *
     * @param config Конфигурация анимации.
     * @return Байтовый массив для секции PHSE.
     * @throws IOException В случае ошибок записи в поток.
     */
    private byte[] createPhaseSection(AnimationConfig config) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {
            for (AnimationConfig.AnimationPhase phase : config.getPhases()) {
                dos.writeUTF(phase.getName());
                dos.writeInt(phase.getDuration());
                dos.writeUTF(phase.getEffectType());
                Map<String, Object> params = phase.getParams();
                dos.writeInt(params.size());
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    dos.writeUTF(entry.getKey());
                    dos.writeUTF(String.valueOf(entry.getValue()));
                }
            }
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IOException("Ошибка при создании секции PHSE", e);
        }
    }
}
