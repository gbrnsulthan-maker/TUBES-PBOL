package com.sportfield.ui.components;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class WebBrowserPanel extends JPanel {

    private final JEditorPane editorPane;
    private final String pagePath;

    public WebBrowserPanel(String pageName) {
        setLayout(new BorderLayout());
        setOpaque(false);

        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        editorPane.setBackground(Color.WHITE);
        editorPane.setBorder(null);

        pagePath = pageName + ".html";

        loadPage();

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadPage() {
        File file = new File("out/" + pagePath);
        if (!file.exists()) {
            file = new File("out\\" + pagePath);
        }

        if (!file.exists()) {
            editorPane.setText("<html><body><h1>Error: Page not found</h1><p>Looking for: " + file.getAbsolutePath() + "</p></body></html>");
            return;
        }

        try {
            editorPane.setPage(file.toURI().toURL());
        } catch (IOException e) {
            editorPane.setText("<html><body><h1>Error loading page</h1><p>" + e.getMessage() + "</p></body></html>");
            e.printStackTrace();
        }
    }

    public void reload() {
        loadPage();
    }
}
