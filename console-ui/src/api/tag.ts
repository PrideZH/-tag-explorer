import axios from 'axios';
import { BaseResp, Page } from '../types';

export interface Tag extends BaseResp {
    name: string;
    count: number;
}

const tagApi = {

    list () {
        return axios.get<Tag[]>('/tags');
    },

    add (data: {
        resourceId: string;
        name: string;
    }) {
        return axios.post('/tags', data);
    },

    delete (resourceId: string, tagId: string) {
        return axios.delete(`/tags/${resourceId}/${tagId}`)
    }

}

export default tagApi;
