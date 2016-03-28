package product.clicklabs.jugnoo.home.models;

public enum Vehicle {
	AUTO(1, "Auto"),
	BIKE(2, "Bike")
	;

	private int id;
	private String name;

	Vehicle(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName(){
		return name;
	}
}
