import axios from 'axios';

// axios-config.js
import axios from 'axios';

// 创建 axios 实例
const instance = axios.create({
    baseURL: 'http://localhost:10418',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
instance.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['token'] = token;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

instance.interceptors.response.use(response => {
    return response;
}, error => {
    if (error.response && error.response.status === 401) {
        // 处理令牌过期的情况，比如重定向到登录页面
        localStorage.removeItem('token');
        window.location.href = '/login/toLogin';
    }
    return Promise.reject(error);
});
export default instance;

// 在请求拦截器中添加令牌到请求头
// axios.interceptors.request.use(config => {
//     const token = localStorage.getItem('token');
//     if (token) {
//         // config.headers['Authorization'] = 'Bearer ' + token;
//         config.headers['token'] = token;
//     }
//     return config;
// }, error => {
//     return Promise.reject(error);
// });

// 在响应拦截器中处理令牌过期的情况
// axios.interceptors.response.use(response => {
//     return response;
// }, error => {
//     if (error.response && error.response.status === 401) {
//         // 处理令牌过期的情况，比如重定向到登录页面
//         localStorage.removeItem('token');
//         window.location.href = '/login/toLogin';
//     }
//     return Promise.reject(error);
// });
