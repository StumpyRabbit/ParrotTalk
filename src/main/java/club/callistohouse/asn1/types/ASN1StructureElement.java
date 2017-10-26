package club.callistohouse.asn1.types;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;

public class ASN1StructureElement extends ASN1ChoiceElement {
	public boolean optional = true;
	public Object defaultValue;

	public boolean hasDefault() { return defaultValue != null; }
	public boolean isOptional() { return optional; }
	public boolean hasDefaultOrIsOptional() { return hasDefault() | isOptional(); }
	public boolean isTypeFor(Object obj) {
		Object val = valueFrom(obj);
		return type.isTypeFor(val);
	}

	public void encode(Object obj, ASN1OutputStream derStream) throws IOException {
		Object val = valueFrom(obj);
		if((val == null) & isOptional()) {
			return;
		}
		if(hasDefault() & (defaultValue == val)) {
			return;			
		}
		type.encode(val, derStream, obj, owner);
	}
	public void decode(Object target, ASN1InputStream derStream) throws IOException, InstantiationException, IllegalAccessException {
		if(derStream.stream.available() == 0) {
			if(hasDefault()) {
				valueInto(defaultValue, target);
				return;
			}
			if(isOptional()) { return; }
			throw new IOException("missing element");
		}

		int tag = derStream.peekTag();
		if(matchesTag(tag)) {
			Object val = type.decode(derStream, target, owner);
			valueInto(val, target);
			return;			
		} else {
			if(hasDefault()) {
				valueInto(defaultValue, target);
				return;
			}
			if(isOptional()) { return; }
			throw new IOException("missing element");
		}
	}
	public void valueInto(Object value, Object obj) {
		String mutator = "set";
		final StringBuilder ret = new StringBuilder(3 + symbol.length());
		ret.append(mutator);
		ret.append(symbol.substring(0, 1).toUpperCase());
		ret.append(symbol.substring(1));
		mutator = ret.toString();
		Method method = lookupMethodNamed(obj.getClass(), mutator, value.getClass());
		
		try {
			method.invoke(obj, value);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private Method lookupMethodNamed(Class<? extends Object> clazz, String mutator, Class<? extends Object> argClazz) {
		Method method = null;
		try {
			method = clazz.getMethod(mutator, argClazz);
		} catch (NoSuchMethodException e) {
			if(argClazz.equals(Object.class)) {
				return null;
			}
			if(argClazz.getSuperclass() == null ) {
				return lookupMethodNamed(clazz, mutator, Object.class);
			} else {
				return lookupMethodNamed(clazz, mutator, argClazz.getSuperclass());
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return method;
	}
	public Object valueFrom(Object obj) {
		String accessor = "get";
		final StringBuilder ret = new StringBuilder(3 + symbol.length());
		ret.append(accessor);
		ret.append(symbol.substring(0, 1).toUpperCase());
		ret.append(symbol.substring(1));
		accessor = ret.toString();
		Method method = null;
		try {
			method = obj.getClass().getMethod(accessor);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			return method.invoke(obj);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
