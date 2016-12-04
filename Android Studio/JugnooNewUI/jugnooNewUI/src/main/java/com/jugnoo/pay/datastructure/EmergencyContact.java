package com.jugnoo.pay.datastructure;

/**
 * Created by socomo20 on 6/30/15.
 */
public class EmergencyContact {

    public int id;
    public String name, phoneNo;

    public EmergencyContact(int id, String name, String phoneNo){
        this.id = id;
        this.name = name;
        this.phoneNo = phoneNo;
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
