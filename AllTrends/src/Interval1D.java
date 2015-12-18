


public class Interval1D implements Comparable<Interval1D> {
    public int low;   // left endpoint
    public int high;  // right endpoint
    public String type;

    // precondition: left <= right
    public Interval1D(int left, int right, String type) {
        if (left <= right) {
            this.low  = left;
            this.high = right;
            this.type = type;
        }
        else throw new RuntimeException("Illegal interval");
    }

    public Interval1D(int left, int right) {
        if (left <= right) {
            this.low  = left;
            this.high = right;
        }
        else throw new RuntimeException("Illegal interval");
    }

    // does this interval intersect that one?
    public boolean intersects(Interval1D that) {
        if (that.high < this.low) return false;
        if (this.high < that.low) return false;
        return true;
    }

    // does this interval a intersect b?
    public boolean contains(int x) {
        return (low <= x) && (x <= high);
    }

    public int compareTo(Interval1D that) {
        if      (this.low  < that.low)  return -1;
        else if (this.low  > that.low)  return +1;
        else if (this.high < that.high) return -1;
        else if (this.high > that.high) return +1;
        else                            return  0;
    }

    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + high;
		result = prime * result + low;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Interval1D other = (Interval1D) obj;
		if (high != other.high)
			return false;
		if (low != other.low)
			return false;
		return true;
	}

	public String toString() {
        //return "[" + type + ": " + low + ", " + high + "]";
    	if (type==null){
    		return "[" + low + ", " + high + "]";	
    	}
    	return "["+ type+ ": " + low + "-" + high + "]";
    }




    // test client
    public static void main(String[] args) {
        /*
    	Interval1D a = new Interval1D(15, 20,"a");
        Interval1D b = new Interval1D(25, 30, "b");
        Interval1D c = new Interval1D(10, 40, "c");
        Interval1D d = new Interval1D(40, 50, "d");
        */

        Interval1D a = new Interval1D(15, 20);
        Interval1D b = new Interval1D(25, 30);
        Interval1D c = new Interval1D(10, 40);
        Interval1D d = new Interval1D(40, 50);

        System.out.println(a); // "a = " + a);
        System.out.println(b); //"b = " + b);
        System.out.println(c); //"c = " + c);
        System.out.println(d); //"d = " + d);

        System.out.println("b intersects a = " + b.intersects(a));
        System.out.println("a intersects b = " + a.intersects(b));
        System.out.println("a intersects c = " + a.intersects(c));
        System.out.println("a intersects d = " + a.intersects(d));
        System.out.println("b intersects c = " + b.intersects(c));
        System.out.println("b intersects d = " + b.intersects(d));
        System.out.println("c intersects d = " + c.intersects(d));

    }

}


