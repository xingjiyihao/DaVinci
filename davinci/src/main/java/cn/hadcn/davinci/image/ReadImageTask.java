package cn.hadcn.davinci.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.nio.ByteBuffer;

import cn.hadcn.davinci.R;
import cn.hadcn.davinci.base.ImageLoader;

/**
 * read image from any where
 * Created by 90Chris on 2016/5/5.
 */
public class ReadImageTask extends AsyncTask<String, Integer, ByteBuffer> {
    private final int DEFAULT_IMAGE_LOADING = R.drawable.image_loading;
    private final int DEFAULT_IMAGE_ERROR = R.drawable.image_load_error;

    private ImageView mImageView;
    private String mImageUrl;
    private int mLoadingImage = DEFAULT_IMAGE_LOADING;
    private int mErrorImage = DEFAULT_IMAGE_ERROR;
    private ImageLoader.ImageCache mImageCache;
    private ImageLoader mImageLoader;
    private Context mContext;
    private int mMaxSize;

    public ReadImageTask(Context context, ImageLoader.ImageCache imageCache, ImageLoader imageLoader, String imageUrl) {
        mImageUrl = imageUrl;
        mImageCache = imageCache;
        mImageLoader = imageLoader;
        mContext = context;
    }

    @Override
    protected ByteBuffer doInBackground(String... params) {
        if ( mImageUrl == null || mImageUrl.isEmpty() || !mImageUrl.startsWith("http") ) {
            return null;
        }
        return mImageCache.getBitmap(mImageUrl);
    }

    @Override
    protected void onPostExecute(ByteBuffer byteBuffer) {
        if ( byteBuffer != null ) {
            byte[] bytes = byteBuffer.array();

            // if it's gif, show as gif
            if ( Util.doGif(mImageView, bytes) ) return;

            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mImageView.setImageBitmap(image);
        } else {
            VolleyImageListener listener = new VolleyImageListener(mContext, mImageView, mImageCache);
            listener.setDefaultImage(mLoadingImage, mErrorImage);
            listener.setMaxSize(mMaxSize);
            mImageLoader.get(mImageUrl, listener);
        }
    }

    protected void setView(ImageView imageView, int image_loading, int image_error) {
        mImageView = imageView;
        mLoadingImage = image_loading;
        mErrorImage = image_error;
    }

    protected void setView(ImageView imageView) {
        mImageView = imageView;
    }

    protected void setSize(int size) {
        mMaxSize = size;
    }
}