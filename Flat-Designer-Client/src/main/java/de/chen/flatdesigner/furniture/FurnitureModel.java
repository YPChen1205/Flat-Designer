package de.chen.flatdesigner.furniture;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import de.chen.flatdesigner.io.IOUtil;

public class FurnitureModel {

	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private Map<String, Set<CustomFurniture>> model = new HashMap<>();
	
	private static FurnitureModel instance;
	
	static {
		if (instance == null) {
			instance = new FurnitureModel();
			instance.model = IOUtil.loadDefaultModel();
		}
	}
	
	private FurnitureModel () {};

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
	
	private void createCatalogIfAbsent(String catalogue) {
		if (get(catalogue) == null) {
			model.put(catalogue, new HashSet<>());
		}
	}
	
	private boolean basicAddFurnitures(String category, Collection<CustomFurniture> furnitures) {
		boolean changed = false;
		createCatalogIfAbsent(category);
		for (CustomFurniture f : furnitures) {
			basicAddFurniture(category, f);
			changed = true;
		}
		
		return changed;
	}
	
	public void addFurniture(String category, CustomFurniture furniture) {
		var oldValue = deepClone();
		createCatalogIfAbsent(category);
		basicAddFurniture(category, furniture);
		pcs.firePropertyChange("model", oldValue, deepClone());
		
		new Thread(() -> {
			IOUtil.saveFurnitureDefault(category, furniture);
		}, "IOAddThread").start();
	}
	
	
	public void addFurnitures(String category, Collection<CustomFurniture> furnitures) {
		var oldValue = deepClone();
		boolean changed = basicAddFurnitures(category, furnitures);
		
		if (changed) {
			pcs.firePropertyChange("mode", oldValue, deepClone());
		}
	}
	
	public void removeFurniture(String catalog, CustomFurniture furniture) {
		Set<CustomFurniture> l = model.get(catalog);
		
		if (l != null && l.contains(furniture)) {
			var oldValue = deepClone();
			l.remove(furniture);
			pcs.firePropertyChange("model", oldValue, deepClone());
	
			new Thread(()-> {
				IOUtil.deleteFurniture(catalog, furniture.getName());
			}, "IORemoveFurnitureThread").start();
		}
		

	}
	
	public void removeFurnitureCatalog(String catalog) {
		if(model.get(catalog) != null) {
			var oldModel = deepClone();
			model.remove(catalog);
			pcs.firePropertyChange("model", oldModel, deepClone());
			
			new Thread(()->{
				IOUtil.deleteFurnitureCatalog(catalog);
			}, "IORemoveCatalogThread").start();
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
		if (model.get(category) == null) {
			return null;
		}
		copy.addAll(model.get(category));
		return copy;
	}
	
	
	
	public static FurnitureModel getInstance() {
		return instance;
	}
	
	private FurnitureModel deepClone() {
		FurnitureModel copy = new FurnitureModel();
		model.forEach((category, list) -> {
			Set<CustomFurniture> listCopy = new HashSet<>();
			listCopy.addAll(list);
			copy.basicAddFurnitures(category, list);
		});
		for (PropertyChangeListener listener : pcs.getPropertyChangeListeners()) {
			copy.addPropertyChangeListener(listener);
		}
		return copy;
	}
	
	public void forEachCategory(BiConsumer<String, Set<CustomFurniture>> consumer) {
		model.forEach((catalogue, furnitures) -> {
			if (!catalogue.equals("other")) {
				consumer.accept(catalogue, furnitures);
			}
		});
		if (model.get("other") != null) {
			consumer.accept("other", model.get("other"));
		}	
	}
	
	public Set<String> getCatalogues() {
		return model.keySet();
	}

	public void addCatalog(String catalog) {
		if(model.get(catalog)==null) {
			var oldModel = deepClone();
			model.put(catalog, new HashSet<>());
			pcs.firePropertyChange("model", oldModel, deepClone());
			
			new Thread(()->{
				IOUtil.createCatalog(catalog);
			}, "IOAddCatalogThread").start();
			
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String c : getCatalogues()) {
			if (!first) {
				sb.append(", ");
			}
			first = false;
			sb.append(c+": {");
			if (get(c) != null) {
				sb.append(get(c));
			}
			sb.append("}");
		}
		
		return sb.toString();
	}
}
