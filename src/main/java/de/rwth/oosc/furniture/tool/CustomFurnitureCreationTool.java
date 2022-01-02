package de.rwth.oosc.furniture.tool;

import java.util.Map;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.tool.CreationTool;

public class CustomFurnitureCreationTool extends CreationTool {

	private static final long serialVersionUID = 1L;

	private String name;
	
	public CustomFurnitureCreationTool(Figure prototype, @Nullable Map<AttributeKey<?>, Object> attributes) {
		super(prototype, attributes);
	}

	public CustomFurnitureCreationTool(Figure prototype, String name) {
		super(prototype);
	}

	public CustomFurnitureCreationTool(String prototypeClassName, @Nullable Map<AttributeKey<?>, Object> attributes,
			@Nullable String name) {
		super(prototypeClassName, attributes, name);
	}

	public CustomFurnitureCreationTool(String prototypeClassName, @Nullable Map<AttributeKey<?>, Object> attributes) {
		super(prototypeClassName, attributes);
	}

	public CustomFurnitureCreationTool(String prototypeClassName) {
		super(prototypeClassName);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

}
