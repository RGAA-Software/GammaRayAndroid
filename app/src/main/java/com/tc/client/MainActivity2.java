//package com.tc.client;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.tc.client.util.SpUtils;
//
//public class MainActivity2 extends AppCompatActivity {
//    private static final String TAG = "Main";
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        EditText ipEt = findViewById(R.id.id_ip);
//        EditText portEt = findViewById(R.id.id_port);
//        String savedIp = SpUtils.getInstance(this).getString("ip");
//        if (!TextUtils.isEmpty(savedIp)) {
//            ipEt.setText(savedIp);
//        }
//        int savedPort = SpUtils.getInstance(this).getInt("port", 9002);
//        portEt.setText(String.valueOf(savedPort));
//
//        findViewById(R.id.id_start_stream).setOnClickListener(v -> {
//            try {
//                String ip = ipEt.getText().toString();
//                int port = Integer.parseInt(portEt.getText().toString());
//                Intent intent = new Intent(this, FrameRenderActivity.class);
//                intent.putExtra("ip", ip);
//                intent.putExtra("port", port);
//                startActivity(intent);
//                saveIpPort(ip, port);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Err: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void saveIpPort(String ip, int port) {
//        SpUtils.getInstance(this).put("ip", ip);
//        SpUtils.getInstance(this).put("port", port);
//    }
//
//}
