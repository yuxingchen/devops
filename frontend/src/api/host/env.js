import request from '@/utils/request'

/**
 * 获取环境列表
 */
export function listEnvs() {
    return request({
        url: '/api/envs',
        method: 'get'
    })
}

/**
 * 获取环境列表
 * @param params
 * @return {*}
 */
export function listEnvsWithParams(params) {
    return request({
        url: '/api/envs',
        method: 'get',
        params
    })
}


/**
 * 创建环境
 */
export function createEnv(data) {
    return request({
        url: '/api/envs',
        method: 'post',
        data
    })
}

/**
 * 更新环境
 */
export function updateEnv(id, data) {
    return request({
        url: `/api/envs/${id}`,
        method: 'put',
        data
    })
}

/**
 * 删除环境
 */
export function deleteEnv(id) {
    return request({
        url: `/api/envs/${id}`,
        method: 'delete'
    })
}