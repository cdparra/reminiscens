package delegates;

public class ApplicationDelegate {

	public static ApplicationDelegate getInstance() {
		return new ApplicationDelegate();
	}
	
	// TODO move here methods from application that access the model and the beans
}
