package com.fouristhenumber.utilitiesinexcess.utils;

public class MaskedArrayView<T>
{
    private int mask = 0;
    private final T[] array;

    public MaskedArrayView(int mask, T[] array)
    {
        this.mask = mask;
        this.array = array;
    }

    public int size()
    {
        return Integer.bitCount(mask);
    }

    public T get(int i)
    {
        int count = 0;

        for (int bit = 0; bit < array.length; bit++)
        {
            if ((mask & (1 << bit)) != 0)
            {
                if (count == i)
                {
                    return array[bit];
                }
                count++;
            }
        }

        throw new IndexOutOfBoundsException();
    }

    public int actualLocation(int index)
    {
        int location = 0;
        for (int bit = 0; bit < array.length; bit++)
        {
            if ((mask & (1 << bit)) != 0)
            {
                if (location == index)
                {
                    return bit;
                }
                location++;
            }
        }
        throw new IndexOutOfBoundsException();
    }
}
