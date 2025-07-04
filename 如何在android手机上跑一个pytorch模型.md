# 如何在android手机上跑一个pytorch模型

* 备注：本文档系统环境为ubuntu 24.04
* 如果你根据这个操作手册执行到某一步报错，不妨往下多看两步，也许有对应的解决方案

## Step1.android 开发环境构建

（如果熟悉android studio可以跳过本步骤）

推荐根据本文档使用vscode进行开发（因为copilot好用）

* 安装java套件：

  ```bash
  sudo apt update
  sudo apt install -y openjdk-21-jdk
  #验证
  java -version
  ```

* 安装Android Command-line tools

  ```bash
  mkdir -p ~/Android/Sdk/cmdline-tools
  cd ~/Android/Sdk/cmdline-tools
  wget https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip
  unzip commandlinetools-linux-*.zip -d ~/Android/Sdk/cmdline-tools/latest
  ```

* ```bash
  sdkmanager --sdk_root=$ANDROID_SDK_ROOT "platform-tools" \
                                          "platforms;android-30" \
                                          "build-tools;30.0.3"
  ```

* 打开`~/.bashrc`在文件末尾追加：

  ```bash
  export ANDROID_SDK_ROOT=$HOME/Android/Sdk
  export ANDROID_HOME=$ANDROID_SDK_ROOT
  export PATH=$ANDROID_SDK_ROOT/cmdline-tools/latest/cmdline-tools/bin:$ANDROID_SDK_ROOT/platform-tools:$PATH
  alias sdkmanager="$HOME/Android/Sdk/cmdline-tools/latest/cmdline-tools/bin/sdkmanager --sdk_root=\$HOME/Android/Sdk"
  
  ```

  然后重新加载`~/.bashrc`

  ```bash
  source ~/.bashrc
  ```

* 用sdkmanager安装平台工具，构建工具和所需平台（API 30为例）

  ```bash
  sdkmanager --sdk_root=$ANDROID_SDK_ROOT "platform-tools" \
                                          "platforms;android-30" \
                                          "build-tools;30.0.3"
  ```

  

* vscode远程连接服务器后安装插件：

  **Java Extension Pack**（`vscjava.vscode-java-pack`）：提供 Java 语法、调试、Maven / Gradle 支持

  **Kotlin**（`fwcd.kotlin`），如果写 Kotlin

  **Android iOS Emulator**

  **Gradle for Java**（`richardwillis.vscode-gradle`）

  **Debugger for Java**（`vscjava.vscode-java-debug`）

* 安装安卓模拟器

  ```bash
  sudo sdkmanager --install "emulator"
  sudo sdkmanager --install "system-images;android-30;google_apis;x86_64"
  ```
  
* 安装gradle 8.5 （对应java jdk 21）

  ```bash
  wget https://services.gradle.org/distributions/gradle-8.5-bin.zip
  unzip gradle-8.5-bin.zip
  sudo mv gradle-8.5-bin.zip /opt/gradle
  echo "export PATH=/opt/gradle/bin:$PATH" >> ~/.bashrc
  source ~/.bashrc
  ```

  

  

## Step2. 写一个基本的helloworld程序，保证你的环境配置正确

* 创建gradle基本组件

```bash
mkdir ~/Android_helloworld
cd ~/Android_helloworld
gradle init 
选择basic，选择groovy
```

