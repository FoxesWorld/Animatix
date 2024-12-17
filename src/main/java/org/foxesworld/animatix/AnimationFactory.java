package org.foxesworld.animatix;

<<<<<<< Updated upstream
import org.foxesworld.animatix.animation.area.KWindow;
import org.foxesworld.animatix.animation.effect.AnimationEffectFactory;
import org.foxesworld.animatix.animation.AnimationFrame;
import org.foxesworld.animatix.animation.phase.AnimationPhaseExecutor;
import org.foxesworld.animatix.animation.config.AnimationConfigLoader;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.AnimationStatus;
import org.foxesworld.animatix.animation.config.AnimationConfig;
import org.foxesworld.animatix.animation.imageEffect.ImageWorks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import javax.swing.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
=======
import org.foxesworld.animatix.animation.AnimationStatus;
import org.foxesworld.animatix.animation.Phase;
import org.foxesworld.animatix.animation.TaskExecutor;
import org.foxesworld.animatix.animation.config.AnimationConfig;
import org.foxesworld.animatix.animation.config.AnimationConfigLoader;
import org.foxesworld.animatix.animation.config.AnimationPhase;
import org.foxesworld.animatix.animation.effect.AnimationEffectFactory;
import org.foxesworld.animatix.animation.phase.AnimationPhaseExecutor;

import javax.swing.*;
import java.io.InputStream;
import java.util.List;
>>>>>>> Stashed changes

public class AnimationFactory implements AnimationStatus {
    private Phase animPhase;

<<<<<<< Updated upstream
    public static Logger logger;
    private final ScheduledExecutorService scheduler;
    private final AnimationConfigLoader configLoader;
    private final AnimationEffectFactory effectFactory;
    private final AnimationPhaseExecutor phaseExecutor;
    private int phaseNum = 0;
    private AnimationPhase currentPhase;
    private JLabel animLabel, textLabel;
    private ImageWorks imageWorks;
    private AnimationConfig config;
    private boolean isPaused = false;
    private final Map<AnimationPhase, List<AnimationFrame>> cachedFrames = new HashMap<>();

    public AnimationFactory(String configPath) {
        this(Executors.newSingleThreadScheduledExecutor(),
                new AnimationConfigLoader(),
                new AnimationEffectFactory(),
                new AnimationPhaseExecutor());
        System.setProperty("log.dir", System.getProperty("user.dir"));
        System.setProperty("log.level", "DEBUG");
        logger = LogManager.getLogger(Main.class);

        this.loadConfig(configPath);
=======
    public static final System.Logger logger = System.getLogger(AnimationFactory.class.getName());
    private final TaskExecutor taskExecutor;
    private final AnimationConfigLoader configLoader = new AnimationConfigLoader();
    private final AnimationEffectFactory effectFactory;
    public final AnimationPhaseExecutor phaseExecutor;
    private AnimationConfig config;

    public AnimationFactory(String configPath) {
        this.taskExecutor = new TaskExecutor();
        effectFactory = new AnimationEffectFactory(this);
        phaseExecutor = new AnimationPhaseExecutor(this);
        loadConfig(configPath);
>>>>>>> Stashed changes
    }

    private void loadConfig(String configPath){
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(configPath)) {
            if (inputStream == null) {
                throw new RuntimeException("Configuration file not found!");
            }
            logger.info("Loading animation config...");
            this.config = configLoader.loadConfig(inputStream);
            logger.log(System.Logger.Level.INFO, "Successfully loaded animation config.");
        } catch (Exception e) {
            AnimationFactory.logger.error("Failed to initialize animation factory", e);
        }
    }

<<<<<<< Updated upstream
    public AnimationFactory(ScheduledExecutorService scheduler,
                            AnimationConfigLoader configLoader,
                            AnimationEffectFactory effectFactory,
                            AnimationPhaseExecutor phaseExecutor) {
        this.scheduler = scheduler;
        this.configLoader = configLoader;
        this.effectFactory = effectFactory;
        this.phaseExecutor = phaseExecutor;

        this.effectFactory.setAnimationFactory(this);
        this.phaseExecutor.setAnimationFactory(this);
    }

