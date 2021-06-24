package org.ejprd.semanticmodel.storage;
/**
 * @author Olamidipupo Ajigboye
 *
 */
public class StorageFileNotFoundException extends StorageException {

	public StorageFileNotFoundException(String message) {
		super(message);
	}

	public StorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
