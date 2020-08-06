package me.tomassetti.examples;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ParentItem extends BaseItem
    {
        
		List<BaseItem> Items = new ArrayList<BaseItem>();
        public String linkname;
        public String linktype;
        // Constructor

        public ParentItem(String name, String type)
        {
        super(name, type);
            this.linkname = name;
            this.linktype = type;
        }

        public List<BaseItem> getItems() {
			return Items;
		}

		public void setItems(List<BaseItem> items) {
			Items = items;
		}

		public String getLinkname() {
			return linkname;
		}

		public void setLinkname(String linkname) {
			this.linkname = linkname;
		}

		public String getLinktype() {
			return linktype;
		}

		public void setLinktype(String linktype) {
			this.linktype = linktype;
		}

		public  void Add(BaseItem item)
        {
            Items.add(item);
        }
        
		public String toString() {
	        return "Name of the class:" + this.linkname + ",, "
	        		+"Name of the class:" + this.Items + ",, "
	                + "Type of the class:" + this.linktype;
	    }
      
    }


