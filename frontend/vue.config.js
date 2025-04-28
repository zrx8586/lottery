module.exports = {
    outputDir: 'dist', // 指定构建输出目录
    devServer: {
        port: 8081, // 指定开发服务器的端口号
        proxy: { // 配置代理
            "/api": {
                target: "http://localhost:8080", // 目标服务器地址
                changeOrigin: true, // 解决跨域问题
            },
        },
    },
};