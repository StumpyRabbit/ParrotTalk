package club.callistohouse.session.marshmuck;

public abstract class AbstractScope {

	public abstract Object externalize(Object obj);
	public abstract Object internalize(Object obj);
}
