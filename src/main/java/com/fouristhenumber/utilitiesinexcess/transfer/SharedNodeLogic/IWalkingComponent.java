package com.fouristhenumber.utilitiesinexcess.transfer.SharedNodeLogic;

// Honestly I don't like how I'm doing this with the inheritance.
public interface IWalkingComponent<T> extends INodeLogicHost
{
    T getWalkingObject();
}
