import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/components/layout/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/DashboardView.vue'),
        meta: { title: '数据大屏', icon: 'DataAnalysis' },
      },
      {
        path: 'videos',
        name: 'VideoList',
        component: () => import('@/views/VideoListView.vue'),
        meta: { title: '视频管理', icon: 'VideoCamera' },
      },
      {
        path: 'videos/:id',
        name: 'VideoDetail',
        component: () => import('@/views/VideoDetailView.vue'),
        meta: { title: '实时分析', hidden: true },
      },
      {
        path: 'alarms',
        name: 'AlarmList',
        component: () => import('@/views/AlarmListView.vue'),
        meta: { title: '告警记录', icon: 'Bell' },
      },
      {
        path: 'alarms/config',
        name: 'AlarmConfig',
        component: () => import('@/views/AlarmConfigView.vue'),
        meta: { title: '告警规则', icon: 'Setting' },
      },
      {
        path: 'devices',
        name: 'DeviceList',
        component: () => import('@/views/DeviceListView.vue'),
        meta: { title: '设备管理', icon: 'Monitor' },
      },
      {
        path: 'heatmap',
        name: 'Heatmap',
        component: () => import('@/views/HeatmapView.vue'),
        meta: { title: '热力图分析', icon: 'Grid' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

export default router
