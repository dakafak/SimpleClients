package dev.fanger.simpleclients.logging;

public enum Level {
	//TODO implement logger wrapper. Add abstract logger so anyone can implement their own logging extension
	//			basically have a default logging class that prints whatever is inputed but allow and overwrite to use
	//			something else like log4j2. All outputs will be printed to a specified logging class that can
	//			redirect to anywhere else
	//			public abstract void log(Level level, Long timeInMs**maybe nano**, String message)

	/**
	 * Information about general processes during operation like when a client connects
	 */
	WARN,

	/**
	 * Additional information that isn't necessary for traditional use - as it would flood logs
	 */
	DEBUG,

	/**
	 * Generally any critical errors like sockets unable to be opened
	 */
	ERROR,

	/**
	 * An individual level for stacktrace logging, stacktraces will be available on error messages but will only
	 * print if deemed necessary
	 */
	STACKTRACE;
}
