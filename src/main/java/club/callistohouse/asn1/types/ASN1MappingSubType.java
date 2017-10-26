package club.callistohouse.asn1.types;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import club.callistohouse.asn1.ASN1InputStream;
import club.callistohouse.asn1.ASN1OutputStream;

public class ASN1MappingSubType extends ASN1SubType {
	public ASN1Type mapping;
	public String mappingSymbol;

	public ASN1MappingSubType() { super(null); }

	public Object accessField(Object host) {
		Class<?> hostClass = host.getClass();
		try {
			Method accessMethod = hostClass.getMethod(mappingSymbol);
			return accessMethod.invoke(host);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	public void mutateField(Object host, Object field) {
		
	}
	public void encode(Object obj, ASN1OutputStream derStream) throws IOException {
		getParent().encode(accessField(obj), derStream);
	}

	public void encode(Object obj, ASN1OutputStream derStream, Object owner, ASN1Type ownerType) throws IOException {
		getParent().encode(obj, derStream, owner, ownerType);
	}
	@Override
	public void encodeValue(Object obj, ASN1OutputStream derStream) {
		// TODO Auto-generated method stub
		
	}

	public Object decode(ASN1InputStream derStream) throws IOException, InstantiationException, IllegalAccessException {
		return getParent().decode(derStream);
	}

	public Object decode(ASN1InputStream derStream, Object owner, ASN1Type ownerType) throws IOException, InstantiationException, IllegalAccessException {
		return getParent().decode(derStream, owner, ownerType);
	}

	public ASN1Type getMapping() { return mapping; }
	public void setMapping(ASN1Type mapping) { this.mapping = mapping; }
	public String getMappingSymbol() { return mappingSymbol; }
	public void setMappingSymbol(String mappingSymbol) { this.mappingSymbol = mappingSymbol; }

	@Override
	public int asn1Tag() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object decodeValue(ASN1InputStream derStream, int length) {
		// TODO Auto-generated method stub
		return null;
	}

}
