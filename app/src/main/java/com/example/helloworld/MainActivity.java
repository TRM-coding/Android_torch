package com.example.helloworld;

// 导入Android相关的类
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

// 导入PyTorch Mobile相关的类
import org.pytorch.Module;           // 表示一个PyTorch模型
import org.pytorch.Tensor;           // 表示张量（多维数组）
import org.pytorch.IValue;           // 用于包装输入输出数据

// 导入文件操作相关的类
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * MainActivity - 应用程序的主界面
 * 这是用户打开应用后看到的第一个界面
 * 主要功能：加载PyTorch模型，提供按钮触发推理，显示结果
 */
public class MainActivity extends AppCompatActivity {
    // 声明类的成员变量
    private Module model;          // 存储加载的PyTorch模型
    private TextView textView;     // 用于显示文本信息的控件
    private Button btnRunModel;    // 用于触发模型推理的按钮

    /**
     * onCreate方法 - Activity生命周期方法
     * 当Activity被创建时调用，用于初始化界面和数据
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置界面布局文件
        setContentView(R.layout.activity_main);
        
        // 通过ID找到界面上的控件并关联到变量
        textView = findViewById(R.id.textView);      // 找到文本显示控件
        btnRunModel = findViewById(R.id.btnRunModel); // 找到按钮控件
        
        // 尝试加载PyTorch模型
        try {
            // 第1步：从assets目录复制模型文件到应用内部存储
            String modelPath = assetFilePath(this, "linear_model.pt");
            
            // 第2步：使用PyTorch Mobile加载模型
            model = Module.load(modelPath);
            
            // 第3步：显示加载成功信息
            textView.setText("Model loaded successfully!");
            
        } catch (IOException e) {
            // 如果加载失败，显示错误信息
            textView.setText("Error loading model: " + e.getMessage());
        }
        
        // 为按钮设置点击监听器
        // 当用户点击按钮时，会执行runModelInference()方法
        btnRunModel.setOnClickListener(v -> runModelInference());
    }
    
    /**
     * runModelInference方法 - 执行模型推理
     * 当用户点击按钮时调用此方法
     * 完成：准备输入数据 -> 模型推理 -> 处理输出 -> 显示结果
     */
    private void runModelInference() {
        try {
            // === 第1步：准备输入数据 ===
            // 创建一个长度为10的浮点数组作为模型输入
            // 这里的10对应我们线性模型的输入维度
            float[] inputData = new float[1 * 10];
            
            // 用随机数填充输入数据（实际应用中这里应该是真实数据）
            for (int i = 0; i < inputData.length; i++) {
                inputData[i] = (float) (Math.random() * 2 - 1); // 生成-1到1之间的随机数
            }
            
            // 将浮点数组转换为PyTorch张量
            // 张量形状为[1, 10]，表示批次大小为1，特征维度为10
            Tensor inputTensor = Tensor.fromBlob(
                inputData, new long[]{1, 10}
            );
            
            // === 第2步：执行模型推理 ===
            // 将输入张量传递给模型，获得输出张量
            Tensor outputTensor = model.forward(IValue.from(inputTensor)).toTensor();
            
            // === 第3步：处理输出数据 ===
            // 将输出张量转换为浮点数组，方便处理
            float[] scores = outputTensor.getDataAsFloatArray();
            
            // === 第4步：格式化并显示结果 ===
            // 构建结果字符串
            StringBuilder result = new StringBuilder("Model Output:\n");
            for (int i = 0; i < scores.length; i++) {
                result.append("Output[").append(i).append("]: ").append(scores[i]).append("\n");
            }
            
            // 在界面上显示结果
            textView.setText(result.toString());
            
        } catch (Exception e) {
            // 如果推理过程出错，显示错误信息
            textView.setText("Error during inference: " + e.getMessage());
        }
    }
    
    /**
     * assetFilePath方法 - 工具方法：从assets中拷贝文件到本地
     * 
     * 为什么需要这个方法？
     * - PyTorch模型文件通常放在assets目录中
     * - 但PyTorch Mobile需要从文件系统路径加载模型
     * - 所以需要先把模型文件从assets复制到应用的内部存储
     * 
     * @param context 应用上下文，用于访问assets和文件系统
     * @param assetName assets目录中的文件名
     * @return 复制后的文件在内部存储中的绝对路径
     * @throws IOException 如果文件操作失败
     */
    public static String assetFilePath(Context context, String assetName) throws IOException {
        // 在应用内部存储目录中创建文件对象
        File file = new File(context.getFilesDir(), assetName);
        
        // 使用try-with-resources语句自动管理资源
        try (InputStream is = context.getAssets().open(assetName);     // 打开assets中的文件
             FileOutputStream os = new FileOutputStream(file)) {        // 创建输出流
            
            // 创建缓冲区用于数据传输
            byte[] buffer = new byte[4 * 1024]; // 4KB缓冲区
            int read;
            
            // 循环读取并写入数据
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            
            // 确保数据写入磁盘
            os.flush();
        }
        
        // 返回复制后文件的完整路径
        return file.getAbsolutePath();
    }
}