* 创建文件目录结构：

  （请使用构建脚本）
  
  ```bash
  .
  ├── app
  │   ├── build.gradle
  │   └── src
  │       └── main
  │           ├── AndroidManifest.xml
  │           ├── java
  │           │   └── com
  │           │       └── example
  │           │           └── helloworld
  │           │               └── MainActivity.java
  │           └── res
  │               ├── layout
  │               │   └── activity_main.xml
  │               └── values
  │                   └── strings.xml
  ├── build.gradle
  ├── gradle
  │   ├── libs.versions.toml
  │   └── wrapper
  │       ├── gradle-wrapper.jar
  │       └── gradle-wrapper.properties
  ├── gradlew
  ├── gradlew.bat
  └── settings.gradle
  |__ gradle.properties
  ```
  
  各文件内容：
  
  MainActivity.java
  
  ```java
  package com.example.helloworld;
  
  import android.os.Bundle;
  import androidx.appcompat.app.AppCompatActivity;
  
  public class MainActivity extends AppCompatActivity {
      @Override
      protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);
      }
  }
  
  ```
  
  activity_main.xml
  
  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
      android:layout_width="match_parent"
      android:layout_height="match_parent">
  
      <TextView
          android:id="@+id/textView"
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:text="Hello, World!"
          android:textSize="24sp"
          android:layout_centerInParent="true"/>
  </RelativeLayout>
  ```
  
  strings.xml
  
  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <resources>
      <string name="app_name">HelloWorld</string>
  </resources>
  ```
  
  app/build.gradle

  ```java
  plugins {
      id 'com.android.application'
      // 如果你用 Kotlin，还可以加：id 'org.jetbrains.kotlin.android'
  }
  
  android {
      compileSdkVersion 30
      namespace "com.example.helloworld"
  
      defaultConfig {
          applicationId "com.example.helloworld"
          minSdkVersion 21
          targetSdkVersion 30
          versionCode 1
          versionName "1.0"
      }
      buildTypes {
          release {
              minifyEnabled false
          }
      }
  }
  
  dependencies {
      implementation 'androidx.appcompat:appcompat:1.3.1'
  }
  
  ```
  
  ./build.gradle
  
  ```java
  buildscript {
      repositories { google(); mavenCentral() }
      dependencies {
          classpath 'com.android.tools.build:gradle:7.4.2'
      }
  }
  allprojects {
      repositories { google(); mavenCentral() }
  }
  
  ```
  
  ./settings.gradle
  
  ```bash
  rootProject.name = 'android_torch'
  include ':app'
  
  ```
  
  ./gradle.properties
  
  ```java
  buildscript {
      repositories { google(); mavenCentral() }
      dependencies {
          classpath 'com.android.tools.build:gradle:7.4.2'
      }
  }
  allprojects {
      repositories { google(); mavenCentral() }
  }
  
  ```
  
  ./settings.gradle
  
  ```bash
  rootProject.name = 'android_torch'
  include ':app'
  
  ```
  
  
  
* 构建项目：

  vscode执行快捷键：ctrl+shift+B

  命令行执行 ./gradlew build（推荐用这个执行，vscode有可能因为proxy无法正常获取安装包）

* 检查可用的AVD：

  ```bash
  avdmanager list avd
  ```

* 如果没有，则创建：

  ```bash
  avdmanager create avd \
    -n my_avd \
    -k "system-images;android-30;google_apis;x86_64" \
    --device "pixel"
    
   sdkmanager "system-images;android-30;google_apis;x86_64"
  ```

  然后再次运行`avdmanager list avd` 检查

* 启动模拟器（无头模式启动） ：

  ```bash
  emulator -avd my_avd -no-window -gpu swiftshader_indirect -no-audio -no-boot-anim &
  ```
  
* 如果到这里提示需要用KVM则根据下面流程安装：

  ```bash
  sudo apt install qemu-kvm libvirt-clients libvirt-daemon-system
  sudo systemctl enable --now libvirtd
  sudo gpasswd -a $USER kvm
  #然后注销，重新ssh连接新的terminal
  ```

* 一切正常的话你应该可以通过这个命令看到启动的模拟器：

  ```bash
  adb devices
  ```

* 安装apk

  ```bash
  ./gradlew installDebug
  ```

* vscode 调试：

  先列出进程 ID：

  ```
  adb jdwp
  ```
  
  假设最后一个数字是 `12345`，再执行：
  
  ```
  adb forward tcp:8700 jdwp:12345
  ```

  **在 `.vscode/launch.json` 添加 Attach 配置**
  
  ```
  jsonc复制代码{
    "version": "0.2.0",
    "configurations": [
      {
        "name": "Attach to Android",
        "type": "java",
        "request": "attach",
        "hostName": "localhost",
        "port": 8700
      }
    ]
  }
  ```
  
  **打断点 & 启动调试**
  
  - 在 `MainActivity.java`（或别的地方）点左侧行号打断点。
  - 按 F5，选择 “Attach to Android” 即可在你的代码里单步、查看变量。
  
* 查看安卓的UI界面

  安装scrcpy.exe :下载预编译压缩包：https://github.com/Genymobile/scrcpy/releases

  * 把解压目录添加到环境变量PATH中

  在**本地机器（maybe windows）** 配置SSH端口转发

  ```bash
  ssh -L 5555:127.0.0.1:5555 user@your.server.com
  ```

  * 注意，输入密码后会表现为“登录到了远程服务器” 请不要退出登录。

  利用scripy连接

  ```bash
  scrcpy
  ```

  

