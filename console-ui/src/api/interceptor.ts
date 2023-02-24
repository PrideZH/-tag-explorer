import axios, { InternalAxiosRequestConfig, ParamsSerializerOptions } from 'axios';
import qs from 'qs';

export const TIMEOUT: number = 30 * 1000;

export interface HttpResponse<T = unknown> {
  code: number;
  data: T;
  message: string;
}

axios.defaults.baseURL = '/';
axios.defaults.timeout = TIMEOUT;

axios.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    if (config.method === 'get') {
      // If method is get，and params is Array(arr[1,2])， transform arr=1&arr=2
      config.paramsSerializer = {
        serialize(params: Record<string, unknown>): string {
          return qs.stringify(params, { arrayFormat: 'repeat' })
        }
      }
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axios.interceptors.response.use(
  (response: any) => {
    const res: any = response.data;
    if (res.code !== 200) {
      console.error(`[${res.code}] ${res.message}`);
      // 显示消息错误信息
      if (res.data) {
        (<string[]>res.data).forEach(item => console.error(item));
      }
      return Promise.reject(res);
    }
    return res;
  },
  (error: any) => {
    const { response, message } = error;
    if (response && response.data) {
      const { status, data } = response
      console.error(`[${status}] ${data.msg || message}`);
    } else {
      let tips = null;
      if (message === 'Network Error') {
        tips = '后端接口连接异常';
      }
      console.error(tips || `后端接口未知异常`);
    }
    return Promise.reject(error)
  }
);
