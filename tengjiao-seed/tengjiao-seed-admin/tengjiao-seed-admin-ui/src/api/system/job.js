import request from '@/utils/request'

export default {
    page(params) {
        return request({
            url: `system/job/query/list/${params.current}/${params.size}`,
            method: 'post',
            data: {
                asc: params.asc,
                text: params.text,
                sort: params.sort
            }
        })
    },
    add(data) {
        return request({
            url: `system/job/add`,
            method: 'post',
            data
        })
    },
    delete(id) {
        return request({
            url: `system/job/del/${id}`,
            method: 'delete',
        })
    },
    mod(data) {
        return request({
            url: `system/job/mod`,
            method: 'put',
            data
        })
    },
    getById(id) {
        return request({
            url: `system/job/query/one/${id}`,
            method: 'get',
        })
    }
}
