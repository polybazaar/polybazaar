package ch.epfl.polybazaar.database.callback;

import ch.epfl.polybazaar.database.generic.GenericCallback;

public class SuccessCallbackAdapter implements GenericCallback {

    private SuccessCallback successcallback;

    /**
     * The resulting Callback behaves like a GenericCallback
     * @param successcallback the SuccessCallback to be adapted
     */
    public SuccessCallbackAdapter(SuccessCallback successcallback){
        this.successcallback = successcallback;
    }

    @Override
    public void onCallback(Object result) {
        if (result==null){
            successcallback.onCallback(false);
        }
        successcallback.onCallback((boolean) result);
    }
}
