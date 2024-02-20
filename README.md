# grid-trade
网格交易  

基于MEXC web接口的网格交易  
MEXC主站地址: https://www.mexc.com  
注册加入: https://www.mexc.com/register?inviteCode=1Nvr6  

q: 为什么选择MEXC做网格交易?   
a: 免现货交易手续费  

使用
1.推荐Linux机器部署;  
2.Linux服务器需要配置好jdk1.8+maven+git;  
3.配置环境变量到Liunx, MEXC_TOKEN=token, token获取web登录mexc控制台查看cookie的u_id字段获取;  
4.git拉取到Liunx服务器;  
5.在/app/grid-trade目录下创建grid-trade-start.json文件, 配置初始化数据;  
grid-trade-start.json  
{  
  "currency": "MX", // 币种  
  "market": "USDT",  // 交易  
  "amount": 5000.0,  // 总额  
  "lowPrice": 2.7102,  // 最低价  
  "highPrice": 3.0813,  // 最高价  
  "gridNum": 200,  // 网格数量  
  "buyStart": true  // 首次启动是否需要购入资产(可以从已有资产卖出开始)  
}  
5.执行sh deploy.sh, 服务会部署到/app/grid-trade/下并启动执行;  
6.运行会生成grid-trade-cache.json文件, 用于后续重新启动重启恢复数据;  
7.不要强行中断程序，需使用stop.sh脚本停止程序;  

