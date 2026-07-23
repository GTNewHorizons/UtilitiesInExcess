package com.fouristhenumber.utilitiesinexcess.common;

/**
 * A simple interface for making sure block and item instances can access their registry names.
 */
public interface IUIERegistered {

    void setRegistryName(String name);

    String getRegistryName();
}
