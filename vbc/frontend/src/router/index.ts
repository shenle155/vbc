import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      component: () => import('@/components/layout/MainLayout.vue'),
      redirect: '/dashboard',
      children: [
        { path: 'dashboard', component: () => import('@/views/DashboardView.vue') },
        { path: 'videos', component: () => import('@/views/VideoListView.vue') },
        { path: 'videos/:id', component: () => import('@/views/VideoDetailView.vue') },
        { path: 'alarms', component: () => import('@/views/AlarmListView.vue') },
        { path: 'alarms/config', component: () => import('@/views/AlarmConfigView.vue') },
        { path: 'devices', component: () => import('@/views/DeviceListView.vue') },
        { path: 'heatmap', component: () => import('@/views/HeatmapView.vue') },
      ],
    },
  ],
})

export default router
