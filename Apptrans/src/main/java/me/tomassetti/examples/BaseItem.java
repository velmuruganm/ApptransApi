package me.tomassetti.examples;

import java.util.Set;

public abstract class BaseItem
{

    protected String name;
    protected String type;
    // Constructor

    public BaseItem(String basename, String basetype)
    {
        this.name = basename;
        this.type = basetype;
    }

    public  boolean IsComposite()
    {
        return true;
    }
    public abstract void Add(BaseItem baseitem);

}