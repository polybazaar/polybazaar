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

    public static void useMockCategory(){
        if(dependency == null){
            System.out.println("HIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII");
        }
        dependency = MockRootCategory.getInstance();
    }

    public static void useJSONCategory(Context context) {
        if (dependency == null) {
            dependency = RootCategory.getInstance(context);
        }
    }

}
