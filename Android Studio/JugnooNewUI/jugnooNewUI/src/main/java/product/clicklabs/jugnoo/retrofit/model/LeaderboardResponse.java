package product.clicklabs.jugnoo.retrofit.model;

/**
 * Created by socomo on 12/30/15.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;



public class LeaderboardResponse {

    @SerializedName("global")
    @Expose
    private Global global;
    @SerializedName("local")
    @Expose
    private Local local;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("flag")
    @Expose
    private Integer flag;

    /**
     * @return The global
     */
    public Global getGlobal() {
        return global;
    }

    /**
     * @param global The global
     */
    public void setGlobal(Global global) {
        this.global = global;
    }

    /**
     * @return The local
     */
    public Local getLocal() {
        return local;
    }

    /**
     * @param local The local
     */
    public void setLocal(Local local) {
        this.local = local;
    }

    /**
     * @return The date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return The flag
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     * @param flag The flag
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public class Daily {

        @SerializedName("ranklist")
        @Expose
        private List<Ranklist> ranklist = new ArrayList<Ranklist>();
        @SerializedName("userinfo")
        @Expose
        private Userinfo userinfo;

        /**
         * @return The ranklist
         */
        public List<Ranklist> getRanklist() {
            return ranklist;
        }

        /**
         * @param ranklist The ranklist
         */
        public void setRanklist(List<Ranklist> ranklist) {
            this.ranklist = ranklist;
        }

        /**
         * @return The userinfo
         */
        public Userinfo getUserinfo() {
            return userinfo;
        }

        /**
         * @param userinfo The userinfo
         */
        public void setUserinfo(Userinfo userinfo) {
            this.userinfo = userinfo;
        }

    }

    public class Global {

        @SerializedName("daily")
        @Expose
        private Daily daily;
        @SerializedName("weekly")
        @Expose
        private Weekly weekly;

        /**
         * @return The daily
         */
        public Daily getDaily() {
            return daily;
        }

        /**
         * @param daily The daily
         */
        public void setDaily(Daily daily) {
            this.daily = daily;
        }

        /**
         * @return The weekly
         */
        public Weekly getWeekly() {
            return weekly;
        }

        /**
         * @param weekly The weekly
         */
        public void setWeekly(Weekly weekly) {
            this.weekly = weekly;
        }

    }


    public class Local {

        @SerializedName("daily")
        @Expose
        private Daily daily;
        @SerializedName("weekly")
        @Expose
        private Weekly weekly;

        /**
         * @return The daily
         */
        public Daily getDaily() {
            return daily;
        }

        /**
         * @param daily The daily
         */
        public void setDaily(Daily daily) {
            this.daily = daily;
        }

        /**
         * @return The weekly
         */
        public Weekly getWeekly() {
            return weekly;
        }

        /**
         * @param weekly The weekly
         */
        public void setWeekly(Weekly weekly) {
            this.weekly = weekly;
        }

    }

    public class Ranklist {

        @SerializedName("downloads")
        @Expose
        private String downloads;
        @SerializedName("rank")
        @Expose
        private Integer rank;
        @SerializedName("name")
        @Expose
        private String name;

        private boolean isUser = false;

        /**
         * @return The downloads
         */
        public String getDownloads() {
            return downloads;
        }

        /**
         * @param downloads The downloads
         */
        public void setDownloads(String downloads) {
            this.downloads = downloads;
        }

        /**
         * @return The rank
         */
        public Integer getRank() {
            return rank;
        }

        /**
         * @param rank The rank
         */
        public void setRank(Integer rank) {
            this.rank = rank;
        }

        /**
         * @return The name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name The name
         */
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            try{
                if(((Ranklist)o).getRank().equals(this.getRank())){
                    return true;
                } else{
                    return false;
                }
            } catch(Exception e){
                return false;
            }
        }

        public boolean getIsUser() {
            return isUser;
        }

        public void setIsUser(boolean isUser) {
            this.isUser = isUser;
        }
    }

    public class Userinfo {

        @SerializedName("rank")
        @Expose
        private Integer rank;
        @SerializedName("downloads")
        @Expose
        private String downloads;

        /**
         * @return The rank
         */
        public Integer getRank() {
            return rank;
        }

        /**
         * @param rank The rank
         */
        public void setRank(Integer rank) {
            this.rank = rank;
        }

        /**
         * @return The downloads
         */
        public String getDownloads() {
            return downloads;
        }

        /**
         * @param downloads The downloads
         */
        public void setDownloads(String downloads) {
            this.downloads = downloads;
        }

    }

    public class Weekly {

        @SerializedName("ranklist")
        @Expose
        private List<Ranklist> ranklist = new ArrayList<Ranklist>();
        @SerializedName("userinfo")
        @Expose
        private Userinfo userinfo;

        /**
         * @return The ranklist
         */
        public List<Ranklist> getRanklist() {
            return ranklist;
        }

        /**
         * @param ranklist The ranklist
         */
        public void setRanklist(List<Ranklist> ranklist) {
            this.ranklist = ranklist;
        }

        /**
         * @return The userinfo
         */
        public Userinfo getUserinfo() {
            return userinfo;
        }

        /**
         * @param userinfo The userinfo
         */
        public void setUserinfo(Userinfo userinfo) {
            this.userinfo = userinfo;
        }

    }
}

