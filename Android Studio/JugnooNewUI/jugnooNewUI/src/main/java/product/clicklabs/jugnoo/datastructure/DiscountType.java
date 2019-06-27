package product.clicklabs.jugnoo.datastructure;

public class DiscountType {

	public String name;
	public double value;
	private int referenceId;

	public DiscountType(String name, double value, int referenceId){
		this.name = name;
		this.value = value;
		this.referenceId = referenceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(int referenceId) {
		this.referenceId = referenceId;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof DiscountType && ((DiscountType) obj).referenceId == referenceId;
	}
}
