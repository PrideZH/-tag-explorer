import axios from 'axios';
import { BaseResp, Page } from '../types';

export interface ResourceItem extends BaseResp {
    name: string;
    type: string;
    coverCount: number;
}

export interface Resource extends BaseResp {
    name: string;
    type: string;
    tags: {
        id: string;
        name: string;
    }[];
}

const resourceApi = {

    list (tags?: string[], page?: number) {
        return axios.get<Page<ResourceItem>>('/resource', { params: { tags, page, size: 15 }} );
    },

    get (id: string) {
        return axios.get<Resource>(`/resource/${id}`);
    },

    put (id: string, data: {
        name?: string;
        tags?: string[];
    }) {
        return axios.put(`/resource/${id}`, data)
    },

    delete (id: string) {
        return axios.delete(`/resource/${id}`)
    }

}

export default resourceApi;
