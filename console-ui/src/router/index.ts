import { createRouter, createWebHashHistory, Router, RouteRecordRaw } from 'vue-router';


const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    component: () => import('../views/index/index.vue')
  },
  {
    path: '/video',
    component: () => import('../views/view.vue')
  },
  
];

const router: Router = createRouter({
  history: createWebHashHistory(),
  routes
});

export default router;
