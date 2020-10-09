package org.cid15.aem.veneer.injectors.utils;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public final class InjectorUtils {

    public static SlingHttpServletRequest getRequest(final Object adaptable) {
        SlingHttpServletRequest request = null;

        if (adaptable instanceof SlingHttpServletRequest) {
            request = (SlingHttpServletRequest) adaptable;
        }

        return request;
    }

    public static Resource getResource(final Object adaptable) {
        Resource resource = null;

        if (adaptable instanceof Resource) {
            resource = (Resource) adaptable;
        } else if (adaptable instanceof SlingHttpServletRequest) {
            resource = ((SlingHttpServletRequest) adaptable).getResource();
        }

        return resource;
    }

    public static boolean isParameterizedListType(final Type declaredType) {
        final boolean isParameterizedType = declaredType instanceof ParameterizedType;

        return isParameterizedType && ((ParameterizedType) declaredType).getRawType() == List.class;
    }

    public static boolean isDeclaredTypeCollection(final Type declaredType) {
        boolean result = false;

        if (declaredType instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) declaredType;
            final Class collectionType = (Class) parameterizedType.getRawType();

            result = Collection.class.isAssignableFrom(collectionType);
        }

        return result;
    }

    public static Class<?> getDeclaredClassForDeclaredType(final Type declaredType) {
        Class<?> clazz;

        if (isDeclaredTypeCollection(declaredType)) {
            clazz = (Class) ((ParameterizedType) declaredType).getActualTypeArguments()[0];
        } else {
            clazz = (Class) declaredType;
        }

        return clazz;
    }

    public static Class<?> getActualType(final ParameterizedType declaredType) {
        final Type[] types = declaredType.getActualTypeArguments();

        return types != null ? (Class<?>) types[0] : null;
    }

    private InjectorUtils() {

    }
}
