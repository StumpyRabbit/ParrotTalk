package club.callistohouse.session.marshmuck;

public class DummyScope extends AbstractScope {

	@Override
	public Object externalize(Object obj) { return obj; }
	@Override
	public Object internalize(Object obj) { return obj; }
}
