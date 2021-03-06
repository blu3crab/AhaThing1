/*
 * Project: Things
 * Contributor(s): M.A.Tucker, Adaptive Handy Apps, LLC
 * Origination: M.A.Tucker JAN 2017
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adaptivehandyapps.ahathing;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class TagListAdapter extends ArrayAdapter<String> {
    private static final String TAG = "TagListAdapter";

    private final Context mContext;
    private final int mTagListItemResId;
    private final List<String> mItemName;
    private final List<String> mItemLabel;
    private final List<Integer> mImageResId;
    private final List<Integer> mBgColor;

    ///////////////////////////////////////////////////////////////////////////
    public TagListAdapter(Context context, int resId, List<String> itemname, List<String> itemlabel, List<Integer> imgid, List<Integer> bgColor) {
        super(context, resId, itemname);
        if ((itemname.size() != imgid.size()) || (itemname.size() != bgColor.size())) {
            Log.e(TAG, "Oops! TagListAdapter find mismatched list size!");
        }
        this.mTagListItemResId = resId;
        this.mContext = context;
        this.mItemName = itemname;
        this.mItemLabel = itemlabel;
        this.mImageResId = imgid;
        this.mBgColor = bgColor;
    }
    ///////////////////////////////////////////////////////////////////////////
    public View getView(int position, View view, ViewGroup parent) {

        Activity activity = (Activity) mContext;
        LayoutInflater inflater = activity.getLayoutInflater();
        int layoutId = mTagListItemResId;
        final View rowView=inflater.inflate(layoutId, null, true);

//        rowView.setBackgroundColor(mContext.getResources().getColor(R.color.colorLightGrey));
        rowView.setBackgroundColor(mBgColor.get(position));

        TextView txtTitle = (TextView) rowView.findViewById(R.id.tv_tag_name);
        txtTitle.setText(mItemName.get(position));

        TextView txtLabel = (TextView) rowView.findViewById(R.id.tv_tag_label);
        if (mItemLabel != null && mItemLabel.size() > position) {
            txtLabel.setText(mItemLabel.get(position));
            txtLabel.setVisibility(View.VISIBLE);
        }
//        else {
//            txtLabel.setVisibility(View.GONE);
//        }

        ImageView imageView = (ImageView) rowView.findViewById(R.id.iv_tag_image);
        imageView.setImageResource(mImageResId.get(position));

        return rowView;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean setName(int position, String text) {
        if (position >= 0 && position < mItemName.size()) {
            mItemName.set(position, text);
            return true;
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean setLabel(int position, String text) {
        if (position >= 0 && position < mItemLabel.size()) {
            mItemLabel.set(position, text);
            return true;
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean setImageResId(int position, int resId) {
        if (position >= 0 && position < mImageResId.size()) {
            mImageResId.set(position, resId);
            return true;
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
    public Boolean setBgColor(int position, int bgColor) {
        if (position >= 0 && position < mBgColor.size()) {
            mBgColor.set(position, bgColor);
            return true;
        }
        return false;
    }
    ///////////////////////////////////////////////////////////////////////////
}
