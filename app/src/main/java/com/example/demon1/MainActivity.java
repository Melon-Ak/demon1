package com.example.demon1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ListView animalListView; // 修改变量名为 animalListView 以匹配 ID
    private Button addAnimalButton;

    // 创建动物数据
    private List<Map<String, Object>> animalData;

    // 通知渠道ID
    private static final String CHANNEL_ID = "animal_notifications";

    // 定义请求码，用于识别权限申请结果
    private static final int REQUEST_CODE_NOTIFY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化视图
        animalListView = findViewById(R.id.animal_list); // 确保 ID 匹配
        addAnimalButton = findViewById(R.id.add_animal_button);

        // 创建并初始化数据
        initData();

        // 创建适配器
        SimpleAdapter adapter = new SimpleAdapter(
                this,
                animalData,
                R.layout.list_item_animal,
                new String[]{"name", "image"},
                new int[]{R.id.animal_name, R.id.animal_image}
        );

        // 设置适配器
        animalListView.setAdapter(adapter);

        // 设置列表项点击事件
        animalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 显示Toast
                Map<String, Object> item = animalData.get(position);
                String animalName = (String) item.get("name");
                Toast.makeText(MainActivity.this, "你选择了：" + animalName, Toast.LENGTH_SHORT).show();

                // 发送通知
                sendNotification(animalName);
            }
        });

        // 添加动物按钮点击事件
        addAnimalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 示例：添加新的动物项
                // 您可以根据需要修改这一部分
                Map<String, Object> newAnimal = new HashMap<>();
                newAnimal.put("name", "新动物");
                animalData.add(newAnimal);
                adapter.notifyDataSetChanged();
            }
        });

        // 创建通知渠道（仅适用于Android 8.0及以上版本）
        createNotificationChannel();
    }

    // 初始化动物数据
    private void initData() {
        animalData = new ArrayList<>();

        // 添加各种动物
        addItem("Lion", R.drawable.lion);
        addItem("Tiger", R.drawable.tiger);
        addItem("Monkey", R.drawable.monkey);
        addItem("Dog", R.drawable.dog);
        addItem("Cat", R.drawable.cat);
        addItem("Elephant", R.drawable.elephant);
    }

    // 辅助方法：添加动物项
    private void addItem(String name, int imageResId) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("image", imageResId);
        animalData.add(item);
    }

    // 创建通知渠道
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            // 注册渠道
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // 发送通知
    private void sendNotification(String animalName) {
        // 检查是否已有通知权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // 没有权限，发起申请
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_CODE_NOTIFY);
        } else {
            // 已经有权限，直接发通知
            sendNotificationInternal(animalName);
        }
    }

    // 处理权限申请结果
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_NOTIFY) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限已授予，可以发送通知", Toast.LENGTH_SHORT).show();
            } else {
                // 用户拒绝了，给个提示
                Toast.makeText(this, "需要通知权限才能发送提醒", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 内部方法，用于发送通知（假设已经有权限）
    private void sendNotificationInternal(String animalName) {
        // 创建点击通知后打开主活动的Intent
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // 创建通知构建器
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // 确保 res/drawable 下有 ic_notification.png
                .setContentTitle(animalName) // 使用列表项名称作为通知标题
                .setContentText("你点击了 " + animalName + "，这是一条通知！") // 通知内容
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true); // 点击后自动消失

        // 显示通知
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }
}