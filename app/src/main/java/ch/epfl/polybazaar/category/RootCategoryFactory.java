package ch.epfl.polybazaar.category;

import android.content.Context;

public class RootCategoryFactory {
    private static Category dependency;

    public static Category getDependency(){
        if(dependency == null){
            throw new IllegalStateException("Choose to use mock category or JSON category before getting the dependency");
        }
        return dependency;
    }

    /**
     * Use this static method to use the mock category
     */
    public static void useMockCategory(){
        dependency = MockRootCategory.getInstance();
    }

    /**
     * Use this method to use the JSON category. Be careful that it is only effective is mock category isn't being used.
     * @param context Context of the activity. Needed to find the json file
     */
    public static void useJSONCategory(Context context) {
        if (dependency == null) {
            dependency = JSONRootCategory.getInstance(context);
        }
    }

    /**
     * Set the root category to null
     */
    public static void clear(){
        dependency = null;
    }

}
