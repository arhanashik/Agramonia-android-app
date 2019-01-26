package com.workfort.apps.agramonia.ui.base.helper;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class LinearHorizontalMarginItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * Fields
     */
    private int mTop, mBottom, mLeft, mRight;

    /**
     * Constructors
     */
    public LinearHorizontalMarginItemDecoration(int space) {
        setTop(space);
        setBottom(space);
        setLeft(space);
        setRight(space);
    }

    public LinearHorizontalMarginItemDecoration(int top, int bottom, int left, int right) {
        setTop(top);
        setBottom(bottom);
        setLeft(left);
        setRight(right);
    }

    /**
     * Getter and setter methods for fields
     */
    private int getTop() {
        return mTop;
    }

    private void setTop(int top) {
        mTop = top;
    }

    private int getBottom() {
        return mBottom;
    }

    private void setBottom(int bottom) {
        mBottom = bottom;
    }

    private int getLeft() {
        return mLeft;
    }

    private void setLeft(int left) {
        mLeft = left;
    }

    private int getRight() {
        return mRight;
    }

    private void setRight(int right) {
        mRight = right;
    }

    /**
     * This overridden method provides each item offsets from here
     *
     * @param outRect item rectangle
     * @param view    item view
     * @param parent  the recycler view
     * @param state   state of the recycler view
     */
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left = getLeft();
        }

        outRect.top = getTop();
        outRect.bottom = getBottom();
        outRect.right = getRight();
    }
}
