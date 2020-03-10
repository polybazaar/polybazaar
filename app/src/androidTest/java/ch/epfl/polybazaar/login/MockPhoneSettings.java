package ch.epfl.polybazaar.login;

/**
 * Singleton class that can be used to mock a device's settings
 */
public class MockPhoneSettings {
    private static MockPhoneSettings INSTANCE;
    private boolean airPlaneMode;

    private MockPhoneSettings() {
        airPlaneMode = false;
    }

    /**
     * Returns the singleton instance
     * @return instance
     */
    public static MockPhoneSettings getInstance() {
        if (INSTANCE == null){
            INSTANCE = new MockPhoneSettings();
        }
        return INSTANCE;
    }

    /**
     * Sets airplane mode on mock device
     * @param airPlaneMode value
     */
    public void setAirPlaneMode(boolean airPlaneMode) {
        this.airPlaneMode = airPlaneMode;
    }

    /**
     * Returns state of airplane mode
     * @return true iff airplane mode is on
     */
    public boolean isAirPlaneModeEnabled() {
        return airPlaneMode;
    }
}
