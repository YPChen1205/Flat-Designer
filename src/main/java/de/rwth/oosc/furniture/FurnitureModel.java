package de.rwth.oosc.furniture;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import de.rwth.oosc.util.IOUtil;

public class FurnitureModel {

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private Map<String, Set<CustomFurniture>> model = new HashMap<>();

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}
	
	private void basicAddFurniture(String category, CustomFurniture furniture) {
		if (hasFurniture(category, furniture.getName())) {
			return;
		}
		
		Set<CustomFurniture> l = model.get(category);
		
		if (l == null) {
			l = new HashSet<CustomFurniture>();
		}
		
		l.add(furniture);
		model.put(category, l);
	}
	
	private boolean basicAddFurnitures(String category, Collection<CustomFurniture> furnitures) {
		boolean changed = false;
		for (CustomFurniture f : furnitures) {
			basicAddFurniture(category, f);
			changed = true;
		}
		
		return changed;
	}
	
	public void addFurniture(String category, CustomFurniture furniture) {
		var oldValue = deepClone();
		basicAddFurniture(category, furniture);
		pcs.firePropertyChange("model", oldValue, deepClone());
		
		new Thread(() -> {
			IOUtil.saveFurnitureDefault(category, furniture);
		}).start();
	}
	
	public void addFurnitures(String category, Collection<CustomFurniture> furnitures) {
		var oldValue = deepClone();
		boolean changed = basicAddFurnitures(category, furnitures);
		
		if (changed) {
			pcs.firePropertyChange("mode", oldValue, deepClone());
		}
	}
	
	public void removeFurniture(String category, CustomFurniture furniture) {
		Set<CustomFurniture> l = model.get(category);
		
		if (l != null) {
			var oldValue = deepClone();
			l.remove(furniture);
			pcs.firePropertyChange("model", oldValue, deepClone());
		}
	}
	
	public boolean hasFurniture(String category, String name) {
		var furnitures = model.get(category);
		if (furnitures == null) {
			return false;
		}
		
		for (CustomFurniture furniture : furnitures) {
			if (furniture.getName().equals(name)) {
				return true;
			}
		}
		
		return false;
	}
	
	public Set<CustomFurniture> get(String category) {
		Set<CustomFurniture> copy = new HashSet<>();
		copy.addAll(model.get(category));
		return copy;
	}
	
	
	
	public static FurnitureModel loadInstance() {
		return IOUtil.loadDefaultModel();
	}
	
	
	private FurnitureModel deepClone() {
		FurnitureModel copy = new FurnitureModel();
		model.forEach((category, list) -> {
			Set<CustomFurniture> listCopy = new HashSet<>();
			listCopy.addAll(list);
			copy.basicAddFurnitures(category, list);
		});
		
		return copy;
	}
	
	public void forEachCategory(BiConsumer<String, Set<CustomFurniture>> consumer) {
		model.forEach((catalogue, furnitures) -> {
			if (!catalogue.equals("other")) {
				consumer.accept(catalogue, furnitures);
			}
		});
		consumer.accept("other", model.get("other"));
	}
	
	public Set<String> getCatalogues() {
		return model.keySet();
	}
}
