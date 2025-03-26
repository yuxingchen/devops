const fs = require('fs');
const path = require('path');
const archiver = require('archiver');
const dayjs = require('dayjs');

// 创建输出目录
const outputDir = path.join(__dirname, '../dist-zip');
if (!fs.existsSync(outputDir)) {
    fs.mkdirSync(outputDir);
}

// 生成zip文件名，格式：frontend_YYYYMMDD_HHmmss.zip
const fileName = `frontend_${dayjs().format('YYYYMMDD_HHmmss')}.zip`;
const output = fs.createWriteStream(path.join(outputDir, fileName));
const archive = archiver('zip', {
    zlib: { level: 9 } // 设置压缩级别
});

// 监听打包完成事件
output.on('close', () => {
    const size = (archive.pointer() / 1024 / 1024).toFixed(2);
    console.log(`\n✨ 打包完成！`);
    console.log(`📦 文件: ${fileName}`);
    console.log(`📊 大小: ${size} MB\n`);
});

// 监听错误事件
archive.on('error', (err) => {
    throw err;
});

// 将输出流管道连接到文件
archive.pipe(output);

// 将dist目录添加到zip，保留目录结构
archive.directory(path.join(__dirname, '../dist/'), 'dist');

// 完成打包
archive.finalize(); 