​		

## Step 2. 构建一个最简单的安卓应用完成纯Linear层模型的推理

* 以Android 10 API 29为例

  ```bash
  sdkmanager --install "platforms;android-29" "build-tools;29.0.3"
  gradle init
  #依次选择：Application java groovy JUnit4  其余全部默认
  ```

* 根据Step1，将对应的文件进行修改

* 运行torch_modules文件夹下的main函数，得到models文件夹

* 将模型文件放入Android项目：

  在 Android Studio 的 `app/src/main/assets/` 目录下创建 `assets` 文件夹（如果尚未存在），并将 `model.pt` 拷贝进去。

  编译时，Gradle 会将其打包到 APK 的 assets 里

* **在 Gradle 中添加 PyTorch Mobile 依赖**
   在 `app/build.gradle` 的 `dependencies` 块中加入：

  ```groovy
  // 核心库
  implementation 'org.pytorch:pytorch_android:1.13.0' 
  // 如果模型用到了 torchvision 操作（如预处理、后处理），再加：
  implementation 'org.pytorch:pytorch_android_torchvision:1.13.0'
  ```

  版本号可根据你在 Python 端安装的 PyTorch 版本来对应替换

* 修改activity_main.xml:

  ```xml
  <?xml version="1.0" encoding="utf-8"?>
  <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
      android:layout_width="match_parent"
      android:layout_height="match_parent">
  
      <TextView
          android:id="@+id/textView"
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:text="Hello, World!"
          android:textSize="24sp"
          android:layout_centerInParent="true"/>
  
      <Button
          android:id="@+id/btnRunModel"
          android:layout_width="wrap_content"
          android:layout_height="wrap_content"
          android:text="Run Model"
          android:layout_below="@id/textView"
          android:layout_centerHorizontal="true"
          android:layout_marginTop="20dp"/>
  
  </RelativeLayout>
  ```

* 修改MainActivity.java:

  ```java
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
  
  
  ```

* 修改gradle.properties

  ```properties
  # Gradle daemon memory settings
  org.gradle.jvmargs=-Xmx4096m -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError
  
  # Enable parallel builds and configure workers
  org.gradle.parallel=true
  org.gradle.configureondemand=true
  org.gradle.workers.max=4
  
  # Android build optimizations
  android.enableJetifier=true
  android.useAndroidX=true
  android.enableR8.fullMode=true
  
  ```

* 修改AndroidManifest.xml

  ```xml
  <manifest xmlns:android="http://schemas.android.com/apk/res/android">
  
      <application
          android:allowBackup="true"
          android:icon="@android:drawable/ic_dialog_info"
          android:label="@string/app_name"
          android:theme="@style/Theme.AppCompat.Light.DarkActionBar">
          <activity
              android:name=".MainActivity"
              android:exported="true">
              <intent-filter>
                  <action android:name="android.intent.action.MAIN" />
                  <category android:name="android.intent.category.LAUNCHER" />
              </intent-filter>
          </activity>
      </application>
  </manifest>
  
  ```

* 修改build.gradle

  ```gradle
  plugins {
      id 'com.android.application'
      // 如果你用 Kotlin，还可以加：id 'org.jetbrains.kotlin.android'
  }
  
  android {
      compileSdkVersion 30
      namespace "com.example.helloworld"
  
      defaultConfig {
          applicationId "com.example.helloworld"
          minSdkVersion 21
          targetSdkVersion 30
          versionCode 1
          versionName "1.0"
      }
      buildTypes {
          release {
              minifyEnabled false
          }
      }
  
      dexOptions {
          javaMaxHeapSize "4g"
          preDexLibraries = false
      }
  
      packagingOptions {
          pickFirst '**/libc++_shared.so'
          pickFirst '**/libtorch.so'
          pickFirst '**/libpytorch_jni.so'
          pickFirst '**/libtorch_cpu.so'
      }
  }
  
  dependencies {
      implementation 'androidx.appcompat:appcompat:1.3.1'
      implementation 'org.pytorch:pytorch_android:1.13.0' 
      // 如果模型用到了 torchvision 操作（如预处理、后处理），再加：
      implementation 'org.pytorch:pytorch_android_torchvision:1.13.0'
  }
  
  ```

  
