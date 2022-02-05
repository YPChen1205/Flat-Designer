package de.chen.flatdesigner.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextField;

public class JPlaceholderTextField extends JTextField {

	private static final long serialVersionUID = 1L;

	private String placeholder;

	private final Font PLACEHOLDER_FONT;
	private final Font STANDARD_FONT;

	public JPlaceholderTextField() {
		this("");
	}

	public JPlaceholderTextField(String placeholder) {
		super(placeholder);
		this.placeholder = placeholder;

		STANDARD_FONT = getFont();
		Map<TextAttribute, Object> attributes = new HashMap<>();
		attributes.put(TextAttribute.FOREGROUND, Color.GRAY);
		PLACEHOLDER_FONT = getFont().deriveFont(attributes);
		setFont(PLACEHOLDER_FONT);

		addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				if (getText().isBlank()) {
					displayPlaceholder();
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				if (JPlaceholderTextField.super.getText().equals(getPlaceholder())) {
					setText("");
					setFont(STANDARD_FONT);
				}
			}
		});

	}

	@Override
	public String getText() {
		if (super.getText().equals(getPlaceholder()) && getFont().equals(PLACEHOLDER_FONT)) {
			return "";
		}
		return super.getText();
	}

	private void displayPlaceholder() {
		setFont(PLACEHOLDER_FONT);
		setText(getPlaceholder());
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	public String getPlaceholder() {
		return placeholder;
	}
}
