import request from '../utils/request'

export default {
    page(params) {
        return request({
            url: `/${entity?lower_case}/query/list/${r'${params.current}'}/${r'${params.size}'}`,
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
            url: `/${entity?lower_case}/add`,
            method: 'post',
            data
        })
    },
    delete(id) {
        return request({
            url: `/${entity?lower_case}/del/${r'${id}'}`,
            method: 'delete',
        })
    },
    mod(data) {
        return request({
            url: `/${entity?lower_case}/mod`,
            method: 'put',
            data
        })
    },
    getById(id) {
        return request({
            url: `/${entity?lower_case}/query/one/${r'${id}'}`,
            method: 'get',
        })
    }
}
