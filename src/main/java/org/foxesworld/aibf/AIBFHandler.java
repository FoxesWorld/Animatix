package org.foxesworld.aibf;

import com.google.gson.Gson;

import java.io.*;
import java.nio.file.Files;
import java.util.Map;

public class AIBFHandler {

    private static final String MAGIC = "AIBF";
    private static final int VERSION = 1;
    private final Gson gson = new Gson();

    public AIBFHandler() {
    }

    /**
     * Обрабатывает AIBF-файл: создаёт или читает файл в зависимости от параметров.
     *
     * @param jsonConfigPath Путь к JSON-конфигурации для создания файла.
     * @param outputAIBFPath Путь для сохранения результирующего AIBF-файла.
     * @throws IOException В случае ошибок при создании или чтении файла.
     */
    public void processAIBF(String jsonConfigPath, String outputAIBFPath) throws IOException {
        // Чтение JSON-конфигурации
        AnimationConfig config = readJsonConfig(jsonConfigPath);

        // Генерация байтовых секций
        Map<String, byte[]> sections = generateSections(config);

        // Запись AIBF в файл
        writeAIBF(outputAIBFPath, sections);
    }

    /**
     * Читает JSON-конфигурацию из файла.
     *
     * @param jsonConfigPath Путь к JSON-файлу конфигурации.
     * @return Объект конфигурации анимации.
     * @throws IOException В случае ошибок при чтении файла.
     */
    private AnimationConfig readJsonConfig(String jsonConfigPath) throws IOException {
        try (Reader reader = new FileReader(jsonConfigPath)) {
            return gson.fromJson(reader, AnimationConfig.class);
        }
    }

    /**
     * Генерирует секции для AIBF из конфигурации анимации.
     *
     * @param config Конфигурация анимации.
     * @return Карта секций для записи в AIBF.
     * @throws IOException В случае ошибок при создании секций.
     */
    private Map<String, byte[]> generateSections(AnimationConfig config) throws IOException {
        Map<String, byte[]> sections = new java.util.HashMap<>();

        // Секция META
        sections.put("META", createMetaSection(config));

        // Секция IMG (если задано изображение)
        if (config.getImagePath() != null) {
            sections.put("IMG", createImageSection(config.getImagePath()));
        }

        // Секция PHSE
        sections.put("PHSE", createPhaseSection(config));

        return sections;
    }

    /**
     * Создает секцию META на основе конфигурации анимации.
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
        return Files.readAllBytes(new File(imagePath).toPath());
    }

    /**
     * Создает секцию PHSE на основе фаз анимации.
     *
     * @param config Конфигурация анимации.
     * @return Байтовый массив для секции PHSE.
     */
    private byte[] createPhaseSection(AnimationConfig config) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return baos.toByteArray();
    }

    /**
     * Записывает AIBF в файл.
     *
     * @param outputPath Путь для сохранения AIBF.
     * @param sections   Секции AIBF.
     * @throws IOException В случае ошибок записи файла.
     */
    private void writeAIBF(String outputPath, Map<String, byte[]> sections) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputPath))) {
            // Записываем заголовок
            dos.writeBytes(MAGIC);
            dos.writeInt(VERSION);
            dos.writeInt(sections.size());

            // Записываем секции
            for (Map.Entry<String, byte[]> entry : sections.entrySet()) {
                dos.writeBytes(entry.getKey()); // Тип секции (4 байта)
                dos.writeInt(entry.getValue().length); // Длина секции
                dos.write(entry.getValue()); // Данные секции
            }
        }
    }

    /**
     * Читает секцию META из AIBF.
     *
     * @param metaData Байтовый массив данных секции META.
     * @return Объект Meta.
     * @throws IOException В случае ошибок при чтении.
     */
    public AnimationConfig.Meta readMetaSection(byte[] metaData) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(metaData);
             DataInputStream dis = new DataInputStream(bais)) {

            // Чтение данных из потока
            String animationName = dis.readUTF();
            String author = dis.readUTF();
            int fps = dis.readInt();

            // Создаем объект Meta через сеттеры (если они есть)
            AnimationConfig.Meta meta = new AnimationConfig.Meta();
            meta.setAnimationName(animationName);
            meta.setAuthor(author);
            meta.setFps(fps);

            // Возвращаем объект Meta
            return meta;
        } catch (EOFException e) {
            // Если произошла ошибка при чтении
            throw new IOException("Не удалось прочитать мета-данные, данные повреждены или неполные", e);
        } catch (IOException e) {
            // Общая ошибка при чтении данных
            throw new IOException("Ошибка при чтении данных", e);
        }
    }


}
