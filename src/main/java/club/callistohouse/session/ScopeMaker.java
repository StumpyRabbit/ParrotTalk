package club.callistohouse.session;

import club.callistohouse.session.marshmuck.AbstractScope;

public abstract class ScopeMaker {

	public abstract AbstractScope scopeMaker(SessionIdentity farKey);
}
