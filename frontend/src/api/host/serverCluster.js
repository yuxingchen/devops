import request from '@/utils/request';

/**
 * 创建服务器集群
 * @param {Object} data 集群信息
 */
export function createServerCluster(data) {
  return request({
    url: '/api/server-clusters',
    method: 'post',
    data
  });
}

/**
 * 更新服务器集群
 * @param {number} id 集群ID
 * @param {Object} data 集群信息
 */
export function updateServerCluster(id, data) {
  return request({
    url: `/api/server-clusters/${id}`,
    method: 'put',
    data
  });
}

/**
 * 删除服务器集群
 * @param {number} id 集群ID
 */
export function deleteServerCluster(id) {
  return request({
    url: `/api/server-clusters/${id}`,
    method: 'delete'
  });
}

/**
 * 获取服务器集群详情
 * @param {number} id 集群ID
 */
export function getServerClusterDetail(id) {
  return request({
    url: `/api/server-clusters/${id}`,
    method: 'get'
  });
}

/**
 * 分页查询服务器集群列表
 * @param {Object} params 查询参数
 */
export function pageServerClusters(params) {
  return request({
    url: '/api/server-clusters/page',
    method: 'get',
    params
  });
} 