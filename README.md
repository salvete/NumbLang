## 运行代码说明

1. 安装JDK
2. 打开终端命令
3. 运行代码 `java REPL/REPL` 可以实现交互式的代码运行，并直接给出运算结果。
4. 运行代码 `java REPL/REPL -f ./input/test.NumbLang` 可以测试如下功能：
    * let 使用示例
    * list 和 dict 使用示例
    * list 和 dict 值的获取
    * 函数的定义与使用
    * 高阶函数使用示例
    * 闭包示例
    * 高阶函数柯里化示例
    * 内置函数使用示例
5. 运行代码 `java REPL/REPL -f ./input/evalTest.NumbLang` 可以完整系统测试
6. 运行代码 `java REPL/REPL -f ./input/lexerTest.NumbLang` 可以测试词法输入
7. 运行代码 `java REPL/REPL -f ./input/parserTest.NumbLang` 可以实现语法分析器测试
8. 也可以通过切换到根目录，运行 `sh run.sh` 运行全部代码

## NumbLang 的介绍

有关NumbLang的详细介绍参见：[NumbLang的介绍](./NumbLang介绍.pdf)

## 用法示例
词法分析、语法分析、语义分析和高阶用法见下：

[词法分析](./input/lexerTest.NumbLang)
[语法分析](./input/parserTest.NumbLang)
[语义分析](./input/evalTest.NumbLang)
[高阶用法](./input/test.NumbLang)
