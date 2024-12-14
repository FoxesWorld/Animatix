package org.foxesworld.animatix;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

            AnimationFactory animationFactory = new AnimationFactory("animatix.json");

            JFrame frame = new JFrame("Animation Demo");
            frame.setSize(400, 300);
            frame.setLayout(null);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            animationFactory.createAnimation(frame);
    }
}
