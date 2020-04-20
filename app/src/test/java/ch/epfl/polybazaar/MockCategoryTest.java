package ch.epfl.polybazaar;

import org.junit.BeforeClass;
import org.junit.Test;

import ch.epfl.polybazaar.category.Category;
import ch.epfl.polybazaar.category.NodeCategory;
import ch.epfl.polybazaar.category.RootCategoryFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MockCategoryTest {
    private static Category mockRoot;
    @BeforeClass
    public static void setMockRoot(){
        RootCategoryFactory.useMockCategory();
        mockRoot = RootCategoryFactory.getDependency();
    }

    @Test
    public void testContainsIsCorrect(){
        assertThat(mockRoot.contains(new NodeCategory("Others")), is(true));
    }

    @Test
    public void maxDepthIsCorrect(){
        assertThat(mockRoot.maxDepth(), is(1));
    }
    
}
