package product.clicklabs.jugnoo.datastructure;

/**
 * Created by socomo20 on 6/30/15.
 */
public class EmergencyContact {

    public int id, userId, verificationStatus;
    public String name, email, phoneNo;

    public EmergencyContact(int id, int userId, String name, String email, String phoneNo, int verificationStatus){
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.verificationStatus = verificationStatus;
    }

    public EmergencyContact(int id){
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        try{
            if(((EmergencyContact)o).id == this.id){
                return true;
            }
            else{
                return false;
            }
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
}