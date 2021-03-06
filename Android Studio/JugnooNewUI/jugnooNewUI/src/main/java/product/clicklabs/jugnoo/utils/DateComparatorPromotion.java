package product.clicklabs.jugnoo.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import product.clicklabs.jugnoo.datastructure.PromotionInfo;

public class DateComparatorPromotion implements Comparator<PromotionInfo>{

	@Override
	public int compare(PromotionInfo lhs, PromotionInfo rhs) {
		try{
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            int i = format.parse(DateOperations.utcToLocalWithTZFallback(lhs.endOn))
                    .compareTo(format.parse(DateOperations.utcToLocalWithTZFallback(rhs.endOn)));
            return i;
        }
        catch (Exception e){
        	e.printStackTrace();
            return 0;
        }
	}
}
