package info.openrocket.swing.gui.util;

import java.io.File;

import javax.swing.SwingWorker;

import info.openrocket.core.document.OpenRocketDocument;
import info.openrocket.core.file.GeneralRocketSaver;
import info.openrocket.core.file.GeneralRocketSaver.SavingProgress;

public class SaveFileWorker extends SwingWorker<Void, Void> {
	
	private final OpenRocketDocument document;
	private final File file;
	private final GeneralRocketSaver saver;
	
	public SaveFileWorker(OpenRocketDocument document, File file, GeneralRocketSaver saver) {
		this.document = document;
		this.file = file;
		this.saver = saver;
	}
	
	
	@Override
	protected Void doInBackground() throws Exception {
		
		// Create the ProgressOutputStream that provides progress estimates
		SavingProgress progressCallback = new GeneralRocketSaver.SavingProgress() {
			
			@Override
			public void setProgress(int progress) {
				SaveFileWorker.this.setProgress(progress);
				
			}
		};
		
		saver.save(file, document, progressCallback);
		return null;
	}
	
}
