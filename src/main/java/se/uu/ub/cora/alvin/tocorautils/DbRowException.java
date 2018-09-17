package se.uu.ub.cora.alvin.tocorautils;

public class DbRowException extends RuntimeException {

	private static final long serialVersionUID = 3533324208291987862L;

	public static DbRowException withMessage(String message) {
		return new DbRowException(message);
	}

	private DbRowException(String message) {
		super(message);
	}

}
