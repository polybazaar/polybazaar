package ch.epfl.polybazaar.filestorage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.Tasks;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import ch.epfl.polybazaar.R;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ImageTransactionTest {
    private MockFileStore mock;
    private final Context ctx = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final Bitmap bm = BitmapFactory.decodeResource(ctx.getResources(), R.drawable.algebre_lin);
    private final static String NAME = "test-image.jpg";

    @Before
    public void setup() {
        mock = new MockFileStore();
        mock.setContext(ctx);
        FileStoreFactory.setDependency(mock);
        LocalCache.setRoot("test-cache");
    }

    @After
    public void cleanUp() {
        mock.cleanUp();
        LocalCache.cleanUp(ctx);
    }

    @Test
    public void storedFileIsCached() throws ExecutionException, InterruptedException {
        Tasks.await(ImageTransaction.store(NAME, bm, 100, ctx)
                .addOnFailureListener(e -> { throw new AssertionError(); }));

        assertTrue(LocalCache.hasFile(NAME, ctx));
    }

    @Test
    public void storedFileCanBeRetrieved() throws ExecutionException, InterruptedException {
        Tasks.await(ImageTransaction.store(NAME, bm, 100, ctx)
                .addOnFailureListener(e -> { throw new AssertionError(); }));

        Tasks.await(ImageTransaction.fetch(NAME, ctx).addOnSuccessListener(Assert::assertNotNull));
    }

    @Test
    public void cacheOnlyFileCanBeRetrieved() throws IOException, ExecutionException, InterruptedException {
        // we store the image directly in cache to check if it is retrieved even if it does not exist "remotely"
        OutputStream outStream = LocalCache.add(NAME, ctx);
        bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

        Tasks.await(ImageTransaction.fetch(NAME, ctx)
                .addOnFailureListener(e -> { throw new AssertionError(); })
                .addOnSuccessListener(Assert::assertNotNull)
        );
    }

    @Test
    public void invalidFileIdIsNull() throws ExecutionException, InterruptedException {
        Tasks.await(ImageTransaction.fetch("test-image-null.jpg", ctx)
                .addOnFailureListener(e -> { throw new AssertionError(); })
                .addOnSuccessListener(Assert::assertNull)
        );
    }

    @Test
    public void fileCanBeRetrievedAndCached() throws ExecutionException, InterruptedException {
        // we store the file directly "remotely" to check if retrieving works when cache does not have a copy
        InputStream input = ctx.getResources().openRawResource(R.drawable.algebre_lin);
        Tasks.await(mock.store(NAME, input));

        Tasks.await(ImageTransaction.fetch(NAME, ctx)
                .addOnFailureListener(e -> { throw new AssertionError(); })
                .addOnSuccessListener(Assert::assertNotNull)
        );
    }
}
