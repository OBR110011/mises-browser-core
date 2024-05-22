package org.chromium.chrome.browser.mises;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.Canvas;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.chromium.chrome.mises.R;
import org.chromium.chrome.browser.tabmodel.TabCreatorManager;
import org.chromium.chrome.browser.tabmodel.TabModel;
import org.chromium.chrome.browser.tabmodel.TabCreator;
import org.chromium.ui.widget.LoadingView;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.kirich1409.svgimageloaderplugins.GlideApp;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;

import org.chromium.net.ChromiumNetworkAdapter;
import org.chromium.net.NetworkTrafficAnnotationTag;
import androidx.annotation.Nullable;
import org.chromium.chrome.browser.tabmodel.TabCreatorManager;
import com.google.firebase.analytics.FirebaseAnalytics;
import org.chromium.base.task.AsyncTask;
import org.chromium.base.MisesController;
import org.chromium.chrome.browser.base.HttpUtil;

public class MisesShareWin extends DialogFragment {

    private static final String TAG = "MisesShareWin";

    private Context mContext;

    private FrameLayout view;

    private Button btn_share;
    private Button btn_cancel;
    private TextView tv_title;
    private TextView tv_url;
    private ImageView image;
    private String mIcon;
    private String mTitle;
    private String mUrl;
    private ImageResult mImageResult;
    private EditText comment;
    private LoadingView mLoadingView;
    private static final int THREAD_ID = 10000;
    private final TabCreatorManager mTabCreatorManager;

    private class ImageResult {
        public String mContentType = "image/png";
        public byte[] mImageData;
    }

    private class MisesShareTask extends AsyncTask<Integer> {
        private String mToken;
        private String mText;
        private String mMisesImageUrl = "";
	public MisesShareTask(String token, String text) {
            mToken = token;
            mText = text;
        }

        private int uploadImageToMises(ImageResult imageResult) {
            int resCode = -1;
            if (imageResult == null || imageResult.mImageData == null || imageResult.mImageData.length == 0)
                return resCode;
            String end = "\r\n";
            String twoHyphens = "--";
            String boundary = "MyBoundary" + System.currentTimeMillis();
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(MisesController.MISES_API_BASE_URL + "/api/v1/upload");
                urlConnection = (HttpURLConnection) ChromiumNetworkAdapter.openConnection(url, NetworkTrafficAnnotationTag.MISSING_TRAFFIC_ANNOTATION);
                urlConnection.setConnectTimeout(20000);
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("POST");
                String userAgent = "Mises Browser";
                urlConnection.setRequestProperty("User-Agent", userAgent);
                urlConnection.setRequestProperty("Connection", "Keep-alive");
                urlConnection.setRequestProperty("Charset", "UTF-8");
                urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                urlConnection.setRequestProperty("Authorization", "Bearer " + mToken);

                DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + end);
                dos.writeBytes("Content-Disposition: form-data; name=\"file_type\"" + end + end);
                dos.writeBytes("image" + end);

                dos.writeBytes(twoHyphens + boundary + end);
                dos.writeBytes("Content-Disposition: form-data; name=\"file\";");
                int nPos = imageResult.mContentType.indexOf("/");
                String ext = ".png";
                if (nPos != -1) {
                    ext = "." + imageResult.mContentType.substring(nPos + 1);
                }
                dos.writeBytes("filename=\"" + System.currentTimeMillis() + ext + "\"" + end);
                dos.writeBytes("Content-Type: " + imageResult.mContentType + end + end);
                dos.write(imageResult.mImageData);
                dos.writeBytes(end);
                String endStr = twoHyphens + boundary + twoHyphens + end;
                dos.write(endStr.getBytes());

                resCode = urlConnection.getResponseCode();
                Log.e(TAG, "upload image to mises " + resCode);

                if (resCode == 200) {
                    InputStream is = urlConnection.getInputStream();
                    ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    int i = is.read();
                    while (i != -1) {
                        bo.write(i);
                        i = is.read();
                    }
                    is.close();
                    String resJson = bo.toString();
                    Log.e(TAG, "upload image to mises " + resJson);
                    JSONObject resJsonObject = new JSONObject(resJson);
                    int code = -1;
                    if (resJsonObject.has("code")) {
                        code = resJsonObject.getInt("code");
                    }
                    if (code == 0) {
                        if (resJsonObject.has("data")) {
                            JSONObject dataObj = resJsonObject.getJSONObject("data");
                            if (dataObj.has("path"))
                                mMisesImageUrl = dataObj.getString("path");
                        }
                    }
                } else {
                    InputStream is = urlConnection.getErrorStream();
                    ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    int i = is.read();
                    while (i != -1) {
                        bo.write(i);
                        i = is.read();
                    }
                    is.close();
                    String err = bo.toString();
                    Log.e(TAG, "upload image to mises " + err);
                }
            } catch (JSONException e) {
                Log.e(TAG, "Upload image error " + e.toString());
            } catch (MalformedURLException e) {
                Log.e(TAG, "Upload image error " + e.toString());
            } catch (IOException e) {
                Log.e(TAG, "Upload image error " + e.toString());
            } catch (IllegalStateException e) {
                Log.e(TAG, "Upload image error " + e.toString());
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
            return resCode;
        }

