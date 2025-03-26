package com.devops.util;

import com.devops.common.BusinessException;
import com.devops.entity.ServerInfo;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * SSH工具类
 *
 * @author yux
 */
@Slf4j
public class SshUtil {

    /**
     * 命令校验
     *
     * @param command 命令
     * @return 是否合法
     */
    public static boolean isValidCommand(String command) {
        // 针对部署命令进行安全校验，禁止非法命令
        // 禁止使用危险命令
        if (command.contains("rm") ||
                command.contains("mkfs") ||
                command.contains(">") ||
                command.contains("mv") ||
                command.contains("|") ||
                command.contains(";") ||
                command.contains("&") ||
                command.contains("$(") ||
                command.contains("`")) {
            throw new BusinessException("命令包含危险字符,请检查命令");
        }
        // 只允许使用sh、bash等基本命令
        String[] allowedCommands = { "sh", "bash", "java", "nohup", "tar", "unzip", "zip" };
        boolean isValidCommand = false;
        for (String cmd : allowedCommands) {
            if (command.startsWith(cmd)) {
                isValidCommand = true;
                break;
            }
        }
        if (!isValidCommand) {
            throw new BusinessException("仅支持sh、bash、java、tar、unzip、zip等基本命令");
        }
        return true;
    }

    /**
     * 连接到服务器
     *
     * @param serverInfo 服务器信息
     * @return SSH会话
     */
    public static Session connectToServer(ServerInfo serverInfo) throws Exception {
        JSch jsch = new JSch();

        if (serverInfo.getPrivateKey() != null) {
            jsch.addIdentity("server_key", serverInfo.getPrivateKey().getBytes(), null, null);
        }

        Session session = jsch.getSession(
                serverInfo.getUsername(),
                serverInfo.getServerIp(),
                serverInfo.getServerPort()
        );

        if (serverInfo.getPassword() != null) {
            session.setPassword(AESUtil.decrypt(serverInfo.getPassword(), AESUtil.SERVER_AES_KEY));
        }

        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        session.connect(30000);
        return session;
    }

    /**
     * 执行远程命令
     *
     * @param session SSH会话
     * @param command 要执行的命令
     * @return 命令输出
     */
    public static String executeCommand(Session session, String command) throws JSchException, IOException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);

        StringBuilder output = new StringBuilder();
        channel.setOutputStream(null);

        // 获取错误输出流
        InputStream errStream = channel.getErrStream();

        try (InputStream in = channel.getInputStream()) {
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                // 读取标准输出
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    output.append(new String(tmp, 0, i));
                }

                // 读取错误输出
                while (errStream.available() > 0) {
                    int i = errStream.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    output.append(new String(tmp, 0, i));
                }

                if (channel.isClosed()) {
                    // 确保所有数据都被读取
                    if (in.available() > 0 || errStream.available() > 0) {
                        continue;
                    }
                    // 获取退出状态
                    int exitStatus = channel.getExitStatus();
                    if (exitStatus != 0) {
                        output.append("\nExit status: ").append(exitStatus);
                    }
                    break;
                }
                try {
                    Thread.sleep(100);
                } catch (Exception ee) {
                    // ignore
                }
            }
        } finally {
            channel.disconnect();
        }

        return output.toString();
    }

    /**
     * 上传文件到远程服务器
     *
     * @param session      SSH会话
     * @param sourceStream 源文件流
     * @param destPath     目标路径
     * @param fileName     文件名
     */
    public static void uploadFile(Session session, InputStream sourceStream, String destPath, String fileName)
            throws JSchException {
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();

        try {
            // 确保目标目录存在
            try {
                channel.mkdir(destPath);
            } catch (SftpException e) {
                // 目录可能已存在，忽略异常
            }
            channel.cd(destPath);
            channel.put(sourceStream, fileName);
        } catch (Exception e) {
            log.error("上传文件失败", e);
        } finally {
            channel.disconnect();
        }
    }

    /**
     * 关闭SSH会话
     *
     * @param session SSH会话
     */
    public static void closeSession(Session session) {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
} 