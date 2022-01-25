package de.rwth.oosc.guisample.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.SwingUtilities;

public class IOUtil {
	
	public synchronized static void file2Str(File file, DefaultListModel<String> model) {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
			String str = null;
			Vector<String> elements = new Vector<>();
			while ((str = br.readLine()) != null) {
				elements.add(str);
			}
			SwingUtilities.invokeLater(() -> {
				model.addAll(elements);
			});
		} catch (IOException e) {
			System.out.println(e);
		}
		
	}

	public synchronized static void str2File(DefaultListModel<String> model, File file) {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
			Iterator<String> it = model.elements().asIterator();
			while (it.hasNext()) {
				bw.write(it.next());
				bw.write("\n");
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

}
