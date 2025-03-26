import request from '@/utils/request'

/**
 * 获取应用列表
 */
export function listApps(params) {
    return request({
        url: '/api/apps',
        method: 'get',
        params
    })
}

/**
 * 获取应用详情
 */
export function getAppDetail(id) {
    return request({
        url: `/api/apps/${id}`,
        method: 'get'
    })
}

/**
 * 创建应用
 */
export function createApp(data) {
    return request({
        url: '/api/apps',
        method: 'post',
        data
    })
}

/**
 * 更新应用
 */
export function updateApp(id, data) {
    return request({
        url: `/api/apps/${id}`,
        method: 'put',
        data
    })
}

/**
 * 删除应用
 */
export function deleteApp(id) {
    return request({
        url: `/api/apps/${id}`,
        method: 'delete'
    })
}
