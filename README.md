# grid-trade
网格交易  

基于MEXC web接口的网格交易

启动配置文件, 需放到jar同目录下, 可自行配置
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
  
后续重启会根据grid-trade-cache.json文件自动恢复数据  

不要强行中断程序，需使用stop.sh脚本停止程序  


