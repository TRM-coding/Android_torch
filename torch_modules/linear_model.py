import torch
import torch.nn as nn

class SimpleLinearModel(nn.Module):
    def __init__(self, input_size=10, output_size=1):
        super(SimpleLinearModel, self).__init__()
        self.linear = nn.Linear(input_size, output_size)
    
    def forward(self, x):
        return self.linear(x)