        private Integer PostToMises(String attachUrl) {
            JSONObject root = new JSONObject();
            try {
                root.put("status_type", "link");
                root.put("from_type","forward");
                root.put("content", mText);
                JSONObject linkObject = new JSONObject();
                linkObject.put("title", mTitle);
                URL uri = new URL(mUrl);
                linkObject.put("host", uri.getHost());
                linkObject.put("link", mUrl);
                linkObject.put("attachment_path", attachUrl);
                root.put("link_meta", linkObject);
            } catch (JSONException e) {
                Log.d(TAG, "Unable to write to a JSONObject.");
            } catch (MalformedURLException e) {
                Log.e(TAG, "Unable to write to a JSONObject." + e.toString());
            } 

            HttpUtil.HttpResp result = HttpUtil.JsonPostSync(MisesController.MISES_API_BASE_URL + "/api/v1/status",root, mToken, "");
            return result.code;
        }


        @Override
        protected Integer doInBackground() {
            int res = -1;
            final ImageResult ir = mImageResult;
            if (ir != null && ir.mImageData != null) {
                res = uploadImageToMises(ir);
                if (res == 200 && !mMisesImageUrl.isEmpty()) {
                    res =  PostToMises(mMisesImageUrl);
            }
            } else {
                Log.e("mises", "share image is null");
                res =  PostToMises("");   
            }
            return res;
        }

        @Override
        protected void onPostExecute(Integer res) {
            Log.e(TAG, "mises share action " + res);
            mLoadingView.hideLoadingUI();
            if (res == 0) {
                dismiss();
                Toast.makeText(mContext, "Share success", Toast.LENGTH_SHORT).show();
            } else if (res == 403) {
                MisesController.getInstance().setLastShareInfo(mIcon, mTitle, mUrl);
                dismiss();
                UIUtil.showAlertDialog(mContext, mContext.getString(R.string.lbl_auth_tip), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TabCreator tabCreator = mTabCreatorManager.getTabCreator(false);
                        if (tabCreator != null) {
                            tabCreator.openSinglePage(MisesController.MISES_WALLET_BASE_URL);
                        }
                    }
                });
            } else {
                Toast.makeText(mContext, "Share failed", Toast.LENGTH_SHORT).show();
            }
            Bundle params = new Bundle();
            params.putString("step", "finish");
            params.putString("resp", res.toString());
            FirebaseAnalytics.getInstance(getContext()).logEvent("mises_share", params);
        }
    }

    public MisesShareWin(TabCreatorManager tabMgr) {
	    mTabCreatorManager = tabMgr;
    }

    public static MisesShareWin newInstance(TabCreatorManager tabMgr, String icon, String title, String url) {
        MisesShareWin f = new MisesShareWin(tabMgr);

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("icon", icon);
        args.putString("title", title);
        args.putString("url", url);
        f.setArguments(args);

        return f;
    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
    	Bitmap bitmap = null;

    	if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

    	if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
    	} else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
    	}

    	Canvas canvas = new Canvas(bitmap);
    	drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
    	drawable.draw(canvas);
        return bitmap;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = (FrameLayout) inflater.inflate(R.layout.mises_share_dialog, container, false);
        image = view.findViewById(R.id.icon);
        tv_title = view.findViewById(R.id.title);
        tv_url = view.findViewById(R.id.url);
        btn_share = (Button) view.findViewById(R.id.btn_share);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        comment = (EditText) view.findViewById(R.id.comment);
        mContext = getContext();

        mIcon = getArguments().getString("icon");
        mTitle = getArguments().getString("title");
        mUrl = getArguments().getString("url");
        tv_title.setText(mTitle);
        tv_url.setText(mUrl);
        // 取消按钮
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 销毁弹出框
                dismiss();
                Bundle params = new Bundle();
                params.putString("step", "cancel");
                FirebaseAnalytics.getInstance(getContext()).logEvent("mises_share", params);
            }
        });
        // 设置按钮监听
        btn_share.setEnabled(false);
        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLoadingView.showLoadingUI();
                MisesShareTask task = new MisesShareTask(MisesController.getInstance().getMisesToken(), comment.getText().toString().trim());
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                Bundle params = new Bundle();
                params.putString("step", "share");
                FirebaseAnalytics.getInstance(getContext()).logEvent("mises_share", params);
            }
        });

        mLoadingView = new LoadingView(mContext);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        mLoadingView.setLayoutParams(lp);
        mLoadingView.setVisibility(View.GONE);
        view.addView(mLoadingView);
        mLoadingView.showLoadingUI();
        GlideApp.with(mContext).asDrawable().load(Uri.parse(mIcon))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("mises", " MisesShareWin load pic failed" + e.toString() );
                        Toast.makeText(mContext, "load pic failed", Toast.LENGTH_SHORT).show();
                        mLoadingView.hideLoadingUI();
			mImageResult = new ImageResult();
			btn_share.setEnabled(true);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mLoadingView.hideLoadingUI();
                        if (resource != null) {
			    Bitmap bitmap = drawableToBitmap(resource);
                            ByteArrayOutputStream obs = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 50, obs);
                            mImageResult = new ImageResult();
                            mImageResult.mImageData = obs.toByteArray();
                            btn_share.setEnabled(true);
                        }
                        return false;
                    }
                })
                .into(image);
        return view;
    }

    @Override
    public void onStart() {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((WindowManager.LayoutParams) params);
        
        Bundle bundleParams = new Bundle();
        bundleParams.putString("step", "begin");
        FirebaseAnalytics.getInstance(getContext()).logEvent("mises_share", bundleParams);
            super.onStart();
    }
}
