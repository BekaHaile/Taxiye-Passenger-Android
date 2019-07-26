package product.clicklabs.jugnoo.smartlock.callbacks;

public interface SmartlockCallbacks {
    void makePair(boolean status);
    void updateStatus(int status);
    void checkForBluetoth();
    void unableToPair(boolean status);
}
