package product.clicklabs.jugnoo.datastructure;

/**
 * Created by shankar on 12/30/15.
 */
public class LeaderboardItem {
	public int rank, downloads;
	public String name;
	public boolean isUser;

	public LeaderboardItem(int rank, int downloads, String name, boolean isUser){
		this.rank = rank;
		this.downloads = downloads;
		this.name = name;
		this.isUser = isUser;
	}
}
