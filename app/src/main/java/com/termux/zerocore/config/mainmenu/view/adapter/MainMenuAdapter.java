package com.termux.zerocore.config.mainmenu.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.termux.R;
import com.termux.zerocore.config.mainmenu.data.MainMenuCategoryData;
import com.termux.zerocore.config.mainmenu.view.viewholder.MainMenuViewHolder;
import com.termux.zerocore.ftp.utils.UserSetManage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuViewHolder> {
    private static String TAG = MainMenuAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<MainMenuCategoryData> mMainMenuCategoryData;
    private HashMap<Integer, MainMenuItemAdapter> mainMenuItemAdapters;

    public MainMenuAdapter(Context context, ArrayList<MainMenuCategoryData> mainMenuCategoryData) {
        mContext = context;
        mMainMenuCategoryData = mainMenuCategoryData;
        mainMenuItemAdapters = new HashMap<>();
    }

    @NonNull
    @Override
    public MainMenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainMenuViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_menu_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainMenuViewHolder holder, int position) {
        int id = mMainMenuCategoryData.get(position).mId;
        holder.mTitle.setText(mMainMenuCategoryData.get(position).mTitle);
        holder.mItemMenuRec.setLayoutManager(new GridLayoutManager(mContext, 3));
        MainMenuItemAdapter mainMenuItemAdapter = new MainMenuItemAdapter(mContext, mMainMenuCategoryData.get(position).mClickArrayList);
        mainMenuItemAdapters.put(position, mainMenuItemAdapter);
        holder.mItemMenuRec.setAdapter(mainMenuItemAdapter);
        // 始终显示折叠按钮，使用保存的展开状态决定子项可见性
        holder.mOpenSettings.setVisibility(View.VISIBLE);
        boolean mainMenuItemShow = UserSetManage.Companion.get().getMainMenuItemShow(String.valueOf(id));
        Log.i(TAG, "onBindViewHolder mainMenuItemShow: " + mainMenuItemShow);
        if (mainMenuItemShow) {
            holder.mOpenSettings.setRotation(180);
            holder.mItemMenuRec.setVisibility(View.VISIBLE);
        } else {
            holder.mOpenSettings.setRotation(0);
            holder.mItemMenuRec.setVisibility(View.GONE);
        }
        // 整个卡片点击切换
        holder.itemView.setOnClickListener(v -> {
            int visibility = holder.mItemMenuRec.getVisibility();
            if (visibility == View.VISIBLE) {
                holder.mOpenSettings.setRotation(0);
                holder.mItemMenuRec.setVisibility(View.GONE);
                UserSetManage.Companion.get().setMainMenuItemShow(
                        String.valueOf(mMainMenuCategoryData.get(position).mId), UserSetManage.Companion.getITEM_GEON());
            } else {
                holder.mOpenSettings.setRotation(180);
                holder.mItemMenuRec.setVisibility(View.VISIBLE);
                UserSetManage.Companion.get().setMainMenuItemShow(
                        String.valueOf(mMainMenuCategoryData.get(position).mId), UserSetManage.Companion.getITEM_VISIBLE());
            }
        });
        // 折叠图标也可点击
            holder.mOpenSettings.setOnClickListener(v -> holder.itemView.performClick());
    }
            holder.itemView.setOnClickListener(v -> {
                int visibility = holder.mItemMenuRec.getVisibility();
                if (visibility == View.VISIBLE) {
                    holder.mOpenSettings.setRotation(0);
                    holder.mItemMenuRec.setVisibility(View.GONE);
                    UserSetManage.Companion.get().setMainMenuItemShow(
                        String.valueOf(mMainMenuCategoryData.get(position).mId), UserSetManage.Companion.getITEM_GEON());
                } else {
                    holder.mOpenSettings.setRotation(180);
                    holder.mItemMenuRec.setVisibility(View.VISIBLE);
                    UserSetManage.Companion.get().setMainMenuItemShow(
                        String.valueOf(mMainMenuCategoryData.get(position).mId), UserSetManage.Companion.getITEM_VISIBLE());
                }
            });
            // 让折叠图标本身也可点击，提供相同的展开/收起功能
            holder.mOpenSettings.setOnClickListener(v -> {
                int visibility = holder.mItemMenuRec.getVisibility();
                if (visibility == View.VISIBLE) {
                    holder.mOpenSettings.setRotation(0);
                    holder.mItemMenuRec.setVisibility(View.GONE);
                    UserSetManage.Companion.get().setMainMenuItemShow(
                        String.valueOf(mMainMenuCategoryData.get(position).mId), UserSetManage.Companion.getITEM_GEON());
                } else {
                    holder.mOpenSettings.setRotation(180);
                    holder.mItemMenuRec.setVisibility(View.VISIBLE);
                    UserSetManage.Companion.get().setMainMenuItemShow(
                        String.valueOf(mMainMenuCategoryData.get(position).mId), UserSetManage.Companion.getITEM_VISIBLE());
                }
            });
            // 删除之前的 toggleGroup lambda，已不再使用

        } else {
            // 全局收起时不显示展开按钮并隐藏子项
            holder.mOpenSettings.setVisibility(View.GONE);
            holder.mItemMenuRec.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mMainMenuCategoryData.size();
    }

    public int findGroupIndexByName(String groupName) {
        if (groupName == null || groupName.trim().isEmpty()) {
            return -1;
        }
        for (int i = 0; i < mMainMenuCategoryData.size(); i++) {
            if (groupName.equals(mMainMenuCategoryData.get(i).mTitle)) {
                return i;
            }
        }
        return -1;
    }

    /** 折叠其他分组，展开指定选项卡。 */
    public void activateGroup(int index) {
        if (index < 0 || index >= mMainMenuCategoryData.size()) {
            return;
        }
        for (int i = 0; i < mMainMenuCategoryData.size(); i++) {
            String id = String.valueOf(mMainMenuCategoryData.get(i).mId);
            String state = (i == index)
                ? UserSetManage.Companion.getITEM_VISIBLE()
                : UserSetManage.Companion.getITEM_GEON();
            UserSetManage.Companion.get().setMainMenuItemShow(id, state);
        }
        notifyDataSetChanged();
    }

    public void release() {
        mContext = null;
        // 释放所有引用的 context 防止造成内存泄漏
        for (Map.Entry<Integer, MainMenuItemAdapter> entry : mainMenuItemAdapters.entrySet()) {
            entry.getValue().release();
        }
        mainMenuItemAdapters.clear();

        // 调用并且释放 Config 里边的release
        for (int i = 0; i < mMainMenuCategoryData.size(); i++) {
            MainMenuCategoryData mainMenuCategoryData = mMainMenuCategoryData.get(i);
            for (int j = 0; j < mainMenuCategoryData.mClickArrayList.size(); j++) {
                mainMenuCategoryData.mClickArrayList.get(j).release();
            }
        }
    }
}
