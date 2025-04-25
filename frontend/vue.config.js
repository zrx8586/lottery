module.exports = {
    outputDir: 'dist', // 确保这里是 dist
    devServer: {
        proxy: {
            "/api": {
                target: "http://localhost:8080",
                changeOrigin: true,
            },
        },
    },
};