package bn.base;

import bn.core.Domain;

/**
 * BooleanVariables all shared a single instance of a BooleanDomain.
 */
public class BooleanVariable extends NamedVariable {

	static Domain domain = new BooleanDomain();

	public BooleanVariable(String name) {
		super(name, domain);
	}

}
