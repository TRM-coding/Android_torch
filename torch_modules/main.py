import torch
from linear_model import SimpleLinearModel
import os

def main():
    # 创建模型实例
    model = SimpleLinearModel(input_size=10, output_size=1)
    
    # 设置为评估模式
    model.eval()
    
    # 创建示例输入用于追踪
    example_input = torch.randn(1, 10)
    
    # 转换为 TorchScript
    traced_model = torch.jit.trace(model, example_input)
    
    # 创建输出目录
    output_dir = "models"
    os.makedirs(output_dir, exist_ok=True)
    
    # 保存模型
    output_path = os.path.join(output_dir, "linear_model.pt")
    traced_model.save(output_path)
    
    print(f"模型已保存到: {output_path}")
    print(f"模型输入形状: {example_input.shape}")
    print(f"模型输出形状: {traced_model(example_input).shape}")

if __name__ == "__main__":
    main()