    public void createAnimation(Object window) {
        validateConfig();

        this.imageWorks = new ImageWorks(ImageWorks.getImageFromStream(config.getImagePath()));
        if (animLabel == null) {
            animLabel = new JLabel(new ImageIcon(imageWorks.getImage()));
            animLabel.setBounds(this.config.getBounds());
            addLabelToWindow(window, animLabel);
=======
    // Создание анимации и запуск фаз
    public void createAnimation(Object window) {
        validateConfig();

        for (AnimationConfig.AnimConf animConf : config.getAnimObj()) {
            JLabel animLabel = createAnimationLabel(window, animConf);
            runAnimation(animLabel, animConf);
>>>>>>> Stashed changes
        }

        if (textLabel == null) {
            textLabel = new JLabel("", SwingConstants.CENTER);
            textLabel.setBounds(0, 200, getWindowWidth(window), 50);
            addLabelToWindow(window, textLabel);
        }

        List<AnimationPhase> phases = config.getPhases();
        boolean repeat = config.isRepeat();

        new Thread(() -> executeAnimation(phases, repeat)).start();
    }

    // Создание и добавление JLabel
    private JLabel createAnimationLabel(Object window, AnimationConfig.AnimConf animConf) {
        JLabel animLabel = new JLabel();
        animLabel.setBounds(animConf.getBounds());
        animLabel.setName(animConf.getName());
        addLabelToWindow(window, animLabel);
        animLabel.setVisible(animConf.isVisible());
        return animLabel;
    }

    // Добавление JLabel в окно
    private void addLabelToWindow(Object window, JLabel label) {
        if (window instanceof JFrame) {
            ((JFrame) window).add(label);
        } else if (window instanceof KWindow) {
            ((KWindow) window).add(label);
        } else if (window instanceof JWindow) {
            ((JWindow) window).add(label);
        } else {
            throw new IllegalArgumentException("Unsupported window type: " + window.getClass().getName());
        }
    }

<<<<<<< Updated upstream
    private int getWindowWidth(Object window) {
        if (window instanceof JFrame) {
            return ((JFrame) window).getWidth();
        } else if (window instanceof KWindow) {
            return ((KWindow) window).getWidth();
        } else if (window instanceof JWindow) {
            return ((JWindow) window).getWidth();
        } else {
            throw new IllegalArgumentException("Unsupported window type: " + window.getClass().getName());
        }
    }

    private void executeAnimation(List<AnimationPhase> phases, boolean repeat) {
        try {
            do {
                for (AnimationPhase phase : phases) {
                    currentPhase = phase;
                    logger.info("Starting phase: {}", phase.getName());
                    List<AnimationFrame> animationFrames = getOrCacheAnimationFrames(phase);
                    phaseExecutor.executePhase(phase, animationFrames);

                    waitForPhaseCompletion(phase.getDuration());
                }
                incrementPhase();
            } while (repeat && phaseNum < config.getPhases().size());
            logger.info("Animation complete.");
        } catch (Exception e) {
            logger.error("Error during animation execution", e);
        } finally {
            shutdownScheduler();
        }
    }

    private List<AnimationFrame> getOrCacheAnimationFrames(AnimationPhase phase) {
        if (!cachedFrames.containsKey(phase)) {
            cachedFrames.put(phase, effectFactory.createEffectsForPhase(phase));
        }
        return cachedFrames.get(phase);
    }

    private void waitForPhaseCompletion(long duration) {
        try {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            future.join();
        } catch (Exception e) {
            logger.error("Error during waiting for phase completion", e);
        }
    }

    public synchronized void pause() {
        isPaused = true;
        logger.info("Animation paused.");
    }

    public synchronized void resume() {
        isPaused = false;
        logger.info("Animation resumed.");
        notify();
    }

    public synchronized void incrementPhase() {
        phaseNum++;
        logger.debug("Phase incremented to: {}", phaseNum);
    }

    private void shutdownScheduler() {
        scheduler.shutdown();
    }

    @Override
    public synchronized void onPhaseCompleted() {
        logger.info("Phase completed, notifying main thread.");
        notify();
=======
    // Запуск анимации для конкретного объекта
    public void runAnimation(JLabel animLabel, AnimationConfig.AnimConf animConf) {
        SwingWorker<Void, Void> animationWorker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                animPhase = new Phase(AnimationFactory.this, animConf, animLabel);
                int phaseNum = 0;

                // Подготовка анимации
                animPhase.preparePhase(animConf.getPhases().get(0)); // Выбираем первую фазу

                for (AnimationPhase phase : animConf.getPhases()) {
                    animPhase.preparePhase(phase);
                    animPhase.executePhase(phaseNum);
                    phaseNum++;
                    animPhase.waitIfPaused(); // Локальная пауза
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Ждем завершения
                    logger.log(System.Logger.Level.INFO, "Animation for {0} completed.", animConf.getName());
                } catch (Exception e) {
                    logger.log(System.Logger.Level.ERROR, "Error during animation: " + animConf.getName(), e);
                }
            }
        };
        animationWorker.execute();
>>>>>>> Stashed changes
    }

    // Проверка на загруженную конфигурацию
    private void validateConfig() {
        if (config == null) {
            throw new IllegalStateException("AnimationConfig must be loaded before creating animation");
        }
    }

    // Остановка всех процессов, если необходимо
    public void dispose() {
        if (imageWorks != null) {
            imageWorks.dispose();
            imageWorks = null;
        }
        scheduler.shutdown();
    }

<<<<<<< Updated upstream
    public JLabel getAnimLabel() {
        return animLabel;
    }
    public JLabel getTextLabel() {
        return textLabel;
=======
    @Override
    public void onPhaseCompleted(AnimationPhase phase) {
        // Обработка завершенной фазы (например, сохранение состояния)
    }

    public Phase getAnimPhase() {
        return animPhase;
>>>>>>> Stashed changes
    }
    public ImageWorks getImageWorks() {
        return imageWorks;
    }
    public AnimationPhase getCurrentPhase() {
        return currentPhase;
    }
<<<<<<< Updated upstream
    public int getPhaseNum() {
        return phaseNum;
=======

    public AnimationEffectFactory getEffectFactory() {
        return effectFactory;
>>>>>>> Stashed changes
    }
}
