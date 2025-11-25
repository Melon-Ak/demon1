package com.example.demon1;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class ActionModeListActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private ListView animal_list;  // 修改变量名为animal_list
    private ArrayAdapter<String> adapter;
    private ArrayList<String> dataList;
    private ActionMode actionMode;
    private int selectedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化数据
        dataList = new ArrayList<>(Arrays.asList(
                "Item 1", "Item 2", "Item 3", "Item 4", "Item 5",
                "Item 6", "Item 7", "Item 8", "Item 9", "Item 10"
        ));

        // 设置 ListView (使用animal_list)
        animal_list = findViewById(R.id.animal_list);  // 使用新的变量名
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, dataList);
        animal_list.setAdapter(adapter);
        animal_list.setOnItemLongClickListener(this);
        animal_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        animal_list.setMultiChoiceModeListener(new MultiChoiceModeListener());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (actionMode != null) {
            return false;
        }
        animal_list.setItemChecked(position, true);  // 使用animal_list
        return true;
    }

    private class MultiChoiceModeListener implements ListView.MultiChoiceModeListener {

        @Override
        public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            if (checked) {
                selectedCount++;
            } else {
                selectedCount--;
            }
            mode.setTitle(selectedCount + " selected");
            adapter.notifyDataSetChanged();
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            actionMode = mode;
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            mode.setTitle(selectedCount + " selected");
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            // 使用if-else代替switch，避免资源ID问题
            if (item.getItemId() == R.id.menu_delete) {
                deleteSelectedItems();
                mode.finish();
                return true;
            } else if (item.getItemId() == R.id.menu_select_all) {
                selectAllItems();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            selectedCount = 0;
            for (int i = 0; i < animal_list.getCount(); i++) {  // 使用animal_list
                animal_list.setItemChecked(i, false);  // 使用animal_list
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void deleteSelectedItems() {
        for (int i = animal_list.getCount() - 1; i >= 0; i--) {  // 使用animal_list
            if (animal_list.isItemChecked(i)) {  // 使用animal_list
                dataList.remove(i);
            }
        }
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Deleted selected items", Toast.LENGTH_SHORT).show();
    }

    private void selectAllItems() {
        for (int i = 0; i < animal_list.getCount(); i++) {  // 使用animal_list
            animal_list.setItemChecked(i, true);  // 使用animal_list
        }
        selectedCount = animal_list.getCount();  // 使用animal_list
        actionMode.setTitle(selectedCount + " selected");
    }
}