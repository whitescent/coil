package coil.transform

import android.content.ContentResolver.SCHEME_FILE
import android.content.Context
import android.graphics.Bitmap
import android.os.Build.VERSION.SDK_INT
import androidx.core.graphics.drawable.toBitmap
import androidx.test.core.app.ApplicationProvider
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.request.animatedTransformation
import coil.util.assertIsSimilarTo
import coil.util.assumeTrue
import coil.util.decodeBitmapAsset
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AnimatedTransformationTest {

    private lateinit var context: Context
    private lateinit var imageLoader: ImageLoader

    @Before
    fun before() {
        context = ApplicationProvider.getApplicationContext()
        imageLoader = ImageLoader(context)
    }

    @Test
    fun gifTransformationTest() = runTest {
        val decoderFactory = if (SDK_INT >= 28) {
            ImageDecoderDecoder.Factory()
        } else {
            GifDecoder.Factory()
        }
        val imageRequest = ImageRequest.Builder(context)
            .data("$SCHEME_FILE:///android_asset/animated.gif")
            .animatedTransformation(RoundedCornersAnimatedTransformation())
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .decoderFactory(decoderFactory)
            .build()
        val actual = imageLoader.execute(imageRequest)
        val expected = context.decodeBitmapAsset("animated_gif_rounded.png")
        assertTrue(actual is SuccessResult)
        actual.drawable.toBitmap().assertIsSimilarTo(expected, threshold = 0.98)
    }

    @Test
    fun heifTransformationTest() = runTest {
        // Animated HEIF is only support on API 28+.
        assumeTrue(SDK_INT >= 28)

        val imageRequest = ImageRequest.Builder(context)
            .data("$SCHEME_FILE:///android_asset/animated.heif")
            .animatedTransformation(RoundedCornersAnimatedTransformation())
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .decoderFactory(ImageDecoderDecoder.Factory())
            .build()
        val actual = imageLoader.execute(imageRequest)
        val expected = context.decodeBitmapAsset("animated_heif_rounded.png")
        assertTrue(actual is SuccessResult)
        actual.drawable.toBitmap().assertIsSimilarTo(expected, threshold = 0.98)
    }

    @Test
    fun animatedWebpTransformationTest() = runTest {
        // Animated WebP is only support on API 28+.
        assumeTrue(SDK_INT >= 28)

        val imageRequest = ImageRequest.Builder(context)
            .data("$SCHEME_FILE:///android_asset/animated.webp")
            .animatedTransformation(RoundedCornersAnimatedTransformation())
            .bitmapConfig(Bitmap.Config.ARGB_8888)
            .decoderFactory(ImageDecoderDecoder.Factory())
            .build()
        val actual = imageLoader.execute(imageRequest)
        val expected = context.decodeBitmapAsset("animated_webp_rounded.png")
        assertTrue(actual is SuccessResult)
        actual.drawable.toBitmap().assertIsSimilarTo(expected, threshold = 0.98)
    }
}
