package me.tomassetti.examples;
import java.io.Console;
import java.util.Set;

    public  class ChildItem extends BaseItem
    {
        // Constructor
        public String subname ;
        public String getSubname() {
			return subname;
		}

		public void setSubname(String subname) {
			this.subname = subname;
		}

		public String getSubtype() {
			return subtype;
		}

		public void setSubtype(String subtype) {
			this.subtype = subtype;
		}
		public String subtype ;
        public ChildItem(String name, String type) 
        {
        super(name,type);
          subname = name;
          subtype = type;
        }

        public  boolean IsComposite()
        {
            return false;
        }
        public  void Add(BaseItem baseitem)
        {
            System.out.println("Cannot add to a leaf");
        }
        
        @Override
        public String toString() {
	        return "Name of the method:" + this.subname + ",, "
	                + "Type of the method:" + this.subtype ;
	    }
      
    }


