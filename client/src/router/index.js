import { createRouter, createWebHistory } from 'vue-router'
import LoginPage from "@/components/LoginPage.vue";
import MainPage from "@/components/MainPage.vue";
import { isTokenExpired } from "@/utils/jwtUtils.js";

const router = createRouter({
  routes: [{
    path: '/login',
    name: 'Login',
    component: LoginPage,
    meta: { requiresAuth: false }
  },
    {
      path: '/bp-min/:username',
      name: 'BpMin', component: MainPage,
      meta: { requiresAuth: true }
    },
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/login' }],
  history: createWebHistory()
})

router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record =>
      record.meta && record.meta.requiresAuth === true
  );
  const token = localStorage.getItem('accessToken');

  if (requiresAuth && (!token || isTokenExpired(token))) {
    next({ name: 'Login' });
  } else if (to.name === 'Login' && token && !isTokenExpired(token)) {
    const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')));
    const username = payload.sub;
    next({ name: 'BpMin', params: { username } });
  } else {
    next();
  }
});

export default router
