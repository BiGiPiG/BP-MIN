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

router.beforeEach(async (to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta?.requiresAuth === true);
  const accessToken = localStorage.getItem('accessToken');
  const refreshToken = localStorage.getItem('refreshToken');

  if (!requiresAuth) {
    return next();
  }

  if (!accessToken || !refreshToken) {
    return next({ name: 'Login' });
  }

  if (!isTokenExpired(accessToken)) {
    return next();
  }

  console.log("refreshing")
  const response = await fetch('/api/auth/refresh_token', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${refreshToken}`
    },
  });

  if (!response.ok) {
    console.error("Refresh failed");
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    next({ name: 'Login' });
    return;
  }

  try {
    const data = await response.json();
    localStorage.setItem('accessToken', data.accessToken);
    localStorage.setItem('refreshToken', data.refreshToken);
  } catch(error) {
    console.error('Missing tokens in response:', error);
    next({ name: 'Login' });
  }

  next();
});

export default router
