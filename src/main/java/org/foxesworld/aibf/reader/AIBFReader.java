package org.foxesworld.aibf.reader;

import org.foxesworld.aibf.AIBFHandler;
import org.foxesworld.aibf.AnimationConfig;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class AIBFReader {

    private static final String MAGIC = "AIBF";
    private static final int VERSION = 1;

    private AIBFHandler handler;

    public AIBFReader() {
        handler = new AIBFHandler();
    }

    /**
     * Читает AIBF-файл и возвращает секции в виде карты.
     *
     * @param filePath Путь к AIBF-файлу.
     * @return Карта с секциями AIBF.
     * @throws IOException В случае ошибок при чтении файла.
     */
    public Map<String, byte[]> readAIBF(String filePath) throws IOException {
        Map<String, byte[]> sections = new HashMap<>();
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            // Проверка заголовка
            String magic = readString(dis, MAGIC.length());
            if (!magic.equals(MAGIC)) {
                throw new IOException("Неверный формат файла. Ожидался заголовок: " + MAGIC + ", получено: " + magic);
            }

            int version = dis.readInt();
            if (version != VERSION) {
                throw new IOException("Неверная версия файла. Ожидалась версия: " + VERSION + ", получено: " + version);
            }

            int sectionsCount = dis.readInt();
            if (sectionsCount <= 0) {
                throw new IOException("Количество секций должно быть больше нуля. Получено: " + sectionsCount);
            }

            for (int i = 0; i < sectionsCount; i++) {
                String sectionType = readString(dis, 4);
                int sectionLength = dis.readInt();

                // Логирование для отладки
                System.out.println("Ожидаемая длина секции: " + sectionLength);

                // Проверка доступных байтов перед чтением
                long remainingBytes = dis.available();
                System.out.println("Оставшиеся байты: " + remainingBytes);

                if (remainingBytes < sectionLength) {
                    System.err.println("Ошибка: недостаточно данных для чтения секции " + sectionType);
                    System.err.println("Ожидаемая длина: " + sectionLength + ", оставшиеся байты: " + remainingBytes);
                    throw new IOException("Недостаточно данных для чтения секции " + sectionType);
                }

                byte[] sectionData = new byte[sectionLength];
                dis.readFully(sectionData);

                // Логирование для отладки
                System.out.println("Чтение секции: " + sectionType + " длиной: " + sectionLength);
                sections.put(sectionType, sectionData);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла " + filePath + ": " + e.getMessage());
            throw e;
        }
        return sections;
    }


    /**
     * Читает строку из DataInputStream с заданной длиной.
     *
     * @param dis    Поток данных.
     * @param length Длина строки.
     * @return Строка.
     * @throws IOException В случае ошибок при чтении.
     */
    private String readString(DataInputStream dis, int length) throws IOException {
        byte[] bytes = new byte[length];
        dis.readFully(bytes);
        return new String(bytes);
    }

    /**
     * Читает секцию META из данных.
     *
     * @param metaData Байтовый массив данных секции META.
     * @return Объект Meta.
     * @throws IOException В случае ошибок при чтении.
     */
    public AnimationConfig.Meta readMetaSection(byte[] metaData) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(metaData);
             DataInputStream dis = new DataInputStream(bais)) {

            // Чтение данных из секции META
            String animationName = dis.readUTF();
            String author = dis.readUTF();
            int fps = dis.readInt();

            // Создание объекта Meta через сеттеры
            AnimationConfig.Meta meta = new AnimationConfig.Meta();
            meta.setAnimationName(animationName);
            meta.setAuthor(author);
            meta.setFps(fps);

            return meta;
        } catch (IOException e) {
            System.err.println("Ошибка при чтении секции META: " + e.getMessage());
            throw e;
        }
    }
}
