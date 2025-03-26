import request from '@/utils/request'

/**
 * 获取服务器列表
 * @param params
 * @return {*}
 */
export function listServersWithParams(params) {
    return request({
        url: '/api/servers',
        method: 'get',
        params
    })
}

/**
 * 获取服务器列表
 */
export function listServers() {
    return request({
        url: '/api/servers',
        method: 'get'
    })
}

/**
 * 创建服务器
 */
export function createServer(data) {
    return request({
        url: '/api/servers',
        method: 'post',
        data
    })
}

/**
 * 更新服务器
 */
export function updateServer(id, data) {
    return request({
        url: `/api/servers/${id}`,
        method: 'put',
        data
    })
}

/**
 * 删除服务器
 */
export function deleteServer(id) {
    return request({
        url: `/api/servers/${id}`,
        method: 'delete'
    })
}

/**
 * 测试服务器连接
 */
export function testConnection(id) {
    return request({
        url: `/api/servers/${id}/test`,
        method: 'post'
    })
}
