package product.clicklabs.jugnoo.rentals;

class InstructionDialogModel {
    private String id;
    private String description;
    private String title;
    private String image;


    InstructionDialogModel(String title, String image, String description) {
        this.title = title;
        this.description = description;
        this.image = image;
    }

    String getDescription() {
        return description;
    }

    String getTitle() {
        return title;
    }

    String getImage() {
        return image;
    }

}
